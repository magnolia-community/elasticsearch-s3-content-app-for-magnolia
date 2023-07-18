/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.utils;

import java.io.File;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import info.magnolia.forge.universalcontent.app.elasticsearch.ElasticsearchQueryFactory;
import info.magnolia.forge.universalcontent.app.generic.annotation.Format;
import info.magnolia.forge.universalcontent.app.generic.entity.GenericItem;
import info.magnolia.forge.universalcontent.app.generic.search.Params;
import info.magnolia.forge.universalcontent.app.generic.service.RepositoryService;
import info.magnolia.forge.universalcontent.elasticsearch.beans.SearchRequest;
import info.magnolia.forge.universalcontent.elasticsearch.search.entity.PaginationModel;
import lombok.Getter;

/**
 * Factory manage Json: entity and entity: Json conversion.
 */
public class FactoryConverter {

	/** The builder to string. */
	GsonBuilder builderToString;

	/** The builder to params search. */
	Gson builderToParamsSearch;

	@Getter
	ModelMapper modelMapper;

	@Getter
	@Inject
	RepositoryService serviceRepository;

	/**
	 * Instantiates a new factory converter.
	 */
	@Inject
	public FactoryConverter(RepositoryService serviceRepository) {
		builderToString = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().registerTypeAdapter(String.class,
				new EmptyStringSerializer());
		builderToParamsSearch = new Gson();
		modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
		this.serviceRepository = serviceRepository;

	}

	/**
	 * Convert.
	 *
	 * @param params the params
	 * @return the string
	 */
	public String convert(Params params) {
		String fullText = "";
		if (params == null) {
			return fullText;
		}
		fullText = builderToString.create().toJson(params);
		return fullText;

	}

	/**
	 * Convert.
	 *
	 * @param params the params
	 * @return the params ES
	 */
	public Params convert(String params) {
		if (params == null) {
			return new Params();
		}

		if (serviceRepository.getCacheHelper().containsFactoryConvertKey(params)) {
			return serviceRepository.getCacheHelper().getFactoryConvert(params);
		} else {
			Params paramsSearch = builderToParamsSearch.fromJson(params, Params.class);
			if (paramsSearch == null) {
				paramsSearch = new Params();
			}
			JsonObject jsonObject = new JsonParser().parse(params).getAsJsonObject();

			if (jsonObject != null && jsonObject.get("fields") != null
					&& jsonObject.get("fields").getAsJsonObject() != null
					&& jsonObject.get("fields").getAsJsonObject().get("index") != null) {
				paramsSearch.getFields().put(GenericConstants.INDEX_ID,
						jsonObject.get("fields").getAsJsonObject().get("index").getAsString());
				paramsSearch.setClassType(serviceRepository.getConverterClass().getClassFromClassName(
						jsonObject.get("fields").getAsJsonObject().get("index").getAsString(), GenericItem.class));
			}
			if (serviceRepository.getCustomContainer() != null
					&& serviceRepository.getCustomContainer().getContentConnector() != null
					&& serviceRepository.getCustomContainer().getContentConnector().get() != null
					&& serviceRepository.getCustomContainer().getContentConnector().get().getPropertyIds() != null) {
				Collection<?> propertyIds = serviceRepository.getCustomContainer().getContentConnector().get()
						.getPropertyIds();
				List<String> properties = propertyIds.stream().map(property -> {
					return (String) property;
				}).collect(Collectors.toList());
				SearchRequest searchRequest = ElasticsearchQueryFactory
						.configuration(serviceRepository.getElasticSearchModule().getConfiguration())
						.propertyColumns(properties).pagination(new PaginationModel()).params(paramsSearch).build();
				paramsSearch.setSearchRequest(searchRequest);
			}

			serviceRepository.getCacheHelper().putFactoryConvert(paramsSearch.toString(), paramsSearch);
			return paramsSearch;
		}
	}

	/**
	 * Convert to object.
	 *
	 * @param <T>       the generic type
	 * @param o         the o
	 * @param classType the class type
	 * @param nameField the name field
	 * @return the object
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	public <T> Object convertToObject(Object o, Class<T> classType, Field nameField)
			throws InstantiationException, IllegalAccessException {
		if (o instanceof File) {
			return o;
		}
		if (classType.newInstance() instanceof Date) {
			String regexFormatter = nameField.getAnnotation(Format.class).formatter();
			SimpleDateFormat formatter = new SimpleDateFormat(regexFormatter);
			Date date = null;
			try {
				date = formatter.parse((String) o);
			} catch (ParseException e) {
				return new Date();
			}
			return date;
		}
		return o;
	}

}
