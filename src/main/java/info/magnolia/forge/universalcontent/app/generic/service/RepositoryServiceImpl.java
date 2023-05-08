package info.magnolia.forge.universalcontent.app.generic.service;

import javax.inject.Inject;

import info.magnolia.event.EventBus;
import info.magnolia.forge.universalcontent.app.aws.s3.S3Delegate;
import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomContainer;
import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomContentConnector;
import info.magnolia.forge.universalcontent.app.custom.interfaces.Listener;
import info.magnolia.forge.universalcontent.app.elasticsearch.ElasticSearchDelegate;
import info.magnolia.forge.universalcontent.app.generic.connector.FactoryContainer;
import info.magnolia.forge.universalcontent.app.generic.others.CacheHelper;
import info.magnolia.forge.universalcontent.app.generic.ui.service.GenericContentConnectorDefinition;
import info.magnolia.forge.universalcontent.app.generic.utils.FactoryConverter;
import info.magnolia.forge.universalcontent.app.generic.utils.GenericClassConverter;
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
