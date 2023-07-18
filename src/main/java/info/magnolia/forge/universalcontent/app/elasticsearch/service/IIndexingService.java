package info.magnolia.forge.universalcontent.app.elasticsearch.service;

import java.util.List;

import javax.jcr.Node;
import javax.servlet.http.HttpServletRequest;

import info.magnolia.forge.universalcontent.app.elasticsearch.configuration.ElasticsearchConfiguration;
import info.magnolia.forge.universalcontent.app.elasticsearch.connector.ElasticSearchOperation;
import info.magnolia.forge.universalcontent.app.generic.others.ElasticSearchException;

public interface IIndexingService {

	public <D> List<D> retrieveDataIndexing(Node node, ElasticsearchConfiguration configuration,
			ElasticSearchOperation operation, D a, IndexingService indexingService) throws ElasticSearchException;

	public void unindex(HttpServletRequest request) throws Exception;

	public void index(HttpServletRequest request) throws Exception;

}
