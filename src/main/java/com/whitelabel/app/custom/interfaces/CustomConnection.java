package com.whitelabel.app.custom.interfaces;

import com.whitelabel.app.generic.connection.ParameterConnection;

public interface CustomConnection {
	public void connection(ParameterConnection params);

	public Object getClient();

	public ParameterConnection getParams();

	public void setParams(ParameterConnection parameterConnection);

	public void connect();
}
