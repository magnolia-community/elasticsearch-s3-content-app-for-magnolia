/*
 *
 */
package com.whitelabel.app.generic.ui.service;

import java.util.function.Consumer;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Container;
import com.whitelabel.app.custom.interfaces.CustomTwoColumnView;
import com.whitelabel.app.custom.interfaces.Listener;
import com.whitelabel.app.generic.connector.FactoryContainer;
import com.whitelabel.app.generic.search.Params;
import com.whitelabel.app.generic.ui.CustomFieldFilter;

/**
 * UI events management interface Click/select method when events occurs.
 */
public interface UiEventAction {

	/**
	 * Click on search.
	 *
	 * @return the runnable
	 */
	public Runnable clickOnSearch();

	/**
	 * Gets the search params.
	 *
	 * @return the search params
	 */
	public Params fillParamsItem();

	/**
	 * Click on add.
	 *
	 * @return the runnable
	 */
	public Runnable clickOnAdd();

	/**
	 * Select index.
	 *
	 * @return the consumer
	 */
	public Consumer<CustomFieldFilter> selectGenericItem();

	/**
	 * Gets the layout.
	 *
	 * @return the layout
	 */
	public VerticalLayout getLayout();

	/**
	 * Gets the listener.
	 *
	 * @return the listener
	 */
	public Listener getListener();

	/**
	 * Gets the elastic search container.
	 *
	 * @return the elastic search container
	 */
	public Container getCustomContainer();

	/**
	 * Sets the layout.
	 *
	 * @param layout the new layout
	 */
	public void setLayout(VerticalLayout layout);

	/**
	 * Sets the listener.
	 *
	 * @param listener the new listener
	 */
	public void setListener(Listener listener);

	/**
	 * Sets the elastic search container.
	 *
	 * @param elasticSearchContainer the new elastic search container
	 */
	public void setCustomContainer(Container elasticSearchContainer);

	/**
	 * Sets the factory elastic search.
	 *
	 * @param factory the new factory elastic search
	 */
	public void setFactoryContainer(FactoryContainer factory);

	/**
	 * Sets the product browser view.
	 *
	 * @param productBrowserView the new product browser view
	 */
	public void setProductBrowserView(CustomTwoColumnView productBrowserView);

	/**
	 * Click on change num page.
	 *
	 * @return the consumer
	 */
	public Consumer<CustomFieldFilter> clickOnChangeNumPage();

	public Consumer<CustomFieldFilter> selectSource();

	public Runnable clickOnSourceConnect();
}
