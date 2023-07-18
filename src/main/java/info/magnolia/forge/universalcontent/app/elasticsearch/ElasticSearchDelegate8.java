/*
 *
 */
package info.magnolia.forge.universalcontent.app.elasticsearch;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.HttpMethod;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.Query.Builder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.util.ObjectBuilder;
import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomConnection;
import info.magnolia.forge.universalcontent.app.elasticsearch.connector.ElasticSearchConnection;
import info.magnolia.forge.universalcontent.app.elasticsearch.connector.ElasticSearchParameter;
import info.magnolia.forge.universalcontent.app.elasticsearch.connector.ElasticSearchParameterFactory;
import info.magnolia.forge.universalcontent.app.generic.annotation.DelegateImplementation;
import info.magnolia.forge.universalcontent.app.generic.connection.ElasticSearchParameterConnection;
import info.magnolia.forge.universalcontent.app.generic.connector.GenericDelegate;
import info.magnolia.forge.universalcontent.app.generic.entity.Field;
import info.magnolia.forge.universalcontent.app.generic.entity.GenericItem;
import info.magnolia.forge.universalcontent.app.generic.entity.ItemDelegate;
import info.magnolia.forge.universalcontent.app.generic.others.ElasticSearchException;
import info.magnolia.forge.universalcontent.app.generic.others.GenericException;
import info.magnolia.forge.universalcontent.app.generic.search.Params;
import info.magnolia.forge.universalcontent.app.generic.service.RepositoryService;
import info.magnolia.forge.universalcontent.app.generic.ui.table.GenericResults;
import info.magnolia.forge.universalcontent.app.generic.utils.GenericClassConverter;
import info.magnolia.forge.universalcontent.elasticsearch.beans.IndexOperationResult;
import info.magnolia.forge.universalcontent.elasticsearch.beans.IndexSearchResult;
import info.magnolia.forge.universalcontent.elasticsearch.beans.SearchRequest;
import info.magnolia.forge.universalcontent.elasticsearch.utils.ElasticSearchConfig;
import info.magnolia.forge.universalcontent.elasticsearch.utils.ElasticsearchUtils;
import lombok.Data;

/**
 * Delegate class implements using Elastic Search library v8.
 *
 * @param <T> the generic type
 */
/**
 * Instantiates a new query elastic search delegate.
 *
 */
@Data
@DelegateImplementation(parameterClass = ElasticSearchParameterConnection.class)
public class ElasticSearchDelegate8<T extends GenericItem> extends GenericDelegate<T> {
	Function<Builder, ObjectBuilder<Query>> queryBuilder = null;

	@Inject
	ElasticSearchConnection connection;

	public ElasticSearchDelegate8() {
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
	public ElasticSearchDelegate8(RepositoryService serviceContainer) throws GenericException {
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
		Response response = null;
		IndexSearchResult indexResult = null;
		SearchRequest searchRequest = searchParams.getSearchRequest();
		if (searchRequest != null) {
			if (StringUtils.isNotEmpty(indexName)) {

				HttpEntity queryAsJson = new NStringEntity(searchRequest.asJson(), ContentType.APPLICATION_JSON);
				try {
					String endpoint = "/" + indexName + "/_search";
					Request request = new Request(HttpMethod.GET, endpoint);
					request.setEntity(queryAsJson);
					setHeadersOnRequest(request, true);

					response = connection.getRestClient().performRequest(request);
					indexResult = ElasticsearchUtils.parseResponse(response, IndexSearchResult.class);

				} catch (ElasticSearchException e) {
					log.error("Exception caught parsing search response sent by Elasticsearch", e);
					indexResult = new IndexSearchResult();
					indexResult.setSuccess(false);
				} catch (Exception e) {
					log.error("Exception caught searching documents on Elasticsearch", e);
					indexResult = new IndexSearchResult();
					indexResult.setSuccess(false);
				}
			} else {
				log.warn("Cannot search a document without specifying the target index");
			}
		} else {
			log.warn("Cannot search a document if query is not defined");
		}
		SearchResponse<? extends GenericItem> search = null;
		List<? extends GenericItem> results = null;
		try {
			results = indexResult.getHits().getHits().stream().map(hit -> {
				Map<String, Object> fieldValues = hit.get_source();
				return GenericClassConverter.createObjectFromMap(searchParams.getClassType(), fieldValues);
			}).collect(Collectors.toList());
		} catch (ElasticsearchException e) {
			throw new GenericException("Error search ");
		}
		GenericResults genericResults = new GenericResults(results,
				indexResult.getHits().getTotal().getValue().longValue());
		return genericResults;
	}

	private void setHeadersOnRequest(Request request, boolean isRead) {
		RequestOptions.Builder options = request.getOptions().toBuilder();

		if (StringUtils.isNotEmpty(connection.getElasticSearchModule().getConfiguration().getXopaqueidHeader())) {
			options.addHeader(ElasticSearchConfig.ES_HEADER_XOPAQUEID,
					connection.getElasticSearchModule().getConfiguration().getXopaqueidHeader());
		}
		if (isRead) {
			if (StringUtils.isNotEmpty(connection.getElasticSearchModule().getConfiguration().getApiKeyRead())) {
				options.addHeader(ElasticSearchConfig.ES_HEADER_AUTHORIZATION,
						ElasticSearchConfig.ES_HEADER_APIKEY + " "
								+ connection.getElasticSearchModule().getConfiguration().getApiKeyRead());
			}
		} else {
			if (StringUtils.isNotEmpty(connection.getElasticSearchModule().getConfiguration().getApiKeyWrite())) {
				options.addHeader(ElasticSearchConfig.ES_HEADER_AUTHORIZATION,
						ElasticSearchConfig.ES_HEADER_APIKEY + " "
								+ connection.getElasticSearchModule().getConfiguration().getApiKeyWrite());
			}
		}
		request.setOptions(options);
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
	}

	/**
	 * Adds the item. Add/Update API: PUT [index]/id
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
		try {
			Request requests = null;
			NStringEntity documentAsJson = null;
			String jsonItem = serviceContainer.getConverterClass().convert(item.getObj());
			ElasticSearchParameter parameters = null;
			if (item != null && item.getId() != null) {
				parameters = ElasticSearchParameterFactory.update(item.getKey(), item.getObj().getIdentifier(),
						jsonItem);
			} else {
				parameters = ElasticSearchParameterFactory.create(item.getKey(), item.getObj().getIdentifier(),
						jsonItem);
			}
			requests = new Request(parameters.getHttpMethod(), parameters.getEndpoint());
			setHeadersOnRequest(requests, false);
			documentAsJson = new NStringEntity(parameters.getRequest(), ContentType.APPLICATION_JSON);
			requests.setEntity(documentAsJson);
			Response response = connection.getRestClient().performRequest(requests);
			IndexOperationResult indexResult = ElasticsearchUtils.parseResponse(response, IndexOperationResult.class);
		} catch (ElasticSearchException | IOException e) {
			throw new GenericException("Error AddItem:");
		}
		return item;
	}

	@Override
	public void setConnection(CustomConnection customConnection) {
		connection = (ElasticSearchConnection) customConnection;

	}

}
