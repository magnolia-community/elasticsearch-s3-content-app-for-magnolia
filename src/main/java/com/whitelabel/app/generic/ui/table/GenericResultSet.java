/*
 *
 */
package com.whitelabel.app.generic.ui.table;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.whitelabel.app.generic.annotation.GenericEntity;
import com.whitelabel.app.generic.entity.Field;
import com.whitelabel.app.generic.entity.GenericItem;
import com.whitelabel.app.generic.others.GenericException;
import com.whitelabel.app.generic.search.Params;
import com.whitelabel.app.generic.search.ResultContainer;
import com.whitelabel.app.generic.service.RepositoryService;

import lombok.Getter;
import lombok.Setter;

/**
 * Represent a resultSet from a query's source content connector app
 *
 * @param <T> the generic type
 */

public class GenericResultSet<T extends GenericItem> implements CustomResultSet {

	/** Result meta. */
	GenericResultMeta resultMeta;

	/** The results. */
	List<T> results;

	/** The row count. */
	private int rowCount = -1;

	/** The column. */
	private int column;

	/** The type class. */
	Class<? extends GenericItem> typeClass;

	private Params paramsSearch;
	@Getter
	@Setter
	private ResultContainer resultContainer;

	@Getter
	@Setter
	private Long totalSize;

	/**
	 * Instantiates a new elastic search result set.
	 *
	 * @param typeClass        the type class
	 * @param serviceContainer TODO
	 */
	public GenericResultSet(Class<? extends GenericItem> typeClass, RepositoryService serviceContainer) {
		super();
		if (typeClass != null) {
			this.typeClass = typeClass;
			this.results = new ArrayList<T>();
			GenericEntity index = typeClass.getAnnotation(GenericEntity.class);
			if (index != null && StringUtils.isNotEmpty(index.name())) {
				this.resultMeta = new GenericResultMeta(typeClass, serviceContainer);
			}
		}
		resultContainer = new ResultContainer();
		this.paramsSearch = new Params();

	}

	/**
	 * Next.
	 *
	 * @return true, if successful
	 * @throws GenericException the elastic search exception
	 */
	@Override
	public boolean next() throws GenericException {
		if (results.size() == 0) {
			return false;
		}

		if (rowCount <= results.size()) {
			rowCount++;
			column = 0;
		}
		Boolean next = rowCount < results.size();
		if (!next) {
			rowCount = -1;
			column = 0;
		}
		return next;
	}

	/**
	 * Gets the meta data.
	 *
	 * @return the meta data
	 * @throws GenericException the elastic search exception
	 */
	@Override
	public GenericResultMeta<T> getMetaData() throws GenericException {
		return resultMeta;
	}

	/**
	 * Gets the object.
	 *
	 * @param columnIndex the column index
	 * @return the object
	 * @throws GenericException the elastic search exception
	 */
	@Override
	public Object getObject(int columnIndex) throws GenericException {

		try {
			T obj = results.get(rowCount);
			Field field = getMetaData().fieldsName.get(columnIndex);
			java.lang.reflect.Field reflectField = typeClass.getDeclaredField(field.getName());
			reflectField.setAccessible(true);
			return reflectField.get(obj);
		} catch (Exception e) {
			return "";
		}

	}

	/**
	 * Gets the object.
	 *
	 * @param columnLabel the column label
	 * @return the object
	 * @throws GenericException the elastic search exception
	 */
	@Override
	public Object getObject(String columnLabel) throws GenericException {
		try {
			T obj = results.get(rowCount);
			GenericEntity index = typeClass.getAnnotation(GenericEntity.class);
			Object value = null;
			if (index.fieldId() != null && index.fieldId().equalsIgnoreCase(columnLabel)) {
//				Get id
				T hit = results.get(rowCount);

				java.lang.reflect.Field fieldID = hit.getClass().getDeclaredField(index.fieldId());
				fieldID.setAccessible(true);
				value = fieldID.get(hit);
			} else {
//				Get value from columnLabel
				java.lang.reflect.Field field = obj.getClass().getDeclaredField(columnLabel);
				field.setAccessible(true);
				value = field.get(obj);
			}
			return value;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return null;
		} catch (NoSuchFieldException e) {
			return null;
		} catch (SecurityException e) {
			return null;
		}
	}

	/**
	 * First.
	 *
	 * @return true, if successful
	 * @throws GenericException the elastic search exception
	 */
	@Override
	public boolean first() throws GenericException {
		return rowCount == 0;
	}

	/**
	 * Last.
	 *
	 * @return true, if successful
	 * @throws GenericException the elastic search exception
	 */
	@Override
	public boolean last() throws GenericException {
		return results.size() == rowCount;
	}

	/**
	 * Gets the row.
	 *
	 * @return the row
	 * @throws GenericException the elastic search exception
	 */
	@Override
	public int getRow() throws GenericException {
		column = 0;
		return rowCount;
	}

	@Override
	public List getResults() throws GenericException {
		return this.results;
	}

	@Override
	public void setResults(List results) {
		this.results = results;

	}

	public Params getParamsSearch() {
		return paramsSearch;
	}

	@Override
	public void setParamsSearch(Params paramsSearch) {
		this.paramsSearch = paramsSearch;
	}

	@Override
	public String toString() {
		return "GenericResultSet [resultMeta=" + resultMeta + ", results=" + results + ", rowCount=" + rowCount
				+ ", column=" + column + ", typeClass=" + typeClass + ", paramsSearch=" + paramsSearch + "]";
	}

	@Override
	public void setMetaData(GenericResultMeta resultSetMetaData) throws GenericException {
		this.resultMeta = resultSetMetaData;

	}

}