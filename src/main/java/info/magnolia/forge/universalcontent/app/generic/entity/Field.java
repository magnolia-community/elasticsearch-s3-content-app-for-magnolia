/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Annotation for definition of field's item
 */
/**
 * Instantiates a new field.
 *
 * @param name            the name
 * @param isSearchable    the is searchable
 * @param isCaseSensitive the is case sensitive
 * @param isAutoIncrement the is auto increment
 * @param isCurrency      the is currency
 * @param indexName       the index name
 * @param isNullable      the is nullable
 * @param isSigned        the is signed
 * @param size            the size
 * @param readOnly        the read only
 * @param classType       the class name
 */
@Data
@AllArgsConstructor
public class Field {

	/** The name. */
	private String name;

	/** The is searchable. */
	private Boolean isSearchable;

	/** The is case sensitive. */
	private Boolean isCaseSensitive;

	/** The is auto increment. */
	private Boolean isAutoIncrement;

	/** The is currency. */
	private Boolean isCurrency;

	/** The index name. */
	private String indexName;

	/** The is nullable. */
	private Boolean isNullable;

	/** The is signed. */
	private Boolean isSigned;

	/** The size. */
	private Integer size;

	/** The read only. */
	private Boolean readOnly;

	/** The class name. */
	private Class classType;

	/**
	 * Instance from.
	 *
	 * @param f         the f
	 * @param indexName the index name
	 * @return the field
	 */
	public static Field instanceFrom(java.lang.reflect.Field f, String indexName) {
		return new Field(f.getName(), true, true, false, false, indexName, true, true, 250, false, f.getType());
	}

}
