/*
 *
 */
package info.magnolia.forge.universalcontent.app.event;

import java.util.EventObject;

import com.vaadin.v7.data.Container;

/**
 * The Class ItemSetChangeEvent.
 */
@SuppressWarnings("serial")
@Deprecated
public class ItemSetChangeEvent extends EventObject implements Container.ItemSetChangeEvent {

	/**
	 * Instantiates a new item set change event.
	 *
	 * @param source the source
	 */
	private ItemSetChangeEvent(Container source) {
		super(source);
	}

	/**
	 * Gets the container.
	 *
	 * @return the container
	 */
	@Override
	public Container getContainer() {
		return (Container) getSource();
	}
}
