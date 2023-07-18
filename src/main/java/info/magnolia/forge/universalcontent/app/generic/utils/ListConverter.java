package info.magnolia.forge.universalcontent.app.generic.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.vaadin.data.Result;
import com.vaadin.v7.data.util.converter.Converter;

public class ListConverter implements Converter<String, List> {

	@Override
	public List convertToModel(String value, Class<? extends List> targetType, Locale locale)
			throws ConversionException {
		return new ArrayList();
	}

	@Override
	public String convertToPresentation(List value, Class<? extends String> targetType, Locale locale)
			throws ConversionException {
		if (value == null || value.isEmpty()) {
			return Result.ok("").toString();
		}
		String joinedString = String.join(", ", value);
		return Result.ok(joinedString).toString();
	}

	@Override
	public Class<List> getModelType() {
		return List.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
