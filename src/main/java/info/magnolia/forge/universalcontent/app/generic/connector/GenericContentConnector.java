/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.connector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.filter.Like;
import com.vaadin.v7.data.util.filter.UnsupportedFilterException;

import info.magnolia.context.MgnlContext;
import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomContentConnector;
import info.magnolia.forge.universalcontent.app.event.RowIdChangeListener;
import info.magnolia.forge.universalcontent.app.event.RowIdChangeNotifier;
import info.magnolia.forge.universalcontent.app.generic.annotation.GenericEntity;
import info.magnolia.forge.universalcontent.app.generic.entity.GenericItem;
import info.magnolia.forge.universalcontent.app.generic.entity.ItemDelegate;
import info.magnolia.forge.universalcontent.app.generic.others.GenericException;
import info.magnolia.forge.universalcontent.app.generic.others.LogStatus;
import info.magnolia.forge.universalcontent.app.generic.search.Params;
import info.magnolia.forge.universalcontent.app.generic.service.RepositoryService;
import info.magnolia.forge.universalcontent.app.generic.ui.service.GenericContentConnectorDefinition;
import info.magnolia.forge.universalcontent.app.generic.ui.table.ColumnProperty;
import info.magnolia.forge.universalcontent.app.generic.ui.table.CustomResultSet;
import info.magnolia.forge.universalcontent.app.generic.ui.table.GenericResultMeta;
import info.magnolia.forge.universalcontent.app.generic.ui.table.OrderBy;
import info.magnolia.forge.universalcontent.app.generic.ui.table.RowId;
import info.magnolia.forge.universalcontent.app.generic.ui.table.RowItem;
import info.magnolia.forge.universalcontent.app.generic.ui.table.TemporaryRowId;
import info.magnolia.forge.universalcontent.app.generic.utils.GenericConstants;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.vaadin.integration.contentconnector.AbstractContentConnector;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnectorDefinition;
import lombok.Getter;

/**
 * Container represent elastic search connector for manage all operations within
 * generic instance Must configure queryCustomDelegate.
 *
 * @param <T> the generic type
 */
public class GenericContentConnector<T extends GenericItem> extends AbstractContentConnector
		implements CustomContentConnector {

	private GenericDelegate<T> queryDelegate;

	/** The property ids. */
	private final List<String> propertyIds = new ArrayList<String>();

	/** The property types. */
	private final Map<String, Class<?>> propertyTypes = new HashMap<String, Class<?>>();

	/** The filters. */
	private final List<Filter> filters = new ArrayList<Filter>();

	/** The sorters. */
	private final List<OrderBy> sorters = new ArrayList<OrderBy>();

	/** The item set change listeners. */
	private LinkedList<Container.ItemSetChangeListener> itemSetChangeListeners;

	/** The type class. */
	private Class<? extends GenericItem> typeClass;

	private Params params;

	@Inject
	private RepositoryService serviceContainer;

	@Getter
	private CustomResultSet resultset;

	public GenericContentConnector(ContentConnectorDefinition contentConnectorDefinition) {
		super(contentConnectorDefinition);
	}

	/**
	 * Instantiates a generic container.
	 *
	 * @param pool             the pool
	 * @param typeClass        the type class
	 * @param factoryConverter the factory converter
	 * @throws GenericException
	 */
	@Inject
	public GenericContentConnector(GenericContentConnectorDefinition definition, ComponentProvider componentProvider)
			throws GenericException {
		super(definition, componentProvider);
		this.params = new Params();
		getPropertyIds();
	}

	/**
	 * Adds the item.
	 *
	 * @return the object
	 * @throws UnsupportedOperationException the unsupported operation exception
	 */
	@Override
	public Object addItem() throws UnsupportedOperationException {
		Object[] emptyKey;
		RowId itemId = null;
		try {
			emptyKey = new Object[queryDelegate.getPropertyColumns().size()];
			itemId = new TemporaryRowId(emptyKey);
		} catch (GenericException e1) {
			e1.printStackTrace();
		}

		// Create new empty column properties for the row item.
		List<ColumnProperty> itemProperties = new ArrayList<ColumnProperty>();
		for (String propertyId : propertyIds) {
			/* Default settings for new item properties. */
			ColumnProperty cp = new ColumnProperty(propertyId, false, true, true, true, null, getType(propertyId),
					serviceContainer);

			itemProperties.add(cp);
		}
		RowItem newRowItem = new RowItem(itemId, itemProperties);
		serviceContainer.getCacheHelper().putCachedItems(itemId, newRowItem);
		return itemId;

	}

	/**
	 * Contains id.
	 *
	 * @param itemId the item id
	 * @return true, if successful
	 */
	@Override
	public boolean containsId(Object itemId) {
		if (queryDelegate == null || itemId == null) {
			return false;
		}

		if (itemId instanceof RowId && !(itemId instanceof TemporaryRowId)) {
			try {
				return queryDelegate.containsRowWithKey(((RowId) itemId).getId());
			} catch (Exception e) {
				/* Query failed, just return false. */
				serviceContainer.getLogService().logger(LogStatus.ERROR, "containsId query failed", this.getClass(), e);
			}
		}
		return false;
	}

	/**
	 * Gets the container property.
	 *
	 * @param itemId     the item id
	 * @param propertyId the property id
	 * @return the container property
	 */
	@Override
	public Property getProperty(Object itemId, Object propertyId) {
		Item item = getItem(itemId);
		if (item == null) {
			return null;
		}
		return item.getItemProperty(propertyId);
	}

	/**
	 * Gets the container property ids.
	 *
	 * @return the container property ids
	 */
	@Override
	public Collection<?> getPropertyIds() {
		return Collections.unmodifiableCollection(propertyIds);
	}

	/**
	 * Gets the item.
	 *
	 * @param itemId the item id
	 * @return the item
	 */
	@Override
	public Item getItem(Object itemId) {
		if (!serviceContainer.getCacheHelper().containsItemsKey((RowId) itemId)) {
			int index = indexOfId(itemId);
			Item item = resultset.getResultContainer().getResultIndexes().get(itemId);
			serviceContainer.getCacheHelper().putCachedItems((RowId) itemId, (RowItem) item);
		}
		return serviceContainer.getCacheHelper().getCachedItems((RowId) itemId);
	}

	/**
	 * Gets the item ids.
	 *
	 * @return the item ids
	 */
	@Override
	public Collection<?> getItemIds() {
		List<RowId> ids = new ArrayList<RowId>();
		if (queryDelegate == null) {
			return Collections.unmodifiableCollection(ids);
		}
		updateCount();

		try {
			String stringParams = serviceContainer.getFactoryConverter().convert(params);

			if (!serviceContainer.getCacheHelper().containsCachedResultsKey(stringParams)) {
				resultset = queryDelegate.search(params);
				serviceContainer.getCacheHelper().putCachedResults(stringParams, resultset);
			} else {
				resultset = serviceContainer.getCacheHelper().getCachedResults(stringParams);
			}
			if (resultset != null && resultset.getResultContainer() != null) {
				ids = resultset.getResultContainer().getIds();
			}
		} catch (Exception e) {
			serviceContainer.getLogService().logger(LogStatus.ERROR, "getItemIds() failed, rolling back.",
					this.getClass(), e);
			throw new RuntimeException("Failed to fetch item indexes.", e);
		}

		return Collections.unmodifiableCollection(ids);
	}

	/**
	 * Gets the type.
	 *
	 * @param propertyId the property id
	 * @return the type
	 */
	@Override
	public Class<?> getType(Object propertyId) {
		if (!propertyIds.contains(propertyId)) {
			return null;
		}
		return propertyTypes.get(propertyId);
	}

	/**
	 * Size.
	 *
	 * @return the int
	 */
	@Override
	public int size() {
		return updateCount();
	}

	/**
	 * Removes the item.
	 *
	 * @param itemId the item id
	 * @return true, if successful
	 * @throws UnsupportedOperationException the unsupported operation exception
	 */
	@Override
	public boolean removeItem(Object itemId) throws UnsupportedOperationException {
		if (!containsId(itemId)) {
			return false;
		}
		serviceContainer.getCacheHelper().removeCachedItems(((RowId) itemId));
		refresh();
		return true;

	}

	/**
	 * Removes the all items.
	 *
	 * @return true, if successful
	 * @throws UnsupportedOperationException the unsupported operation exception
	 */
	@Override
	public boolean removeAllItems() throws UnsupportedOperationException {
		for (Object id : getItemIds()) {
			serviceContainer.getCacheHelper().removeCachedItems((RowId) id);
		}
		refresh();
		return true;
	}

	/**
	 * Adds filter.
	 *
	 * @param filter the filter
	 * @throws UnsupportedFilterException the unsupported filter exception
	 */
	@Override
	public void addFilter(Filter filter) throws UnsupportedFilterException {
		filters.add(filter);
		refresh();
	}

	/**
	 * Removes filter.
	 *
	 * @param filter the filter
	 */
	@Override
	public void removeFilter(Filter filter) {
		filters.remove(filter);
		refresh();
	}

	/**
	 * Adds filter.
	 *
	 * @param propertyId      the property id
	 * @param filterString    the filter string
	 * @param ignoreCase      the ignore case
	 * @param onlyMatchPrefix the only match prefix
	 */
	@Override
	public void addFilter(Object propertyId, String filterString, boolean ignoreCase, boolean onlyMatchPrefix) {
		if (propertyId == null || !propertyIds.contains(propertyId)) {
			return;
		}

		/* Generate Filter -object */
		String likeStr = onlyMatchPrefix ? filterString + "%" : "%" + filterString + "%";
		Like like = new Like(propertyId.toString(), likeStr);
		like.setCaseSensitive(!ignoreCase);
		filters.add(like);
		refresh();
	}

	/**
	 * Removes filters.
	 *
	 * @param propertyId the property id
	 */
	@Override
	public void removeFilters(Object propertyId) {
		List<Filter> toRemove = new ArrayList<Filter>();
		for (Filter f : filters) {
			if (f.appliesToProperty(propertyId)) {
				toRemove.add(f);
			}
		}
		filters.removeAll(toRemove);
		refresh();
	}

	/**
	 * Removes filters.
	 */
	@Override
	public void removeAllFilters() {
		filters.clear();
		refresh();
	}

	/**
	 * Checks for filters.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean hasFilters() {
		return !getFilters().isEmpty();
	}

	/**
	 * Gets the filters.
	 *
	 * @return the filters
	 */
	@Override
	public Collection<Filter> getFilters() {
		return Collections.unmodifiableCollection(filters);
	}

	/**
	 * Index of id.
	 *
	 * @param itemId the item id
	 * @return the int
	 */
	@Override
	public int indexOfId(Object itemId) {
		if (resultset.getResultContainer().getItemIndexes().containsValue(itemId)) {
			for (Integer idx : resultset.getResultContainer().getItemIndexes().keySet()) {
				if (resultset.getResultContainer().getItemIndexes().get(idx).equals(itemId)) {
					return idx;
				}
			}
		}
		return -1;
	}

	/**
	 * Gets the id by index.
	 *
	 * @param index the index
	 * @return the id by index
	 */
	@Override
	public Object getIdByIndex(int index) {
		if (index < 0) {
			throw new IndexOutOfBoundsException("Index is negative! index=" + index);
		}

		if (resultset != null && resultset.getResultContainer() != null
				&& resultset.getResultContainer().getItemIndexes() != null && index <= updateCount()) {
			if (resultset.getResultContainer().getItemIndexes().keySet().contains(index)) {
				return resultset.getResultContainer().getItemIndexes().get(index);
			}
			return resultset.getResultContainer().getItemIndexes().get(index);
		}
		return null;
	}

	/**
	 * Gets the item ids.
	 *
	 * @param startIndex  the start index
	 * @param numberOfIds the number of ids
	 * @return the item ids
	 */
	@Override
	public List<Object> getItemIds(int startIndex, int numberOfIds, Container.Indexed container) {
		return (List<Object>) getItemIds().stream().collect(Collectors.toList()).subList(startIndex, numberOfIds);
	}

	/**
	 * Next item id.
	 *
	 * @param itemId the item id
	 * @return the object
	 */
	@Override
	public Object nextItemId(Object itemId) {
		int index = indexOfId(itemId) + 1;
		try {
			return getIdByIndex(index);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * Prev item id.
	 *
	 * @param itemId the item id
	 * @return the object
	 */
	@Override
	public Object prevItemId(Object itemId) {
		int prevIndex = indexOfId(itemId) - 1;
		try {
			return getIdByIndex(prevIndex);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	/**
	 * First item id.
	 *
	 * @return the object
	 */
	@Override
	public Object firstItemId() {
		updateCount();
		return resultset.getResultContainer().getItemIndexes().get(0);
	}

	/**
	 * Last item id.
	 *
	 * @return the object
	 */
	@Override
	public Object lastItemId() {
		int lastIx = size() - 1;
		return resultset.getResultContainer().getItemIndexes().get(lastIx);
	}

	/**
	 * Checks if is first id.
	 *
	 * @param itemId the item id
	 * @return true, if is first id
	 */
	@Override
	public boolean isFirstId(Object itemId) {
		if (itemId != null) {
			List<String> idsList = new ArrayList<>();
			if (CollectionUtils.isNotEmpty(getItemIds()) && itemId != null) {
				int index = idsList.indexOf(itemId);
				if (index == 0) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if is last id.
	 *
	 * @param itemId the item id
	 * @return true, if is last id
	 */
	@Override
	public boolean isLastId(Object itemId) {
		if (itemId != null) {
			Collection<?> ids = getItemIds();
			if (CollectionUtils.isNotEmpty(ids) && itemId != null) {
				Boolean existItemId = ids.stream().map(field -> {
					return field.toString();
				}).filter(field -> {
					return field.equals(itemId.toString());
				}).collect(Collectors.toList()).size() > 0;
				Boolean isLastId = ids.stream().collect(Collectors.toList()).get(ids.size() - 1).equals(itemId);
				return isLastId;
			}
		}
		return false;
	}

	/**
	 * Sort.
	 *
	 * @param propertyId the property id
	 * @param ascending  the ascending
	 */
	@Override
	public void sort(Object[] propertyId, boolean[] ascending) {
		sorters.clear();
		if (propertyId == null || propertyId.length == 0) {
			refresh();
			return;
		}
		/* Generate OrderBy -objects */
		boolean asc = true;
		for (int i = 0; i < propertyId.length; i++) {
			/* Check that the property id is valid */
			if (propertyId[i] instanceof String && propertyIds.contains(propertyId[i])) {
				try {
					asc = ascending[i];
				} catch (Exception e) {
					serviceContainer.getLogService().logger(LogStatus.ERROR, "", this.getClass(), e);
				}
				sorters.add(new OrderBy((String) propertyId[i], asc));
			}
		}
		refresh();

		try {

			Map<String, String> sorts = sorters.stream().collect(Collectors.toMap(OrderBy::getColumn, f -> {
				return f.isAscending() ? GenericConstants.ORDER_ASC : GenericConstants.ORDER_DESC;
			}));
			params.setOrders(sorts);
			CustomResultSet resultset;
			if (!serviceContainer.getCacheHelper().containsCachedResultsKey(params.toString())) {
				resultset = queryDelegate.search(params);
				serviceContainer.getCacheHelper().putCachedResults(params.toString(), resultset);
			} else {
				resultset = serviceContainer.getCacheHelper().getCachedResults(params.toString());
			}
		} catch (Exception e) {
			serviceContainer.getLogService().logger(LogStatus.ERROR, "Get results", this.getClass(), e);
		}
	}

	/**
	 * Gets the sortable container property ids.
	 *
	 * @return the sortable container property ids
	 */
	@Override
	public Collection<?> getSortablePropertyIds() {
		return getPropertyIds();
	}

	/**
	 * Refresh.
	 */
	@Override
	public void refresh() {
	}

	/**
	 * Adds the order by.
	 *
	 * @param orderBy the order by
	 */
	public void addOrderBy(OrderBy orderBy) {
		if (orderBy == null) {
			return;
		}
		if (!propertyIds.contains(orderBy.getColumn())) {
			throw new IllegalArgumentException("The column given for sorting does not exist in this container.");
		}
		sorters.add(orderBy);
		refresh();
	}

	/**
	 * Update count.
	 */
	private int updateCount() {
		if (queryDelegate == null) {
			return 0;
		}
		try {
			try {
				queryDelegate.setFilters(filters);
			} catch (UnsupportedOperationException e) {
				serviceContainer.getLogService().logger(LogStatus.ERROR, "The query delegate doesn't support filtering",
						this.getClass(), e);
			}
			try {
				queryDelegate.setOrderBy(sorters);
			} catch (UnsupportedOperationException e) {
				serviceContainer.getLogService().logger(LogStatus.ERROR, "The query delegate doesn't support sorting",
						this.getClass(), e);
			}
			return queryDelegate.size();
		} catch (Exception e) {
			throw new RuntimeException("Failed to update item set size.", e);
		}
	}

	/**
	 * Gets the property ids.
	 *
	 * @return the property ids
	 */
	@Override
	public void refreshPropertyIds() {
		if (queryDelegate == null) {
			return;
		}
		propertyIds.clear();
		propertyTypes.clear();

		queryDelegate.setFilters(null);
		queryDelegate.setOrderBy(null);
		GenericResultMeta rsmd = null;
		try {
			Class<?> type = null;
			rsmd = new GenericResultMeta(typeClass, serviceContainer);

			for (int i = 0; i < rsmd.getColumnCount(); i++) {
				String colName = rsmd.getColumnLabel(i);

				if (!propertyIds.contains(colName)) {
					propertyIds.add(colName);
				}
				try {
					type = Class.forName(rsmd.getColumnClassName(i));
				} catch (Exception e) {
					serviceContainer.getLogService().logger(LogStatus.ERROR, "Class not found", this.getClass(), e);
					type = Object.class;
				}

				propertyTypes.put(colName, type);
			}
		} catch (Exception e) {
			serviceContainer.getLogService().logger(LogStatus.ERROR, "Failed to fetch property ids, rolling back",
					this.getClass(), e);
		}
	}

	/**
	 * Adds the container property.
	 *
	 * @param propertyId   the property id
	 * @param type         the type
	 * @param defaultValue the default value
	 * @return true, if successful
	 * @throws UnsupportedOperationException the unsupported operation exception
	 */
	@Override
	public boolean addProperty(Object propertyId, Class<?> type, Object defaultValue)
			throws UnsupportedOperationException {
		propertyTypes.put((String) propertyId, type);
		return true;
	}

	/**
	 * Removes the container property.
	 *
	 * @param propertyId the property id
	 * @return true, if successful
	 * @throws UnsupportedOperationException the unsupported operation exception
	 */
	@Override
	public boolean removeProperty(Object propertyId) throws UnsupportedOperationException {
		propertyTypes.remove(propertyId);
		return true;
	}

	/**
	 * Adds the item.
	 *
	 * @param itemId the item id
	 * @return the item
	 * @throws UnsupportedOperationException the unsupported operation exception
	 */
	@Override
	public Item addItem(Object itemId) throws UnsupportedOperationException {
		if (queryDelegate != null && itemId != null) {
			GenericEntity annotation = itemId.getClass().getAnnotation(GenericEntity.class);
			if (annotation != null) {
				try {
					if (!queryDelegate.hasExecuteSetup(annotation.name())) {
						queryDelegate.setup(annotation.name(), itemId.getClass());
					}
					String id = (String) serviceContainer.getConverterClass().getFieldFromInstance(typeClass,
							(T) itemId, GenericConstants.PARAM_ID);
					if (!StringUtils.isNotEmpty(id)) {
						id = UUID.randomUUID().toString();
					}
					ItemDelegate<T> item = new ItemDelegate(annotation.name(), (T) itemId, id, typeClass);
					queryDelegate.addItem(item);
				} catch (GenericException e1) {
					serviceContainer.getLogService().logger(LogStatus.ERROR, "Error addItem:", this.getClass(), e1);
				}
			}
		}
		return null;
	}

	/**
	 * Adds the row id change listener.
	 *
	 * @param listener the listener
	 */
	public void addRowIdChangeListener(RowIdChangeListener listener) {
		if (queryDelegate != null && queryDelegate instanceof RowIdChangeNotifier) {
			((RowIdChangeNotifier) queryDelegate).addListener(listener);
		}
	}

	/**
	 * Adds the listener.
	 *
	 * @param listener the listener
	 */
	@Deprecated
	public void addListener(RowIdChangeListener listener) {
		addRowIdChangeListener(listener);
	}

	/**
	 * Removes the row id change listener.
	 *
	 * @param listener the listener
	 */
	public void removeRowIdChangeListener(RowIdChangeListener listener) {
		if (queryDelegate != null && queryDelegate instanceof RowIdChangeNotifier) {
			((RowIdChangeNotifier) queryDelegate).removeListener(listener);
		}
	}

	/**
	 * Removes the listener.
	 *
	 * @param listener the listener
	 */
	@Deprecated
	public void removeListener(RowIdChangeListener listener) {
		removeRowIdChangeListener(listener);
	}

	/**
	 * Sets the query delegate.
	 *
	 * @param queryDelegate the new query delegate
	 */
	@Override
	public void setQueryDelegate(GenericDelegate queryDelegate) {
		this.queryDelegate = queryDelegate;
	}

	/**
	 * Gets the query delegate.
	 *
	 * @return the query delegate
	 */
	@Override
	public GenericDelegate<T> getQueryDelegate() {
		return this.queryDelegate;
	}

	/**
	 * Gets the type class.
	 *
	 * @return the type class
	 */
	@Override
	public Class<? extends GenericItem> getTypeClass() {
		return typeClass;
	}

	/**
	 * Sets the type class.
	 *
	 * @param typeClass the new type class
	 */
	public void setTypeClass(Class<? extends GenericItem> typeClass) {
		this.typeClass = typeClass;
	}

	/**
	 * Gets the sorters.
	 *
	 * @return the sorters
	 */
	@Override
	public List<OrderBy> getSorters() {
		return sorters;
	}

	/**
	 * Gets the item indexes.
	 *
	 * @return the item indexes
	 */
	@Override
	public Map<Integer, RowId> getItemIndexes() {
		return resultset.getResultContainer().getItemIndexes();
	}

	/**
	 * Gets the results.
	 *
	 * @param params the search params
	 * @return the results
	 * @throws Exception the exception
	 */

	@Override
	public CustomResultSet getResults(Params params) throws Exception {
		Map<String, String> sorts = sorters.stream().collect(Collectors.toMap(OrderBy::getColumn, f -> {
			return f.isAscending() ? GenericConstants.ORDER_ASC : GenericConstants.ORDER_DESC;
		}));
		MgnlContext.getWebContext().getRequest().getSession().setAttribute(GenericConstants.SEARCH_PARAMS, params);

		params.setOrders(sorts);
		this.params = params;
		String stringParams = serviceContainer.getFactoryConverter().convert(params);
		if (queryDelegate != null && !serviceContainer.getCacheHelper().containsCachedResultsKey(stringParams)) {
			resultset = queryDelegate.search(params);
			serviceContainer.getCacheHelper().putCachedResults(stringParams, resultset);
		} else {
			resultset = serviceContainer.getCacheHelper().getCachedResults(stringParams);
		}

		return resultset;
	}

	@Override
	public String getItemUrlFragment(Object itemId) {
		return null;
	}

	/**
	 * Gets the item id by url fragment.
	 *
	 * @param urlFragment the url fragment
	 * @return the item id by url fragment
	 */
	@Override
	public Object getItemIdByUrlFragment(String urlFragment) {
		return null;
	}

	/**
	 * Gets the item id.
	 *
	 * @param item the item
	 * @return the item id
	 */
	@Override
	public Object getItemId(Item item) {
		return false;
	}

	/**
	 * Gets the default item id.
	 *
	 * @return the default item id
	 */
	@Override
	public Object getDefaultItemId() {
		serviceContainer.setCustomContainer(serviceContainer.getFactoryContainer()
				.getClassTypeContainer(serviceContainer.getFactoryContainer().getClassType()));
		Object defaultItemId = null;
		if (serviceContainer.getFactoryContainer().getClassType() != null
				&& serviceContainer.getCustomContainer() != null) {
			defaultItemId = serviceContainer.getCustomContainer().getIdByIndex(0);
		}
		return defaultItemId;
	}

	/**
	 * Can handle item.
	 *
	 * @param itemId the item id
	 * @return true, if successful
	 */
	@Override
	public boolean canHandleItem(Object itemId) {
		return false;
	}

	/**
	 * Gets the new item id.
	 *
	 * @param parentId       the parent id
	 * @param typeDefinition the type definition
	 * @return the new item id
	 */
	@Override
	public Object getNewItemId(Object parentId, Object typeDefinition) {
		return true;
	}

	@Override
	public void createConnection(Params params) throws Exception {
		if (queryDelegate != null) {
			this.typeClass = params.getClassType();
			this.params = params;
			this.queryDelegate.setTypeParameterClass(typeClass);
			refreshPropertyIds();
		}
	}

	@Override
	public void setParams(Params params) {
		this.params = params;
	}

	@Override
	public Params getParams() {
		return params;
	}

	@Override
	public void itemChangeNotification(RowItem changedItem) {
	}

}
