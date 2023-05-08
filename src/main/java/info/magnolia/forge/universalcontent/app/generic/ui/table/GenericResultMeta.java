/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.ui.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import info.magnolia.forge.universalcontent.app.generic.entity.Field;
import info.magnolia.forge.universalcontent.app.generic.entity.GenericItem;
import info.magnolia.forge.universalcontent.app.generic.service.RepositoryService;

/**
 * Represent a meta of results from a query's generic implementation.
 *
 * @param <T> the generic type
 */
public class GenericResultMeta<T extends GenericItem> implements ResultSetMetaData {

	/** The fields name. */
	public List<Field> fieldsName;

	/** The type parameter class. */
	private Class<T> typeParameterClass;

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(GenericResultMeta.class);

	private RepositoryService repositoryService;

	/**
	 * Instantiates a new elastic search result meta.
	 *
	 * @param fieldsName the fields name
	 */
	public GenericResultMeta(List<Field> fieldsName, RepositoryService repositoryService) {
		super();
		this.fieldsName = fieldsName;
		this.repositoryService = repositoryService;
	}

	/**
	 * Instantiates a new elastic search result meta.
	 *
	 * @param typeParameterClass the type parameter class
	 */
	public GenericResultMeta(Class<T> typeParameterClass, RepositoryService repositoryService) {
		super();
		this.repositoryService = repositoryService;
		String indexName;
		try {
			this.fieldsName = new ArrayList<Field>();
			if (typeParameterClass != null) {
				indexName = typeParameterClass.newInstance().getIndexName();
				List<java.lang.reflect.Field> classFields = repositoryService.getConverterClass()
						.getAllFields(typeParameterClass);
				List<Field> fields = classFields.stream().map(f -> {
					return Field.instanceFrom(f, indexName);
				}).collect(Collectors.toList());
				this.fieldsName = fields;
				this.typeParameterClass = typeParameterClass;
			}
		} catch (InstantiationException | IllegalAccessException e) {
			log.error("ElasticSearchResultMeta error costructor", e);
		}
	}

	/**
	 * Gets the column count.
	 *
	 * @return the column count
	 * @throws ElasticsearchException the ElasticSearch exception
	 */
	@Override
	public int getColumnCount() throws ElasticsearchException {
		return fieldsName.size();
	}

	/**
	 * Checks if is auto increment.
	 *
	 * @param column the column
	 * @return true, if is auto increment
	 * @throws ElasticsearchException the Elasticsearch exception
	 */
	@Override
	public boolean isAutoIncrement(int column) throws ElasticsearchException {
		return fieldsName.get(column).getIsAutoIncrement();
	}

	/**
	 * Checks if is case sensitive.
	 *
	 * @param column the column
	 * @return true, if is case sensitive
	 * @throws ElasticsearchException the Elastic Search engine exception
	 */
	@Override
	public boolean isCaseSensitive(int column) throws ElasticsearchException {
		return fieldsName.get(column).getIsCaseSensitive();
	}

	/**
	 * Checks if is searchable.
	 *
	 * @param column the column
	 * @return true, if is searchable
	 * @throws ElasticsearchException the Elastic Search engine exception
	 */
	@Override
	public boolean isSearchable(int column) throws ElasticsearchException {
		return fieldsName.get(column).getIsSearchable();
	}

	/**
	 * Checks if is currency.
	 *
	 * @param column the column
	 * @return true, if is currency
	 * @throws ElasticsearchException the Elastic Search engine exception
	 */
	@Override
	public boolean isCurrency(int column) throws ElasticsearchException {
		return fieldsName.get(column).getIsCurrency();
	}

	/**
	 * Checks if is nullable.
	 *
	 * @param column the column
	 * @return the int
	 * @throws ElasticsearchException the Elastic Search engine exception
	 */
	@Override
	public int isNullable(int column) throws ElasticsearchException {
		if (fieldsName.get(column).getIsNullable()) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Checks if is signed.
	 *
	 * @param column the column
	 * @return true, if is signed
	 * @throws ElasticsearchException the Elastic Search engine exception
	 */
	@Override
	public boolean isSigned(int column) throws ElasticsearchException {
		return fieldsName.get(column).getIsSigned();
	}

	/**
	 * Gets the column display size.
	 *
	 * @param column the column
	 * @return the column display size
	 * @throws ElasticsearchException the Elastic Search engine exception
	 */
	@Override
	public int getColumnDisplaySize(int column) throws ElasticsearchException {
		return fieldsName.get(column).getSize();
	}

	/**
	 * Gets the column label.
	 *
	 * @param column the column
	 * @return the column label
	 * @throws ElasticsearchException the Elastic Search engine exception
	 */
	@Override
	public String getColumnLabel(int column) throws ElasticsearchException {
		return fieldsName.get(column).getName();
	}

	/**
	 * Gets the column name.
	 *
	 * @param column the column
	 * @return the column name
	 * @throws ElasticsearchException the Elastic Search engine exception
	 */
	@Override
	public String getColumnName(int column) throws ElasticsearchException {
		return fieldsName.get(column).getName();
	}

	/**
	 * Gets the schema name.
	 *
	 * @param column the column
	 * @return the schema name
	 * @throws ElasticsearchException the Elastic Search engine exception
	 */
	@Override
	public String getSchemaName(int column) throws ElasticsearchException {
		return fieldsName.get(column).getIndexName();
	}

	/**
	 * Gets the table name.
	 *
	 * @param column the column
	 * @return the table name
	 * @throws ElasticsearchException the Elastic Search engine exception
	 */
	@Override
	public String getTableName(int column) throws ElasticsearchException {
		return fieldsName.get(column).getIndexName();
	}

	/**
	 * Checks if is read only.
	 *
	 * @param column the column
	 * @return true, if is read only
	 * @throws ElasticsearchException the Elastic Search engine exception
	 */
	@Override
	public boolean isReadOnly(int column) throws ElasticsearchException {
		return fieldsName.get(column).getReadOnly();
	}

	/**
	 * Checks if is writable.
	 *
	 * @param column the column
	 * @return true, if is writable
	 * @throws ElasticsearchException the Elastic Search engine exception
	 */
	@Override
	public boolean isWritable(int column) throws ElasticsearchException {
		return !fieldsName.get(column).getReadOnly();
	}

	/**
	 * Checks if is definitely writable.
	 *
	 * @param column the column
	 * @return true, if is definitely writable
	 * @throws ElasticsearchException the Elastic Search engine exception
	 */
	@Override
	public boolean isDefinitelyWritable(int column) throws ElasticsearchException {
		return !fieldsName.get(column).getReadOnly();
	}

	/**
	 * Gets the column class name.
	 *
	 * @param column the column
	 * @return the column class name
	 * @throws ElasticsearchException the Elastic Search engine exception
	 */
	@Override
	public String getColumnClassName(int column) throws ElasticsearchException {
		return fieldsName.get(column).getClassType().getName();
	}

	@Override
	public void setFieldsName(ArrayList<Field> fieldsName) {
		this.fieldsName = fieldsName;
	}

	@Override
	public Collection<Field> getFieldsName() {
		return this.fieldsName;
	}

	@Override
	public String toString() {
		return "GenericResultMeta [fieldsName=" + fieldsName + ", typeParameterClass=" + typeParameterClass + "]";
	}

}
