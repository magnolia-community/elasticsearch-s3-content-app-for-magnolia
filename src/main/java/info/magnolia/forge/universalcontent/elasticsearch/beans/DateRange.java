package info.magnolia.forge.universalcontent.elasticsearch.beans;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DateRange implements Serializable {

	@JsonProperty("gte")
	private String gte;

	@JsonProperty("lte")
	private String lte;

	@JsonProperty("format")
	private String format;

}
