/*
 *
 */
package com.whitelabel.app.event;

import com.whitelabel.app.generic.search.Params;

import info.magnolia.event.Event;
import info.magnolia.event.EventHandler;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Event manager for Add Item. Instantiates a new adds the item event.
 */

@AllArgsConstructor
@Data
public class AddItemEvent implements Event<AddItemEvent.Handler> {

	/** The add item. */
	private AddItem addItem;

	/** The search params. */
	private Params searchParams;

	/**
	 * The Interface Handler.
	 */
	public interface Handler extends EventHandler {

		/**
		 * On add item.
		 *
		 * @param event the event
		 */
		void onAddItem(AddItemEvent event);
	}

	/**
	 * Dispatch.
	 *
	 * @param handler the handler
	 */
	@Override
	public void dispatch(Handler handler) {
		handler.onAddItem(this);
	}

	/**
	 * Instantiates a new adds the item event.
	 *
	 * @param addItem the add item
	 */
	public AddItemEvent(AddItem addItem) {
		super();
		this.addItem = addItem;
	}

}
