/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.ui.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.vaadin.server.Resource;
import com.vaadin.v7.data.Item;

import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomListenerPresenter;
import info.magnolia.ui.actionbar.ActionbarPresenter;
import info.magnolia.ui.actionbar.definition.ActionbarDefinition;
import info.magnolia.ui.actionbar.definition.ActionbarGroupDefinition;
import info.magnolia.ui.actionbar.definition.ActionbarItemDefinition;
import info.magnolia.ui.actionbar.definition.ActionbarSectionDefinition;
import info.magnolia.ui.api.action.ActionDefinition;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.api.action.ActionExecutor;
import info.magnolia.ui.api.app.AppContext;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.api.availability.AvailabilityChecker;
import info.magnolia.ui.api.availability.AvailabilityDefinition;
import info.magnolia.ui.api.message.Message;
import info.magnolia.ui.api.message.MessageType;
import info.magnolia.ui.contentapp.browser.BrowserSubAppDescriptor;
import info.magnolia.ui.imageprovider.ImageProvider;
import info.magnolia.ui.vaadin.integration.NullItem;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;
import info.magnolia.ui.workbench.WorkbenchPresenter;
import lombok.AllArgsConstructor;

/**
 * Implementation managing all action you can do on actionbar (on the right
 * column).
 */

/**
 * Instantiates a new action bar service impl.
 *
 * @param workbenchPresenter  the workbench presenter
 * @param subAppDescriptor    the sub app descriptor
 * @param actionExecutor      the action executor
 * @param appContext          the app context
 * @param contentConnector    the content connector
 * @param actionbarPresenter  the actionbar presenter
 * @param availabilityChecker the availability checker
 * @param imageProvider       the image provider
 * @param checker             the checker
 * @param browser             the browser
 * @param subAppContext       the sub app context
 */
@AllArgsConstructor
public class ActionBarServiceImpl implements ActionBarService {

	/** The workbench presenter. */
	private WorkbenchPresenter workbenchPresenter;

	/** The sub app descriptor. */
	private BrowserSubAppDescriptor subAppDescriptor;

	/** The action executor. */
	private ActionExecutor actionExecutor;

	/** The app context. */
	private AppContext appContext;

	/** The content connector. */
	private ContentConnector contentConnector;

	/** The configured actions. */
	private static List<String> configuredActions = Lists.newArrayList();

	/** The actionbar presenter. */
	private ActionbarPresenter actionbarPresenter;

	/** The availability checker. */
	private AvailabilityChecker availabilityChecker;

	/** The image provider. */
	private ImageProvider imageProvider;

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(ActionBarService.class);

	/** The checker. */
	private info.magnolia.ui.availability.AvailabilityChecker checker;

	/** The browser. */
	private CustomListenerPresenter browser;

	/** The sub app context. */
	private SubAppContext subAppContext;

	public ActionBarServiceImpl() {
	}

	/**
	 * Execute default action.
	 */
	@Override
	public void executeDefaultAction() {
		ActionbarDefinition actionbarDefinition = subAppDescriptor.getActionbar();
		if (actionbarDefinition == null) {
			return;
		}
		String defaultAction = actionbarDefinition.getDefaultAction();
		if (StringUtils.isNotEmpty(defaultAction)) {
			executeAction(defaultAction);
		}
	}

	/**
	 * Execute delete action.
	 */
	@Override
	public void executeDeleteAction() {
		ActionbarDefinition actionbarDefinition = subAppDescriptor.getActionbar();
		if (actionbarDefinition == null) {
			return;
		}
		String deleteAction = actionbarDefinition.getDeleteAction();
		if (StringUtils.isNotEmpty(deleteAction)) {
			executeAction(deleteAction);
		}
	}

	/**
	 * Execute action.
	 *
	 * @param actionName the action name
	 */
	@Override
	public void executeAction(String actionName) {
		try {
			AvailabilityDefinition availability = actionExecutor.getActionDefinition(actionName).getAvailability();
			if (availabilityChecker.isAvailable(availability, workbenchPresenter.getSelectedIds())) {
				Object[] args = prepareActionArgs(actionName);
				actionExecutor.execute(actionName, args);
			}
		} catch (ActionExecutionException e) {
			Message error = new Message(MessageType.ERROR, "An error occurred while executing an action.",
					e.getMessage());
			log.error("An error occurred while executing action [{}]", actionName, e);
			appContext.sendLocalMessage(error);
		}
	}

	/**
	 * Execute action.
	 *
	 * @param actionName the action name
	 * @param itemIds    the item ids
	 * @param parameters the parameters
	 */
	@Override
	public void executeAction(String actionName, Set<Object> itemIds, Object... parameters) {
		try {
			AvailabilityDefinition availability = actionExecutor.getActionDefinition(actionName).getAvailability();
			if (availabilityChecker.isAvailable(availability, workbenchPresenter.getSelectedIds())) {
				List<Object> args = new ArrayList<Object>();
				args.add(itemIds);
				args.addAll(Arrays.asList(parameters));
				actionExecutor.execute(actionName, new Object[] { args.toArray(new Object[args.size()]) });
			}
		} catch (ActionExecutionException e) {
			Message error = new Message(MessageType.ERROR, "An error occurred while executing an action.",
					e.getMessage());
			log.error("An error occurred while executing action [{}]", actionName, e);
			appContext.sendLocalMessage(error);
		}
	}

	/**
	 * Prepare action args.
	 *
	 * @param actionName the action name
	 * @return the object[]
	 */
	@Override
	public Object[] prepareActionArgs(String actionName) {
		List<Object> argList = new ArrayList<Object>();
		List<Item> selectedItems = new ArrayList<Item>();
		List<Object> selectedIds = workbenchPresenter.getSelectedIds();

		if (configuredActions.contains(actionName)) {

			if (selectedIds.isEmpty()) {
				selectedIds.add(contentConnector.getDefaultItemId());
			}
			Iterator<Object> idIt = selectedIds.iterator();
			while (idIt.hasNext()) {
				selectedItems.add(contentConnector.getItem(idIt.next()));
			}

			if (selectedItems.size() <= 1) {
				// we have a simple selection; action implementation may expect either an item,
				// either a list parameter, so we have to support both.
				argList.add(selectedItems.isEmpty() ? new NullItem() : selectedItems.get(0));
				argList.add(selectedItems);
			} else {
				// we have a multiple selection; action implementation must support a list
				// parameter, no way around that.
				argList.add(selectedItems);
			}

		} else {
			log.warn("Action with name <{}> is not configured in list <{}> ", actionName,
					configuredActions.stream().collect(Collectors.joining(",")));
			argList.add(selectedIds);
		}

		return argList.toArray(new Object[argList.size()]);
	}

	/**
	 * Refresh actionbar preview image.
	 *
	 * @param itemId the item id
	 */
	@Override
	public void refreshActionbarPreviewImage(Object itemId) {
		Object previewResource = getPreviewImageForId(itemId);
		if (previewResource instanceof Resource) {
			actionbarPresenter.setPreview((Resource) previewResource);
		} else {
			actionbarPresenter.setPreview(null);
		}
	}

	/**
	 * Gets the visible section.
	 *
	 * @param sections the sections
	 * @return the visible section
	 */
	@Override
	public ActionbarSectionDefinition getVisibleSection(List<ActionbarSectionDefinition> sections) {
		for (ActionbarSectionDefinition section : sections) {
			if (isSectionVisible(section))
				return section;
		}
		return null;
	}

	/**
	 * Checks if is section visible.
	 *
	 * @param section the section
	 * @return true, if is section visible
	 */
	private boolean isSectionVisible(ActionbarSectionDefinition section) {
		return checker.isAvailable(section.getAvailability(), browser.getSelectedItemIds());
	}

	/**
	 * Gets the preview image for id.
	 *
	 * @param itemId the item id
	 * @return the preview image for id
	 */
	@Override
	public Object getPreviewImageForId(Object itemId) {
		if (imageProvider != null) {
			return imageProvider.getThumbnailResource(itemId, ImageProvider.PORTRAIT_GENERATOR);
		}
		return null;
	}

	/**
	 * Update actionbar.
	 *
	 * @param actionbar the actionbar
	 */
	@Override
	public void updateActionbar(ActionbarPresenter actionbar) {

		BrowserSubAppDescriptor subAppDescriptor = (BrowserSubAppDescriptor) subAppContext.getSubAppDescriptor();
		ActionbarDefinition actionbarDefinition = subAppDescriptor.getActionbar();
		if (actionbarDefinition == null) {
			return;
		}
		List<ActionbarSectionDefinition> sections = actionbarDefinition.getSections();
		// Figure out which section to show, only one
		ActionbarSectionDefinition sectionDefinition = getVisibleSection(sections);

		// Hide all other sections
		for (ActionbarSectionDefinition section : sections) {
			actionbar.hideSection(section.getName());
		}

		if (sectionDefinition != null) {
			// Show our section
			actionbar.showSection(sectionDefinition.getName());

			// Evaluate availability of each action within the section
			for (ActionbarGroupDefinition groupDefinition : sectionDefinition.getGroups()) {
				for (ActionbarItemDefinition itemDefinition : groupDefinition.getItems()) {

					String actionName = itemDefinition.getName();
					ActionDefinition actionDefinition = actionExecutor.getActionDefinition(actionName);
					if (actionDefinition != null) {
						AvailabilityDefinition availability = actionDefinition.getAvailability();
						if (checker.isAvailable(availability, browser.getSelectedItemIds())) {
							actionbar.enable(actionName);
						} else {
							actionbar.disable(actionName);
						}
					} else {
						log.warn("Action bar expected an action named {}, but no such action is currently configured.",
								actionName);
					}
				}
			}
		}
	}

}
