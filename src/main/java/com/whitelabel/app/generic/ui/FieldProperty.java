/*
 *
 */
package com.whitelabel.app.generic.ui;

import java.lang.reflect.Field;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Instantiates a new field property.
 *
 * @param fieldName the field name
 * @param field     the field
 * @param isVisible the is visible
 * @param value     the value
 */
@Data
@AllArgsConstructor
public class FieldProperty {

	/** The field name. */
	private String fieldName;

	/** The field. */
	private Field field;

	/** The is visible. */
	private Boolean isVisible;

	/** The value. */
	private String value;
}
