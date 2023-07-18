package info.magnolia.forge.universalcontent.elasticsearch.search.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import lombok.Data;

@Data
public class SearchModel {

	/**
	 * A query clause used in query context answers the question "How well does this
	 * document match this query clause?"<br>
	 * Besides deciding whether or not the document matches, the query clause also
	 * calculates a _score representing how well the document matches, relative to
	 * other documents.
	 */
	private Collection<SearchItem> queryFields = new LinkedList<SearchItem>();

	/**
	 * A query clause used in a filter contest answers the question "Does this
	 * document match this query clause?"<br>
	 * The answer is a simple Yes or No — no scores are calculated.
	 */
	private Collection<SearchItem> filterFields = new LinkedList<SearchItem>();

	/**
	 * Search site
	 */
	private String targetSite = null;

	/**
	 * Search repository
	 */
	private String targetRepository = null;

	/**
	 * Search path context
	 */
	private String targetPath = null;

	/**
	 * The locale to be use for performing search.<br>
	 * If null the default one will be used.
	 */
	private Locale locale;

	/**
	 * Map defining the list of fields to use for sorting.<br>
	 * Key is the field name, the value if the sorting order (ascending if true,
	 * descending otherwise)
	 */
	private Map<String, Boolean> sortings = new HashMap<String, Boolean>();

	private String groupby;

	private Integer groupSize;

	private List<String> idsToExclude;

	private String operator;

	private boolean conjunction = true;

	public void addQuery(SearchItem query) {
		queryFields.add(query);
	}

	public void addFilter(SearchItem filter) {
		filterFields.add(filter);
	}

	public void addSorting(String fieldName, boolean ascending) {
		if (StringUtils.isNotEmpty(fieldName)) {
			sortings.put(fieldName, ascending);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (conjunction ? 1231 : 1237);
		result = prime * result + ((filterFields == null) ? 0 : filterFields.hashCode());
		result = prime * result + ((groupSize == null) ? 0 : groupSize.hashCode());
		result = prime * result + ((groupby == null) ? 0 : groupby.hashCode());
		result = prime * result + ((locale == null) ? 0 : locale.hashCode());
		result = prime * result + ((queryFields == null) ? 0 : queryFields.hashCode());
		result = prime * result + ((sortings == null) ? 0 : sortings.hashCode());
		result = prime * result + ((targetPath == null) ? 0 : targetPath.hashCode());
		result = prime * result + ((targetRepository == null) ? 0 : targetRepository.hashCode());
		result = prime * result + ((targetSite == null) ? 0 : targetSite.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SearchModel other = (SearchModel) obj;
		if (conjunction != other.conjunction)
			return false;
		if (filterFields == null) {
			if (other.filterFields != null)
				return false;
		} else if (!filterFields.equals(other.filterFields))
			return false;
		if (groupSize == null) {
			if (other.groupSize != null)
				return false;
		} else if (!groupSize.equals(other.groupSize))
			return false;
		if (groupby == null) {
			if (other.groupby != null)
				return false;
		} else if (!groupby.equals(other.groupby))
			return false;
		if (locale == null) {
			if (other.locale != null)
				return false;
		} else if (!locale.equals(other.locale))
			return false;
		if (queryFields == null) {
			if (other.queryFields != null)
				return false;
		} else if (!queryFields.equals(other.queryFields))
			return false;
		if (sortings == null) {
			if (other.sortings != null)
				return false;
		} else if (!sortings.equals(other.sortings))
			return false;
		if (targetPath == null) {
			if (other.targetPath != null)
				return false;
		} else if (!targetPath.equals(other.targetPath))
			return false;
		if (targetRepository == null) {
			if (other.targetRepository != null)
				return false;
		} else if (!targetRepository.equals(other.targetRepository))
			return false;
		if (targetSite == null) {
			if (other.targetSite != null)
				return false;
		} else if (!targetSite.equals(other.targetSite))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SearchModel [queryFields=" + queryFields + ", filterFields=" + filterFields + ", targetSite="
				+ targetSite + ", targetRepository=" + targetRepository + ", targetPath=" + targetPath + ", locale="
				+ locale + ", sortings=" + sortings + ", groupSize=" + groupSize + ", groupby=" + groupby
				+ ", conjunction=" + conjunction + "]";
	}

}
