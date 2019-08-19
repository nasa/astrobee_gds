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

public class Dock extends FreeFlyerCommand {
	protected int berth; // either 1 or 2
	
	@Override
	@JsonIgnore
	public String getDisplayName() {
		if(berth != 0) {
			return getName() + " " + berth;
		} else {
			return getName();
		}
	}

	public int getBerth() {
		return berth;
	}
	
	public void setBerth(int berth) {
		int oldBerth = this.berth;
		this.berth = berth;
		firePropertyChange("berth", oldBerth, berth);
	}
	
	public int getCalculatedDuration() {
		return 60;
	}
	
	@JsonIgnore
	public static String getClassNameForWidgetDropdown() {
		return "Dock";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + berth;
		result = prime * result + ((m_name == null) ? 0 : m_name.hashCode());
		result = prime * result + ((m_notes == null) ? 0 : m_notes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		// instanceof returns false for null
		if(!(o instanceof Dock)) {
			return false;
		}
		if(!super.equals(o)) {
			return false;
		}

		Dock other = (Dock)o;

		if(berth != other.berth) {
			return false;
		}

		return true;
	}
}
