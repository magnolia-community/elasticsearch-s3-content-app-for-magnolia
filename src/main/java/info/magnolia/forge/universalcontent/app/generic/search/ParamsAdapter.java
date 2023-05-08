/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.search;

import java.io.File;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;

import info.magnolia.forge.universalcontent.app.generic.service.RepositoryService;
import info.magnolia.forge.universalcontent.app.generic.ui.CustomFieldFilter;
import info.magnolia.forge.universalcontent.app.generic.ui.OptionsCustomUi;
import info.magnolia.forge.universalcontent.app.generic.utils.GenericConstants;
import lombok.Data;

/**
 * Adapter from Component that convert in query params's generic
 *
 * @return the java.lang. string
 */
@Data
public class ParamsAdapter {

	/** The params. */
	Params params;

	/** The builder params. */
	GenericParamsBuilder builderParams;

	RepositoryService repositoryService;

	/**
	 * Instantiates a new params search adapter.
	 *
	 * @param params the params
	 */
	public ParamsAdapter(Params params, RepositoryService repositoryService) {
		builderParams = GenericParamsBuilder.createSearch(repositoryService).params(params);
		this.params = params;
	}

	/**
	 * Sets the size full text from filter params.
	 *
	 * @return the params ES
	 */
	private Params setSizeFullTextFromFilterParams() {
		if (builderParams.get().getFields() != null) {
			try {
				builderParams.size(Integer
						.parseInt((String) builderParams.getField(GenericConstants.SEARCH_PARAMS_NUMBER_PAGE_LABEL)));
			} catch (NumberFormatException e) {
				builderParams.size(GenericConstants.SEARCH_PARAMS_DEFAULT_SIZE_PAGE);
			}

			try {
				builderParams.fullTextSearch(
						(String) builderParams.getField(GenericConstants.SEARCH_PARAMS_FULLTEXT_SEARCH));
			} catch (Exception e) {
				builderParams.fullTextSearch("");
			}

		}
		return params;
	}

	/**
	 * Fill params search.
	 *
	 * @param component the component
	 * @return the params
	 */
	public Params fillParamsSearch(Component component) {
		if (component instanceof com.vaadin.ui.ComboBox) {
			ComboBox selectSourceCombobox = (com.vaadin.ui.ComboBox) component;
			if (selectSourceCombobox.getValue() != null) {
				CustomFieldFilter searchText = (CustomFieldFilter) selectSourceCombobox.getValue();

				OptionsCustomUi selectSourceValue = (OptionsCustomUi) selectSourceCombobox.getData();
				if (searchText != null && selectSourceValue != null && selectSourceValue.getFilters() != null
						&& selectSourceValue.getFilters().size() > 0) {
					builderParams.addField(searchText.getField(), searchText.getValue());
				}
			}
		}
		if (component instanceof com.vaadin.ui.TextField) {
			TextField selectSourceCombobox = (com.vaadin.ui.TextField) component;
			String searchText = selectSourceCombobox.getValue();
			OptionsCustomUi selectSourceValue = (OptionsCustomUi) selectSourceCombobox.getData();
			if (searchText != null && selectSourceValue != null && selectSourceValue.getFilters() != null
					&& selectSourceValue.getFilters().size() > 0) {
				builderParams.addField(selectSourceValue.getFilters().get(0).getField(), searchText);
			}
		}
		if (component instanceof Upload) {
			Upload upload = (Upload) component;
			Object file = upload.getData();
			if (((info.magnolia.forge.universalcontent.app.generic.ui.FileUploader) upload.getReceiver()).getFile() != null
					&& ((info.magnolia.forge.universalcontent.app.generic.ui.FileUploader) upload.getReceiver()) != null
					&& upload.getId() != null) {
				File fileContent = ((info.magnolia.forge.universalcontent.app.generic.ui.FileUploader) upload.getReceiver()).getFile();
				if (upload.getId() != null && fileContent != null) {
					builderParams.addField(upload.getId(), fileContent);
				}
			}
		}
		params = setSizeFullTextFromFilterParams();
		return params;
	}
}
