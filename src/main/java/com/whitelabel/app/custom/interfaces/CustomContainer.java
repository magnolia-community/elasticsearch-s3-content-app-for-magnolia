package com.whitelabel.app.custom.interfaces;

import java.util.Optional;

import com.vaadin.v7.data.Container;
import com.whitelabel.app.generic.entity.GenericItem;
import com.whitelabel.app.generic.others.GenericException;
import com.whitelabel.app.generic.search.Params;

import info.magnolia.ui.workbench.container.Refreshable;

public interface CustomContainer extends Container.Hierarchical, Container.ItemSetChangeNotifier, Refreshable,
		Container.Ordered, Container.Sortable, Container.Filterable, Container.Indexed, Container {
	public <T extends GenericItem> void createCustomContainer(Params params) throws GenericException;

	public Optional<CustomContentConnector> getContentConnector();

	public void setContentConnector(Optional<CustomContentConnector> opt);

	void createConnection(Params params) throws Exception;

	public void refreshDelegate(Params params) throws GenericException;
}
