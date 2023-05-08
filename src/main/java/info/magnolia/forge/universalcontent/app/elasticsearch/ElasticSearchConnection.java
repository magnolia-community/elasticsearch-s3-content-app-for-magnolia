/*
 *
 */
package info.magnolia.forge.universalcontent.app.elasticsearch;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomConnection;
import info.magnolia.forge.universalcontent.app.generic.connection.ElasticSearchParameterConnection;
import info.magnolia.forge.universalcontent.app.generic.connection.ParameterConnection;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Create a general purpose connection with Elastic Search instance Load
 * configuration from Magnolia properties file.
 */
@Data
public class ElasticSearchConnection implements CustomConnection {

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(ElasticSearchConnection.class);

	/** The rest client. */
	RestClient restClient;

	/** The client. */
	ElasticsearchClient client;

	@Getter
	@Setter
	ElasticSearchParameterConnection params;

	/**
	 * Instantiates a new elastic search connection.
	 */
	public ElasticSearchConnection() {
		if (params != null) {
			connection(params);
		}
	}

	/**
	 * Connection.
	 *
	 * @param server the server
	 * @param port   the port
	 */
	@Override
	public void connection(ParameterConnection params) {
		restClient = RestClient.builder(new HttpHost(params.getServer(), Integer.valueOf(params.getPort())))
				.setDefaultHeaders(new Header[] { new BasicHeader("Content-type", "application/json"),
						new BasicHeader("X-Elastic-Product", "Elasticsearch") })
				.build();
		ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
		client = new ElasticsearchClient(transport);
	}

	@Override
	public void connect() {
		connection(params);
	}

	@Override
	public void setParams(ParameterConnection parameterConnection) {
		this.params = (ElasticSearchParameterConnection) parameterConnection;
	}

}
