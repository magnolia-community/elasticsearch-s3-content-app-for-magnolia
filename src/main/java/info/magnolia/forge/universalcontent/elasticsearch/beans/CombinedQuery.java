package info.magnolia.forge.universalcontent.elasticsearch.beans;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class CombinedQuery {
	@Expose
	private BooleanQuery bool;

}
