/*
 *
 */
package info.magnolia.forge.universalcontent.app.custom.interfaces;

import javax.inject.Inject;
import javax.inject.Named;

import info.magnolia.context.MgnlContext;
import info.magnolia.event.EventBus;
import info.magnolia.forge.universalcontent.app.event.AddItem;
import info.magnolia.forge.universalcontent.app.event.AddItemEvent;
import info.magnolia.forge.universalcontent.app.event.GenericSearchEvent;
import info.magnolia.forge.universalcontent.app.generic.search.Params;
import info.magnolia.forge.universalcontent.app.generic.utils.GenericConstants;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.api.app.SubAppEventBus;

/**
 * Manage all listener concern by Search, Add Item and delete in index ES.
 */
public class CustomSenderEventManager implements Listener {

	/** The app event bus. */
	private EventBus appEventBus;

	/** The view. */
	private CustomView view;

	/** The sub app context. */
	private SubAppContext subAppContext;

	/**
	 * Instantiates a new custom sender event manager.
	 *
	 * @param appEventBus   the app event bus
	 * @param view          the view
	 * @param subAppContext the sub app context
	 */
	@Inject
	public CustomSenderEventManager(@Named(SubAppEventBus.NAME) EventBus appEventBus, CustomView view,
			SubAppContext subAppContext) {
		this.appEventBus = appEventBus;
		this.view = view;
		this.subAppContext = subAppContext;
	}

	public CustomSenderEventManager() {
	}

	/**
	 * Start.
	 *
	 * @return the custom view
	 */
	@Override
	public CustomView start() {
		view.setListener(this);
		return view;
	}

	/**
	 * On search configuration.
	 *
	 * @param params the params
	 */
	@Override
	public void onSearchConfiguration(Params params) {
		MgnlContext.getWebContext().getRequest().getSession().setAttribute(GenericConstants.SEARCH_PARAMS, params);
		appEventBus.fireEvent(new GenericSearchEvent(params));
	}

	/**
	 * Adds the item.
	 *
	 * @param item         the item
	 * @param searchParams the search params
	 */
	@Override
	public void addItem(AddItem item, Params searchParams) {
		appEventBus.fireEvent(new AddItemEvent(item, searchParams));

	}

	/**
	 * Edits the item.
	 */
	@Override
	public void editItem() {
	}

	/**
	 * Delete item.
	 */
	@Override
	public void deleteItem() {
	}

	@Override
	public void refreshCache(Params searchParams) {
		appEventBus.fireEvent(new RefreshCacheEvent(searchParams));
	}

}
