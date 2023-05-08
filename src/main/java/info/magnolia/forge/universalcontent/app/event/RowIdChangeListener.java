/*
 *
 */
package info.magnolia.forge.universalcontent.app.event;

import java.io.Serializable;

/**
 * The listener interface for receiving rowIdChange events. The class that is
 * interested in processing a rowIdChange event implements this interface, and
 * the object created with that class is registered with a component using the
 * component's <code>addRowIdChangeListener<code> method. When the rowIdChange
 * event occurs, that object's appropriate method is invoked.
 *
 * @see RowIdChangeEvent
 */
public interface RowIdChangeListener extends Serializable {

	/**
	 * Row id change.
	 *
	 * @param event the event
	 */
	public void rowIdChange(RowIdChangeEvent event);
}
