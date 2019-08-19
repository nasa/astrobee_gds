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

public class SetTelemetryRate extends FreeFlyerCommand {

	protected String telemetryName;
	protected float rate;
	
	public String getTelemetryName() {
		return telemetryName;
	}

	public void setTelemetryName(String name) {
		String old = this.telemetryName;
		this.telemetryName = name;
		firePropertyChange("name", old, name);
	}
	
	@Override
	@JsonIgnore
	public String getDisplayName() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName());
		if(telemetryName != null) {
			sb.append(" " + telemetryName);
		} else {
			sb.append(" " + PlanConstants.UNKNOWN_CHARACTER);
		}
		sb.append(" " + rate);
		
		return sb.toString();
	}

	public float getRate() {
		return rate;
	}

	public void setRate(float rate) {
		float old = this.rate;
		this.rate = rate;
		firePropertyChange("rate", old, rate);
	}

	@Override
	public int getCalculatedDuration() {
		return 0;
	}

	@JsonIgnore
	public static String getClassNameForWidgetDropdown() {
		return "Set Telemetry Rate ";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(rate);
		result = prime * result + ((telemetryName == null) ? 0 : telemetryName.hashCode());
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
		if(!(o instanceof SetTelemetryRate)) {
			return false;
		}
		if(!super.equals(o)) {
			return false;
		}

		SetTelemetryRate other = (SetTelemetryRate)o;

		if(telemetryName == null) {
			if(other.telemetryName != null) {
				return false;
			}
		} else {
			if(!telemetryName.equals(other.telemetryName)) {
				return false;
			}
		}
		
		if(Math.abs(rate - other.rate) > EPSILON) {
			return false;
		}
		
		return true;
	}
}
