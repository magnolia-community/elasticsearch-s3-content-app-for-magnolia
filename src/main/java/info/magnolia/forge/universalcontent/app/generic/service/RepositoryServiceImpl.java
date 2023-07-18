package info.magnolia.forge.universalcontent.app.generic.service;

import java.util.Optional;

import javax.inject.Inject;

import info.magnolia.event.EventBus;
import info.magnolia.forge.universalcontent.app.aws.s3.S3Delegate;
import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomContainer;
import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomContentConnector;
import info.magnolia.forge.universalcontent.app.custom.interfaces.Listener;
import info.magnolia.forge.universalcontent.app.elasticsearch.ElasticSearchDelegate8;
import info.magnolia.forge.universalcontent.app.elasticsearch.configuration.ElasticsearchConfiguration;
import info.magnolia.forge.universalcontent.app.elasticsearch.connector.ElasticSearchConnection;
import info.magnolia.forge.universalcontent.app.generic.annotation.DelegateImplementation;
import info.magnolia.forge.universalcontent.app.generic.connection.ElasticSearchParameterConnection;
import info.magnolia.forge.universalcontent.app.generic.connection.ParameterConnection;
import info.magnolia.forge.universalcontent.app.generic.connector.FactoryContainer;
import info.magnolia.forge.universalcontent.app.generic.connector.GenericDelegate;
import info.magnolia.forge.universalcontent.app.generic.entity.GenericItem;
import info.magnolia.forge.universalcontent.app.generic.others.CacheHelper;
import info.magnolia.forge.universalcontent.app.generic.search.GenericParamsBuilder;
import info.magnolia.forge.universalcontent.app.generic.search.Params;
import info.magnolia.forge.universalcontent.app.generic.ui.service.GenericContentConnectorDefinition;
import info.magnolia.forge.universalcontent.app.generic.utils.FactoryConverter;
import info.magnolia.forge.universalcontent.app.generic.utils.GenericClassConverter;
import info.magnolia.forge.universalcontent.app.generic.utils.GenericConstants;
import info.magnolia.forge.universalcontent.app.manageES.ElasticSearchModuleCore;
import info.magnolia.objectfactory.ComponentProvider;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class RepositoryServiceImpl implements RepositoryService {

	/** The search. */
	private Listener search;

	/** The sub app event bus. */
	private EventBus subAppEventBus;

	/** The content connector. */
	@Inject
	private CustomContentConnector contentConnector;

	@Inject
	private FactoryContainer factoryContainer;

	private EventBus adminCentralEventBus;

	private GenericContentConnectorDefinition definition;

	private FactoryContainer factory;

//	@Inject
	private CacheHelper cacheHelper;
	@Inject
	private S3Delegate s3Delegate;
	@Inject
	private ElasticSearchDelegate8 elasticSearchDelegate;

	@Inject
	CustomContainer customContainer;

	@Inject
	private FactoryConverter factoryConverter;

	@Inject
	private LogService logService;

	private UiService uiService;

	private GenericClassConverter converterClass;

	private ElasticSearchModuleCore elasticSearchModule;
	@Inject
	private ElasticSearchConnection connection;
	@Inject
	private ComponentProvider componentProvider;

	@Inject
	public RepositoryServiceImpl() {
		log.info("Repository initialiaze");
		this.uiService = new UiServiceImpl(this);
		this.converterClass = new GenericClassConverter(this);
	}

	@Override
	public <D> void configure(Class typeParameterClass, ElasticsearchConfiguration elasticSearchConfiguration,
			D delegate, ElasticSearchModuleCore elasticSearchModule) {
		this.elasticSearchModule = elasticSearchModule;
		this.uiService = new UiServiceImpl(this);
		this.converterClass = new GenericClassConverter(this);
		try {
			this.setContentConnector(contentConnector);
			this.cacheHelper = new CacheHelper();
			GenericParamsBuilder genericParamsBuilder = new GenericParamsBuilder(this);
			Params params = genericParamsBuilder.createSearch(this)
					.addField(GenericConstants.FILTER_INDEX, typeParameterClass.getCanonicalName()).get();
			params.setClassType(typeParameterClass);
			params.getFields().put((String) params.getFields().get(GenericConstants.SELECT_SOURCE),
					delegate.getClass().getName());
			elasticSearchDelegate = (ElasticSearchDelegate8) setDelegateToUse(params);

			GenericClassConverter genericClassConverter = new GenericClassConverter(this);
			this.setConverterClass(genericClassConverter);
			this.setElasticSearchDelegate(elasticSearchDelegate);
			this.getElasticSearchDelegate().setServiceContainer(this);
			FactoryConverter factoryConverter = new FactoryConverter(this);
			this.setFactoryConverter(factoryConverter);
			this.setLogService(logService);
			factoryContainer.getClassTypeContainer(typeParameterClass);
			this.setFactory(factoryContainer);
			contentConnector.setQueryDelegate(elasticSearchDelegate);
			CustomContentConnector c = contentConnector;
			c.setQueryDelegate(elasticSearchDelegate);
			Optional<CustomContentConnector> o = Optional.of(c);
			this.getCustomContainer().setContentConnector(o);
			if (contentConnector.getQueryDelegate() != null) {
				contentConnector.getQueryDelegate().setServiceContainer(this);
			}

			connection.setParams(new ElasticSearchParameterConnection("localhost", "9200"));
			connection.connection(connection.getParams());

			elasticSearchDelegate.setTypeParameterClass(typeParameterClass);
			elasticSearchDelegate.setConnection(connection);
			contentConnector.setQueryDelegate(elasticSearchDelegate);
			contentConnector.setTypeClass(typeParameterClass);
			this.setContentConnector(contentConnector);

			this.getCustomContainer().getContentConnector().get().setQueryDelegate(elasticSearchDelegate);
		} catch (Exception e) {
			log.error("Error configuration repositoryService", e);
		}
	}

	public GenericDelegate setDelegateToUse(Params params) {
		// TODO like Source selected
		Class<? extends GenericDelegate> classType = this.getConverterClass().getClassItem(params,
				params.getFields().get(params.getFields().get(GenericConstants.SELECT_SOURCE)), GenericDelegate.class);
		GenericDelegate delegate = getComponentProvider().getComponent(classType);
		Class classParameterConnection = delegate.getClass().getAnnotation(DelegateImplementation.class)
				.parameterClass();
		GenericItem createInstanceFromClassAndValues;
		try {
			ParameterConnection p = null;
			if (delegate.getConnection() != null) {
				p = delegate.getConnection().getParams();
			}
			ParameterConnection parameterConnection = this.getConverterClass()
					.createInstanceFromClassAndValues(classParameterConnection, params, p);
			delegate.getConnection().setElasticSearchModule(this.getElasticSearchModule());
			delegate.getConnection().setParams(parameterConnection);
			delegate.getConnection().connect();
			customContainer.getContentConnector().get().setQueryDelegate(delegate);
		} catch (Exception e) {
			log.error("Click on source connect", e);
			delegate.setConnection(null);
		}
		return delegate;
	}

}
