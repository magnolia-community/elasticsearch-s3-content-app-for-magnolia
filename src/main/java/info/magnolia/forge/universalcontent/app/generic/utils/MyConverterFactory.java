package info.magnolia.forge.universalcontent.app.generic.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.v7.data.util.converter.Converter;
import com.vaadin.v7.data.util.converter.DefaultConverterFactory;

public class MyConverterFactory extends DefaultConverterFactory {
	private static final Logger log = LoggerFactory.getLogger(MyConverterFactory.class);

	@Override
	protected <PRESENTATION, MODEL> Converter<PRESENTATION, MODEL> findConverter(Class<PRESENTATION> presentationType,
			Class<MODEL> modelType) {
		if (presentationType == String.class && modelType == Date.class) {
			return (Converter<PRESENTATION, MODEL>) new DateConverter();
		}
		if (presentationType == String.class && modelType == File.class) {
			return (Converter<PRESENTATION, MODEL>) new FileConverter();
		}
		log.error(modelType.getName() + " " + presentationType.getName());
		if (presentationType == String.class && modelType == ArrayList.class) {
			return (Converter<PRESENTATION, MODEL>) new ArrayListConverter();
		}
		if (presentationType == String.class && modelType == List.class) {
			return (Converter<PRESENTATION, MODEL>) new ListConverter();
		}
		// Let default factory handle the rest
		return super.findConverter(presentationType, modelType);
	}
}
