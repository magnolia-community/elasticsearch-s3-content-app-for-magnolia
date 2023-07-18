package info.magnolia.forge.universalcontent.app.elasticsearch.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.jcr.Node;

import org.apache.commons.lang3.StringUtils;

import info.magnolia.forge.universalcontent.app.generic.utils.DateConverter;
import info.magnolia.forge.universalcontent.app.generic.utils.MagnoliaConstants;
import info.magnolia.forge.universalcontent.elasticsearch.utils.ElasticsearchDocumentField;
import info.magnolia.forge.universalcontent.elasticsearch.utils.ElasticsearchUtils;
import info.magnolia.forge.universalcontent.elasticsearch.utils.MagnoliaNodeData;
import info.magnolia.forge.universalcontent.elasticsearch.utils.MagnoliaUtils;
import info.magnolia.jcr.util.PropertyUtil;

public class CommonIndexingServiceImpl implements CommonIndexingService {
	/**
	 * Add audit fields to the input document
	 *
	 * @param <D>
	 *
	 * @param elasticSearchDoc
	 * @param node
	 */
	@Override
	public <D> Map<String, Object> addAudit(D elasticSearchDoc, Node node) {
		Map<String, Object> fields = new HashMap<String, Object>();
		Calendar created = PropertyUtil.getDate(node, MagnoliaConstants.MGNL_CREATED_FIELD);
		Calendar modified = null;
		if (created != null) {
			fields.put(ElasticsearchDocumentField.ES_CREATED_FIELD,
					DateConverter.formatDate(created.getTime()));
		}
		modified = PropertyUtil.getDate(node, MagnoliaNodeData.MGNL_INDEXINGTIMESTAMP_FIELD);
		if (modified != null) {

			modified = PropertyUtil.getDate(node, MagnoliaNodeData.MGNL_INDEXINGTIMESTAMP_FIELD);
			fields.put(ElasticsearchDocumentField.ES_MODIFIED_FIELD,
					DateConverter.formatDate(modified.getTime()));
		} else {
			modified = PropertyUtil.getDate(node, MagnoliaConstants.MGNL_MODIFIED_FIELD);
			fields.put(ElasticsearchDocumentField.ES_MODIFIED_FIELD,
					DateConverter.formatDate(modified.getTime()));
		}
		return fields;
	}

	/**
	 * Add the common fields to the input document extracting them from Magnolia
	 * node
	 *
	 * @param <D>
	 *
	 * @param elasticSearchDoc
	 * @param node
	 */
	@Override
	public <D> Map<String, Object> addCommonFields(D elasticSearchDoc, Node node) {
		Map<String, Object> fields = new HashMap<String, Object>();
		fields.put(ElasticsearchDocumentField.ES_IDENTIFIER_FIELD,
				MagnoliaUtils.getDocumentId(node));
		fields.put(ElasticsearchDocumentField.ES_WORKSPACE_FIELD,
				MagnoliaUtils.getNodeWorkspace(node));
		fields.put(ElasticsearchDocumentField.ES_PATH_FIELD,
				MagnoliaUtils.getNodePath(node).toLowerCase());

		Calendar date = PropertyUtil.getDate(node, MagnoliaConstants.MGNL_DATE_FIELD);
		if (date != null) {
			fields.put(ElasticsearchDocumentField.ES_DATE_FIELD,
					DateConverter.formatDate(date.getTime()));
		} else {
			Calendar date1 = PropertyUtil.getDate(node, MagnoliaConstants.MGNL_DATE_FIELD + "1");
			if (date1 != null) {
				fields.put(ElasticsearchDocumentField.ES_DATE_FIELD,
						DateConverter.formatDate(date1.getTime()));
			} else {
				Calendar lastActivated = PropertyUtil.getDate(node, MagnoliaConstants.MGNL_LAST_ACTIVATED);
				if (lastActivated == null) {
					lastActivated = Calendar.getInstance();
				}
				fields.put(ElasticsearchDocumentField.ES_DATE_FIELD,
						DateConverter.formatDate(lastActivated.getTime()));
			}
		}
		return fields;
	}

	@Override
	public StringBuffer getHTML(Node node, Locale locale, String propertyName) {
		StringBuffer sb = new StringBuffer();
		String htmlText = ElasticsearchUtils.getI18nPropertyValue(node, propertyName, locale);
		if (StringUtils.isNotEmpty(htmlText)) {
			String plainText = ElasticsearchUtils.plainText(htmlText);
			if (StringUtils.isNotEmpty(plainText)) {
				sb.append(plainText);
			}
		}
		return sb;
	}

}
