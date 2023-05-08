/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.connector;

import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;

import info.magnolia.forge.universalcontent.app.custom.interfaces.CustomContainer;
import info.magnolia.forge.universalcontent.app.generic.custom.entity.CustomIndexElasticSearch;
import info.magnolia.forge.universalcontent.app.generic.entity.GenericItem;
import info.magnolia.forge.universalcontent.app.generic.others.GenericException;

/**
 * Factory contain all ElasticSearch connection (one connection represent
 * connection with one instance ES and one index) All connections will be saved
 * in a Map for be retrieved more quickly (solution time saver if you open use
 * more index).
 */
public class FactoryContainer {

	/** The list. */
	GenericContentConnector<? extends GenericItem> list;

	/** The containers. */
	Map<Class, CustomContainer> containers;

	/** The class type. */
	Class<? extends GenericItem> classType;

	/**
	 * Instantiates a new factory elastic search.
	 */
	public FactoryContainer() {
		super();
		containers = new HashedMap<Class, CustomContainer>();
	}

	/**
	 * Creates the.
	 *
	 * @param classType the class type
	 * @param container TODO
	 * @return the elastic search container
	 * @throws GenericException
	 */
	public CustomContainer create(Class classType, CustomContainer container) throws GenericException {
		this.classType = classType;
		containers.put(classType, container);
		return container;
	}

	/**
	 * Gets the class type container.
	 *
	 * @param classType the class type
	 * @return the class type container
	 */
	public CustomContainer getClassTypeContainer(Class classType) {
		return containers.get(classType);
	}

	/**
	 * Gets the actual container.
	 *
	 * @return the actual container
	 */
	public CustomContainer getActualContainer() {
		return containers.get(this.classType);
	}

	/**
	 * Gets the class type.
	 *
	 * @return the class type
	 */
	public Class<? extends GenericItem> getClassType() {
		return classType;
	}

	/**
	 * Sets the class type.
	 *
	 * @param classType the new class type
	 */
	public void setClassType(Class<? extends GenericItem> classType) {
		this.classType = classType;
	}

	public static Class<? extends GenericItem> getDefaultIndex() {
		return CustomIndexElasticSearch.class;
	}

}
