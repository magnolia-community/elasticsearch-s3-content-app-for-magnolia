/*
 *
 */
package com.whitelabel.app.event.service;

import com.whitelabel.app.generic.service.RepositoryService;

/**
 * Interface for manage UI event
 *
 * @implSpec ContentEventManagerImpl
 */
public interface ContentEventManager {

	/**
	 * Handler selection content event.
	 */
	public void handlerSelectionContentEvent();

	/**
	 * Handler selection changed event.
	 */
	public void handlerSelectionChangedEvent();

	/**
	 * Handler item double clicked event.
	 */
	public void handlerItemDoubleClickedEvent();

	/**
	 * Handler search event.
	 */
	public void handlerSearchEvent();

	/**
	 * Handler item shortcut key event.
	 */
	public void handlerItemShortcutKeyEvent();

	/**
	 * Handler action event.
	 */
	public void handlerActionEvent();

	/**
	 * Handler add item event.
	 */
	public void handlerAddItemEvent();

	/**
	 * Handler add item event.
	 */
	public void handlerRefreshCacheEvent();

	/**
	 * Handler log search event.
	 */
	public void handlerLogSearchEvent();

	/**
	 * Handler view type changed event.
	 */
	public void handlerViewTypeChangedEvent();

	/**
	 * Handler item right clicked event.
	 */
	public void handlerItemRightClickedEvent();

	/**
	 * Adds the handler location changed event.
	 */
	public void addHandlerLocationChangedEvent();

	public void setServices(RepositoryService services);

	public RepositoryService getServices();
}
