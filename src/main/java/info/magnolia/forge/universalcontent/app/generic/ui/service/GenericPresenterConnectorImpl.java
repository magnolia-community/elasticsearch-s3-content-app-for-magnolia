/*
 *
 */

package info.magnolia.forge.universalcontent.app.generic.ui.service;

import javax.inject.Inject;

import com.vaadin.v7.data.Container;

import info.magnolia.forge.universalcontent.app.custom.interfaces.ListSearchViewAppInterface;
import info.magnolia.forge.universalcontent.app.generic.connector.GenericContainer;
import info.magnolia.forge.universalcontent.app.generic.connector.GenericPresenterConnectorDefinition;
import info.magnolia.forge.universalcontent.app.generic.others.GenericException;
import info.magnolia.forge.universalcontent.app.generic.others.LogStatus;
import info.magnolia.forge.universalcontent.app.generic.service.RepositoryService;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.workbench.search.SearchPresenter;

/**
 * The ElasticSearchPresenterConnectorImpl is responsible for create generic
 * container
 */
public class GenericPresenterConnectorImpl extends SearchPresenter {

	@Inject
	private RepositoryService serviceContainer;

	/** The view. */
	private ListSearchViewAppInterface view;

	/**
	 * Instantiates a new elastic search presenter connector impl.
	 *
	 * @param view              the view
	 * @param componentProvider the component provider
	 */
	@Inject
	public GenericPresenterConnectorImpl(ListSearchViewAppInterface view, ComponentProvider componentProvider) {
		super(view, componentProvider);
		this.view = view;
	}

	/**
	 * Search.
	 *
	 * @param fulltextExpr the fulltext expr
	 */
	@Override
	public void search(String fulltextExpr) {
		((GenericContainer) container)
				.setParams(serviceContainer.getUiService().getFactoryConverter().convert(fulltextExpr));
		refresh();
	}

	/**
	 * Clear.
	 */
	@Override
	public void clear() {
		refresh();
	}

	/**
	 * Initialize container.
	 *
	 * @return the container
	 */
	@Override
	protected Container initializeContainer() {
		Class indexElasticSearchClass = ((GenericPresenterConnectorDefinition) getPresenterDefinition())
				.getIndexElasticSearch();
		try {
			if (serviceContainer != null && serviceContainer.getFactoryContainer() != null) {
				serviceContainer.getFactoryContainer().create(indexElasticSearchClass,
						serviceContainer.getCustomContainer());
			}
		} catch (GenericException e) {
			serviceContainer.getLogService().logger(LogStatus.ERROR, "InitializeContainer:", this.getClass(), e);
		}

		return serviceContainer.getCustomContainer();
	}

	/**
	 * Gets the view.
	 *
	 * @return the view
	 */
	public ListSearchViewAppInterface getView() {
		return this.view;
	}

}
