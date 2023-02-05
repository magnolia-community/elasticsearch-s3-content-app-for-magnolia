/*
 *
 */
package com.whitelabel.app.generic.ui;

import org.apache.commons.lang.StringUtils;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

/**
 * Factory manage UI Build, entry point for creating in left column side
 * textbox,textarea,selectbox.
 */
public class FactoryCustomUi {

	/**
	 * Creates the.
	 *
	 * @param <T>     the generic type
	 * @param layout  the layout
	 * @param classUi the class ui
	 * @return the custom ui
	 */
	public static <T> CustomUi<T> create(VerticalLayout layout, Class<T> classUi) {
		CustomUi<T> customUi = new CustomUi<T>(layout);
		return customUi;
	}

	/**
	 * Create files
	 *
	 */
	public static Upload createFile(String name, String label, String placeholder) {
		final Embedded image = new Embedded(name);
		image.setVisible(false);

		// Implement both receiver that saves upload in a file and
		// listener for successful upload

		FileUploader receiver = new FileUploader();

		// Create the upload with a caption and set receiver later
		Upload upload = new Upload(placeholder, receiver);
		upload.setButtonCaption(label);
		upload.addSucceededListener(receiver);
		upload.setId(name);
		return upload;
	}

	/**
	 * Creates the textbox.
	 *
	 * @param label       the label
	 * @param placeholder the placeholder
	 * @param name        the name
	 * @return the text field
	 */
	public static TextField createTextbox(String label, String placeholder, String name) {
		TextField textField = new TextField();
		if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(label)) {
			textField.setCaption(label);
			if (StringUtils.isNotEmpty(placeholder)) {
				textField.setPlaceholder(placeholder);
			}
			textField.setWidth("100%");
			textField.setId(name);
			textField.setData(textField);
		}
		return textField;
	}

	/**
	 * Creates the textarea.
	 *
	 * @param label       the label
	 * @param placeholder the placeholder
	 * @param name        the name
	 * @param rows        the rows
	 * @return the text area
	 */
	public static TextArea createTextarea(String label, String placeholder, String name, Integer rows) {
		TextArea textArea = new TextArea();
		if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(label)) {
			textArea.setCaption(label);
			textArea.setPlaceholder(placeholder);
			textArea.setWidth("100%");
			textArea.setRows(rows != null ? rows : 5);
			textArea.setId(name);
		}
		return textArea;
	}

	/**
	 * Creates the combobox.
	 *
	 * @param <T>             the generic type
	 * @param type            the type
	 * @param id              the id
	 * @param placeholderText the placeholder text
	 * @return the combo box
	 */
	public static <T> ComboBox<T> createCombobox(Class<T> type, String id, String placeholderText) {
		ComboBox<T> selectbox = new ComboBox<>(id);
		selectbox.setData(selectbox);
		selectbox.setStyleName("search-filter");
		selectbox.setWidth(100f, Unit.PERCENTAGE);
		if (StringUtils.isNotEmpty(placeholderText)) {
			selectbox.setPlaceholder(placeholderText);
		}
		selectbox.setId(id);
		selectbox.setPopupWidth(null);
		return selectbox;
	}

	/**
	 * Creates the button.
	 *
	 * @param caption the caption
	 * @param id      the id
	 * @param visible the visible
	 * @return the button
	 */
	public static Button createButton(String caption, String id, Boolean visible) {
		Button button = new Button(caption);
		button.setId(id);
		button.setEnabled(visible);
		return button;
	}

	/**
	 * Creates the labe field.
	 *
	 * @param message the message
	 * @return the label
	 */
	public static Label createLabeField(String message) {
		if (StringUtils.isNotEmpty(message)) {
			Label label = new Label();
			label.setCaptionAsHtml(true);
			label.setCaption(message);
			return label;
		}
		return null;
	}

}
