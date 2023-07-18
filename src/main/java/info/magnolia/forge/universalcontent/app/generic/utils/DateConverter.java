package info.magnolia.forge.universalcontent.app.generic.utils;

import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.time.FastDateFormat;

import com.vaadin.v7.data.util.converter.Converter;

import info.magnolia.forge.universalcontent.elasticsearch.utils.ElasticsearchConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

    public static String formatDate(Date date) {
	try {
	    return FastDateFormat.getInstance(ElasticsearchConstants.DATE_FORMAT).format(date);

	} catch (Exception e) {
	    log.error("Error formatting input date '{}'", date.toString());
	    return null;
	}
    }
}
