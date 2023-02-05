package com.whitelabel.app.generic.service;

import javax.inject.Inject;

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
import lombok.Data;

@Data
public class RepositoryServiceImpl implements RepositoryService {

	/** The search. */
	private Listener search;

	/** The sub app event bus. */
	private EventBus subAppEventBus;

	/** The content connector. */

	private CustomContentConnector contentConnector;

	@Inject
	private FactoryContainer factoryContainer;

	private EventBus adminCentralEventBus;

	private GenericContentConnectorDefinition definition;

	private FactoryContainer factory;

	@Inject
	private CacheHelper cacheHelper;
	@Inject
	private S3Delegate s3Delegate;
	@Inject
	private ElasticSearchDelegate elasticSearchDelegate;

	@Inject
	CustomContainer customContainer;

	@Inject
	private FactoryConverter factoryConverter;

	@Inject
	private LogService logService;

	private UiService uiService;

	private GenericClassConverter converterClass;

	public RepositoryServiceImpl() {
		this.uiService = new UiServiceImpl(this);
		this.converterClass = new GenericClassConverter(this);
	}

}
