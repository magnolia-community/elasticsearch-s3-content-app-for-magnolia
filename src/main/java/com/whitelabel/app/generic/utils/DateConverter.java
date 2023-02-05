package com.whitelabel.app.generic.utils;

import java.util.Date;
import java.util.Locale;

import com.vaadin.v7.data.util.converter.Converter;

public class DateConverter implements Converter<String, Date> {

	@Override
	public Date convertToModel(String value, Class<? extends Date> targetType, Locale locale)
			throws ConversionException {
		return new Date();
	}

	@Override
	public String convertToPresentation(Date value, Class<? extends String> targetType, Locale locale)
			throws ConversionException {
		return value.toString();
	}

	@Override
	public Class<Date> getModelType() {
		return Date.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}
}
