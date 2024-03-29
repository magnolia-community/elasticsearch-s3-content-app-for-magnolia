/*
 *
 */
package info.magnolia.forge.universalcontent.app.event;

import info.magnolia.event.Event;
import info.magnolia.event.EventHandler;
import info.magnolia.forge.universalcontent.app.generic.search.Params;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Event fired when click on search or select new item.
 */

@AllArgsConstructor
@Data
public class GenericSearchEvent implements Event<GenericSearchEvent.Handler> {

	/** The search params. */
	private Params searchParams;

	/**
	 * The Interface Handler.
	 */
	public interface Handler extends EventHandler {

		/**
		 * On search.
		 *
		 * @param event the event
		 */
		void onSearch(GenericSearchEvent event);
	}

	/**
	 * Dispatch.
	 *
	 * @param handler the handler
	 */
	@Override
	public void dispatch(Handler handler) {
		handler.onSearch(this);
	}

}
