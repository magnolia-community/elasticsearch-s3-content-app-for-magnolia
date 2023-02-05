/*
 *
 */
package com.whitelabel.app.generic.ui.service;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.whitelabel.app.custom.interfaces.CustomListenerPresenter;
import com.whitelabel.app.generic.ui.dialog.ActionBarService;

import info.magnolia.ui.api.app.AppContext;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.api.location.Location;
import info.magnolia.ui.contentapp.browser.BrowserLocation;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Manage browser functions for restore view different regarding url in browser.
 */
@AllArgsConstructor
@Data
public class BrowserManager {

	/** The location. */
	private Location location;

	/** The action bar service. */
	private ActionBarService actionBarService;

	/** The content connector. */
	private ContentConnector contentConnector;

	/** The sub app context. */
	private SubAppContext subAppContext;

	/** The app context. */
	private AppContext appContext;

	/** The browser. */
	private CustomListenerPresenter browser;

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(BrowserManager.class);

	/**
	 * Restore browser.
	 *
	 * @param location the location
	 */
	public void restoreBrowser(BrowserLocation location) {
		String path = location.getNodePath();
		String viewType = location.getViewType();

		if (!browser.hasViewType(viewType)) {
			if (!StringUtils.isBlank(viewType)) {
				log.warn("Unknown view type [{}], returning to default view type.", viewType);
			}
			viewType = browser.getDefaultViewType();
			location.updateViewType(viewType);
			appContext.updateSubAppLocation(subAppContext, location);
		}
		String query = location.getQuery();

		Object itemId = contentConnector.getItemIdByUrlFragment(path);

		if (!contentConnector.canHandleItem(itemId)) {
			itemId = contentConnector.getDefaultItemId();
			BrowserLocation newLocation = getCurrentLocation();
			newLocation.updateNodePath("/");
			appContext.updateSubAppLocation(subAppContext, newLocation);
		}

		browser.resync(Arrays.asList(itemId), viewType, query);
		actionBarService.updateActionbar(browser.getActionbarPresenter());
	}

	/**
	 * Gets the current location.
	 *
	 * @return the current location
	 */
	public BrowserLocation getCurrentLocation() {
		return BrowserLocation.wrap(subAppContext.getLocation());
	}
}
