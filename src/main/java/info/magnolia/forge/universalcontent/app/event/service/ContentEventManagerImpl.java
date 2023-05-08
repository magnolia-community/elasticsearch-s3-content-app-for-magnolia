/*
 *
 */
package info.magnolia.forge.universalcontent.app.event.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.collections4.ListUtils;

import com.vaadin.event.ShortcutAction;

import info.magnolia.forge.universalcontent.app.custom.interfaces.RefreshCacheEvent;
import info.magnolia.forge.universalcontent.app.event.AddItem;
import info.magnolia.forge.universalcontent.app.event.AddItemEvent;
import info.magnolia.forge.universalcontent.app.event.GenericSearchEvent;
import info.magnolia.forge.universalcontent.app.generic.search.Params;
import info.magnolia.forge.universalcontent.app.generic.service.RepositoryService;
import info.magnolia.ui.actionbar.ActionbarPresenter;
import info.magnolia.ui.api.event.ContentChangedEvent;
import info.magnolia.ui.api.location.LocationChangedEvent;
import info.magnolia.ui.contentapp.browser.BrowserLocation;
import info.magnolia.ui.workbench.event.ActionEvent;
import info.magnolia.ui.workbench.event.ItemDoubleClickedEvent;
import info.magnolia.ui.workbench.event.ItemRightClickedEvent;
import info.magnolia.ui.workbench.event.ItemShortcutKeyEvent;
import info.magnolia.ui.workbench.event.SearchEvent;
import info.magnolia.ui.workbench.event.SelectionChangedEvent;
import info.magnolia.ui.workbench.event.ViewTypeChangedEvent;
import info.magnolia.ui.workbench.search.SearchPresenterDefinition;
import lombok.Data;

/**
 * Implementation Class for manage UI event
 *
 * @see ContentEventManager
 */
@Data
public class ContentEventManagerImpl implements ContentEventManager {

	@Inject
	private RepositoryService services;

	/**
	 * Instantiates a new content event manager impl.
	 *
	 *
	 */
	public ContentEventManagerImpl() {
		super();
	}

	/**
	 * Handler selection changed event.
	 */
	@Override
	public void handlerSelectionChangedEvent() {
		services.getSubAppEventBus().addHandler(SelectionChangedEvent.class, new SelectionChangedEvent.Handler() {

			@Override
			public void onSelectionChanged(SelectionChangedEvent event) {
				handleSelectionChange(event.getItemIds(), services.getUiService().getActionbarPresenter());
			}
		});
	}

	/**
	 * Handle selection change.
	 *
	 * @param selectionIds the selection ids
	 * @param actionbar    the actionbar
	 */
	private void handleSelectionChange(Set<Object> selectionIds, ActionbarPresenter actionbar) {
		BrowserLocation location = services.getUiService().getBrowserManager().getCurrentLocation();
		applySelectionToLocation(location, selectionIds.isEmpty() ? services.getContentConnector().getDefaultItemId()
				: selectionIds.iterator().next());
		services.getUiService().getSubAppContext().getAppContext()
				.updateSubAppLocation(services.getUiService().getSubAppContext(), location);
		services.getUiService().getActionBarService().updateActionbar(actionbar);

	}

	/**
	 * Apply selection to location.
	 *
	 * @param location   the location
	 * @param selectedId the selected id
	 */
	private void applySelectionToLocation(BrowserLocation location, Object selectedId) {
		location.updateNodePath("");
		if (!services.getContentConnector().canHandleItem(selectedId)) {
			// nothing is selected at the moment
		} else {
			location.updateNodePath(services.getContentConnector().getItemUrlFragment(selectedId));
		}
	}

	/**
	 * Handler item double clicked event.
	 */
	@Override
	public void handlerItemDoubleClickedEvent() {
		services.getSubAppEventBus().addHandler(ItemDoubleClickedEvent.class,
				event -> services.getUiService().getActionBarService().executeDefaultAction());
	}

	/**
	 * Handler search event.
	 */
	@Override
	public void handlerSearchEvent() {
		services.getSubAppEventBus().addHandler(SearchEvent.class,
				event -> services.getUiService().getWorkbenchPresenter().doSearch(event.getSearchExpression()));
	}

	/**
	 * Handler item shortcut key event.
	 */
	@Override
	public void handlerItemShortcutKeyEvent() {
		services.getSubAppEventBus().addHandler(ItemShortcutKeyEvent.class, event -> {
			int keyCode = event.getKeyCode();
			switch (keyCode) {
			case ShortcutAction.KeyCode.ENTER:
				services.getUiService().getActionBarService().executeDefaultAction();
				break;
			case ShortcutAction.KeyCode.DELETE:
				services.getUiService().getActionBarService().executeDeleteAction();
				break;
			}
		});
	}

	/**
	 * Handler action event.
	 */
	@Override
	public void handlerActionEvent() {
		services.getSubAppEventBus().addHandler(ActionEvent.class, event -> services.getUiService()
				.getActionBarService().executeAction(event.getActionName(), event.getItemIds(), event.getParameters()));
	}

	/**
	 * Handler add item event.
	 */
	@Override
	public void handlerAddItemEvent() {
		services.getSubAppEventBus().addHandler(AddItemEvent.class, event -> {
			AddItem item = event.getAddItem();
			Params searchParams = event.getSearchParams();
			if (searchParams != null) {
				services.getCustomContainer().addItem(event.getAddItem().getObj());
				services.getCacheHelper().removeAllCachedItems();
				services.getCacheHelper().removeAllCachedResults();
				String jsonSearchParams = services.getUiService().getFactoryConverter().convert(searchParams);
				services.getUiService().getWorkbenchPresenter().doSearch(jsonSearchParams);
			}
		});
	}

	/**
	 * Handler log search event.
	 */
	@Override
	public void handlerLogSearchEvent() {
		services.getSubAppEventBus().addHandler(GenericSearchEvent.class, event -> {
			Params searchParams = event.getSearchParams();
			if (searchParams != null && services.getUiService().getWorkbenchPresenter().getActivePresenter() != null) {
				String jsonSearchParams = services.getUiService().getFactoryConverter().convert(searchParams);
				services.getUiService().getWorkbenchPresenter().doSearch(jsonSearchParams);
			}
		});
//		services.getSubAppEventBus().addHandler(GenericSearchEvent.class, new GenericSearchEvent.Handler() {
//
//			@Override
//			public void onSearch(GenericSearchEvent event) {
//				event.getSearchParams();
//				String searchExpression = "";
//				BrowserLocation location = services.getUiService().getBrowserManager().getCurrentLocation();
//				if (StringUtils.isNotBlank(searchExpression)) {
//					location.updateViewType(ListPresenterDefinition.VIEW_TYPE);
//				}
//				location.updateQuery(searchExpression);
//				services.getUiService().getSubAppContext().getAppContext()
//						.updateSubAppLocation(services.getUiService().getSubAppContext(), location);
//				services.getUiService().getActionBarService()
//						.updateActionbar(services.getUiService().getActionbarPresenter());
//			}
//
//		});
	}

	/**
	 * Handler view type changed event.
	 */
	@Override
	public void handlerViewTypeChangedEvent() {
		services.getSubAppEventBus().addHandler(ViewTypeChangedEvent.class, new ViewTypeChangedEvent.Handler() {

			@Override
			public void onViewChanged(ViewTypeChangedEvent event) {
				BrowserLocation location = services.getUiService().getBrowserManager().getCurrentLocation();
				// remove search term from fragment when switching back
				if (location.getViewType().equals(SearchPresenterDefinition.VIEW_TYPE)
						&& !event.getViewType().equals(SearchPresenterDefinition.VIEW_TYPE)) {
					location.updateQuery("");
					location.updateViewType(SearchPresenterDefinition.VIEW_TYPE);
				}
				location.updateViewType(event.getViewType());
				services.getUiService().getSubAppContext().getAppContext()
						.updateSubAppLocation(services.getUiService().getSubAppContext(), location);
				services.getUiService().getActionBarService()
						.updateActionbar(services.getUiService().getActionbarPresenter());
			}
		});

	}

	/**
	 * Handler item right clicked event.
	 */
	@Override
	public void handlerItemRightClickedEvent() {
		services.getSubAppEventBus().addHandler(ItemRightClickedEvent.class, new ItemRightClickedEvent.Handler() {

			@Override
			public void onItemRightClicked(ItemRightClickedEvent event) {
				services.getUiService().getPopupService().showActionPopup(event.getItemId(), event.getClickX(),
						event.getClickY());
			}
		});

	}

	/**
	 * Handler selection content event.
	 */
	@Override
	public void handlerSelectionContentEvent() {
		services.getSubAppEventBus().addHandler(ContentChangedEvent.class, event -> {
			List<Object> itemIds = new ArrayList<>();
			if (event.getItemId() instanceof Collection) {
				itemIds.addAll((Collection<?>) event.getItemId());
			} else {
				itemIds.add(event.getItemId());
			}
			services.getUiService().getWorkbenchPresenter().refresh();
			// filter out items that can't be handled or doesn't exist
			// if an item passed in the event exists, mark it as selected (see MGNLUI-2919)
			// otherwise preserve previous selection
			List<Object> existingSelectedItemIds = ListUtils.select(itemIds, this::verifyItemExists);
			if (existingSelectedItemIds.isEmpty()) {
				existingSelectedItemIds = ListUtils.select(getSelectedItemIds(), this::verifyItemExists);
			}

			services.getUiService().getWorkbenchPresenter().select(existingSelectedItemIds);

			if (event.isItemContentChanged() && !existingSelectedItemIds.isEmpty()) {
				services.getUiService().getWorkbenchPresenter().expand(existingSelectedItemIds.get(0));
			}

			// use just the first selected item to show the preview image
			if (!existingSelectedItemIds.isEmpty()) {
				services.getUiService().getActionBarService()
						.refreshActionbarPreviewImage(existingSelectedItemIds.get(0));
			}
		});

	}

	/**
	 * Verify item exists.
	 *
	 * @param itemId the item id
	 * @return true, if successful
	 */
	protected boolean verifyItemExists(Object itemId) {
		return services.getContentConnector().canHandleItem(itemId)
				&& services.getContentConnector().getItem(itemId) != null;
	}

	/**
	 * Gets the selected item ids.
	 *
	 * @return the selected item ids
	 */
	public List<Object> getSelectedItemIds() {
		return services.getUiService().getWorkbenchPresenter().getSelectedIds();
	}

	/**
	 * Adds the handler location changed event.
	 */
	@Override
	public void addHandlerLocationChangedEvent() {
		services.getAdminCentralEventBus().addHandler(LocationChangedEvent.class, new LocationChangedEvent.Handler() {
			@Override
			public void onLocationChanged(LocationChangedEvent event) {
				if (event.getNewLocation().equals(services.getUiService().getBrowserManager().getLocation())) {
					services.getUiService().getBrowserManager().restoreBrowser(
							(BrowserLocation) services.getUiService().getBrowserManager().getLocation());
				}
			}
		});

	}

	@Override
	public void handlerRefreshCacheEvent() {
		services.getSubAppEventBus().addHandler(RefreshCacheEvent.class, event -> {
			Params item = event.getParams();
			if (item != null) {
				services.getCacheHelper().removeAllCachedItems();
				services.getCacheHelper().removeAllCachedResults();
				services.getCacheHelper().removeAllFactoryConvert();
				services.getCacheHelper().removeCachedResults(item.toString());
				services.getUiService().getWorkbenchPresenter().doSearch(item.toString());
			}
		});
	}

}
