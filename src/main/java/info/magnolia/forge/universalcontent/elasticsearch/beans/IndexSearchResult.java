package info.magnolia.forge.universalcontent.elasticsearch.beans;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IndexSearchResult implements Serializable {

	@JsonProperty("hits")
	private SearchHits hits;

	@JsonIgnore
	private boolean success = true;

}
