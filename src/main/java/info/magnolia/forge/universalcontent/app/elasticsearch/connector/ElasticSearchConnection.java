/*
 *
 */
package info.magnolia.forge.universalcontent.app.elasticsearch.connector;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig.Builder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.sniff.Sniffer;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomConnection;
import info.magnolia.forge.universalcontent.app.elasticsearch.configuration.ElasticsearchConfiguration;
import info.magnolia.forge.universalcontent.app.generic.connection.ElasticSearchParameterConnection;
import info.magnolia.forge.universalcontent.app.generic.connection.ParameterConnection;
import info.magnolia.forge.universalcontent.app.manageES.ElasticSearchModuleCore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Create a general purpose connection with Elastic Search instance Load
 * configuration from Magnolia properties file.
 */
@Data
@Slf4j
public class ElasticSearchConnection implements CustomConnection {

	@Getter
	RestClient restClient;

	/** The client. */
	ElasticsearchClient client;

	private Sniffer sniffer;

	@Getter
	@Setter
	ElasticSearchParameterConnection params;
	@Setter
	@Getter
	@Inject
	ElasticSearchModuleCore elasticSearchModule;
	@Setter
	@Getter
	ElasticsearchConfiguration elasticSearchConfiguration;

	/**
	 * Instantiates a new elastic search connection.
	 */
	public ElasticSearchConnection() {
		if (elasticSearchModule != null) {
			this.elasticSearchConfiguration = elasticSearchModule.getConfiguration();
		}
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
//		restClient = RestClient.builder(new HttpHost(params.getServer(), Integer.valueOf(params.getPort())))
//				.setDefaultHeaders(new Header[] { new BasicHeader("Content-type", "application/json"),
//						new BasicHeader("X-Elastic-Product", "Elasticsearch") })
//				.build();
		ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
		client = new ElasticsearchClient(transport);

		try {
			if (elasticSearchModule != null) {
				this.elasticSearchConfiguration = elasticSearchModule.getConfiguration();
			}
			if (elasticSearchConfiguration != null) {

				List<HttpHost> httpHosts = new LinkedList<HttpHost>();
				List<String> hosts = Arrays.asList(StringUtils.split(elasticSearchConfiguration.getHosts(), ","));
				String port = elasticSearchConfiguration.getPort() == null ? "9200"
						: elasticSearchConfiguration.getPort();

				if (CollectionUtils.isNotEmpty(hosts)) {
					for (String host : hosts) {
						try {
							HttpHost httpHost;
							if (elasticSearchConfiguration.getHttps() != null
									&& BooleanUtils.isTrue(elasticSearchConfiguration.getHttps())) {
								httpHost = new HttpHost(host, Integer.parseInt(port), "https");
							} else {
								httpHost = new HttpHost(host, Integer.parseInt(port), "http");
							}

							if (httpHost != null) {
								httpHosts.add(httpHost);
							} else {
								log.error("Cannot create http host from '{}'", host);
							}
						} catch (Exception e) {
							log.error("Cannot create http host from '{}'", host, e);
						}
					}
				}

				if (CollectionUtils.isNotEmpty(httpHosts)) {
					RestClientBuilder builder = RestClient.builder(httpHosts.toArray(new HttpHost[httpHosts.size()]))
							.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {

								@Override
								public Builder customizeRequestConfig(Builder requestConfigBuilder) {
									return requestConfigBuilder
											.setConnectTimeout(
													(int) (long) elasticSearchConfiguration.getConnectTimeout())
											.setSocketTimeout(
													(int) (long) elasticSearchConfiguration.getSocketTimeout());
								}

							});

					restClient = builder.build();

					restClient = RestClient.builder(httpHosts.toArray(new HttpHost[httpHosts.size()])).build();

					if (Boolean.valueOf(elasticSearchConfiguration.getSniffer())) {
						sniffer = Sniffer.builder(restClient).build();
					}

					log.info("Successfully loaded Elasticsearch configuration");

				} else {
					log.error(
							"No valid http host can be built for connecting to elasticsearch: please check your magnolia.properties");
				}

			} else {
				log.error(
						"No valid configuration found for connecting to elasticsearch: please check your magnolia.properties");
			}

		} catch (Exception e) {
			log.error("Exception caught building Elasticserach's client", e);
		}

	}

	@PreDestroy
	public void release() {
		if (sniffer != null) {
			try {
				sniffer.close();
			} catch (Exception e) {
				log.error("Exception caught closing Elasticserach's sniffer", e);
			}
		}
		if (restClient != null) {
			try {
				restClient.close();
			} catch (Exception e) {
				log.error("Exception caught closing Elasticserach's client", e);
			}
		}
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
