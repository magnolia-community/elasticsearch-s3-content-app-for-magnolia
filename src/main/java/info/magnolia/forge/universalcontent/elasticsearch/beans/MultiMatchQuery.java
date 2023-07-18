package info.magnolia.forge.universalcontent.elasticsearch.beans;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Performs a full text query over multiple fields: the text provided is
 * analyzed and the analysis process constructs a boolean query from the
 * provided text.<br>
 * type can be one of the following:
 * <ul>
 * <li>best_fields: Finds documents which match any field, but uses the _score
 * from the best field
 * <li>phrase: Runs a match_phrase query on each field and combines the _score
 * from each field
 * <li>most_fields: Finds documents which match any field and combines the
 * _score from each field
 * </ul>
 * operator can be one of the following:
 * <ul>
 * <li>or
 * <li>and
 * </ul>
 */
@Data
@NoArgsConstructor
public class MultiMatchQuery implements Serializable {

	@JsonProperty("query")
	private String query;

	@JsonProperty("fuzziness")
	private String fuzziness;

	@JsonProperty("fields")
	private List<String> fields;

	@JsonProperty("type")
	private String type;

	@JsonProperty("operator")
	private String operator;

	@Override
	public String toString() {
		return "MultiMatchQuery [query=" + query + ", fuzziness=" + fuzziness + ", fields=" + fields + ", type=" + type
				+ ", operator=" + operator + "]";
	}

}
