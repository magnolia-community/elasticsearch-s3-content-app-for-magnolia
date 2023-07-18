package info.magnolia.forge.universalcontent.elasticsearch.beans;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchAggregation implements Serializable {

	@JsonProperty("doc_count_error_upper_bound")
	private int docCountErrorUpperBound;

	@JsonProperty("sum_other_doc_count")
	private int sumOtherDocCount;

	@JsonProperty("buckets")
	private List<Bucket> buckets;

}
