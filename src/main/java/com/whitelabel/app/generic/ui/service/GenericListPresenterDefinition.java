/*
 *
 */

package com.whitelabel.app.generic.ui.service;

import java.util.List;

import com.whitelabel.app.generic.entity.GenericItem;

import info.magnolia.ui.workbench.column.definition.ColumnDefinition;
import info.magnolia.ui.workbench.list.ListPresenterDefinition;
import lombok.Data;

/**
 * Definition of GenericListPresenter for be defined in yaml files.
 */
@Data
public class GenericListPresenterDefinition extends ListPresenterDefinition {

	/** The columns. */
	private List<ColumnDefinition> columns;

	/** Item Class */
	private Class<? extends GenericItem> itemClass;

	/**
	 * Instantiates a new Generic list presenter definition.
	 */
	public GenericListPresenterDefinition() {
		setViewType(GenericListPresenterDefinition.class.getName());
		setImplementationClass(GenericListPresenter.class);
	}

	/**
	 * Gets the columns.
	 *
	 * @return the columns
	 */
	@Override
	public List<ColumnDefinition> getColumns() {
		return super.getColumns();
	}

	/**
	 * Sets the columns.
	 *
	 * @param columns the new columns
	 */
	@Override
	public void setColumns(List<ColumnDefinition> columns) {
		super.setColumns(columns);
	}

}
