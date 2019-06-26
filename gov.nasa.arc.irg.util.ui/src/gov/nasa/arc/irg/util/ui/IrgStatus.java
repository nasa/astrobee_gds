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
package gov.nasa.arc.irg.util.ui;

import gov.nasa.arc.irg.util.log.IrgLog;
import gov.nasa.arc.irg.util.log.IrgLogBuffer;

import org.apache.log4j.Logger;


public class IrgStatus extends IrgStatusBase implements IrgLog
{
//	public static String ID_VIEW = "PUT_THE_ID_IN_SUB_CLASS_!!";
	protected boolean		m_doTrace = true;
	protected Logger 		trace = null;
		
	public IrgStatus() {
		super();
	}
	
	public synchronized void replace(IrgLog oldLog) 
	{
		if(oldLog instanceof IrgLogBuffer)
		{
			IrgLogBuffer lb = (IrgLogBuffer)oldLog;
			// add buffered messages before setting trace
			IrgLogBuffer.StringLevel sl;
			for(int i = 0; i < lb.m_buffer.size(); i++)
			{
				sl = lb.m_buffer.get(i);
				logStyledImpl(sl.level, sl.str, null);
			}
			lb.m_buffer.clear();
			
			// transfer ownership of trace 
			trace = lb.trace;
		}
	}
	
	public void log(IrgLog.Level level, String message)
	{	logStyledImpl(level, message, null);	}
	
	public void debug(String message){	logStyledImpl(Level.Debug,	message, null); }
	public void info(String message) {	logStyledImpl(Level.Info,	message, null); }
	public void warn(String message) {	logStyledImpl(Level.Warn,	message, null); }
	public void error(String message){	logStyledImpl(Level.Error,	message, null); }
	public void fatal(String message){	logStyledImpl(Level.Fatal, 	message, null); }
	
	public void debug(String message, Throwable e) {	logStyledImpl(Level.Debug,	message, e); }
	public void info (String message, Throwable e) {	logStyledImpl(Level.Info,	message, e); }
	public void warn (String message, Throwable e) {	logStyledImpl(Level.Warn,	message, e); }
	public void error(String message, Throwable e) {	logStyledImpl(Level.Error,	message, e); }
	public void fatal(String message, Throwable e) {	logStyledImpl(Level.Fatal, 	message, e); }
		
	/**
	 * @param level
	 * @param message
	 * @param e
	 */
	public void logStyledImpl(final Level level, String message, Throwable e) {
		if (trace != null) {
			if (e != null) {
				doTrace(level, message, e);
			} else {
				doTrace(level, message);
			}
		}
		super.postLogMessage(level, message, e);
	}
		
	protected void doTrace(IrgLog.Level level, String msg) {
		if(m_doTrace) {
			switch(level) {
			case Debug:	trace.debug(msg); break;
			case Info:	trace.info (msg); break;
			case Warn:	trace.warn (msg); break;
			case Error:	trace.error(msg); break;
			case Fatal:	trace.fatal(msg); break;
			default:	trace.info (msg); break;
			}
		}
	}
	
	protected void doTrace(IrgLog.Level level, String msg, Throwable e) {
		if(m_doTrace) {
			switch(level) {
			case Debug:	trace.debug(msg, e); break;
			case Info:	trace.info (msg, e); break;
			case Warn:	trace.warn (msg, e); break;
			case Error:	trace.error(msg, e); break;
			case Fatal:	trace.fatal(msg, e); break;
			default:	trace.info (msg, e); break;
			}
		}
	}
	
	public void enableTrace(boolean status) {
		m_doTrace = status;
	}

}  
