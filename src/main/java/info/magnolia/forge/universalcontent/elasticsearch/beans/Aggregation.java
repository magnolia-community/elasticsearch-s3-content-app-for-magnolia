package info.magnolia.forge.universalcontent.elasticsearch.beans;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;

@Data
public class Aggregation implements Serializable {

	private Map<String, Map> aggregation;

	@Override
	public String toString() {
		return "Aggregation [aggregation=" + aggregation + "]";
	}

}
