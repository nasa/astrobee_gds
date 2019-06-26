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
package gov.nasa.arc.irg.util.log;

import gov.nasa.arc.irg.util.PropertiesHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Mostly lifted from EnsembleLogginConfigurator. 
 * First, check if log4j has been initialized (e.g. by
 * Ensemble startup) If not, configure with defaults
 * 
 * @author mallan, tecohen
 *
 */
public class IrgLoggingConfigurator {
	private static final String DEFAULT_LOG_FILE_DIRECTORY_NAME = System.getProperty("user.home");
	private static final String DEFAULT_LOG_FILE_NAME = "IrgEclipseApplication.log";
	private static final String DEFAULT_LOG4J_PROPERTIES_FILE = "defaultLog4j.properties";

	protected String m_logFileName = DEFAULT_LOG_FILE_NAME;
	protected JULHandler m_jul;

	public static IrgLoggingConfigurator INSTANCE = null;

	public static synchronized void init(boolean force) {
		if(INSTANCE == null) {
			INSTANCE = new IrgLoggingConfigurator();
			INSTANCE.configureLogging(force);
		}
	}

	public static synchronized void init() {
		init(false);
	}

	/**
	 * set up log4j if it hasn't already been initialized
	 *
	 */
	@SuppressWarnings("unchecked")
	public void configureLogging(boolean force) {

		// check if log4j has been initialized
		int cnt = 0;
		for( Enumeration e = Logger.getRootLogger().getAllAppenders(); e.hasMoreElements(); e.nextElement()) {
			cnt++;
		}
		if(cnt == 0) {
			System.out.println("IrgLoggingConfigurator: No log4j appenders found. Initializing with defaults.");
			System.out.println("IrgLoggingConfigurator: Default log directory is "+DEFAULT_LOG_FILE_DIRECTORY_NAME);
		}
		else if (force == false){
			// be silent if logger is already there...
			//System.out.println("IrgLoggingConfigurator: log4j has already been initialized.");
			return;
		}

		Properties loggerProperties = loadProperties();

		// set the log file property
		String logFilename = getLogFilePath();
		loggerProperties.setProperty("log4j.appender.file.File", logFilename);
		System.out.println("IrgLoggingConfigurator: log file name is "+logFilename);
		PropertyConfigurator.configure(loggerProperties);
		
		//add java util logging to the mix
		m_jul = new JULHandler(java.util.logging.Level.FINE);
		
	}

	/**
	 * Override this method to load properties from a different bundle
	 * @return the loaded properties
	 */
	protected Properties loadProperties() {
		// do initialization
		Properties loggerProperties = new Properties();

		try {
			String propertiesFilePath = getPropertiesFilePath();
			InputStream stream = getClass().getResourceAsStream(propertiesFilePath);
			if (stream != null && (stream.available() > 0)) {
				loggerProperties.load(stream);
			} else {
				loggerProperties = PropertiesHelper.loadProperties(propertiesFilePath);
			}
		}
		catch (IOException e) {
			System.err.println("Unable to configure logging - error loading default properties file.");
		}
		return loggerProperties;
	}
	
	
	public String getLogFileDirectoryName() {
		return DEFAULT_LOG_FILE_DIRECTORY_NAME;
	}

	protected String getPropertiesFilePath() {
		return DEFAULT_LOG4J_PROPERTIES_FILE;
	}

	public String getLogFilePath() {
		return getLogFileDirectoryName() + File.separator + getLogFileName();
	}

	public String getLogFileName() {
		return m_logFileName;
	}

	public void setLogFileName(String logFileName) {
		m_logFileName = logFileName;
	}


}
