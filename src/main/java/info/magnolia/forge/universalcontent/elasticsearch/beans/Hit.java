package info.magnolia.forge.universalcontent.elasticsearch.beans;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Hit implements Serializable {

	@JsonProperty("_index")
	private String _index;

	@JsonProperty("_type")
	private String _type;

	@JsonProperty("_id")
	private String _id;

	@JsonProperty("_score")
	private float _score;

	@JsonProperty("_source")
	private Map<String, Object> _source;

	@JsonIgnore
	private Highlight highlight;

}
