package info.magnolia.forge.universalcontent.elasticsearch.beans;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Highlight implements Serializable {
	private List<String> title;

	private List<String> description;

	private List<String> text;

}
