/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.ui.dialog;

import java.util.List;
import java.util.Set;

import info.magnolia.ui.actionbar.ActionbarPresenter;
import info.magnolia.ui.actionbar.definition.ActionbarSectionDefinition;

/**
 * Manage for actions if they are defined in yaml app file.
 *
 * @implSpec ActionBarServiceImpl
 */
@SuppressWarnings("deprecation")
public interface ActionBarService {

	/**
	 * Execute default action.
	 */
	public void executeDefaultAction();

	/**
	 * Execute delete action.
	 */
	public void executeDeleteAction();

	/**
	 * Execute action.
	 *
	 * @param actionName the action name
	 */
	public void executeAction(String actionName);

	/**
	 * Execute action.
	 *
	 * @param actionName the action name
	 * @param itemIds    the item ids
	 * @param parameters the parameters
	 */
	public void executeAction(String actionName, Set<Object> itemIds, Object... parameters);

	/**
	 * Prepare action args.
	 *
	 * @param actionName the action name
	 * @return the object[]
	 */
	public Object[] prepareActionArgs(String actionName);

	/**
	 * Refresh actionbar preview image.
	 *
	 * @param itemId the item id
	 */
	public void refreshActionbarPreviewImage(Object itemId);

	/**
	 * Gets the preview image for id.
	 *
	 * @param itemId the item id
	 * @return the preview image for id
	 */
	public Object getPreviewImageForId(Object itemId);

	/**
	 * Update actionbar.
	 *
	 * @param actionbar the actionbar
	 */
	public void updateActionbar(ActionbarPresenter actionbar);

	/**
	 * Gets the visible section.
	 *
	 * @param sections the sections
	 * @return the visible section
	 */
	public ActionbarSectionDefinition getVisibleSection(List<ActionbarSectionDefinition> sections);

}
