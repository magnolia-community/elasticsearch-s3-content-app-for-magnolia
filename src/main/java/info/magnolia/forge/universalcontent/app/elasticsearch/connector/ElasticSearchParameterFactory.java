package info.magnolia.forge.universalcontent.app.elasticsearch.connector;

import javax.ws.rs.HttpMethod;

import org.apache.commons.lang.StringUtils;

import info.magnolia.forge.universalcontent.elasticsearch.beans.SearchRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ElasticSearchParameterFactory {

	/**
	 * Get count parameters
	 *
	 * @param search
	 * @param index
	 * @return
	 */
	public static ElasticSearchParameter count(SearchRequest search, String index) {
		ElasticSearchParameter parameters = new ElasticSearchParameter();
		if (search != null && StringUtils.isNotEmpty(index)) {
			try {
				String endpoint = "/" + index + "/_count";
				parameters.setEndpoint(endpoint);
				parameters.setHttpMethod(HttpMethod.GET);
				parameters.setRequest(search.asJson());

			} catch (Exception e) {
				log.error("Exception caught searching documents on Elasticsearch", e);
			}
		}
		return parameters;
	}

	public static ElasticSearchParameter update(String index, String id, String body) {
		ElasticSearchParameter parameters = new ElasticSearchParameter();
		if (StringUtils.isNotEmpty(body) && StringUtils.isNotEmpty(index)) {
			String endpoint = "/" + index + "/" + "_doc" + "/" + id;
			parameters.setEndpoint(endpoint);
			parameters.setHttpMethod(HttpMethod.POST);
			parameters.setRequest(body);
		}
		return parameters;
	}

	public static ElasticSearchParameter create(String index, String id, String body) {
		ElasticSearchParameter parameters = new ElasticSearchParameter();
		if (StringUtils.isNotEmpty(body) && StringUtils.isNotEmpty(index)) {
			parameters.setEndpoint("/" + index + "/" + "_create" + "/" + id);
			parameters.setHttpMethod(HttpMethod.POST);
			parameters.setRequest(body);
		}
		return parameters;
	}

	/**
	 * Delete API: DELETE [index]/id
	 *
	 * @param documentIdentifier
	 * @param index
	 * @return
	 */
	public static ElasticSearchParameter delete(String documentIdentifier, String index) {
		ElasticSearchParameter parameters = new ElasticSearchParameter();

		if (StringUtils.isNotEmpty(documentIdentifier) && StringUtils.isNotEmpty(index)) {
			parameters.setEndpoint("/" + index + "/" + "_doc" + "/" + documentIdentifier);
			parameters.setHttpMethod(HttpMethod.DELETE);
		}

		return parameters;
	}

}