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

import gov.nasa.arc.irg.freeflyer.rapid.CompressedFilePublisher;
import gov.nasa.arc.irg.freeflyer.rapid.faults.GdsFaultState;
import gov.nasa.arc.irg.plan.freeflyer.plan.PointCommand;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPlan;
import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.rapid.v2.e4.agent.Agent;
import rapid.AccessControlState;
import rapid.PositionConfig;
import rapid.PositionSample;
import rapid.ext.astrobee.AgentState;
import rapid.ext.astrobee.CompressedFile;
import rapid.ext.astrobee.DataToDiskState;
import rapid.ext.astrobee.DataTopicsList;
import rapid.ext.astrobee.FaultConfig;
import rapid.ext.astrobee.FaultState;
import rapid.ext.astrobee.InertialProperties;
import rapid.ext.astrobee.PlanStatus;
import rapid.ext.astrobee.SaveSetting;
import rapid.ext.astrobee.TelemetryConfig;
import rapid.ext.astrobee.TelemetryState;

/**
 * Container for classes that store telemetry that comes from Astrobee
 * 
 * Managed by AstrobeeStateManager
 * 
 * @author DW
 */
public class AggregateAstrobeeState {
	private static AggregateAstrobeeState INSTANCE;
	protected Agent agent;
	protected AstrobeeStateGds astrobeeState;

	protected String currentPlanName;
	protected PointCommand currentPointCommand;
	
	protected GdsFaultState gdsFaultState;
	protected InertialPropertiesGds inertialProperties;
	protected PositionGds positionGds;
	protected TelemetryStateGds telemetryState;

	// id of control station that has control of the robot
	protected String uninitialized = WorkbenchConstants.UNINITIALIZED_STRING;
	protected String accessControl = uninitialized;
	protected boolean receivedDataAboutSelectedAstrobee = false; // basically, is this valid or not
	protected CompressedFile zonesCompressedFile;

	/** make a deep copy of all members */
	public AggregateAstrobeeState copyFrom(AggregateAstrobeeState original) {

		if(original != null) {
			// XXX this is by reference, not sure how to do it by value when I can't make another Agent object
			// XXX agent might be null 
			agent = original.agent;
			accessControl = new String(original.getAccessControl());
			
			if(original.getAstrobeeState() != null) {
				astrobeeState.copyFrom(original.getAstrobeeState());
			} else {
				astrobeeState = null;
			}
			
			currentPlanName = new String(original.getCurrentPlanName());
			
			currentPointCommand = new PointCommand(original.getCurrentPlanCommand());

			if(original.gdsFaultState != null) {
				gdsFaultState.copyFrom(original.gdsFaultState);
			} else {
				gdsFaultState = null;
			}

			if(original.getPositionGds() != null) {
				positionGds.copyFrom(original.getPositionGds());
			} else {
				positionGds = null;
			}

			if(original.getTelemetryState() != null) {
				telemetryState.copyFrom(original.getTelemetryState());
			} else {
				telemetryState = null;
			}

		}
		return this;
	}

	public static AggregateAstrobeeState getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new AggregateAstrobeeState();
		}
		return INSTANCE;
	}

	public AggregateAstrobeeState() {
		astrobeeState = new AstrobeeStateGds();
		currentPlanName = uninitialized;
		currentPointCommand = new PointCommand(0,-1);
		gdsFaultState = new GdsFaultState();
		positionGds = new PositionGds();
		inertialProperties = new InertialPropertiesGds();
		telemetryState = new TelemetryStateGds();
	}

	public void ingestAccessControlState(AccessControlState acs) {
		accessControl = acs.controller;
	}

	public String getAccessControl() {
		return accessControl;
	}

	public void setAgent(Agent a) {
		agent = a;
	}

	public Agent getAgent() {
		return agent;
	}

	public void ingestAgentState(AgentState agentState) {
		astrobeeState.ingestAgentState(agentState);
	}

	public AstrobeeStateGds getAstrobeeState() {
		return astrobeeState;
	}
	
	public void ingestArmState(rapid.ext.astrobee.ArmState armState) {
		astrobeeState.ingestArmState(armState);
	}

	public ModuleBayPlan ingestCurrentPlanCompressedFile(CompressedFile compressedFile) {
		ModuleBayPlan plan  = CompressedFilePublisher.uncompressCurrentPlanCompressedFile(compressedFile);
		currentPlanName = plan.getName();
		return plan;
	}
	
	public void ingestCurrentZonesCompressedFile(CompressedFile compressedFile) {
		zonesCompressedFile = compressedFile;
	}

	public void ingestDataToDiskState(DataToDiskState dataState) {
		RosTopicsList.clear(); // forget the past. we are not being stateful
		RosTopicsList.setRecordingName(dataState.name);
		RosTopicsList.setRecordingData(dataState.recording);
		for(int i=0; i<dataState.topicSaveSettings.userData.size(); i++) {
			SaveSetting setting = (SaveSetting)dataState.topicSaveSettings.userData.get(i);
			RosTopicsList.setThisTopicsSettings(setting);
		}
	}

	public void ingestDataTopicsList(DataTopicsList dataConfig) {
		RosTopicsList.clear(); // Topics list should not be stateful either.
		for(int i=0; i<dataConfig.topics.userData.size(); i++) {
			RosTopicsList.addTopicName((String)dataConfig.topics.userData.get(i));
		}
	}

	public void ingestFaultConfig(FaultConfig faultConfig) {
		gdsFaultState.ingestConfig(faultConfig);
	}

	public void ingestFaultState(FaultState faultState) {
		gdsFaultState.ingestState(faultState);
	}

	public boolean isThisSubsystemDisabled(String subsystemName) {
		return gdsFaultState.isThisSubsystemDisabled(subsystemName);
	}

	public boolean isRecordingData() {
		return RosTopicsList.isRecordingData();
	}
	
	public boolean isAnySubsystemDisabled() {
		return gdsFaultState.isAnySubsystemDisabled();
	}
	
	public String getDisabledSubsystems() {
		return gdsFaultState.getDisabledSubsystems();
	}
	
	public void ingestInertialProperties(InertialProperties ip) {
		inertialProperties.ingestInertialProperties(ip);
	}

	public InertialPropertiesGds getInertialProperties() {
		return inertialProperties;
	}

	public String getInertiaConfigName() {
		return inertialProperties.getName();
	}

	public float getMass() {
		return inertialProperties.getMass();
	}

	public float[] getInertiaMatrix() {
		return inertialProperties.getInertiaMatrix();
	}

	public GdsFaultState getGdsFaultState() {
		return gdsFaultState;
	}
	
	public String getRecordingName() {
		return RosTopicsList.getRecordingName();
	}

	public void ingestPlanStatus(PlanStatus ps) {
		if(ps.planName.isEmpty()) {
			currentPlanName = WorkbenchConstants.NO_PLAN_LOADED;
		} else {
			currentPlanName = ps.planName;
		}
		currentPointCommand.set(ps.currentPoint, ps.currentCommand);
	}

	public PointCommand getCurrentPlanCommand() {
		return currentPointCommand;
	}

	public String getCurrentPlanName() {
		return currentPlanName;
	}

	public void ingestPositionConfig(PositionConfig pc) {
		positionGds.ingestPositionConfig(pc);
	}

	public void ingestPositionSample(PositionSample sample) {
		positionGds.ingestPositionSample(sample);
	}

	public PositionGds getPositionGds() {
		return positionGds;
	}

	public void ingestTelemetryConfig(TelemetryConfig tc) {
		telemetryState.ingestTelemetryConfig(tc);
	}

	public void ingestTelemetryState(TelemetryState ts) {
		telemetryState.ingestTelemetryState(ts);
	}

	public TelemetryStateGds getTelemetryState() {
		return telemetryState;
	}

	public void clearAll() {
		accessControl = uninitialized;
		astrobeeState = new AstrobeeStateGds();
		currentPlanName = uninitialized;
		currentPointCommand = new PointCommand(0,-1);
		gdsFaultState = new GdsFaultState();
		positionGds = new PositionGds();
		telemetryState.clear();
		receivedDataAboutSelectedAstrobee = false;
		RosTopicsList.clear();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accessControl == null) ? 0 : accessControl.hashCode());
		result = prime * result + ((agent == null) ? 0 : agent.hashCode());
		result = prime * result + ((astrobeeState == null) ? 0 : astrobeeState.hashCode());
		result = prime * result + ((currentPlanName == null) ? 0 : currentPlanName.hashCode());
		result = prime * result + ((currentPointCommand == null) ? 0 : currentPointCommand.hashCode());

		result = prime * result + ((gdsFaultState == null) ? 0 : gdsFaultState.hashCode());
		
		result = prime * result + ((inertialProperties == null) ? 0 : inertialProperties.hashCode());
		result = prime * result + ((positionGds == null) ? 0 : positionGds.hashCode());
		result = prime * result + ((telemetryState == null) ? 0 : telemetryState.hashCode());
		result = prime * result + (receivedDataAboutSelectedAstrobee ? 0 : 1);
		
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (obj instanceof AggregateAstrobeeState) {
			AggregateAstrobeeState other = (AggregateAstrobeeState) obj;

			if (accessControl == null) {
				if (other.getAccessControl() != null) {
					return false;
				}
			} else if (!accessControl.equals(other.getAccessControl())) {
				return false;
			}	
			if (agent == null) {
				if (other.getAgent() != null) {
					return false;
				}
			} else if (!agent.equals(other.getAgent())) {
				return false;
			}
			if (astrobeeState == null) {
				if (other.getAstrobeeState() != null) {
					return false;
				}
			} else if (!astrobeeState.equals(other.getAstrobeeState())) {
				return false;
			}
			if (currentPlanName == null) {
				if (other.getCurrentPlanName() != null) {
					return false;
				}
			} else if (!currentPlanName.equals(other.getCurrentPlanName())) {
				return false;
			}
			if (currentPointCommand == null) {
				if (other.getCurrentPlanCommand() != null) {
					return false;
				}
			} else if (!currentPointCommand.equals(other.getCurrentPlanCommand())) {
				return false;
			}
			if (gdsFaultState == null) {
				if (other.gdsFaultState != null) {
					return false;
				}
			} else if (!gdsFaultState.equals(other.gdsFaultState)) {
				return false;
			}	
			if (inertialProperties == null) {
				if (other.getInertialProperties() != null) {
					return false;
				}
			} else if (!inertialProperties.equals(other.getInertialProperties())) {
				return false;
			}	
			if (positionGds == null) {
				if (other.getPositionGds() != null) {
					return false;
				}
			} else if (!positionGds.equals(other.getPositionGds())) {
				return false;
			}
			if (telemetryState == null) {
				if (other.getTelemetryState() != null) {
					return false;
				}
			} else if (!telemetryState.equals(other.getTelemetryState())) {
				return false;
			}
			if(receivedDataAboutSelectedAstrobee != other.isValid()) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isValid() {
		return receivedDataAboutSelectedAstrobee;
	}
	
	/** AstrobeeStateManager must set this to true */
	public void setValid(boolean valid) {
		receivedDataAboutSelectedAstrobee = valid;
	}
}
