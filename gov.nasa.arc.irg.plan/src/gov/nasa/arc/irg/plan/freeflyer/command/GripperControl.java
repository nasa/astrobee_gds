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

public class GripperControl extends FreeFlyerCommand {
	protected boolean open = true;

	@Override
	@JsonIgnore
	public String getDisplayName() {
		if(open) {
			return getName() + " Open";
		} else {
			return getName() + " Close";
		}
	}
	
	@JsonIgnore
	public static String getClassNameForWidgetDropdown() {
		return "Gripper";
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		boolean oldOpen = this.open;
		this.open = open;
		firePropertyChange("open", oldOpen, open);
	}

	@Override
	public int getCalculatedDuration() {
		return 2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((open) ? 1 : 0);
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
		if(!(o instanceof GripperControl)) {
			return false;
		}
		if(!super.equals(o)) {
			return false;
		}

		GripperControl other = (GripperControl)o;

		if(open != other.open) {
			return false;
		}
		
		return true;
	}
}
