package info.magnolia.forge.universalcontent.app.elasticsearch.configuration;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class ElasticsearchConfiguration {
	public ElasticsearchConfiguration() {

//		if (this.getHosts() == null)
//			this.setHosts(properties.getProperty(StringUtils.isNotEmpty(propertiesPrefix)
//					? propertiesPrefix + "." + ElasticsearchConstants.ElasticsearchConfig.ES_HOSTS
//					: ElasticsearchConstants.ElasticsearchConfig.ES_HOSTS));
//
//		if (this.getApiKeyRead() == null)
//			this.setApiKeyRead(properties.getProperty(StringUtils.isNotEmpty(propertiesPrefix)
//					? propertiesPrefix + "." + ElasticsearchConstants.ElasticsearchConfig.ES_APIKEY_READ
//					: ElasticsearchConstants.ElasticsearchConfig.ES_APIKEY_READ));
//
//		if (this.getApiKeyWrite() == null)
//			this.setApiKeyWrite(properties.getProperty(StringUtils.isNotEmpty(propertiesPrefix)
//					? propertiesPrefix + "." + ElasticsearchConstants.ElasticsearchConfig.ES_APIKEY_WRITE
//					: ElasticsearchConstants.ElasticsearchConfig.ES_APIKEY_WRITE));
//
//		if (this.getXopaqueidHeader() == null)
//			this.setXopaqueidHeader(properties.getProperty(StringUtils.isNotEmpty(propertiesPrefix)
//					? propertiesPrefix + "." + ElasticsearchConstants.ElasticsearchConfig.ES_HEADER_XOPAQUEID
//					: ElasticsearchConstants.ElasticsearchConfig.ES_HEADER_XOPAQUEID));
//
//		if (this.getSniffer() == null)
//			this.setSniffer(properties.getProperty(StringUtils.isNotEmpty(propertiesPrefix)
//					? propertiesPrefix + "." + ElasticsearchConstants.ElasticsearchConfig.ES_SNIFFER
//					: ElasticsearchConstants.ElasticsearchConfig.ES_SNIFFER));
//
//		if (this.getWebsitePaths() == null || this.getWebsitePaths().size() == 0) {
//			String websitePaths = properties.getProperty(StringUtils.isNotEmpty(propertiesPrefix)
//					? propertiesPrefix + "." + ElasticsearchConstants.ElasticsearchConfig.ES_GMWEBSITEPATHS
//					: ElasticsearchConstants.ElasticsearchConfig.ES_GMWEBSITEPATHS);
//
//			if (StringUtils.isNotEmpty(websitePaths)) {
//				String[] websitePathsArray = StringUtils.split(websitePaths, ",");
//				if (websitePathsArray != null) {
//					this.setWebsitePaths(Arrays.asList(websitePathsArray));
//				}
//			}
//		}
//
//		if (this.getDamPaths() == null || this.getDamPaths().size() == 0) {
//			String damPaths = properties.getProperty(StringUtils.isNotEmpty(propertiesPrefix)
//					? propertiesPrefix + "." + ElasticsearchConstants.ElasticsearchConfig.ES_GMDAMPATHS
//					: ElasticsearchConstants.ElasticsearchConfig.ES_GMDAMPATHS);
//
//			if (StringUtils.isNotEmpty(damPaths)) {
//				String[] damPathsArray = StringUtils.split(damPaths, ",");
//				if (damPathsArray != null) {
//					this.setDamPaths(Arrays.asList(damPathsArray));
//				}
//			}
//		}
	}

	private String hosts;
	private Boolean https;
	private String sniffer;
	private List<String> websitePaths = new LinkedList<String>();
	private List<String> damPaths = new LinkedList<String>();
	private Long connectTimeout = 5000l;
	private Long socketTimeout = 60000l;
	private Long maxRetryTimeoutMillis = 60000l;
	private List<Map<String, String>> excludedTemplates = new LinkedList<>();
	private List<Map<String, String>> excludedResources = new LinkedList<>();
	private List<Map<String, String>> specialWordsMapping = new LinkedList<>();
	private Map<String, String> boosts = new HashMap<>();
	private String xopaqueidHeader;
	private String apiKeyRead;
	private String apiKeyWrite;
	private String port;
	private Map<String, String> locales = new HashMap<>();
	private Map<String, String> fieldsJcrFulltext;
	private Map<String, String> tagsParameters;
	@JsonIgnore
	private List<TagJcrConfiguration> tagsJcrConfigurations;

	public void setTagsParameters(Map<String, String> tagsParameters) {
		this.tagsParameters = tagsParameters;
		Gson gson = new Gson();
		Comparator<? super TagJcrConfiguration> item = new Comparator<TagJcrConfiguration>() {
			@Override
			public int compare(TagJcrConfiguration o1, TagJcrConfiguration o2) {
				return o1.getPriority().compareTo(o2.getPriority());
			}
		};
		tagsJcrConfigurations = tagsParameters.keySet().stream().filter(parameter -> {
			return parameter != null && StringUtils.isNotEmpty(tagsParameters.get(parameter));
		}).map(parameter -> {
			TagJcrConfiguration tagConfiguration = null;
			try {
				tagConfiguration = gson.fromJson(tagsParameters.get(parameter), TagJcrConfiguration.class);
			} catch (Exception e) {
				log.error("TagsParameters convert: ", e);
			}
			return tagConfiguration;
		}).collect(Collectors.toList());
		tagsJcrConfigurations.sort(item);
	}

}