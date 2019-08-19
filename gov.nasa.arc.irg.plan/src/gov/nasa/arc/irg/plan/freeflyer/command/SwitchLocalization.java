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

import gov.nasa.arc.irg.plan.util.PlanConstants;

import org.codehaus.jackson.annotate.JsonIgnore;

public class SwitchLocalization extends FreeFlyerCommand {
	protected String mode;

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		String old = this.mode;
		this.mode = mode;
		firePropertyChange("mode", old, mode);
	}

	@Override
	public int getCalculatedDuration() {
		return 0;
	}

	@JsonIgnore
	public static String getClassNameForWidgetDropdown() {
		return "Switch Localization ";
	}
	
	@Override
	@JsonIgnore
	public String getDisplayName() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName());
		if(mode != null) {
			sb.append(" " + mode);
		} else {
			sb.append(" " + PlanConstants.UNKNOWN_CHARACTER);
		}
		
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mode == null) ? 0 : mode.hashCode());
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
		if(!(o instanceof SwitchLocalization)) {
			return false;
		}
		if(!super.equals(o)) {
			return false;
		}

		SwitchLocalization other = (SwitchLocalization)o;

		if(mode == null) {
			if(other.mode != null) {
				return false;
			}
		} else {
			if(!mode.equals(other.mode)) {
				return false;
			}
		}
		
		return true;
	}
}
