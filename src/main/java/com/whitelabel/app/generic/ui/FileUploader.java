package com.whitelabel.app.generic.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

import info.magnolia.init.MagnoliaConfigurationProperties;
import info.magnolia.objectfactory.Components;
import lombok.Getter;

public class FileUploader implements Receiver, SucceededListener {
	@Getter
	File file;

	Embedded image;

	MagnoliaConfigurationProperties magnoliaConfigurationProperties;

	String path;

	public FileUploader() {
		magnoliaConfigurationProperties = Components.getComponent(MagnoliaConfigurationProperties.class);
		path = magnoliaConfigurationProperties.getProperty("magnolia.upload-file");
		image = new Embedded("Uploaded Image");
		image.setVisible(false);
	}

	@Override
	public OutputStream receiveUpload(String fileName, String mimeType) {
		// Create upload stream
		FileOutputStream fos = null; // Stream to write to
		try {
			// Open the file for writing.
			file = new File(path + fileName);
			fos = new FileOutputStream(file);
		} catch (final java.io.FileNotFoundException e) {
			new Notification("Could not open file<br/>", e.getMessage(), Notification.Type.ERROR_MESSAGE)
					.show(Page.getCurrent());
			return null;
		}
		return fos; // Return the output stream to write to
	}

	@Override
	public void uploadSucceeded(SucceededEvent event) {
		// Show the uploaded file in the image viewer
		image.setVisible(true);
		image.setCaption(file.getName());
		image.setSource(new FileResource(file));
	}
};
