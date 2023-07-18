package info.magnolia.forge.universalcontent.elasticsearch.beans;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import info.magnolia.forge.universalcontent.elasticsearch.utils.ElasticsearchFramework;
import lombok.Data;

@Data
public class HighlightFragment implements Serializable {

	@JsonProperty("fragment_size")
	private Integer fragment_size = ElasticsearchFramework.HIGHLIGHT_DEFAULT_MAX_FRAGMENT_SIZE;

	@JsonProperty("number_of_fragments")
	private Integer number_of_fragments = ElasticsearchFramework.HIGHLIGHT_DEFAULT_MAX_FRAGMENTS;

	@JsonProperty("fragmenter")
	private String fragmenter = ElasticsearchFramework.HIGHLIGHT_DEFAULT_FRAGMENTER;

}
