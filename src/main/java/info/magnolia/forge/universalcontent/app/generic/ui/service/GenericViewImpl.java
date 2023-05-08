/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.ui.service;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomTwoColumnView;
import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomView;
import info.magnolia.forge.universalcontent.app.custom.interfaces.ListSearchViewAppInterface;
import info.magnolia.forge.universalcontent.app.custom.interfaces.Listener;
import info.magnolia.forge.universalcontent.app.custom.interfaces.QueryDelegate;
import info.magnolia.forge.universalcontent.app.generic.annotation.DelegateImplementation;
import info.magnolia.forge.universalcontent.app.generic.entity.GenericItem;
import info.magnolia.forge.universalcontent.app.generic.search.Params;
import info.magnolia.forge.universalcontent.app.generic.service.RepositoryService;
import info.magnolia.forge.universalcontent.app.generic.ui.CustomFieldFilter;
import info.magnolia.forge.universalcontent.app.generic.ui.CustomUi;
import info.magnolia.forge.universalcontent.app.generic.ui.FactoryCustomUi;
import info.magnolia.forge.universalcontent.app.generic.ui.GroupCustomUi;
import info.magnolia.forge.universalcontent.app.generic.utils.GenericConstants;
import lombok.Data;

@Data
public class GenericViewImpl implements CustomView {

	/** The log. */
	private Logger log = org.slf4j.LoggerFactory.getLogger(GenericViewImpl.class);

	/** The ui event action. */
	@Inject
	private UiEventAction uiEventAction;

	/** The listener. */
	private Listener listener;

	/** The params. */
	private Params params;

	/** The layout. */
	private VerticalLayout layout;

	/** The log list view. */
	private ListSearchViewAppInterface logListView;

	/** The custom two column view. */
	private CustomTwoColumnView customTwoColumnView;

	@Inject
	private RepositoryService serviceContainer;

	/**
	 * Gets the listener.
	 *
	 * @return the listener
	 */
	public Listener getListener() {
		return this.listener;
	}

	/**
	 * Instantiates a new manage ES view impl.
	 *
	 * @param logListView         the log list view
	 * @param workbenchPresenter  the workbench presenter
	 * @param customTwoColumnView the custom two column view
	 */
	@Inject
	public GenericViewImpl(ListSearchViewAppInterface logListView, CustomTwoColumnView customTwoColumnView) {
		this.logListView = logListView;
		this.customTwoColumnView = customTwoColumnView;
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
	 * As vaadin component.
	 *
	 * @return the component
	 */
	@Override
	public Component asVaadinComponent() {
		if (layout == null) {
			layout = new VerticalLayout();
			layout.setMargin(true);
			layout.setSpacing(true);

			List<String> nameClasses = serviceContainer.getConverterClass().getAllClassGenericItem(GenericItem.class)
					.stream().map(classObj -> {
						return classObj.getName();
					}).collect(Collectors.toList());
			uiEventAction.setFactoryContainer(serviceContainer.getFactoryContainer());
			uiEventAction.setLayout(layout);
			uiEventAction.setListener(listener);

			Runnable clickOnAdd = uiEventAction.clickOnAdd();
			Consumer<CustomFieldFilter> selectOnIndex = uiEventAction.selectGenericItem();
			CustomUi<CustomFieldFilter> customUI = FactoryCustomUi.create(layout, CustomFieldFilter.class);
//			Create manage Tab
			List<String> listSources = serviceContainer.getConverterClass().getAllClassGenericItem(QueryDelegate.class)
					.stream().filter(classObj -> {
						return classObj.getAnnotation(DelegateImplementation.class) != null;
					}).map(classObj -> {
						return classObj.getName();
					}).collect(Collectors.toList());
			Consumer<CustomFieldFilter> selectSource = uiEventAction.selectSource();
			try {
				customUI.createTab(Boolean.FALSE)
						.nameTab(GenericConstants.TAB_NAME_CONFIGURATION, GenericConstants.TAB_NAME_CONFIGURATION)
						.addSelect(GenericConstants.SELECT_SOURCE, selectSource,
								GenericConstants.SELECT_SOURCE_PLACEHOLDER, listSources, GenericConstants.SELECT_SOURCE)
						.addButtom(GenericConstants.BUTTON_SOURCE_CONNECT, GenericConstants.BUTTON_SOURCE_CONNECT,
								Boolean.FALSE, uiEventAction.clickOnSourceConnect())
						.build();
			} catch (InstantiationException | IllegalAccessException e1) {
				log.error("Error create tab Configuration", e1);
			}

			;
			GroupCustomUi<CustomFieldFilter> tabManage = customUI.createTab(Boolean.TRUE)
					.nameTab(GenericConstants.TAB_NAME_MANAGE, GenericConstants.TAB_GROUP_ITEM_MANAGE);
			try {
				tabManage.addSelect(GenericConstants.BUTTON_SELECT_INDEX_LABEL, selectOnIndex,
						GenericConstants.BUTTON_SELECT_INDEX_PLACEHOLDER, nameClasses, GenericConstants.FILTER_INDEX);
			} catch (InstantiationException | IllegalAccessException e) {
				log.error("Error Ui Creation", e);
			}
			tabManage.addButtom(GenericConstants.BUTTON_ADD_LABEL, GenericConstants.BUTTON_ADD_LABEL, Boolean.FALSE,
					clickOnAdd).build();
			customUI.createTab(Boolean.TRUE)
					.nameTab(GenericConstants.TAB_NAME_SEARCH, GenericConstants.TAB_GROUP_ITEM_SEARCH)
					.addTextField(GenericConstants.SEARCH_TEXT_ID, "Search", "Search by word ...")
					.addFilter(GenericConstants.SEARCH_PARAMS_FULLTEXT_SEARCH, "").endGroup()
					.addButtom(GenericConstants.BUTTON_SEARCH_LABEL, GenericConstants.BUTTON_SEARCH_LABEL, Boolean.TRUE,
							uiEventAction.clickOnSearch())
					.build().build();
		}
		return layout;
	}

}
