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

public class PlanPayloadConfigList {
	private String type;
	private List<PlanPayloadConfig> planPayloadConfigs = new ArrayList<PlanPayloadConfig>();

	public PlanPayloadConfigList() {
		// for JSON
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<PlanPayloadConfig> getPlanPayloadConfigs() {
		return planPayloadConfigs;
	}

	public void setPlanPayloadConfigs(List<PlanPayloadConfig> planPayloadConfigs) {
		this.planPayloadConfigs = planPayloadConfigs;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + type.hashCode();
		for(PlanPayloadConfig conf : planPayloadConfigs) {
			result = prime * result + ((conf == null) ? 0 : conf.hashCode());
		}
		return result;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(!(o instanceof PlanPayloadConfigList)) {
			return false;
		}
		PlanPayloadConfigList other = (PlanPayloadConfigList)o;
		
		if(type == null) {
			if(other.getType() != null) {
				return false;
			}
		} else if(!type.equals(other.getType())) {
			return false;
		}
		
		List<PlanPayloadConfig> otherConfigs = other.getPlanPayloadConfigs();
		
		if(planPayloadConfigs.size() != otherConfigs.size()) {
			return false;
		}
		
		for(int i=0; i<planPayloadConfigs.size(); i++) {
			if(!planPayloadConfigs.get(i).equals(otherConfigs.get(i))){
				return false;
			}
		}
		return true;
	}
}
