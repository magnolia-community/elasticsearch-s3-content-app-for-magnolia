/*
 *
 */
package com.whitelabel.app.generic.ui.table;

import java.util.Collection;
import java.util.List;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represent a row in table which is retrieved from a source content connector app
 *
 * @param <T> the generic type
 * @see Item
 */
/**
 * Instantiates a new row item.
 *
 * @param id             the id
 * @param container      the elastic search container
 * @param itemId         the item id
 * @param obj            the obj
 * @param typeClass      the type class
 * @param itemProperties the item properties
 */
@AllArgsConstructor
@Data
public class RowItem<T> implements Item {

	/** The id. */
	String id;

	/** The item id. */
	RowId itemId;

	/** The obj. */
	T obj;

	/** The type class. */
	Class<T> typeClass;

	/** The item properties. */
	List<ColumnProperty> itemProperties;

	/**
	 * Instantiates a new row item.
	 *
	 * @param elasticSearchContainer the elastic search container
	 * @param itemId                 the item id
	 * @param itemProperties         the item properties
	 */
	public RowItem(RowId itemId, List<ColumnProperty> itemProperties) {
		super();
		this.itemId = itemId;
		this.itemProperties = itemProperties;
	}

	/**
	 * Gets the item property.
	 *
	 * @param id the id
	 * @return the item property
	 */
	@Override
	public Property<T> getItemProperty(Object id) {
		Property property = new GenericProperty<String>(String.class);
		int index = 0;
		for (int i = 0; i < itemProperties.size(); i++) {
			if (itemProperties.get(i).getPropertyId() != null && itemProperties.get(i).getPropertyId().equals(id)) {
				index = i;
				break;
			}
		}
		property = new GenericProperty<String>(itemProperties.get(index).getType());
		property.setValue(itemId.getId()[index]);
		return property;
	}

	/**
	 * Gets the item property ids.
	 *
	 * @return the item property ids
	 */
	@Override
	public Collection<?> getItemPropertyIds() {
		return null;
	}

	/**
	 * Adds the item property.
	 *
	 * @param id       the id
	 * @param property the property
	 * @return true, if successful
	 * @throws UnsupportedOperationException the unsupported operation exception
	 */
	@Override
	public boolean addItemProperty(Object id, Property property) throws UnsupportedOperationException {
		return false;
	}

	/**
	 * Removes the item property.
	 *
	 * @param id the id
	 * @return true, if successful
	 * @throws UnsupportedOperationException the unsupported operation exception
	 */
	@Override
	public boolean removeItemProperty(Object id) throws UnsupportedOperationException {
		return false;
	}

}
