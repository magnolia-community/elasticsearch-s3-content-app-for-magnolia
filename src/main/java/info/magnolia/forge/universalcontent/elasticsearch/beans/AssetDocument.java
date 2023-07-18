package info.magnolia.forge.universalcontent.elasticsearch.beans;

import java.util.List;

import info.magnolia.forge.universalcontent.elasticsearch.utils.ElasticsearchMappingTypes;
import lombok.Data;

/**
 * Represents an elasticsearch document related to a magnolia page
 *
 * It contains the following fields:
 * <ul>
 * <li>Date [single]
 * <li>Extension [single] *
 * <li>categories: multiple magnolia categories (as uuid) [multiple]
 * </ul>
 *
 * @see AbstractDocument
 *
 */
@Data
public class AssetDocument extends AbstractDocument {

	private String extension;

	private List<String> categories;

	public AssetDocument() {
		super();
		setMappingType(ElasticsearchMappingTypes.ES_ASSET_TYPE);
	}
}
