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
package gov.nasa.arc.verve.freeflyer.workbench.parts.standard;

import gov.nasa.rapid.v2.e4.agent.Agent;

public class TopBarDataHolder {

	private static TopBarDataHolder instance;
	public boolean activate = false;
	
	private Agent savedBee = null; // whatever was last selected, even if it died
	
	public static TopBarDataHolder getInstance(){
		if(instance == null)
			instance = new TopBarDataHolder();
		return instance;
	}
	
	/** if an Agent was selected and then died, return that Agent
	* 	otherwise, return null
	*/
	public Agent getSavedBee() {
		return savedBee;
	}
	
	public String getSavedBeeName() {
		return savedBee.name();
	}

	public void setSavedBee(Agent savedBee) {
		this.savedBee = savedBee;
	}
	
}
