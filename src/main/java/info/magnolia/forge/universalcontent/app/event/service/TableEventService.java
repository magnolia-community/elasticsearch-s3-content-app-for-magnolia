/*
 *
 */
package info.magnolia.forge.universalcontent.app.event.service;

/**
 * Interface for managing table event
 *
 * @see TableEventServiceImpl
 */
public interface TableEventService {

	/**
	 * Adds the value change listener.
	 */
	public void AddValueChangeListener();

	/**
	 * Adds the item click listener.
	 */
	public void addItemClickListener();

	/**
	 * Adds the column resize listener.
	 */
	public void addColumnResizeListener();

	/**
	 * Adds the column reorder listener.
	 */
	public void addColumnReorderListener();

	/**
	 * Adds the action handler.
	 */
	public void addActionHandler();

	/**
	 * Sets the cell style generator.
	 */
	public void setCellStyleGenerator();
}
