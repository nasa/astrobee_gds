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

import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.function.UnaryOperator;

import rapid.Ack;
import rapid.Command;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF;
import rapid.ParameterUnion;
import rapid.ext.astrobee.ACCESSCONTROL_METHOD_GRAB_CONTROL;
import rapid.ext.astrobee.ARM_METHOD_ARM_PAN_AND_TILT;
import rapid.ext.astrobee.GUESTSCIENCE_METHOD_CUSTOM_GUEST_SCIENCE;
import rapid.ext.astrobee.GUESTSCIENCE_METHOD_START_GUEST_SCIENCE;
import rapid.ext.astrobee.GUESTSCIENCE_METHOD_STOP_GUEST_SCIENCE;
import rapid.ext.astrobee.POWER_METHOD_POWER_OFF_ITEM;
import rapid.ext.astrobee.POWER_METHOD_POWER_ON_ITEM;
import rapid.ext.astrobee.SETTINGS_METHOD_SET_CAMERA;
import rapid.ext.astrobee.SETTINGS_METHOD_SET_TELEMETRY_RATE;


public class LogPoster {
	private static List<ILogPosterListener> s_listeners = new ArrayList<ILogPosterListener>();
	private static String s_oldPosts = "";
	private static List<LogEntry> s_entries = new ArrayList<LogEntry>();

	private static DecimalFormat df = new DecimalFormat("#.##"); 
	private static DecimalFormat noDecimal = new DecimalFormat("#"); 
	public final static String UNKNOWN_COMMAND_STRING = "Unknown Command";

	protected static SimpleDateFormat s_dateFormatUTC = new SimpleDateFormat("HH:mm:ss");
	{
		s_dateFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	public static void addListener(ILogPosterListener l) {
		if(!s_listeners.contains(l)) {
			s_listeners.add(l);
			if(s_oldPosts.length() > 0) {
				l.postedToLog(s_oldPosts);
			}
		}
	}

	public static void removeListener(ILogPosterListener l) {
		s_listeners.remove(l);
	}

	private static void notifyListeners(String post) {
		for(ILogPosterListener l : s_listeners) {
			l.postedToLog(post);
		}
	}
	
	private static void printEntryMessageList() {
		System.out.println("Current LogPoster entries:");
		for(LogEntry le : s_entries) {
			System.out.println(" = " + le.getEntry() + " : " + le.getCmdId());
		}
		System.out.println("===========================");
	}
	
	public static void updateLog(String cmdId, Command cmd) {
		UnaryOperator<LogEntry> replaceUnknownCommandWithCommandName = (entry) -> {
			if(entry.getCmdId().equals(cmdId)) {
				String newMsg = entry.getEntry().replaceAll(UNKNOWN_COMMAND_STRING, toTitleCase(cmd.cmdName));
				entry.setEntry(newMsg);
			}
			return entry;
		};
		s_entries.replaceAll(replaceUnknownCommandWithCommandName);
		notifyListeners("YES YOU DO HAVE TO CHANGE ME");
	}

	public static void postToLog(String category, Command cmd, String freeflyer) {
		postToLog(category, getLogString(cmd), freeflyer, cmd.cmdId);
	}

	public static void postToLog(String category, String entry, String freeflyer) {
		postToLog(category, entry, freeflyer, WorkbenchConstants.UNINITIALIZED_STRING);
	}
	
	public static void postToLog(String category, String entry, String freeflyer, String id) {
		String post = s_dateFormatUTC.format(new Date()) + "\t" + entry;

		String previous = s_oldPosts;

		s_oldPosts =  post + "\n" + previous;

		LogEntry le = new LogEntry(new Date(), category, entry, freeflyer, id);

		s_entries.add(0, le);

		notifyListeners(post);
	}

	public static void postAckToLog(String cmdName, Ack ack, String freeflyer) {

		String entry = cmdName + " " + prettyPrintAck(ack);
		String post = s_dateFormatUTC.format(new Date()) + "\t" + entry;

		String previous = s_oldPosts;

		s_oldPosts =  post + "\n" + previous;

		LogEntry le = new LogEntry(new Date(), LogEntry.ACK, entry, freeflyer, ack.cmdId, ack.message);

		s_entries.add(0, le);

		notifyListeners(post);
	}

	public static String prettyPrintAck(Ack ack) {
		if(ack.completedStatus.equals(rapid.AckCompletedStatus.ACK_COMPLETED_CANCELED)) {
			return "Skipped";
		}
		else if(ack.completedStatus.equals(rapid.AckCompletedStatus.ACK_COMPLETED_EXEC_FAILED)) {
			return "Failed";
		}
		else if(ack.completedStatus.equals(rapid.AckCompletedStatus.ACK_COMPLETED_NOT)) {
			return "Pending ...";
		}
		else if(ack.completedStatus.equals(rapid.AckCompletedStatus.ACK_COMPLETED_OK)) {
			return "Completed";
		}
		//ACK_COMPLETED_BAD_SYNTAX
		return "Bad Syntax";
	}

	public static String getLogString(Command cmd) {
		if(cmd.cmdName.equals(GUESTSCIENCE_METHOD_START_GUEST_SCIENCE.VALUE) ||
				cmd.cmdName.equals(GUESTSCIENCE_METHOD_STOP_GUEST_SCIENCE.VALUE) ||
				cmd.cmdName.equals(POWER_METHOD_POWER_ON_ITEM.VALUE) 
						|| cmd.cmdName.equals(POWER_METHOD_POWER_OFF_ITEM.VALUE)) {
			return toTitleCase(cmd.cmdName) + " " + ((ParameterUnion)cmd.arguments.userData.get(0)).s();
		}
		else if(cmd.cmdName.equals(GUESTSCIENCE_METHOD_CUSTOM_GUEST_SCIENCE.VALUE)) {
			return toTitleCase(cmd.cmdName) + " " + ((ParameterUnion)cmd.arguments.userData.get(0)).s() + " " + ((ParameterUnion)cmd.arguments.userData.get(1)).s();
		}
		else if(cmd.cmdName.equals(ARM_METHOD_ARM_PAN_AND_TILT.VALUE)) {
			String panDeg = noDecimal.format(((ParameterUnion)cmd.arguments.userData.get(0)).f());
			String tiltDeg =  noDecimal.format(((ParameterUnion)cmd.arguments.userData.get(1)).f());
			return toTitleCase(cmd.cmdName) + " " + panDeg + ", " + tiltDeg;
		}
		else if(cmd.cmdName.equals(SETTINGS_METHOD_SET_CAMERA.VALUE)) {
			return toTitleCase(cmd.cmdName) + " " + 
					((ParameterUnion)cmd.arguments.userData.get(0)).s() 
					+ ", " + ((ParameterUnion)cmd.arguments.userData.get(1)).s()
					+ ", Frame Rate=" + ((ParameterUnion)cmd.arguments.userData.get(2)).f() 
					+ ", Bandwidth=" + ((ParameterUnion)cmd.arguments.userData.get(3)).f();
		}
		else if(cmd.cmdName.equals(SETTINGS_METHOD_SET_TELEMETRY_RATE.VALUE)) {
			return toTitleCase(cmd.cmdName) + " " + 
					((ParameterUnion)cmd.arguments.userData.get(0)).s() + " = " + 
					((ParameterUnion)cmd.arguments.userData.get(1)).f() + " Hz";
		}
		else if(cmd.cmdName.equals(MOBILITY_METHOD_SIMPLEMOVE6DOF.VALUE)) {
			return prettyPrintTeleopCommand(cmd);
		}
		else if(cmd.cmdName.equals(ACCESSCONTROL_METHOD_GRAB_CONTROL.VALUE)) {
			return toTitleCase(cmd.cmdName);
		}
		else {
			return prettyPrintGenericCommand(cmd);
		}
	}
	
	public static String prettyPrintGenericCommand(Command cmd) {
		StringBuilder sb = new StringBuilder();
		sb.append(toTitleCase(cmd.cmdName));
		
		if(cmd.arguments.userData != null) {
			for(int i=0; i<cmd.arguments.userData.size(); i++) {
				ParameterUnion argument = (ParameterUnion)cmd.arguments.userData.get(i);
				sb.append(" ");
				sb.append(parameterUnionToString(argument));
			}
		}
		return sb.toString();
	}
	
	protected static String parameterUnionToString(ParameterUnion union) {
		
		if(union.discriminator().equals(rapid.DataType.RAPID_BOOL)) {
			return toTitleCase(Boolean.toString(union.b()));
		}
		if(union.discriminator().equals(rapid.DataType.RAPID_DOUBLE)) {
			return Double.toString(union.d());
		}
		if(union.discriminator().equals(rapid.DataType.RAPID_FLOAT)) {
			return Float.toString(union.f());
		}
		if(union.discriminator().equals(rapid.DataType.RAPID_INT)) {
			return Integer.toString(union.i());
		}
		if(union.discriminator().equals(rapid.DataType.RAPID_LONGLONG)) {
			return Long.toString(union.ll());
		}
		if(union.discriminator().equals(rapid.DataType.RAPID_STRING)) {
			return union.s();
		}
		if(union.discriminator().equals(rapid.DataType.RAPID_VEC3d)) {
			StringBuilder sb = new StringBuilder();
			
			for(int i=0; i<3; i++) {
				sb.append((union.vec3d().userData[i]) + ", ");
			}
			return sb.toString();
		}
		if(union.discriminator().equals(rapid.DataType.RAPID_MAT33f)) {
			StringBuilder sb = new StringBuilder();
			
			for(int i=0; i<9; i++) {
				sb.append((union.mat33f().userData[i]) + ", ");
			}
			return sb.toString();
		}
		
		return "";
	}
	
	/** Takes textInCamelCase and returns Text In Title Case */
	public static String toTitleCase(String str) {
		StringBuilder sb = new StringBuilder(str.substring(0, 1).toUpperCase());
		for(int i=1; i<str.length(); i++) {
			if(Character.isUpperCase(str.charAt(i))) {
				sb.append(" " + str.charAt(i));
			} else {
				sb.append(str.charAt(i));
			}
		}
		return sb.toString();
	}

	/** Takes textInCamelCase and returns Text In Title Case */
	public static String toTitleCase(boolean bool) {
		if(bool) {
			return "True";
		}
		return "False";
	}
	
	private static String prettyPrintTeleopCommand(Command cmd) {
		if(cmd.cmdName.equals(MOBILITY_METHOD_SIMPLEMOVE6DOF.VALUE)) {
			String mode = ((ParameterUnion)cmd.arguments.userData.get(0)).s();
			
			return toTitleCase(mode) + " Translate: "
					+ df.format(((ParameterUnion)cmd.arguments.userData.get(1)).vec3d().userData[0]) + ", "
					+ df.format(((ParameterUnion)cmd.arguments.userData.get(1)).vec3d().userData[1]) + ", "
					+ df.format(((ParameterUnion)cmd.arguments.userData.get(1)).vec3d().userData[2])
					+ "; Rotate: "
					+ df.format(((ParameterUnion)cmd.arguments.userData.get(3)).mat33f().userData[0]) + ", "
					+ df.format(((ParameterUnion)cmd.arguments.userData.get(3)).mat33f().userData[1]) + ", "
					+ df.format(((ParameterUnion)cmd.arguments.userData.get(3)).mat33f().userData[2]) + ", "
					+ df.format(((ParameterUnion)cmd.arguments.userData.get(3)).mat33f().userData[3]);
		}
		return "Not Teleop Command";
	}

	public static LogEntry getLastLogEntry() {
		return s_entries.get(0);
	}

	public static List<LogEntry> getLogEntries() {
		return s_entries;
	}

}
