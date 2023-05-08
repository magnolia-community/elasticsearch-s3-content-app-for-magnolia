/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.connector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.filter.UnsupportedFilterException;

import info.magnolia.context.MgnlContext;
import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomContainer;
import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomContentConnector;
import info.magnolia.forge.universalcontent.app.event.BaseItemSetChangeEvent;
import info.magnolia.forge.universalcontent.app.generic.entity.GenericItem;
import info.magnolia.forge.universalcontent.app.generic.others.GenericException;
import info.magnolia.forge.universalcontent.app.generic.others.LogStatus;
import info.magnolia.forge.universalcontent.app.generic.search.GenericParamsBuilder;
import info.magnolia.forge.universalcontent.app.generic.search.Params;
import info.magnolia.forge.universalcontent.app.generic.service.RepositoryService;
import info.magnolia.forge.universalcontent.app.generic.ui.table.OrderBy;
import info.magnolia.forge.universalcontent.app.generic.utils.FactoryConverter;
import info.magnolia.forge.universalcontent.app.generic.utils.GenericConstants;
import lombok.Getter;
import lombok.Setter;

/**
 * Manage generic container with contentConnector
 */

public class GenericContainer implements CustomContainer {

	/** The listeners. */
	private List<ItemSetChangeListener> itemSetChangeListeners = new LinkedList<ItemSetChangeListener>();

	@Inject
	private GenericContentConnector<GenericItem> del;

	@Getter
	@Setter
	private Optional<CustomContentConnector> contentConnector;

	private String fullTextExpression = "";

	private Params params;

	@Inject
	private RepositoryService serviceContainer;

	public <T extends GenericItem> GenericContainer() throws GenericException {
		setParams(GenericParamsBuilder.createSearch(serviceContainer).get());
		this.contentConnector = Optional.empty();
	}

	/**
	 * Instantiates a new refreshable log container.
	 *
	 * @param <T>              the generic type
	 * @param typeIndexESClass the type index ES class
	 * @param factory          the factory
	 * @param factoryConverter the factory converter
	 * @throws GenericException
	 */
	public <T extends GenericItem> GenericContainer(Class<T> typeIndexESClass, FactoryContainer factory,
			FactoryConverter factoryConverter) throws GenericException {
		setParams(GenericParamsBuilder.createSearch(serviceContainer)
				.addField(GenericConstants.FILTER_INDEX, typeIndexESClass.getCanonicalName()).get());
		createCustomContainer(params);
	}

	/**
	 * Creates the custom container.
	 *
	 * @param <T> the generic type
	 * @throws GenericException
	 */
	@Override
	public <T extends GenericItem> void createCustomContainer(Params params) throws GenericException {
		Class<? extends GenericItem> searchedClass = serviceContainer.getConverterClass()
				.getClassFromParams(params, GenericItem.class);
		searchedClass = FactoryContainer.getDefaultIndex();
		params.getFields().put("index", searchedClass.getClass().getName());
		params.setClassType(searchedClass);
		serviceContainer.getCustomContainer().setContentConnector(Optional.of(del));
		if ((Optional.empty().equals(serviceContainer.getCustomContainer().getContentConnector())
				|| searchedClass != null)) {

			serviceContainer.getFactoryContainer().create(params.getClassType(), this);
			del.getQueryDelegate().setServiceContainer(serviceContainer);
			serviceContainer.setContentConnector(del);
			List<Field> fields = serviceContainer.getConverterClass().getAllFields(params.getClassType());
			for (Field field : fields) {
				addContainerProperty(field.getName(),
						info.magnolia.ui.workbench.column.definition.PropertyColumnDefinition.class, "-");
			}
		}
		setParams(params);
		serviceContainer.setCustomContainer(this);
	}

	/**
	 * Refresh delegate.
	 *
	 * @throws GenericException
	 */
	@Override
	public void refreshDelegate(Params params) throws GenericException {
		if (serviceContainer.getCustomContainer().getContentConnector().isPresent() && params != null) {
			try {
				fillOrderFields(params);
				serviceContainer.getCustomContainer().getContentConnector().get().getResults(params);
				serviceContainer.getCustomContainer().getContentConnector().get().getItemIds();
			} catch (Exception e) {
				serviceContainer.getLogService().logger(LogStatus.ERROR, "Error refreshing delegate", this.getClass(),
						e);
			}
		}
	}

	private void fillOrderFields(Params params) {
		List<OrderBy> orders = params.getOrders().keySet().stream().map(sort -> {
			OrderBy orderBy = new OrderBy();
			if (params.getOrders().get(sort) != null && "asc".equalsIgnoreCase(params.getOrders().get(sort))) {
				orderBy.setAscending(true);
			}

			orderBy.setColumn(sort);

			return orderBy;
		}).collect(Collectors.toList());
		serviceContainer.getCustomContainer().getContentConnector().get().getSorters().clear();
		serviceContainer.getCustomContainer().getContentConnector().get().getSorters().addAll(orders);
	}

	/**
	 * Refresh.
	 */
	@Override
	public void refresh() {
		if (serviceContainer.getCustomContainer().getContentConnector().isPresent()) {
//	    Create Generic Container
			try {
				refreshDelegate(params);
			} catch (GenericException e) {
				serviceContainer.getLogService().logger(LogStatus.ERROR, "Error refreshing results:", this.getClass(),
						e);
			}
			contentConnector.get().refresh();
			serviceContainer.getCustomContainer().getContentConnector().get().refreshPropertyIds();

			notifyItemSetChange();
		}

	}

	/**
	 * Notify item set change.
	 */
	private void notifyItemSetChange() {
		BaseItemSetChangeEvent event = new BaseItemSetChangeEvent(this);
		try {
			if (event != null && event.getContainer() != null && event.getSource() instanceof GenericContainer) {
				GenericContainer sourceEvent = (GenericContainer) event.getSource();
				if (StringUtils.isNotEmpty(sourceEvent.getFullTextExpression())) {
					MgnlContext.getWebContext().getRequest().getSession().setAttribute("SEARCH",
							event.getContainer().size());
				}
			}

		} catch (Exception e) {
			serviceContainer.getLogService().logger(LogStatus.ERROR,
					"Exception caught setting in session the result of product search", this.getClass(), e);
		}

		final Object[] l = itemSetChangeListeners.toArray();
		for (int i = 0; i < l.length; i++) {
			((ItemSetChangeListener) l[i]).containerItemSetChange(event);
		}

	}

	/**
	 * Gets the item.
	 *
	 * @param itemId the item id
	 * @return the item
	 */
	@Override
	public Item getItem(Object itemId) {
		if (serviceContainer.getCustomContainer().getContentConnector().isPresent()) {
			return serviceContainer.getCustomContainer().getContentConnector().get().getItem(itemId);
		}
		return null;
	}

	/**
	 * Gets the container property ids.
	 *
	 * @return the container property ids
	 */
	@Override
	public Collection<?> getContainerPropertyIds() {
		if (serviceContainer.getCustomContainer().getContentConnector().isPresent()) {
			return serviceContainer.getCustomContainer().getContentConnector().get().getPropertyIds();
		}
		return Collections.emptyList();
	}

	/**
	 * Gets the item ids.
	 *
	 * @return the item ids
	 */
	@Override
	public Collection<?> getItemIds() {
		if (serviceContainer.getCustomContainer().getContentConnector().isPresent()) {
			return serviceContainer.getCustomContainer().getContentConnector().get().getItemIds();
		}
		return Collections.emptyList();
	}

	/**
	 * Gets the container property.
	 *
	 * @param itemId     the item id
	 * @param propertyId the property id
	 * @return the container property
	 */
	@Override
	public Property getContainerProperty(Object itemId, Object propertyId) {
		if (serviceContainer.getCustomContainer().getContentConnector().isPresent()) {
			return serviceContainer.getCustomContainer().getContentConnector().get().getProperty(itemId, propertyId);
		}
		return null;
	}

	/**
	 * Gets the type.
	 *
	 * @param propertyId the property id
	 * @return the type
	 */
	@Override
	public Class<?> getType(Object propertyId) {
		if (serviceContainer.getCustomContainer().getContentConnector().isPresent()) {
			return serviceContainer.getCustomContainer().getContentConnector().get().getType(propertyId);
		}
		return null;

	}

	/**
	 * Size.
	 *
	 * @return the int
	 */
	@Override
	public int size() {
		if (serviceContainer.getCustomContainer().getContentConnector().isPresent()) {
			return serviceContainer.getCustomContainer().getContentConnector().get().size();
		}
		return 0;
	}

	/**
	 * Contains id.
	 *
	 * @param itemId the item id
	 * @return true, if successful
	 */
	@Override
	public boolean containsId(Object itemId) {
		if (serviceContainer.getCustomContainer().getContentConnector().isPresent()) {
			return serviceContainer.getCustomContainer().getContentConnector().get().containsId(itemId);
		}
		return false;
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
		if (serviceContainer.getCustomContainer().getContentConnector().isPresent()) {
			try {
				Item item = serviceContainer.getCustomContainer().getContentConnector().get().addItem(itemId);
				return item;
			} catch (UnsupportedOperationException e) {
				serviceContainer.getLogService().logger(LogStatus.ERROR, "Error addItem:", this.getClass(), e);
			} catch (GenericException e) {
				serviceContainer.getLogService().logger(LogStatus.ERROR, "Error addItem:", this.getClass(), e);
			}
		}

		return null;

	}

	/**
	 * Adds the item.
	 *
	 * @return the object
	 * @throws UnsupportedOperationException the unsupported operation exception
	 */
	@Override
	public Object addItem() throws UnsupportedOperationException {
		if (serviceContainer.getCustomContainer().getContentConnector().isPresent()) {
			return serviceContainer.getCustomContainer().getContentConnector().get().addItem();
		}
		return null;
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
		if (serviceContainer.getCustomContainer().getContentConnector().isPresent()) {
			return serviceContainer.getCustomContainer().getContentConnector().get().removeItem(itemId);
		}
		return false;
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
	public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue)
			throws UnsupportedOperationException {
		return serviceContainer.getCustomContainer().getContentConnector().get().addProperty(propertyId, type,
				defaultValue);
	}

	/**
	 * Removes the container property.
	 *
	 * @param propertyId the property id
	 * @return true, if successful
	 * @throws UnsupportedOperationException the unsupported operation exception
	 */
	@Override
	public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
		if (serviceContainer.getCustomContainer().getContentConnector().isPresent()) {
			return serviceContainer.getCustomContainer().getContentConnector().get().removeProperty(propertyId);
		}
		return false;
	}

	/**
	 * Removes the all items.
	 *
	 * @return true, if successful
	 * @throws UnsupportedOperationException the unsupported operation exception
	 */
	@Override
	public boolean removeAllItems() throws UnsupportedOperationException {
		if (serviceContainer.getCustomContainer().getContentConnector().isPresent()) {
			return serviceContainer.getCustomContainer().getContentConnector().get().removeAllItems();
		}
		return false;
	}

	/**
	 * Gets the full text expression.
	 *
	 * @return the full text expression
	 */
	public String getFullTextExpression() {
		return fullTextExpression;
	}

	/**
	 * Sort.
	 *
	 * @param propertyId the property id
	 * @param ascending  the ascending
	 */
	@Override
	public void sort(Object[] propertyId, boolean[] ascending) {
		serviceContainer.getCustomContainer().getContentConnector().get().sort(propertyId, ascending);
		serviceContainer.getCustomContainer().getContentConnector().get().getItemIds();
	}

	/**
	 * Gets the sortable container property ids.
	 *
	 * @return the sortable container property ids
	 */
	@Override
	public Collection<?> getSortableContainerPropertyIds() {
		if (serviceContainer.getCustomContainer().getContentConnector().isPresent()) {
			return serviceContainer.getCustomContainer().getContentConnector().get().getSortablePropertyIds();
		}
		return Collections.emptyList();
	}

	@Override
	public int indexOfId(Object itemId) {
		return serviceContainer.getCustomContainer().getContentConnector().get().indexOfId(itemId);
	}

	@Override
	public Object getIdByIndex(int index) {
		return serviceContainer.getCustomContainer().getContentConnector().get().getIdByIndex(index);
	}

	@Override
	public List<?> getItemIds(int startIndex, int numberOfItems) {
		int lastItem = startIndex + numberOfItems;
		if ((startIndex + numberOfItems) > size()) {
			if (serviceContainer.getCustomContainer().getContentConnector().get().getResultset()
					.getTotalSize() > size()) {
				try {
					params.setSize(params.getSize() + GenericConstants.DEFAULT_PAGE_LENGTH);
					serviceContainer.getCustomContainer().getContentConnector().get().getResults(params);
					serviceContainer.getCustomContainer().getContentConnector().get().getItemIds();
				} catch (Exception e) {
					serviceContainer.getLogService().logger(LogStatus.ERROR, "Error search", GenericContainer.class, e);
				}
			}

			lastItem = size() - 1;
		}
		return serviceContainer.getCustomContainer().getContentConnector().get().getItemIds(startIndex, lastItem, this);
	}

	@Override
	public Object addItemAt(int index) throws UnsupportedOperationException {
		return null;
	}

	@Override
	public Item addItemAt(int index, Object newItemId) throws UnsupportedOperationException {
		return null;
	}

	/**
	 * Adds the container filter.
	 *
	 * @param filter the filter
	 * @throws UnsupportedFilterException the unsupported filter exception
	 */
	@Override
	public void addContainerFilter(Filter filter) throws UnsupportedFilterException {
		serviceContainer.getCustomContainer().getContentConnector().get().addFilter(filter);
		refresh();
	}

	/**
	 * Removes the container filter.
	 *
	 * @param filter the filter
	 */
	@Override
	public void removeContainerFilter(Filter filter) {
		serviceContainer.getCustomContainer().getContentConnector().get().removeFilter(filter);
		refresh();
	}

	@Override
	public void removeAllContainerFilters() {
		serviceContainer.getCustomContainer().getContentConnector().get().removeAllFilters();

	}

	/**
	 * Gets the container filters.
	 *
	 * @return the container filters
	 */
	@Override
	public Collection<Filter> getContainerFilters() {
		return serviceContainer.getCustomContainer().getContentConnector().get().getFilters();
	}

	/**
	 * --------------------------------------------------------------------------------
	 * HIERARCHICAL CONTAINER INTERFACE
	 * --------------------------------------------------------------------------------
	 */
	/**
	 * Sets the parent.
	 *
	 * @param itemId      the item id
	 * @param newParentId the new parent id
	 * @return true, if successful
	 * @throws UnsupportedOperationException the unsupported operation exception
	 */
	@Override
	public boolean setParent(Object itemId, Object newParentId) throws UnsupportedOperationException {
		return false;
	}

	/**
	 * Are children allowed.
	 *
	 * @param itemId the item id
	 * @return true, if successful
	 */
	@Override
	public boolean areChildrenAllowed(Object itemId) {
		return false;
	}

	/**
	 * Sets the children allowed.
	 *
	 * @param itemId             the item id
	 * @param areChildrenAllowed the are children allowed
	 * @return true, if successful
	 * @throws UnsupportedOperationException the unsupported operation exception
	 */
	@Override
	public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed) throws UnsupportedOperationException {
		return false;
	}

	/**
	 * Checks if is root.
	 *
	 * @param itemId the item id
	 * @return true, if is root
	 */
	@Override
	public boolean isRoot(Object itemId) {
		return false;
	}

	/**
	 * Checks for children.
	 *
	 * @param itemId the item id
	 * @return true, if successful
	 */
	@Override
	public boolean hasChildren(Object itemId) {
		return false;
	}

	/**
	 * Gets the children.
	 *
	 * @param itemId the item id
	 * @return the children
	 */
	@Override
	public Collection<?> getChildren(Object itemId) {
		return Collections.emptyList();
	}

	/**
	 * Gets the parent.
	 *
	 * @param itemId the item id
	 * @return the parent
	 */
	@Override
	public Object getParent(Object itemId) {
		return null;
	}

	/**
	 * Root item ids.
	 *
	 * @return the collection
	 */
	@Override
	public Collection<?> rootItemIds() {
		return Collections.emptyList();
	}

	/**
	 * ------------------------------- ORDERED CONTAINER INTERFACE
	 * -------------------------------
	 */

	/**
	 * Next item id.
	 *
	 * @param itemId the item id
	 * @return the object
	 */
	@Override
	public Object nextItemId(Object itemId) {
		if (itemId != null) {
			List<Object> idsList = new ArrayList<>();
			if (CollectionUtils.isNotEmpty(
					serviceContainer.getCustomContainer().getContentConnector().get().getItemIds()) && itemId != null) {
				idsList.addAll(serviceContainer.getCustomContainer().getContentConnector().get().getItemIds());
				int index = idsList.indexOf(itemId);
				if (index >= 0 && index + 1 < idsList.size()) {
					return idsList.get(index + 1);
				}
			}
		}
		return null;
	}

	/**
	 * Prev item id.
	 *
	 * @param itemId the item id
	 * @return the object
	 */
	@Override
	public Object prevItemId(Object itemId) {
		if (itemId != null) {
			List<Object> idsList = new ArrayList<>();
			if (CollectionUtils.isNotEmpty(
					serviceContainer.getCustomContainer().getContentConnector().get().getItemIds()) && itemId != null) {
				idsList.addAll(serviceContainer.getCustomContainer().getContentConnector().get().getItemIds());
				int index = idsList.indexOf(itemId);
				if (index > 0) {
					return idsList.get(index - 1);
				}
			}
		}
		return null;
	}

	/**
	 * First item id.
	 *
	 * @return the object
	 */
	@Override
	public Object firstItemId() {
		if (CollectionUtils
				.isNotEmpty(serviceContainer.getCustomContainer().getContentConnector().get().getItemIds())) {
			return serviceContainer.getCustomContainer().getContentConnector().get().getItemIds().iterator().next();
		}
		return null;
	}

	/**
	 * Last item id.
	 *
	 * @return the object
	 */
	@Override
	public Object lastItemId() {
		List<Object> idsList = new ArrayList<>();
		if (CollectionUtils
				.isNotEmpty(serviceContainer.getCustomContainer().getContentConnector().get().getItemIds())) {
			idsList.addAll(serviceContainer.getCustomContainer().getContentConnector().get().getItemIds());
			return idsList.get(idsList.size() - 1);
		}
		return null;
	}

	/**
	 * Checks if is first id.
	 *
	 * @param itemId the item id
	 * @return true, if is first id
	 */
	@Override
	public boolean isFirstId(Object itemId) {
		return serviceContainer.getCustomContainer().getContentConnector().get().isFirstId(itemId);
	}

	/**
	 * Checks if is last id.
	 *
	 * @param itemId the item id
	 * @return true, if is last id
	 */
	@Override
	public boolean isLastId(Object itemId) {
		return serviceContainer.getCustomContainer().getContentConnector().get().isLastId(itemId);
	}

	/**
	 * Adds the item after.
	 *
	 * @param previousItemId the previous item id
	 * @return the object
	 * @throws UnsupportedOperationException the unsupported operation exception
	 */
	@Override
	public Object addItemAfter(Object previousItemId) throws UnsupportedOperationException {
		return null;
	}

	/**
	 * Adds the item after.
	 *
	 * @param previousItemId the previous item id
	 * @param newItemId      the new item id
	 * @return the item
	 * @throws UnsupportedOperationException the unsupported operation exception
	 */
	@Override
	public Item addItemAfter(Object previousItemId, Object newItemId) throws UnsupportedOperationException {
		return null;
	}

	/**
	 * --------------------------------------------- ItemSetChangeListener
	 * ---------------------------------------------
	 */
	/**
	 * Adds the item set change listener.
	 *
	 * @param listener the listener
	 */
	@Override
	public void addItemSetChangeListener(Container.ItemSetChangeListener listener) {
		if (itemSetChangeListeners == null) {
			itemSetChangeListeners = new LinkedList<Container.ItemSetChangeListener>();
		}
		itemSetChangeListeners.add(listener);
	}

	/**
	 * Adds the listener.
	 *
	 * @param listener the listener
	 */
	@Override
	@Deprecated
	public void addListener(Container.ItemSetChangeListener listener) {
		addItemSetChangeListener(listener);
	}

	/**
	 * Removes the item set change listener.
	 *
	 * @param listener the listener
	 */
	@Override
	public void removeItemSetChangeListener(Container.ItemSetChangeListener listener) {
		if (itemSetChangeListeners != null) {
			itemSetChangeListeners.remove(listener);
		}
	}

	/**
	 * Removes the listener.
	 *
	 * @param listener the listener
	 */
	@Override
	@Deprecated
	public void removeListener(Container.ItemSetChangeListener listener) {
		removeItemSetChangeListener(listener);
	}

	/**
	 * Fire contents change.
	 */
	protected void fireContentsChange() {
		if (itemSetChangeListeners != null) {
			final Container.ItemSetChangeEvent event = new BaseItemSetChangeEvent(this);
			for (Object l : itemSetChangeListeners.toArray()) {
				((Container.ItemSetChangeListener) l).containerItemSetChange(event);
			}
		}
	}

	@Override
	public void createConnection(Params params) throws Exception {
		if (serviceContainer.getCustomContainer().getContentConnector().isEmpty()) {
			createCustomContainer(params);
		}
		serviceContainer.getCustomContainer().getContentConnector().get().createConnection(params);
		setParams(params);
	}

	public Params getParams() {
		return params;
	}

	public void setParams(Params params) {
		this.params = params;
		if (serviceContainer != null && serviceContainer.getCustomContainer() != null
				&& serviceContainer.getCustomContainer().getContentConnector().isPresent()) {
			serviceContainer.getCustomContainer().getContentConnector().get().setParams(params);
		}
	}

}
