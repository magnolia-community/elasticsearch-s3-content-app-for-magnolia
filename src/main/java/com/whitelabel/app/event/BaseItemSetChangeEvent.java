/*
 *
 */
package com.whitelabel.app.event;

import java.io.Serializable;
import java.util.EventObject;

import com.vaadin.v7.data.Container;

/**
 * Event fired when content change something.
 */
public class BaseItemSetChangeEvent extends EventObject implements Container.ItemSetChangeEvent, Serializable {

	/**
	 * Instantiates a new base item set change event.
	 *
	 * @param source the source
	 */
	public BaseItemSetChangeEvent(Container source) {
		super(source);
	}

	/**
	 * Gets the container.
	 *
	 * @return the container
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Container getContainer() {
		return (Container) getSource();
	}
}
