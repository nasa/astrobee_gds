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
package gov.nasa.arc.irg.iss.ui.view.log.internal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;

public class IssLogService extends Object implements LogService {

	private static final IssLogService INSTANCE = new IssLogService();
	
	private static final Calendar CALENDAR = Calendar.getInstance();
	private static final char DATE_DELIMETER = '-';
	private static final char TIME_DELIMETER = ':';
	private static final char WHITESPACE = ' ';
	private static final char ZERO_PADDING = '0';

	private static Calendar getCalendar() {
		Date now = new Date();
		IssLogService.CALENDAR.setTime(now);
		return IssLogService.CALENDAR;
	}
	
	private LogReaderService logReader;
	
	private IssLogService log;
	
	private File logFile;
	
	/**
	 * Public getter for the <code>LogUtility</code> singleton instance.
	 * 
	 * @return The <code>LogUtility</code> singleton instance.
	 */
	public static IssLogService getInstance() {
		return IssLogService.INSTANCE;
	}
	
	/** The line separator used in the log output */
	private static final String LINE_SEPARATOR;
	static {
		String s = System.getProperty("line.separator"); //$NON-NLS-1$
		LINE_SEPARATOR = s == null ? "\n" : s; //$NON-NLS-1$
	}
	
	/**
	 * The Writer to log messages to.
	 */
	private Writer writer;
	
	public void setLogReader(LogReaderService value) {
		logReader = value;
	}
	
//	public void log(String planName, String elementName, String type, String oldValue, String newValue) {
//		CommsLogEntry entry = new CommsLogEntry(getDate(), System.getProperty("user.name"), planName, elementName, type, oldValue, newValue);
//		writeLog(entry);
//		log(LogService.LOG_INFO, entry.toString());
//	}
	
//	public void log(String planName, String elementName, String type) {
//		IssLogEntry entry = new IssLogEntry(getDate(), System.getProperty("user.name"), planName, false);
//		writeLog(entry);
//		log(IssLogService.LOG_INFO, entry.toString());
//	}

	
	public LogReaderService getLogReader() {
		return logReader;
	}
	
	public void setLogFile(File logFile) {
		this.logFile = logFile;
	}
	
	public void setLog(IssLogService log) {
		if (equals(log) == true)
			return; // Early return.
		if (this.log != null && log != null)
			return; // Early return.
		this.log = log;
	}
	
	private IssLogService getLog() {
		return log;
	}
	
	
	/**
	 * Returns a Writer for the given OutputStream
	 * 
	 * @param output
	 *            an OutputStream to use for the Writer
	 * @return a Writer for the given OutputStream
	 */
	private Writer logForStream(OutputStream output) {
		try {
			return new BufferedWriter(new OutputStreamWriter(output, "UTF-8")); //$NON-NLS-1$
		} catch (UnsupportedEncodingException e) {
			return new BufferedWriter(new OutputStreamWriter(output));
		}
	}

	/**
	 * If a File is used to log messages to then the File opened and a Writer is created to log messages to.
	 */
	private void openFile() {
		if (writer == null) {
			try {
				writer = logForStream(new FileOutputStream((logFile != null) ? logFile : Platform.getLogFileLocation().toFile(), true));
			} catch (IOException e) {
				writer = logForStream(System.err);
			}
		}
	}

	/**
	 * If a File is used to log messages to then the writer is closed.
	 */
	private void closeFile() {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				// we cannot log here; just print the stacktrace.
				e.printStackTrace();
			}
			writer = null;
		}
	}
	
	private synchronized void writeLog(IssLogEntry logEntry) {
		if (logEntry == null)
			return;
		try {
			checkLogFileSize();
			openFile();
			writeEntry(logEntry);
			writer.flush();
		} catch (Exception e) {
			// any exceptions during logging should be caught
			System.err.println("An exception occurred while writing to the platform log:");//$NON-NLS-1$
			e.printStackTrace(System.err);
			System.err.println("Logging to the console instead.");//$NON-NLS-1$
			// we failed to write, so dump log entry to console instead
			try {
				writer = logForStream(System.err);
				writeEntry(logEntry);
				writer.flush();
			} catch (Exception e2) {
				System.err.println("An exception occurred while logging to the console:");//$NON-NLS-1$
				e2.printStackTrace(System.err);
			}
		} finally {
			closeFile();
		}
	}

	private void writeEntry(IssLogEntry entry) throws IOException {
		write(entry.getTime());
		commaSeparator();
		write(entry.getLevel().toString());
		commaSeparator();
		write(entry.getDescription());
		commaSeparator();
		writeln();
	}

	/**
	 * Writes the given message to the log.
	 * 
	 * @param message
	 *            the message
	 * @throws IOException
	 *             if any error occurs writing to the log
	 */
	private void write(String message) throws IOException {
		if (message != null) {
			writer.write(message);
		}
	}

	/**
	 * Writes a newline log.
	 * 
	 * @throws IOException
	 *             if any error occurs writing to the log
	 */
	private void writeln() throws IOException {
		write(LINE_SEPARATOR);
	}

	/**
	 * Writes a comma to the log.
	 * 
	 * @throws IOException
	 *             if any error occurs writing to the log
	 */
	private void commaSeparator() throws IOException {
		write(","); //$NON-NLS-1$
	}

	/**
	 * Checks the log file size. If the log file size reaches the limit then the log is rotated
	 * 
	 * @return false if an error occurred trying to rotate the log
	 */
	private boolean checkLogFileSize() {
		return true; // no size limitation for now.
	}
	
	/**
	 * Returns a date string using the correct format for the log.
	 * 
	 * @param date
	 *            the Date to format
	 * @return a date string.
	 */
	private static String getDate() {
		Calendar c = IssLogService.getCalendar();
		StringBuilder sb = new StringBuilder();
		appendPaddedInt(c.get(Calendar.YEAR), 4, sb).append(DATE_DELIMETER);
		appendPaddedInt(c.get(Calendar.MONTH) + 1, 2, sb).append(DATE_DELIMETER);
		appendPaddedInt(c.get(Calendar.DAY_OF_MONTH), 2, sb).append(WHITESPACE);
		appendPaddedInt(c.get(Calendar.HOUR_OF_DAY), 2, sb).append(TIME_DELIMETER);
		appendPaddedInt(c.get(Calendar.MINUTE), 2, sb).append(TIME_DELIMETER);
		appendPaddedInt(c.get(Calendar.SECOND), 2, sb);
		return sb.toString();
	}

	private static StringBuilder appendPaddedInt(int value, int pad, StringBuilder buffer) {
		pad = pad - 1;
		if (pad == 0)
			return buffer.append(Integer.toString(value));
		int padding = (int) Math.pow(10, pad);
		if (value >= padding)
			return buffer.append(Integer.toString(value));
		while (padding > value && padding > 1) {
			buffer.append(ZERO_PADDING);
			padding = padding / 10;
		}
		buffer.append(value);
		return buffer;
	}

	@Override
	public void log(int level, String message) {
		log = getLog();
		if (log != null)
			log.log(level, message);
		else
			System.err.println("oeg.eclipse.equinox.log is not loaded");//$NON-NLS-1$
	}

	@Override
	public void log(int level, String message, Throwable exception) {
		// TODO Auto-generated method stub
	}

	@Override
	public void log(ServiceReference sr, int level, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void log(ServiceReference sr, int level, String message, Throwable exception) {
		// TODO Auto-generated method stub
	}
}
