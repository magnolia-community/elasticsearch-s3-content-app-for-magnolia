/*
 *
 */
package com.whitelabel.app.aws.s3;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.whitelabel.app.custom.interfaces.CustomConnection;
import com.whitelabel.app.generic.annotation.DelegateImplementation;
import com.whitelabel.app.generic.annotation.FileUpload;
import com.whitelabel.app.generic.annotation.GenericEntity;
import com.whitelabel.app.generic.connection.S3ParameterConnection;
import com.whitelabel.app.generic.connector.GenericDelegate;
import com.whitelabel.app.generic.entity.GenericItem;
import com.whitelabel.app.generic.entity.ItemDelegate;
import com.whitelabel.app.generic.others.GenericException;
import com.whitelabel.app.generic.search.Params;
import com.whitelabel.app.generic.service.RepositoryService;
import com.whitelabel.app.generic.ui.table.GenericResults;
import com.whitelabel.app.generic.utils.GenericConstants;
import com.whitelabel.app.generic.utils.HashFactory;

import lombok.Data;

/**
 * Delegate class implements using S3
 *
 * @param <T> the generic type
 */

@Data
@DelegateImplementation(parameterClass = S3ParameterConnection.class)
public class S3Delegate<T extends GenericItem> extends GenericDelegate<T> {
	@Inject
	S3Connection connection;

	public S3Delegate() {
		super();
	}

	/**
	 * Instantiates a S3 delegate.
	 *
	 * @param pool               the client search
	 * @param typeParameterClass the type parameter class
	 * @param factoryConverter   the factory converter
	 * @throws GenericException
	 */
	@Inject
	public S3Delegate(RepositoryService serviceContainer) throws GenericException {
		super(serviceContainer);
	}

	/**
	 * Search.
	 *
	 * @param bucketName   the index name
	 * @param searchParams the search params
	 * @return the list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	public GenericResults search(String bucketName, Params searchParams) throws GenericException {
		if (!hasExecuteSetup(bucketName)) {
			setup(bucketName, typeParameterClass);
		}
		Map<String, Object> filterByField = searchParams.getFields().entrySet().stream().filter(key -> {
			try {
				boolean notEmpty;
				try {
					notEmpty = StringUtils.isNotEmpty((String) key.getValue());
				} catch (Exception e) {
					notEmpty = key.getValue() != null;
				}
				return getPropertyColumns().contains(key.getKey()) && notEmpty;
			} catch (GenericException e) {
				log.error("QueryElasticSearchDelegate", e);
			}
			return false;
		}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		List<T> results = new ArrayList<T>();
		try {
			AmazonS3Client amazonS3Client = (AmazonS3Client) connection.getClient();
			List<Bucket> buckets = amazonS3Client.listBuckets();
			ObjectListing objectListing = amazonS3Client.listObjects(bucketName);
			for (S3ObjectSummary os : objectListing.getObjectSummaries()) {
				File file = downloadObject(bucketName, amazonS3Client, os.getKey());
				obj = (T) serviceContainer.getFactoryConverter().getModelMapper().map(os, typeParameterClass);

				Optional<java.lang.reflect.Field> fileAnnotation = getServiceContainer().getConverterClass()
						.getFieldFromAnnotation(typeParameterClass, FileUpload.class);
				if (fileAnnotation.isPresent()) {
					params.getFields().put(fileAnnotation.get().getName(), file);
				}
				String pathFile = fileAnnotation.get().getAnnotation(FileUpload.class).fieldPath();
				Optional<java.lang.reflect.Field> filePathAnnotation = getServiceContainer().getConverterClass()
						.getAllFields(typeParameterClass).stream().filter(field -> {
							return field.getName().equalsIgnoreCase(pathFile);
						}).findFirst();
				if (filePathAnnotation.isPresent()) {
					params.getFields().put(filePathAnnotation.get().getName(), os.getKey());
				}
				String idField = typeParameterClass.getAnnotation(GenericEntity.class).fieldId();
				Optional<java.lang.reflect.Field> idAnnotation = getServiceContainer().getConverterClass()
						.getAllFields(typeParameterClass).stream().filter(field -> {
							return field.getName().equalsIgnoreCase(idField);
						}).findFirst();
				if (idAnnotation.isPresent()) {
					params.getFields().put(idAnnotation.get().getName(),
							HashFactory.convertToHex(FileUtils.readFileToByteArray(file)));
				}

				serviceContainer.getFactoryConverter().getModelMapper()
						.map(typeParameterClass.cast(getServiceContainer().getConverterClass()
								.createInstanceFromClassAndValues(typeParameterClass, params, null)), obj);
				if (params.getFields().get(GenericConstants.SEARCH_PARAMS_FULLTEXT_SEARCH) != null
						&& params.getFields().get(GenericConstants.SEARCH_PARAMS_FULLTEXT_SEARCH).equals(os.getKey())) {
					results.add(obj);
				} else if (params.getFields().get(GenericConstants.SEARCH_PARAMS_FULLTEXT_SEARCH) == null) {
					results.add(obj);
				}
			}
		} catch (Exception e) {
			throw new GenericException("Error search ");
		}
		GenericResults genericResults = new GenericResults(results, Integer.valueOf(results.size()).longValue());
		return genericResults;
	}

	/**
	 * Checks for index.
	 *
	 * @param key the field name
	 * @return the boolean
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	public Boolean hasExecuteSetup(String key) throws GenericException {
		if (connection != null) {
			try {
				return ((AmazonS3Client) connection.getClient()) != null;
			} catch (Exception e) {
				throw new GenericException("Error index " + key);
			}
		}
		return Boolean.FALSE;
	}

	/**
	 * Adds the index.
	 *
	 * @param fieldName the field name
	 * @param typeClass the type class
	 * @throws GenericException Signals that an I/O exception has occurred.
	 */
	@Override
	public void setup(String fieldName, Class typeClass) throws GenericException {
	}

	/**
	 * Adds the item.
	 *
	 * @param <? extends GenericItem> item
	 * @return
	 * @throws GenericException
	 */
	@Override
	public ItemDelegate<? extends GenericItem> addItem(ItemDelegate<? extends GenericItem> item)
			throws GenericException {
		if (!hasExecuteSetup(item.getKey())) {
			setup(item.getKey(), item.getObj().getClass());
		}

		try {
			Optional<java.lang.reflect.Field> fileAnnotation = getServiceContainer().getConverterClass()
					.getFieldFromAnnotation(item.getObj().getClass(), FileUpload.class);
			fileAnnotation.get().setAccessible(true);
			File file = (File) fileAnnotation.get().get(item.getObj());
			Optional<java.lang.reflect.Field> pathField = getServiceContainer().getConverterClass()
					.getAllFields(item.getTypeClass()).stream().filter(field -> {
						return field.getName()
								.equalsIgnoreCase(fileAnnotation.get().getAnnotation(FileUpload.class).fieldPath());
					}).findFirst();
			pathField.get().setAccessible(true);
			Object path = pathField.get().get(item.getObj());
			((AmazonS3Client) connection.getClient()).putObject(item.getKey(), (String) path, file);
		} catch (Exception e) {
			throw new GenericException("Error AddItem:");
		}
		return item;
	}

	private File downloadObject(String bucketName, AmazonS3Client s3client, String pathBucketFilename) {
		S3Object s3object = s3client.getObject(bucketName, pathBucketFilename);
		S3ObjectInputStream inputStream = s3object.getObjectContent();
		String path = magnoliaConfigurationProperties.getProperty("magnolia.upload-file-bucket");
		File destination = null;
		try {
			destination = new File(path + pathBucketFilename);
			FileUtils.copyInputStreamToFile(inputStream, destination);
		} catch (IOException e) {
			log.error("Error downloadObject:", e);
		}
		return destination;
	}

	@Override
	public void setConnection(CustomConnection customConnection) {
		connection = (S3Connection) customConnection;

	}
}
