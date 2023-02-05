package com.whitelabel.app.custom.interfaces;

import com.whitelabel.app.generic.search.Params;

import info.magnolia.event.Event;
import info.magnolia.event.EventHandler;
import lombok.Data;

@Data
public class RefreshCacheEvent implements Event<RefreshCacheEvent.Handler> {

	private Params params;

	/**
	 * The Interface Handler.
	 */
	public interface Handler extends EventHandler {

		void refresh(RefreshCacheEvent event);
	}

	/**
	 * Dispatch.
	 *
	 * @param handler the handler
	 */
	@Override
	public void dispatch(Handler handler) {
		handler.refresh(this);
	}

	/**
	 * Instantiates a new adds the item event.
	 *
	 * @param addItem the add item
	 */
	public RefreshCacheEvent(Params refresh) {
		super();
		this.params = refresh;
	}
}
