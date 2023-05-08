/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.ui.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Container;

import info.magnolia.context.MgnlContext;
import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomContainer;
import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomTwoColumnView;
import info.magnolia.forge.universalcontent.app.custom.interfaces.ListSearchViewAppInterface;
import info.magnolia.forge.universalcontent.app.custom.interfaces.Listener;
import info.magnolia.forge.universalcontent.app.event.AddItem;
import info.magnolia.forge.universalcontent.app.generic.annotation.DelegateImplementation;
import info.magnolia.forge.universalcontent.app.generic.annotation.Format;
import info.magnolia.forge.universalcontent.app.generic.connection.ParameterConnection;
import info.magnolia.forge.universalcontent.app.generic.connector.FactoryContainer;
import info.magnolia.forge.universalcontent.app.generic.connector.GenericDelegate;
import info.magnolia.forge.universalcontent.app.generic.entity.GenericItem;
import info.magnolia.forge.universalcontent.app.generic.format.PlainCellColumnFormatter;
import info.magnolia.forge.universalcontent.app.generic.search.GenericParamsBuilder;
import info.magnolia.forge.universalcontent.app.generic.search.Params;
import info.magnolia.forge.universalcontent.app.generic.search.ParamsAdapter;
import info.magnolia.forge.universalcontent.app.generic.service.RepositoryService;
import info.magnolia.forge.universalcontent.app.generic.ui.AdapterField;
import info.magnolia.forge.universalcontent.app.generic.ui.CustomFieldFilter;
import info.magnolia.forge.universalcontent.app.generic.ui.CustomUi;
import info.magnolia.forge.universalcontent.app.generic.ui.FactoryCustomUi;
import info.magnolia.forge.universalcontent.app.generic.ui.FieldProperty;
import info.magnolia.forge.universalcontent.app.generic.ui.GroupCustomUi;
import info.magnolia.forge.universalcontent.app.generic.ui.table.CustomTable;
import info.magnolia.forge.universalcontent.app.generic.ui.table.GenericColumnDefinitions;
import info.magnolia.forge.universalcontent.app.generic.utils.GenericConstants;
import info.magnolia.forge.universalcontent.app.generic.utils.MyConverterFactory;
import info.magnolia.ui.workbench.column.definition.ColumnFormatter;

/**
 * UI events management implementation Click/select method when events occurs.
 */
public class UiEventActionImpl implements UiEventAction {
	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(UiEventActionImpl.class);
	/** The layout. */
	private VerticalLayout layout;

	/** The listener. */
	private Listener listener;

	/** The elastic search container. */
	@Inject
	private CustomContainer customContainer;

	/** The product browser view. */
	private CustomTwoColumnView productBrowserView;

	/** The log list view. */
	@Inject
	private ListSearchViewAppInterface logListView;

	@Inject
	private RepositoryService serviceContainer;

	/**
	 * Instantiates a new ui event action impl.
	 *
	 * @param layout                      the layout
	 * @param listener                    the listener
	 * @param factoryElasticSearchManager the factory elastic search manager
	 */
	public UiEventActionImpl(VerticalLayout layout, Listener listener) {
		super();
		this.layout = layout;
		this.listener = listener;
	}

	/**
	 * Click on search.
	 *
	 * @return the runnable
	 */
	@Override
	public Runnable clickOnSearch() {
		return () -> {
			listener.onSearchConfiguration(fillParamsItem());
		};
	}

	/**
	 * Click on add.
	 *
	 * @return the runnable
	 */
	@Override
	public Runnable clickOnAdd() {
		return () -> {
			listener.addItem(getNewItem(), fillParamsItem());
		};
	}

	/**
	 * Gets the new item.
	 *
	 * @return the new item
	 */
	private AddItem getNewItem() {
		customContainer = serviceContainer.getFactoryContainer().getActualContainer();
		if (customContainer != null) {
			Params params = fillParamsItem();

			Class<? extends GenericItem> classType = serviceContainer.getConverterClass().getClassItem(params,
					params.getFields().get("index"), GenericItem.class);
			AddItem addItem = null;
			try {
				addItem = new AddItem(
						serviceContainer.getConverterClass().createInstanceFromClassAndValues(classType, params, null),
						classType);
			} catch (InstantiationException | IllegalAccessException e) {
				log.error("Error New Item", e);
			}
			return addItem;
		}
		return null;
	}

	/**
	 * Gets value items inserted
	 *
	 * @return params
	 */
	@Override
	public Params fillParamsItem() {
		Params params = new Params();
		ParamsAdapter paramsSearchAdapter = new ParamsAdapter(params, serviceContainer);

		params.setSize(GenericConstants.SEARCH_PARAMS_DEFAULT_NUMBER_PAGE);
		params.setOffset(GenericConstants.SEARCH_PARAMS_DEFAULT_OFFSET_PAGE);
		params.setSizePage(GenericConstants.SEARCH_PARAMS_DEFAULT_SIZE_PAGE);

		for (Iterator iterator = layout.iterator(); iterator.hasNext();) {
			Component component = (Component) iterator.next();
			if (component instanceof TabSheet) {
				TabSheet tabSheet = (TabSheet) component;
				Component c = tabSheet.getSelectedTab();
				if (c instanceof VerticalLayout) {
					VerticalLayout childLayout = (VerticalLayout) c;
					for (Iterator<Component> it = childLayout.getComponentIterator(); it.hasNext();) {
						Component childComponent = it.next();
						params = paramsSearchAdapter.fillParamsSearch(childComponent);
					}
				}
			}
			params = paramsSearchAdapter.fillParamsSearch(component);
		}
		String nameIndex = (String) params.getFields().get(GenericConstants.FILTER_INDEX);
		if (StringUtils.isNotEmpty(nameIndex)) {
			params.setClassType(serviceContainer.getConverterClass().getClassFromParams(params, GenericItem.class));
		} else {
			Class indexClass = (Class) MgnlContext.getWebContext().getRequest().getSession()
					.getAttribute(GenericConstants.SELECT_INDEX_KEY);
			params.setClassType(indexClass);
			if (indexClass != null) {
				params.getFields().put(GenericConstants.FILTER_INDEX, indexClass.getName());
			}
		}
		params.getFields().put(GenericConstants.FILTER_INDEX, params.getFields().get(GenericConstants.FILTER_INDEX));
		if (params.getFields().get(GenericConstants.SELECT_SOURCE) != null
				&& params.getFields().get(GenericConstants.SELECT_SOURCE) instanceof String) {
			params.setSource(serviceContainer.getConverterClass()
					.getClassFromName((String) params.getFields().get(GenericConstants.SELECT_SOURCE)));
		}
		return params;
	}

	/**
	 * Click on change num page.
	 *
	 * @return the consumer
	 */
	@Override
	public Consumer<CustomFieldFilter> clickOnChangeNumPage() {
		return (event) -> {
			listener.onSearchConfiguration(fillParamsItem());
		};
	}

	/**
	 * Click on select source
	 *
	 * @return the runnable
	 */
	@Override
	public Runnable clickOnSourceConnect() {
		return () -> {
			Params params = fillParamsItem();

			Class<? extends GenericDelegate> classType = serviceContainer.getConverterClass().getClassItem(params,
					params.getFields().get("source"), GenericDelegate.class);
			GenericDelegate delegate = serviceContainer.getUiService().getComponentProvider().getComponent(classType);
			Class classParameterConnection = delegate.getClass().getAnnotation(DelegateImplementation.class)
					.parameterClass();
			;
			GenericItem createInstanceFromClassAndValues;
			try {
				ParameterConnection p = null;
				if (delegate.getConnection() != null) {
					p = delegate.getConnection().getParams();
				}
				ParameterConnection parameterConnection = serviceContainer.getConverterClass()
						.createInstanceFromClassAndValues(classParameterConnection, params, p);

				delegate.getConnection().setParams(parameterConnection);
				delegate.getConnection().connect();
			} catch (Exception e) {
				log.error("Click on source connect", e);
				delegate.setConnection(null);
			}
		};
	}

	/**
	 * Select item
	 *
	 * @return the consumer
	 */
	@Override
	public Consumer<CustomFieldFilter> selectSource() {
		return (event) -> {
			Params params = fillParamsItem();
//			Clean all fieldText to recent selected index
			CustomUi<CustomFieldFilter> create = FactoryCustomUi.create(layout, CustomFieldFilter.class);

			create.setVisibilityComponent(GenericConstants.TAB_NAME_CONFIGURATION,
					GenericConstants.BUTTON_SOURCE_CONNECT, Boolean.TRUE);
			create.clearAllFieldTextInTab(GenericConstants.TAB_NAME_MANAGE, GenericConstants.GROUP_FILTER_ID);
			create.clearAllFieldTextInTab(GenericConstants.TAB_NAME_SEARCH, GenericConstants.GROUP_BOOST_ID);
			create.clearAllFieldTextInTab(GenericConstants.TAB_NAME_CONFIGURATION, GenericConstants.GROUP_FILTER_ID);
			AdapterField createField = field -> {
				return field.getField().getName();
			};
			GenericDelegate delegate;
			List<Field> allFields = null;
			try {
				delegate = (GenericDelegate) serviceContainer.getUiService().getComponentProvider()
						.getComponent(params.getSource());
				delegate.setServiceContainer(serviceContainer);

				Class paramClass = delegate.getClass().getAnnotation(DelegateImplementation.class).parameterClass();
				allFields = serviceContainer.getConverterClass().getAllFields(paramClass);
				Runnable clickOnSource = clickOnSourceConnect();

				CustomUi<CustomFieldFilter> customUI = FactoryCustomUi.create(layout, CustomFieldFilter.class);

//				Create manage Tab
				GroupCustomUi<CustomFieldFilter> tabManage = customUI.createTab(Boolean.TRUE)
						.nameTab(GenericConstants.TAB_NAME_CONFIGURATION, GenericConstants.TAB_GROUP_ITEM_MANAGE);

				serviceContainer.getContentConnector().setQueryDelegate(delegate);
				CustomUi<CustomFieldFilter> layout = customUI.createFieldsFromTab(
						GenericConstants.TAB_NAME_CONFIGURATION, allFields, GenericConstants.GROUP_FILTER_ID,
						createField, clickOnSource);

				create.clearAllFieldTextInTab(GenericConstants.TAB_NAME_MANAGE, GenericConstants.GROUP_FILTER_ID);
				create.clearAllFieldTextInTab(GenericConstants.TAB_NAME_SEARCH, GenericConstants.GROUP_BOOST_ID);
//		Create all fieldText to new selected index

				create.setVisibilityTabSheet(GenericConstants.TAB_NAME_MANAGE, Boolean.FALSE);
				create.setVisibilityTabSheet(GenericConstants.TAB_NAME_SEARCH, Boolean.TRUE);
				customContainer.getContentConnector().get().getQueryDelegate();
			} catch (Exception e) {
				log.error("Error configuration", e);
			}

		};
	}

	/**
	 * Select item
	 *
	 * @return the consumer
	 */
	@Override
	public Consumer<CustomFieldFilter> selectGenericItem() {
		return (event) -> {
//			Remove cache
			serviceContainer.getCacheHelper().removeAllCachedItems();
			serviceContainer.getCacheHelper().removeAllCachedResults();
			serviceContainer.getCacheHelper().removeAllFactoryConvert();

			Params params = fillParamsItem();

			Params paramsClassType = GenericParamsBuilder.createSearch(serviceContainer).params(params)
					.addField(event.getField(), event.getValue()).get();
			CustomUi<CustomFieldFilter> create = FactoryCustomUi.create(layout, CustomFieldFilter.class);
			create.setVisibilityTabSheet(GenericConstants.TAB_NAME_SEARCH, Boolean.FALSE);
			params.setClassType(paramsClassType.getClassType());

			if (paramsClassType.getClassType() != null) {
				try {
					customContainer.createConnection(params);
					serviceContainer.getFactoryContainer().create(params.getClassType(), customContainer);
				} catch (Exception e) {
					log.error("Error SelectIndex:", e);
					return;
				}
				List<Field> allFields = serviceContainer.getConverterClass().getAllFields(params.getClassType());
				VaadinSession.getCurrent().setConverterFactory(new MyConverterFactory());
				serviceContainer.getCustomContainer().refresh();
				CustomTable.removeColumns(logListView);

				List<Field> fieldsBoosted = serviceContainer.getConverterClass().getAllFields(params.getClassType())
						.stream().filter(field -> {
							return serviceContainer.getConverterClass().isBoostField(field);
						}).collect(Collectors.toList());
				AdapterField createBoostField = field -> {
					String nameField = serviceContainer.getConverterClass().getFieldNameBoostField(field.getField());
					return nameField;
				};
				AdapterField createField = field -> {
					return field.getField().getName();
				};
				// Clean all fieldText to recent selected index
				create.setVisibilityComponent(GenericConstants.TAB_NAME_MANAGE, GenericConstants.BUTTON_ADD_LABEL,
						Boolean.TRUE);

				create.clearAllFieldTextInTab(GenericConstants.TAB_NAME_MANAGE, GenericConstants.GROUP_FILTER_ID);
				create.clearAllFieldTextInTab(GenericConstants.TAB_NAME_SEARCH, GenericConstants.GROUP_BOOST_ID);
				// Create all fieldText to new selected index
				create.createFieldsFromTab(GenericConstants.TAB_NAME_SEARCH, fieldsBoosted,
						GenericConstants.GROUP_BOOST_ID, createBoostField, null);
				create.createFieldsFromTab(GenericConstants.TAB_NAME_MANAGE, allFields,
						GenericConstants.GROUP_FILTER_ID, createField, null);
				// Modify Column Table
				for (Field field : allFields) {
					Class<? extends ColumnFormatter> classFormatter = PlainCellColumnFormatter.class;
					int width = 55;
					float expandWidth = 3.5F;
					if (field.getAnnotation(Format.class) != null) {
						classFormatter = field.getAnnotation(Format.class).classFormatter();
						width = field.getAnnotation(Format.class).width();
						expandWidth = field.getAnnotation(Format.class).expandWidth();
					}
					GenericColumnDefinitions customColumn = new GenericColumnDefinitions(field.getName(),
							field.getName(), field.getName(), width, expandWidth, classFormatter, String.class, null,
							true, true, true, true, true);
					CustomTable.addColumn(customColumn, logListView,
							serviceContainer.getUiService().getComponentProvider());

				}
				// Visible true Add Button
				FieldProperty property = new FieldProperty(GenericConstants.BUTTON_ADD_LABEL, null, Boolean.TRUE,
						GenericConstants.BUTTON_ADD_LABEL);
				List<FieldProperty> fields = new ArrayList<>();
				fields.add(property);
				FactoryCustomUi.create(layout, serviceContainer.getFactoryContainer().getClassType())
						.updateFieldsFromTab(GenericConstants.TAB_NAME_MANAGE, fields);

				MgnlContext.getWebContext().getRequest().getSession().setAttribute(GenericConstants.SELECT_INDEX_KEY,
						params.getClassType());

				MgnlContext.getWebContext().getRequest().getSession().removeAttribute(GenericConstants.SEARCH_PARAMS);
				clickOnSearch();
			}
		};
	}

	/**
	 * Gets the layout.
	 *
	 * @return the layout
	 */
	@Override
	public VerticalLayout getLayout() {
		return layout;
	}

	/**
	 * Gets the listener.
	 *
	 * @return the listener
	 */
	@Override
	public Listener getListener() {
		return listener;
	}

	/**
	 * Gets custom container.
	 *
	 * @return the custom container
	 */
	@Override
	public Container getCustomContainer() {
		return customContainer;
	}

	/**
	 * Sets the layout.
	 *
	 * @param layout the new layout
	 */
	@Override
	public void setLayout(VerticalLayout layout) {
		this.layout = layout;
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
	 * Sets the custom container.
	 *
	 * @param customContainer the new custom container
	 */
	@Override
	public void setCustomContainer(Container customContainer) {
		this.customContainer = (CustomContainer) customContainer;
	}

	/**
	 * Sets the factory container.
	 *
	 * @param factory
	 */
	@Override
	public void setFactoryContainer(FactoryContainer factory) {
		this.serviceContainer.setFactory(factory);
	}

	/**
	 * Sets the product browser view.
	 *
	 * @param productBrowserView the new product browser view
	 */
	@Override
	public void setProductBrowserView(CustomTwoColumnView productBrowserView) {
		this.productBrowserView = productBrowserView;

	}

}
