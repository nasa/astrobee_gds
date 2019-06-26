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
/*
 * Copyright (c) 2009 United States Government as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.rapid.util.log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * First, check if log4j has been initialized (e.g. by Ensemble startup) If not, configure with defaults. Will not clobber or add to
 * an existing log configuration.
 */
public class RapidLoggingConfigurator {

	private static final String DEFAULT_LOG_FILE_DIRECTORY_NAME = System.getProperty("user.home");
	private static final String DEFAULT_LOG_FILE_NAME           = ".RapidApplication.log";
	private static final String DEFAULT_LOG4J_PROPERTIES_FILE   = "defaultLog4j.properties";
	private static boolean initialized = false;
	
	private static String logFileDir  = DEFAULT_LOG_FILE_DIRECTORY_NAME;
	private static String logFileName = DEFAULT_LOG_FILE_NAME;
	
	public static boolean verbose = true;

    public static synchronized void init(boolean force, String logFileName, String logFileDir) {
        if (!initialized) {
            RapidLoggingConfigurator.logFileName = logFileName;
            RapidLoggingConfigurator.logFileDir  = logFileDir;
            init(force);
        }
    }

    public static synchronized void init(boolean force, String logFileName) {
        if (!initialized) {
            RapidLoggingConfigurator.logFileName = logFileName;
            init(force);
        }
    }

    public static synchronized void init(boolean force) {
        if (!initialized) {
            RapidLoggingConfigurator lc = new RapidLoggingConfigurator();
            lc.configureLogging(force);
            initialized = true;
        }
    }

    public static synchronized void init() {
        if(!initialized) {
            RapidLoggingConfigurator lc = new RapidLoggingConfigurator();
            lc.configureLogging(false);
        }
        initialized = true;
    }

	/**
	 * Set up log4j if it hasn't already been initialized.
	 */
	public void configureLogging(boolean force) {

		// check if log4j has been initialized
	    // there is probably a better way to do this...
		int cnt = 0;
		for (Enumeration e = Logger.getRootLogger().getAllAppenders(); e.hasMoreElements(); e.nextElement()) {
			cnt++;
		}
		if (cnt == 0) {
		    if(verbose) {
		        System.out.println(this.getClass().getSimpleName() + ": No log4j appenders found. Initializing with defaults.");
		        System.out.println(this.getClass().getSimpleName() + ": Default log directory is " + logFileDir);
		    }
		} 
		else if (force == false) {
			// be silent if logger is already there...
			return;
		}

		// do initialization
		Properties loggerProperties = new Properties();

		try {
			InputStream stream = getClass().getResourceAsStream(DEFAULT_LOG4J_PROPERTIES_FILE);
			loggerProperties.load(stream);
		} catch (IOException e) {
			System.err.println("Unable to configure logging - error loading default properties file.");
			return;
		}

		// set the log file property
		String logFilePath = logFileDir + File.separator + logFileName;
		loggerProperties.setProperty("log4j.appender.file.File", logFilePath);
		if(verbose) {
		    System.out.println(this.getClass().getSimpleName() + ": log file path is " + logFilePath);
		}
		PropertyConfigurator.configure(loggerProperties);
	}

	protected String getLogFileDirectoryName() {
		return logFileDir;
	}
}
