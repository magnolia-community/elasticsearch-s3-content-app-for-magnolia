/*
 *
 */
package info.magnolia.forge.universalcontent.app.event.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.event.ItemClickEvent.ItemClickListener;
import com.vaadin.v7.ui.Table;
import com.vaadin.v7.ui.Table.ColumnReorderEvent;
import com.vaadin.v7.ui.Table.ColumnResizeEvent;

import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomTwoColumnView;
import info.magnolia.forge.universalcontent.app.generic.connector.FactoryContainer;
import info.magnolia.forge.universalcontent.app.generic.service.RepositoryService;
import info.magnolia.forge.universalcontent.app.generic.ui.FactoryCustomUi;
import info.magnolia.forge.universalcontent.app.generic.ui.FieldProperty;
import info.magnolia.forge.universalcontent.app.generic.ui.table.CustomTable;
import info.magnolia.forge.universalcontent.app.generic.ui.table.RowId;
import info.magnolia.forge.universalcontent.app.generic.utils.GenericConstants;
import info.magnolia.ui.workbench.ContentView.Listener;
import info.magnolia.ui.workbench.list.ListView;
import lombok.Data;

/**
 * Implementation Class for manage event table in generic app.
 */

@Data
public class TableEventServiceImpl implements TableEventService {

	/** The table. */
	private CustomTable table;

	/** The listener. */
	private ListView.Listener listener;

	/** The custom two column view. */
	private CustomTwoColumnView customTwoColumnView;

	/** The factory elastic search. */
	private FactoryContainer factoryContainer;

	/** The Constant log. */
	static final Logger log = LoggerFactory.getLogger(TableEventServiceImpl.class);

	private RepositoryService serviceContainer;

	/**
	 * Adds the value change listener.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void AddValueChangeListener() {
		table.addValueChangeListener(new Property.ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				Object valueSelect = event.getProperty().getValue();
				if (listener != null) {
					Set<Object> items;
					if (valueSelect instanceof Set) {
						items = (Set) valueSelect;
						// Container roots are expected to have null parent itemId.
						// Then, we remove it from table selection, so next multi-selection won't add to
						// it, and let valueChangeListener kick in again
						if (items.size() == 1 && items.contains(null)) {
							table.setValue(null);
							return;
						}
					} else if (valueSelect == null) {
						items = Collections.emptySet();
					} else {
						items = new LinkedHashSet<>();
						items.add(valueSelect);
					}
					VerticalLayout verticalLayout = null;

					Component component = customTwoColumnView.getLeftView();
					if (component instanceof VerticalLayout) {
						verticalLayout = (VerticalLayout) component;
					}
					List<FieldProperty> values = new ArrayList<FieldProperty>();

					List<java.lang.reflect.Field> classFields = serviceContainer.getConverterClass()
							.getAllFields(factoryContainer.getClassType());

					List<Object> item = items.stream().collect(Collectors.toList());
					if (item != null && item.size() > 0) {
						RowId row = (RowId) item.get(0);
						for (int i = 0; i < row.getId().length; i++) {
							String fieldName = classFields.get(i).getName();
							String value = "";
							try {

								if (row.getId()[i] != null) {
									value = row.getId()[i].toString();
								}

								if (items.size() == 0) {
									value = "";
								}
							} catch (Exception e) {
								log.error("ValueChange:", e);
							}

							FieldProperty property = new FieldProperty(fieldName, classFields.get(i), Boolean.TRUE,
									value);
							values.add(property);
						}
					}
					if (items.size() > 0) {
						FieldProperty property = new FieldProperty(GenericConstants.BUTTON_ADD_LABEL, null,
								Boolean.TRUE, GenericConstants.BUTTON_EDIT_LABEL);
						values.add(property);
					} else {
						FieldProperty property = new FieldProperty(GenericConstants.BUTTON_EDIT_LABEL, null,
								Boolean.TRUE, GenericConstants.BUTTON_ADD_LABEL);
						values.add(property);
					}

					FactoryCustomUi.create(verticalLayout, factoryContainer.getClassType())
							.updateFieldsFromTab(GenericConstants.TAB_NAME_MANAGE, values);
					listener.onItemSelection(items);
				}
			}
		});

	}

	/**
	 * Adds the item click listener.
	 */
	@Override
	public void addItemClickListener() {
		table.addItemClickListener(new ItemClickListener() {

			@Override
			public void itemClick(ItemClickEvent event) {

				if (event.getButton() == MouseButton.RIGHT) {
					if (listener != null) {
						listener.onRightClick(event.getItemId(), event.getClientX(), event.getClientY());
					}
				} else if (event.isDoubleClick()) {
					if (listener != null) {
						listener.onDoubleClick(event.getItemId());
					}
				} else {
					Object value = table.getValue();
					if (value != null) {
						Set<Object> items;
						if (value instanceof Set) {
							items = (Set<Object>) value;
						} else {
							items = new LinkedHashSet<>();
							items.add(value);
						}
						if (items.size() == 1 && ObjectUtils.equals(items.iterator().next(), event.getItemId())) {
							table.setValue(null);
						}
					}
				}
			}
		});

	}

	/**
	 * Adds the column resize listener.
	 */
	@Override
	public void addColumnResizeListener() {
		table.addColumnResizeListener(new Table.ColumnResizeListener() {

			@Override
			public void columnResize(ColumnResizeEvent event) {
				if (event.getCurrentWidth() < GenericConstants.MINIMUM_COLUMN_WIDTH) {
					table.setColumnWidth(event.getPropertyId(), GenericConstants.MINIMUM_COLUMN_WIDTH);
				}
			}
		});

	}

	/**
	 * Adds the column reorder listener.
	 */
	@Override
	public void addColumnReorderListener() {
		table.addColumnReorderListener(new Table.ColumnReorderListener() {

			@Override
			public void columnReorder(ColumnReorderEvent event) {
//		log.error("addColumnReorderListener");
			}
		});

	}

	/**
	 * Adds the action handler.
	 */
	@Override
	public void addActionHandler() {
		table.addActionHandler(new Handler() {

			@Override
			public void handleAction(Action action, Object sender, Object target) {
			}

			@Override
			public Action[] getActions(Object target, Object sender) {
				return null;
			}
		});

	}

	/**
	 * Sets the cell style generator.
	 */
	@Override
	public void setCellStyleGenerator() {
		table.setCellStyleGenerator(new Table.CellStyleGenerator() {

			@Override
			public String getStyle(Table source, Object itemId, Object propertyId) {
				// icon style is expected on the whole table row, not on a column matching a
				// specific propertyId
				if (propertyId == null && itemId != null) {
					final Item item = source.getContainerDataSource().getItem(itemId);
					if (item == null) {
						return GenericConstants.DELETED_ROW_STYLENAME;
					} else {
						return listener.getIcon(item);
					}
				}
				return null;
			}
		});

	}

	/**
	 * Instantiates a new table event service impl.
	 *
	 * @param table               the table
	 * @param listener            the listener
	 * @param factoryContainer    the factory elastic search
	 * @param customTwoColumnView the custom two column view
	 */
	public TableEventServiceImpl(CustomTable table, Listener listener, FactoryContainer factoryContainer,
			CustomTwoColumnView customTwoColumnView, RepositoryService repositoryService) {
		super();
		this.table = table;
		this.listener = listener;
		this.customTwoColumnView = customTwoColumnView;
		this.factoryContainer = factoryContainer;
		this.serviceContainer = repositoryService;
	}

}
