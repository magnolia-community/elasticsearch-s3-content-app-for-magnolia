/*
 *
 */
package info.magnolia.forge.universalcontent.app.manageES;

import com.google.inject.AbstractModule;

import info.magnolia.forge.universalcontent.app.elasticsearch.configuration.ElasticsearchConfiguration;
import info.magnolia.forge.universalcontent.app.generic.others.CacheHelper;
import info.magnolia.forge.universalcontent.app.generic.service.RepositoryService;
import info.magnolia.forge.universalcontent.app.generic.service.RepositoryServiceImpl;
import info.magnolia.module.ModuleLifecycle;
import info.magnolia.module.ModuleLifecycleContext;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Module class for source content connector app
 */
@Slf4j
public class ElasticSearchModuleCore extends AbstractModule implements ModuleLifecycle {
	private ElasticsearchConfiguration configuration;
//	@Inject
//	private LogService logService;
	@Getter
	@Setter
	private RepositoryService repositoryService;
	@Getter
	@Setter
	private CacheHelper cacheHelper;

	public ElasticsearchConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(ElasticsearchConfiguration configuration) {
		this.configuration = configuration;
	}

	public ElasticSearchModuleCore() {
	}

	/**
	 * Start.
	 *
	 * @param moduleLifecycleContext the module lifecycle context
	 */
	@Override
	public void start(ModuleLifecycleContext moduleLifecycleContext) {
		log.info("Start Module: ");
		log.info("Start Module: ");
		log.info("ElasticSearchModule START:");
		cacheHelper = new CacheHelper();
//		serviceContainer.setElasticSearchModule(this);
//		if (((CustomContentConnector) contentConnector).getQueryDelegate() != null) {
//			((CustomContentConnector) contentConnector).getQueryDelegate().setServiceContainer(serviceContainer);
//		}
//		serviceContainer.getS3Delegate().setServiceContainer(serviceContainer);
//		serviceContainer.getElasticSearchDelegate().setServiceContainer(serviceContainer);
//		CustomContentConnector c = (GenericContentConnector) contentConnector;
//		Optional<CustomContentConnector> o = Optional.of(c);
//		serviceContainer.setContentConnector((CustomContentConnector) contentConnector);
//		serviceContainer.getCustomContainer().setContentConnector(o);
	}

	/**
	 * Stop.
	 *
	 * @param moduleLifecycleContext the module lifecycle context
	 */
	@Override
	public void stop(ModuleLifecycleContext moduleLifecycleContext) {
		log.info("Stop Module: ");
	}

	@Override
	protected void configure() {
		bind(RepositoryService.class).to(RepositoryServiceImpl.class);
	}

}
