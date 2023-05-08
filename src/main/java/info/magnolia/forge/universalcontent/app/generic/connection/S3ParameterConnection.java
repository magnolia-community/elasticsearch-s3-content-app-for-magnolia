package info.magnolia.forge.universalcontent.app.generic.connection;

import java.io.Serializable;

import lombok.Data;

@Data
public class S3ParameterConnection extends ParameterConnection implements Serializable {
	private String accessKey;
	private String secretKey;
	private String region;

	public S3ParameterConnection() {
		super("", "0");
	}

	public S3ParameterConnection(String accessKey, String secretKey, String region) {
		super("", "0");
		this.accessKey = accessKey;
		this.secretKey = secretKey;
		this.region = region;
	}
}
