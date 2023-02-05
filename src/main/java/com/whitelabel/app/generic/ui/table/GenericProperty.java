/*
 *
 */
package com.whitelabel.app.generic.ui.table;

import com.vaadin.v7.data.Property;

/**
 * Define one Property of item source content app entity.
 *
 * @param <T> the generic type
 */
public class GenericProperty<T> implements Property<T> {

	/** The value. */
	T value;

	/** The type class. */
	Class<?> typeClass;

	/** The is read only. */
	Boolean isReadOnly = true;

	/**
	 * Instantiates a new property ES.
	 *
	 * @param typeClass the type class
	 */
	public GenericProperty(Class<?> typeClass) {
		super();
		this.typeClass = typeClass;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	@Override
	public T getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param newValue the new value
	 * @throws ReadOnlyException the read only exception
	 */
	@Override
	public void setValue(Object newValue) throws ReadOnlyException {
		this.value = (T) newValue;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	@Override
	public Class getType() {
		return typeClass;
	}

	/**
	 * Checks if is read only.
	 *
	 * @return true, if is read only
	 */
	@Override
	public boolean isReadOnly() {
		return this.isReadOnly;
	}

	/**
	 * Sets the read only.
	 *
	 * @param newStatus the new read only
	 */
	@Override
	public void setReadOnly(boolean newStatus) {
		this.isReadOnly = newStatus;
	}
}
