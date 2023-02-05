/*
 *
 */

package com.whitelabel.app.generic.format;

import javax.inject.Inject;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.Table;

import info.magnolia.ui.workbench.column.AbstractColumnFormatter;
import info.magnolia.ui.workbench.column.definition.PropertyColumnDefinition;

/**
 * The Class PlainCellColumnFormatter.
 */
public class PlainCellColumnFormatter extends AbstractColumnFormatter<PropertyColumnDefinition> {

	@Inject
	public PlainCellColumnFormatter(PropertyColumnDefinition definition) {
		super(definition);
	}

	/**
	 * Generate cell.
	 *
	 * @param source   the source
	 * @param itemId   the item id
	 * @param columnId the column id
	 * @return the object
	 */
	@Override
	public Object generateCell(Table source, Object itemId, Object columnId) {
		Item item = source.getItem(itemId);
		Property prop = (item == null) ? null : item.getItemProperty(columnId);
		if (prop != null && prop.getValue() != null) {
			String plainNumber = prop.getValue().toString();
			return plainNumber;
		}
		return null;
	}

}
