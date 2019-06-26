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



public class FaultInfoGds {
	private short subsystem;
	private short node;
	private int faultId;
	private boolean warning;
	private String faultDescription;
	
	public FaultInfoGds() {
		// for JSON
	}
	
	public short getSubsystem() {
		return subsystem;
	}

	public void setSubsystem(short subsystem) {
		this.subsystem = subsystem;
	}

	public short getNode() {
		return node;
	}

	public void setNode(short node) {
		this.node = node;
	}

	public int getFaultId() {
		return faultId;
	}

	public void setFaultId(int faultId) {
		this.faultId = faultId;
	}

	public boolean isWarning() {
		return warning;
	}

	public void setWarning(boolean warning) {
		this.warning = warning;
	}

	public String getFaultDescription() {
		return faultDescription;
	}

	public void setFaultDescription(String faultDescription) {
		this.faultDescription = faultDescription;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + subsystem;
		result = prime * result + node;
		result = prime * result + (int)faultId;
		result = prime * result + (warning ? 0 : 1);
		result = prime * result + ((faultDescription == null) ? 0 : faultDescription.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(!(o instanceof FaultInfoGds)) {
			return false;
		}
		FaultInfoGds other = (FaultInfoGds)o;
		
		if(subsystem != other.subsystem) {
			return false;
		}
		
		if(node != other.node) {
			return false;
		}
		
		if(faultId != other.faultId) {
			return false;
		}
		
		if(warning != other.warning) {
			return false;
		}
		
		if(!faultDescription.equals(other.faultDescription)) {
			return false;
		}
		return true;
	}
}
