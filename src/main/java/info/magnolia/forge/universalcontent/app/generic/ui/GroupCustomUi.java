/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.vaadin.event.selection.SingleSelectionEvent;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 * Represent a logical group of component on tab or in one view.
 *
 * @param <T> the generic type
 */
public class GroupCustomUi<T> {

	/** The layout. */
	private VerticalLayout layout;

	/** The component. */
	private Component component;

	/** The components. */
	private List<com.vaadin.ui.Component> components;

	/** The option custom ui. */
	private OptionsCustomUi optionCustomUi;

	/** The custom ui. */
	private CustomUi customUi;

	/** The text. */
	private String text;

	/** The id. */
	private String id;

	/**
	 * Adds the text field.
	 *
	 * @param name        the name
	 * @param label       the label
	 * @param placeholder the placeholder
	 * @return the options custom ui
	 */
	public OptionsCustomUi addTextField(String name, String label, String placeholder) {
		setOptionCustomUi();
		TextField textField = FactoryCustomUi.createTextbox(label, placeholder, name);
		this.components.add(textField);
		optionCustomUi = new OptionsCustomUi(this);
		return optionCustomUi;
	}

	/**
	 * Adds the select.
	 *
	 * @param id              the id
	 * @param function        the function
	 * @param placeholderText the placeholder text
	 * @return the group custom ui
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	public GroupCustomUi<T> addSelect(String id, Consumer<CustomFieldFilter> function, String placeholderText,
			List<String> options, String key) throws InstantiationException, IllegalAccessException {
		setOptionCustomUi();
		ComboBox<CustomFieldFilter> selectbox = FactoryCustomUi.createCombobox(CustomFieldFilter.class, id,
				placeholderText);
		selectbox.addSelectionListener(new SingleSelectionListener<CustomFieldFilter>() {
			@Override
			public void selectionChange(SingleSelectionEvent<CustomFieldFilter> event) {
				if (function != null) {
					function.accept(event.getValue());
				}
			}
		});
		this.components.add(selectbox);
		optionCustomUi = new OptionsCustomUi(this);
		for (String option : options) {
			getOptionCustomUi().addFilter(key, option);
		}
		createOptionCustomUi(getOptionCustomUi()).endGroup();
		return this;
	}

	/**
	 * Adds the label.
	 *
	 * @param caption the caption
	 * @return the group custom ui
	 */
	public GroupCustomUi<T> addLabel(String caption) {
		setOptionCustomUi();
		Label label = FactoryCustomUi.createLabeField(caption);
		this.components.add(label);
		return this;
	}

	/**
	 * Adds the buttom.
	 *
	 * @param id      the id
	 * @param caption the caption
	 * @param visible the visible
	 * @param func    the func
	 * @return the group custom ui
	 */
	public GroupCustomUi<T> addButtom(String id, String caption, Boolean visible, Runnable func) {
		setOptionCustomUi();
		Button button = FactoryCustomUi.createButton(caption, id, visible);
		button.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (func != null) {
					func.run();
				}
			}
		});
		this.components.add(button);
		return this;
	}

	/**
	 * Sets the option custom ui.
	 */
	private void setOptionCustomUi() {
		if (this.components != null && this.components.size() > 0) {
			Component c = this.components.get(this.components.size() - 1);
			if (c != null) {
				if (c instanceof ComboBox) {
					((ComboBox) c).setItems(optionCustomUi.getFilters());
					((ComboBox) c).setData(optionCustomUi);
				}
				if (c instanceof TextField) {
					((TextField) c).setData(optionCustomUi);
				}
				if (c instanceof Button) {
					((Button) c).setData(optionCustomUi);
				}
				this.components.remove(this.components.size() - 1);
				this.components.add(c);
			}
		}
	}

	/**
	 * Name tab.
	 *
	 * @param nameTab the name tab
	 * @param id      the id
	 * @return the group custom ui
	 */
	public GroupCustomUi nameTab(String nameTab, String id) {
		this.text = nameTab;
		this.id = id;
		return this;
	}

	/**
	 * Builds the.
	 *
	 * @return the custom ui
	 */
	public CustomUi<T> build() {
		setOptionCustomUi();
		VerticalLayout layoutChild = new VerticalLayout();
		for (Component c : components) {
			layoutChild.addComponent(c);
		}
		if (component instanceof TabSheet) {
			((TabSheet) component).addTab(layoutChild, text);
		}

		return customUi;
	}

	/**
	 * Instantiates a new group custom ui.
	 *
	 * @param component the component
	 * @param customUi  the custom ui
	 */
	public GroupCustomUi(Component component, CustomUi customUi) {
		super();
		this.component = component;
		this.layout = new VerticalLayout();
		this.components = new ArrayList<>();
		this.customUi = customUi;
		this.text = "";
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
	 * Gets the components.
	 *
	 * @return the components
	 */
	public List<com.vaadin.ui.Component> getComponents() {
		return components;
	}

	/**
	 * Gets the layout.
	 *
	 * @return the layout
	 */
	public VerticalLayout getLayout() {
		return layout;
	}

}
