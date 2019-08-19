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
package gov.nasa.arc.irg.plan.ui.io;

import org.eclipse.core.runtime.Platform;

public class WorkbenchConstants {
	public static final String FAKE_CURRENT_PLAN_MESSAGE = "-fakeCurrentPlanMessage";
	public static final String SHOW_ENGINEERING_CONFIGURATION_STRING = "-engineering";
	public static final String NO_IMAGES = "-noImages";
	private static final String WORLD_FOLDER = "-world";
	private static final String DEFAULT_WORLD_FOLDER_NAME = "IssWorld";
	public static final String ENLARGE = "-enlargeButtons";
	public static final int enlargedWidth = 100;
	public static final int enlargedHeight = 50;
	public static final int enlargedFontSize = 16;
	
	public static final String AGENT_1_PARAM_STRING = "-agent1";
	public static final String AGENT_2_PARAM_STRING = "-agent2";
	public static final String AGENT_3_PARAM_STRING = "-agent3";
	
	public static final String GREEN = "green";
	public static final String PURPLE = "purple";
	public static final String BLUE = "blue";
	public static final String ORANGE = "orange";
	public static final String YELLOW = "yellow";
	public static final String PINK = "pink";
	public static final String WHITE = "white";
	
	public static final String HONEY = "Honey";
	public static final String QUEEN = "Queen";
	public static final String BUMBLE = "Bumble";

	public static final String STOP_BUTTON_TEXT = "Station Keep";
	
	public static final String CONNECTED_TOOLTIP = "Connected to Selected Astrobee";
	public static final String DISCONNECTED_TOOLTIP = "Not Connected to an Astrobee";
	
	public static final String HIBERNATE_TOOLTIP = "Shut Down Astrobee";
	public static final String WAKE_TOOLTIP = "Turn On Astrobee";
	public static final String GRAB_CONTROL_TOOLTIP = "Grab Control of Astrobee";
	public static final String STOP_TOOLTIP = "Station Keep Astrobee at Current Location";
	
	public static final String CONTROL_TOOLTIP = "User that has Control of Astrobee";
	public static final String BATT_TOOLTIP = "Estimated Hours and Minutes Left of Charge";
	public static final String LOW_BATT_TOOLTIP = "Low Battery";
	
	public static final String SUMMARY_LABEL_TOOLTIP = "Information about Current Astrobee State";
	public static final String PLAN_LABEL_TOOLTIP = "Currently Loaded Plan";
	public static final String PLAN_STATUS_TOOLTIP = "Status of Currently Loaded Plan";
	
	public static final String INFO_LABEL_TOOLTIP = "Time and Sequence Number of Latest Image";
	
	public static final String SENDING_PLAN_STRING = "Sending Plan ...";
	
	public static final String wakeString = "Wake";
	public static final String hibernateString = "Hibernate";
	
	public static final String UNINITIALIZED_STRING = "";
	
	public static final int LOW_BATT_THRESHOLD = 25;
	
	public static final String NO_PLAN_LOADED = "No Plan Loaded";
	
	private static WorkbenchConstants instance = new WorkbenchConstants();
	
	/** true if that flag is in the command line args */
	public static boolean isFlagPresent(String flag) {
		return instance.isFlagPresentInternal(flag);
	}
	
	/** returns string value associated with that command-line parameter */
	public static String getStringValueOfParameter(String param) {
		return instance.getValueOfParameterInternal(param);
	}
	
	public static String getWorldFolderName() {
		String worldFolderName = getStringValueOfParameter(WORLD_FOLDER);
		if(worldFolderName == null) {
			worldFolderName = DEFAULT_WORLD_FOLDER_NAME;
		}
		return worldFolderName;
	}
	
	public static boolean worldIsGraniteLab() {
		String world = getWorldFolderName();
		if(world.contains("ranite")) {
			return true;
		}
		return false;
	}
	
	public static Float getFloatValueOfParameter(String param) {
		String raw = instance.getValueOfParameterInternal(param);
		if(raw != null) {
			return Float.valueOf(raw);
		}
		return null;
	}
	
	private String getValueOfParameterInternal(String param) {
		String[] args = Platform.getCommandLineArgs();

		for(int i=0; i<args.length-1; i++) {
			if(args[i].equals(param)) {
				return args[i+1];
			}
		}
		return null;
	}

	private boolean isFlagPresentInternal(String flag) {
		String[] args = Platform.getCommandLineArgs();

		for(int i=0; i<args.length; i++) {
			if(args[i].equals(flag)) {
				return true;
			}
		}
		return false;
	}
}
