package info.magnolia.forge.universalcontent.elasticsearch.utils;

import java.util.Arrays;
import java.util.List;

public class ElasticsearchFramework {
	public static final String ELASTICSEARCH_MULTIMATCH_BESTFIELDS = "best_fields";
	public static final String ELASTICSEARCH_MULTIMATCH_MOSTFIELDS = "most_fields";
	public static final String ELASTICSEARCH_MULTIMATCH_PHRASE = "phrase";
	public static final String ALL_FIELDS_SEARCH = "_all";
	public static final String OR_OPERATOR = "or";
	public static final String AND_OPERATOR = "and";
	public static final List<String> FULL_TEXT_FIELDS = Arrays.asList("title", "description", "text");
	public static final Integer HIGHLIGHT_DEFAULT_MAX_FRAGMENT_SIZE = 180;
	public static final Integer HIGHLIGHT_DEFAULT_MAX_FRAGMENTS = 5;
	public static final String HIGHLIGHT_DEFAULT_FRAGMENTER = "simple";
	public static final Integer MAX_RESULTS_SIZE = 10000;
	public static final String FUZZINESS_AUTO = "auto";
}