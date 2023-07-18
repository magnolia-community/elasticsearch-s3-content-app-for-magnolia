package info.magnolia.forge.universalcontent.app.elasticsearch.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

import info.magnolia.dam.jcr.AssetNodeTypes;
import info.magnolia.forge.universalcontent.app.elasticsearch.configuration.ElasticsearchConfiguration;
import info.magnolia.forge.universalcontent.app.generic.entity.GenericItem;
import info.magnolia.forge.universalcontent.app.generic.utils.GenericClassConverter;
import info.magnolia.forge.universalcontent.elasticsearch.utils.ElasticsearchDocumentField;
import info.magnolia.forge.universalcontent.elasticsearch.utils.ElasticsearchUtils;
import info.magnolia.forge.universalcontent.elasticsearch.utils.MagnoliaNodeData;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.categorization.functions.CategorizationTemplatingFunctions;
import info.magnolia.objectfactory.Components;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AssetIndexingServiceImpl<D extends GenericItem> implements IndexingService {
	private CommonIndexingService commonIndexingService;
	private ElasticsearchConfiguration elasticsearchConfiguration;

	public AssetIndexingServiceImpl() {
		this.commonIndexingService = new CommonIndexingServiceImpl();
	}

	@Override
	public <D> D indexing(Node node, Locale locale, D assetDocument) {
		try {

			Map<String, Object> fields = new HashMap<String, Object>();
			// Common
			fields.putAll(commonIndexingService.addCommonFields(assetDocument, node));
			// Audit
			fields.putAll(commonIndexingService.addAudit(assetDocument, node));
			// Asset contents
			fields.putAll(addAssetFields(assetDocument, node, locale));

			fields.put(ElasticsearchDocumentField.ES_AVAILABLE_FIELD, "true");
			return (D) GenericClassConverter.createObjectFromMap(assetDocument.getClass(), fields);
		} catch (Exception e) {
			log.error("Exception caught generating document for sending to elasticsearch the node '{}'", node);
			return null;
		}

	}

	private <D> Map<String, Object> addAssetFields(D assetDoc, Node node, Locale locale) {
		Map<String, Object> fields = new HashMap<String, Object>();
		String i18nTitle = ElasticsearchUtils.getI18nPropertyValue(node,
				MagnoliaNodeData.MGNL_ASSET_SUBJECT_FIELD, locale);
		if (StringUtils.isEmpty(i18nTitle)) {
			i18nTitle = ElasticsearchUtils.getI18nPropertyValue(node,
					MagnoliaNodeData.MGNL_PAGE_TITLE_FIELD, locale);
		}
		StringBuffer i18nDescription = commonIndexingService.getHTML(node, locale,
				MagnoliaNodeData.MGNL_ASSET_DESCRIPTION_FIELD);
		fields.put(MagnoliaNodeData.MGNL_ASSET_DESCRIPTION_FIELD, i18nDescription);
		// Name
		fields.put(ElasticsearchDocumentField.ES_NAME_FIELD, StringUtils.trim(i18nTitle));

		String groupType = "document";

		fields.put(ElasticsearchDocumentField.ES_GROUPTYPE_FIELD, groupType);

		int weightPage = Integer.valueOf(PropertyUtil.getString(node,
				ElasticsearchDocumentField.ES_WEIGHTPAGE_FIELD, "0"));
		fields.put(ElasticsearchDocumentField.ES_WEIGHTPAGE_FIELD, PropertyUtil.getString(node,
				ElasticsearchDocumentField.ES_WEIGHTPAGE_FIELD, "0"));
		int weightPath = Integer.valueOf(PropertyUtil.getString(node,
				ElasticsearchDocumentField.ES_WEIGHTPATH_FIELD, "0"));
		fields.put(ElasticsearchDocumentField.ES_WEIGHTPATH_FIELD, PropertyUtil.getString(node,
				ElasticsearchDocumentField.ES_WEIGHTPAGE_FIELD, "0"));
		fields.put(ElasticsearchDocumentField.ES_SUMWEIGHTPAGEANDPATH_FIELD,
				String.valueOf(weightPage + weightPath));
		fields.put(ElasticsearchDocumentField.ES_AVAILABLE_FIELD,
				PropertyUtil.getString(node, "notAvailable" + locale.toString().toUpperCase()));
		// Content
		fields.put(ElasticsearchDocumentField.ES_TITLE_FIELD, i18nTitle);
		fields.put(ElasticsearchDocumentField.ES_DESCRIPTION_FIELD, i18nDescription.toString());


		Node n = null;
		if (n != null) {
			InputStream nodeInputStream;
			try {
				nodeInputStream = n.getProperty(AssetNodeTypes.AssetResource.DATA).getBinary().getStream();
				if (nodeInputStream != null) {
					ContentHandler textHandler = new BodyContentHandler(10 * 1024 * 1024);
					Metadata meta = new Metadata();
					Parser parser = new AutoDetectParser(); // handles documents in different formats:
					ParseContext context = new ParseContext();
					parser.parse(nodeInputStream, textHandler, meta, context); // convert to plain text
					fields.put(ElasticsearchDocumentField.ES_TEXT_FIELD, textHandler.toString());
				}
				String assetName = PropertyUtil.getString(n, "fileName", "");
				fields.put(ElasticsearchDocumentField.ES_NAME_FIELD, assetName);

				String assetTitle = PropertyUtil.getString(n, "fileName", "");
				fields.put(ElasticsearchDocumentField.ES_TITLE_FIELD, assetTitle);

			} catch (Throwable t) {
				log.error("Exception caught indexing binary content for node '{}' according to locale '{}'", node,
						locale, t);
			}

			// Mime type
			String extension = PropertyUtil.getString(n, AssetNodeTypes.AssetResource.EXTENSION, null);
			fields.put(ElasticsearchDocumentField.ES_EXTENSION_FIELD, extension);
		}

		// Categories
		List<Node> categoriesNodes = Components.getComponent(CategorizationTemplatingFunctions.class)
				.getCategories(node, MagnoliaNodeData.MGNL_ASSET_CATEGORIES_FIELD);
		List<String> categoriesUUIDs = new ArrayList<String>();
		if (CollectionUtils.isNotEmpty(categoriesNodes)) {
			for (Node categoryNode : categoriesNodes) {
				try {
					categoriesUUIDs.add(categoryNode.getIdentifier());
				} catch (RepositoryException e) {
					log.error("Exception caught retrieving UUID for node '{}'", categoryNode, e);
				}
			}
		}
		if (CollectionUtils.isNotEmpty(categoriesUUIDs)) {
			fields.put(ElasticsearchDocumentField.ES_CATEGORIES_FIELD, categoriesUUIDs);
		}
		return fields;
	}

	@Override
	public IndexingService configuration(ElasticsearchConfiguration elasticsearchConfiguration) {
		this.elasticsearchConfiguration = elasticsearchConfiguration;
		return this;
	}
}
