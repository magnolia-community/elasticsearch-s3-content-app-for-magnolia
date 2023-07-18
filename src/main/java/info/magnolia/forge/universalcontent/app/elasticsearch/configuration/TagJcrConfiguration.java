package info.magnolia.forge.universalcontent.app.elasticsearch.configuration;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class TagJcrConfiguration {
	private List<String> parameters;
	private String name;
	private String id;
	private Integer priority;
	private List<String> mustPaths;
	private List<String> mustNotPaths;

	public TagJcrConfiguration() {
		this.parameters = new ArrayList<String>();
		this.mustNotPaths = new ArrayList<String>();
		this.mustPaths = new ArrayList<String>();
	}

}
