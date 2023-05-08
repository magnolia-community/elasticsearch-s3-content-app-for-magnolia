package info.magnolia.forge.universalcontent.app.custom.interfaces;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.filter.UnsupportedFilterException;

import info.magnolia.forge.universalcontent.app.generic.others.GenericException;
import info.magnolia.forge.universalcontent.app.generic.search.Params;
import info.magnolia.forge.universalcontent.app.generic.ui.table.CustomResultSet;
import info.magnolia.forge.universalcontent.app.generic.ui.table.RowItem;

/**
 * Interface GenericDelegate.
 */
public interface GenericContentConnector {

	/**
	 * Gets the type class.
	 *
	 * @return the type class
	 */
	public Class getTypeClass();

	/**
	 * Gets the item ids.
	 *
	 * @return the item ids
	 */
	public Collection<?> getItemIds();

	/**
	 * Gets the item indexes.
	 *
	 * @return the item indexes
	 */
	public Map getItemIndexes();

	/**
	 * Adds the filter.
	 *
	 * @param filter the filter
	 * @throws UnsupportedFilterException the unsupported filter exception
	 */
	public void addFilter(Filter filter) throws UnsupportedFilterException;

	/**
	 * Removes the filter.
	 *
	 * @param filter the filter
	 */
	public void removeFilter(Filter filter);

	/**
	 * Removes the all filters.
	 */
	public void removeAllFilters();

	/**
	 * Gets the filters.
	 *
	 * @return the filters
	 */
	public Collection<Filter> getFilters();

	/**
	 * Checks for filters.
	 *
	 * @return true, if successful
	 */
	public boolean hasFilters();

	/**
	 * Adds the filter.
	 *
	 * @param propertyId      the property id
	 * @param filterString    the filter string
	 * @param ignoreCase      the ignore case
	 * @param onlyMatchPrefix the only match prefix
	 */
	public void addFilter(Object propertyId, String filterString, boolean ignoreCase, boolean onlyMatchPrefix);

	/**
	 * Removes the filters.
	 *
	 * @param propertyId the property id
	 */
	public void removeFilters(Object propertyId);

	public void sort(Object[] propertyId, boolean[] ascending);

	public Collection<?> getSortablePropertyIds();

	public boolean isLastId(Object itemId);

	public boolean isFirstId(Object itemId);

	public int indexOfId(Object itemId);

	public Object getIdByIndex(int index);

	public List<Object> getItemIds(int startIndex, int numberOfIds, Container.Indexed container);

	public Item getItem(Object itemId);

	public Object nextItemId(Object itemId);

	public Object prevItemId(Object itemId);

	public Object firstItemId();

	public Object lastItemId();

	public Object addItem() throws UnsupportedOperationException;

	public Item addItem(Object itemId) throws UnsupportedOperationException, GenericException;

	public boolean removeItem(Object itemId) throws UnsupportedOperationException;

	public boolean removeAllItems() throws UnsupportedOperationException;

	public boolean containsId(Object itemId);

	public boolean addProperty(Object propertyId, Class<?> type, Object defaultValue)
			throws UnsupportedOperationException;

	public boolean removeProperty(Object propertyId) throws UnsupportedOperationException;

	public Property getProperty(Object itemId, Object propertyId);

	public Collection<?> getPropertyIds();

	public void refreshPropertyIds();

	public Class<?> getType(Object propertyId);

	public int size();

	public void refresh();

	/**
	 * Gets the results.
	 *
	 * @param searchParams the search params
	 * @return the results
	 * @throws Exception the exception
	 */
	public CustomResultSet getResults(Params searchParams) throws Exception;

	public void itemChangeNotification(RowItem changedItem);

}
