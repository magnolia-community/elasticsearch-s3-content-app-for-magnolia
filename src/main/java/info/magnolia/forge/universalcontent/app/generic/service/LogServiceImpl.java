package info.magnolia.forge.universalcontent.app.generic.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.forge.universalcontent.app.generic.others.LogStatus;
import info.magnolia.forge.universalcontent.app.generic.ui.table.CustomTable;

public class LogServiceImpl implements LogService {
	Logger log = LoggerFactory.getLogger(CustomTable.class);

	@Override
	public void logger(LogStatus status, String message, Class classType, Exception e) {
		if (LogStatus.ERROR.equals(status)) {
			log.error("[" + classType.getName() + "] " + message + classType.getName(), e);
		} else if (LogStatus.DEBUG.equals(status)) {
			log.debug("[" + classType.getName() + "] " + message + classType.getName(), e);
		}

	}

}
