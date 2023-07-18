package info.magnolia.forge.universalcontent.elasticsearch.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.jcr.Node;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.Response;

import com.google.gson.Gson;

import info.magnolia.dam.jcr.DamConstants;
import info.magnolia.forge.universalcontent.app.elasticsearch.configuration.ElasticsearchConfiguration;
import info.magnolia.forge.universalcontent.app.generic.entity.GenericItem;
import info.magnolia.forge.universalcontent.app.generic.others.ElasticSearchException;
import info.magnolia.forge.universalcontent.elasticsearch.search.entity.SearchModel;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.repository.RepositoryConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ElasticsearchUtils {

	public static <T> T parseResponse(Response response, Class<T> type) throws ElasticSearchException {

		if (response == null) {
			throw new ElasticSearchException("Failed : null response");
		}

		if (!StringUtils.startsWith(String.valueOf(response.getStatusLine().getStatusCode()), "2")) {
			throw new ElasticSearchException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
		}

		if (response.getEntity() == null) {
			throw new ElasticSearchException("Failed : empty response");
		}

		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		try {
			br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
			String output;
			while ((output = br.readLine()) != null) {
				sb.append(output);
			}
		} catch (IOException e) {
			log.error("Exception caught reading elasticsearch response content", e);
			throw new ElasticSearchException("Failed : response cannot be parsed");
		}

		String responseAsString = sb.toString();
		Gson gson = new Gson();
		T responseAsEntity = gson.fromJson(responseAsString, type);

		return responseAsEntity;

	}

	public static String normalizePath(String path) {
		String returnString = StringUtils.replace(path, "/", "_");
		return returnString;
	}

	/**
	 * Returns the list of Elasticsearch target indexes related with the input
	 * search (target site and local are considered)
	 *
	 * @param configuration Elasticsearch configuration containing all the avilable
	 *                      indexes
	 * @param search
	 * @return
	 */
	public static List<String> getSearchTargetIndexes(ElasticsearchConfiguration configuration, SearchModel search) {
		List<String> indexes = new LinkedList<String>();

		if (search != null && StringUtils.isNotEmpty(search.getTargetSite())) {

			for (String websitePath : configuration.getWebsitePaths()) {
				if (StringUtils.startsWith(websitePath, search.getTargetSite())
						&& (StringUtils.equals(search.getTargetRepository(), RepositoryConstants.WEBSITE)
								|| StringUtils.isBlank(search.getTargetRepository())))
					indexes.add(StringUtils.lowerCase(
							"magnolia" + "_" + RepositoryConstants.WEBSITE + "_" + search.getLocale().toString()));
			}
			for (String damPath : configuration.getDamPaths()) {
				if (StringUtils.startsWith(damPath, search.getTargetSite())
						&& (StringUtils.equals(search.getTargetRepository(), DamConstants.WORKSPACE)
								|| StringUtils.isBlank(search.getTargetRepository())))
					indexes.add(StringUtils.lowerCase(
							"magnolia" + "_" + DamConstants.WORKSPACE + "_" + search.getLocale().toString()));
			}
		}

		return indexes;
	}

	/*
	 * FIELDS
	 */
	public static String getI18nFieldName(String fieldBaseName, Locale locale) {
		if (locale == null) {
			return fieldBaseName;
		} else {
			return fieldBaseName + "_" + locale;
		}
	}

	public static LinkedList<String> getI18nPropertyArraysValue(Node node, String propertyName, Locale locale) {
		Object propertyValue = PropertyUtil.getPropertyValueObject(node, getI18nFieldName(propertyName, locale));
		if (propertyValue == null) {
			propertyValue = PropertyUtil.getPropertyValueObject(node, getI18nFieldName(propertyName, null));
			if (propertyValue != null && propertyValue instanceof LinkedList) {
				LinkedList<String> listNode = (LinkedList<String>) propertyValue;
				return listNode;
			}
		}
		return new LinkedList<String>();
	}

	public static String getI18nPropertyValue(Node node, String propertyName, Locale locale) {
		String propertyValue;
		try {
			propertyValue = PropertyUtil.getString(node, getI18nFieldName(propertyName, locale));
			if (StringUtils.isEmpty(propertyValue)) {
				// Fallback to default locale if no ad hoc title defined for current one
				propertyValue = PropertyUtil.getString(node, getI18nFieldName(propertyName, null));
			}
			if (StringUtils.isNotBlank(propertyValue)) {
				propertyValue = StringEscapeUtils.unescapeHtml4(propertyValue);
			}
		} catch (Exception e) {
			return null;
		}
		return propertyValue;
	}

	public static String getMgnlNodeIdentifier(final GenericItem document) {
		if (document.getIdentifier() != null
				&& StringUtils.split(document.getIdentifier(),
						ElasticsearchConstants.INDEXDOCUMENT_ID_SEPARATOR) != null
				&& StringUtils.split(document.getIdentifier(),
						ElasticsearchConstants.INDEXDOCUMENT_ID_SEPARATOR).length == 2) {
			return StringUtils.split(document.getIdentifier(), ElasticsearchConstants.INDEXDOCUMENT_ID_SEPARATOR)[1];
		} else {
			return null;
		}
	}

	public static String plainText(final String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("<[^>]*>", "");
	}

	public static String escapeQueryString(String query) {
		String escapedQuery = null;
		if (StringUtils.isNotEmpty(query)) {
			String[] searchList = new String[] { "+", "-", "=", "&&", "||", "!", "(", ")", "{", "}", "[", "]", "^",
					"\"", "~", "*", "?", ":", "\\", "/" };
			String[] replacements = new String[] { "\\+", "\\-", "\\=", "\\&&", "\\||", "\\!", "\\(", "\\)", "\\{",
					"\\}", "\\[", "\\]", "\\^", "\\\"", "\\~", "\\*", "\\?", "\\:", "\\\\", "\\/" };
			escapedQuery = StringUtils.replaceEach(query, searchList, replacements);
		}
		return escapedQuery;
	}
}
