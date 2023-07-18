package info.magnolia.forge.universalcontent.app.elasticsearch.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import com.google.gson.Gson;

import info.magnolia.dam.jcr.DamConstants;
import info.magnolia.forge.universalcontent.app.elasticsearch.configuration.ElasticsearchConfiguration;
import info.magnolia.forge.universalcontent.app.elasticsearch.configuration.FieldsJcr;
import info.magnolia.forge.universalcontent.app.elasticsearch.configuration.TagJcrConfiguration;
import info.magnolia.forge.universalcontent.app.generic.utils.GenericClassConverter;
import info.magnolia.forge.universalcontent.app.generic.utils.MagnoliaConstants;
import info.magnolia.forge.universalcontent.elasticsearch.utils.ElasticsearchDocumentField;
import info.magnolia.forge.universalcontent.elasticsearch.utils.ElasticsearchUtils;
import info.magnolia.forge.universalcontent.elasticsearch.utils.MagnoliaNodeData;
import info.magnolia.forge.universalcontent.elasticsearch.utils.MagnoliaUtils;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.jcr.util.SessionUtil;
import info.magnolia.module.categorization.functions.CategorizationTemplatingFunctions;
import info.magnolia.objectfactory.Components;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.templating.functions.TemplatingFunctions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PageIndexingServiceImpl implements IndexingService {
	private CommonIndexingService commonIndexingService;
	private ElasticsearchConfiguration elasticsearchConfiguration;
	private Gson gson;

	public PageIndexingServiceImpl() {
		this.gson = new Gson();
		this.commonIndexingService = new CommonIndexingServiceImpl();
	}

	@Override
	public <D> D indexing(Node node, Locale locale, D pageDocument) {

		try {
			Map<String, Object> fields = new HashMap<String, Object>();
			// Common
			fields.putAll(commonIndexingService.addCommonFields(pageDocument, node));
			// Audit
			fields.putAll(commonIndexingService.addAudit(pageDocument, node));
			// Page contents
			fields.putAll(addPageFields(pageDocument, node, locale, "website", (Class<D>) pageDocument.getClass()));
			return (D) GenericClassConverter.createObjectFromMap(pageDocument.getClass(), fields);

		} catch (Exception e) {
			log.error("Exception caught generating document for sending to elasticsearch the node '{}'", node);
			return null;
		}

	}

	public <D> Map<String, Object> addPageFields(D pageDoc, Node node, Locale locale, String workspace,
			Class<D> clazz) {
		Map<String, Object> fields = new HashMap<String, Object>();

		// Content
		StringBuffer sb = new StringBuffer();
		addFullTextContent(node, locale, workspace, sb);
		fields.put(ElasticsearchDocumentField.ES_TEXT_FIELD, sb.toString());
		fields.put(ElasticsearchDocumentField.ES_TEMPLATE_FIELD,
				MagnoliaUtils.getTemplate(node));
		fields.put(ElasticsearchDocumentField.ES_GROUPTYPE_FIELD, "page");

		String localeString = "";
		if (locale != null) {
			localeString = locale.toString().toUpperCase();
		}

		fields.put(ElasticsearchDocumentField.ES_AVAILABLE_FIELD,
				BooleanUtils.negate(PropertyUtil.getBoolean(node, "notAvailable" + localeString, false)).toString());
		// Categories
		List<Node> categoriesNodes = Components.getComponent(CategorizationTemplatingFunctions.class)
				.getCategories(node, MagnoliaNodeData.MGNL_PAGE_CATEGORIES_FIELD);
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

		if (elasticsearchConfiguration.getTagsJcrConfigurations() != null) {
			List<String> tags = new ArrayList<String>();
			for (TagJcrConfiguration tag : elasticsearchConfiguration.getTagsJcrConfigurations()) {
				String path = (String) GenericClassConverter.getFieldValue(clazz, pageDoc, "path");
				Boolean isNotTagged = tag.getMustNotPaths().stream().filter(mustNotPath -> {
					return path != null && path.contains(mustNotPath);
				}).count() == 0;
				Boolean isTagged = tag.getMustPaths().stream().filter(mustPath -> {
					return path != null && path.contains(mustPath);
				}).count() > 0;
				if (isNotTagged && isTagged) {
					tags.add(tag.getId());
				}
			}
			fields.put(ElasticsearchDocumentField.ES_TAGS_FIELD, tags);
		}

		String hideInList = ElasticsearchUtils.getI18nPropertyValue(node,
				MagnoliaNodeData.MGNL_PAGE_HIDE_IN_LIST_FIELD, locale);

		fields.put(ElasticsearchDocumentField.ES_HIDE_IN_LIST, hideInList);
		Boolean hide = PropertyUtil.getBoolean(node,
				MagnoliaNodeData.MGNL_PAGE_HIDE_IN_LIST_FIELD, false);
		fields.put(ElasticsearchDocumentField.ES_HIDE, hide);
		return fields;

	}

	public void addFullTextContent(Node node, Locale locale, String workspace, StringBuffer sb) {
		// Content
		try {
			List<Node> componentNodes = new ArrayList<Node>();
			if (elasticsearchConfiguration.getFieldsJcrFulltext() != null
					&& elasticsearchConfiguration.getFieldsJcrFulltext()
							.get(MagnoliaNodeData.MGNL_FULLTEXT_CONTENT_MAIN_LEVEL) != null) {
				componentNodes.addAll(explorerFieldsAndGetRelatedNodes(node, locale, sb,
						elasticsearchConfiguration.getFieldsJcrFulltext()
								.get(MagnoliaNodeData.MGNL_FULLTEXT_CONTENT_MAIN_LEVEL)));
			}
			TemplatingFunctions cmsfn = Components.getComponent(TemplatingFunctions.class);
			List<Node> areas = cmsfn.children(node, NodeTypes.Area.NAME);
			componentNodes.addAll(MagnoliaUtils.getComponents(workspace, areas));

			if (CollectionUtils.isNotEmpty(componentNodes)) {
				for (Node componentNode : componentNodes) {
					List<Node> areaNodes = new ArrayList<Node>();
					if (elasticsearchConfiguration.getFieldsJcrFulltext() != null && elasticsearchConfiguration
							.getFieldsJcrFulltext()
							.get(MagnoliaNodeData.MGNL_FULLTEXT_CONTENT_SECOND_LEVEL) != null) {
						areaNodes.addAll(explorerFieldsAndGetRelatedNodes(componentNode, locale, sb,
								elasticsearchConfiguration.getFieldsJcrFulltext().get(
										MagnoliaNodeData.MGNL_FULLTEXT_CONTENT_SECOND_LEVEL)));
					}

					areaNodes.addAll(cmsfn.children(componentNode, NodeTypes.Area.NAME));

					// ADD LINKS
					if (CollectionUtils.isNotEmpty(areaNodes))
						for (Node areaNode : areaNodes) {
							if (StringUtils.equals(areaNode.getName(), "links")) {
								List<Node> linksNodes = cmsfn.children(areaNode);
								for (Node linksNode : linksNodes) {
									String link = PropertyUtil.getString(linksNode, "link", "");
									if (StringUtils.isNotBlank(link)) {
										String linkType = PropertyUtil.getString(linksNode, "linkType", "");
										String linkTitle = ElasticsearchUtils.getI18nPropertyValue(linksNode,
												"linkTitle", locale);
										if (StringUtils.equals(linkType, "internal")) {
											if (StringUtils.isNotBlank(linkTitle)) {
												addTextToBuffer(linksNode, locale, sb, "linkTitle");
											} else {
												Node pageNode = SessionUtil
														.getNodeByIdentifier(RepositoryConstants.WEBSITE, link);
												if (pageNode != null)
													addTextToBuffer(pageNode, locale, sb,
															MagnoliaNodeData.MGNL_PAGE_TITLE_FIELD);
											}
										} else if (StringUtils.equals(linkType, "external")) {
											if (StringUtils.isNotBlank(linkTitle)) {
												addTextToBuffer(linksNode, locale, sb, "linkTitle");
											}
										} else if (StringUtils.equals(linkType, "download")) {
											if (StringUtils.isNotBlank(linkTitle)) {
												addTextToBuffer(linksNode, locale, sb, "linkTitle");
											} else {
												Node assetNode = SessionUtil.getNodeByIdentifier(DamConstants.WORKSPACE,
														StringUtils.replace(link, MagnoliaConstants.JCR, ""));
												if (assetNode != null)
													addTextToBuffer(assetNode, locale, sb,
															MagnoliaNodeData.MGNL_PAGE_TITLE_FIELD);
											}
										}
									}
								}
							}
						}
				}
			}
		} catch (Exception e) {
			log.error("Exception caught adding textual contents of the components within page '{}'", node, e);
		}
	}

	public String addTextToBuffer(Node node, Locale locale, StringBuffer sb, String nodeDataName) {
		String text = ElasticsearchUtils.getI18nPropertyValue(node, nodeDataName, locale);
		String plainText = "";
		if (StringUtils.isNotEmpty(text)) {
			plainText = ElasticsearchUtils.plainText(text);
			if (StringUtils.isNotEmpty(plainText)) {

				sb.append(plainText);
				sb.append(" ");
			}
		}
		return plainText;
	}

	public List<Node> explorerFieldsAndGetRelatedNodes(Node node, Locale locale, StringBuffer sb,
			String configurationJson) {
		List<Node> relatedNodes = new ArrayList<Node>();
		if (configurationJson != null) {
			FieldsJcr fieldsJcr = gson.fromJson(configurationJson, FieldsJcr.class);
			for (String key : fieldsJcr.getFieldsText()) {
				addTextToBuffer(node, locale, sb, key);
			}

			for (String key : fieldsJcr.getFieldsNode()) {
				if (Strings.isEmpty(key)) {
					LinkedList<String> listNode = ElasticsearchUtils.getI18nPropertyArraysValue(node, key,
							locale);
					if (listNode != null) {
						relatedNodes.addAll(listNode.stream().map(idNode -> {
							return MagnoliaUtils.getMgnlNode(idNode, "website");
						}).collect(Collectors.toList()));
					}
				}
			}
			for (String key : fieldsJcr.getFieldsJcrNodeId()) {
				String id = addTextToBuffer(node, locale, sb, key);
				if (Strings.isEmpty(id)) {
					id = addTextToBuffer(node, null, sb, key);
				}
				if (StringUtils.isNotEmpty(key)) {
					Node assetNode = null;
					assetNode = SessionUtil.getNodeByIdentifier(DamConstants.WORKSPACE,
							StringUtils.replace(id, MagnoliaConstants.JCR, ""));

					if (assetNode != null) {
						relatedNodes.add(assetNode);
					}
				}
			}
		}
		return relatedNodes;
	}

	@Override
	public IndexingService configuration(ElasticsearchConfiguration elasticsearchConfiguration) {
		this.elasticsearchConfiguration = elasticsearchConfiguration;
		return this;
	}
}
