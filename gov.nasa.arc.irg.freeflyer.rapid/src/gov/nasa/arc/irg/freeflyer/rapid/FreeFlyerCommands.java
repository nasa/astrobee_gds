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

import gov.nasa.rapid.v2.e4.message.helpers.ParameterList;
import rapid.CommandDef;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_END_LOCATION;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_END_LOCATION_TOLERANCE;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_FRAME_NAME;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_ROT;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_END_LOCATION;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_END_LOCATION_TOLERANCE;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_FRAME_NAME;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_ROT;
import rapid.Subsystem;
import rapid.SubsystemType;
import rapid.ext.astrobee.ARM_METHOD_GRIPPER_CONTROL;
import rapid.ext.astrobee.ARM_METHOD_GRIPPER_CONTROL_DTYPE_OPEN;
import rapid.ext.astrobee.ARM_METHOD_GRIPPER_CONTROL_PARAM_OPEN;
import rapid.ext.astrobee.GUESTSCIENCE_METHOD_START_GUEST_SCIENCE;
import rapid.ext.astrobee.GUESTSCIENCE_METHOD_START_GUEST_SCIENCE_DTYPE_APK_NAME;
import rapid.ext.astrobee.GUESTSCIENCE_METHOD_START_GUEST_SCIENCE_PARAM_APK_NAME;
import rapid.ext.astrobee.PLAN_METHOD_RUN_PLAN;

public class FreeFlyerCommands {

	final public static String SUBSYSTEM_TYPE_NAME = "astrobee";
	final public static String SUBSYSTEM_NAME = "astrobee";
	final public static String ABSOLUTE_FRAME_NAME = "ISS";
	final public static String RELATIVE_FRAME_NAME = "body";
	final public static double TRANSLATION_TOLERANCE = 0.00;// Astrobee ignores this
	final public static String GO_PARAM_NAME = "data";

	final private SubsystemType freeFlyerSubsystemType = new SubsystemType();
	final private Subsystem freeFlyerSubsystem = new Subsystem();

	private static FreeFlyerCommands eInstance;

	public static FreeFlyerCommands getInstance(){
		if(eInstance == null){
			eInstance = new FreeFlyerCommands();
		}
		return eInstance;
	}

	private FreeFlyerCommands() {

		freeFlyerSubsystemType.name = SUBSYSTEM_TYPE_NAME;

		makeStartGuestScienceCommand();
		makeRunPlanCommand();
		makeGripperControlCommand();
		makeSimpleMove6DofCommand();

		freeFlyerSubsystem.name = SUBSYSTEM_NAME;
		freeFlyerSubsystem.subsystemTypeName = SUBSYSTEM_TYPE_NAME;
	}
	
	private void makeSimpleMove6DofCommand() {
		final CommandDef cmdDef = new CommandDef();
		cmdDef.name = MOBILITY_METHOD_SIMPLEMOVE6DOF.VALUE;
		cmdDef.abortable = true;
		cmdDef.suspendable = true;

		ParameterList params = new ParameterList();
		params.clear();
		params.add(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_FRAME_NAME.VALUE, MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_FRAME_NAME.VALUE);
		params.add(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_END_LOCATION.VALUE, MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_END_LOCATION.VALUE);
		params.add(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_END_LOCATION_TOLERANCE.VALUE, MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_END_LOCATION_TOLERANCE.VALUE);
		params.add(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_ROT.VALUE, MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_ROT.VALUE);

		params.assign(cmdDef.parameters.userData);
		freeFlyerSubsystemType.commands.userData.add(cmdDef);
	}
	
	private void makeGripperControlCommand() {
		final CommandDef cmdDef = new CommandDef();
		cmdDef.name = ARM_METHOD_GRIPPER_CONTROL.VALUE;
		cmdDef.abortable = true;
		cmdDef.suspendable = true;

		ParameterList params = new ParameterList();
		params.clear();
		params.add(ARM_METHOD_GRIPPER_CONTROL_PARAM_OPEN.VALUE, ARM_METHOD_GRIPPER_CONTROL_DTYPE_OPEN.VALUE);

		params.assign(cmdDef.parameters.userData);
		freeFlyerSubsystemType.commands.userData.add(cmdDef);
	}

	private void makeRunPlanCommand() {

		final CommandDef cmdDef = new CommandDef();
		cmdDef.name = PLAN_METHOD_RUN_PLAN.VALUE;
		cmdDef.suspendable = false;
		cmdDef.abortable = false;

		freeFlyerSubsystemType.commands.userData.add(cmdDef);
	}

	private void makeStartGuestScienceCommand() {
		final CommandDef cmdDef = new CommandDef();
		cmdDef.name = GUESTSCIENCE_METHOD_START_GUEST_SCIENCE.VALUE;
		cmdDef.abortable = true;
		cmdDef.suspendable = true;

		ParameterList params = new ParameterList();
		params.clear();
		params.add(GUESTSCIENCE_METHOD_START_GUEST_SCIENCE_PARAM_APK_NAME.VALUE, GUESTSCIENCE_METHOD_START_GUEST_SCIENCE_DTYPE_APK_NAME.VALUE);

		params.assign(cmdDef.parameters.userData);
		freeFlyerSubsystemType.commands.userData.add(cmdDef);
	}

	public SubsystemType getFreeFlyerSubsystemType(){
		return freeFlyerSubsystemType;
	}

	public Subsystem getFreeFlyerSubsystem() {
		return freeFlyerSubsystem;
	}
}
