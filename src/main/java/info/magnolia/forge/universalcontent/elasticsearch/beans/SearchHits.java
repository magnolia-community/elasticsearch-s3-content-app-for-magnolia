package info.magnolia.forge.universalcontent.elasticsearch.beans;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchHits implements Serializable {

	@JsonProperty("total")
	private Total total;

	@JsonProperty("max_score")
	private float maxScore;

	@JsonProperty("hits")
	private List<Hit> hits;
}
