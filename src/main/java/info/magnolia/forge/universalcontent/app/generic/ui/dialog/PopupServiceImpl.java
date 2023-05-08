/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.ui.dialog;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.ExternalResource;
import com.vaadin.ui.MenuBar.MenuItem;

import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomListenerPresenter;
import info.magnolia.ui.actionbar.definition.ActionbarDefinition;
import info.magnolia.ui.actionbar.definition.ActionbarGroupDefinition;
import info.magnolia.ui.actionbar.definition.ActionbarItemDefinition;
import info.magnolia.ui.actionbar.definition.ActionbarSectionDefinition;
import info.magnolia.ui.api.action.ActionDefinition;
import info.magnolia.ui.api.action.ActionExecutor;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.api.availability.AvailabilityDefinition;
import info.magnolia.ui.availability.AvailabilityChecker;
import info.magnolia.ui.contentapp.browser.BrowserSubAppDescriptor;
import info.magnolia.ui.vaadin.actionbar.ActionPopup;

/**
 * Implementation for manage all actions that should be done with popup.
 */
public class PopupServiceImpl implements PopupService {

	/** The browser. */
	private CustomListenerPresenter browser;

	/** The sub app context. */
	private SubAppContext subAppContext;

	/** The action executor. */
	private ActionExecutor actionExecutor;

	/** The checker. */
	private info.magnolia.ui.availability.AvailabilityChecker checker;

	/** The Constant log. */
	private static final Logger log = LoggerFactory.getLogger(PopupService.class);

	private ActionBarService actionBarService;

	/**
	 * Instantiates a new popup service impl.
	 *
	 * @param browser        the browser
	 * @param subAppContext  the sub app context
	 * @param actionExecutor the action executor
	 * @param checker        the checker
	 * @param popupService   the popup service
	 */
	@Inject
	public PopupServiceImpl(CustomListenerPresenter browser, SubAppContext subAppContext, ActionExecutor actionExecutor,
			AvailabilityChecker checker, ActionBarService actionBarService) {
		this.browser = browser;
		this.subAppContext = subAppContext;
		this.actionExecutor = actionExecutor;
		this.checker = checker;
		this.subAppContext = subAppContext;
		this.actionBarService = actionBarService;
	}

	/**
	 * Show action popup.
	 *
	 * @param itemId the item id
	 * @param x      the x
	 * @param y      the y
	 */
	@Override
	public void showActionPopup(Object itemId, int x, int y) {

		// If there's no actionbar configured we don't want to show an empty action
		// popup
		BrowserSubAppDescriptor subAppDescriptor = (BrowserSubAppDescriptor) subAppContext.getSubAppDescriptor();
		ActionbarDefinition actionbarDefinition = subAppDescriptor.getActionbar();
		if (actionbarDefinition == null) {
			return;
		}

		ActionPopup actionPopup = browser.getView().getActionPopup();

		updateActionPopup(actionPopup);
		actionPopup.open(x, y);
	}

	/**
	 * Update action popup.
	 *
	 * @param actionPopup the action popup
	 */
	@Override
	public void updateActionPopup(ActionPopup actionPopup) {

		actionPopup.removeItems();

		BrowserSubAppDescriptor subAppDescriptor = (BrowserSubAppDescriptor) subAppContext.getSubAppDescriptor();
		ActionbarDefinition actionbarDefinition = subAppDescriptor.getActionbar();
		if (actionbarDefinition == null) {
			return;
		}
		List<ActionbarSectionDefinition> sections = actionbarDefinition.getSections();

		// Figure out which section to show, only one
		ActionbarSectionDefinition sectionDefinition = actionBarService.getVisibleSection(sections);

		// If there no section matched the selection we just hide everything
		if (sectionDefinition == null) {
			return;
		}

		// Evaluate availability of each action within the section
		for (final Iterator<ActionbarGroupDefinition> groupDefinitionIterator = sectionDefinition.getGroups()
				.iterator(); groupDefinitionIterator.hasNext();) {
			final ActionbarGroupDefinition groupDefinition = groupDefinitionIterator.next();
			for (ActionbarItemDefinition itemDefinition : groupDefinition.getItems()) {

				final String actionName = itemDefinition.getName();
				final MenuItem menuItem = addActionPopupItem(subAppDescriptor, itemDefinition);
				ActionDefinition actionDefinition = actionExecutor.getActionDefinition(actionName);
				if (actionDefinition != null) {
					AvailabilityDefinition availability = actionDefinition.getAvailability();
					menuItem.setEnabled(checker.isAvailable(availability, browser.getSelectedItemIds()));
				}
			}

			// Add group separator.
			if (groupDefinitionIterator.hasNext()) {
				browser.getView().getActionPopup().addSeparator();
			}
		}
	}

	/**
	 * Adds the action popup item.
	 *
	 * @param subAppDescriptor the sub app descriptor
	 * @param itemDefinition   the item definition
	 * @return the menu item
	 */
	private MenuItem addActionPopupItem(BrowserSubAppDescriptor subAppDescriptor,
			ActionbarItemDefinition itemDefinition) {
		String actionName = itemDefinition.getName();

		ActionDefinition action = subAppDescriptor.getActions().get(actionName);
		String label = action.getLabel();
		String iconFontCode = ActionPopup.ICON_FONT_CODE + action.getIcon();
		ExternalResource iconFontResource = new ExternalResource(iconFontCode);

		return browser.getView().getActionPopup().addItem(label, iconFontResource,
				item -> browser.onActionbarItemClicked(actionName));
	}

}
