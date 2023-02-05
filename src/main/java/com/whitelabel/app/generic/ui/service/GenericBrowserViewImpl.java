/*
 *
 */
package com.whitelabel.app.generic.ui.service;

import java.util.ArrayList;
import java.util.Iterator;

import javax.inject.Inject;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.whitelabel.app.custom.interfaces.CustomTwoColumnView;

import info.magnolia.ui.api.view.View;
import info.magnolia.ui.workbench.column.definition.ColumnDefinition;
import info.magnolia.ui.workbench.definition.ContentPresenterDefinition;
import info.magnolia.ui.workbench.definition.WorkbenchDefinition;
import lombok.Data;

/**
 * Represent View containing HorizontalPanel layout and left/right view UI.
 */

@Data
public class GenericBrowserViewImpl implements CustomTwoColumnView {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The listener. */
	private Listener listener;

	/** The left view. */
	private Component leftView;

	/** The right view. */
	private Component rightView;

	/** The layout. */
	private final HorizontalSplitPanel layout;

	/**
	 * Instantiates a new manage es browser view impl.
	 */
	@Inject
	public GenericBrowserViewImpl() {
		layout = new HorizontalSplitPanel();
		layout.setSplitPosition(35);
	}

	/**
	 * As vaadin component.
	 *
	 * @return the component
	 */
	@Override
	public Component asVaadinComponent() {
		return layout;
	}

	/**
	 * Sets the listener.
	 *
	 * @param listener the new listener
	 */
	@Override
	public void setListener(Listener listener) {
		this.listener = listener;
	}

	/**
	 * Sets the left view.
	 *
	 * @param left the new left view
	 */
	@Override
	public void setLeftView(View left) {
		this.leftView = left.asVaadinComponent();
		layout.setFirstComponent(leftView);

	}

	/**
	 * Sets the right view.
	 *
	 * @param right the new right view
	 */
	@Override
	public void setRightView(View right) {
		this.rightView = right.asVaadinComponent();
		layout.setSecondComponent(rightView);
	}

	/**
	 * Checks for columns.
	 *
	 * @param workbenchDefinition the workbench definition
	 * @return the boolean
	 */
	public static Boolean hasColumns(WorkbenchDefinition workbenchDefinition) {
		for (Iterator iterator = workbenchDefinition.getContentViews().iterator(); iterator.hasNext();) {
			ContentPresenterDefinition contentView = (ContentPresenterDefinition) iterator.next();
			if (contentView.getColumns() == null && contentView instanceof GenericListPresenterDefinition) {
				GenericListPresenterDefinition customContentViewDefinition = (GenericListPresenterDefinition) contentView;
				customContentViewDefinition.setColumns(new ArrayList<ColumnDefinition>());
				return true;
			}
		}
		return true;
	}
}
