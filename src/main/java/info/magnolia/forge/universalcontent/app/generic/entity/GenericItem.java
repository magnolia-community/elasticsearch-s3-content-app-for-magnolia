/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import info.magnolia.forge.universalcontent.app.elasticsearch.service.GetIndexService;
import info.magnolia.forge.universalcontent.app.generic.annotation.GenericEntity;
import info.magnolia.forge.universalcontent.elasticsearch.beans.Highlight;
import info.magnolia.forge.universalcontent.elasticsearch.utils.ElasticsearchConstants;
import info.magnolia.forge.universalcontent.elasticsearch.utils.ElasticsearchDocumentField;
import lombok.Data;

/**
 * Represent one abstract item on generic source
 */
@Data
public abstract class GenericItem implements GetIndexService<GenericItem> {

	private String id;
	@JsonProperty(ElasticsearchDocumentField.ES_IDENTIFIER_FIELD)
	private String identifier;

	@JsonProperty(ElasticsearchDocumentField.ES_CREATED_FIELD)
	private String created;

	@JsonProperty(ElasticsearchDocumentField.ES_DATE_FIELD)
	private String date;

	@JsonProperty(ElasticsearchDocumentField.ES_MODIFIED_FIELD)
	private String modified;

	@JsonIgnore
	private Highlight highlight;

	@JsonIgnore
	private String mappingType;

	@JsonProperty(ElasticsearchDocumentField.ES_GOLDKEYWORDS_FIELD)
	private List<String> goldkeywords;

	@JsonProperty(ElasticsearchDocumentField.ES_TEXT_FIELD)
	private String text;

	@JsonProperty(ElasticsearchDocumentField.ES_TITLE_FIELD)
	private String title;

	@JsonProperty(ElasticsearchDocumentField.ES_NAME_FIELD)
	private String name;

	/**
	 * Gets the index name.
	 *
	 * @return the index name
	 */
	public String getIndexName() {
		GenericEntity index = this.getClass().getAnnotation(GenericEntity.class);
		if (index != null) {
			return index.name();
		}
		return null;
	};
}
