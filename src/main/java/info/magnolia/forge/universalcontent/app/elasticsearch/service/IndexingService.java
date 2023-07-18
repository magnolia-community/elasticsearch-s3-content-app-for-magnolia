package info.magnolia.forge.universalcontent.app.elasticsearch.service;

import java.util.Locale;

import javax.jcr.Node;

import info.magnolia.forge.universalcontent.app.elasticsearch.configuration.ElasticsearchConfiguration;

public interface IndexingService {

	<D> D indexing(Node node, Locale locale, D assetDocument);

	public IndexingService configuration(ElasticsearchConfiguration elasticsearchConfiguration);
}
