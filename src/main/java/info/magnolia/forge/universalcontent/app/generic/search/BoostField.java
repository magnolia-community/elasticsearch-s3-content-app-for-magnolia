/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.search;

import com.google.gson.annotations.Expose;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Field boosted in search In Elastic Search API: must be before "^" character
 * at 7.17 version. ES MustContainWord and shouldContainWord fields for future
 * use in search API
 *
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BoostField {
	@Expose
	/** The field name. */
	private String fieldName;
	@Expose
	/** The boost. */
	private Integer boost;
	@Expose
	/** The must contain word. */
	private String mustContainWord;
	@Expose
	/** The should contain word. */
	private String shouldContainWord;
	@Expose
	/** The not contain word. */
	private String notContainWord;
}
