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

import java.util.ArrayList;
import java.util.List;

public class SetCameraPresetsList {
	private String type;
	private List<OptionsForOneCamera> optionsForOneCamera = new ArrayList<OptionsForOneCamera>();

	public SetCameraPresetsList() {
		// for json deserializing
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<OptionsForOneCamera> getOptionsForOneCamera() {
		return optionsForOneCamera;
	}

	public void setOptionsForOneCamera(List<OptionsForOneCamera> optionsForOneCamera) {
		this.optionsForOneCamera = optionsForOneCamera;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		
		for(OptionsForOneCamera opt : optionsForOneCamera) {
			result = prime * result + ((opt == null) ? 0 : opt.hashCode());
		}
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(!(o instanceof SetCameraPresetsList)) {
			return false;
		}
		SetCameraPresetsList other = (SetCameraPresetsList)o;

		if(type == null) {
			if(other.type != null) {
				return false;
			}
		} else {
			if(!type.equals(other.type)) {
				return false;
			}
		}
		
		List<OptionsForOneCamera> otherOptions = other.getOptionsForOneCamera();

		if(optionsForOneCamera.size() != otherOptions.size()) {
			return false;
		}
		
		for(int i=0; i<optionsForOneCamera.size(); i++) {
			if(!optionsForOneCamera.get(i).equals(otherOptions.get(i))){
				return false;
			}
		}
		return true;
	}
}
