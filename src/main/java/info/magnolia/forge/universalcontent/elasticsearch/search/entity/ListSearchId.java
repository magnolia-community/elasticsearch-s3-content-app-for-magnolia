package info.magnolia.forge.universalcontent.elasticsearch.search.entity;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import info.magnolia.forge.universalcontent.elasticsearch.beans.IdsQuery;
import info.magnolia.forge.universalcontent.elasticsearch.beans.SingleQuery;
import lombok.Data;

@Data
public class ListSearchId extends SearchItem {

	private String repository;
	private List<String> values;

	public ListSearchId(final String repository, final List<String> values) {
		super();
		this.repository = repository;
		List<String> ids = new LinkedList<String>();
		for (String value : values) {
			ids.add(repository + "_" + value);
		}
		this.values = ids;
	}

	@Override
	public SingleQuery createSearchParameter() {
		SingleQuery query = null;
		if (CollectionUtils.isNotEmpty(this.getValues())) {
			query = new SingleQuery();
			IdsQuery idsQuery = new IdsQuery();
			idsQuery.put("values", this.getValues());
			query.setIds(idsQuery);
		}
		return query;
	}

}
