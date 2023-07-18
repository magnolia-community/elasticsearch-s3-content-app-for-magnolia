package info.magnolia.forge.universalcontent.app.custom.interfaces;

import java.util.List;

import info.magnolia.forge.universalcontent.app.generic.connector.GenericDelegate;
import info.magnolia.forge.universalcontent.app.generic.entity.GenericItem;
import info.magnolia.forge.universalcontent.app.generic.search.Params;
import info.magnolia.forge.universalcontent.app.generic.ui.table.CustomResultSet;
import info.magnolia.forge.universalcontent.app.generic.ui.table.OrderBy;
import info.magnolia.ui.vaadin.integration.contentconnector.SupportsCreation;

public interface CustomContentConnector extends GenericContentConnector, ElasticSearchOperations, SupportsCreation {
	public GenericDelegate<? extends GenericItem> getQueryDelegate();

	public void setQueryDelegate(GenericDelegate<? extends GenericItem> delegate);

	void createConnection(Params params) throws Exception;

	public void setParams(Params params);

	public Params getParams();

	public CustomResultSet getResultset();

	public List<OrderBy> getSorters();

	@Override
	public Class<? extends GenericItem> getTypeClass();

	public void setTypeClass(Class<? extends GenericItem> typeClass);

}
