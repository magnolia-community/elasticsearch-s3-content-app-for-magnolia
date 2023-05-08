package info.magnolia.forge.universalcontent.app.generic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ItemDelegate<T extends GenericItem> {
	String key;
	T obj;
	String id;
	Class typeClass;
}
