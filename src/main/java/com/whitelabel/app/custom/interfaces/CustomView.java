/*
 *
 */
package com.whitelabel.app.custom.interfaces;

import info.magnolia.ui.api.view.View;

/**
 * The Interface CustomView.
 */
public interface CustomView extends View {

	/**
	 * Sets the listener.
	 *
	 * @param listener the new listener
	 */
	void setListener(Listener listener);

}
