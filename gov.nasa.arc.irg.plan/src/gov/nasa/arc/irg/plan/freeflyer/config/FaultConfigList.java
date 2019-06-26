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

public class FaultConfigList {
	private String type;
	private List<String> subsystems;
	private List<String> nodes;
	private List<FaultInfoGds> faultInfos = new ArrayList<FaultInfoGds>();
	
	public FaultConfigList() {
		// for JSON
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public List<String> getSubsystems() {
		return subsystems;
	}
	
	public void setSubsystems(List<String> subsystems) {
		this.subsystems = subsystems;
	}
	
	public List<String> getNodes() {
		return nodes;
	}
	
	public void setNodes(List<String> nodes) {
		this.nodes = nodes;
	}
	
	public List<FaultInfoGds> getFaultInfos() {
		return faultInfos;
	}
	
	public void setFaultInfos(List<FaultInfoGds> faultInfos) {
		this.faultInfos = faultInfos;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + type.hashCode();
		result = prime * result + subsystems.hashCode();
		result = prime * result + nodes.hashCode();
		result = prime * result + faultInfos.hashCode();
		return result;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(!(o instanceof FaultConfigList)) {
			return false;
		}
		FaultConfigList other = (FaultConfigList)o;
		
		if(type == null) {
			if(other.getType() != null) {
				return false;
			}
		} else if(!type.equals(other.getType())) {
			return false;
		}
		
		if(subsystems == null) {
			if(other.subsystems != null) {
				return false;
			}
		} else if(!subsystems.equals(other.subsystems)) {
			return false;
		}
		
		if(nodes == null) {
			if(other.nodes != null) {
				return false;
			}
		} else if(!nodes.equals(other.nodes)) {
			return false;
		}
		
		if(faultInfos == null) {
			if(other.faultInfos != null) {
				return false;
			}
		} else if(!faultInfos.equals(other.faultInfos)) {
			return false;
		}
		return true;
	}
}
