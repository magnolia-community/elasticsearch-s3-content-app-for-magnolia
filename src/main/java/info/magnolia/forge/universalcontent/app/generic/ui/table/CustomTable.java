/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.ui.table;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.v7.data.Container;

import info.magnolia.context.MgnlContext;
import info.magnolia.forge.universalcontent.app.generic.others.GenericException;
import info.magnolia.forge.universalcontent.app.generic.others.LogStatus;
import info.magnolia.forge.universalcontent.app.generic.search.Params;
import info.magnolia.forge.universalcontent.app.generic.service.RepositoryService;
import info.magnolia.forge.universalcontent.app.generic.utils.GenericConstants;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.vaadin.grid.MagnoliaTable;
import info.magnolia.ui.workbench.list.ListView;
import lombok.Getter;
import lombok.Setter;

/**
 * Custom table UI
 */
public class CustomTable extends MagnoliaTable {

	/** The sort map. */
	Map<String, Boolean> sortMap;
	@Getter
	@Setter
	private RepositoryService serviceContainer;

	/**
	 * Instantiates a new custom table.
	 */
	public CustomTable() {
		sortMap = new HashMap<>();
	}

	/**
	 * Instantiates a new custom table.
	 *
	 * @param dataSource       the data source
	 * @param serviceContainer TODO
	 * @param appEventBus      the app event bus
	 * @param uiEventAction    the ui event action
	 */
	public CustomTable(Container dataSource) {
		super(dataSource);
		sortMap = new HashMap<>();
		this.setSortEnabled(true);
		this.setSortAscending(true);
		this.setSortDisabled(false);
		this.setWidthFull();
		if (this.getVisibleColumns() != null && this.getVisibleColumns().length > 0) {
			this.setSortContainerPropertyId(this.getVisibleColumns()[1]);
		}
	}

	/**
	 * Sort.
	 *
	 * @param propertyId the property id
	 * @param ascending  the ascending
	 * @throws UnsupportedOperationException the unsupported operation exception
	 */
	@Override
	public void sort(Object[] propertyId, boolean[] ascending) throws UnsupportedOperationException {
		super.sort(propertyId, ascending);
	}

	/**
	 * Sort.
	 */
	@Override
	public void sort() {
		Object[] sort = new Object[1];
		boolean[] ascSorted = new boolean[1];
		sort[0] = getSortContainerPropertyId();
		Boolean sortOption = true;
		if (sortMap.get(sort[0]) != null) {
			sortMap.put((String) sort[0], !sortMap.get(sort[0]));
		} else {
			sortMap.put((String) sort[0], sortOption);
		}

		ascSorted[0] = sortMap.get(sort[0]);
		sort(sort, ascSorted);
	}

	/**
	 * Container item set change.
	 *
	 * @param event the event
	 */
	@Override
	public void containerItemSetChange(Container.ItemSetChangeEvent event) {
		try {
			Object productsresult = MgnlContext.getWebContext().getRequest().getSession().getAttribute("SEARCH");
			if (productsresult != null && productsresult instanceof Integer) {
				this.setCaption("<h3>" + productsresult + " products found</h3>");
				this.setCaptionAsHtml(true);
			}
			MgnlContext.getWebContext().getRequest().getSession().removeAttribute("SEARCH");

		} catch (Exception e) {
			serviceContainer.getLogService().logger(LogStatus.ERROR,
					"Exception caught reading products search results within table", CustomTable.class, e);
		}
		super.containerItemSetChange(event);
	}

	/**
	 * Removes the columns.
	 *
	 * @param view the view
	 */
	public static void removeColumns(ListView view) {
		view.clearColumns();
	}

	/**
	 * Adds the column.
	 *
	 * @param column            the column
	 * @param view              the view
	 * @param componentProvider the component provider
	 * @return the custom column definitions
	 */
	public static GenericColumnDefinitions addColumn(GenericColumnDefinitions column, ListView view,
			ComponentProvider componentProvider) {

		String propertyId = column.getPropertyName();
		String title = column.getLabel();

		if (column.getWidth() > 0) {
			view.addColumn(propertyId, title, column.getWidth());
		} else if (column.getExpandRatio() > 0) {
			view.addColumn(propertyId, title, column.getExpandRatio());
		} else {
			view.addColumn(propertyId, column.getLabel());
		}

		if (column.getFormatterClass() != null) {
			view.setColumnFormatter(propertyId, componentProvider.newInstance(column.getFormatterClass(), column));
		}
		return column;
	}

	/**
	 * Change variables.
	 *
	 * @param source    the source
	 * @param variables the variables
	 */
	@Override
	public void changeVariables(Object source, Map<String, Object> variables) {
		super.changeVariables(source, variables);
		Integer lastToBeRendered = null;
		Integer firstvisible = null;
		try {
			lastToBeRendered = (Integer) variables.get("lastToBeRendered");
			firstvisible = (Integer) variables.get("firstvisible");
		} catch (Exception e) {
			serviceContainer.getLogService().logger(LogStatus.ERROR, "Error conversion item visble", CustomTable.class,
					e);
		}
		if (lastToBeRendered != null && firstvisible != null
				&& (((lastToBeRendered - firstvisible) < GenericConstants.SEARCH_PARAMS_DEFAULT_SIZE_PAGE)
						|| (lastToBeRendered) < GenericConstants.SEARCH_PARAMS_DEFAULT_SIZE_PAGE)) {
			Object params = MgnlContext.getWebContext().getRequest().getSession()
					.getAttribute(GenericConstants.SEARCH_PARAMS);
			Params convertParams = null;
			if (params != null && params instanceof Params) {
				convertParams = (Params) MgnlContext.getWebContext().getRequest().getSession()
						.getAttribute(GenericConstants.SEARCH_PARAMS);
			} else {
				convertParams = serviceContainer.getUiService().getUiEventAction().fillParamsItem();
			}
			try {
				if (serviceContainer.getCustomContainer().getContentConnector().get().getResultset() != null
						&& serviceContainer.getCustomContainer().getContentConnector().get().getResultset()
								.getTotalSize() > (convertParams.getSize())) {
					convertParams.setSize(convertParams.getSize() + convertParams.getSizePage());
					serviceContainer.getCustomContainer().refreshDelegate(convertParams);
				}
			} catch (GenericException e) {
				serviceContainer.getLogService().logger(LogStatus.ERROR, "Error change Variables", CustomTable.class,
						e);
			}
			MgnlContext.getWebContext().getRequest().getSession().setAttribute(GenericConstants.SEARCH_PARAMS,
					convertParams);
			Class indexClass = (Class) MgnlContext.getWebContext().getRequest().getSession()
					.getAttribute(GenericConstants.SELECT_INDEX_KEY);
			convertParams.setClassType(indexClass);
			convertParams.getFields().put(GenericConstants.FILTER_INDEX, indexClass.getName());
		}
	}

	/**
	 * Checks if is last id.
	 *
	 * @param itemId the item id
	 * @return true, if is last id
	 */
	@Override
	public boolean isLastId(Object itemId) {
		return super.isLastId(itemId);
	}

	@Override
	public int size() {
		return this.getContainerDataSource().size();
	}

	@Override
	public Object[] getVisibleColumns() {
		return this.getContainerDataSource().getContainerPropertyIds().toArray();
	}

}
