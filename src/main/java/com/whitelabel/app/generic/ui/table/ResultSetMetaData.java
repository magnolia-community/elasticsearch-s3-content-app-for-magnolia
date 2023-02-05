package com.whitelabel.app.generic.ui.table;

import java.util.ArrayList;
import java.util.Collection;

import com.whitelabel.app.generic.entity.Field;

import co.elastic.clients.elasticsearch._types.ElasticsearchException;

public interface ResultSetMetaData {

	String getColumnClassName(int column) throws ElasticsearchException;

	boolean isDefinitelyWritable(int column) throws ElasticsearchException;

	boolean isWritable(int column) throws ElasticsearchException;

	boolean isReadOnly(int column) throws ElasticsearchException;

	String getTableName(int column) throws ElasticsearchException;

	String getSchemaName(int column) throws ElasticsearchException;

	String getColumnName(int column) throws ElasticsearchException;

	String getColumnLabel(int column) throws ElasticsearchException;

	int getColumnDisplaySize(int column) throws ElasticsearchException;

	boolean isSigned(int column) throws ElasticsearchException;

	int isNullable(int column) throws ElasticsearchException;

	boolean isCurrency(int column) throws ElasticsearchException;

	boolean isSearchable(int column) throws ElasticsearchException;

	int getColumnCount() throws ElasticsearchException;

	boolean isCaseSensitive(int column) throws ElasticsearchException;

	boolean isAutoIncrement(int column) throws ElasticsearchException;

	void setFieldsName(ArrayList<Field> arrayList);

	Collection<Field> getFieldsName();

}
