package info.magnolia.forge.universalcontent.elasticsearch.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Filters documents that have fields that match any of the provided terms (not
 * analyzed)<br>
 * The term query looks for the exact term in the field’s inverted index — it
 * doesn’t know anything about the field’s analyzer.<br>
 * This makes it useful for looking up values in keyword fields, or in numeric
 * or date fields.<br>
 * <b>The key is the field name</b>
 */
public class IdsQuery extends HashMap<String, List<String>> implements Serializable {
}
