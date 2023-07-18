package info.magnolia.forge.universalcontent.elasticsearch.search.entity;

import java.io.Serializable;
import java.util.Iterator;

import javax.jcr.Node;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.forge.universalcontent.app.generic.entity.GenericItem;
import info.magnolia.forge.universalcontent.elasticsearch.beans.Highlight;

public class SearchResultItem implements Serializable {

	private GenericItem document;
	private Node node;

	private boolean searchErrorOccured;

	public SearchResultItem(GenericItem document, Node node) {
		this.document = document;
		this.node = node;
	}

	public SearchResultItem(GenericItem document, Node node, boolean searchErrorOccured) {
		this.document = document;
		this.node = node;
		this.searchErrorOccured = searchErrorOccured;
	}

	public GenericItem getDocument() {
		return document;
	}

	public void setDocument(GenericItem document) {
		this.document = document;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public boolean isSearchErrorOccured() {
		return searchErrorOccured;
	}

	public void setSearchErrorOccured(boolean searchErrorOccured) {
		this.searchErrorOccured = searchErrorOccured;
	}

	public String getSnippets() {
		StringBuffer snippets = new StringBuffer();
		if (document != null && document.getHighlight() != null) {
			Highlight highlight = document.getHighlight();
			highlight.getTitle();
			highlight.getDescription();
			highlight.getText();

			if (CollectionUtils.isNotEmpty(highlight.getTitle())) {
				Iterator<String> iterator = highlight.getTitle().iterator();
				while (iterator.hasNext()) {
					snippets.append(iterator.next());
					if (iterator.hasNext()) {
						snippets.append(" ");
					}
				}
			}

			if (CollectionUtils.isNotEmpty(highlight.getDescription())) {
				Iterator<String> iterator = highlight.getDescription().iterator();
				while (iterator.hasNext()) {
					snippets.append(iterator.next());
					if (iterator.hasNext()) {
						snippets.append(" ");
					}
				}
			}

			if (CollectionUtils.isNotEmpty(highlight.getText())) {
				Iterator<String> iterator = highlight.getText().iterator();
				while (iterator.hasNext()) {
					snippets.append(iterator.next());
					if (iterator.hasNext()) {
						snippets.append(" ");
					}
				}
			}
		}
		return snippets.toString();
	}
}
