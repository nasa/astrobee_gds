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

public class SetFlashlightBrightness extends FreeFlyerCommand {

	protected String which;
	protected float brightness;

	public String getWhich() {
		return which;
	}

	public void setWhich(String which) {
		String old = this.which;
		this.which = which;
		firePropertyChange("which", old, which);
	}

	@Override
	@JsonIgnore
	public String getDisplayName() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName());
		if(which != null) {
			sb.append(" " + which);
		} else {
			sb.append(" " + PlanConstants.UNKNOWN_CHARACTER);
		}
		sb.append(" " + brightness);
		
		return sb.toString();
	}
	
	@JsonIgnore
	public static String getClassNameForWidgetDropdown() {
		return "Flashlight";
	}
	
	@Override
	public int getCalculatedDuration() {
		return 0;
	}

	public float getBrightness() {
		return brightness;
	}

	public void setBrightness(float brightness) {
		float oldBrightness = this.brightness;
		this.brightness = brightness;
		firePropertyChange("brightness", oldBrightness, brightness);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(brightness);
		result = prime * result + ((which == null) ? 0 : which.hashCode());
		result = prime * result + ((m_name == null) ? 0 : m_name.hashCode());
		result = prime * result + ((m_notes == null) ? 0 : m_notes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		float EPSILON = 0.001f;
		if(this == o) {
			return true;
		}
		// instanceof returns false for null
		if(!(o instanceof SetFlashlightBrightness)) {
			return false;
		}
		if(!super.equals(o)) {
			return false;
		}

		SetFlashlightBrightness other = (SetFlashlightBrightness)o;

		if(which == null) {
			if(other.which != null) {
				return false;
			}
		} else {
			if(!which.equals(other.which)) {
				return false;
			}
		}
		
		if(Math.abs(brightness - other.brightness) > EPSILON) {
			return false;
		}
		
		return true;
	}
}
