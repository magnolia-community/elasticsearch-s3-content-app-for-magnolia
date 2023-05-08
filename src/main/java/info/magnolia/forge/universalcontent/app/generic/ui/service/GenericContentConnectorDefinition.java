/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.ui.service;

import info.magnolia.forge.universalcontent.app.generic.connector.GenericContentConnector;
import info.magnolia.ui.vaadin.integration.contentconnector.AbstractContentConnector;
import info.magnolia.ui.vaadin.integration.contentconnector.ConfiguredContentConnectorDefinition;

/**
 * Definition Source Content Connector implements
 * {@link AbstractContentConnector}.
 */
public class GenericContentConnectorDefinition extends ConfiguredContentConnectorDefinition {

	/**
	 * Instantiates a new elastic search content connector definition.
	 */
	public GenericContentConnectorDefinition() {
		setImplementationClass(GenericContentConnector.class);
	}
}
