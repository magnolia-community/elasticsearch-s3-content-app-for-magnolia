package info.magnolia.forge.universalcontent.app.generic.connection;

public class ElasticSearchParameterConnection extends ParameterConnection {

	public ElasticSearchParameterConnection() {
		super("", "0");
	}

	public ElasticSearchParameterConnection(String server, String port) {
		super(server, port);
	}
}
