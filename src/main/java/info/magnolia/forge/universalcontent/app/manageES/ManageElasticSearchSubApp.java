/*
 *
 */
package info.magnolia.forge.universalcontent.app.manageES;

import java.util.Optional;

import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.event.EventBus;
import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomContentConnector;
import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomTwoColumnView;
import info.magnolia.forge.universalcontent.app.custom.interfaces.ListSearchViewAppInterface;
import info.magnolia.forge.universalcontent.app.custom.interfaces.Listener;
import info.magnolia.forge.universalcontent.app.event.service.ContentEventManager;
import info.magnolia.forge.universalcontent.app.event.service.ContentEventManagerImpl;
import info.magnolia.forge.universalcontent.app.generic.connector.GenericContentConnector;
import info.magnolia.forge.universalcontent.app.generic.search.Params;
import info.magnolia.forge.universalcontent.app.generic.service.RepositoryService;
import info.magnolia.forge.universalcontent.app.generic.ui.dialog.ActionBarService;
import info.magnolia.forge.universalcontent.app.generic.ui.dialog.ActionBarServiceImpl;
import info.magnolia.forge.universalcontent.app.generic.ui.dialog.PopupService;
import info.magnolia.forge.universalcontent.app.generic.ui.dialog.PopupServiceImpl;
import info.magnolia.forge.universalcontent.app.generic.ui.service.BrowserManager;
import info.magnolia.forge.universalcontent.app.generic.ui.service.GenericBrowserPresenter;
import info.magnolia.forge.universalcontent.app.generic.ui.service.UiEventAction;
import info.magnolia.forge.universalcontent.app.generic.utils.FactoryConverter;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.actionbar.ActionbarPresenter;
import info.magnolia.ui.api.action.ActionExecutor;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.api.app.SubAppEventBus;
import info.magnolia.ui.api.event.AdmincentralEventBus;
import info.magnolia.ui.api.location.Location;
import info.magnolia.ui.api.view.View;
import info.magnolia.ui.contentapp.browser.BrowserLocation;
import info.magnolia.ui.contentapp.browser.BrowserSubAppDescriptor;
import info.magnolia.ui.contentapp.browser.BrowserView;
import info.magnolia.ui.framework.app.BaseSubApp;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;
import info.magnolia.ui.workbench.WorkbenchPresenter;
import lombok.Data;

/**
 * Principal Class source content connector app
 */
@Data
public class ManageElasticSearchSubApp extends BaseSubApp<CustomTwoColumnView> implements CustomTwoColumnView.Listener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(ManageElasticSearchSubApp.class);

	/** The view. */
	protected CustomTwoColumnView view;

	/** The search. */
	protected Listener search;

	/** The sub app event bus. */
	protected EventBus subAppEventBus;

	/** The action executor. */
	protected ActionExecutor actionExecutor;

	/** The content connector. */
	protected ContentConnector contentConnector;

	/** The checker. */
	protected info.magnolia.ui.availability.AvailabilityChecker checker;

	/** The popup service. */
	private PopupService popupService;

	/** The action bar service. */
	private ActionBarService actionBarService;

	/** The content event manager. */
	private ContentEventManager contentEventManager;

	/** The browser manager. */
	private BrowserManager browserManager;

	private RepositoryService serviceContainer;

	/**
	 * Instantiates a new manage elastic search sub app.
	 *
	 * @param actionExecutor       the action executor
	 * @param subAppContext        the sub app context
	 * @param productBrowserView   the product browser view
	 * @param search               the search
	 * @param browser              the browser
	 * @param subAppEventBus       the sub app event bus
	 * @param adminCentralEventBus the admin central event bus
	 * @param contentConnector     the content connector
	 * @param checker              the checker
	 * @param workbenchPresenter   the workbench presenter
	 * @param factoryConverter     the factory converter
	 * @param actionbarPresenter   the actionbar presenter
	 * @param factoryContainer     the factory elastic search
	 * @param serviceContainer
	 *
	 *
	 */
	public ManageElasticSearchSubApp(ActionExecutor actionExecutor, final SubAppContext subAppContext,
			final CustomTwoColumnView productBrowserView, final Listener search, UiEventAction uiEventAction,
			final @Named(SubAppEventBus.NAME) EventBus subAppEventBus, GenericBrowserPresenter browser,
			@Named(AdmincentralEventBus.NAME) EventBus adminCentralEventBus, ContentConnector contentConnector,
			info.magnolia.ui.availability.AvailabilityChecker checker, WorkbenchPresenter workbenchPresenter,
			FactoryConverter factoryConverter, ActionbarPresenter actionbarPresenter,
			RepositoryService serviceContainer, ComponentProvider componentProvider,
			ListSearchViewAppInterface listSearchViewAppInterface) {

		super(subAppContext, productBrowserView);
		this.view = productBrowserView;
		serviceContainer.getUiService().setLogListView(listSearchViewAppInterface);
		if (subAppContext == null || view == null || browser == null || subAppEventBus == null) {
			throw new IllegalArgumentException("Constructor does not allow for null args. Found SubAppContext = "
					+ subAppContext + ", ContentSubAppView = " + view + ", BrowserPresenter = " + browser
					+ ", EventBus = " + subAppEventBus);
		}
		serviceContainer.getUiService().setProductBrowserView(productBrowserView);
		serviceContainer.getUiService().setBrowser(browser);
		this.search = search;
		this.subAppEventBus = subAppEventBus;
		this.actionExecutor = actionExecutor;
		this.contentConnector = contentConnector;
		this.checker = checker;

		this.actionBarService = new ActionBarServiceImpl(workbenchPresenter,
				(BrowserSubAppDescriptor) subAppContext.getSubAppDescriptor(), actionExecutor, getAppContext(),
				contentConnector, actionbarPresenter, null, null, checker, browser, subAppContext);
		this.popupService = new PopupServiceImpl(browser, subAppContext, actionExecutor, checker, actionBarService);

		browserManager = new BrowserManager(null, actionBarService, contentConnector, subAppContext, getAppContext(),
				browser);
		serviceContainer.getUiService().setUiEventAction(uiEventAction);
		serviceContainer.getUiService().setActionbarPresenter(actionbarPresenter);
		serviceContainer.setContentConnector((CustomContentConnector) contentConnector);
		serviceContainer.setAdminCentralEventBus(adminCentralEventBus);
		serviceContainer.getUiService().setProductBrowserView(productBrowserView);
		serviceContainer.getUiService().setSubAppContext(subAppContext);
		serviceContainer.getUiService().setActionExecutor(actionExecutor);
		serviceContainer.setSubAppEventBus(subAppEventBus);
		serviceContainer.getUiService().setWorkbenchPresenter(workbenchPresenter);
		serviceContainer.getUiService().setChecker(checker);
		if (((CustomContentConnector) contentConnector).getQueryDelegate() != null) {
			((CustomContentConnector) contentConnector).getQueryDelegate().setServiceContainer(serviceContainer);
		}
		serviceContainer.getS3Delegate().setServiceContainer(serviceContainer);
		serviceContainer.getElasticSearchDelegate().setServiceContainer(serviceContainer);
		serviceContainer.setSearch(search);
		serviceContainer.getUiService().setPopupService(popupService);

		serviceContainer.getUiService().setBrowserManager(browserManager);

		serviceContainer.getUiService().setActionBarService(actionBarService);
		serviceContainer.getUiService().setComponentProvider(componentProvider);
		contentEventManager = new ContentEventManagerImpl();
		contentEventManager.setServices(serviceContainer);
		contentEventManager.addHandlerLocationChangedEvent();
		CustomContentConnector c = (GenericContentConnector) contentConnector;
		Optional<CustomContentConnector> o = Optional.of(c);
		serviceContainer.setContentConnector((CustomContentConnector) contentConnector);
		serviceContainer.getCustomContainer().setContentConnector(o);

		serviceContainer.getUiService().setContentEventManager(contentEventManager);
		this.serviceContainer = serviceContainer;
	}

	/**
	 * On search configuration.
	 *
	 * @param params the params
	 */
	@Override
	public void onSearchConfiguration(Params params) {
		serviceContainer.getUiService().getBrowser().setSearchParams(params);
	}

	/**
	 * Start.
	 *
	 * @param location the location
	 * @return the custom two column view
	 */
	@Override
	public CustomTwoColumnView start(final Location location) {
		info.magnolia.ui.contentapp.browser.BrowserLocation loc = info.magnolia.ui.contentapp.browser.BrowserLocation
				.wrap(location);
		serviceContainer.getUiService().getProductBrowserView().setListener(this);
		View startSearch = serviceContainer.getSearch().start();
		BrowserView startBrowser = serviceContainer.getUiService().getBrowser().start();
		String defaultViewType = null;
		try {
			defaultViewType = serviceContainer.getUiService().getBrowser().getDefaultViewType();
		} catch (Exception e) {
			defaultViewType = null;
		}
		serviceContainer.getUiService().getProductBrowserView().setLeftView(startSearch);

		if (startBrowser != null && defaultViewType != null) {
			serviceContainer.getUiService().getProductBrowserView().setRightView(startBrowser);
			serviceContainer.getUiService().getBrowserManager().restoreBrowser(loc);
			final ActionbarPresenter actionbar = serviceContainer.getUiService().getBrowser().getActionbarPresenter();
			serviceContainer.getUiService().setActionbarPresenter(actionbar);
			serviceContainer.getUiService().getContentEventManager().handlerSelectionChangedEvent();
			serviceContainer.getUiService().getContentEventManager().handlerItemRightClickedEvent();
			serviceContainer.getUiService().getContentEventManager().handlerViewTypeChangedEvent();
			serviceContainer.getUiService().getContentEventManager().handlerLogSearchEvent();
		}
		serviceContainer.getUiService().setProductBrowserView(getView());
		serviceContainer.getUiService().setView(startBrowser);
		return getView();
	}

	/**
	 * The default implementation selects the path in the current workspace and
	 * updates the available actions in the actionbar.
	 *
	 * @param location the location
	 */
	@Override
	public void locationChanged(final Location location) {
		super.locationChanged(location);
		serviceContainer.getUiService().getBrowserManager().setLocation(location);
		serviceContainer.getUiService().getBrowserManager().restoreBrowser(BrowserLocation.wrap(location));
	}

	/**
	 * Wraps the current DefaultLocation in a {@link BrowserLocation}. Providing
	 * getter and setters for used parameters.
	 *
	 * @return the current location
	 */
	@Override
	public BrowserLocation getCurrentLocation() {
		return BrowserLocation.wrap(super.getCurrentLocation());
	}

}
