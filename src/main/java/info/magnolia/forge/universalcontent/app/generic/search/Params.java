/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.search;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.gson.annotations.Expose;

import info.magnolia.forge.universalcontent.app.generic.entity.GenericItem;
import info.magnolia.forge.universalcontent.app.generic.utils.GenericConstants;
import info.magnolia.forge.universalcontent.elasticsearch.beans.SearchRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Params represent all parameter search In filters field there are all fields
 * needed to search KEY: id value in CostantElasticSearch VALUE: value to be
 * searched.
 */

@Data
@AllArgsConstructor
public class Params implements Serializable {
	private Class source;

	private TypeParam type;
	@Expose
	/** The fields. */
	private Map<String, Object> fields;
	@Expose
	/** The orders. */
	private Map<String, String> orders;
	@Expose
	/** The size page. */
	private int sizePage;
	@Expose
	/** The size. */
	private int size;
	@Expose
	/** The offset. */
	private int offset;
	@Expose
	/** The relevance search. */
	private RelevanceSearch relevanceSearch;

	private Class<? extends GenericItem> classType;
	@Expose
	private SearchRequest searchRequest;

	/**
	 * Instantiates a new params ES.
	 */
	public Params() {
		fields = new HashMap<String, Object>();
		sizePage = GenericConstants.SEARCH_PARAMS_DEFAULT_SIZE_PAGE;
		size = GenericConstants.SEARCH_PARAMS_DEFAULT_SIZE_PAGE;
		offset = GenericConstants.SEARCH_PARAMS_DEFAULT_OFFSET_PAGE;
		relevanceSearch = new RelevanceSearch();
		orders = new HashMap<>();
		searchRequest = new SearchRequest();
	}

	/**
	 * Checks if is field filtered.
	 *
	 * @return the boolean
	 */
	public Boolean isFieldFiltered() {
		return getRelevanceSearch() != null && getRelevanceSearch().getBoostFields() != null
				&& getRelevanceSearch().getBoostFields().size() > 0;
	}

	/**
	 * Checks if is simple query string.
	 *
	 * @return the boolean
	 */
	public Boolean isSimpleQueryString() {
		return getRelevanceSearch() != null && StringUtils.isNotEmpty(getRelevanceSearch().getFullTextSearch());
	}

	@Override
	public String toString() {
		Map<String, Object> printFields = new HashMap<String, Object>();
		for (String key : fields.keySet()) {
			if ((fields.get(key) != null && (!(fields.get(key) instanceof String))
					|| (fields.get(key) != null && fields.get(key) instanceof String)
							&& StringUtils.isNotEmpty((String) fields.get(key)))) {
				printFields.put(key, fields.get(key));
			}
		}
		return "Params [fields=" + printFields + ", orders=" + orders + ", sizePage=" + sizePage + ", size=" + size
				+ ", offset=" + offset + ", searchRequest=" + searchRequest + "]";
	}

}
