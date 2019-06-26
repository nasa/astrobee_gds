/*******************************************************************************
 * Copyright (c) 2013 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package gov.nasa.arc.irg.iss.ui;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

/**
 * same code just different filename
 */
public class IssButtonEventLoggingConfigurator extends IssLoggingConfigurator {
	
	public static final String BUTTON_LOGGER_NAME = "buttonLogger";
	public static final String BUTTON_LOGGER_APPENDER_NAME = "buttonLoggerAppender";
	private static final Logger s_buttonLogger = Logger.getLogger(BUTTON_LOGGER_NAME);
	
	protected static String m_logDirectory = "";

	protected static  String s_rootLogFileName    = "ISSButtonEventCapture";
	protected static  String s_fullFileName;

	/**
	 * Force initialization always, we don't need the other init.
	 * @param args  Filename, directory name, properties file name.
	 */
	public static synchronized void init(String ... args) {

		if(INSTANCE == null) {
			// need to initialize before using the button logger since it will act as just another Appender
			Logger logger = Logger.getLogger(IssButtonEventLoggingConfigurator.class);
			logger.error("Main logger not initialized.  ButtonLogger must be configured AFTER the main logger has been configured.");
			return;
		}
		
		if (args.length > 0){
			s_rootLogFileName = args[0];
			if (args.length > 1){
				s_directoryName = args[1];
				File logsRuntimeDirectory = new File(s_directoryName);
				try {
					FileUtils.forceMkdir(logsRuntimeDirectory);
				} catch (IOException e) {
				}

				if (logsRuntimeDirectory.exists()){
					try {
						m_logDirectory = logsRuntimeDirectory.getCanonicalPath();
					} catch (IOException e) {
					}
				}
			}
			if (args.length > 2){
				s_propertiesFileName = args[2];
			}
		}

		
		// adjust file for the appender
		RollingFileAppender rfa = new RollingFileAppender();
		rfa.setMaxFileSize("512KB");
		rfa.setMaxBackupIndex(0);
		rfa.setLayout(new PatternLayout("%d{ddMMMyy HH:mm:ss.SSS} %5p [%-9t] %-25c{3} %x | %m%n"));
		s_fullFileName = s_rootLogFileName + getDateString() + ".log";
		rfa.setFile(INSTANCE.getLogFileDirectoryName() + File.separator + s_fullFileName);
		rfa.activateOptions();
		s_buttonLogger.addAppender(rfa);

		s_buttonLogger.setAdditivity(false);
	}
	
	
}
