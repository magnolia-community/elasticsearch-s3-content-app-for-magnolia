/*
 *
 */

package info.magnolia.forge.universalcontent.app.generic.connector;

import java.util.ArrayList;
import java.util.List;

import info.magnolia.forge.universalcontent.app.generic.ui.service.GenericPresenterConnectorImpl;
import info.magnolia.ui.workbench.column.definition.ColumnDefinition;
import info.magnolia.ui.workbench.definition.ConfiguredContentPresenterDefinition;

/**
 * Definition generic Presenter connector
 * {@link ConfiguredContentPresenterDefinition}.
 */
public class GenericPresenterConnectorDefinition extends ConfiguredContentPresenterDefinition {

	private Class indexElasticSearch;

	/** The Constant VIEW_TYPE. */
	public static final String VIEW_TYPE = "searchview";

	/**
	 * Instantiates a new elastic search presenter connector.
	 */
	public GenericPresenterConnectorDefinition() {
		setImplementationClass(GenericPresenterConnectorImpl.class);
		setViewType(VIEW_TYPE);
		setActive(true);
		setIcon("icon-view-list");
		List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
		setColumns(columns);
	}

	/**
	 * Gets the index elastic search.
	 *
	 * @return the index elastic search
	 */
	public Class getIndexElasticSearch() {
		return indexElasticSearch;
	}

	/**
	 * Sets the index elastic search.
	 *
	 * @param indexElasticSearch the new index elastic search
	 */
	public void setIndexElasticSearch(Class indexElasticSearch) {
		this.indexElasticSearch = indexElasticSearch;
	}

}
