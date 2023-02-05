/*
 *
 */
package com.whitelabel.app.generic.ui;

/**
 * The Interface AdapterField.
 */
@FunctionalInterface
public interface AdapterField {

	/**
	 * Gets the field name.
	 *
	 * @param field the field
	 * @return the field name
	 */
	public String getFieldName(FieldProperty field);
}
