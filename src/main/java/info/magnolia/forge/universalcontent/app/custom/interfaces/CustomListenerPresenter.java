/*
 *
 */
package info.magnolia.forge.universalcontent.app.custom.interfaces;

import java.util.List;

import info.magnolia.forge.universalcontent.app.generic.search.Params;
import info.magnolia.ui.actionbar.ActionbarPresenter;
import info.magnolia.ui.contentapp.browser.BrowserView;

/**
 * The Interface Listener.
 */
public interface CustomListenerPresenter extends ActionbarPresenter.Listener, BrowserView.Listener {

	/**
	 * Start.
	 *
	 * @return the view
	 */
	public BrowserView start();

	public boolean hasViewType(String viewType);

	public String getDefaultViewType();

	public ActionbarPresenter getActionbarPresenter();

	public void resync(List<Object> asList, String viewType, String query);

	public void setSearchParams(Params params);

	public List<Object> getSelectedItemIds();

	public BrowserView getView();
}
