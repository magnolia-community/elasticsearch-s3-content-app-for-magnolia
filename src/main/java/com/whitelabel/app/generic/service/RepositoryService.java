package com.whitelabel.app.generic.service;

import com.whitelabel.app.aws.s3.S3Delegate;
import com.whitelabel.app.custom.interfaces.CustomContainer;
import com.whitelabel.app.custom.interfaces.CustomContentConnector;
import com.whitelabel.app.custom.interfaces.Listener;
import com.whitelabel.app.elasticsearch.ElasticSearchDelegate;
import com.whitelabel.app.generic.connector.FactoryContainer;
import com.whitelabel.app.generic.others.CacheHelper;
import com.whitelabel.app.generic.ui.service.GenericContentConnectorDefinition;
import com.whitelabel.app.generic.utils.FactoryConverter;
import com.whitelabel.app.generic.utils.GenericClassConverter;

import info.magnolia.event.EventBus;

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

	public ElasticSearchDelegate getElasticSearchDelegate();

	public void setElasticSearchDelegate(ElasticSearchDelegate elasticSearchDelegate);

	public void setCustomContainer(CustomContainer container);

	public CustomContainer getCustomContainer();

	public void setLogService(LogService logService);

	public LogService getLogService();

	public void setConverterClass(GenericClassConverter converterClass);

	public GenericClassConverter getConverterClass();

}
