package info.magnolia.forge.universalcontent.elasticsearch.beans;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Defines a numeric range
 */
@Data
public class NumericRange implements Serializable {

	@JsonProperty("gte")
	private Integer gte;

	@JsonProperty("gte")
	private Integer lte;

}
