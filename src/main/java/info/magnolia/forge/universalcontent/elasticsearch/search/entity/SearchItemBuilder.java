package info.magnolia.forge.universalcontent.elasticsearch.search.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import info.magnolia.forge.universalcontent.app.elasticsearch.configuration.ElasticsearchConfiguration;
import lombok.Getter;

public class SearchItemBuilder {
	@Getter
	private List<SearchItem> searchItems = new ArrayList<SearchItem>();
	private ElasticsearchConfiguration configuration;

	public static SearchItemBuilder configuration(ElasticsearchConfiguration configuration) {
		SearchItemBuilder searchItemBuilder = new SearchItemBuilder();
		searchItemBuilder.configuration = configuration;
		return searchItemBuilder;
	}

	public SearchItem addBooleanSearchItem(String field, Boolean value) {
		BooleanSearchItem booleanSearchItem = new BooleanSearchItem(field, value);
		this.searchItems.add(booleanSearchItem);
		return booleanSearchItem;
	}

	public SearchItem addDateRangeSearchItem(String field, Calendar start, Calendar end) {
		DateRangeSearchItem dateRangeSearchItem = new DateRangeSearchItem(field, start, end);
		this.searchItems.add(dateRangeSearchItem);
		return dateRangeSearchItem;
	}

	public SearchItem addKeywordSearchItem(String field, List<String> collection) {
		KeywordSearchItem keywordSearchItem = new KeywordSearchItem(field, collection);
		this.searchItems.add(keywordSearchItem);
		return keywordSearchItem;
	}

	public SearchItem addNumericSearchItem(String field, Integer start, Integer end) {
		NumericRangeSearchItem numericRangeSearchItem = new NumericRangeSearchItem(field, start, end);
		this.searchItems.add(numericRangeSearchItem);
		return numericRangeSearchItem;
	}

	public SearchItem addPhraseSearchItem(List<String> field, String value) {
		PhraseSearchItem phraseSearchItem = new PhraseSearchItem(field, value, configuration);
		this.searchItems.add(phraseSearchItem);
		return phraseSearchItem;
	}

	public SearchItem addTextSearchItem(List<String> fields, String value, boolean matchAllValues, boolean notContains,
			String operator) {
		TextSearchItem textSearchItem = new TextSearchItem(fields, value, matchAllValues, notContains, operator,
				configuration);
		this.searchItems.add(textSearchItem);
		return textSearchItem;
	}

	public SearchItem addWildcardSearchItem(List<String> fields, String value, boolean matchAllValues,
			boolean notContains, String operator) {
		WildcardSearchItem wildcardSearchItem = new WildcardSearchItem(fields, value, configuration);
		this.searchItems.add(wildcardSearchItem);
		return wildcardSearchItem;
	}

}
