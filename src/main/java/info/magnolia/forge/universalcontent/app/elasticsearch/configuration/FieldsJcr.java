package info.magnolia.forge.universalcontent.app.elasticsearch.configuration;

import java.util.List;

public class FieldsJcr {
	private List<String> fieldsText;
	private List<String> fieldsNode;
	private List<String> fieldsJcrNodeId;

	public List<String> getFieldsText() {
		return fieldsText;
	}

	public void setFieldsText(List<String> fieldsText) {
		this.fieldsText = fieldsText;
	}

	public List<String> getFieldsNode() {
		return fieldsNode;
	}

	public void setFieldsNode(List<String> fieldsNode) {
		this.fieldsNode = fieldsNode;
	}

	public List<String> getFieldsJcrNodeId() {
		return fieldsJcrNodeId;
	}

	public void setFieldsJcrNodeId(List<String> fieldsJcrNodeId) {
		this.fieldsJcrNodeId = fieldsJcrNodeId;
	}

}
