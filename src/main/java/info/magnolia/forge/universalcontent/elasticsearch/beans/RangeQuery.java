package info.magnolia.forge.universalcontent.elasticsearch.beans;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Matches documents with fields that have terms within a certain range<br>
 * <b>The key is the field name</b>
 */
public class RangeQuery<T> extends HashMap<String, T> implements Serializable {
}
