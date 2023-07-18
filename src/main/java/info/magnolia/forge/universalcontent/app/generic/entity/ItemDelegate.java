package info.magnolia.forge.universalcontent.app.generic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Instantiates a new item delegate.
 *
 * @param key       the key
 * @param obj       the obj
 * @param id        the id
 * @param typeClass the type class
 */
@AllArgsConstructor
@Data
public class ItemDelegate<T extends GenericItem> {

    /** The key. */
    String key;

    /** Represent the row in the table of app, the document you want to save */
    T obj;

    /** The id. */
    String id;

    /** The class of the item */
    Class typeClass;
}
