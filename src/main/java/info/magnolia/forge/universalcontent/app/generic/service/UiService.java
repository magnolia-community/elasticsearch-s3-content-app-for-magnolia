package info.magnolia.forge.universalcontent.app.generic.service;

import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomListenerPresenter;
import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomTwoColumnView;
import info.magnolia.forge.universalcontent.app.custom.interfaces.ListSearchViewAppInterface;
import info.magnolia.forge.universalcontent.app.event.service.ContentEventManager;
import info.magnolia.forge.universalcontent.app.generic.ui.dialog.ActionBarService;
import info.magnolia.forge.universalcontent.app.generic.ui.dialog.PopupService;
import info.magnolia.forge.universalcontent.app.generic.ui.service.BrowserManager;
import info.magnolia.forge.universalcontent.app.generic.ui.service.UiEventAction;
import info.magnolia.forge.universalcontent.app.generic.ui.table.CustomTable;
import info.magnolia.forge.universalcontent.app.generic.utils.FactoryConverter;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.actionbar.ActionbarPresenter;
import info.magnolia.ui.api.action.ActionExecutor;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.contentapp.browser.BrowserView;
import info.magnolia.ui.workbench.WorkbenchPresenter;

public interface UiService {

	public void setWorkbenchPresenter(WorkbenchPresenter workbenchPresenter);

	/** The action executor. */
	public ActionExecutor getActionExecutor();

	public FactoryConverter getFactoryConverter();

	public void setView(BrowserView browserView);

	public void setUiEventAction(UiEventAction uiEventAction);

	/** The checker. */
	public info.magnolia.ui.availability.AvailabilityChecker getChecker();

	/** The popup service. */
	public PopupService getPopupService();

	/** The action bar service. */
	public ActionBarService getActionBarService();

	/** The content event manager. */
	public ContentEventManager getContentEventManager();

	/** The browser manager. */
	public BrowserManager getBrowserManager();

	public SubAppContext getSubAppContext();

	public ActionbarPresenter getActionbarPresenter();

	public ListSearchViewAppInterface getLogListView();

	public void setLogListView(ListSearchViewAppInterface logListView);

	public CustomListenerPresenter getBrowser();

	public void setBrowser(CustomListenerPresenter browser);

	public void setTable(CustomTable table);

	public CustomTable getTable();

	public ComponentProvider getComponentProvider();

	public BrowserView getView();

	public UiEventAction getUiEventAction();

	public CustomTwoColumnView getProductBrowserView();

	public void setProductBrowserView(CustomTwoColumnView productBrowserView);

	/** The action executor. */
	public void setActionExecutor(ActionExecutor actionExecutor);

	/** The checker. */
	public void setChecker(info.magnolia.ui.availability.AvailabilityChecker availabilityChecker);

	public void setComponentProvider(ComponentProvider componentProvider);

	/** The popup service. */
	public void setPopupService(PopupService popupService);

	/** The action bar service. */
	public void setActionBarService(ActionBarService actionBarService);

	/** The content event manager. */
	public void setContentEventManager(ContentEventManager contentEventManager);

	/** The browser manager. */
	public void setBrowserManager(BrowserManager browserManager);

	public void setSubAppContext(SubAppContext subAppContext);

	public void setActionbarPresenter(ActionbarPresenter actionbarPresenter);

	public void setFactoryConverter(FactoryConverter factoryConverter);

	public WorkbenchPresenter getWorkbenchPresenter();
}
