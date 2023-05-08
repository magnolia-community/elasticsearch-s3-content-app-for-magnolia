package info.magnolia.forge.universalcontent.app.generic.service;

import info.magnolia.forge.universalcontent.app.generic.others.LogStatus;

public interface LogService {
	public void logger(LogStatus status, String message, Class classType, Exception e);
}
