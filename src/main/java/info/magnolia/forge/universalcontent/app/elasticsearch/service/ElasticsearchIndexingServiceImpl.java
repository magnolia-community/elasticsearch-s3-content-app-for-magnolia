package info.magnolia.forge.universalcontent.app.elasticsearch.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.v7.data.Item;

import info.magnolia.dam.jcr.DamConstants;
import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomContentConnector;
import info.magnolia.forge.universalcontent.app.elasticsearch.configuration.ElasticsearchConfiguration;
import info.magnolia.forge.universalcontent.app.elasticsearch.connector.ElasticSearchOperation;
import info.magnolia.forge.universalcontent.app.elasticsearch.connector.ElasticSearchParameterFactory;
import info.magnolia.forge.universalcontent.app.generic.annotation.GenericEntity;
import info.magnolia.forge.universalcontent.app.generic.custom.entity.Page;
import info.magnolia.forge.universalcontent.app.generic.entity.ItemDelegate;
import info.magnolia.forge.universalcontent.app.generic.others.ElasticSearchException;
import info.magnolia.forge.universalcontent.app.generic.service.RepositoryService;
import info.magnolia.forge.universalcontent.app.generic.utils.MagnoliaConstants;
import info.magnolia.forge.universalcontent.app.manageES.ElasticSearchModuleCore;
import info.magnolia.forge.universalcontent.elasticsearch.utils.MagnoliaUtils;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.jcr.util.SessionUtil;
import info.magnolia.publishing.Constants;
import info.magnolia.repository.RepositoryConstants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class ElasticsearchIndexingServiceImpl implements IIndexingService {
	@Getter
	@Setter
	@Inject
	private ElasticSearchParameterFactory client;
	private ElasticsearchConfiguration configuration;

	@Inject
	private ElasticSearchModuleCore elasticSearchModule;
	@Inject
	CustomContentConnector customContentConnector;
	@Inject
	RepositoryService repositoryService;

	@Override
	public <D> List<D> retrieveDataIndexing(Node node, ElasticsearchConfiguration configuration,
			ElasticSearchOperation operation, D a, IndexingService indexingService) throws ElasticSearchException {
		this.configuration = configuration;
		List<D> documents = new ArrayList<D>();
		if (node != null) {
			String nodePath = null;
			String workspace = null;
			try {
				String jcrPrimaryType = PropertyUtil.getString(node, MagnoliaConstants.JCR_PRIMARY_TYPE, "");
				if (StringUtils.equals(jcrPrimaryType, MagnoliaConstants.MGNL_FOLDER)) {
					return documents;
				}

				nodePath = node.getPath();
				workspace = node.getSession().getWorkspace().getName();

				Boolean isResourceExcludedFromIndexing = false;
				Node currentNode = SessionUtil.getNode(workspace, nodePath);

				List<Map<String, String>> excludedResourcesList = configuration.getExcludedResources();

				for (Map<String, String> excludedPathMap : excludedResourcesList) {
					for (Map.Entry<String, String> excludedEntry : excludedPathMap.entrySet()) {
						if (workspace.equals(excludedPathMap.get("name"))
								&& currentNode.getPath().contains(excludedEntry.getValue())) {
							isResourceExcludedFromIndexing = true;
							break;
						}
					}
				}

				Boolean noIndex = PropertyUtil.getBoolean(currentNode, "noIndexEs", false);
				if (noIndex) {
					retrieveDataIndexing(node, configuration, ElasticSearchOperation.ELASTIC_SEARCH_OPERATION_UNINDEX,
							a, indexingService);
					return documents;
				}

				if (BooleanUtils.isFalse(isResourceExcludedFromIndexing)) {
					if (StringUtils.equals(workspace, DamConstants.WORKSPACE)) {
						List<Node> items = new ArrayList<Node>();
						Node current = currentNode;
						while (current.getDepth() >= 1) {
							;
							items.add(current);
							if (current.getDepth() == 1) {
								break;
							}
							current = current.getParent();
						}
						Collections.reverse(items);
						for (Node item : items) {
							noIndex = PropertyUtil.getBoolean(item, "noIndexEs", false);

							if (noIndex) {
								retrieveDataIndexing(node, configuration,
										ElasticSearchOperation.ELASTIC_SEARCH_OPERATION_UNINDEX, a, indexingService);
								return documents;
							}
						}
					}

					for (String language : configuration.getLocales().keySet()) {
						Locale locale = null;
						if (!"default".equals(language)) {
							locale = new Locale(language);
						}
						D doc = indexingService.configuration(elasticSearchModule.getConfiguration())
								.indexing(currentNode, locale, a);
						documents.add(doc);
					}
				}
			} catch (Exception e) {
				log.error("Exception caught retrieving workspace and/or path for node '{}'", node, e);
			}

		}
		return documents;
	}

	@Override
	public synchronized void unindex(HttpServletRequest request) throws Exception {
		String uuidNode = null;
		String workspaceNode = null;
		try {
			uuidNode = request.getHeader(Constants.Parameters.NODE_UUID);
			workspaceNode = request.getHeader(Constants.Parameters.WORKSPACE_NAME);
			Node node = NodeUtil.getNodeByIdentifier(workspaceNode, uuidNode);
			if (node != null) {
				Page page = new Page();
				if (node != null) {
					retrieveDataIndexing(node, elasticSearchModule.getConfiguration(),
							ElasticSearchOperation.ELASTIC_SEARCH_OPERATION_UNINDEX, page, page.getServiceIndexing());

					List<Node> children = new ArrayList<Node>();
					String workspace = node.getSession().getWorkspace().getName();
					if (StringUtils.equalsIgnoreCase(workspace, RepositoryConstants.WEBSITE)) {
						MagnoliaUtils.searchNodes(node.getPath(), children, RepositoryConstants.WEBSITE,
								NodeTypes.Page.NAME, null);
					} else if (StringUtils.equalsIgnoreCase(workspace, DamConstants.WORKSPACE)) {
						if (node.getPrimaryNodeType() != null && StringUtils
								.equalsIgnoreCase(node.getPrimaryNodeType().getName(), NodeTypes.Folder.NAME)) {
							MagnoliaUtils.searchNodes(node.getPath(), children, DamConstants.WORKSPACE,
									MagnoliaConstants.MGNL_ASSET, MagnoliaConstants.MGNL_FOLDER);
						}
					}
					if (CollectionUtils.isNotEmpty(children)) {
						for (Node child : children) {
							log.debug("Unindexing child content {}'", child);
							retrieveDataIndexing(child, elasticSearchModule.getConfiguration(),
									ElasticSearchOperation.ELASTIC_SEARCH_OPERATION_UNINDEX, page,
									page.getServiceIndexing());
						}
					}
				}
			}
		} catch (Throwable t) {
			log.error("Exception caught unindexing request", t);
		}

	}

	@Override
	public synchronized void index(HttpServletRequest request) throws Exception {

		try {
			final String workspace = request.getHeader(Constants.Parameters.WORKSPACE_NAME);
			final String uuid = request.getHeader(Constants.Parameters.NODE_UUID);
			if (StringUtils.isNotEmpty(workspace) && StringUtils.isNotEmpty(uuid)) {

				Node content = NodeUtil.getNodeByIdentifier(workspace, uuid);
				if (content != null) {

					String jcrPrimaryType = PropertyUtil.getString(content, MagnoliaConstants.JCR_PRIMARY_TYPE, "");

					log.debug("Indexing request for content with UUID '{}' within workspace '{}'", uuid, workspace);
					Page page = new Page();
					List<Page> docs = retrieveDataIndexing(content, elasticSearchModule.getConfiguration(),
							ElasticSearchOperation.ELASTIC_SEARCH_OPERATION_INDEX, page, page.getServiceIndexing());
					docs.stream().forEach(doc -> {
						ItemDelegate<Page> pageItem = new ItemDelegate<Page>(
								Page.class.getAnnotation(GenericEntity.class).name(), doc,
								Page.class.getAnnotation(GenericEntity.class).name(), doc.getClass());
						Item item = repositoryService.getCustomContainer().addItem(pageItem.getObj());
					});

					repositoryService.getCacheHelper().removeAllCachedItems();
					repositoryService.getCacheHelper().removeAllCachedResults();
					log.debug("Node indexed on Elasticsearch");

				}
			}

		} catch (Exception e) {
			log.error("Index: ", e);
		}
	}
}
