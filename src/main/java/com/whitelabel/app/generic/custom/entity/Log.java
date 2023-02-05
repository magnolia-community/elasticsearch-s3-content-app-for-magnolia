/*
 *
 */
package com.whitelabel.app.generic.custom.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.whitelabel.app.generic.annotation.GenericEntity;
import com.whitelabel.app.generic.entity.GenericItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * * It's a item must extends {@link GenericItem}
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@GenericEntity(name = "test", fieldId = "id")
public class Log extends GenericItem {

	/** The id. */
	private String id;

	/** The level. */
	private String level;

	/** The logger name. */
	private String loggerName;

}
