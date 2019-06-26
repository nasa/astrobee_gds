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

import gov.nasa.arc.irg.plan.freeflyer.config.GuestScienceApkGds;

import java.util.ArrayList;

import rapid.ext.astrobee.GuestScienceApk;
import rapid.ext.astrobee.GuestScienceCommand;

public class GuestScienceApkGdsRunning extends GuestScienceApkGds{
	protected boolean running;
	
	
	public GuestScienceApkGdsRunning(GuestScienceApk input) {
		apkName = input.apkName;
		shortName = input.shortName;
		primary = input.primary;
		running = false;
		
		guestScienceCommandGds = new ArrayList<GuestScienceCommandGds>();
		for(int i=0; i<input.commands.userData.size(); i++) {
			GuestScienceCommand rapidCmd = (GuestScienceCommand) input.commands.userData.get(i);
			guestScienceCommandGds.add(new GuestScienceCommandGds(rapidCmd));
		}
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public String getStatusString() {
		if(running) {
			return "Running";
		}
		return "Idle";
	}
}
