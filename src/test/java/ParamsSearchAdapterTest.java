
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import com.whitelabel.app.generic.search.GenericParamsBuilder;
import com.whitelabel.app.generic.search.Params;
import com.whitelabel.app.generic.search.ParamsAdapter;
import com.whitelabel.app.generic.ui.CustomFieldFilter;
import com.whitelabel.app.generic.ui.OptionsCustomUi;
import com.whitelabel.app.generic.utils.GenericConstants;

public class ParamsSearchAdapterTest {
	Params params;
	GenericParamsBuilder builderParamsES;
	private ParamsAdapter adapter;

	@Test
	void paramsSearchAdapter() {
		int offset = 10;
		params = GenericParamsBuilder.createSearch(null).offset(10).get();
		adapter = new ParamsAdapter(params, null);

		assertEquals(adapter.getBuilderParams().get().getOffset(), offset);
	}

	@Test
	void setSizeFullTextFromFilterParams() {
		String numberPage = "10";
		String fullText = "fullText";
//		adapter.setSizeFullTextFromFilterParams()
		params = GenericParamsBuilder.createSearch(null)
				.addField(GenericConstants.SEARCH_PARAMS_NUMBER_PAGE_LABEL, numberPage)
				.addField(GenericConstants.SEARCH_PARAMS_FULLTEXT_SEARCH, fullText).get();
		assertTrue(Integer.valueOf(params.getSize()).toString().equals(numberPage));
		assertTrue(Integer.valueOf(params.getRelevanceSearch().getFullTextSearch()).toString().equals(fullText));

	}

	@Test
	void fillParamsSearch() {

	}

	public Params fillParamsSearch(Component component) {
		if (component instanceof com.vaadin.ui.ComboBox) {
			ComboBox selectSourceCombobox = (com.vaadin.ui.ComboBox) component;
			if (selectSourceCombobox.getValue() != null) {
				CustomFieldFilter searchText = (CustomFieldFilter) selectSourceCombobox.getValue();

				OptionsCustomUi selectSourceValue = (OptionsCustomUi) selectSourceCombobox.getData();
				if (searchText != null && selectSourceValue != null && selectSourceValue.getFilters() != null
						&& selectSourceValue.getFilters().size() > 0) {
					builderParamsES.addField(searchText.getField(), searchText.getValue());
				}
			}
		}
		if (component instanceof com.vaadin.ui.TextField) {
			TextField selectSourceCombobox = (com.vaadin.ui.TextField) component;
			String searchText = selectSourceCombobox.getValue();
			OptionsCustomUi selectSourceValue = (OptionsCustomUi) selectSourceCombobox.getData();
			if (searchText != null && selectSourceValue != null && selectSourceValue.getFilters() != null
					&& selectSourceValue.getFilters().size() > 0) {
				builderParamsES.addField(selectSourceValue.getFilters().get(0).getField(), searchText);
			}
		}
		return params;
	}
}
