package info.magnolia.forge.universalcontent.elasticsearch.search.entity;

import java.io.Serializable;

import info.magnolia.forge.universalcontent.elasticsearch.beans.SingleQuery;

public abstract class SearchItem implements Serializable, SearchParameterConvert {

	@Override
	public SingleQuery createFieldsValueSearch(String field, String value, Float boost) {
		SingleQuery singleQuery = new SingleQuery();
		return singleQuery;
	}
}
