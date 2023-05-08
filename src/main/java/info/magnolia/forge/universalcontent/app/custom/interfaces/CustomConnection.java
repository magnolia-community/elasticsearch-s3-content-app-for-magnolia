package info.magnolia.forge.universalcontent.app.custom.interfaces;

import info.magnolia.forge.universalcontent.app.generic.connection.ParameterConnection;

public interface CustomConnection {
	public void connection(ParameterConnection params);

	public Object getClient();

	public ParameterConnection getParams();

	public void setParams(ParameterConnection parameterConnection);

	public void connect();
}
