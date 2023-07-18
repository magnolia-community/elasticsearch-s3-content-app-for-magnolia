package info.magnolia.forge.universalcontent.elasticsearch.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import info.magnolia.context.MgnlContext;
import info.magnolia.forge.universalcontent.app.generic.utils.MagnoliaConstants;
import info.magnolia.jcr.predicate.NodeTypePredicate;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.jcr.util.SessionUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MagnoliaUtils {

	public static List<Node> getComponents(String workspace, List<Node> areas)
			throws RepositoryException, LoginException, InvalidQueryException {
		List<Node> componentNodes = new ArrayList<Node>();
		if (CollectionUtils.isNotEmpty(areas)) {
			for (Node area : areas) {
				String queryString = "SELECT * FROM [nt:base] AS p WHERE ISDESCENDANTNODE(['" + area.getPath() + "'])";
				queryString += " AND (p.[" + MagnoliaConstants.JCR_PRIMARY_TYPE +"] = '" + NodeTypes.Component.NAME + "') ";

				final Session session = MgnlContext.getJCRSession(workspace);
				final QueryManager manager = session.getWorkspace().getQueryManager();
				final Query query = manager.createQuery(queryString, "JCR-SQL2");
				final QueryResult result = query.execute();
				Iterator<Node> nodeIt = NodeUtil
						.filterDuplicates(NodeUtil.filterParentNodeType(result.getNodes(), NodeTypes.Component.NAME));

				while (nodeIt.hasNext()) {
					final Node item = nodeIt.next();
					componentNodes.add(item);
				}
			}
		}
		return componentNodes;
	}

	public static Node getMgnlNode(String identifier, String workspace) {
		Node node = null;
		if (StringUtils.isNotEmpty(identifier) && StringUtils.isNotEmpty(workspace)) {
			try {
				node = NodeUtil.getNodeByIdentifier(workspace, identifier);
			} catch (RepositoryException e) {
				log.error("Exception caught retrieving node corresponding to index '{}'", identifier, e);
			}
		}
		return node;
	}

	public static String getNodeWorkspace(Node node) {
		String workspace = null;
		if (node != null) {
			Session session;
			try {
				session = node.getSession();
				if (session.getWorkspace() != null) {
					workspace = session.getWorkspace().getName();
				}

			} catch (RepositoryException e) {
				log.error("Exception caught getting workspace for node '{}'", node, e);
			}
		}
		return workspace;
	}

	public static String getNodePath(Node node) {
		String path = null;
		if (node != null) {
			try {
				path = node.getPath();
			} catch (RepositoryException e) {
				log.error("Exception caught getting path for node '{}'", node, e);
			}
		}
		return path;
	}

	public static String getDocumentId(Node node) {
		String id = null;
		if (node != null) {
			String workspace = null;
			String identifier;
			Session session;
			try {
				identifier = node.getIdentifier();
				session = node.getSession();
				if (session.getWorkspace() != null) {
					workspace = session.getWorkspace().getName();
				}
				if (StringUtils.isNotEmpty(workspace) && StringUtils.isNotEmpty(identifier)) {
					return workspace + ElasticsearchConstants.INDEXDOCUMENT_ID_SEPARATOR + identifier;
				}
				return null;
			} catch (Exception e) {
				log.error("Exception caught building Elasticsearch id for node '{}'", node, e);
			}
		}
		return id;
	}

	public static void searchNodes(String path, List<Node> nodeList, String workspace, String predicateNode,
			String predicateFolder) {
		try {
			if (nodeList == null) {
				return;
			}
			Node currNode = SessionUtil.getNode(workspace, path);
			if (currNode != null) {
				Iterable<Node> nodeAssetIterable = NodeUtil.getNodes(currNode, new NodeTypePredicate(predicateNode));
				if (nodeAssetIterable != null) {
					Iterator<Node> iterator = nodeAssetIterable.iterator();
					if (iterator != null && iterator.hasNext()) {
						while (iterator.hasNext()) {
							Node childNode = iterator.next();
							if (childNode != null) {
								nodeList.add(childNode);
								searchNodes(childNode.getPath(), nodeList, workspace, predicateNode, predicateNode);
							}
						}
					}
				}
				if (predicateFolder != null) {
					Iterable<Node> nodeFolderIterable = NodeUtil.getNodes(currNode,
							new NodeTypePredicate(predicateFolder));
					if (nodeFolderIterable != null) {
						Iterator<Node> iterator = nodeFolderIterable.iterator();
						if (iterator != null && iterator.hasNext()) {
							while (iterator.hasNext()) {
								Node childNode = iterator.next();
								nodeList.add(childNode);
								searchNodes(childNode.getPath(), nodeList, workspace, predicateNode, predicateNode);
							}
						}
					}
				}

			}
		} catch (Exception e) {
			log.error("Exception caught searching recursive nodes on website", e.getMessage());
		}
	}

	public static String getTemplate(final Node node) {
		String tpl = "";
		if (node != null) {
			tpl = PropertyUtil.getString(node, MagnoliaConstants.MGNL_TEMPLATE, "");
		}
		return tpl;
	}

}
