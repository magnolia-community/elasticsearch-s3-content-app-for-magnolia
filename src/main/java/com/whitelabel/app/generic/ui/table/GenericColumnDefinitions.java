/*
 *
 */
package com.whitelabel.app.generic.ui.table;

import info.magnolia.ui.workbench.column.definition.ColumnAvailabilityRule;
import info.magnolia.ui.workbench.column.definition.ColumnDefinition;
import info.magnolia.ui.workbench.column.definition.ColumnFormatter;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Custom Column Definitions with all properties (name, label on the table, is
 * sortable, searchable ...)
 *
 * @see ColumnDefinition
 *
 */

/**
 * Instantiates a new custom column definitions.
 *
 * @param name                  the name
 * @param propertyName          the property name
 * @param label                 the label
 * @param width                 the width
 * @param expandRatio           the expand ratio
 * @param formatterClass        the formatter class
 * @param type                  the type
 * @param ruleClass             the rule class
 * @param sortable              the sortable
 * @param searchable            the searchable
 * @param enabled               the enabled
 * @param editable              the editable
 * @param displayInChooseDialog the display in choose dialog
 */
@AllArgsConstructor
@Data
@SuppressWarnings("deprecation")
public class GenericColumnDefinitions implements ColumnDefinition {

	/** The name. */
	private String name;

	/** The property name. */
	private String propertyName;

	/** The label. */
	private String label;

	/** The width. */
	private int width;

	/** The expand ratio. */
	private float expandRatio;

	/** The formatter class. */
	private Class<? extends ColumnFormatter> formatterClass;

	/** The type. */
	private Class<?> type;

	/** The rule class. */
	private Class<? extends ColumnAvailabilityRule> ruleClass;

	/** The sortable. */
	private boolean sortable;

	/** The searchable. */
	private boolean searchable;

	/** The enabled. */
	private boolean enabled;

	/** The editable. */
	private boolean editable;

	/** The display in choose dialog. */
	private boolean displayInChooseDialog;

	/**
	 * Checks if is sortable.
	 *
	 * @return true, if is sortable
	 */
	@Override
	public boolean isSortable() {
		return sortable;
	}

	/**
	 * Checks if is display in choose dialog.
	 *
	 * @return true, if is display in choose dialog
	 */
	@Override
	public boolean isDisplayInChooseDialog() {
		return displayInChooseDialog;
	}

	/**
	 * Checks if is searchable.
	 *
	 * @return true, if is searchable
	 */
	@Override
	public boolean isSearchable() {
		return searchable;
	}

	/**
	 * Checks if is editable.
	 *
	 * @return true, if is editable
	 */
	@Override
	public boolean isEditable() {
		return editable;
	}

	/**
	 * Checks if is enabled.
	 *
	 * @return true, if is enabled
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
}
