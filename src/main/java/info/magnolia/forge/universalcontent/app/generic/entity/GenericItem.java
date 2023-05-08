/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.entity;

import info.magnolia.forge.universalcontent.app.generic.annotation.GenericEntity;

/**
 * Represent one abstract item on generic source
 */
public abstract class GenericItem {

	/**
	 * Gets the index name.
	 *
	 * @return the index name
	 */
	public String getIndexName() {
		GenericEntity index = this.getClass().getAnnotation(GenericEntity.class);
		if (index != null) {
			return index.name();
		}
		return null;
	};
}
