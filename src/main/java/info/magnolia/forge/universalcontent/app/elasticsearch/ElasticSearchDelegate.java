/*
 *
 */
package info.magnolia.forge.universalcontent.app.elasticsearch;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.Query.Builder;
import co.elastic.clients.elasticsearch.cluster.HealthResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.util.ObjectBuilder;
import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomConnection;
import info.magnolia.forge.universalcontent.app.generic.annotation.DelegateImplementation;
import info.magnolia.forge.universalcontent.app.generic.connection.ElasticSearchParameterConnection;
import info.magnolia.forge.universalcontent.app.generic.connector.GenericDelegate;
import info.magnolia.forge.universalcontent.app.generic.entity.Field;
import info.magnolia.forge.universalcontent.app.generic.entity.GenericItem;
import info.magnolia.forge.universalcontent.app.generic.entity.ItemDelegate;
import info.magnolia.forge.universalcontent.app.generic.others.GenericException;
import info.magnolia.forge.universalcontent.app.generic.search.BoostField;
import info.magnolia.forge.universalcontent.app.generic.search.Params;
import info.magnolia.forge.universalcontent.app.generic.service.RepositoryService;
import info.magnolia.forge.universalcontent.app.generic.ui.table.GenericResults;
import lombok.Data;

/**
 * Delegate class implements using Elastic Search library.
 *
 * @param <T> the generic type
 */
/**
 * Instantiates a new query elastic search delegate.
 *
 */
@Data
@DelegateImplementation(parameterClass = ElasticSearchParameterConnection.class)
public class ElasticSearchDelegate<T extends GenericItem> extends GenericDelegate<T> {
	/** The query builder. */
	Function<Builder, ObjectBuilder<Query>> queryBuilder = null;

	@Inject
	ElasticSearchConnection connection;

	public ElasticSearchDelegate() {
		super();
	}

	/**
	 * Instantiates a new query elastic search delegate.
	 *
	 * @param pool               the client search
	 * @param typeParameterClass the type parameter class
	 * @param factoryConverter   the factory converter
	 * @throws GenericException
	 */
	@Inject
	public ElasticSearchDelegate(RepositoryService serviceContainer) throws GenericException {
		super(serviceContainer);
	}

	/**
	 * Search.
	 *
	 * @param indexName    the index name
	 * @param searchParams the search params
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	public GenericResults search(String indexName, Params searchParams) throws GenericException {
		Map<String, Object> filterByField = searchParams.getFields().entrySet().stream().filter(key -> {
			try {
				Boolean isNotEmpty;
				try {
					isNotEmpty = StringUtils.isNotEmpty((String) key.getValue());
				} catch (Exception e) {
					isNotEmpty = key.getValue() != null;
				}
				return getPropertyColumns().contains(key.getKey()) && isNotEmpty;
			} catch (GenericException e) {
				log.error("QueryElasticSearchDelegate", e);
			}
			return false;
		}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		queryBuilder = q -> {
//	    Simple query all field search
			if (searchParams.isSimpleQueryString()) {
				q.simpleQueryString(simple -> {
					simple.query(searchParams.getRelevanceSearch().getFullTextSearch());
					List<String> fields = searchParams.getRelevanceSearch().getBoostFields().stream().map(f -> {
						return f.getFieldName();
					}).collect(Collectors.toList());
					simple.fields(fields);
					return simple;
				});
			}
//	    Advanced query
			if (searchParams.isFieldFiltered()) {
				q.bool(bool -> {
					bool.must(mustQ -> {
						for (String field : filterByField.keySet()) {
							mustQ.match(match -> {
								if (field != null && filterByField.get(field) != null) {
									match.field(field);
									Optional<BoostField> boostField = searchParams.getRelevanceSearch().getBoostFields()
											.stream().filter(f -> {
												return field.equals(f.getFieldName());
											}).findFirst();
									if (boostField.isPresent()) {
										try {
											match.boost(boostField.get().getBoost().floatValue());
										} catch (Exception e) {
											match.boost(1F);
										}
									}
									match.query(v -> {
										Object object = filterByField.get(field);
										if (object instanceof String) {
											return v.stringValue((String) object);
										}
										return v.stringValue("");

									});
								}
								return match;
							});
						}
						return mustQ;
					});
					return bool;
				});
			}
			return q;
		};

		Function<co.elastic.clients.elasticsearch.core.SearchRequest.Builder, ObjectBuilder<SearchRequest>> fn2 = s -> {
			s.index(indexName);
			if (searchParams.getOrders().size() > 0) {
				s.sort(SortOptions.of(fieldSortable -> {
					return fieldSortable.field(FieldSort.of(a -> {
						for (String order : searchParams.getOrders().keySet()) {
							a.field(order).order(SortOrder.valueOf(searchParams.getOrders().get(order)));
						}
						return a;
					}));
				}));
			}
			if (searchParams.isSimpleQueryString() || searchParams.isFieldFiltered()) {
				s.query(queryBuilder);
			}
			s.from(searchParams.getOffset());
			s.size(searchParams.getSize());
			return s;
		};

		SearchResponse<? extends GenericItem> search;
		List<? extends GenericItem> results = null;
		try {
			search = connection.getClient().search(fn2, typeParameterClass);
			results = search.hits().hits().stream().map(hit -> {
				return hit.source();
			}).collect(Collectors.toList());
		} catch (ElasticsearchException e) {
			throw new GenericException("Error search ");
		} catch (IOException e) {
			throw new GenericException("Error search ");
		}
		GenericResults genericResults = new GenericResults(results, search.hits().total().value());
		return genericResults;
	}

	/**
	 * Checks for index.
	 *
	 * @param key the field name
	 * @return the boolean
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	public Boolean hasExecuteSetup(String key) throws GenericException {
		if (connection != null && connection.getClient() != null) {
			try {
				Boolean result = connection.getClient().cat().indices().valueBody().stream().filter(f -> {
					return key.equals(f.index());
				}).count() > 0;
			} catch (ElasticsearchException e) {
				throw new GenericException("Error index " + key);
			} catch (IOException e) {
				throw new GenericException("Error index " + key);
			}
		}
		return Boolean.FALSE;
	}

	/**
	 * Adds the index.
	 *
	 * @param fieldName the field name
	 * @param typeClass the type class
	 * @throws ElasticsearchException the elasticsearch exception
	 * @throws IOException            Signals that an I/O exception has occurred.
	 */
	@Override
	public void setup(String fieldName, Class typeClass) throws GenericException {
		// Create index with all sortable fields
		List<java.lang.reflect.Field> classFields = serviceContainer.getConverterClass().getAllFields(typeClass);
		List<Field> fields = classFields.stream().map(f2 -> {
			return Field.instanceFrom(f2, fieldName);
		}).collect(Collectors.toList());
		CreateIndexRequest mappingFieldsSortable = new CreateIndexRequest.Builder().index(fieldName)
				.mappings(mappings -> {
					fields.stream().forEach(mapping -> {
						mappings.properties(mapping.getName(), field -> {
							field.text(propertyField -> {
								propertyField.fielddata(true);
								return propertyField;
							});
							return field;
						});
					});
					return mappings;
				}).build();

		try {
			connection.getClient().indices().create(mappingFieldsSortable);
			HealthResponse response = connection.getClient().cluster().health();
			response.indices().keySet();
		} catch (ElasticsearchException e) {
		} catch (IOException e) {
			throw new GenericException("Error addIndex " + fieldName);
		}
	}

	/**
	 * Adds the item.
	 *
	 * @param key the field name
	 * @param obj the obj
	 * @param id  the id
	 * @return
	 * @throws ElasticsearchException the elasticsearch exception
	 * @throws IOException            Signals that an I/O exception has occurred.
	 * @throws GenericException
	 */
	@Override
	public ItemDelegate<? extends GenericItem> addItem(ItemDelegate<? extends GenericItem> item)
			throws GenericException {
		if (!hasExecuteSetup(item.getKey())) {
			setup(item.getKey(), item.getObj().getClass());
		}
		IndexRequest<? extends GenericItem> request = IndexRequest
				.of(i -> i.index(item.getKey()).id(item.getId()).document(item.getObj()));
		try {
			IndexResponse response = connection.getClient().index(request);
		} catch (ElasticsearchException | IOException e) {
			throw new GenericException("Error AddItem:");
		}
		return item;
	}

	@Override
	public void setConnection(CustomConnection customConnection) {
		connection = (ElasticSearchConnection) customConnection;

	}

}
