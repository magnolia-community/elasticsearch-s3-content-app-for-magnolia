package info.magnolia.forge.universalcontent.elasticsearch.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;

import lombok.Data;

/**
 * Abstract class which represents a generic elasticsearch document. It contains
 * all the fields common to all the real documents:
 * <ul>
 * <li>Mapping type (type of the document created on elasticearch)
 * <li>Identifier (document identifier on elasticsearch)
 * <li>Title [single]
 * <li>Description [single]
 * <li>Text [single]
 * <li>Creation data [single]
 * <li>Last modification date [single]
 * <li>Path [single]
 * <li>Workspace [single]
 * <ul>
 */
@Data
public abstract class AbstractDocument implements Document, Serializable {

	private String created;

	private String date;

	private String description;

	private List<String> goldkeywords;

	private String groupType;

	private String modified;

	private String name;

	private String path;

	private String weightPath;

	private String weightPage;

	private String sumWeightPageAndPath;

	private String text;

	private String title;

	private String workspace;

	private String available;

	@JsonIgnore
	private String identifier;

	@JsonIgnore
	private Highlight highlight;

	@JsonIgnore
	private String mappingType;

	private Integer visited;

	private Integer downloaded;

	private List<String> tags = new ArrayList<String>();

	@Override
	public String asJson() {
		Gson gson = new Gson();
		String jsonDocument = gson.toJson(this);
		return jsonDocument;
	}

}
