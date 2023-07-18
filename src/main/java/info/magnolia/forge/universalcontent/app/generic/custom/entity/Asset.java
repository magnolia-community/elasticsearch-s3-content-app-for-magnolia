/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.custom.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import info.magnolia.dam.jcr.DamConstants;
import info.magnolia.forge.universalcontent.app.elasticsearch.service.AssetIndexingServiceImpl;
import info.magnolia.forge.universalcontent.app.elasticsearch.service.IndexingService;
import info.magnolia.forge.universalcontent.app.generic.annotation.GenericEntity;
import info.magnolia.forge.universalcontent.app.generic.entity.GenericItem;
import info.magnolia.forge.universalcontent.elasticsearch.beans.Highlight;
import info.magnolia.forge.universalcontent.elasticsearch.utils.ElasticsearchConstants;
import info.magnolia.forge.universalcontent.elasticsearch.utils.ElasticsearchDocumentField;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * * It's a item must extends {@link GenericItem}
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@GenericEntity(name = "magnolia_asset", fieldId = "id")
public class Asset extends GenericItem {

	@JsonProperty(ElasticsearchDocumentField.ES_EXTENSION_FIELD)
	private String extension;

	@JsonProperty(ElasticsearchDocumentField.ES_CATEGORIES_FIELD)
	private List<String> categories;

	@JsonProperty(ElasticsearchDocumentField.ES_CREATED_FIELD)
	private String created;

	@JsonProperty(ElasticsearchDocumentField.ES_DATE_FIELD)
	private String date;

	@JsonProperty(ElasticsearchDocumentField.ES_DESCRIPTION_FIELD)
	private String description; // for full text

	@JsonProperty(ElasticsearchDocumentField.ES_GOLDKEYWORDS_FIELD)
	private List<String> goldkeywords;

	@JsonProperty(ElasticsearchDocumentField.ES_GROUPTYPE_FIELD)
	private String groupType;

	@JsonProperty(ElasticsearchDocumentField.ES_MODIFIED_FIELD)
	private String modified;

	@JsonProperty(ElasticsearchDocumentField.ES_NAME_FIELD)
	private String name; // for sorting only

	@JsonProperty(ElasticsearchDocumentField.ES_PATH_FIELD)
	private String path;

	@JsonProperty(ElasticsearchDocumentField.ES_WEIGHTPATH_FIELD)
	private String weightPath;

	@JsonProperty(ElasticsearchDocumentField.ES_WEIGHTPAGE_FIELD)
	private String weightPage;

	@JsonProperty(ElasticsearchDocumentField.ES_SUMWEIGHTPAGEANDPATH_FIELD)
	private String sumWeightPageAndPath;

	@JsonProperty(ElasticsearchDocumentField.ES_TEXT_FIELD)
	private String text; // for full text/ for full text

	@JsonProperty(ElasticsearchDocumentField.ES_TITLE_FIELD)
	private String title; // for full text

	@JsonProperty(ElasticsearchDocumentField.ES_WORKSPACE_FIELD)
	private String workspace;

	@JsonProperty(ElasticsearchDocumentField.ES_AVAILABLE_FIELD)
	private String available;

	private String identifier;

	@JsonIgnore
	private Highlight highlight;

	@JsonIgnore
	private String mappingType;

	@JsonProperty(ElasticsearchDocumentField.ES_VISITED_FIELD)
	private Integer visited;

	@JsonProperty(ElasticsearchDocumentField.ES_DOWNLOADED_FIELD)
	private Integer downloaded;

	@JsonProperty(ElasticsearchDocumentField.ES_TAGS_FIELD)
	private List<String> tags = new ArrayList<String>();

	public Asset() {
		this.workspace = DamConstants.WORKSPACE;
	}

	@Override
	public IndexingService getServiceIndexing() {
		IndexingService service = new AssetIndexingServiceImpl<Asset>();
		return service;
	}

}
