/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.custom.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import info.magnolia.forge.universalcontent.app.elasticsearch.service.IndexingService;
import info.magnolia.forge.universalcontent.app.elasticsearch.service.PageIndexingServiceImpl;
import info.magnolia.forge.universalcontent.app.generic.annotation.GenericEntity;
import info.magnolia.forge.universalcontent.app.generic.entity.GenericItem;
import info.magnolia.forge.universalcontent.elasticsearch.utils.ElasticsearchConstants;
import info.magnolia.forge.universalcontent.elasticsearch.utils.ElasticsearchDocumentField;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * It's a item must extends {@link GenericItem}.
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@GenericEntity(name = "magnolia_page", fieldId = "id", workspace = "website")
public class Page extends GenericItem {

	@JsonProperty(ElasticsearchDocumentField.ES_DESCRIPTION_FIELD)
	private String description; // for full text

	@JsonProperty(ElasticsearchDocumentField.ES_GROUPTYPE_FIELD)
	private String groupType;

	@JsonProperty(ElasticsearchDocumentField.ES_PATH_FIELD)
	private String path;

	@JsonProperty(ElasticsearchDocumentField.ES_TEMPLATE_FIELD)
	private String template;

	@JsonProperty(ElasticsearchDocumentField.ES_PAGE_FIELD)
	private String page;

	public Page() {
	}

	@Override
	public IndexingService getServiceIndexing() {
		IndexingService service = new PageIndexingServiceImpl();
		return service;
	}
}
