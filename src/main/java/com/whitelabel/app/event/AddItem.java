/*
 *
 */
package com.whitelabel.app.event;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Event AddItem on generic item
 *
 * @param <T> the generic type
 */
@Data
@AllArgsConstructor
public class AddItem<T> implements Serializable {

	/** The obj. */
	T obj;

	/** The type class. */
	Class typeClass;

}
