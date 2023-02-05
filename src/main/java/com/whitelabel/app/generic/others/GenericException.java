/*
 *
 */
package com.whitelabel.app.generic.others;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Custom exception thrown in query manager.
 */
@Data
@AllArgsConstructor
public class GenericException extends Exception {

	/** The message. */
	private String message;

}
