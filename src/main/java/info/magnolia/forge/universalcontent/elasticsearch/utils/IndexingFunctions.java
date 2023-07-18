package info.magnolia.forge.universalcontent.elasticsearch.utils;

import java.text.Normalizer;
import java.util.List;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;

import info.magnolia.context.MgnlContext;
import info.magnolia.dam.jcr.DamConstants;
import info.magnolia.dam.templating.functions.DamTemplatingFunctions;
import info.magnolia.forge.universalcontent.app.elasticsearch.connector.ElasticSearchParameterFactory;
import info.magnolia.forge.universalcontent.app.elasticsearch.service.ElasticsearchIndexingServiceImpl;
import info.magnolia.forge.universalcontent.app.elasticsearch.service.IIndexingService;
import info.magnolia.forge.universalcontent.app.generic.entity.GenericItem;
import info.magnolia.forge.universalcontent.app.manageES.ElasticSearchModuleCore;
import info.magnolia.forge.universalcontent.elasticsearch.beans.ElasticSearchFilter;
import info.magnolia.forge.universalcontent.elasticsearch.search.entity.SearchResultItem;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.templating.functions.TemplatingFunctions;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IndexingFunctions {
	@Inject
	private TemplatingFunctions cmsfn;
	@Inject
	private DamTemplatingFunctions damfn;
	@Inject
	private IIndexingService indexingService;
	@Inject
	@Getter
	@Setter
	private ElasticSearchModuleCore elasticSearchModule;

	@Inject
	public IndexingFunctions(final TemplatingFunctions cmsfn, final DamTemplatingFunctions damfn,
			IIndexingService indexingService) {
		this.cmsfn = cmsfn;
		this.damfn = damfn;
		this.indexingService = indexingService;
	}

	public ElasticSearchFilter getSearchFilterBySite(final Node node, String repository) {
		ElasticSearchFilter searchFilter = new ElasticSearchFilter();
		searchFilter.setSite(getSearchTargetSite(node, repository));
		return searchFilter;
	}

	public String getSearchTargetSite(final Node node, String repository) {
		if (node != null && StringUtils.isNotEmpty(repository) && elasticSearchModule.getConfiguration() != null) {
			try {
				String path = node.getPath();

				if (indexingService != null && indexingService instanceof ElasticsearchIndexingServiceImpl) {
					ElasticsearchIndexingServiceImpl elasticsearchIndexingService = (ElasticsearchIndexingServiceImpl) indexingService;
					ElasticSearchParameterFactory elasticSearchClient = elasticsearchIndexingService.getClient();
					if (StringUtils.equals(repository, RepositoryConstants.WEBSITE))
						for (String websitePath : elasticSearchModule.getConfiguration().getWebsitePaths()) {
							if (StringUtils.startsWith(path, websitePath)) {
								return "/" + StringUtils.split(websitePath, "/")[0];
							}

						}
					if (StringUtils.equals(repository, DamConstants.WORKSPACE))
						for (String damPath : elasticSearchModule.getConfiguration().getDamPaths()) {
							if (StringUtils.startsWith(path, damPath)) {
								return "/" + StringUtils.split(damPath, "/")[0];
							}

						}
				}

			} catch (RepositoryException e) {
				log.error("Exception caught target site  for node '{}' for initializing search", node, e);
			}
		}
		return null;
	}

	public String getMgnlNodeIdentifier(GenericItem document) {
		if (document != null) {
			return ElasticsearchUtils.getMgnlNodeIdentifier(document);
		}
		return null;
	}

	public ElasticsearchPagination getPager(final Node content, int total, int maxResultsPerPage,
			List<SearchResultItem> results) throws RepositoryException {

		final String currentPageLink;
		if (MgnlContext.isWebContext() && (MgnlContext.getAggregationState() != null)) {
			currentPageLink = MgnlContext.getAggregationState().getOriginalURL();
		} else {
			currentPageLink = cmsfn.link(MgnlContext.getAggregationState().getMainContentNode());
		}
		return new ElasticsearchPagination(currentPageLink, results, content, maxResultsPerPage);
	}

	public ElasticsearchPagination getPager(final Node content, final List<SearchResultItem> items,
			int maxResultsPerPage) throws RepositoryException {

		final String currentPageLink;
		if (MgnlContext.isWebContext() && (MgnlContext.getAggregationState() != null)) {
			currentPageLink = MgnlContext.getAggregationState().getOriginalURL();
		} else {
			currentPageLink = cmsfn.link(MgnlContext.getAggregationState().getMainContentNode());
		}
		return new ElasticsearchPagination(currentPageLink, items, content, maxResultsPerPage);
	}

	public static String[][] UMLAUT_REPLACEMENTS = { { "Ä", "Ae" }, { "Ü", "Ue" }, { "Ö", "Oe" }, { "ä", "ae" },
			{ "ü", "ue" }, { "ö", "oe" }, { "ß", "ss" } };

	public static String replaceUmlaute(String orig) {
		String result = orig;

		for (int i = 0; i < UMLAUT_REPLACEMENTS.length; i++) {
			result = result.replaceAll(UMLAUT_REPLACEMENTS[i][0], UMLAUT_REPLACEMENTS[i][1]);
		}

		return result;
	}

	public String normalizeSlug(String name) {
		String slug = name;
		if (org.apache.commons.lang3.StringUtils.isNotBlank(name)) {
			slug = org.apache.commons.lang3.StringUtils.lowerCase(name);
			slug = Normalizer.normalize(slug, Normalizer.Form.NFKC);// NFD NFKC
			slug = replaceUmlaute(slug);
			slug = org.apache.commons.lang3.StringUtils.stripAccents(slug);
			slug = slug.replaceAll("[^\\x00-\\x7F]", "");
			slug = slug.replaceAll("\\W", "-");
			slug = slug.replaceAll("-([^?=.*])-{1,100}", "-");
			slug = slug.replaceAll("--", "-");
		}
		return slug;
	}

}
