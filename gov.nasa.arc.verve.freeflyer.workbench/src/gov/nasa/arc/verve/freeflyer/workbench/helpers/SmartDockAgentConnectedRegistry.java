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

import gov.nasa.rapid.v2.e4.agent.ActiveAgentSet;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.agent.IActiveAgentSetListener;

import java.util.ArrayList;
import java.util.List;

public class SmartDockAgentConnectedRegistry  implements IActiveAgentSetListener {
	private static List<SelectedAgentConnectedListener> s_registry = new ArrayList<SelectedAgentConnectedListener>();
	private static Agent smartDockAgent = Agent.SmartDock;
	private static boolean smartDockIsConnected = false;
	
	{
		ActiveAgentSet.INSTANCE.addListener(this);
	}
	
	public static void addListener(SelectedAgentConnectedListener sacl) {
		s_registry.add(sacl);
		if(smartDockIsConnected) {
			sacl.onSelectedAgentConnected();
		} else {
			sacl.onSelectedAgentDisconnected();
		}
	}
	
	public static void removeListener(SelectedAgentConnectedListener sacl) {
		s_registry.remove(sacl);
	}
	
	private void checkIfChanged() {
		boolean oldSmartDockIsConnected = smartDockIsConnected;
		boolean changed = false;
		if(ActiveAgentSet.contains(smartDockAgent)) {
			if(!oldSmartDockIsConnected) {
				changed = true;
			}
			smartDockIsConnected = true;
		} else {
			if(oldSmartDockIsConnected) {
				changed = true;
			}
			smartDockIsConnected = false;
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
		if(smartDockIsConnected) {
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
