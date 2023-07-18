package info.magnolia.forge.universalcontent.elasticsearch.search.entity;

import info.magnolia.forge.universalcontent.elasticsearch.beans.SingleQuery;

public interface SearchParameterConvert {
	public SingleQuery createSearchParameter();

	SingleQuery createFieldsValueSearch(String field, String value, Float boost);
}
