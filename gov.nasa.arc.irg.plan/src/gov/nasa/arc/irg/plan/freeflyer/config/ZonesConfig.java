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

import java.util.ArrayList;
import java.util.List;

/** holds one of the "Zones" files to send to Astrobee */
public class ZonesConfig {
	
	private String timestamp;
	private List<KeepoutConfig> zones;
	
	public ZonesConfig() {
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	public List<KeepoutConfig> getZones() {
		return zones;
	}

	public void setZones(List<KeepoutConfig> zones) {
		this.zones = zones;
	}
	
	public void addZone(KeepoutConfig zone) {
		if(zones == null) {
			zones = new ArrayList<KeepoutConfig>();
		}
		zones.add(zone);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		result = prime * result + ((zones == null) ? 0 : zones.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(!(o instanceof ZonesConfig)) {
			return false;
		}
		ZonesConfig other = (ZonesConfig)o;

		if(!timestamp.equals(other.timestamp)) {
			return false;
		}
		
		if(!zones.equals(other.zones)) {
			return false;
		}
		return true;
	}
}
