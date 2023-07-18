package info.magnolia.forge.universalcontent.elasticsearch.beans;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RankFeature {

	@JsonProperty("field")
	private String field;
	@JsonProperty("boost")
	private String boost;
}
