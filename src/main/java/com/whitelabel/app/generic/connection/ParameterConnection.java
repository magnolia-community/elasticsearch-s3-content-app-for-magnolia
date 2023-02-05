package com.whitelabel.app.generic.connection;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParameterConnection implements Serializable {
	private String server;
	private String port;

}
