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
package gov.nasa.arc.irg.util.ui.monitor;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;

public class MonitorJavaLogger extends Handler {
	protected int m_worked = 0;
	protected final int m_amt; 
	protected final IProgressMonitor m_monitor;
	protected final Logger m_logger;

	protected long m_lastMsgTime = 0;
	protected final long MIN_TIME_BETWEEN_MESSAGES = 20; // msecs

	/**
	 * This class listens for log messages in a given context and uses those to report progress in an IProgressMonitor.
	 * 
	 * IMPORTANT: always call done() when progress is complete. Use try-catch-finally and call done() in the finally block.
	 * 
	 * @param logContext
	 * @param monitor
	 * @param name
	 * @param totalWork
	 */
	@SuppressWarnings("unchecked")
	public MonitorJavaLogger(Class logContext, IProgressMonitor monitor, String name, int totalWork) {
		this(logContext, monitor, name, totalWork, 1);
	}

	@SuppressWarnings("unchecked")
	public MonitorJavaLogger(Class logContext, IProgressMonitor monitor, String name, int totalWork, int amt) {
		m_monitor = monitor;
		m_amt = amt;
		String pkg = logContext.getPackage().getName();
		m_logger = Logger.getLogger(pkg);
		m_logger.setLevel(Level.FINE);
		m_logger.addHandler(this);
		m_monitor.beginTask(name, totalWork);
		setMessage(name);
	}

	/**
	 * Always call done() when progress is complete. This will remove
	 * the custom appender from the logger and call IProgressMonitor.done()
	 */
	public void done() {
		m_monitor.done();
		m_logger.removeHandler(this);
	}

	public int  amountWorked() {
		return m_worked;
	}

	public void worked(int amt) {
		m_monitor.worked(amt);
	}
	
	/**
	 * alias for subTask() - subTask() is a stupid method name. 
	 * @param message
	 */
	public void setMessage(String message) {
		m_monitor.subTask(message);
		m_monitor.worked(m_amt);
	}

	public void subTask(String message) {
		setMessage(message);
	}

	@Override
	public void publish(LogRecord arg0) {
		final Level level = arg0.getLevel();
		String prefix = "\n";
		if(level.equals(Level.SEVERE)) prefix = "ERROR: \n";
		if(level.equals(Level.WARNING)) prefix = "WARNING: \n";
		String msg = prefix + arg0.getMessage();

		long currMsgTime = System.currentTimeMillis();
		if(currMsgTime - m_lastMsgTime > MIN_TIME_BETWEEN_MESSAGES) {
			m_monitor.subTask(msg);
			m_monitor.worked(m_amt);
			m_worked += m_amt;
			m_lastMsgTime = currMsgTime;
		}
	}

	@Override
	public void close() throws SecurityException {
		// do nothing
	}

	@Override
	public void flush() {
		// do nothing
	}


}
