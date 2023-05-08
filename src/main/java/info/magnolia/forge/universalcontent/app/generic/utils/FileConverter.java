package info.magnolia.forge.universalcontent.app.generic.utils;

import java.io.File;
import java.util.Locale;

import com.vaadin.v7.data.util.converter.Converter;

public class FileConverter implements Converter<String, File> {

	@Override
	public File convertToModel(String value, Class<? extends File> targetType, Locale locale)
			throws ConversionException {
		return null;
	}

	@Override
	public String convertToPresentation(File value, Class<? extends String> targetType, Locale locale)
			throws ConversionException {
		if (value == null) {
			return "";
		}
		return value.getName();
	}

	@Override
	public Class<File> getModelType() {
		return File.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}
}
