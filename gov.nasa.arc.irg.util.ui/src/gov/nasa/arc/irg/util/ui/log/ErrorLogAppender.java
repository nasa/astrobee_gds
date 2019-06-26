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
package gov.nasa.arc.irg.util.ui.log;

import gov.nasa.arc.irg.util.ui.UtilUIActivator;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * This is a custom appender to take Apache log4j logged events 
 * and have them show up automagically in the Error Log view in Eclipse.
 * 
 * NOTE:  In order for the apache plugin to know about any appenders it will be loading, you must have the BuddyPolicy set up.  
 * For the apache (or logging) plugin, you should have in the MANIFEST.MF: Eclipse-BuddyPolicy: registered
 * and in all plugins that contribute their own appenders, in their manifest you must register the apache (or logging) plugin as a buddy,
 * namely:  Eclipse-RegisterBuddy: org.apache.log4j
 * 
 * Alternately you can add it manually as follows:
 * Simply call ErrorLogAppender.init() to add it to the root logger after you have called your IrgLoggingConfigurator.init() 
 * and all log4j messages will show up in the error log.
 * Alternately you can add it to just the loggers you want, ie logger.addAppender(ErrorLogAppender.INSTANCE); 
 * @author tecohen
 *
 */
public class ErrorLogAppender extends AppenderSkeleton {
	
	public static ErrorLogAppender INSTANCE = new ErrorLogAppender();
	protected static boolean initialized = false;
	
	public ErrorLogAppender() {
	}
	
	public synchronized static void init() {
		if (!initialized){
			Logger.getRootLogger().addAppender(INSTANCE);
			initialized = true;
		}
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean requiresLayout() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void append(LoggingEvent event) {
		String loggerName = event.getLoggerName();
		Plugin plugin = UtilUIActivator.getDefault(); // default plugin, this one
		if (plugin == null){
			return;
		}
		Bundle bun = plugin.getBundle(); // default bundle, this one
		ILog log = plugin.getLog(); // default log, this one.
		Class<?> theClass = null;
		try {
			theClass = Class.forName(loggerName);
			bun = FrameworkUtil.getBundle(theClass);
			//log = Platform.getLog(bun);
		} catch (ClassNotFoundException e) {
			// did not find it, use defaults.
		}
		
		
		Throwable throwable = null;
		if (event.getThrowableInformation() != null){
			throwable = event.getThrowableInformation().getThrowable();
		}
		Status status = new Status(toStatusSeverity(event.getLevel()), 
								   bun.getSymbolicName(),
								   Status.OK, 
								   event.getRenderedMessage(), 
								   throwable);
		
		if (log != null){
			log.log(status);
		}
	}
	
	/**
	 * Convert the error level to a status severity
	 * @param level
	 * @return
	 */
	public static int toStatusSeverity(Level level){
		int severity = Status.OK;
		int levelInt = level.toInt();
		
		if (levelInt >= Level.ERROR_INT) {
			severity = Status.ERROR;
		} else if (levelInt >= Level.WARN_INT) {
			severity = Status.WARNING;
		} else if (levelInt >= Level.DEBUG_INT) {
			severity = Status.INFO;
		}

		return severity;
	}

}
