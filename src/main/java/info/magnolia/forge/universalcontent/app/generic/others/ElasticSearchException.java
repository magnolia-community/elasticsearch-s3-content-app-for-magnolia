package info.magnolia.forge.universalcontent.app.generic.others;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Custom exception thrown in Elastic Search.
 */
@Data
@AllArgsConstructor
public class ElasticSearchException extends Exception {

	/** The message. */
	private String message;

}
