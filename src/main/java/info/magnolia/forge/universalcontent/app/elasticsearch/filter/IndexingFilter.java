package info.magnolia.forge.universalcontent.app.elasticsearch.filter;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import info.magnolia.forge.universalcontent.app.elasticsearch.ElasticSearchDelegate8;
import info.magnolia.forge.universalcontent.app.elasticsearch.service.IIndexingService;
import info.magnolia.forge.universalcontent.app.generic.custom.entity.Page;
import info.magnolia.forge.universalcontent.app.generic.service.RepositoryService;
import info.magnolia.forge.universalcontent.app.manageES.ElasticSearchModuleCore;
import info.magnolia.publishing.Constants;
import info.magnolia.publishing.dispatcher.Dispatcher;
import info.magnolia.publishing.receiver.filter.PublicationFilter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IndexingFilter extends PublicationFilter {
	@Getter
	@Setter
	private String targetExtensions;
	@Getter
	@Setter
	@Inject
	private IIndexingService indexingService;
	@Getter
	@Setter
	@Inject
	private ElasticSearchModuleCore elasticSearchModule;
	@Inject
	private RepositoryService serviceContainer;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);
		serviceContainer.configure(Page.class, elasticSearchModule.getConfiguration(), new ElasticSearchDelegate8(),
				elasticSearchModule);
	}

	@Inject
	public IndexingFilter(Dispatcher dispatcher) {
		super(dispatcher);
	}

	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (isEnabled()) {

			String action = request.getHeader(Constants.Parameters.OPERATION);
			if (Constants.PublicationOperations.PUBLISH.equalsIgnoreCase(action)
					|| Constants.PublicationOperations.COMMIT.equalsIgnoreCase(action)
					|| Constants.PublicationOperations.ROLLBACK.equalsIgnoreCase(action)) {
				try {
					super.doFilter(request, response, chain);
					indexingService.index(request);
				} catch (Exception e) {
					log.error("Exception caught indexing node on Elasticsearch");
				}

			} else if (Constants.PublicationOperations.UNPUBLISH.equalsIgnoreCase(action)) {
				try {
					indexingService.unindex(request);
					super.doFilter(request, response, chain);
				} catch (Exception e) {
					log.error("Exception caught unindexing node on Elasticsearch");
				}
			} else {
				chain.doFilter(request, response);
			}
		} else {

			chain.doFilter(request, response);

		}
	}

}
