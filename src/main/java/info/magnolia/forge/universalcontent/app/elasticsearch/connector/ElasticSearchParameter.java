package info.magnolia.forge.universalcontent.app.elasticsearch.connector;

import lombok.Data;

@Data
public class ElasticSearchParameter {
	private String endpoint;
	private String httpMethod;
	private String request;
}
