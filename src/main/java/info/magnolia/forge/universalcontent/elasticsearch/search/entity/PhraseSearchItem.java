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
import info.magnolia.forge.universalcontent.elasticsearch.utils.ElasticsearchFramework;
import lombok.Data;

@Data
public class PhraseSearchItem extends SearchItem {

	/**
	 * Fields to include in the full text (all available fields will be considered
	 * if empty)
	 */
	private List<String> fields;

	/**
	 * Target value to match
	 */
	private String value = null;
	private ElasticsearchConfiguration configuration;

	public PhraseSearchItem(List<String> fields, String value, ElasticsearchConfiguration configuration) {
		this.fields = fields;
		this.value = value;
		this.configuration = configuration;
	}

	@Override
	public SingleQuery createSearchParameter() {
		SingleQuery query = null;
		if (StringUtils.isNotEmpty(this.getValue()) && configuration != null) {
			query = new SingleQuery();
			MultiMatchQuery matchQuery = new MultiMatchQuery();

			List<String> oldFields = ElasticsearchFramework.FULL_TEXT_FIELDS;
			List<String> boostedFields = new ArrayList<>();
			List<String> tempBoostList = new ArrayList<>();

			Map<String, String> boosts = configuration.getBoosts();

			if (CollectionUtils.isNotEmpty(oldFields) && MapUtils.isNotEmpty(boosts)) {

				for (Map.Entry<String, String> configBoostField : boosts.entrySet()) {
					for (String oldField : oldFields) {
						if (configBoostField.getKey().equalsIgnoreCase(oldField)) {
							tempBoostList.add(oldField);
							oldFields.remove(oldField);
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

			matchQuery.setType(ElasticsearchFramework.ELASTICSEARCH_MULTIMATCH_PHRASE);
			matchQuery.setQuery(this.getValue());
			matchQuery.setFuzziness(ElasticsearchFramework.FUZZINESS_AUTO);
			query.setMulti_match(matchQuery);
		}
		return query;
	}

}
