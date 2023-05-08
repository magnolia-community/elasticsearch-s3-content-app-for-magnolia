/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.ui;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

import info.magnolia.forge.universalcontent.app.custom.interfaces.Listener;

import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

import lombok.NoArgsConstructor;

/**
 * Builder for create programmately UI on tab (or not) textbox, textarea,
 * selectbox.
 *
 * @param <T> the generic type
 */
@NoArgsConstructor
public class CustomUi<T> {

	/** The layout. */
	private VerticalLayout layout;

	/** The component. */
	private com.vaadin.ui.Component component;

	/** The option custom ui. */
	private OptionsCustomUi optionCustomUi;

	/** The tabsheet. */
	private TabSheet tabsheet;

	/** The group. */
	private GroupCustomUi<T> group;

	/** The tab. */
	private TabSheet tab;

	/**
	 * Creates the text field.
	 *
	 * @param name        the name
	 * @param label       the label
	 * @param placeholder the placeholder
	 * @return the options custom ui
	 */
	public OptionsCustomUi createTextField(String name, String label, String placeholder) {
		this.component = FactoryCustomUi.createTextbox(label, placeholder, name);
		optionCustomUi = new OptionsCustomUi(this);
		return optionCustomUi;
	}

	/**
	 * Creates the text field.
	 *
	 * @param name        the name
	 * @param label       the label
	 * @param placeholder the placeholder
	 * @return the options custom ui
	 */
	public OptionsCustomUi createFileField(String name, String label, String placeholder) {
		this.component = FactoryCustomUi.createFile(name, label, placeholder);
		optionCustomUi = new OptionsCustomUi(this);
		return optionCustomUi;
	}

	/**
	 * Creates the text area field.
	 *
	 * @param name        the name
	 * @param label       the label
	 * @param placeholder the placeholder
	 * @param rows        the rows
	 * @return the options custom ui
	 */
	public OptionsCustomUi createTextAreaField(String name, String label, String placeholder, Integer rows) {
		this.component = FactoryCustomUi.createTextarea(label, placeholder, name, rows);
		optionCustomUi = new OptionsCustomUi(this);
		return optionCustomUi;
	}

	/**
	 * Creates the tab.
	 *
	 * @param visible the visible
	 * @return the group custom ui
	 */
	public GroupCustomUi<T> createTab(Boolean visible) {
		build();
		if (tabsheet == null) {
			tabsheet = new TabSheet();
		}
		if (!visible) {
			tabsheet.hideTabs(!visible);
		}

		this.component = tabsheet;
		group = new GroupCustomUi<>(component, this);
		return group;
	}

	/**
	 * Creates the select.
	 *
	 * @param id          the id
	 * @param function    the function
	 * @param placeholder the placeholder
	 * @return the options custom ui
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	public OptionsCustomUi createSelect(String id, Consumer<CustomFieldFilter> function, String placeholder)
			throws InstantiationException, IllegalAccessException {
		ComboBox<CustomFieldFilter> selectbox = FactoryCustomUi.createCombobox(CustomFieldFilter.class, id,
				placeholder);
		selectbox.addSelectionListener(new SingleSelectionListener<CustomFieldFilter>() {
			@Override
			public void selectionChange(SingleSelectionEvent<CustomFieldFilter> event) {
				if (function != null) {
					function.accept(event.getValue());
				}
			}
		});
		this.component = selectbox;
		optionCustomUi = new OptionsCustomUi(this);
		return optionCustomUi;
	}

	/**
	 * Creates the buttom.
	 *
	 * @param id      the id
	 * @param caption the caption
	 * @param visible the visible
	 * @param func    the func
	 * @return the custom ui
	 */
	public CustomUi<T> createButtom(String id, String caption, Boolean visible, Runnable func) {
		Button button = FactoryCustomUi.createButton(caption, id, visible);
		button.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (func != null) {
					func.run();
				}
			}
		});
		this.component = button;
		return this;
	}

	/**
	 * Builds the.
	 *
	 * @return the custom ui
	 */
	public CustomUi build() {
		if (component instanceof ComboBox) {
			((ComboBox) component).setItems(optionCustomUi.getFilters());
			((ComboBox) component).setData(optionCustomUi);
		}
		if (component instanceof TextField) {
			((TextField) component).setData(optionCustomUi);
		}
		if (tab != null) {
			tab.addComponent(component);
		} else if (layout != null && component != null) {
			layout.addComponent(component);
		}

		return this;
	}

	/**
	 * Instantiates a new custom ui.
	 *
	 * @param tab      the tab
	 * @param listener the listener
	 */
	public CustomUi(TabSheet tab, Listener listener) {
		super();
		this.tab = tab;
	}

	/**
	 * Instantiates a new custom ui.
	 *
	 * @param layout the layout
	 */
	public CustomUi(VerticalLayout layout) {
		super();
		this.layout = layout;
	}

	/**
	 * Gets the option custom ui.
	 *
	 * @return the option custom ui
	 */
	public OptionsCustomUi getOptionCustomUi() {
		return optionCustomUi;
	}

	/**
	 * Creates the option custom ui.
	 *
	 * @param optionCustomUi the option custom ui
	 * @return the options custom ui
	 */
	public OptionsCustomUi createOptionCustomUi(OptionsCustomUi optionCustomUi) {
		this.optionCustomUi = optionCustomUi;
		return optionCustomUi;
	}

	/**
	 * Gets the component.
	 *
	 * @return the component
	 */
	public com.vaadin.ui.Component getComponent() {
		return component;
	}

	/**
	 * Gets the layout.
	 *
	 * @return the layout
	 */
	public VerticalLayout getLayout() {
		return layout;
	}

	/**
	 * Clear all field text in tab.
	 *
	 * @param tabNameSearch the tab name search
	 * @param groupId       the group id
	 * @return the custom ui
	 */
	public CustomUi<T> clearAllFieldTextInTab(String tabNameSearch, String groupId) {
		for (VerticalLayout verticalLayout : getVerticalLayout(tabNameSearch, Boolean.TRUE)) {
			int cont = 0;
			int componentCount = verticalLayout.getComponentCount();
			for (int i = 0; i < componentCount; i++) {
				if (verticalLayout.getComponent(cont) instanceof TextField) {
					TextField textField = (TextField) verticalLayout.getComponent(cont);
					OptionsCustomUi option = (OptionsCustomUi) textField.getData();
					if (option != null && groupId != null && groupId.equals(option.getGroup())) {
						verticalLayout.removeComponent(verticalLayout.getComponent(cont));
					}
				} else if (verticalLayout.getComponent(cont) instanceof Upload) {
					verticalLayout.removeComponent(verticalLayout.getComponent(cont));
				}

				else {
					cont++;
				}
			}
		}

		return this;
	}

	/**
	 * Select visible/hidden a component
	 *
	 * @param tabNameSearch the tab name search
	 * @param component     the component id
	 * @param visible       visible
	 * @return the custom ui
	 */
	public CustomUi<T> setVisibilityComponent(String tabNameSearch, String component, Boolean visible) {
		for (VerticalLayout verticalLayout : getVerticalLayout(tabNameSearch, Boolean.TRUE)) {
			int componentCount = verticalLayout.getComponentCount();
			for (int i = 0; i < componentCount; i++) {
				if (verticalLayout.getComponent(i) != null && verticalLayout.getComponent(i).getId() != null
						&& verticalLayout.getComponent(i).getId().contentEquals(component)) {
					verticalLayout.getComponent(i).setEnabled(visible);
				}
			}
		}

		return this;
	}

	/**
	 * Select visible/hidden a TabSheet
	 *
	 * @param tabNameSearch the tab name search
	 * @param component     the component id
	 * @param visible       visible
	 * @return the custom ui
	 */
	public CustomUi<T> setVisibilityTabSheet(String tabNameSearch, Boolean visible) {
		getVerticalLayout(tabNameSearch, visible);
		return this;
	}

	/**
	 * Gets the vertical layout.
	 *
	 * @param tabNameSearch the tab name search
	 * @param enable        TODO
	 * @return the vertical layout
	 */
	private List<VerticalLayout> getVerticalLayout(String tabNameSearch, Boolean enable) {
		List<VerticalLayout> verticalLayouts = new ArrayList<>();
		for (Iterator<Component> iterator = getLayout().getComponentIterator(); iterator.hasNext();) {
			Component field = iterator.next();
			if (field instanceof TabSheet) {
				TabSheet childLayout = (TabSheet) field;
				childLayout.hideTabs(!enable);
				int cont = 0;
				for (Iterator<Component> j = childLayout.getComponentIterator(); j.hasNext();) {
					Tab tab = childLayout.getTab(cont);
					Component cV = j.next();
					cont++;
					if (tabNameSearch.equals(tab.getCaption())) {
						if (cV instanceof VerticalLayout) {
							VerticalLayout lay = (VerticalLayout) cV;
							verticalLayouts.add(lay);
						}
					}
				}
			}
		}
		return verticalLayouts;
	}

	/**
	 * Creates the fields from tab.
	 *
	 * @param tabName         the tab name search
	 * @param fieldsToCreate  the fields to create
	 * @param groupId         the group id
	 * @param giveMeFieldName the give me field name
	 */
	public CustomUi<CustomFieldFilter> createButtomFromTab(String tabName, String id, String label,
			Runnable giveMeFieldName) {
		List<VerticalLayout> verticalLayouts = getVerticalLayout(tabName, Boolean.TRUE);
		CustomUi<CustomFieldFilter> create = null;
		for (VerticalLayout verticalLayout : verticalLayouts) {
			create = FactoryCustomUi.create(verticalLayout, CustomFieldFilter.class);
			createButtom(id, label, true, giveMeFieldName);
		}
		return create;
	}

	/**
	 * Creates the fields from tab.
	 *
	 * @param tabNameSearch   the tab name search
	 * @param fieldsToCreate  the fields to create
	 * @param groupId         the group id
	 * @param giveMeFieldName the give me field name
	 * @param runnable        TODO
	 */
	public CustomUi<CustomFieldFilter> createFieldsFromTab(String tabNameSearch, List<Field> fieldsToCreate,
			String groupId, AdapterField giveMeFieldName, Runnable runnable) {
		List<VerticalLayout> verticalLayouts = getVerticalLayout(tabNameSearch, Boolean.TRUE);
		CustomUi<CustomFieldFilter> create = null;
		for (VerticalLayout verticalLayout : verticalLayouts) {
			create = FactoryCustomUi.create(verticalLayout, CustomFieldFilter.class);
			createTextFieldsFromList(create, fieldsToCreate, groupId, giveMeFieldName);
		}
		if (runnable != null) {
			createButtom(tabNameSearch, tabNameSearch, true, runnable);
		}

		return create;
	}

	/**
	 * Creates the text fields from list.
	 *
	 * @param tabManage       the tab manage
	 * @param fieldsToCreate  the fields to create
	 * @param groupId         the group id
	 * @param giveMeFieldName the give me field name
	 */
	public void createTextFieldsFromList(CustomUi<CustomFieldFilter> tabManage, List<Field> fieldsToCreate,
			String groupId, AdapterField giveMeFieldName) {
		for (Field field : fieldsToCreate) {
			FieldProperty fieldExtends = new FieldProperty("", field, Boolean.TRUE, "");
			String nameField = giveMeFieldName.getFieldName(fieldExtends);
			if (StringUtils.isNotEmpty(nameField) && String.class.equals(field.getType())) {
				tabManage.createTextField(nameField, nameField, "").setGroup(groupId).addFilter(nameField, "").end()
						.build();
			} else if (StringUtils.isNotEmpty(nameField) && File.class.equals(field.getType())) {
				tabManage.createFileField(nameField, nameField, "").setGroup(groupId).addFilter(nameField, "").end()
						.build();
			}
		}

	}

	/**
	 * Update fields from tab.
	 *
	 * @param tabNameSearch the tab name search
	 * @param fieldToUpdate the field to update
	 */
	public void updateFieldsFromTab(String tabNameSearch, List<FieldProperty> fieldToUpdate) {
		Map<String, FieldProperty> fieldNameUpdate = fieldToUpdate.stream()
				.collect(Collectors.toMap(FieldProperty::getFieldName, Function.identity()));

		List<VerticalLayout> verticalLayouts = getVerticalLayout(tabNameSearch, Boolean.TRUE);
		for (VerticalLayout verticalLayout : verticalLayouts) {
			for (Iterator<Component> itTextBox = verticalLayout.getComponentIterator(); itTextBox.hasNext();) {
				Component component = itTextBox.next();
				if (component instanceof TextField) {
					try {
						FieldProperty fieldProperty = fieldNameUpdate.get(((TextField) component).getId());
						if (fieldProperty != null) {
							if (fieldProperty.getValue() != null) {
								((TextField) component).setValue(fieldProperty.getValue());
							}
							if (fieldProperty.getIsVisible() != null) {
								((TextField) component).setVisible(fieldProperty.getIsVisible());
							}
						} else {
							((TextField) component).setValue("");
						}
					} catch (Exception e) {
					}
				}
				if (component instanceof Button) {
					try {
						FieldProperty fieldProperty = fieldNameUpdate.get(((Button) component).getId());
						if (fieldProperty != null) {
							if (fieldProperty.getValue() != null) {
								((Button) component).setCaption(fieldProperty.getValue());
								((Button) component).setId(fieldProperty.getValue());
							}
							if (fieldProperty.getIsVisible() != null) {
								((Button) component).setVisible(fieldProperty.getIsVisible());
							}
						}
					} catch (Exception e) {
					}
				}
			}
		}

	}
}
