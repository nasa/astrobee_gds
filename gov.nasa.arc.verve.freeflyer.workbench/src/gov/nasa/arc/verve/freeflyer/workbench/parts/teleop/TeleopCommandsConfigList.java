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
package gov.nasa.arc.verve.freeflyer.workbench.parts.teleop;

import java.util.ArrayList;
import java.util.List;

public class TeleopCommandsConfigList {
	private String type;
	private List<AbstractTeleopCommandConfig> teleopCommandConfigs = new ArrayList<AbstractTeleopCommandConfig>();
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public List<AbstractTeleopCommandConfig> getTeleopCommandConfigs() {
		return teleopCommandConfigs;
	}
	
	public void setTeleopCommandConfigs(
			List<AbstractTeleopCommandConfig> teleopCommandConfigs) {
		this.teleopCommandConfigs = teleopCommandConfigs;
	}
	
}
