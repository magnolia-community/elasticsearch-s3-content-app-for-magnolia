package info.magnolia.forge.universalcontent.elasticsearch.search.entity;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import info.magnolia.forge.universalcontent.elasticsearch.beans.SingleQuery;
import info.magnolia.forge.universalcontent.elasticsearch.beans.TermsQuery;
import lombok.Data;

@Data
public class KeywordSearchItem extends SearchItem {

	private String field;
	private List<String> values;

	public KeywordSearchItem(final String field, final List<String> collection) {
		super();
		this.field = field;
		this.values = collection;
	}

	@Override
	public SingleQuery createSearchParameter() {
		SingleQuery query = null;
		if (StringUtils.isNotEmpty(this.getField()) && CollectionUtils.isNotEmpty(this.getValues())) {
			query = new SingleQuery();
			TermsQuery termsQuery = new TermsQuery();
			termsQuery.put(this.getField(), this.getValues());
			query.setTerms(termsQuery);
		}
		return query;
	}

}
