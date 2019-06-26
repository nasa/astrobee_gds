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
package gov.nasa.arc.simulator.freeflyer.telemetry;

import rapid.ext.astrobee.AgentState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateGds;

public class SimulatorAstrobeeStateGds extends AstrobeeStateGds {

	public SimulatorAstrobeeStateGds() {
		setUpInitialAgentState();
		setUpInitalArmState();
	}
	
	private void setUpInitialAgentState() {
		operatingState = OperatingState.READY;
		planExecutionState = ExecutionState.IDLE;
		guestState = ExecutionState.IDLE;
		mobilityState = MobilityState.STOPPING;
		subMobilityState = 0;
		
		proximity = 1;
		profileName = "Default_Safeguard";
		flightMode = "Default flight mode";
		targetLinearVelocity = 1;
		targetLinearAccel = 1;
		targetAngularVelocity = 1;
		targetAngularAccel = 1;
		
		collisionDistance = 1;
		enableHolonomic = false;
		checkKeepouts = true;
		checkObstacles = true;
		enableAutoReturn = true;
		bootTime = 1;
	}
	
	private void setUpInitalArmState() {
		armJointState = AstrobeeStateGds.ArmJointState.STOWED;
		armGripperState = AstrobeeStateGds.ArmGripperState.CLOSED;
	}
	
	public AgentState toAgentState() {
		AgentState state = new AgentState();

		state.operatingState = translateOperatingState(operatingState);
		state.executionState = translateExecutionState(planExecutionState);
		state.guestScienceState = translateExecutionState(guestState);
		state.mobilityState = translateMobilityState(mobilityState);
		state.subMobilityState = subMobilityState;
		
		state.proximity = proximity;
		state.profileName = profileName;
		state.flightMode = flightMode;
		
		state.targetLinearVelocity = targetLinearVelocity;
		state.targetLinearAccel = targetLinearAccel;
		state.targetAngularVelocity = targetAngularVelocity;
		state.targetAngularAccel = targetAngularAccel;
		
		state.collisionDistance = collisionDistance;
		state.enableHolonomic = enableHolonomic;
		state.checkKeepouts = checkKeepouts;
		state.checkObstacles = checkObstacles;
		state.enableAutoReturn = enableAutoReturn;
		state.bootTime = bootTime;
		return state;
	}
	
	private rapid.ext.astrobee.OperatingState translateOperatingState(OperatingState operState) {
		// It won't let me use a switch statement.  Even though it is an Enum.
		if(operState.equals(OperatingState.AUTO_RETURN)) {
			return rapid.ext.astrobee.OperatingState.OPERATING_STATE_AUTO_RETURN;
		}else if(operState.equals(OperatingState.FAULT)) {
			return rapid.ext.astrobee.OperatingState.OPERATING_STATE_FAULT;
		}else if(operState.equals(OperatingState.PLAN_EXECUTION)) {
			return rapid.ext.astrobee.OperatingState.OPERATING_STATE_PLAN_EXECUTION;
		}else if(operState.equals(OperatingState.READY)) {
			return rapid.ext.astrobee.OperatingState.OPERATING_STATE_READY;
		}else if(operState.equals(OperatingState.TELEOPERATION)) {
			return rapid.ext.astrobee.OperatingState.OPERATING_STATE_TELEOPERATION;
		}
		return null;
	}

	private rapid.ext.astrobee.ExecutionState translateExecutionState(ExecutionState execState) {
		if(execState.equals(ExecutionState.EXECUTING)) {
			return rapid.ext.astrobee.ExecutionState.EXECUTION_STATE_EXECUTING;
		}
		else if(execState.equals(ExecutionState.IDLE)) {
			return rapid.ext.astrobee.ExecutionState.EXECUTION_STATE_IDLE;
		}
		else if(execState.equals(ExecutionState.PAUSED)) {
			return rapid.ext.astrobee.ExecutionState.EXECUTION_STATE_PAUSED;
		}
		return rapid.ext.astrobee.ExecutionState.EXECUTION_STATE_IDLE;
	}

	private rapid.ext.astrobee.MobilityState translateMobilityState(MobilityState mobilityState) {
		if(mobilityState.equals(MobilityState.DOCKING)) {
			return rapid.ext.astrobee.MobilityState.MOBILITY_STATE_DOCKING;
		}else if(mobilityState.equals(MobilityState.DRIFTING)) {
			return rapid.ext.astrobee.MobilityState.MOBILITY_STATE_DRIFTING;
		}else if(mobilityState.equals(MobilityState.FLYING)) {
			return rapid.ext.astrobee.MobilityState.MOBILITY_STATE_FLYING;
		}else if(mobilityState.equals(MobilityState.PERCHING)) {
			return rapid.ext.astrobee.MobilityState.MOBILITY_STATE_PERCHING;
		}else if(mobilityState.equals(MobilityState.STOPPING)) {
			return rapid.ext.astrobee.MobilityState.MOBILITY_STATE_STOPPING;
		}
		return null;
	}

	public void setOperatingState(OperatingState operatingState) {
		this.operatingState = operatingState;
	}
	
	public void setSubMobilityState(final int subMobilityState){
		this.subMobilityState = subMobilityState;
	}

	public void setPlanExecutionState(ExecutionState planExecutionState) {
		this.planExecutionState = planExecutionState;
	}

	public void setGuestState(ExecutionState guestState) {
		this.guestState = guestState;
	}

	public void setMobilityState(MobilityState mobilityState) {
		this.mobilityState = mobilityState;
	}

	public void setProximity(float proximity) {
		this.proximity = proximity;
	}

	public void setTargetLinearVelocity(float maxVel) {
		this.targetLinearVelocity = maxVel;
	}
	
	public void setTargetLinearAccel(float maxAccel) {
		this.targetLinearAccel = maxAccel;
	}
	
	public void setTargetAngularVelocity(float maxAVel) {
		this.targetAngularVelocity = maxAVel;
	}
	
	public void setTargetAngularAccel(float maxAAccel) {
		this.targetAngularAccel = maxAAccel;
	}
	
	public void setBootTime(int bootTime) {
		this.bootTime = bootTime;
	}

	public void setProfileName(String name) {
		this.profileName = name;
	}
	
	public void setFlightMode(String name) {
		this.flightMode = name;
	}
	
	public void setEnableAutoReturn(boolean allow) {
		this.enableAutoReturn = allow;
	}
	
	public void setEnableHolonomic(boolean allow) {
		this.enableHolonomic = allow;
	}
	
	public void setCheckKeepouts(boolean check) {
		this.checkKeepouts = check;
	}
	
	public void setCheckObstacles(boolean check) {
		this.checkObstacles = check;
	}
	
	public void setCollisionDistance(float margin) {
		this.collisionDistance = margin;
	}
}
