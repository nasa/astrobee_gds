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
package gov.nasa.arc.irg.freeflyer.rapid;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class LogEntry {
	public static final String ACK		= "Ack";
	public static final String COMMAND	= "Command";
	public static final String FILE		= "File";
	public static final String ERROR	= "Error";
	public static final String WARNING	= "Warning";
	public static final String PREVIEW	= "Preview"; // for testing, will go away
	
	private final String timestamp;
	private final String category;
	private String entry;
	private final String agent;
	private final String cmdId;
	private final String ackMessage;
	
	protected static SimpleDateFormat s_dateFormatUTC = new SimpleDateFormat("HH:mm:ss");
	{
		s_dateFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	public LogEntry(Date time, String cat, String entry, String agent, String cmdId) {
		timestamp = s_dateFormatUTC.format(time);
		category = cat;
		this.entry = entry;
		this.agent = agent;
		this.cmdId = cmdId;
		ackMessage = "";
	}
	
	public LogEntry(Date time, String cat, String entry, String agent, String cmdId, String ackMsg) {
		timestamp = s_dateFormatUTC.format(time);
		category = cat;
		this.entry = entry;
		this.agent = agent;
		this.cmdId = cmdId;
		this.ackMessage = ackMsg;
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	
	public String getCategory() {
		return category;
	}
	
	public String getAckMessage() {
		return ackMessage;
	}
	
	public String getEntry() {
		return entry;
	}
	
	// Allows Unknown Command messages to be updated
	public void setEntry(String newMsg) {
		entry = newMsg;
	}
	
	public String getAgent() {
		return agent;
	}
	
	public String getCmdId() {
		return cmdId;
	}
	
	@Override
	public String toString() {
		return timestamp + "\t" + getAgent() + ": " + getEntry();
	}
}
