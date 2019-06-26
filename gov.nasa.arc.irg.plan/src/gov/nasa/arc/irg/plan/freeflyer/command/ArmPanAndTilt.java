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

import rapid.ext.astrobee.ARM_ACTION_TYPE_BOTH;

public class ArmPanAndTilt extends FreeFlyerCommand {
	protected float pan = 0; // degrees
	protected float tilt = 0; // degrees
	protected float armSpeed = 5; // degrees/second
	protected String which; 
	
	@JsonIgnore
	public static String getClassNameForWidgetDropdown() {
		return "Arm Pan And Tilt";
	}
	
	public int getCalculatedDuration() {
		return (int) ((pan + tilt) / armSpeed);
	}
	
	public String getWhich()
	{
		return ARM_ACTION_TYPE_BOTH.VALUE;
	}

	public void setPan(float pan) {
		float oldvalue = this.pan;
		this.pan = pan;
		if (oldvalue != pan){
			firePropertyChange("pan", oldvalue, this.pan);
		}
	}

	public void setTilt(float tilt) {
		float oldvalue = this.tilt;
		this.tilt = tilt;
		if (oldvalue != this.tilt){
			firePropertyChange("tilt", oldvalue, this.tilt);
		}
	}

	public float getPan() {
		return pan;
	}

	public float getTilt() {
		return tilt;
	}

	@Override
	protected String customMembersToString() {
		String result =  super.customMembersToString();
		StringBuffer sb = new StringBuffer(result);
		sb.append("Pan ");
		sb.append(getPan());
		sb.append(" deg, tilt ");
		sb.append(getTilt());
		sb.append(" deg");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(pan);
		result = prime * result + Float.floatToIntBits(tilt);
		return result;
	}

	@Override
	public boolean equals(Object o) {
		float EPSILON = 0.0001f;
		if(this == o) {
			return true;
		}
		// instanceof returns false for null
		if(!(o instanceof ArmPanAndTilt)) {
			return false;
		}
		if(!super.equals(o)) {
			return false;
		}
		ArmPanAndTilt other = (ArmPanAndTilt)o;

		if(Math.abs(pan-other.getPan()) > EPSILON) {
			return false;
		}
		if(Math.abs(tilt-other.getTilt()) > EPSILON) {
			return false;
		}
		return true;
	}
}
