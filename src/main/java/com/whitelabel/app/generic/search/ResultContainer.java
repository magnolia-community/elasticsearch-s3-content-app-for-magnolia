/*
 *
 */
package com.whitelabel.app.generic.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.whitelabel.app.generic.ui.table.RowId;
import com.whitelabel.app.generic.ui.table.RowItem;

import lombok.Data;

/**
 * Pojo containing RowId and Id, it will be used how an transient entity.
 */
@Data
public class ResultContainer {

	/** The ids. */
	List<RowId> ids = new ArrayList<RowId>();

	/** The item indexes. */
	Map<Integer, RowId> itemIndexes;
	/** The item indexes. */
	Map<RowId, RowItem> resultIndexes;

	/**
	 * Instantiates a new result container.
	 */
	public ResultContainer() {
		this.ids = new ArrayList<RowId>();
		this.itemIndexes = new HashMap<>();
	}

	/**
	 * Instantiates a new result container.
	 *
	 * @param ids         the ids
	 * @param itemIndexes the item indexes
	 */
	public ResultContainer(List<RowId> ids, Map<Integer, RowId> itemIndexes, Map<RowId, RowItem> results) {
		super();
		this.ids = ids;
		this.itemIndexes = itemIndexes;
		this.resultIndexes = results;
	}

}
