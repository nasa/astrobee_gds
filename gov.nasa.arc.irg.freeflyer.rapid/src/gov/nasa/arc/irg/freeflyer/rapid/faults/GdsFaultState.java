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
package gov.nasa.arc.irg.freeflyer.rapid.faults;

import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rapid.ext.astrobee.Fault;
import rapid.ext.astrobee.FaultConfig;
import rapid.ext.astrobee.FaultInfo;
import rapid.ext.astrobee.FaultState;

public class GdsFaultState {
	private Map<Integer,GdsFault> allFaults = new HashMap<Integer,GdsFault>();

	private Set<Integer> triggered = new HashSet<Integer>();

	private List<String> subsystems = new ArrayList<String>();
	private List<String> nodes = new ArrayList<String>();
	
	private String[] faultCategories = new String[]{"Triggered", "Not Triggered"};

	private FaultState currentFaultState;

	public synchronized boolean isAnySubsystemDisabled() {
		for(Integer t : triggered) {
			GdsFault fault = allFaults.get(t);
			if(fault != null && !fault.isWarning()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isThisSubsystemDisabled(String subsystemName) {
		Set<String> disabledSubsystems = getSubsystemsWithNonWarningFaults();
		
		if(disabledSubsystems.contains(subsystemName)) {
			return true;
		}
		return false;
	}
	
	public String getDisabledSubsystems() {
		Set<String> disabledSubsystems = getSubsystemsWithNonWarningFaults();
		
		if(disabledSubsystems.isEmpty()) {
			return WorkbenchConstants.UNINITIALIZED_STRING;
		}
		
		return turnSetIntoList(disabledSubsystems);
	}
	
	protected Set<String> getSubsystemsWithNonWarningFaults() {
		Set<String> disabledSubsystems = new HashSet<String>();
		for(Integer t : triggered) {
			GdsFault fault = allFaults.get(t);
			
			if(!(fault == null) && !fault.isWarning()) {
				disabledSubsystems.add(fault.getSubsystem());
			}
		}
		return disabledSubsystems;
	}
	
	protected String turnSetIntoList(Set<String> input) { 
		StringBuilder sb = new StringBuilder();
		for(String s : input) {
			if(sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(s);
		}
		return sb.toString();
	}
	
	public GdsFaultState copyFrom(GdsFaultState original) {
		allFaults = new HashMap<Integer,GdsFault>();

		for(Integer i : original.allFaults.keySet()) {
			allFaults.put(new Integer(i), new GdsFault(original.allFaults.get(i)));
		}

		triggered = new HashSet<Integer>();
		for(Integer t : original.triggered) {
			triggered.add(new Integer(t));
		}

		subsystems = new ArrayList<String>();
		for(String s : original.subsystems) {
			subsystems.add(new String(s));
		}

		nodes = new ArrayList<String>();
		for(String n : original.nodes) {
			nodes.add(new String(n));
		}

		if(original.currentFaultState != null) {
			currentFaultState = new FaultState(original.currentFaultState);
		} else {
			currentFaultState = null;
		}
		return this;
	}

	public synchronized void ingestState(FaultState faultState) {
		currentFaultState = faultState;

		triggered.clear();

		addTriggered();
	}

	public String[] getFaultCategories() {
		return faultCategories;
	}

	public List<GdsFault> getEnabledFaults() {
		List<GdsFault> ret = new ArrayList<GdsFault>();

		for(Integer f : allFaults.keySet()) {
			if(!triggered.contains(f)) {
				ret.add(allFaults.get(f));
			}
		}
		return ret;
	}

	public List<GdsFault> getTriggeredFaults() {
		List<GdsFault> ret = new ArrayList<GdsFault>();

		for(Integer i : triggered) {
			ret.add(allFaults.get(i));
		}
		return ret;
	}

	protected void addTriggered() {
		for(int i=0; i<currentFaultState.faults.userData.size(); i++) {
			Fault theirTriggered = (Fault) currentFaultState.faults.userData.get(i);
			if(!triggered.contains(theirTriggered.code)) {
				GdsFault fault = allFaults.get(theirTriggered.code);
				if(fault != null) {
					fault.setTo(theirTriggered);
				}
				triggered.add(theirTriggered.code);
			}
		}
	}

	protected boolean isTriggered(long faultId) {
		for(int i=0; i<currentFaultState.faults.userData.size(); i++) {
			Fault f = (Fault) currentFaultState.faults.userData.get(i);
			if(faultId == f.code) {
				return true;
			}
		}
		return false;
	}

	public void ingestConfig(FaultConfig faultConfig) {
		ingestSubsystemAndNodeNames(faultConfig);

		for(int i=0; i<faultConfig.faults.userData.size(); i++) {
			FaultInfo faultInfo = (FaultInfo)faultConfig.faults.userData.get(i);

			GdsFault gdsFault = new GdsFault(faultInfo);
			gdsFault.setSubsystemName(subsystems);
			gdsFault.setNodeName(nodes);
			allFaults.put((int) gdsFault.getFaultId(), gdsFault);
		}
	}

	protected void ingestSubsystemAndNodeNames(FaultConfig faultConfig) {
		if(subsystems == null) {
			subsystems = new ArrayList<String>();
		} else {
			subsystems.clear();
		}

		for(int i=0; i<faultConfig.subsystems.userData.size(); i++) {
			this.subsystems.add((String) faultConfig.subsystems.userData.get(i));
		}

		if(nodes == null) {
			nodes = new ArrayList<String>();
		} else {
			nodes.clear();
		}

		for(int i=0; i<faultConfig.nodes.userData.size(); i++) {
			this.nodes.add((String) faultConfig.nodes.userData.get(i));
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (obj instanceof GdsFaultState) {
			GdsFaultState other = (GdsFaultState) obj;

			if (allFaults == null) {
				if (other.allFaults != null) {
					return false;
				}
			} else if (!allFaults.equals(other.allFaults)) {
				return false;
			}	
			if (triggered == null) {
				if (other.triggered != null) {
					return false;
				}
			} else if (!triggered.equals(other.triggered)) {
				return false;
			}	
			if (subsystems == null) {
				if (other.subsystems != null) {
					return false;
				}
			} else if (!subsystems.equals(other.subsystems)) {
				return false;
			}	
			if (nodes == null) {
				if (other.nodes != null) {
					return false;
				}
			} else if (!nodes.equals(other.nodes)) {
				return false;
			}	
			if (currentFaultState == null) {
				if (other.currentFaultState != null) {
					return false;
				}
			} else if (!currentFaultState.equals(other.currentFaultState)) {
				return false;
			}	
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((allFaults == null) ? 0 : allFaults.hashCode());
		result = prime * result + ((triggered == null) ? 0 : triggered.hashCode());
		result = prime * result + ((subsystems == null) ? 0 : subsystems.hashCode());
		result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
		result = prime * result + ((currentFaultState == null) ? 0 : currentFaultState.hashCode());
		return result;
	}
}
