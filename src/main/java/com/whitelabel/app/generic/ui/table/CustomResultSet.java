/*
 *
 */
package com.whitelabel.app.generic.ui.table;

import java.util.List;

import com.whitelabel.app.generic.entity.GenericItem;
import com.whitelabel.app.generic.others.GenericException;
import com.whitelabel.app.generic.search.Params;
import com.whitelabel.app.generic.search.ResultContainer;

/**
 * Interface who want implements a Connector for source content app must
 * implements.
 */
public interface CustomResultSet<T> {

	/**
	 * Next.
	 *
	 * @return true, if successful
	 * @throws GenericException the elastic search exception
	 */
	boolean next() throws GenericException;

	/**
	 * Set the meta data.
	 *
	 * @return the meta data
	 * @throws GenericException the elastic search exception
	 */
	void setMetaData(GenericResultMeta resultSetMetaData) throws GenericException;

	/**
	 * Gets the object.
	 *
	 * @param columnIndex the column index
	 * @return the object
	 * @throws GenericException the elastic search exception
	 */
	Object getObject(int columnIndex) throws GenericException;

	/**
	 * Gets the object.
	 *
	 * @param columnLabel the column label
	 * @return the object
	 * @throws GenericException the elastic search exception
	 */
	Object getObject(String columnLabel) throws GenericException;

	/**
	 * First.
	 *
	 * @return true, if successful
	 * @throws GenericException the elastic search exception
	 */
	boolean first() throws GenericException;

	/**
	 * Last.
	 *
	 * @return true, if successful
	 * @throws GenericException the elastic search exception
	 */
	boolean last() throws GenericException;

	/**
	 * Gets the row.
	 *
	 * @return the row
	 * @throws GenericException the elastic search exception
	 */
	int getRow() throws GenericException;

	/**
	 * Gets the row.
	 *
	 * @return the row
	 * @throws GenericException the elastic search exception
	 */
	List<T> getResults() throws GenericException;

	void setResults(List<? extends GenericItem> search);

	void setParamsSearch(Params searchParams);

	void setResultContainer(ResultContainer resultContainer);

	ResultContainer getResultContainer();

	/**
	 * Gets the meta data.
	 *
	 * @return the meta data
	 * @throws GenericException the elastic search exception
	 */
	ResultSetMetaData getMetaData() throws GenericException;

	public void setTotalSize(Long totalSize);

	public Long getTotalSize();

}
