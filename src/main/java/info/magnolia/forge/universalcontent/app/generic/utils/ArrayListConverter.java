package info.magnolia.forge.universalcontent.app.generic.utils;

import java.util.ArrayList;
import java.util.Locale;

import com.vaadin.data.Result;
import com.vaadin.v7.data.util.converter.Converter;

public class ArrayListConverter implements Converter<String, ArrayList> {

	@Override
	public ArrayList convertToModel(String value, Class<? extends ArrayList> targetType, Locale locale)
			throws ConversionException {
		return new ArrayList();
	}

	@Override
	public String convertToPresentation(ArrayList value, Class<? extends String> targetType, Locale locale)
			throws ConversionException {
		if (value == null || value.isEmpty()) {
			return Result.ok("").toString();
		}
		String joinedString = String.join(", ", value);
		return Result.ok(joinedString).toString();
	}

	@Override
	public Class<ArrayList> getModelType() {
		return ArrayList.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
