/*
 *
 */
package com.whitelabel.app.generic.ui.dialog;

import info.magnolia.ui.vaadin.actionbar.ActionPopup;

/**
 * Define which are action you can configure in yaml file.
 *
 * @implSpec PopupServiceImpl
 */
public interface PopupService {

	/**
	 * Show action popup.
	 *
	 * @param itemId the item id
	 * @param x      the x
	 * @param y      the y
	 */
	public void showActionPopup(Object itemId, int x, int y);

	/**
	 * Update action popup.
	 *
	 * @param actionPopup the action popup
	 */
	public void updateActionPopup(ActionPopup actionPopup);

}
