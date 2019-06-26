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

public class SetCameraStreaming extends AbstractCameraCommand {
	protected boolean stream = true;

	@Override
	@JsonIgnore
	public String getDisplayName() {
		if(cameraName == null)
		{		
			if(stream) {
				return getName() + " " + PlanConstants.UNKNOWN_CHARACTER + " Start";
			} else {
				return getName() + " " + PlanConstants.UNKNOWN_CHARACTER + " Stop";
			}
		}
		if(stream) {
			return getName() + " " + getCameraName() + " Start";
		} else {
			return getName() + " " + getCameraName() + " Stop";
		}
	}

	@JsonIgnore
	public static String getClassNameForWidgetDropdown() {
		return "Stream Camera";
	}

	public boolean isStream() {
		return stream;
	}

	public void setStream(boolean stream) {
		boolean old = this.stream;
		this.stream = stream;
		firePropertyChange("stream", old, stream);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + super.hashCode();
		result = prime * result + (stream ? 1 : 0);
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		// instanceof returns false for null
		if(!(o instanceof SetCameraStreaming)) {
			return false;
		}
		if(!super.equals(o)) {
			return false;
		}

		SetCameraStreaming other = (SetCameraStreaming)o;

		if(stream != other.stream) {
			return false;
		}
		return true;
	}
}
