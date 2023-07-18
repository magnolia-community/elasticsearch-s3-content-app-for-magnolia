package info.magnolia.forge.universalcontent.app.generic.service;

import javax.inject.Inject;

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
import lombok.Data;

@Data
public class UiServiceImpl implements UiService {
	private ComponentProvider componentProvider;

	/** The checker. */
	private info.magnolia.ui.availability.AvailabilityChecker checker;

	/** The popup service. */
	private PopupService popupService;

	/** The action bar service. */
	private ActionBarService actionBarService;

	/** The content event manager. */
	private ContentEventManager contentEventManager;

	/** The browser manager. */
	private BrowserManager browserManager;

	private CustomListenerPresenter browser;

	private SubAppContext subAppContext;

	private ActionbarPresenter actionbarPresenter;

	private WorkbenchPresenter workbenchPresenter;

	private BrowserView view;

	private UiEventAction uiEventAction;

	private ListSearchViewAppInterface logListView;

	private CustomTable table;

	private CustomTwoColumnView productBrowserView;

	/** The action executor. */
	private ActionExecutor actionExecutor;

	private FactoryConverter factoryConverter;

	@Inject
	public UiServiceImpl(RepositoryService serviceRepository) {
		factoryConverter = new FactoryConverter(serviceRepository);
	}
}
