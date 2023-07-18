package info.magnolia.forge.universalcontent.elasticsearch.search.entity;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import info.magnolia.forge.universalcontent.elasticsearch.beans.SingleQuery;
import info.magnolia.forge.universalcontent.elasticsearch.beans.TermsQuery;
import lombok.Data;

@Data
public class BooleanSearchItem extends SearchItem {

	private String field;
	private Boolean value;

	public BooleanSearchItem(final String field, final Boolean value) {
		super();
		this.field = field;
		this.setValue(value);
	}

	@Override
	public SingleQuery createSearchParameter() {
		SingleQuery query = null;
		if (StringUtils.isNotEmpty(this.getField()) && this.getValue() != null) {
			query = new SingleQuery();
			TermsQuery termsQuery = new TermsQuery();
			List<String> terms = new LinkedList<String>();
			terms.add(this.getValue().toString());
			termsQuery.put(this.getField(), terms);
			query.setTerms(termsQuery);
		}
		return query;
	}

}
