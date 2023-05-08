/*
 *
 */

package info.magnolia.forge.universalcontent.app.generic.ui.service;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.v7.data.Container;

import info.magnolia.event.EventBus;
import info.magnolia.forge.universalcontent.app.custom.interfaces.ListSearchViewAppInterface;
import info.magnolia.forge.universalcontent.app.generic.others.GenericException;
import info.magnolia.forge.universalcontent.app.generic.search.GenericParamsBuilder;
import info.magnolia.forge.universalcontent.app.generic.search.Params;
import info.magnolia.forge.universalcontent.app.generic.service.RepositoryService;
import info.magnolia.forge.universalcontent.app.generic.utils.GenericConstants;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;
import info.magnolia.ui.workbench.definition.WorkbenchDefinition;
import info.magnolia.ui.workbench.list.ListPresenter;
import info.magnolia.ui.workbench.list.ListView;

/**
 * GenericListPresenter is responsible for manage items in List.
 */
public class GenericListPresenter extends ListPresenter {
	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(GenericListPresenter.class);
	@Inject
	private RepositoryService serviceContainer;

	/**
	 * Instantiates a new Generic list presenter.
	 *
	 * @param view              the view
	 * @param componentProvider the component provider
	 */
	@Inject
	public GenericListPresenter(ListSearchViewAppInterface view, ComponentProvider componentProvider) {
		super(view, componentProvider);
	}

	/**
	 * initializeContainer have default IndexClass if there isn't Class defined for
	 * indexClass.
	 *
	 * @return the container
	 */
	@Override
	public Container initializeContainer() {
		Class indexElasticSearchClass = ((GenericListPresenterDefinition) getPresenterDefinition()).getItemClass();
		if (indexElasticSearchClass == null && serviceContainer != null
				&& serviceContainer.getFactoryContainer() != null) {
			indexElasticSearchClass = serviceContainer.getFactoryContainer().getClassType();
		}
		if (indexElasticSearchClass != null) {
			Params params = GenericParamsBuilder.createSearch(serviceContainer)
					.addField(GenericConstants.FILTER_INDEX, indexElasticSearchClass.getCanonicalName()).get();
			try {
				serviceContainer.getCustomContainer().createCustomContainer(params);
			} catch (GenericException e) {
				log.error("InitializeContainer", e);
			}
		}

		return serviceContainer.getCustomContainer();
	}

	/**
	 * Start.
	 *
	 * @param workbenchDefinition the workbench definition
	 * @param eventBus            the event bus
	 * @param viewTypeName        the view type name
	 * @param contentConnector    the content connector
	 * @return the list view
	 */
	@Override
	public ListView start(WorkbenchDefinition workbenchDefinition, EventBus eventBus, String viewTypeName,
			ContentConnector contentConnector) {
		Boolean hasColumn = true;
		if (workbenchDefinition.getContentViews() != null && workbenchDefinition.getContentViews().iterator() != null) {
			if (GenericBrowserViewImpl.hasColumns(workbenchDefinition)) {
				ListView listView = super.start(workbenchDefinition, eventBus, viewTypeName, contentConnector);
				return listView;
			}
		}

		return null;
	}

	/**
	 * Gets the container.
	 *
	 * @return the container
	 */
	public Container getContainer() {
		return serviceContainer.getCustomContainer();
	}

}
