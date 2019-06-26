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

import org.apache.log4j.Logger;

/**
 * Simple wrapper around log4j logger to ensure that a logging configurator gets
 * called before log4j calls are made
 * @author mallan
 *
 */
public class IrgLogTrace implements IrgLog 
{
	Logger trace;
	
	/**
	 * Create with identifier as per log4j standard
	 * @param logName
	 */
	public IrgLogTrace(String logName) {
		trace = Logger.getLogger(logName);
	}
	
	public void log(IrgLog.Level level, String msg) { doTrace(level, msg); }
	
	public void debug(String msg){	trace.debug(msg);	}
	public void info(String msg) {	trace.info (msg);	}
	public void warn(String msg) {	trace.warn (msg);	}
	public void error(String msg){	trace.error(msg);	}
	public void fatal(String msg){	trace.fatal(msg);	}

	public void debug(String msg, Throwable e)	{	trace.debug(msg, e);	}
	public void info (String msg, Throwable e)	{	trace.info (msg, e);	}
	public void warn (String msg, Throwable e)	{	trace.warn (msg, e);	}
	public void error(String msg, Throwable e)	{	trace.error(msg, e);	}
	public void fatal(String msg, Throwable e)	{	trace.fatal(msg, e);	}

	protected void doTrace(IrgLog.Level level, String msg) {
		switch(level) {
		case Debug:	trace.debug(msg); break;
		case Info:	trace.info(msg); break;
		case Warn:	trace.warn(msg); break;
		case Error:	trace.error(msg); break;
		case Fatal:	trace.fatal(msg); break;
		default:	trace.info(msg); break;
		}
	}

	/**
	 * XXX UNIMPLEMENTED
	 */
	public void replace(IrgLog newLog) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * XXX UNIMPLEMENTED
	 */
	public void enableTrace(boolean status) {
		//
	}

}
