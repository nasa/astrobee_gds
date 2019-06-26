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
package gov.nasa.arc.verve.freeflyer.workbench.parts.engineering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gov.nasa.rapid.v2.e4.agent.Agent;

public class LiveVideoAgentHolder {
	private static List<LiveVideoAgentListener> listeners = new ArrayList<LiveVideoAgentListener>();
	private static HashMap<Agent,LiveVideoAgentListener> selectedAgents = new HashMap<Agent, LiveVideoAgentListener>();
	
	public static void addListener(LiveVideoAgentListener listener){
		if(!listeners.contains(listener)){
			listeners.add(listener);
		}
	}
	
	public static void removeListener(LiveVideoAgentListener listener){
		if(listeners.contains(listener)){
			listeners.remove(listener);
		}
	}
	
	//update = true will notify other listeners that an agent is added
	//sometimes we don't want to do that
	public static void selectAgent(Agent a,LiveVideoAgentListener currentListener,boolean update){
		if(!selectedAgents.containsKey(a)){
			selectedAgents.put(a,currentListener);
			if(update){
				for(LiveVideoAgentListener list : listeners){
					if(!currentListener.equals(list))
						list.agentSelected(a);
				}
			}
		}
	}
	
	//update = true will notify other listeners that an agent is removed
	public static void releaseAgent(Agent a,LiveVideoAgentListener currentListener,boolean update){
		if(selectedAgents.containsKey(a)){
			if(selectedAgents.get(a).equals(currentListener)){
				selectedAgents.remove(a);
				if(update){
					for(LiveVideoAgentListener list : listeners){
						if(!currentListener.equals(list))
							list.agentRelease();
					}
				}
			}
		}
	}
	
	public static boolean isAgentSelected(Agent a){
		for(Agent b : selectedAgents.keySet()){
			if(b.name().equals(a.name()))
				return true;
		}
		return false;
	}
}
