package info.magnolia.forge.universalcontent.app.generic.connector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.v7.data.Container.Filter;

import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomConnection;
import info.magnolia.forge.universalcontent.app.custom.interfaces.QueryDelegate;
import info.magnolia.forge.universalcontent.app.elasticsearch.ElasticSearchDelegate;
import info.magnolia.forge.universalcontent.app.generic.annotation.GenericEntity;
import info.magnolia.forge.universalcontent.app.generic.entity.Field;
import info.magnolia.forge.universalcontent.app.generic.entity.GenericItem;
import info.magnolia.forge.universalcontent.app.generic.entity.ItemDelegate;
import info.magnolia.forge.universalcontent.app.generic.others.GenericException;
import info.magnolia.forge.universalcontent.app.generic.search.Params;
import info.magnolia.forge.universalcontent.app.generic.search.ResultContainer;
import info.magnolia.forge.universalcontent.app.generic.service.RepositoryService;
import info.magnolia.forge.universalcontent.app.generic.ui.table.ColumnProperty;
import info.magnolia.forge.universalcontent.app.generic.ui.table.CustomResultSet;
import info.magnolia.forge.universalcontent.app.generic.ui.table.GenericResultSet;
import info.magnolia.forge.universalcontent.app.generic.ui.table.GenericResults;
import info.magnolia.forge.universalcontent.app.generic.ui.table.OrderBy;
import info.magnolia.forge.universalcontent.app.generic.ui.table.ResultSetMetaData;
import info.magnolia.forge.universalcontent.app.generic.ui.table.RowId;
import info.magnolia.forge.universalcontent.app.generic.ui.table.RowItem;
import info.magnolia.forge.universalcontent.app.generic.utils.GenericConstants;
import info.magnolia.init.MagnoliaConfigurationProperties;
import info.magnolia.objectfactory.Components;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public abstract class GenericDelegate<T extends GenericItem> implements QueryDelegate<T> {

	protected static final Logger log = LoggerFactory.getLogger(ElasticSearchDelegate.class);

	@Setter
	@Getter
	protected Class<? extends GenericItem> typeParameterClass;

	protected Params params;
	@Setter
	@Getter
	private CustomResultSet<T> resultset;

	@Setter
	@Getter
	protected RepositoryService serviceContainer;

	protected MagnoliaConfigurationProperties magnoliaConfigurationProperties;

	protected T obj;

	@Inject
	public GenericDelegate(RepositoryService serviceContainer) throws GenericException {
		magnoliaConfigurationProperties = Components.getComponent(MagnoliaConfigurationProperties.class);
		resultset = new GenericResultSet<T>(typeParameterClass, serviceContainer);
		this.params = new Params();
		resultset.setResults(new ArrayList<T>());
		if (resultset.getMetaData() != null) {
			resultset.getMetaData().setFieldsName(new ArrayList<Field>());
		}
	}

	@Override
	public Boolean hasExecuteSetup(String key) throws GenericException {
		return null;
	}

	/**
	 * Search.
	 *
	 * @param searchParams the search params
	 * @return the elastic search result set
	 * @throws IOException      Signals that an I/O exception has occurred.
	 * @throws GenericException
	 */
	@Override
	public CustomResultSet<T> search(Params searchParams) throws IOException, GenericException {
		if (getConnection() == null) {
			return null;
		}
		if (searchParams == null) {
			searchParams = new Params();
		}
		if (typeParameterClass != null) {
			resultset = new GenericResultSet<T>(typeParameterClass, serviceContainer);

			GenericEntity index = typeParameterClass.getAnnotation(GenericEntity.class);
			if (index != null && StringUtils.isNotEmpty(index.name())) {
				GenericResults search = search(index.name(), searchParams);
				resultset.setResults(search.getSearch());
				resultset.setTotalSize(search.getTotal());
				resultset.setParamsSearch(searchParams);
				resultset.setResultContainer(convertResultSetInCollectionItems(resultset));
			}
		}

		return resultset;
	}

	/**
	 * Gets the all entity index.
	 *
	 * @return the all entity index
	 */
	@Override
	public List<Class<? extends GenericItem>> getAllTypeItems() {
		return serviceContainer.getConverterClass().getAllClassGenericItem(GenericItem.class);
	}

	@Override
	public void setup(String key, Class typeClass) throws GenericException {
	}

	/**
	 * Gets the count.
	 *
	 * @return the count
	 * @throws Exception the exception
	 */
	@Override
	public int size() throws Exception {
		if (resultset != null && resultset.getResults() != null) {
			return resultset.getResults().size();
		}
		return 0;
	}

	@Override
	public boolean implementationRespectsPagingLimits() {
		return false;
	}

	@Override
	public void setFilters(List<Filter> filters) throws UnsupportedOperationException {
	}

	/**
	 * Gets the primary key columns.
	 *
	 * @return the primary key columns
	 * @throws GenericException the elastic search exception
	 */
	@Override
	public List<String> getPropertyColumns() throws GenericException {
		if (resultset != null && resultset.getMetaData() != null) {
			return resultset.getMetaData().getFieldsName().stream().filter(f -> {
				return true;
			}).map(f -> {
				return f.getName();
			}).collect(Collectors.toList());
		}
		return new ArrayList<String>();
	}

	/**
	 * Contains row with key.
	 *
	 * @param keys the keys
	 * @return true, if successful
	 * @throws Exception the exception
	 */
	@Override
	public boolean containsRowWithKey(Object... keys) throws Exception {
		return true;
	}

	/**
	 * Sets the order by.
	 *
	 * @param orderBys the new order by
	 * @throws UnsupportedOperationException the unsupported operation exception
	 */
	@Override
	public void setOrderBy(List<OrderBy> orderBys) throws UnsupportedOperationException {
		if (orderBys != null) {
			if (params == null) {
				params = new Params();
			}
			Map<String, String> mapOrderBy = orderBys.stream().filter(k -> {
				return k.getColumn() != null;
			}).collect(Collectors.toMap(k -> {
				return k.getColumn();
			}, k -> {
				return k.isAscending() ? GenericConstants.ORDER_ASC : GenericConstants.ORDER_DESC;
			}));
			params.setOrders(mapOrderBy);
		}
	}

	@Override
	public ItemDelegate<? extends GenericItem> addItem(ItemDelegate<? extends GenericItem> item)
			throws GenericException {
		return null;
	}

	@Override
	public void itemChangeNotification(RowItem changedItem) {
	}

	/**
	 * Creates the property ids with value.
	 *
	 * @param rs          the rs
	 * @param rsmd        the rsmd
	 * @param propertyIds the property ids
	 * @return the list
	 * @throws GenericException the elastic search exception
	 */
	private List<ColumnProperty> createPropertyIdsWithValue(CustomResultSet rs, ResultSetMetaData rsmd,
			List<String> propertyIds) throws GenericException {
		List<String> propertiesToAdd = new ArrayList<String>(propertyIds);
		List<ColumnProperty> itemProperties = new ArrayList<ColumnProperty>();
		ColumnProperty cp;
		for (int i = 0; i < rsmd.getColumnCount(); i++) {
			String colName = rsmd.getColumnLabel(i);
			Object value = rs.getObject(i);
			Class<?> type = value != null ? value.getClass() : Object.class;

			cp = new ColumnProperty(colName, false, true, true, true, value, type, serviceContainer);
			itemProperties.add(cp);
			propertiesToAdd.remove(colName);
		}
		return itemProperties;
	}

	/**
	 * Convert result set in collection items.
	 *
	 * @param rs the rs
	 * @return the result container
	 * @throws GenericException the elastic search exception
	 */
	protected ResultContainer convertResultSetInCollectionItems(CustomResultSet rs) throws GenericException {
		Map<Integer, RowId> itemIndexes = new HashMap<Integer, RowId>();
		Map<RowId, RowItem> resultIndexes = new HashMap<RowId, RowItem>();
		ResultContainer result;
		List<RowId> ids = new ArrayList<RowId>();
		List<String> pKeys = rs.getMetaData().getFieldsName().stream().map(field -> {
			return field.getName();
		}).collect(Collectors.toList());
		int counter = 0;
		List<ColumnProperty> itemProperties = new ArrayList<ColumnProperty>();
		for (String propertyId : pKeys) {
			Field fieldProperty = rs.getMetaData().getFieldsName().stream().filter(field -> {
				return propertyId.equals(field.getName());
			}).findFirst().get();
			ColumnProperty cp = new ColumnProperty(propertyId, fieldProperty.getReadOnly(), fieldProperty.getReadOnly(),
					fieldProperty.getIsNullable(), true, null, fieldProperty.getClassType(), serviceContainer);
			itemProperties.add(cp);
		}
		while (rs.next()) {
			RowId id = null;
			Object[] itemId = new Object[pKeys.size()];
			for (int i = 0; i < pKeys.size(); i++) {
				itemId[i] = rs.getObject(pKeys.get(i));
			}
			id = new RowId(itemId);

			if (id != null) {
				ids.add(id);
				itemIndexes.put(counter, id);
				resultIndexes.put(id, new RowItem(id, itemProperties));
				counter++;
			}
		}
		result = new ResultContainer(ids, itemIndexes, resultIndexes);
		return result;
	}

	public abstract GenericResults search(String indexName, Params searchParams) throws GenericException;

	@Override
	public abstract CustomConnection getConnection();

	@Override
	public abstract void setConnection(CustomConnection customConnection);

}
