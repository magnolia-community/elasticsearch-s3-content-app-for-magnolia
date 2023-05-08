/*
 *
 */
package info.magnolia.forge.universalcontent.app.custom.interfaces;

import com.vaadin.ui.Component;

import info.magnolia.forge.universalcontent.app.generic.search.Params;
import info.magnolia.ui.api.app.SubApp;
import info.magnolia.ui.api.view.View;

/**
 * Interface define method to implement for app Defined two column structure and
 * getter for return View components.
 */
public interface CustomTwoColumnView extends View {

	/**
	 * The Interface Listener.
	 */
	public interface Listener extends SubApp {

		/**
		 * On search configuration.
		 *
		 * @param params the params
		 */
		void onSearchConfiguration(Params params);
	}

	/**
	 * Sets the listener.
	 *
	 * @param listener the new listener
	 */
	void setListener(Listener listener);

	/**
	 * Sets the left view.
	 *
	 * @param left the new left view
	 */
	void setLeftView(View left);

	/**
	 * Sets the right view.
	 *
	 * @param right the new right view
	 */
	void setRightView(View right);

	/**
	 * Gets the left view.
	 *
	 * @return the left view
	 */
	Component getLeftView();

	/**
	 * Gets the right view.
	 *
	 * @return the right view
	 */
	Component getRightView();

}
