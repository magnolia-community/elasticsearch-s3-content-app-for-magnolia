package info.magnolia.forge.universalcontent.app.generic.ui.table;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.converter.Converter.ConversionException;

import info.magnolia.forge.universalcontent.app.generic.service.RepositoryService;

public class ColumnProperty implements Property {
	private static final long serialVersionUID = -3694463129581802457L;

	private RowItem owner;

	private String propertyId;

	private boolean readOnly;
	private boolean allowReadOnlyChange = true;
	private boolean nullable = true;

	private Object value;
	private Object changedValue;
	private Class<?> type;

	private boolean modified;

	private boolean versionColumn;
	private boolean primaryKey = false;
	private RepositoryService services;

	private ColumnProperty() {
	}

	public ColumnProperty(String propertyId, boolean readOnly, boolean allowReadOnlyChange, boolean nullable,
			boolean primaryKey, Object value, Class<?> type, RepositoryService services) {

		if (propertyId == null) {
			throw new IllegalArgumentException("Properties must be named.");
		}
		if (type == null) {
			throw new IllegalArgumentException("Property type must be set.");
		}
		this.propertyId = propertyId;
		this.type = type;
		this.value = value;

		this.allowReadOnlyChange = allowReadOnlyChange;
		this.nullable = nullable;
		this.readOnly = readOnly;
		this.primaryKey = primaryKey;
		this.services = services;
	}

	@Override
	public Object getValue() {
		if (isModified()) {
			return changedValue;
		}
		return value;
	}

	public Object getOldValue() {
		return value;
	}

	@Override
	public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
		if (newValue == null && !nullable) {
			throw new NotNullableException("Null values are not allowed for this property.");
		}
		if (readOnly) {
			throw new ReadOnlyException("Cannot set value for read-only property.");
		}

		/* Set the new value and notify container of the change. */
		changedValue = newValue;
		modified = true;
		services.getCustomContainer().refresh();
	}

	private boolean isValueAlreadySet(Object newValue) {
		Object referenceValue = isModified() ? changedValue : value;

		return (isNullable() && newValue == null && referenceValue == null) || newValue.equals(referenceValue);
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	public boolean isReadOnlyChangeAllowed() {
		return allowReadOnlyChange;
	}

	@Override
	public void setReadOnly(boolean newStatus) {
		if (allowReadOnlyChange) {
			readOnly = newStatus;
		}
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public String getPropertyId() {
		return propertyId;
	}

	public void setOwner(RowItem owner) {
		if (owner == null) {
			throw new IllegalArgumentException("Owner can not be set to null.");
		}
		if (this.owner != null) {
			throw new IllegalStateException("ColumnProperties can only be bound once.");
		}
		this.owner = owner;
	}

	public boolean isModified() {
		return modified;
	}

	public boolean isVersionColumn() {
		return versionColumn;
	}

	public void setVersionColumn(boolean versionColumn) {
		this.versionColumn = versionColumn;
	}

	public boolean isNullable() {
		return nullable;
	}

	public boolean isPersistent() {
		if (isVersionColumn()) {
			return false;
		}
		return isReadOnlyChangeAllowed() && !isReadOnly();
	}

	public boolean isRowIdentifier() {
		return isPrimaryKey() || isVersionColumn();
	}

	public class NotNullableException extends RuntimeException {

		public NotNullableException() {
		}

		public NotNullableException(String msg) {
			super(msg);
		}

		public NotNullableException(Throwable cause) {
			super(cause);
		}
	}

	public void commit() {
		if (isModified()) {
			modified = false;
			value = changedValue;
		}
	}
}
