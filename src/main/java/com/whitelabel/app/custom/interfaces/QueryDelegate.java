/*
 *
 */
package com.whitelabel.app.custom.interfaces;

import java.io.IOException;
import java.util.List;

import com.vaadin.v7.data.Container.Filter;
import com.whitelabel.app.generic.entity.GenericItem;
import com.whitelabel.app.generic.entity.ItemDelegate;
import com.whitelabel.app.generic.others.GenericException;
import com.whitelabel.app.generic.search.Params;
import com.whitelabel.app.generic.service.RepositoryService;
import com.whitelabel.app.generic.ui.table.CustomResultSet;
import com.whitelabel.app.generic.ui.table.OrderBy;
import com.whitelabel.app.generic.ui.table.RowItem;

/**
 * Delegate manage connection and operation within generic instance.
 *
 * @param <T> the generic type
 */
public interface QueryDelegate<T extends GenericItem> {

	/**
	 * Checks for index.
	 *
	 * @param key the field name
	 * @return the boolean
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Boolean hasExecuteSetup(String key) throws GenericException;

	/**
	 * Gets the all entity index.
	 *
	 * @return the all entity index
	 */
	public List<Class<? extends GenericItem>> getAllTypeItems();

	/**
	 * Adds the index.
	 *
	 * @param key       the field name
	 * @param typeClass the type class
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void setup(String key, Class typeClass) throws GenericException;

	/**
	 * Size
	 *
	 * @return the count
	 * @throws Exception the exception
	 */
	public int size() throws Exception;

	/**
	 * Implementation respects paging limits.
	 *
	 * @return true, if successful
	 */
	public boolean implementationRespectsPagingLimits();

	/**
	 * Sets the filters.
	 *
	 * @param filters the new filters
	 * @throws UnsupportedOperationException the unsupported operation exception
	 */
	public void setFilters(List<Filter> filters) throws UnsupportedOperationException;

	/**
	 * Gets the primary key columns.
	 *
	 * @return the primary key columns
	 * @throws GenericException the elastic search exception
	 */
	public List<String> getPropertyColumns() throws GenericException;

	/**
	 * Contains row with key.
	 *
	 * @param keys the keys
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	public boolean containsRowWithKey(Object... keys) throws Exception;

	/**
	 * Sets the order by.
	 *
	 * @param orderBys the new order by
	 * @throws UnsupportedOperationException the unsupported operation exception
	 */
	void setOrderBy(List<OrderBy> orderBys) throws UnsupportedOperationException;

	public CustomResultSet search(Params searchParams) throws IOException, GenericException;

	public void setTypeParameterClass(Class<? extends GenericItem> typeParameterClass);

	public ItemDelegate<? extends GenericItem> addItem(ItemDelegate<? extends GenericItem> item)
			throws GenericException;

	public void setServiceContainer(RepositoryService serviceContainer);

	public void itemChangeNotification(RowItem changedItem);

	public CustomConnection getConnection();

	void setConnection(CustomConnection customConnection);

}
