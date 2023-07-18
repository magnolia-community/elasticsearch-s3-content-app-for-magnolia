package info.magnolia.forge.universalcontent.elasticsearch.beans;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchRequest implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	@Expose
	@JsonProperty("from")
	private Integer from;
	@Expose
	@JsonProperty("size")
	private Integer size;
	@Expose
	@JsonProperty("query")
	private CombinedQuery query;
	@Expose
	@JsonProperty("highlight")
	private SearchHighlight highlight;
	@Expose
	@JsonProperty("aggs")
	private Aggregation aggs;
	@Expose
	@JsonProperty("sort")
	private List<Map<String, String>> sort;

	public SearchRequest() {
		this.from = 0;
		this.size = 10;
	}

	public String asJson() {
		Gson gson = new Gson();
		String jsonDocument = gson.toJson(this);
		return jsonDocument;
	}

	@Override
	public String toString() {
		return "SearchRequest [from=" + from + ", size=" + size + ", query=" + query + ", highlight=" + highlight
				+ ", aggs=" + aggs + ", sort=" + sort + "]";
	}

}
