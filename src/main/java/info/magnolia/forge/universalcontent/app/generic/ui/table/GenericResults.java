package info.magnolia.forge.universalcontent.app.generic.ui.table;

import java.util.List;

import info.magnolia.forge.universalcontent.app.generic.entity.GenericItem;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GenericResults {
	List<? extends GenericItem> search;
	Long total;
}
