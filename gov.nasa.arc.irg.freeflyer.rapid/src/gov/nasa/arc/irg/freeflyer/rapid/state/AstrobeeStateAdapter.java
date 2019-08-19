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

import gov.nasa.arc.irg.plan.ui.io.ConfigFileWrangler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

/** 
 * This class makes the AggregateAstrobeeState into a format that the TelemetryPanel can 
 * put into a table easily
 * 
 * @author DW
 */
public class AstrobeeStateAdapter {

	private AggregateAstrobeeState aggregateState;
	private List<StateTableRow> standardHealthAndStatusData;
	private List<StateTableRow> detailedHealthAndStatusData;
	private List<StateTableRow> operatingLimitsData;

	private final String DISTANCE_UNITS = "m";
	private final String VELOCITY_UNITS = "m/s";
	private final String ANG_VEL_UNITS = "rad/s";
	private final String ACCEL_UNITS = "m/s/s";
	private final String ANG_ACCEL_UNITS = "rad/s/s";

	private final String accessControlName = "Control";
	private final String operatingStateName = "Operating State";
	private final String planExecutionStateName = "Plan Execution State";
	private final String mobilityStateName = "Mobility State";
	private final String rawMobilityStateName = "Raw Mobility State";
	private final String subMobilityStateName = "Sub Mobility State";
	private final String operatingLimitsName = "Operating Limits";
	private final String planName = "Plan";
	private final String planStatusName = "Plan Status";
	private final String armMobilityName = "Arm Mobility";
	private final String armGripperName = "Arm Gripper";
	public final String disabledSubsystemsName = "Disabled Subsystems";
	private final String processorName = "Processor";
	private final String mobilityName = "Mobility";
	private final String localizationName = "Localization";
	private final String recordingName = "Recording Data to Disk";
	private final String recordingNameName = "Data to Disk Filename";
	private final String recordingSummaryName = "Data to Disk";
	private final String DISABLED_STRING = "Disabled";
	
	private final int priorityOffset = 33; // Astrobee can report up to 32 faults
	private final int priorityIgnoreOffset = 101;

	private final String[] allData = { accessControlName,
			operatingStateName, planExecutionStateName, rawMobilityStateName, subMobilityStateName,
			operatingLimitsName, planName, planStatusName, armMobilityName, armGripperName,
			recordingSummaryName };
	
	private final String[] faultData = { processorName, mobilityName, localizationName };

	private String[] standardData = { operatingStateName, mobilityStateName, operatingLimitsName,
			planName, planStatusName, recordingSummaryName };

	public AstrobeeStateAdapter(AggregateAstrobeeState state) {
		aggregateState = state;
		standardData = prepareListOfStandardData(ConfigFileWrangler.getInstance().getHealthAndStatusConfigPath());

		setupStandardHealthAndStatusData();
		setupDetailedHealthAndStatusData();
		setupOperatingLimitsData();
	}

	public String[] prepareListOfStandardData(String filename) {
		try {
			Vector<String> namesVector = new Vector<String>();
			File file = new File(filename);
			Scanner reader = new Scanner(file);
			while(reader.hasNextLine()) {
				namesVector.add(reader.nextLine());
			}
			reader.close();
			return namesVector.toArray(new String[1]);

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	private void setupStandardHealthAndStatusData() {
		standardHealthAndStatusData = new ArrayList<StateTableRow>();
		setupSomeHealthAndStatusData(standardHealthAndStatusData);
	}

	private void setupDetailedHealthAndStatusData() {
		detailedHealthAndStatusData = new ArrayList<StateTableRow>();
		setupAllHealthAndStatusData(detailedHealthAndStatusData);
	}

	private void setupSomeHealthAndStatusData(List<StateTableRow> columnData) {
		for(int i=0; i<standardData.length; i++) {
			addTopicNameToColumnData(standardData[i], columnData, priorityOffset + i);
		}
		addTheFaults(columnData);
	}

	private void setupAllHealthAndStatusData(List<StateTableRow> columnData) {
		for(int i=0; i<allData.length; i++) {
			addTopicNameToColumnData(allData[i], columnData, priorityOffset + i);
		}
		addTheFaults(columnData);
	}

	private void addTheFaults(List<StateTableRow> columnData) {
		for(int i=0; i<faultData.length; i++) {
			addTopicNameToColumnData(faultData[i], columnData, priorityIgnoreOffset);
		}
	}
	
	private void addTopicNameToColumnData(String topicName, List<StateTableRow> columnData, int priority) {
		if(topicName.equals(accessControlName)) {
			columnData.add(new StateTableRow(accessControlName, aggregateState, priority, true) {
				@Override
				public String getValue() {
					return aggregateState.getAccessControl();
				}
			});
			return;
		}
		if(topicName.equals(operatingStateName)) {
			columnData.add(new StateTableRow(operatingStateName, aggregateState, priority) {
				@Override
				public String getValue() {
					return aggregateState.getAstrobeeState().getOperatingStateName();
				}
			});
			return;
		}
		if(topicName.equals(recordingSummaryName)) {
			columnData.add(new StateTableRow(recordingSummaryName, aggregateState, priority, true) {
				@Override
				public String getValue() {
					if(aggregateState.isRecordingData()) {
						return "Recording " + aggregateState.getRecordingName();
					}
					else {
						return "Not Recording " + aggregateState.getRecordingName();
					}
				}
			});
			return;
		}
		if(topicName.equals(recordingName)) {
			columnData.add(new StateTableRow(recordingName, aggregateState, priority) {
				@Override
				public String getValue() {
					return Boolean.toString(aggregateState.isRecordingData());
				}
			});
			return;
		}
		if(topicName.equals(recordingNameName)) {
			columnData.add(new StateTableRow(recordingNameName, aggregateState, priority) {
				@Override
				public String getValue() {
					return aggregateState.getRecordingName();
				}
			});
			return;
		}
		if(topicName.equals(planExecutionStateName)) {
			columnData.add(new StateTableRow(planExecutionStateName, aggregateState, priority) {
				@Override
				public String getValue() {
					return aggregateState.getAstrobeeState().getPlanExecutionStateName();
				}
			});
			return;
		}
		if(topicName.equals(mobilityStateName)) {
			columnData.add(new StateTableRow(mobilityStateName, aggregateState, priority) {
				@Override
				public String getValue() {
					return aggregateState.getAstrobeeState().getPrettyMobilityStateName();
				}
			});
			return;
		}
		if(topicName.equals(rawMobilityStateName)) {
			columnData.add(new StateTableRow(rawMobilityStateName, aggregateState, priority) {
				@Override
				public String getValue() {
					return aggregateState.getAstrobeeState().getMobilityStateName();
				}
			});
			return;
		}
		if(topicName.equals(subMobilityStateName)) {
			columnData.add(new StateTableRow(subMobilityStateName, aggregateState, priority){

				@Override
				public String getValue() {
					return aggregateState.getAstrobeeState().getSubMobilityStateName();
				}

			});
			return;
		}
		if(topicName.equals(operatingLimitsName)) {
			columnData.add(new StateTableRow(operatingLimitsName, aggregateState, priority, true) {
				@Override
				public String getValue() {
					return aggregateState.getAstrobeeState().getProfileName();
				}
			});
			return;
		}
		if(topicName.equals(planName)) {
			columnData.add(new StateTableRow("Plan Name", aggregateState, priority, true) {
				@Override
				public String getValue() {
					return aggregateState.getCurrentPlanName();
				}
			});
			return;
		}
		if(topicName.equals(planStatusName)) {
			columnData.add(new StateTableRow("Plan State", aggregateState, priority) {
				@Override
				public String getValue() {
					return aggregateState.getAstrobeeState().getPlanExecutionStateName();
				}
			});
			return;
		}
		
		if(topicName.equals(armMobilityName)) {
			columnData.add(new StateTableRow(armMobilityName, aggregateState, priority) {
				@Override
				public String getValue() {
					return aggregateState.getAstrobeeState().getArmMobilityStateName();
				}
			});
			return;
		}
		if(topicName.equals(armGripperName)) {
			columnData.add(new StateTableRow("Arm Gripper", aggregateState, priority) {
				@Override
				public String getValue() {
					return aggregateState.getAstrobeeState().getArmGripperStateName();
				}
			});
			return;
		}
		
		if(topicName.equals(disabledSubsystemsName)) {
			columnData.add(new StateTableRow(disabledSubsystemsName, aggregateState, priority, true) {
				
				@Override
				public boolean showBecauseHighPriority() {
					if(aggregateState.isAnySubsystemDisabled()) {
						priority = 1;
						return true;
					} else {
						priority = priorityIgnoreOffset;
						return false;
						
					}
				}
				
				@Override
				public String getValue() {
					return aggregateState.getDisabledSubsystems();
				}
			});
			return;
		}
		
		if(topicName.equals(processorName)) {
			columnData.add(new StateTableRow(processorName, aggregateState, priority, true) {
				
				@Override
				public boolean showBecauseHighPriority() {
					if(aggregateState.isThisSubsystemDisabled(processorName)) {
						priority = 1;
						return true;
					} else {
						priority = priorityIgnoreOffset;
						return false;
						
					}
				}
				
				@Override
				public String getValue() {
					return DISABLED_STRING;
				}
			});
			return;
		}
		if(topicName.equals(mobilityName)) {
			columnData.add(new StateTableRow(mobilityName, aggregateState, priority, true) {
				
				@Override
				public boolean showBecauseHighPriority() {
					if(aggregateState.isThisSubsystemDisabled(mobilityName)) {
						priority = 2;
						return true;
					} else {
						priority = priorityIgnoreOffset;
						return false;
						
					}
				}
				
				@Override
				public String getValue() {
					return DISABLED_STRING;
				}
			});
			return;
		}
		if(topicName.equals(localizationName)) {
			columnData.add(new StateTableRow(localizationName, aggregateState, priority, true) {
				
				@Override
				public boolean showBecauseHighPriority() {
					if(aggregateState.isThisSubsystemDisabled(localizationName)) {
						priority = 3;
						return true;
					} else {
						priority = priorityIgnoreOffset;
						return false;
						
					}
				}
				
				@Override
				public String getValue() {
					return DISABLED_STRING;
				}
			});
			return;
		}
	}

	private void setupOperatingLimitsData() {
		operatingLimitsData	 = new ArrayList<StateTableRow>();
		operatingLimitsData.add(new StateTableRow("Profile Name", aggregateState, true) {
			@Override
			public String getValue() {
				return aggregateState.getAstrobeeState().getProfileName();
			}
		});
		operatingLimitsData.add(new StateTableRow("Flight Mode", aggregateState, true) {
			@Override
			public String getValue() {
				return aggregateState.getAstrobeeState().getFlightMode();
			}
		});
		operatingLimitsData.add(new StateTableRow("Target Linear Velocity", aggregateState) {
			@Override
			public String getValue() {
				return aggregateState.getAstrobeeState().getTargetLinearVelocityString();
			}

			@Override
			public String getUnits() {
				return VELOCITY_UNITS;
			}
		});
		operatingLimitsData.add(new StateTableRow("Target Linear Accel", aggregateState) {
			@Override
			public String getValue() {
				return aggregateState.getAstrobeeState().getTargetLinearAccelString();
			}
			@Override
			public String getUnits() {
				return ACCEL_UNITS;
			}
		});
		operatingLimitsData.add(new StateTableRow("Target Angular Velocity", aggregateState) {
			@Override
			public String getValue() {
				return aggregateState.getAstrobeeState().getTargetAngularVelocityString();
			}
			@Override
			public String getUnits() {
				return ANG_VEL_UNITS;
			}
		});
		operatingLimitsData.add(new StateTableRow("Target Angular Accel", aggregateState) {
			@Override
			public String getValue() {
				return aggregateState.getAstrobeeState().getTargetAngularAccelString();
			}
			@Override
			public String getUnits() {
				return ANG_ACCEL_UNITS;
			}
		});
		operatingLimitsData.add(new StateTableRow("Collision Distance", aggregateState) {
			@Override
			public String getValue() {
				return aggregateState.getAstrobeeState().getCollisionDistanceString();
			}
			@Override
			public String getUnits() {
				return DISTANCE_UNITS;
			}
		});
		operatingLimitsData.add(new StateTableRow("Check Obstacles", aggregateState) {
			@Override
			public String getValue() {
				return aggregateState.getAstrobeeState().getCheckObstaclesString();
			}
		});
		operatingLimitsData.add(new StateTableRow("Check Keepouts", aggregateState) {
			@Override
			public String getValue() {
				return aggregateState.getAstrobeeState().getCheckKeepoutString();
			}
		});
		operatingLimitsData.add(new StateTableRow("Enable Auto Return", aggregateState) {
			@Override
			public String getValue() {
				return aggregateState.getAstrobeeState().getEnableAutoReturnString();
			}
		});
		operatingLimitsData.add(new StateTableRow("Enable Holonomic", aggregateState) {
			@Override
			public String getValue() {
				return aggregateState.getAstrobeeState().getEnableHolonomicString();
			}
		});
	}

	public List<StateTableRow> getStandardHealthAndStatusData() {
		return standardHealthAndStatusData;
	}

	public List<StateTableRow> getDetailedHealthAndStatusData() {
		return detailedHealthAndStatusData;
	}

	public List<StateTableRow> getOperatingLimitsData() {
		return operatingLimitsData;
	}

	public abstract class StateTableRow implements Comparable<StateTableRow> {
		private String label;
		protected AggregateAstrobeeState state;
		private final boolean verbatim; // set to true to display value not in title case
		protected int priority;

		public StateTableRow(String label, AggregateAstrobeeState state, int priority) {
			this(label, state, priority, false);
		}

		public StateTableRow(String label, AggregateAstrobeeState state) {
			this(label, state, false);
		}

		public StateTableRow(String label, AggregateAstrobeeState state, int priority, boolean verbatim) {
			this.label = label;
			this.state = state;
			this.verbatim = verbatim;
			this.priority = priority;
		}

		public StateTableRow(String label, AggregateAstrobeeState state, boolean verbatim) {
			this.label = label;
			this.state = state;
			this.verbatim = verbatim;
			this.priority = 1;
		}

		public String getLabel() {
			return label;
		}

		public abstract String getValue();

		public String getUnits() {
			return "";
		}

		public boolean getVerbatim() {
			return verbatim;
		}

		@Override
		public String toString() {
			return label + ": " + getValue() + getUnits();
		}

		public int getPriority() {
			return priority;
		}

		public void setPriority(int priority) {
			this.priority = priority;
		}
		
		public boolean showBecauseHighPriority() {
			if(getPriority() < priorityIgnoreOffset) {
				return true;
			}
			return false;
		}
		
		public boolean colorOrangeBecauseFault() {
			if(getPriority() < priorityOffset) {
				return true;
			}
			return false;
		}

		public int compareTo(StateTableRow other) {
			if(other.getPriority() < priority) {
				return -1;
			}
			if(other.getPriority() > priority) {
				return 1;
			}
			return 0;
		}
	}
}