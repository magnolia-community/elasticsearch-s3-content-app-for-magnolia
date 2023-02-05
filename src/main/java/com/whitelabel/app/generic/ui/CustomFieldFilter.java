/*
 *
 */
package com.whitelabel.app.generic.ui;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomFieldFilter implements Serializable {

	/** The label. */
	public Object value;

	/** The field. */
	public String field;

	@Override
	public String toString() {
		return value.toString();
	}

}
