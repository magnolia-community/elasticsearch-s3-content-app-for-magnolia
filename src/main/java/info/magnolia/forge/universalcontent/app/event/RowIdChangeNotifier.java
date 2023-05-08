/*
 *
 */
package info.magnolia.forge.universalcontent.app.event;

import java.io.Serializable;

/**
 * Notifier for rowIdChange Event.
 *
 * @author net.seniorsoftwareengineer
 */
public interface RowIdChangeNotifier extends Serializable {

	/**
	 * Adds the row id change listener.
	 *
	 * @param listener the listener
	 */
	public void addRowIdChangeListener(RowIdChangeListener listener);

	/**
	 * Adds the listener.
	 *
	 * @param listener the listener
	 */
	public void addListener(RowIdChangeListener listener);

	/**
	 * Removes the row id change listener.
	 *
	 * @param listener the listener
	 */
	public void removeRowIdChangeListener(RowIdChangeListener listener);

	/**
	 * Removes the listener.
	 *
	 * @param listener the listener
	 */
	public void removeListener(RowIdChangeListener listener);

}
