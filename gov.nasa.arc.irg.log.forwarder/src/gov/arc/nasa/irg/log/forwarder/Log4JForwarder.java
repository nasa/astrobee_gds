/******************************************************************************
 * Copyright © 2019, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration. All 
 * rights reserved.
 * 
 * The Astrobee Control Station platform is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0. 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 *****************************************************************************/
package gov.arc.nasa.irg.log.forwarder;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogService;

public final class Log4JForwarder implements LogListener {
	private static final Logger logger = Logger.getLogger(Log4JForwarder.class);

	private Logger getLogger(final Bundle bundle) {
		final String name = bundle.getSymbolicName();
		final Logger logger = Logger.getLogger(name);
		return logger;
	}

	private Level convertLevel(int level) {
		switch (level) {
		case LogService.LOG_ERROR: return Level.ERROR;
		case LogService.LOG_WARNING: return Level.WARN;
		case LogService.LOG_INFO: return Level.INFO;
		case LogService.LOG_DEBUG: return Level.DEBUG;
		default:
			logger.warn("Level " + level + " not supported!");
			return Level.INFO;
		}
	}

	@Override
	public void logged(final LogEntry entry) {
		final Bundle b = entry.getBundle();
		final Logger logger = getLogger(b);

		logger.log(convertLevel(entry.getLevel()), entry.getMessage(), entry.getException());
	}

}
