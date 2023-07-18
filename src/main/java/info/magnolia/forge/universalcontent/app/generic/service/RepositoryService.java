package info.magnolia.forge.universalcontent.app.generic.service;

import info.magnolia.event.EventBus;
import info.magnolia.forge.universalcontent.app.aws.s3.S3Delegate;
import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomContainer;
import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomContentConnector;
import info.magnolia.forge.universalcontent.app.custom.interfaces.Listener;
import info.magnolia.forge.universalcontent.app.elasticsearch.ElasticSearchDelegate8;
import info.magnolia.forge.universalcontent.app.elasticsearch.configuration.ElasticsearchConfiguration;
import info.magnolia.forge.universalcontent.app.generic.connector.FactoryContainer;
import info.magnolia.forge.universalcontent.app.generic.others.CacheHelper;
import info.magnolia.forge.universalcontent.app.generic.ui.service.GenericContentConnectorDefinition;
import info.magnolia.forge.universalcontent.app.generic.utils.FactoryConverter;
import info.magnolia.forge.universalcontent.app.generic.utils.GenericClassConverter;
import info.magnolia.forge.universalcontent.app.manageES.ElasticSearchModuleCore;

public interface RepositoryService {

	public void setFactoryConverter(FactoryConverter factoryConverter);

	public FactoryConverter getFactoryConverter();

	public void setUiService(UiService uiService);

	public UiService getUiService();

	/** The sub app event bus. */
	public void setSubAppEventBus(EventBus eventBus);

	public FactoryContainer getFactoryContainer();

	/** The search. */
	public void setSearch(Listener listener);

	/** The content connector. */
	public void setContentConnector(CustomContentConnector contentConnector);

	public void setFactoryContainer(FactoryContainer factoryContainer);

	public void setAdminCentralEventBus(EventBus eventBus);

	/** The search. */
	public Listener getSearch();

	/** The sub app event bus. */
	public EventBus getSubAppEventBus();

	/** The content connector. */
	public CustomContentConnector getContentConnector();

	public EventBus getAdminCentralEventBus();

	public void setDefinition(GenericContentConnectorDefinition definition);

	public void setFactory(FactoryContainer factoryContainer);

	public void setCacheHelper(CacheHelper cacheHelper);

	public GenericContentConnectorDefinition getDefinition();

	public CacheHelper getCacheHelper();

	public void setS3Delegate(S3Delegate queryDelegate);

	public S3Delegate getS3Delegate();

	public ElasticSearchDelegate8 getElasticSearchDelegate();

	public void setElasticSearchDelegate(ElasticSearchDelegate8 elasticSearchDelegate);

	public void setCustomContainer(CustomContainer container);

	public CustomContainer getCustomContainer();

	public void setLogService(LogService logService);

	public LogService getLogService();

	public void setConverterClass(GenericClassConverter converterClass);

	public GenericClassConverter getConverterClass();

	public ElasticSearchModuleCore getElasticSearchModule();

	public void setElasticSearchModule(ElasticSearchModuleCore elasticSearchModuleCore);

	<D> void configure(Class typeParameterClass, ElasticsearchConfiguration elasticSearchConfiguration, D delegate,
			ElasticSearchModuleCore elasticSearchModule);

}
