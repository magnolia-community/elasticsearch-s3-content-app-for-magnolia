package com.whitelabel.app.generic.ui.table;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderBy implements Serializable {
	private String column;
	private boolean isAscending;
}
