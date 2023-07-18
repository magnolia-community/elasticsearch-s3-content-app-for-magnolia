package info.magnolia.forge.universalcontent.app.elasticsearch.service;

import java.util.Locale;
import java.util.Map;

import javax.jcr.Node;

public interface CommonIndexingService {
	public <D> Map<String, Object> addCommonFields(D elasticSearchDoc, Node node);

	public <D> Map<String, Object> addAudit(D elasticSearchDoc, Node node);

	StringBuffer getHTML(Node node, Locale locale, String propertyName);
}
