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

public class SetCheckZones extends FreeFlyerCommand {

	protected boolean checkZones;

	public boolean getCheckZones() {
		return checkZones;
	}

	public void setCheckZones(boolean check) {
		boolean oldCheck = this.checkZones;
		this.checkZones = check;
		firePropertyChange("check", oldCheck, check);
	}

	@Override
	public int getCalculatedDuration() {
		return 0;
	}

	@Override
	@JsonIgnore
	public String getDisplayName() {
		return getName() + " " + checkZones;
	}

	@JsonIgnore
	public static String getClassNameForWidgetDropdown() {
		return "Set Check Zones";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (checkZones ? 0 : 1);
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		// instanceof returns false for null
		if(!(o instanceof SetCheckZones)) {
			return false;
		}
		if(!super.equals(o)) {
			return false;
		}

		SetCheckZones other = (SetCheckZones)o;

		if(other.checkZones != checkZones) {
			return false;
		}

		return true;
	}
}
