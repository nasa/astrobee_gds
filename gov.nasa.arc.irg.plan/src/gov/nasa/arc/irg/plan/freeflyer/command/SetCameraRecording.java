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

public class SetCameraRecording extends AbstractCameraCommand {
	protected boolean record = true;

	@Override
	@JsonIgnore
	public String getDisplayName() {
		if(cameraName == null)
		{		
			if(record) {
				return getName() + " " + PlanConstants.UNKNOWN_CHARACTER + " Start";
			} else {
				return getName() + " " + PlanConstants.UNKNOWN_CHARACTER + " Stop";
			}
		}
		if(record) {
			return getName() + " " + getCameraName() + " Start";
		} else {
			return getName() + " " + getCameraName() + " Stop";
		}
	}

	@JsonIgnore
	public static String getClassNameForWidgetDropdown() {
		return "Record Camera";
	}
	public boolean isRecord() {
		return record;
	}

	public void setRecord(boolean record) {
		boolean old = this.record;
		this.record = record;
		firePropertyChange("record", old, record);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + super.hashCode();
		result = prime * result + (record ? 1 : 0);
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		// instanceof returns false for null
		if(!(o instanceof SetCameraRecording)) {
			return false;
		}
		if(!super.equals(o)) {
			return false;
		}

		SetCameraRecording other = (SetCameraRecording)o;

		if(record != other.record) {
			return false;
		}
		return true;
	}
}
