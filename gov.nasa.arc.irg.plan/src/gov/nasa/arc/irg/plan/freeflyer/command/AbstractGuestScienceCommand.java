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
package gov.nasa.arc.irg.plan.freeflyer.command;

import org.codehaus.jackson.annotate.JsonIgnore;

import gov.nasa.arc.irg.plan.freeflyer.config.GuestScienceApkGds;

public abstract class AbstractGuestScienceCommand extends FreeFlyerCommand {
	
	protected GuestScienceApkGds guestScienceApkGds;
	
	protected String apkName; // put at top level for FSW

	public GuestScienceApkGds getGuestScienceApkGds() {
		return guestScienceApkGds;
	}

	public void setGuestScienceApkGds(GuestScienceApkGds option) {
		GuestScienceApkGds oldValue = this.guestScienceApkGds;
		apkName = option.getApkName();
		this.guestScienceApkGds = option;
		if(oldValue != option) {
			firePropertyChange("guestScienceApkGds", oldValue, this.guestScienceApkGds);
		}
	}
	
	@Override
	@JsonIgnore
	public String getDisplayName() {
		if(guestScienceApkGds != null) {
			return getName() + " " + guestScienceApkGds.getShortName();
		} else {
			return getName();
		}
	}

	public String getApkName() {
		return apkName;
	}

	public void setApkName(String apkName) {
		this.apkName = apkName;
	}

}
