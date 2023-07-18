package info.magnolia.forge.universalcontent.elasticsearch.utils;

import java.util.Collection;

import javax.jcr.Node;

import info.magnolia.templating.models.util.Pagination;

/**
 * Simple class providing pagination.
 */
public class ElasticsearchPagination extends Pagination {

	public ElasticsearchPagination(String link, Collection items, Node content, int maxResultsPerPage) {
		super(link, items, content);
		setResults(maxResultsPerPage);
	}
}
