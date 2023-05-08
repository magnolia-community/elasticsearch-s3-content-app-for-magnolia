/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.search;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * For enabling boosted search, highlight flag for enabling highlight
 * information. isSimpleQueryString for enabling all fields search
 *
 */

@Data
public class RelevanceSearch {
	@Expose
	/** The full text search. */
	private String fullTextSearch;
	@Expose
	/** The is highlight. */
	private Boolean isHighlight;
	@Expose
	/** The is simple query string. */
	private Boolean isSimpleQueryString;
	@Expose
	/** The boost fields. */
	private List<BoostField> boostFields;

	/**
	 * Instantiates a new relevance search.
	 */
	public RelevanceSearch() {
		boostFields = new ArrayList<>();
		isSimpleQueryString = Boolean.TRUE;
		isHighlight = Boolean.FALSE;
	}

}
