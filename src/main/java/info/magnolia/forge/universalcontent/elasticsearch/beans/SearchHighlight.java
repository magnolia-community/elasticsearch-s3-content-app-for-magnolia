package info.magnolia.forge.universalcontent.elasticsearch.beans;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

/**
 *
 * "highlight" : { "pre_tags" : ["<tag1>", "<tag2>"], "post_tags" : ["</tag1>",
 * "</tag2>"], "fields" : { "field1" : {}, "field2" : {} } }
 *
 *
 */
@Data
public class SearchHighlight implements Serializable {

	private List<String> pre_tags;
	private List<String> post_tags;
	private Map<String, HighlightFragment> fields;

	public void setTag(String tag) {
		if (StringUtils.isNotEmpty(tag)) {
			pre_tags = new LinkedList<String>();
			pre_tags.add("<" + tag + ">");
			post_tags = new LinkedList<>();
			post_tags.add("</" + tag + ">");
		}
	}

	@Override
	public String toString() {
		return "SearchHighlight [pre_tags=" + pre_tags + ", post_tags=" + post_tags + ", fields=" + fields + "]";
	}

}
