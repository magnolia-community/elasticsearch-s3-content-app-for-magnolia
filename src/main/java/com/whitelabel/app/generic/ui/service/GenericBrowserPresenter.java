/*
 *
 */
package com.whitelabel.app.generic.ui.service;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import com.whitelabel.app.custom.interfaces.CustomListenerPresenter;
import com.whitelabel.app.generic.search.Params;
import com.whitelabel.app.generic.service.RepositoryService;
import com.whitelabel.app.generic.ui.OptionsCustomUi;
import com.whitelabel.app.generic.utils.GenericConstants;

import info.magnolia.ui.actionbar.ActionbarPresenter;
import info.magnolia.ui.actionbar.ActionbarView;
import info.magnolia.ui.contentapp.browser.BrowserPresenter;
import info.magnolia.ui.contentapp.browser.BrowserSubAppDescriptor;
import info.magnolia.ui.contentapp.browser.BrowserView;
import info.magnolia.ui.workbench.WorkbenchView;

/**
 * Manage Presentation Layer for Content Connector app
 */
public class GenericBrowserPresenter implements CustomListenerPresenter {

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(BrowserPresenter.class);

	/** The view. */
	private final BrowserView view;

	/** The params. */
	private Params params;

	@Inject
	private RepositoryService serviceContainer;

	/**
	 * Instantiates a new log browser presenter.
	 *
	 * @param view the view
	 */
	@Inject
	public GenericBrowserPresenter(BrowserView view) {
		this.view = view;
	}

	/**
	 * Start.
	 *
	 * @return the browser view
	 */
	@Override
	public BrowserView start() {
		serviceContainer.getUiService().getActionbarPresenter().setListener(this);
		if (GenericBrowserViewImpl.hasColumns(
				((BrowserSubAppDescriptor) serviceContainer.getUiService().getSubAppContext().getSubAppDescriptor())
						.getWorkbench())) {
			WorkbenchView workbenchView = serviceContainer.getUiService().getWorkbenchPresenter().start(
					((BrowserSubAppDescriptor) serviceContainer.getUiService().getSubAppContext().getSubAppDescriptor())
							.getWorkbench(),
					((BrowserSubAppDescriptor) serviceContainer.getUiService().getSubAppContext().getSubAppDescriptor())
							.getImageProvider(),
					serviceContainer.getSubAppEventBus());
			if (((BrowserSubAppDescriptor) serviceContainer.getUiService().getSubAppContext().getSubAppDescriptor())
					.getActions() != null
					&& !((BrowserSubAppDescriptor) serviceContainer.getUiService().getSubAppContext()
							.getSubAppDescriptor()).getActions().isEmpty()) {
				ActionbarView actionbar = serviceContainer.getUiService().getActionbarPresenter()
						.start(((BrowserSubAppDescriptor) serviceContainer.getUiService().getSubAppContext()
								.getSubAppDescriptor()).getActionbar(),
								((BrowserSubAppDescriptor) serviceContainer.getUiService().getSubAppContext()
										.getSubAppDescriptor()).getActions());
				view.setActionbarView(actionbar);

			}
			view.setWorkbenchView(workbenchView);
			view.setListener(this);

			bindHandlers();
		}
		return view;
	}

	/**
	 * Bind handlers.
	 */
	protected void bindHandlers() {

		serviceContainer.getUiService().getContentEventManager().handlerSelectionContentEvent();
		serviceContainer.getUiService().getContentEventManager().handlerItemDoubleClickedEvent();
		serviceContainer.getUiService().getContentEventManager().handlerSearchEvent();
		serviceContainer.getUiService().getContentEventManager().handlerLogSearchEvent();
		serviceContainer.getUiService().getContentEventManager().handlerAddItemEvent();

		serviceContainer.getUiService().getContentEventManager().handlerItemShortcutKeyEvent();
	}

	/**
	 * Sets the value in component.
	 *
	 * @param iterator the iterator
	 * @param value    the value
	 * @param cont     the cont
	 * @return the int
	 */
	private int setValueInComponent(Iterator<Component> iterator, String value, int cont) {
		Component component = iterator.next();
		OptionsCustomUi options = OptionsCustomUi.convert(component);
		if (options != null && GenericConstants.GROUP_FILTER_ID.equals(options.getGroup())) {
			if (component instanceof TextField) {
				((TextField) component).setValue(value);
			}
			cont++;
		}
		return cont;
	}

	/**
	 * Verify item exists.
	 *
	 * @param itemId the item id
	 * @return true, if successful
	 */
	protected boolean verifyItemExists(Object itemId) {
		return serviceContainer.getContentConnector().canHandleItem(itemId)
				&& serviceContainer.getContentConnector().getItem(itemId) != null;
	}

	/**
	 * Gets the selected item ids.
	 *
	 * @return the selected item ids
	 */
	@Override
	public List<Object> getSelectedItemIds() {
		return serviceContainer.getUiService().getWorkbenchPresenter().getSelectedIds();
	}

	/**
	 * Gets the default view type.
	 *
	 * @return The configured default view Type.<br>
	 *         If non define, return the first Content Definition as default.
	 */
	@Override
	public String getDefaultViewType() {
		return serviceContainer.getUiService().getWorkbenchPresenter().getDefaultViewType();
	}

	/**
	 * Checks for view type.
	 *
	 * @param viewType the view type
	 * @return true, if successful
	 */
	@Override
	public boolean hasViewType(String viewType) {
		return serviceContainer.getUiService().getWorkbenchPresenter().hasViewType(viewType);
	}

	/**
	 * Gets the view.
	 *
	 * @return the view
	 */
	@Override
	public BrowserView getView() {
		return view;
	}

	/**
	 * Gets the actionbar presenter.
	 *
	 * @return the actionbar presenter
	 */
	@Override
	public ActionbarPresenter getActionbarPresenter() {
		return serviceContainer.getUiService().getActionbarPresenter();
	}

	/**
	 * Resync.
	 *
	 * @param itemIds  the item ids
	 * @param viewType the view type
	 * @param query    the query
	 */
	@Override
	public void resync(final List<Object> itemIds, final String viewType, final String query) {
		serviceContainer.getUiService().getWorkbenchPresenter().resynch(itemIds, viewType, query);
	}

	/**
	 * On actionbar item clicked.
	 *
	 * @param actionName the action name
	 */
	@Override
	public void onActionbarItemClicked(String actionName) {
		serviceContainer.getUiService().getActionBarService().executeAction(actionName);
	}

	/**
	 * On action bar selection.
	 *
	 * @param actionName the action name
	 */
	@Override
	public void onActionBarSelection(String actionName) {
		serviceContainer.getUiService().getActionBarService().executeAction(actionName);
	}

	/**
	 * Sets the search params.
	 *
	 * @param params the new search params
	 */
	@Override
	public void setSearchParams(Params params) {
		this.params = params;
	}

	/**
	 * Gets the search params.
	 *
	 * @return the search params
	 */
	public Params getSearchParams() {
		return this.params;
	}

}
