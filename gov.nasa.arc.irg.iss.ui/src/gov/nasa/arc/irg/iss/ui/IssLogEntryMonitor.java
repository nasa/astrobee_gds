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

import gov.nasa.arc.irg.iss.ui.view.log.ILogEntryChangedListener;
import gov.nasa.arc.irg.iss.ui.view.log.ILogFileReadListener;
import gov.nasa.arc.irg.iss.ui.view.log.internal.IssLogEntry;
import gov.nasa.arc.irg.iss.ui.view.log.internal.IssLogService;
import gov.nasa.arc.irg.iss.ui.view.log.internal.TailInputStream;
import gov.nasa.arc.irg.iss.ui.view.log.util.LogViewUtils;
import gov.nasa.arc.irg.util.log.IrgLevel;
import gov.nasa.arc.irg.util.ui.status.AlertBarUtil;
import gov.nasa.util.StrUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;

/**
 * This monitors log activity and reads the log file.
 * This must be called by the application to ensure initialization at the right time.
 * 
 * @author tecohen, rjtorres
 *
 */
public class IssLogEntryMonitor implements LogListener {
	private final static Logger logger = Logger.getLogger(IssLogEntryMonitor.class);

	public static final long MAX_FILE_LENGTH = 1048576; //1024 * 1024;
	public static final Level MIN_LEVEL = Level.WARN;
	public static final Level MIN_ACK_REQUIRED_LEVEL = IrgLevel.ALERT;

	private static List<ILogFileReadListener> s_logFileReadListeners = new ArrayList<ILogFileReadListener>();
	private static List<ILogEntryChangedListener> s_logEntryChangedListeners = new ArrayList<ILogEntryChangedListener>();

	public static final IssLogEntryMonitor INSTANCE = new IssLogEntryMonitor();

	private static List<IssLogEntry> s_elements = new ArrayList<IssLogEntry>();

	private LogReaderService m_logReader;
	private File m_logFile;

	private IssLogEntryMonitor() {
		Logger.getRootLogger().addAppender(new IssLoggerAppender());
		Logger.getLogger(IssLoggerAppender.class.getName()).setAdditivity(false);

		m_logFile = getBaseUsageLogDirectory();
		IssLogService.getInstance().setLogFile(m_logFile);
		if (m_logReader != null){
			m_logReader.addLogListener(this);
		}

		readLogFile();
		notifyLogFileReadListeners();
	}

	@Override
	protected void finalize() throws Throwable {
		if (m_logReader != null){
			m_logReader.removeLogListener(this);
		}
		super.finalize();
	}

	public File getLogFile() {
		return m_logFile;
	}

	public void setReader(LogReaderService value) {
		m_logReader = value;
	}

	public void notifyLogFileReadListeners() {
		for (ILogFileReadListener l : s_logFileReadListeners) {
			l.logFileRead();
		}
	}

	public void addLogFileReadListener(ILogFileReadListener e) {
		s_logFileReadListeners.add(e);
	}

	public void removeLogFileReadListener(ILogFileReadListener e) {
		s_logFileReadListeners.remove(e);
	}

	public void entryChanged(IssLogEntry entry){
		for (ILogEntryChangedListener l : s_logEntryChangedListeners){
			l.entryChanged(entry);
		}
	}
	
	public void entryAdded(IssLogEntry entry){
		if (entry == null){
			return;
		}
		for (ILogEntryChangedListener l : s_logEntryChangedListeners){
			l.entryAdded(entry);
		}
	}

	public void addLogEntryChangedListener(ILogEntryChangedListener l){
		s_logEntryChangedListeners.add(l);
	}

	public void removeLogEntryChangedListener(ILogEntryChangedListener l){
		s_logEntryChangedListeners.remove(l);
	}

	public void addLogEntry(IssLogEntry entry) {
		s_elements.add(entry);
	}

	public void logged(LogEntry entry) {
		IssLogEntry ile = processEntry(IrgLevel.toLevel(entry.getLevel()), entry.getMessage());
		entryAdded(ile);
	}

	public boolean checkForDuplicateLogEntry(IssLogEntry e) {
		if (!s_elements.isEmpty()){
			for (IssLogEntry entry : s_elements)  {
				if (entry.getTime().equals(e.getTime()))
					return true;
			}
		}
		return false;
	}

	public IssLogEntry[] getElementsFromEntry() {
		return s_elements.toArray(new IssLogEntry[s_elements.size()]);
	}


	public void removeIssLogEntry(IssLogEntry logEntry) {
		String time = logEntry.getTime();

		int idx = 0;
		for (IssLogEntry e : s_elements) {
			if (e.getTime().equals(time)) {
				s_elements.remove(idx);
				break;
			}
			idx++;
		}
	}


	public void ackIssLogEntry(String gpsTimeString, boolean ack) {
		for (IssLogEntry e : s_elements) {
			if (e.getTime().equals(gpsTimeString)) {
				e.acknowledge();
				entryChanged(e);
				break;
			}
		}
	}

	public void ackIssLogEntry(IssLogEntry cle) {
		cle.acknowledge();
		entryChanged(cle);
	}

	public List<IssLogEntry> getIssLogEntryList() {
		return s_elements;
	}

	private File getBaseUsageLogDirectory() {
		return new File(IssLoggingConfigurator.INSTANCE.getLogFilePath());
	}

	private void readLogFile() {
		BufferedReader reader = null;
		try {
			if (!m_logFile.exists()) {
				File parent = m_logFile.getParentFile();
				boolean parentExist = parent.exists();
				if (!parentExist) {
					parentExist = parent.mkdirs();
				}
				if (!parentExist || !m_logFile.createNewFile()){
					AlertBarUtil.alert("Problem creating log file " + m_logFile.getName());
					return; // did not really read it but failed so we are doomed
				}
			}
			reader = new BufferedReader(new InputStreamReader(new TailInputStream(m_logFile, MAX_FILE_LENGTH), "UTF-8"));
			for (;;) {
				String line0 = reader.readLine();
				if (line0 == null)
					break;
				String line = line0.trim();

				// check if there was a warning thrown prior to the log being set up.
				Level lineLevel = processForLevel(line);

				if (lineLevel.isGreaterOrEqual(MIN_ACK_REQUIRED_LEVEL)) {
					IssLogEntry entry = processEntry(lineLevel, line);
					addLogEntry(entry);
				}
			}
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e1) { // do nothing
			}
		}
		return;

	}

	private boolean checkIfTime(String t) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("ddMMMyy");
			sdf.parse(t);
			return true;
		} catch (Exception e) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
				sdf.parse(t);
				// if it doensn't fail
				return true;
			} catch (Exception e2) {
				return false;
			}
		}
	}

	protected Level processForLevel(String line) {
		Level level = Level.DEBUG;
		if (line.contains(Level.INFO.toString()))
			level = Level.INFO;
		else if (line.contains(Level.WARN.toString()))
			level = Level.WARN;
		else if (line.contains(Level.DEBUG.toString()))
			level = Level.DEBUG;
		else if (line.contains(Level.FATAL.toString())) 
			level = Level.FATAL;
		else if (line.contains(Level.ERROR.toString())) 
			level = Level.ERROR;
		else if (line.contains(IrgLevel.ALERT.toString())){
			level = IrgLevel.ALERT;
		} 

		return level;

	}

	/**
	 * Returns true if the given string is a level.
	 * @param line
	 * @return
	 */
	protected boolean isLevel(String line) {
		String text = line;
		if (text.endsWith(":")){
			text = text.substring(0, text.length() - 1);
		}
		Level found = IrgLevel.toLevel(text);
		if (found == Level.DEBUG){
			if (text.toLowerCase().equals("debug")){
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if the pattern is a package name
	 * @param packageName
	 * @return
	 */
	protected boolean isPackage(String packageName){
		Pattern pattern = Pattern.compile("(\\w+\\.)+\\w+");
		Matcher matcher = pattern.matcher(packageName);
		return (matcher.find());
	}

	/**
	 * Processes a given line from the log file
	 * 
	 * @param line
	 * @throws ParseException
	 */
	public IssLogEntry processEntry(Level level, String line) {
		StringTokenizer stok = new StringTokenizer(line, " ");
		int tokens = stok.countTokens();
		String token = null;
		String time = null;
		String levelString = null;
		String description = null;
		boolean foundPackage = false;

		// check for message level
		levelString = level.toString();

		// are we showing info?
		if (MIN_LEVEL.isGreaterOrEqual(level)){
			return null;
		}

		for (int i = 0; i < tokens; i++) {
			token = stok.nextToken();
			switch (i) {
			case 0: {
				// check to see if it is actually a date or time if not it is a description
				if (checkIfTime(token))
					time = token;
				else {
					if (description != null){
						description += " " + token;
					} else {
						description = token;
					}
				}
				break;
			}
			case 1: {
				if (checkIfTime(token)) {
					if (time!=null)
						time += " " + token;
					else
						time = token;
				}
				else { 
					if (description != null){
						description += " " + token;
					} else {
						description = token;
					}
				}

				break;
			}

			//			case 2: {
			//				if (description != null){
			//					description += " " + token;
			//				} else {
			//					description = token;
			//				}
			//				break;
			//			}

			default: {
				if (token.equals("]")) {
					break;
				}
				if (token.equals("|")) {
					break;
				}
				if (token.startsWith("[Thread")){
					break;
				}
				if (isLevel(token)){
					break;
				}
				if (!foundPackage && isPackage(token)){
					foundPackage = true;
					break;
				}
				if (description != null){
					description += " " + token;
				} else {
					description = token;
				}
				break;
			}
			}
		}

		// double check to see that it is not a duplicate error message
		IssLogEntry entry = new IssLogEntry(time, levelString, description, false);
		if (!checkForDuplicateLogEntry(entry)) {
			addLogEntry(entry);
		}

		return entry;
	}

	protected class IssLoggerAppender extends AppenderSkeleton {

		// 37838 - travel

		public IssLoggerAppender(){
			name = IssLoggerAppender.class.getName();
		}

		@Override
		public void close() {
			// no impl.
		}

		@Override
		public boolean requiresLayout() {
			return false;
		}

		@Override
		protected void append(LoggingEvent event) {
			if (event.getLevel().isGreaterOrEqual(MIN_LEVEL)){
//				String status = getEventLevelString(event.getLevel()) +  // do not prepend with the level.
				String status = event.getRenderedMessage() ;
				status = StrUtil.upperFirstChar(status, false);
				String ds = LogViewUtils.convertToCorrectDateFormat(event.getTimeStamp());
				IssLogEntry entry = processEntry(event.getLevel(), ds + " " + status);
				entryAdded(entry);
			}
		}

		protected String getEventLevelString(Level level){
			if (level == null){
				return "";
			}
			if (level.equals(Level.WARN)){
				return "Alert: ";
			}
			String result =  level.toString();
			return StrUtil.upperFirstChar(result, true) + ": ";
		}
	}


}
