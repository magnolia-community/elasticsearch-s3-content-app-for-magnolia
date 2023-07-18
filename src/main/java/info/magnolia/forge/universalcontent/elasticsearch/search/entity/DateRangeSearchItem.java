package info.magnolia.forge.universalcontent.elasticsearch.search.entity;

import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;

import info.magnolia.forge.universalcontent.app.generic.utils.DateConverter;
import info.magnolia.forge.universalcontent.elasticsearch.beans.DateRange;
import info.magnolia.forge.universalcontent.elasticsearch.beans.RangeQuery;
import info.magnolia.forge.universalcontent.elasticsearch.beans.SingleQuery;
import lombok.Data;

@Data
public class DateRangeSearchItem extends SearchItem {

	private String field;
	private Calendar startDate;
	private Calendar endDate;

	public DateRangeSearchItem(String field, Calendar startDate, Calendar endDate) {
		super();
		this.field = field;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	@Override
	public SingleQuery createSearchParameter() {
		SingleQuery query = null;
		if (StringUtils.isNotEmpty(this.getField()) && this.getStartDate() != null && this.getEndDate() != null) {
			query = new SingleQuery();
			DateRange dateRange = new DateRange();
			dateRange.setGte(DateConverter.formatDate(this.getStartDate().getTime()));
			dateRange.setLte(DateConverter.formatDate(this.getEndDate().getTime()));
			RangeQuery<DateRange> dateRangeQuery = new RangeQuery<DateRange>();
			dateRangeQuery.put(this.getField(), dateRange);
			query.setRange(dateRangeQuery);
		}
		return query;
	}

}
