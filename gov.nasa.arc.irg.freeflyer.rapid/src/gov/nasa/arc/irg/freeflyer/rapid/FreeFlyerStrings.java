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

import org.eclipse.e4.core.di.annotations.Creatable;

@Creatable
public class FreeFlyerStrings {
	public static final String CONNECTED_STRING = "Agent ";
	public static final String UNCONNECTED_STRING = "Please select an Agent in the Status Part";
	
	/** Primary Agent (Astrobee that individual parts follow) */
	final public static String PRIMARY_BEE = "PrimaryAgent";
	
	/** 
	 * RunningPlanInfo for the Primary Agent
	 * This string is not strictly necessary but it facilitates debugging.
	 * Most classes should have RunningPlanInfo injected, not the FreeFlyerPlan itself
	 */
	final public static String RUNNING_PLAN_INFO = "Primary RunningPlanInfo";
	
	
	/** 
	 * For the GuestSciencePlanTraces
	 */
	final public static String GUEST_SCIENCE_PLAN_INFO_1 = "GuestSciencePlanInfo 1";
	final public static String GUEST_SCIENCE_PLAN_INFO_2 = "GuestSciencePlanInfo 2";
	final public static String GUEST_SCIENCE_PLAN_INFO_3 = "GuestSciencePlanInfo 3";
	final public static String[] GUEST_SCIENCE_PLAN_INFO = {
		GUEST_SCIENCE_PLAN_INFO_1, 
		GUEST_SCIENCE_PLAN_INFO_2,
		GUEST_SCIENCE_PLAN_INFO_3
	};
	
	/**
	 * FreeFlyerPlan for the Primary Agent
	 * Only used by AstrobeeStateManager to communicate with RunningPlanInfo
	 * Everything else gets RunningPlanInfo injected.
	 * This string is not strictly necessary but it facilitates debugging.
	 */
	final public static String FREE_FLYER_PLAN = "Primary FreeFlyerPlan";
	
	/** The agent for -agent1 specified on the command line */
	final public static String COMMAND_LINE_AGENT_1 = "Command Line Agent 1";

	/** The agent for -agent2 specified on the command line */
	final public static String COMMAND_LINE_AGENT_2 = "Command Line Agent 2";

	/** The agent for -agent3 specified on the command line */
	final public static String COMMAND_LINE_AGENT_3 = "Command Line Agent 3";
	
	final public static String[] COMMAND_LINE_AGENTS = {
		COMMAND_LINE_AGENT_1, 
		COMMAND_LINE_AGENT_2,
		COMMAND_LINE_AGENT_3
	};
	
	/** The agent selected by checking the box in the first row of the Guest Science Top Part */
	final public static String SELECTED_GUEST_SCIENCE_1 = "Selected Guest Science 1";
	
	/** The agent selected by checking the box in the second row of the Guest Science Top Part */
	final public static String SELECTED_GUEST_SCIENCE_2 = "Selected Guest Science 2";
	
	/** The agent selected by checking the box in the third row of the Guest Science Top Part */
	final public static String SELECTED_GUEST_SCIENCE_3 = "Selected Guest Science 3";
	
	final public static String[] SELECTED_GUEST_SCIENCE = {
		SELECTED_GUEST_SCIENCE_1, 
		SELECTED_GUEST_SCIENCE_2,
		SELECTED_GUEST_SCIENCE_3
	};
	
	/** The manager associated with the -agent1 specified on the command line */
	final public static String GUEST_SCIENCE_ASTROBEE_STATE_MANAGER_1 = "Abridged State Manager 1";
	
	/** The manager associated with the -agent2 specified on the command line */
	final public static String GUEST_SCIENCE_ASTROBEE_STATE_MANAGER_2 = "Abridged State Manager 2";
	
	/** The manager associated with the -agent3 specified on the command line */
	final public static String GUEST_SCIENCE_ASTROBEE_STATE_MANAGER_3 = "Abridged State Manager 3";
	
	final public static String[] GUEST_SCIENCE_ASTROBEE_STATE_MANAGER = {
		GUEST_SCIENCE_ASTROBEE_STATE_MANAGER_1, 
		GUEST_SCIENCE_ASTROBEE_STATE_MANAGER_2,
		GUEST_SCIENCE_ASTROBEE_STATE_MANAGER_3
	};
	
	/** The agent selected in the Hibernate combo on the DockingStationStatusAndCommandingPart */
	final public static String SELECTED_OVERVIEW_BEE = "Selected Overview Bee";
	
	/** An count of the times the workbench has requested a stream. 
	 * If a streaming class sees a higher count than it knows about, it should stop the stream.
	 */
	final public static String VIDEO_STREAMING_COUNTER = "Video Streaming Counter";
	
//	/** Tag for boolean that is true if workbench sees an Astrobee on RAPID */
//	final public static String COMM_CONNECTED = "CommConnected";
}
