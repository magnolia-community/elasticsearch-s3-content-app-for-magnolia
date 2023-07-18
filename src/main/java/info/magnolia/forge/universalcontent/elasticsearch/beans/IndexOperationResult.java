package info.magnolia.forge.universalcontent.elasticsearch.beans;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class IndexOperationResult implements Serializable {

	@JsonProperty("_index")
	private String index;

	@JsonProperty("_type")
	private String type;

	@JsonProperty("_id")
	private String id;

	@JsonProperty("_version")
	private String version;

	@JsonProperty("created")
	private String created;

	@JsonProperty("result")
	private String result;

	@JsonIgnore
	private boolean success = true;

	@Override
	public String toString() {
		return "IndexOperarionResult [index=" + index + ", type=" + type + ", id=" + id + ", version=" + version
				+ ", created=" + created + ", result=" + result + "]";
	}

}
