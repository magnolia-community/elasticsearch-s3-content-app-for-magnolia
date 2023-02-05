package com.whitelabel.app.generic.service;

import com.whitelabel.app.generic.others.LogStatus;

public interface LogService {
	public void logger(LogStatus status, String message, Class classType, Exception e);
}
