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
package gov.nasa.arc.irg.freeflyer.rapid.state;

import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import rapid.ext.astrobee.AgentState;

public class AstrobeeStateGds {

	public enum OperatingState { 
		READY, PLAN_EXECUTION, TELEOPERATION, AUTO_RETURN, FAULT
	}

	// Plan execution state
	public enum ExecutionState {
		IDLE, EXECUTING, PAUSED, ERROR
	}

	public enum MobilityState {
		DRIFTING, STOPPING, FLYING, DOCKING, PERCHING,
	}

	public int SubMobilityState = 0;

	public enum ArmJointState {
		UNKNOWN, STOWED, DEPLOYING, STOPPED, MOVING, STOWING
	}

	public enum ArmGripperState {
		UNKNOWN, UNCALIBRATED, CALIBRATING, CLOSED, OPEN
	}

	protected OperatingState operatingState;
	protected ExecutionState planExecutionState;
	protected ExecutionState guestState;
	protected MobilityState mobilityState;
	protected int subMobilityState = Integer.MAX_VALUE;

	protected ArmJointState armJointState;
	protected ArmGripperState armGripperState;

	protected float proximity;
	protected String profileName;
	protected String flightMode;
	protected float targetLinearVelocity;
	protected float targetLinearAccel;
	protected float targetAngularVelocity;
	protected float targetAngularAccel;

	protected float collisionDistance;
	protected boolean enableHolonomic;
	protected boolean checkKeepouts;
	protected boolean checkObstacles;
	protected boolean enableAutoReturn;
	protected int bootTime;

	protected String uninitializedString = WorkbenchConstants.UNINITIALIZED_STRING;
	protected String prettyMobilityState = uninitializedString;
	protected String DOCKING_STRING = "Docking ";
	protected String DOCKED_STRING = "Docked";
	protected String UNDOCKING_STRING = "Undocking ";
	protected String PERCHING_STRING = "Perching ";
	protected String PERCHED_STRING = "Perched";
	protected String UNPERCHING_STRING = "Unperching ";
	protected String STOPPING_STRING = "Stopping ";
	protected String STOPPED_STRING = "Stopped";

	protected boolean initialized = false;
	int i=0;

	public void ingestAgentState(final AgentState agentState) {
		updateOperatingState(agentState.operatingState);
		updatePlanExecutionState(agentState.executionState);
		updateGuestState(agentState.guestScienceState);
		updateMobilityState(agentState.mobilityState, agentState.subMobilityState);

		proximity = agentState.proximity;
		profileName = agentState.profileName;
		flightMode = agentState.flightMode;
		targetLinearVelocity = agentState.targetLinearVelocity;
		targetLinearAccel = agentState.targetLinearAccel;
		targetAngularVelocity = agentState.targetAngularVelocity;
		targetAngularAccel = agentState.targetAngularAccel;

		collisionDistance = agentState.collisionDistance;
		enableHolonomic = agentState.enableHolonomic;
		checkKeepouts = agentState.checkKeepouts;
		checkObstacles = agentState.checkObstacles;
		enableAutoReturn = agentState.enableAutoReturn;
		bootTime = agentState.bootTime;

		if(!initialized) {
			initialized = true;
		}
	}
	
	public void ingestArmState(final rapid.ext.astrobee.ArmState armState) {
		rapid.ext.astrobee.ArmJointState rapidJointState = armState.jointState;
		updateArmJointState(rapidJointState);
		
		rapid.ext.astrobee.ArmGripperState rapidGripperState = armState.gripperState;
		updateArmGripperState(rapidGripperState);
	}

	private void updateArmGripperState(rapid.ext.astrobee.ArmGripperState rapidGripperState) {
		if(rapidGripperState.equals(rapid.ext.astrobee.ArmGripperState.ARM_GRIPPER_STATE_CALIBRATING)) {
			armGripperState = ArmGripperState.CALIBRATING;
		}
		if(rapidGripperState.equals(rapid.ext.astrobee.ArmGripperState.ARM_GRIPPER_STATE_CLOSED)) {
			armGripperState = ArmGripperState.CLOSED;
		}
		if(rapidGripperState.equals(rapid.ext.astrobee.ArmGripperState.ARM_GRIPPER_STATE_OPEN)) {
			armGripperState = ArmGripperState.OPEN;
		}
		if(rapidGripperState.equals(rapid.ext.astrobee.ArmGripperState.ARM_GRIPPER_STATE_UNCALIBRATED)) {
			armGripperState = ArmGripperState.UNCALIBRATED;
		}
		if(rapidGripperState.equals(rapid.ext.astrobee.ArmGripperState.ARM_GRIPPER_STATE_UNKNOWN)) {
			armGripperState = ArmGripperState.UNKNOWN;
		}
	}

	private void updateArmJointState(rapid.ext.astrobee.ArmJointState rapidJointState)  {
		if(rapidJointState.equals(rapid.ext.astrobee.ArmJointState.ARM_JOINT_STATE_DEPLOYING)) {
			armJointState = ArmJointState.DEPLOYING;
		}
		if(rapidJointState.equals(rapid.ext.astrobee.ArmJointState.ARM_JOINT_STATE_MOVING)) {
			armJointState = ArmJointState.MOVING;
		}
		if(rapidJointState.equals(rapid.ext.astrobee.ArmJointState.ARM_JOINT_STATE_STOPPED)) {
			armJointState = ArmJointState.STOPPED;
		}
		if(rapidJointState.equals(rapid.ext.astrobee.ArmJointState.ARM_JOINT_STATE_STOWED)) {
			armJointState = ArmJointState.STOWED;
		}
		if(rapidJointState.equals(rapid.ext.astrobee.ArmJointState.ARM_JOINT_STATE_STOWING)) {
			armJointState = ArmJointState.STOWING;
		}
		if(rapidJointState.equals(rapid.ext.astrobee.ArmJointState.ARM_JOINT_STATE_UNKNOWN)) {
			armJointState = ArmJointState.UNKNOWN;
		}
	}
	
	
	public boolean getInitialized() {
		return initialized;
	}

	private void updateMobilityState(final rapid.ext.astrobee.MobilityState rapidMobilityState, final int rapidSubMobilityState) {
		subMobilityState = rapidSubMobilityState;

		if(rapidMobilityState.equals(rapid.ext.astrobee.MobilityState.MOBILITY_STATE_DOCKING)) {
			mobilityState = MobilityState.DOCKING;

			if(subMobilityState > 0) {
				prettyMobilityState = DOCKING_STRING + subMobilityState;
			} else if(subMobilityState < 0) {
				prettyMobilityState = UNDOCKING_STRING + -subMobilityState;
			} else {
				prettyMobilityState = DOCKED_STRING;
			}
		}else if(rapidMobilityState.equals(rapid.ext.astrobee.MobilityState.MOBILITY_STATE_DRIFTING)) {
			mobilityState = MobilityState.DRIFTING;
			prettyMobilityState = MobilityState.DRIFTING.name();
		}else if(rapidMobilityState.equals(rapid.ext.astrobee.MobilityState.MOBILITY_STATE_FLYING)) {
			mobilityState = MobilityState.FLYING;
			prettyMobilityState = MobilityState.FLYING.name();
		}else if(rapidMobilityState.equals(rapid.ext.astrobee.MobilityState.MOBILITY_STATE_PERCHING)) {
			mobilityState = MobilityState.PERCHING;

			if(subMobilityState > 0) {
				prettyMobilityState = PERCHING_STRING + subMobilityState;
			} else if(subMobilityState < 0) {
				prettyMobilityState = UNPERCHING_STRING + -subMobilityState;
			} else {
				prettyMobilityState = PERCHED_STRING;
			}

		}else if(rapidMobilityState.equals(rapid.ext.astrobee.MobilityState.MOBILITY_STATE_STOPPING)) {
			mobilityState = MobilityState.STOPPING;

			if(subMobilityState > 0) {
				prettyMobilityState = STOPPING_STRING;
			} else {
				prettyMobilityState = STOPPED_STRING;
			}

		}
	}

	private void updatePlanExecutionState(final rapid.ext.astrobee.ExecutionState rapidExecState) {
		planExecutionState = translateExecutionState(rapidExecState);
	}

	private void updateGuestState(final rapid.ext.astrobee.ExecutionState rapidGuestState) {
		guestState = translateExecutionState(rapidGuestState);
	}

	private ExecutionState translateExecutionState(final rapid.ext.astrobee.ExecutionState rapidExecState) {
		if(rapidExecState.equals(rapid.ext.astrobee.ExecutionState.EXECUTION_STATE_EXECUTING)) {
			return ExecutionState.EXECUTING;
		}
		else if(rapidExecState.equals(rapid.ext.astrobee.ExecutionState.EXECUTION_STATE_IDLE)) {
			return ExecutionState.IDLE;
		}
		else if(rapidExecState.equals(rapid.ext.astrobee.ExecutionState.EXECUTION_STATE_PAUSED)) {
			return ExecutionState.PAUSED;
		}
		return ExecutionState.ERROR;
	}


	private void updateOperatingState(final rapid.ext.astrobee.OperatingState rapidOpState) {
		// It won't let me use a switch statement.  Even though it is an Enum.
		if(rapidOpState.equals(rapid.ext.astrobee.OperatingState.OPERATING_STATE_AUTO_RETURN)){
			operatingState = OperatingState.AUTO_RETURN;
		}else if(rapidOpState.equals(rapid.ext.astrobee.OperatingState.OPERATING_STATE_PLAN_EXECUTION)){
			operatingState = OperatingState.PLAN_EXECUTION;
		}else if(rapidOpState.equals(rapid.ext.astrobee.OperatingState.OPERATING_STATE_READY)){
			operatingState = OperatingState.READY;
		}else if(rapidOpState.equals(rapid.ext.astrobee.OperatingState.OPERATING_STATE_TELEOPERATION)){
			operatingState = OperatingState.TELEOPERATION;
		}else if(rapidOpState.equals(rapid.ext.astrobee.OperatingState.OPERATING_STATE_FAULT)){
			operatingState = OperatingState.FAULT;
		}
	}

	public OperatingState getOperatingState() {
		return operatingState;
	}

	public int getSubMobilityState(){
		return subMobilityState;
	}

	public String getPrettyMobilityStateName() {
		return prettyMobilityState;
	}

	public String getOperatingStateName() {
		if(operatingState == null) {
			return uninitializedString;
		}
		return operatingState.name();
	}

	public ExecutionState getPlanExecutionState() {
		return planExecutionState;
	}

	public String getPlanExecutionStateName() {
		if(planExecutionState != null) {
			return planExecutionState.name();
		}
		return uninitializedString;
	}

	public MobilityState getMobilityState() {
		return mobilityState;
	}

	public String getMobilityStateName() {
		if(mobilityState != null) {
			return mobilityState.name();
		}
		return uninitializedString;
	}

	public ExecutionState getGuestState() {
		return guestState;
	}

	public float getProximity() {
		return proximity;
	}

	public float getTargetLinearVelocity() {
		return targetLinearVelocity;
	}

	public String getTargetLinearVelocityString() {
		if(profileName != null) {
			return Float.toString(targetLinearVelocity);
		}
		return uninitializedString;
	}

	public float getTargetLinearAccel() {
		return targetLinearAccel;
	}

	public String getTargetLinearAccelString() {
		if(profileName != null) {
			return Float.toString(targetLinearAccel);
		}
		return uninitializedString;
	}

	public float getCollisionDistance() {
		return collisionDistance;
	}

	public String getCollisionDistanceString() {
		if(profileName != null) {
			return Float.toString(collisionDistance);
		}
		return uninitializedString;
	}

	public float getTargetAngularVelocity() {
		return targetAngularVelocity;
	}

	public String getTargetAngularVelocityString() {
		if(profileName != null) {
			return Float.toString(targetAngularVelocity);
		}
		return uninitializedString;
	}

	public float getTargetAngularAccel() {
		return targetAngularAccel;
	}

	public String getTargetAngularAccelString() {
		if(profileName != null) {
			return Float.toString(targetAngularAccel);
		}
		return uninitializedString;
	}

	public boolean getEnableHolonomic() {
		return enableHolonomic;
	}

	public String getEnableHolonomicString() {
		if(profileName != null) {
			return Boolean.toString(enableHolonomic);
		}
		return uninitializedString;
	}

	public String getCheckKeepoutString() {
		if(profileName != null) {
			return Boolean.toString(checkKeepouts);
		}
		return uninitializedString;
	}

	public String getCheckObstaclesString() {
		if(profileName != null) {
			return Boolean.toString(checkObstacles);
		}
		return uninitializedString;
	}

	public boolean getCheckObstacles() {
		return checkObstacles;
	}

	public boolean getCheckKeepouts() {
		return checkKeepouts;
	}

	public String getEnableAutoReturnString() {
		if(profileName != null) {
			return Boolean.toString(enableAutoReturn);
		}
		return uninitializedString;
	}

	public boolean getEnableAutoReturn() {
		return this.enableAutoReturn;
	}

	public int getBootTime() {
		return bootTime;
	}

	public String getArmMobilityStateName() {
		if(armJointState != null) {
			return armJointState.name();
		}
		return uninitializedString;
	}

	public ArmJointState getArmMobilityState() {
		return armJointState;
	}

	public String getSubMobilityStateName() {
		if(subMobilityState != Integer.MAX_VALUE){
			return Integer.toString(subMobilityState);
		}
		return uninitializedString;
	}

	public String getArmGripperStateName() {
		if(armGripperState != null) {
			return armGripperState.name();
		}
		return uninitializedString;
	}

	public ArmGripperState getArmGripperState() {
		return armGripperState;
	}

	public String getProfileName() {
		if(profileName != null) {
			return profileName;
		}
		return uninitializedString;
	}

	public String getFlightMode() {
		if(flightMode != null) {
			return flightMode;
		}
		return uninitializedString;
	}

	public AstrobeeStateGds copyFrom(AstrobeeStateGds original) {
		operatingState = original.getOperatingState();
		planExecutionState = original.getPlanExecutionState();
		guestState = original.getGuestState();
		mobilityState = original.getMobilityState();
		subMobilityState = original.getSubMobilityState();
		armJointState = original.getArmMobilityState();
		armGripperState = original.getArmGripperState();
		proximity = original.getProximity();
		profileName = new String(original.getProfileName());
		flightMode = new String(original.getFlightMode());

		targetLinearVelocity = original.getTargetLinearVelocity();
		targetLinearAccel = original.getTargetLinearAccel();
		targetAngularVelocity = original.getTargetAngularVelocity();
		targetAngularAccel = original.getTargetAngularAccel();
		collisionDistance = original.getCollisionDistance();
		enableHolonomic = original.getEnableHolonomic();
		checkKeepouts = original.getCheckKeepouts();
		checkObstacles = original.getCheckObstacles();
		enableAutoReturn = original.getEnableAutoReturn();
		bootTime = original.getBootTime();
		prettyMobilityState = new String(prettyMobilityState);
		initialized = original.getInitialized();
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((operatingState == null) ? 0 : operatingState.hashCode());
		result = prime * result + ((planExecutionState == null) ? 0 : planExecutionState.hashCode());
		result = prime * result + ((guestState == null) ? 0 : guestState.hashCode());
		result = prime * result + ((mobilityState == null) ? 0 : mobilityState.hashCode());
		result = prime * result + subMobilityState;
		result = prime * result + ((armJointState == null) ? 0 : armJointState.hashCode());
		result = prime * result + ((armGripperState == null) ? 0 : armGripperState.hashCode());
		result = prime * result + Float.floatToIntBits(proximity);
		result = prime * result + ((profileName == null) ? 0 : profileName.hashCode());
		result = prime * result + ((flightMode == null) ? 0 : flightMode.hashCode());
		result = prime * result + Float.floatToIntBits(targetLinearVelocity);
		result = prime * result + Float.floatToIntBits(targetLinearAccel);
		result = prime * result + Float.floatToIntBits(targetAngularVelocity);
		result = prime * result + Float.floatToIntBits(targetAngularAccel);
		result = prime * result + Float.floatToIntBits(collisionDistance);
		result = prime * result + (enableHolonomic ? 0 : 1);
		result = prime * result + (checkKeepouts ? 0 : 1);
		result = prime * result + (checkObstacles ? 0 : 1);
		result = prime * result + (enableAutoReturn ? 0 : 1);
		result = prime * result + bootTime;
		result = prime * result + ((prettyMobilityState == null) ? 0 : prettyMobilityState.hashCode());
		result = prime * result + (initialized ? 0 : 1);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		float EPSILON = 0.0001f;
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (obj instanceof AstrobeeStateGds){
			AstrobeeStateGds other = (AstrobeeStateGds) obj;
			if (operatingState == null) {
				if (other.getOperatingState() != null) {
					return false;
				}
			} else if (!operatingState.equals(other.getOperatingState())) {
				return false;
			}
			if (planExecutionState == null) {
				if (other.getPlanExecutionState() != null) {
					return false;
				}
			} else if (!planExecutionState.equals(other.getPlanExecutionState())) {
				return false;
			}
			if (guestState == null) {
				if (other.getGuestState() != null) {
					return false;
				}
			} else if (!guestState.equals(other.getGuestState())) {
				return false;
			}
			if (mobilityState == null) {
				if (other.getMobilityState() != null) {
					return false;
				}
			} else if (!mobilityState.equals(other.getMobilityState())) {
				return false;
			}
			if(subMobilityState != other.getSubMobilityState()) {
				return false;
			}
			if (!getArmMobilityStateName().equals(other.getArmMobilityStateName())) {
				return false;
			}
			if (!getArmGripperStateName().equals(other.getArmGripperStateName())) {
				return false;
			}
			if (Math.abs(proximity-other.getProximity()) > EPSILON) {
				return false;
			}
			if (profileName == null) {
				if (other.getProfileName() != null) {
					return false;
				}
			} else if (!profileName.equals(other.getProfileName())) {
				return false;
			}
			if (flightMode == null) {
				if (other.getFlightMode() != null) {
					return false;
				}
			} else if (!flightMode.equals(other.getFlightMode())) {
				return false;
			}
			if (Math.abs(targetLinearVelocity-other.getTargetLinearVelocity()) > EPSILON) {
				return false;
			}
			if (Math.abs(targetLinearAccel-other.getTargetLinearAccel()) > EPSILON) {
				return false;
			}
			if (Math.abs(targetAngularVelocity-other.getTargetAngularVelocity()) > EPSILON) {
				return false;
			}
			if (Math.abs(targetAngularAccel-other.getTargetAngularAccel()) > EPSILON) {
				return false;
			}
			if (Math.abs(collisionDistance-other.getCollisionDistance()) > EPSILON) {
				return false;
			}
			if(enableHolonomic != other.getEnableHolonomic()) {
				return false;
			}
			if(checkKeepouts != other.getCheckKeepouts()) {
				return false;
			}
			if(checkObstacles != other.getCheckObstacles()) {
				return false;
			}
			if(enableAutoReturn != other.getEnableAutoReturn()) {
				return false;
			}
			if(bootTime != other.getBootTime()) {
				return false;
			}
			if(prettyMobilityState != other.getPrettyMobilityStateName()) {
				return false;
			}
			if(initialized != other.getInitialized()) {
				return false;
			}
			return true;
		}
		return false;
	}
}
