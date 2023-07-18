package info.magnolia.forge.universalcontent.app.elasticsearch.service;

import info.magnolia.forge.universalcontent.app.generic.entity.GenericItem;

public interface GetIndexService<D extends GenericItem> {
	public IndexingService getServiceIndexing();
}
