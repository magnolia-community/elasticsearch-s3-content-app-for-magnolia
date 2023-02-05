/*
 *
 */
package com.whitelabel.app.generic.custom.entity;

import java.io.File;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.whitelabel.app.generic.annotation.FileUpload;
import com.whitelabel.app.generic.annotation.GenericEntity;
import com.whitelabel.app.generic.entity.GenericItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * It's a item must extends {@link GenericItem}.
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@GenericEntity(name = "test-magnolia-connector-s3", fieldId = "id")
public class CustomIndexElasticSearch extends GenericItem {

	/** The id. */
	private String id;

	private String pathFileBucket;

	@FileUpload(fieldPath = "pathFileBucket")
	private File file;
}
