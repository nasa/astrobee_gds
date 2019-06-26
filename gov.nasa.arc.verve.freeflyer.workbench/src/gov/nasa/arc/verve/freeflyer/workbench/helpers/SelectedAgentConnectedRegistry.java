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
package gov.nasa.arc.verve.freeflyer.workbench.helpers;

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.rapid.v2.e4.agent.ActiveAgentSet;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.agent.IActiveAgentSetListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;

/** Keeps track of whether the agent selected in the dropdown is connected via DDS */
public class SelectedAgentConnectedRegistry implements IActiveAgentSetListener {
	private static List<SelectedAgentConnectedListener> s_registry = new ArrayList<SelectedAgentConnectedListener>();
	private static Agent selectedAgent;
	private static boolean selectedIsConnected = false;
	
	{
		ActiveAgentSet.INSTANCE.addListener(this);
	}
	
	public static void addListener(SelectedAgentConnectedListener sacl) {
		s_registry.add(sacl);
		if(selectedIsConnected) {
			sacl.onSelectedAgentConnected();
		} else {
			sacl.onSelectedAgentDisconnected();
		}
	}
	
	public static void removeListener(SelectedAgentConnectedListener sacl) {
		s_registry.remove(sacl);
	}
	
	@Inject @Optional
	public void setAgent(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent selected) {
		if(selected == null) {
			return;
		}
		selectedAgent = selected;
		checkIfChanged();
	}
	
	private void checkIfChanged() {
		boolean oldSelectedIsConnected = selectedIsConnected;
		boolean changed = false;
		if(ActiveAgentSet.contains(selectedAgent)) {
			if(!oldSelectedIsConnected) {
				changed = true;
			}
			selectedIsConnected = true;
		} else {
			if(oldSelectedIsConnected) {
				changed = true;
			}
			selectedIsConnected = false;
		}
		if(changed) {
			notifyListeners();
		}
	}
	
	@Override
	public void activeAgentSetChanged() {
		checkIfChanged();
	}
	
	private void notifyListeners() {
		if(selectedIsConnected) {
			for(SelectedAgentConnectedListener sacl : s_registry) {
				sacl.onSelectedAgentConnected();
			}
		} else {
			for(SelectedAgentConnectedListener sacl : s_registry) {
				sacl.onSelectedAgentDisconnected();
			}
		}
	}

	@Override
	public void activeAgentAdded(Agent agent, String participantId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activeAgentRemoved(Agent agent) {
		// TODO Auto-generated method stub
		
	}
}
