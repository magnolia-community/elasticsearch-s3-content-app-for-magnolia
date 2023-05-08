/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.ui.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Container;
import com.vaadin.v7.ui.Table.TableDragMode;

import info.magnolia.event.EventBus;
import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomTwoColumnView;
import info.magnolia.forge.universalcontent.app.custom.interfaces.ListSearchViewAppInterface;
import info.magnolia.forge.universalcontent.app.event.service.TableEventService;
import info.magnolia.forge.universalcontent.app.event.service.TableEventServiceImpl;
import info.magnolia.forge.universalcontent.app.generic.connector.GenericContainer;
import info.magnolia.forge.universalcontent.app.generic.others.LogStatus;
import info.magnolia.forge.universalcontent.app.generic.service.RepositoryService;
import info.magnolia.forge.universalcontent.app.generic.ui.table.CustomTable;
import info.magnolia.ui.workbench.ContentView;
import info.magnolia.ui.workbench.column.definition.ColumnFormatter;
import info.magnolia.ui.workbench.list.ListView;
import lombok.Getter;

/**
 * Manage Table List view for source content app
 *
 * @see ListSearchViewAppInterface
 */
public class GenericListViewImpl implements ListSearchViewAppInterface {

	/** The table. */
	@Getter
	private CustomTable table;

	/** The listener. */
	private ListView.Listener listener;

	/** The manage table event. */
	private TableEventService manageTableEvent;

	/** The custom two column view. */
	private CustomTwoColumnView customTwoColumnView;

	@Inject
	private RepositoryService serviceContainer;

	/**
	 * Instantiates a new log list view impl.
	 *
	 * @param appEventBus         the app event bus
	 * @param uiEventAction       the ui event action
	 * @param factory             the factory
	 * @param customTwoColumnView the custom two column view
	 */
	@Inject
	public GenericListViewImpl(CustomTwoColumnView customTwoColumnView) {
		this.customTwoColumnView = customTwoColumnView;
	}

	/**
	 * Initialize table.
	 *
	 * @param table the table
	 */
	public void initializeTable(CustomTable table) {
		table.setSizeFull();

		table.setSelectable(true);
		table.setMultiSelect(true);
		table.setNullSelectionAllowed(true);

		table.setDragMode(TableDragMode.ROW);
		table.setEditable(true);
		table.setColumnCollapsingAllowed(true);
		table.setColumnReorderingAllowed(true);
		manageTableEvent.setCellStyleGenerator();
		table.setPageLength(10);
		table.setImmediate(true);

		table.setSizeFull();
		table.addStyleName("no-header-checkbox");
		serviceContainer.getUiService().setTable(table);
		bindHandlers();
	}

	/**
	 * Bind handlers.
	 */
	public void bindHandlers() {
		manageTableEvent.addActionHandler();

		manageTableEvent.AddValueChangeListener();
		manageTableEvent.addItemClickListener();

		serviceContainer.getUiService().getTable().getColumnHeaders();
		manageTableEvent.addColumnResizeListener();

	}

	/**
	 * Gets the listener.
	 *
	 * @return the listener
	 */
	public ListView.Listener getListener() {
		return listener;
	}

	/**
	 * Gets the UI event action.
	 *
	 * @return the UI event action
	 */
	public UiEventAction getUIEventAction() {
		return this.serviceContainer.getUiService().getUiEventAction();
	}

	/**
	 * Sets the listener.
	 *
	 * @param listener the new listener
	 */
	@Override
	public void setListener(ContentView.Listener listener) {
		this.listener = listener;
	}

	/**
	 * Sets the container.
	 *
	 * @param container the new container
	 */
	@Override
	public void setContainer(Container container) {
		if (container != null && ((GenericContainer) container).getContentConnector().isPresent()) {
			table = createTable(container, serviceContainer.getSubAppEventBus());
			initializeTable(table);
			serviceContainer.getUiService().setTable(table);
		}

	}

	/**
	 * Creates the table.
	 *
	 * @param container   the container
	 * @param appEventBus the app event bus
	 * @return the custom table
	 */
	public CustomTable createTable(Container container, EventBus appEventBus) {
		CustomTable table = new CustomTable(container);
		table.setServiceContainer(serviceContainer);
		manageTableEvent = new TableEventServiceImpl(table, listener, serviceContainer.getFactoryContainer(),
				customTwoColumnView, serviceContainer);
		manageTableEvent.addColumnReorderListener();
		return table;
	}

	/**
	 * Adds the column.
	 *
	 * @param propertyId the property id
	 * @param title      the title
	 */
	@Override
	public void addColumn(String propertyId, String title) {
		if (table == null || !table.getContainerPropertyIds().contains(propertyId)) {
			serviceContainer.getLogService().logger(LogStatus.ERROR,
					"Ignoring column '{}', container does not support this propertyId.", this.getClass(), null);
			return;
		}

		serviceContainer.getUiService().getTable().setColumnHeader(propertyId, title);
		List<Object> visibleColumns = new ArrayList<Object>(
				Arrays.asList(serviceContainer.getUiService().getTable().getVisibleColumns()));
		if (!visibleColumns.contains(propertyId)) {
			visibleColumns.add(propertyId);
		}
		serviceContainer.getUiService().getTable().setVisibleColumns(visibleColumns.toArray());
	}

	/**
	 * Adds the column.
	 *
	 * @param propertyId the property id
	 * @param title      the title
	 * @param width      the width
	 */
	@Override
	public void addColumn(String propertyId, String title, int width) {
		if (serviceContainer.getUiService().getTable() != null) {
			addColumn(propertyId, title);
			serviceContainer.getUiService().getTable().setColumnWidth(propertyId, width);
		}

	}

	/**
	 * Adds the column.
	 *
	 * @param propertyId  the property id
	 * @param title       the title
	 * @param expandRatio the expand ratio
	 */
	@Override
	public void addColumn(String propertyId, String title, float expandRatio) {
		if (serviceContainer.getUiService().getTable() != null) {
			addColumn(propertyId, title);
			serviceContainer.getUiService().getTable().setColumnExpandRatio(propertyId, expandRatio);
		}
	}

	/**
	 * Sets the column formatter.
	 *
	 * @param propertyId the property id
	 * @param formatter  the formatter
	 */
	@Override
	public void setColumnFormatter(String propertyId, ColumnFormatter formatter) {
		serviceContainer.getUiService().getTable().removeGeneratedColumn(propertyId);
		if (serviceContainer.getUiService().getTable() != null
				&& serviceContainer.getUiService().getTable().getColumnHeaders() != null
				&& !Arrays.asList(serviceContainer.getUiService().getTable().getColumnHeaders()).contains(propertyId)) {
			serviceContainer.getUiService().getTable().addGeneratedColumn(propertyId, formatter);
		} else {

			serviceContainer.getUiService().getTable().addGeneratedColumn(propertyId, formatter);
		}
	}

	/**
	 * Clear columns.
	 */
	@Override
	public void clearColumns() {
		if (serviceContainer.getUiService().getTable() != null) {
			serviceContainer.getUiService().getTable().setVisibleColumns(new Object[] {});
		}
	}

	/**
	 * Select.
	 *
	 * @param itemIds the item ids
	 */
	@Override
	public void select(List<Object> itemIds) {
		if (serviceContainer.getUiService().getTable() == null) {
			return;
		}
		if (itemIds == null) {
			serviceContainer.getUiService().getTable().setValue(null);
			return;
		}

		// convert to set before comparing with actual table selection (which is also a
		// set underneath)
		Set<Object> uniqueItemIds = new HashSet<>(itemIds);
		if (uniqueItemIds.equals(serviceContainer.getUiService().getTable().getValue())) {
			// selection already in sync, nothing to do
			return;
		}

		serviceContainer.getUiService().getTable().setValue(uniqueItemIds);
		// do not #setCurrentPageFirstItemId because AbstractJcrContainer's index
		// resolution is super slow.
	}

	/**
	 * Expand.
	 *
	 * @param itemId the item id
	 */
	@Override
	public void expand(Object itemId) {
	}

	/**
	 * As vaadin component.
	 *
	 * @return the component
	 */
	@Override
	public Component asVaadinComponent() {
		VerticalLayout layout = new VerticalLayout();
		if (serviceContainer.getUiService().getTable() != null) {
			layout.addComponent(serviceContainer.getUiService().getTable());
		}
		return layout;
	}

	/**
	 * Sets the multiselect.
	 *
	 * @param multiselect the new multiselect
	 */
	@Override
	public void setMultiselect(boolean multiselect) {
		if (serviceContainer.getUiService().getTable() != null) {
			serviceContainer.getUiService().getTable().setMultiSelect(multiselect);
		}
	}

	/**
	 * On shortcut key.
	 *
	 * @param keyCode      the key code
	 * @param modifierKeys the modifier keys
	 */
	@Override
	public void onShortcutKey(int keyCode, int[] modifierKeys) {
		if (listener != null) {
			listener.onShortcutKey(keyCode, modifierKeys);
		}
	}

}
