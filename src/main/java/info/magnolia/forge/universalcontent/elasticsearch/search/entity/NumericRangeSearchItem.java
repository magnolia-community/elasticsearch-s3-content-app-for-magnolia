package info.magnolia.forge.universalcontent.elasticsearch.search.entity;

import org.apache.commons.lang3.StringUtils;

import info.magnolia.forge.universalcontent.elasticsearch.beans.NumericRange;
import info.magnolia.forge.universalcontent.elasticsearch.beans.RangeQuery;
import info.magnolia.forge.universalcontent.elasticsearch.beans.SingleQuery;
import lombok.Data;

@Data
public class NumericRangeSearchItem extends SearchItem {

	private String field;
	private Integer start;
	private Integer end;

	public NumericRangeSearchItem(String field, Integer start, Integer end) {
		super();
		this.field = field;
		this.start = start;
		this.end = end;
	}

	@Override
	public SingleQuery createSearchParameter() {
		SingleQuery query = null;
		if (StringUtils.isNotEmpty(this.getField()) && this.getStart() != null && this.getEnd() != null) {
			query = new SingleQuery();
			NumericRange numericRange = new NumericRange();
			numericRange.setGte(this.getStart());
			numericRange.setLte(this.getEnd());
			RangeQuery<NumericRange> numericRangeQuery = new RangeQuery<NumericRange>();
			numericRangeQuery.put(this.getField(), numericRange);
			query.setRange(numericRangeQuery);
		}
		return query;
	}

}
