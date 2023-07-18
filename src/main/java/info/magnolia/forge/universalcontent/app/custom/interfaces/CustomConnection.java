package info.magnolia.forge.universalcontent.app.custom.interfaces;

import info.magnolia.forge.universalcontent.app.generic.connection.ParameterConnection;
import info.magnolia.forge.universalcontent.app.manageES.ElasticSearchModuleCore;

public interface CustomConnection {
	public void connection(ParameterConnection params);

	public Object getClient();

	public ParameterConnection getParams();

	public void setParams(ParameterConnection parameterConnection);

	public void connect();

	public ElasticSearchModuleCore getElasticSearchModule();

	public void setElasticSearchModule(ElasticSearchModuleCore elasticSearchModuleCore);
}
