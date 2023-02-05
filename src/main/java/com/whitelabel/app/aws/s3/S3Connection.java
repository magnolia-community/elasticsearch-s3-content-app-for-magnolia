/*
 *
 */
package com.whitelabel.app.aws.s3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.whitelabel.app.custom.interfaces.CustomConnection;
import com.whitelabel.app.generic.connection.ParameterConnection;
import com.whitelabel.app.generic.connection.S3ParameterConnection;

import lombok.Data;

/**
 * Create a general purpose connection with AWS S3 instance
 */
@Data
public class S3Connection implements CustomConnection {
	private static final Logger log = LoggerFactory.getLogger(S3Connection.class);

	AmazonS3 client;
	AWSCredentials credentials;
	ParameterConnection params;

	/**
	 * Instantiates a S3 connection.
	 */
	public S3Connection() {
		if (params != null) {
			connection(params);
		}
	}

	/**
	 * Instantiates a new S3 connection.
	 *
	 * @param server the server
	 * @param port   the port
	 */
	public S3Connection(ParameterConnection params) {
		connection(params);
	}

	/**
	 * Connection.
	 *
	 * @param server the server
	 * @param port   the port
	 */
	@Override
	public void connection(ParameterConnection params) {
		credentials = new BasicAWSCredentials(((S3ParameterConnection) params).getAccessKey(),
				((S3ParameterConnection) params).getSecretKey());
		client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(Regions.fromName(((S3ParameterConnection) params).getRegion())).build();
	}

	@Override
	public void connect() {
		connection(params);
	}
}
