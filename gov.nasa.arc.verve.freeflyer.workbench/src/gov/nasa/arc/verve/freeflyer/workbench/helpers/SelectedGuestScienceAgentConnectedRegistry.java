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

/** Keeps track of whether the ABs selected in the GS are connected via DDS */
public class SelectedGuestScienceAgentConnectedRegistry implements IActiveAgentSetListener {
	private static List<SelectedAgentConnectedListener> s_registry = new ArrayList<SelectedAgentConnectedListener>();
	private static final int NUM_ROBOTS = 3;
	private static Agent[] selected = new Agent[NUM_ROBOTS];
	private static boolean[] connected = new boolean[NUM_ROBOTS];
	private static boolean selectedAndAllConnected = false; // at least one selected, all selected are connected

	{
		ActiveAgentSet.INSTANCE.addListener(this);
		for(int i=0; i<NUM_ROBOTS; i++) {
			selected[i] = null;
			connected[i] = false;
		}
	}

	public static void addListener(SelectedAgentConnectedListener sacl) {
		s_registry.add(sacl);
		if(selectedAndAllConnected) {
			sacl.onSelectedAgentConnected();
		} else {
			sacl.onSelectedAgentDisconnected();
		}
	}

	public static void removeListener(SelectedAgentConnectedListener sacl) {
		s_registry.remove(sacl);
	}

	@Inject @Optional
	public void acceptGuestScienceAgent1(@Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_1) Agent a) {
		selected[0] = a;
		checkIfChanged();
	}

	@Inject @Optional
	public void acceptGuestScienceAgent2(@Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_2) Agent a) {
		selected[1] = a;
		checkIfChanged();
	}

	@Inject @Optional
	public void acceptGuestScienceAgent3(@Named(FreeFlyerStrings.SELECTED_GUEST_SCIENCE_3) Agent a) {
		selected[2] = a;
		checkIfChanged();
	}

	private void checkIfChanged() {
		boolean oldSelectedAndAllConnected = selectedAndAllConnected;
		boolean atLeastOneSelected = false;
		
		boolean selectedAreConnected = true;
		for(int i=0; i<NUM_ROBOTS; i++) {
			if(selected[i] != null) {
				atLeastOneSelected = true;
				if(!ActiveAgentSet.contains(selected[i])) {
					selectedAreConnected = false;
					break;
				}
			}
		}
		
		if(atLeastOneSelected && selectedAreConnected) {
			selectedAndAllConnected = true;
		} else {
			selectedAndAllConnected = false;
		}
		
		if(oldSelectedAndAllConnected != selectedAndAllConnected) {
			notifyListeners();
		}
	}

	@Override
	public void activeAgentSetChanged() {
		checkIfChanged();
	}

	private void notifyListeners() {
		if(selectedAndAllConnected) {
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
