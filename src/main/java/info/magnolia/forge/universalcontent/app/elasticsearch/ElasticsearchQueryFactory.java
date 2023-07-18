package info.magnolia.forge.universalcontent.app.elasticsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.forge.universalcontent.app.elasticsearch.configuration.ElasticsearchConfiguration;
import info.magnolia.forge.universalcontent.app.generic.search.Params;
import info.magnolia.forge.universalcontent.app.generic.utils.GenericConstants;
import info.magnolia.forge.universalcontent.elasticsearch.beans.BooleanQuery;
import info.magnolia.forge.universalcontent.elasticsearch.beans.CombinedQuery;
import info.magnolia.forge.universalcontent.elasticsearch.beans.HighlightFragment;
import info.magnolia.forge.universalcontent.elasticsearch.beans.SearchHighlight;
import info.magnolia.forge.universalcontent.elasticsearch.beans.SearchRequest;
import info.magnolia.forge.universalcontent.elasticsearch.beans.SingleQuery;
import info.magnolia.forge.universalcontent.elasticsearch.search.entity.PaginationModel;
import info.magnolia.forge.universalcontent.elasticsearch.search.entity.SearchItem;
import info.magnolia.forge.universalcontent.elasticsearch.search.entity.SearchItemBuilder;
import info.magnolia.forge.universalcontent.elasticsearch.utils.ElasticsearchFramework;
import lombok.Getter;
import lombok.Setter;

public class ElasticsearchQueryFactory {
	protected static final Logger log = LoggerFactory.getLogger(ElasticsearchQueryFactory.class);

	private static ElasticsearchQueryFactory INSTANCE = new ElasticsearchQueryFactory();
	@Getter
	@Setter
	private ElasticsearchConfiguration elasticsearchConfiguration;
	private Params params;
	private PaginationModel paginationModel;
	private List<String> propertyColumns;
	private SearchItemBuilder searchItemBuilder;

	public ElasticsearchQueryFactory() {
		INSTANCE = this;
		this.propertyColumns = new ArrayList<String>();
		this.searchItemBuilder = new SearchItemBuilder();
		this.paginationModel = new PaginationModel();
	}

	public static ElasticsearchQueryFactory configuration(ElasticsearchConfiguration elasticsearchConfiguration) {
		INSTANCE.setElasticsearchConfiguration(elasticsearchConfiguration);
		return INSTANCE;
	}

	public ElasticsearchQueryFactory params(Params params) {
		INSTANCE.params = params;
		return INSTANCE;
	}

	public ElasticsearchQueryFactory pagination(PaginationModel paginationModel) {
		INSTANCE.paginationModel = paginationModel;
		return INSTANCE;
	}

	public ElasticsearchQueryFactory propertyColumns(List<String> propertyColumns) {
		INSTANCE.propertyColumns = propertyColumns;
		return INSTANCE;
	}

	public SearchRequest build() {
		SearchRequest searchRequest = null;

		if (params != null) {

			searchRequest = new SearchRequest();
			CombinedQuery combinedQuery = new CombinedQuery();
			BooleanQuery booleanQuery = new BooleanQuery();
			List<SingleQuery> musts = new LinkedList<SingleQuery>();
			List<SingleQuery> should = new LinkedList<SingleQuery>();
			List<SingleQuery> must_nots = new LinkedList<SingleQuery>();
			List<SingleQuery> filters = new LinkedList<SingleQuery>();

			// Filters (each search item must be converted in a single query)
			Map<String, Object> filterByField = params.getFields().entrySet().stream().filter(key -> {
				try {
					Boolean isNotEmpty;
					try {
						isNotEmpty = StringUtils.isNotEmpty((String) key.getValue());
					} catch (Exception e) {
						isNotEmpty = key.getValue() != null;
					}
					return propertyColumns.contains(key.getKey()) && isNotEmpty;
				} catch (Exception e) {
					log.error("QueryElasticSearchDelegate", e);
				}
				return false;
			}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
			searchItemBuilder = SearchItemBuilder.configuration(elasticsearchConfiguration);
			filterByField.forEach((key, value) -> {
				List<String> fields = new ArrayList<String>();
				fields.add(key);
				searchItemBuilder.addTextSearchItem(fields, (String) value, Boolean.TRUE, Boolean.FALSE,
						ElasticsearchFramework.AND_OPERATOR);
			});
			if (CollectionUtils.isNotEmpty(searchItemBuilder.getSearchItems())) {
				for (SearchItem item : searchItemBuilder.getSearchItems()) {
					if (item != null) {
						SingleQuery must = item.createSearchParameter();
						if (must != null) {
							musts.add(must);
						}
					}
				}
			}
//			FullText from Content APP
			propertyColumns.stream().forEach(field -> {
				if (params.getFields() != null
						&& params.getFields().containsKey(GenericConstants.SEARCH_PARAMS_FULLTEXT_SEARCH)) {
					List<String> fields = new ArrayList<String>();
					fields.add(field);
					SingleQuery singleQuery = searchItemBuilder.addTextSearchItem(fields,
							(String) params.getFields().get(GenericConstants.SEARCH_PARAMS_FULLTEXT_SEARCH),
							Boolean.FALSE, Boolean.FALSE, ElasticsearchFramework.OR_OPERATOR)
							.createFieldsValueSearch(field,
									(String) params.getFields().get(GenericConstants.SEARCH_PARAMS_FULLTEXT_SEARCH),
									1F);
					should.add(singleQuery);
				}
			});

			booleanQuery.setMust(musts);
			booleanQuery.setShould(should);
			booleanQuery.setMust_not(must_nots);
			booleanQuery.setFilter(filters);

			combinedQuery.setBool(booleanQuery);
			searchRequest.setQuery(combinedQuery);

			// Highlight
			SearchHighlight highlight = new SearchHighlight();
			highlight.setTag("em");
			Map<String, HighlightFragment> highlightMap = new HashMap<String, HighlightFragment>();
			for (String fullTextField : ElasticsearchFramework.FULL_TEXT_FIELDS) {
				highlightMap.put(fullTextField, new HighlightFragment());
			}
			highlight.setFields(highlightMap);
			searchRequest.setHighlight(highlight);

			// Sort
//			if (MapUtils.isNotEmpty(params.getSortings())) {
//				boolean isPageAndPathWeightPresent = false;
//
//				List<Map<String, String>> sortMaps = new LinkedList<Map<String, String>>();
//
//				for (String field : params.getSortings().keySet()) {
//
//					Map<String, String> sortMap = new LinkedHashMap<>();
//
//					if (StringUtils.equalsIgnoreCase(field,
//							ElasticsearchConstants.ElasticsearchDocumentField.ES_SUMWEIGHTPAGEANDPATH_FIELD)) {
//						isPageAndPathWeightPresent = true;
//					}
//
//					sortMap.put(field, BooleanUtils.isTrue(params.getSortings().get(field)) ? "asc" : "desc");
//					if (StringUtils.equalsIgnoreCase(field,
//							ElasticsearchConstants.ElasticsearchDocumentField.ES_SUMWEIGHTPAGEANDPATH_FIELD)) {
//						sortMaps.add(0, sortMap);
//					} else {
//						sortMaps.add(sortMap);
//					}
//				}
//				if (BooleanUtils.isFalse(isPageAndPathWeightPresent)) {
//
//					Map<String, String> sortMap = new HashMap<String, String>();
//
//					sortMap.put(ElasticsearchConstants.ElasticsearchDocumentField.ES_SUMWEIGHTPAGEANDPATH_FIELD,
//							"desc");
//					sortMaps.add(0, sortMap);
//				}
//				searchRequest.setSort(sortMaps);
//			}

			// Group by
//			if (StringUtils.isNotEmpty(params.getGroupby())) {
//				Aggregation aggregation = new Aggregation();
//				Map<String, String> terms = new HashMap<String, String>();
//				terms.put("field", params.getGroupby() + ".field");
//				if (params.getGroupSize() != null && params.getGroupSize() > 0) {
//					terms.put("size", params.getGroupSize().toString());
//				}
//				Map<String, Map> map = new HashMap<String, Map>();
//				map.put("terms", terms);
//				aggregation.setAggregation(map);
//				searchRequest.setAggs(aggregation);
//			}

			// Pagination
			if (paginationModel != null && paginationModel.getFrom() != null && paginationModel.getSize() != null) {
				searchRequest.setFrom(paginationModel.getFrom());
				searchRequest.setSize(paginationModel.getSize());
			} else {
				searchRequest.setFrom(0);
				searchRequest.setSize(ElasticsearchFramework.MAX_RESULTS_SIZE);
			}

		}

		return searchRequest;

	}

}
