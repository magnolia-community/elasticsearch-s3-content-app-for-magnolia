package info.magnolia.forge.universalcontent.elasticsearch.beans;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SingleQuery implements Serializable {

	/**
	 * Allows the definition of a term filter over a list of values: only documents
	 * matching any of these values will be included within the results<br>
	 * { "query": {"terms" : { "targetField" : ["value1", "value2"]} } }
	 */
	@Expose
	@JsonProperty("terms")
	private TermsQuery terms;
	@Expose
	@JsonProperty("match")
	private TermQuery match;
	/**
	 * Allows the definition of a full text search for a target string over a list
	 * of fields { "query": { "multi_match" : { "query": "value1", "fields": [
	 * "field1", "field2", "field3" ] } } }
	 */
	@Expose
	@JsonProperty("multi_match")
	private MultiMatchQuery multi_match;
	@Expose
	@JsonProperty("rank_feature")
	private RankFeature rank_feature;

	/**
	 * Allows the execution of wildcard queries
	 */
	@Expose
	@JsonProperty("query_string")
	private MultiMatchQuery query_string;

	@Expose
	@JsonProperty("range")
	private RangeQuery range;
	@Expose
	@JsonProperty("ids")
	private IdsQuery ids;

	@Override
	public String toString() {
		return "SingleQuery [terms=" + terms + ", match=" + match + ", multi_match=" + multi_match + ", rank_feature="
				+ rank_feature + ", query_string=" + query_string + ", range=" + range + ", ids=" + ids + "]";
	}

}
