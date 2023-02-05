/*
 *
 */
package com.whitelabel.app.generic.format;

import java.util.Optional;

import com.vaadin.v7.data.validator.IntegerValidator;

/**
 * The Class CustomIntegerValidator.
 */
@SuppressWarnings("serial")
public class CustomIntegerValidator extends IntegerValidator {

	/**
	 * Instantiates a new custom integer validator.
	 *
	 * @param errorMessage the error message
	 */
	public CustomIntegerValidator(String errorMessage) {
		super(errorMessage);
	}

	/**
	 * Checks if is valid value.
	 *
	 * @param value the value
	 * @return true, if is valid value
	 */
	@Override
	protected boolean isValidValue(String value) {
		return Optional.ofNullable(value).filter(v -> !v.isEmpty()).map(v -> {
			try {
				Integer.parseInt(v);
				return true;
			} catch (Exception e) {
				return false;
			}
		}).orElse(Boolean.TRUE);
	}

}