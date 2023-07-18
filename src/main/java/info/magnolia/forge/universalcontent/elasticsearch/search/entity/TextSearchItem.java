package info.magnolia.forge.universalcontent.elasticsearch.search.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import info.magnolia.forge.universalcontent.app.elasticsearch.configuration.ElasticsearchConfiguration;
import info.magnolia.forge.universalcontent.elasticsearch.beans.MultiMatchQuery;
import info.magnolia.forge.universalcontent.elasticsearch.beans.SingleQuery;
import info.magnolia.forge.universalcontent.elasticsearch.beans.TermQuery;
import info.magnolia.forge.universalcontent.elasticsearch.utils.ElasticsearchFramework;
import lombok.Data;

@Data
public class TextSearchItem extends SearchItem {

	/**
	 * Fields to include in the full text (all available fields will be considered
	 * if empty)
	 */
	private List<String> fields;

	/**
	 * Target value to match
	 */
	private String value = null;

	/**
	 * If true all the tokens contained in the target value must match
	 */
	private boolean matchAllValues = false;

	/**
	 * If true the tokens contained in the target value must not be found
	 */
	private boolean notContains = false;
	private String operator;
	private ElasticsearchConfiguration elasticsearchConfiguration;

	public TextSearchItem(List<String> fields, String value, boolean matchAllValues, boolean notContains,
			String operator, ElasticsearchConfiguration configuration) {
		super();
		this.fields = fields;
		this.value = value;
		this.matchAllValues = matchAllValues;
		this.notContains = notContains;
		this.operator = operator;
		this.elasticsearchConfiguration = configuration;
	}

	@Override
	public synchronized SingleQuery createFieldsValueSearch(String field, String value, Float boost) {
		SingleQuery query = new SingleQuery();
		if (this != null && StringUtils.isNotEmpty(value) && boost != null) {
			TermQuery termQuery = new TermQuery();
			termQuery.put(field, value);
			query.setMatch(termQuery);
		}
		return query;
	}

	@Override
	public synchronized SingleQuery createSearchParameter() {
		SingleQuery query = null;
		if (this != null && StringUtils.isNotEmpty(this.getValue()) && elasticsearchConfiguration != null) {
			query = new SingleQuery();
			MultiMatchQuery matchQuery = new MultiMatchQuery();

			List<String> oldFields = this.getFields();
			List<String> boostedFields = new ArrayList<>();
			List<String> tempBoostList = new ArrayList<>();

			Map<String, String> boosts = elasticsearchConfiguration.getBoosts();

			if (CollectionUtils.isNotEmpty(oldFields) && MapUtils.isNotEmpty(boosts)) {

				for (Map.Entry<String, String> configBoostField : boosts.entrySet()) {
					for (String oldField : oldFields) {
						if (configBoostField.getKey().equalsIgnoreCase(oldField)) {
							tempBoostList.add(oldField);
//							oldFields.remove(oldField);
							boostedFields.add(oldField + "^" + configBoostField.getValue());
						}
					}
				}

				if (CollectionUtils.isNotEmpty(oldFields)) {
					boostedFields.addAll(oldFields);
				}

				if (CollectionUtils.isNotEmpty(boostedFields)) {
					matchQuery.setFields(boostedFields);
				} else {
					matchQuery.setFields(oldFields);
				}
			} else if (CollectionUtils.isEmpty(oldFields) && MapUtils.isNotEmpty(boosts)) {
				for (Map.Entry<String, String> configBoostField : boosts.entrySet()) {
					boostedFields.add(configBoostField.getKey() + "^" + configBoostField.getValue());
				}
				matchQuery.setFields(boostedFields);
			} else {
				matchQuery.setFields(oldFields);
			}

			if (StringUtils.isEmpty(operator)) {
				if (this.isMatchAllValues()) {
					matchQuery.setOperator(ElasticsearchFramework.AND_OPERATOR);
				} else {
					matchQuery.setOperator(ElasticsearchFramework.OR_OPERATOR);
				}
			} else {
				matchQuery.setOperator(operator);
			}
			matchQuery.setType(ElasticsearchFramework.ELASTICSEARCH_MULTIMATCH_MOSTFIELDS);
			matchQuery.setQuery(this.getValue());
			matchQuery.setFuzziness(ElasticsearchFramework.FUZZINESS_AUTO);
			query.setMulti_match(matchQuery);
		}
		return query;
	}

}
