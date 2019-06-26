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
package gov.nasa.arc.irg.plan.freeflyer.config;

import gov.nasa.arc.irg.plan.freeflyer.config.GuestScienceApkGds.GuestScienceCommandGds;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

public class GuestScienceConfigList {
	private String type;
	private List<GuestScienceApkGds> guestScienceApkGds = new ArrayList<GuestScienceApkGds>();

	public GuestScienceConfigList() {
		// for JSON
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@JsonIgnore
	public String getCommandBody(String shortName, String cmdName) {
		for(GuestScienceApkGds conf : guestScienceApkGds) {
			if(conf.getShortName().equals(shortName)) {
				List<GuestScienceCommandGds> cmds = conf.getGuestScienceCommands();
				
				for(GuestScienceCommandGds gsCmd : cmds) {
					if(gsCmd.getName().equals(cmdName)) {
						return gsCmd.getCommand();
					}
				}
			}
		}
		throw new IllegalArgumentException("APK " + shortName + " does not have command " + cmdName);
	}
	
	@JsonIgnore
	public String getApkName(String shortName) {
		for(GuestScienceApkGds conf : guestScienceApkGds) {
			if(conf.getShortName().equals(shortName)) {
				return conf.getApkName();
			}
		}
		throw new IllegalArgumentException("No APK " + shortName + " found");
	}

	public List<GuestScienceApkGds> getGuestScienceConfigs() {
		return guestScienceApkGds;
	}

	public void setGuestScienceConfigs(List<GuestScienceApkGds> guestScienceApkGds) {
		this.guestScienceApkGds = guestScienceApkGds;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + type.hashCode();
		for(GuestScienceApkGds conf : guestScienceApkGds) {
			result = prime * result + ((conf == null) ? 0 : conf.hashCode());
		}
		return result;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(!(o instanceof GuestScienceConfigList)) {
			return false;
		}
		GuestScienceConfigList other = (GuestScienceConfigList)o;
		
		if(type == null) {
			if(other.getType() != null) {
				return false;
			}
		} else if(!type.equals(other.getType())) {
			return false;
		}
		
		List<GuestScienceApkGds> otherConfigs = other.getGuestScienceConfigs();
		
		if(guestScienceApkGds.size() != otherConfigs.size()) {
			return false;
		}
		
		for(int i=0; i<guestScienceApkGds.size(); i++) {
			if(!guestScienceApkGds.get(i).equals(otherConfigs.get(i))){
				return false;
			}
		}
		return true;
	}
}
