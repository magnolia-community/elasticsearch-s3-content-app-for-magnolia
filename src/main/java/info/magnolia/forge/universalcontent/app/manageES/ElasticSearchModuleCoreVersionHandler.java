/*
 *
 */
package info.magnolia.forge.universalcontent.app.manageES;

import java.util.ArrayList;
import java.util.List;

import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.Task;

/**
 * Version handler to execute task at first time or all times starting up.
 */
public class ElasticSearchModuleCoreVersionHandler extends DefaultModuleVersionHandler {

	/**
	 * Instantiates a new whitelabel module core version handler.
	 */
	public ElasticSearchModuleCoreVersionHandler() {
	}

	@Override
	protected List<Task> getBasicInstallTasks(InstallContext installContext) {
		final List<Task> elasticsearchConfiguration = new ArrayList<Task>();
//		elasticsearchConfiguration.add(new WarnBootstrapSingleModuleResource("eoc-elasticsearch",
//				"elasticsearch configuration", "config.modules.eoc-elasticsearch.config.xml"));
//		elasticsearchConfiguration.add(new WarnBootstrapSingleModuleResource("eoc-elasticsearch-filter",
//				"elasticsearch filter", "config.server.filters.indexing.xml"));
//		elasticsearchConfiguration.add(new SetContextAttributeTask("elasticsearch", IndexingFunctions.class));
		return elasticsearchConfiguration;
	}
}
