/*
 *
 */
package info.magnolia.forge.universalcontent.app.custom.interfaces;

import info.magnolia.forge.universalcontent.app.generic.ui.table.CustomTable;
import info.magnolia.ui.workbench.list.ListView;
import info.magnolia.ui.workbench.search.SearchView;

/**
 * Interface Search&View for ES app.
 */
public interface ListSearchViewAppInterface extends ListView, SearchView {
	public CustomTable getTable();

}
