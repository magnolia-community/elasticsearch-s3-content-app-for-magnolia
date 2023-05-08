/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.ui;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;

import lombok.Data;

/**
 * Represent options of UI.
 *
 * @see CustomUI, GroupCustomUi
 */
@Data
public class OptionsCustomUi {

	/** The filters. */
	private List<CustomFieldFilter> filters;

	/** The group. */
	private String group;

	/** The custom UI. */
	private CustomUi customUI;

	/** The group custom ui. */
	private GroupCustomUi groupCustomUi;

	/**
	 * Instantiates a new options custom ui.
	 */
	public OptionsCustomUi() {
		filters = new ArrayList<>();
		this.groupCustomUi = null;
	}

	/**
	 * Instantiates a new options custom ui.
	 *
	 * @param customUI the custom UI
	 */
	public OptionsCustomUi(CustomUi customUI) {
		filters = new ArrayList<>();
		this.customUI = customUI;
		this.groupCustomUi = null;
	}

	/**
	 * Instantiates a new options custom ui.
	 *
	 * @param groupCustomUi the group custom ui
	 */
	public OptionsCustomUi(GroupCustomUi groupCustomUi) {
		filters = new ArrayList<>();
		this.groupCustomUi = groupCustomUi;
		this.customUI = null;
	}

	/**
	 * Adds the filter.
	 *
	 * @param key   the key
	 * @param label the label
	 * @return the options custom ui
	 */
	public OptionsCustomUi addFilter(String key, String label) {
		filters.add(new CustomFieldFilter(label, key));
		return this;
	}

	/**
	 * Sets the group.
	 *
	 * @param groupId the group id
	 * @return the options custom ui
	 */
	public OptionsCustomUi setGroup(String groupId) {
		this.group = groupId;
		return this;
	}

	/**
	 * End.
	 *
	 * @return the custom ui
	 */
	public CustomUi end() {
		return customUI;
	}

	/**
	 * End group.
	 *
	 * @return the group custom ui
	 */
	public GroupCustomUi endGroup() {
		return groupCustomUi;
	}

	/**
	 * Convert.
	 *
	 * @param component the component
	 * @return the options custom ui
	 */
	public static OptionsCustomUi convert(Component component) {
		if (component instanceof TextField) {
			return (OptionsCustomUi) ((TextField) component).getData();
		}
		if (component instanceof ComboBox) {
			return (OptionsCustomUi) ((ComboBox) component).getData();
		}
		return null;
	}

}
