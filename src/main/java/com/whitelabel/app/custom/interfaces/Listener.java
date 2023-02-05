/*
 *
 */
package com.whitelabel.app.custom.interfaces;

import com.whitelabel.app.event.AddItem;
import com.whitelabel.app.generic.search.Params;

import info.magnolia.ui.api.view.View;

/**
 * The Interface Listener.
 */
public interface Listener {

	/**
	 * On search configuration.
	 *
	 * @param params the params
	 */
	void onSearchConfiguration(Params params);

	/**
	 * Adds the item.
	 *
	 * @param item         the item
	 * @param searchParams the search params
	 */
	void addItem(AddItem item, Params searchParams);

	/**
	 * Edits the item.
	 */
	void editItem();

	/**
	 * Delete item.
	 */
	void deleteItem();

	/**
	 * Start.
	 *
	 * @return the view
	 */
	public View start();

	void refreshCache(Params params);
}
