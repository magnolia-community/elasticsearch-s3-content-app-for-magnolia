package info.magnolia.forge.universalcontent.elasticsearch.beans;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import lombok.Data;

@Data
public class ElasticSearchFilter {
	private String site;
	private String repository;
	private String root;
	private List<String> templateTypes;
	private List<String> categories;
	private String fieldGroupBy;
	private String fieldBoolean;
	private String hideInList;
	private Integer groupSize;
	private String order;
	private List<String> fulltextFields;
	private String fieldOrder;
	private String phrase;
	private String keywords;
	private String keyword;
	private Calendar startDate;
	private List<String> idsToExclude;
	private Boolean valueBoolean;
	private List<String> templateSubtypes;
	private List<String> extensions;
	private List<String> templates;
	private String operator;
	private String fieldDate;
	private Calendar endDate;
	private Locale locale;
	private List<String> tags;

	public Locale configureLocale(String locale) {
		this.locale = new Locale(locale);
		return this.locale;
	}
}
