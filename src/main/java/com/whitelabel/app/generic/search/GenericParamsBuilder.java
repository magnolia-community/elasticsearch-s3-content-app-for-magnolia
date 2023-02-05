/*
 *
 */
package com.whitelabel.app.generic.search;

import org.apache.commons.lang.StringUtils;

import com.whitelabel.app.generic.entity.GenericItem;
import com.whitelabel.app.generic.service.RepositoryService;
import com.whitelabel.app.generic.utils.GenericConstants;

/**
 * Builder pattern to generic params search
 */
public class GenericParamsBuilder {

	/** The params. */
	private Params params;
	private RepositoryService repositoryService;

	public GenericParamsBuilder(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	/**
	 * Creates the search.
	 *
	 * @return the builder params
	 */
	public static GenericParamsBuilder createSearch(RepositoryService repositoryService) {
		GenericParamsBuilder builder = new GenericParamsBuilder(repositoryService);
		builder.params = new Params();
		builder.params.setType(TypeParam.SEARCH);
		return builder;
	}

	/**
	 * Creates the add.
	 *
	 * @param <T>       the generic type
	 * @param classType the class type
	 * @return the builder params
	 */
	public static <T> GenericParamsBuilder createAdd(Class<? extends GenericItem> classType,
			RepositoryService repositoryService) {
		GenericParamsBuilder builder = new GenericParamsBuilder(repositoryService);
		builder.params = new Params();
		builder.params.setType(TypeParam.ADD);
		builder.addField("index", classType.getName());
		builder.params.setClassType(classType);
		return builder;
	}

	/**
	 * Params.
	 *
	 * @param paramsES the params
	 * @return the builder params
	 */
	public GenericParamsBuilder params(Params params) {
		this.params = params;
		return this;
	}

	/**
	 * Adds the field.
	 *
	 * @param field the field
	 * @param value the value
	 * @return the builder params
	 */
	public GenericParamsBuilder addField(String field, Object value) {
		if ((value instanceof String && this.params.getFields() != null && StringUtils.isNotEmpty((String) value))
				|| (this.params.getFields() != null)) {
			this.params.getFields().put(field, value);
		}
		if (GenericConstants.FILTER_INDEX.equals(field)) {
			this.params.setClassType(
					repositoryService.getConverterClass().getClassFromParams(this.params, GenericItem.class));
		}
		return this;
	}

	/**
	 * Gets the field.
	 *
	 * @param key the key
	 * @return the field
	 */
	public Object getField(String key) {
		if (this.params.getFields() != null) {
			return this.params.getFields().get(key);
		}
		return "";
	}

	/**
	 * Removes the field.
	 *
	 * @param field the field
	 * @param value the value
	 * @return the builder params
	 */
	public GenericParamsBuilder removeField(String field, String value) {
		if (this.params.getFields() != null) {
			this.params.getFields().remove(field, value);
		}
		return this;
	}

	/**
	 * Adds the order.
	 *
	 * @param field the field
	 * @param value the value
	 * @return the builder params
	 */
	public GenericParamsBuilder addOrder(String field, String value) {
		if ((value instanceof String && this.params.getOrders() != null && StringUtils.isNotEmpty(value))
				|| (this.params.getOrders() != null)) {
			this.params.getOrders().put(field, value);
		}
		return this;
	}

	/**
	 * Removes the order.
	 *
	 * @param field the field
	 * @param value the value
	 * @return the builder params
	 */
	public GenericParamsBuilder removeOrder(String field, String value) {
		if (this.params.getOrders() != null) {
			this.params.getOrders().remove(field, value);
		}
		return this;
	}

	/**
	 * Gets the order.
	 *
	 * @param field the field
	 * @return the order
	 */
	public String getOrder(String field) {
		if (this.params.getOrders() != null) {
			return this.params.getOrders().get(field);
		}
		return "";
	}

	/**
	 * Hightlight.
	 *
	 * @param highlight the highlight
	 * @return the builder params
	 */
	public GenericParamsBuilder hightlight(Boolean highlight) {
		if (this.params != null && this.params.getRelevanceSearch() != null) {
			this.params.getRelevanceSearch().setIsHighlight(highlight);
		}
		return this;
	}

	/**
	 * Gets the.
	 *
	 * @return the params
	 */
	public Params get() {
		return this.params;
	}

	/**
	 * Size page.
	 *
	 * @param sizePage the size page
	 * @return the builder params
	 */
	public GenericParamsBuilder sizePage(int sizePage) {
		if (this.params != null) {
			this.params.setSizePage(sizePage);
		}
		return this;
	}

	/**
	 * Size.
	 *
	 * @param size the size
	 * @return the builder params
	 */
	public GenericParamsBuilder size(int size) {
		if (this.params != null) {
			this.params.setSize(size);
		}
		return this;
	}

	/**
	 * Offset.
	 *
	 * @param offset the offset
	 * @return the builder params
	 */
	public GenericParamsBuilder offset(int offset) {
		if (this.params != null) {
			this.params.setOffset(offset);
		}
		return this;
	}

	/**
	 * Full text search.
	 *
	 * @param fullTextSearch the full text search
	 * @return the builder params
	 */
	public GenericParamsBuilder fullTextSearch(String fullTextSearch) {
		if (this.params != null && this.params.getRelevanceSearch() != null) {
			this.params.getRelevanceSearch().setFullTextSearch(fullTextSearch);
		}
		return this;
	}

	/**
	 * Simple query.
	 *
	 * @param isSimpleQuery the is simple query
	 * @return the builder params
	 */
	public GenericParamsBuilder simpleQuery(Boolean isSimpleQuery) {
		if (this.params != null && this.params.getRelevanceSearch() != null) {
			this.params.getRelevanceSearch().setIsSimpleQueryString(Boolean.TRUE);
		}
		return this;
	}

	/**
	 * Adds the boost field.
	 *
	 * @param boostField the boost field
	 * @return the builder params
	 */
	public GenericParamsBuilder addBoostField(BoostField boostField) {
		if (this.params != null && this.params.getRelevanceSearch() != null
				&& this.params.getRelevanceSearch().getBoostFields() != null) {
			this.params.getRelevanceSearch().getBoostFields().add(boostField);
		}
		return this;
	}

}
