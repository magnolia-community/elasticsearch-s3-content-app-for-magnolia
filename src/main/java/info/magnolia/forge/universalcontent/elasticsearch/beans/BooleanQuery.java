package info.magnolia.forge.universalcontent.elasticsearch.beans;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * The default query for combining multiple leaf or compound query clauses, as
 * must, should, must_not, or filter clauses.<br>
 * The must and should clauses have their scores combined — the more matching
 * clauses, the better — while the must_not and filter clauses are executed in
 * filter context.
 *
 */
@Data
@JsonInclude(Include.NON_NULL)
public class BooleanQuery implements Serializable {

	/**
	 * The clause (query) must appear in matching documents and will contribute to
	 * the score.
	 */
	@Expose
	private List<SingleQuery> must;
	@Expose
	private List<SingleQuery> should;

	/**
	 * The clause (query) must not appear in the matching documents. Clauses are
	 * executed in filter context meaning that scoring is ignored and clauses are
	 * considered for caching.<br>
	 * Because scoring is ignored, a score of 0 for all documents is returned.
	 */
	@Expose
	private List<SingleQuery> must_not;

	/**
	 * The clause (query) must appear in matching documents.<br>
	 * However unlike must the score of the query will be ignored.<br>
	 * Filter clauses are executed in filter context, meaning that scoring is
	 * ignored and clauses are considered for caching.
	 */
	@Expose
	private List<SingleQuery> filter;

	@Override
	public String toString() {
		return "BooleanQuery [must=" + must + ", should=" + should + ", must_not=" + must_not + ", filter=" + filter
				+ "]";
	}

}
