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
package gov.nasa.arc.irg.plan.ui.io;

import gov.nasa.arc.irg.plan.freeflyer.config.OptionsForOneCamera;
import gov.nasa.arc.irg.plan.freeflyer.config.SetCameraPresetsList;

public class SetCameraPresetsListLoader extends GenericJsonConfigLoader {
	private static SetCameraPresetsList cameraPresetList;

	static {
		classToLoad = SetCameraPresetsList.class;
	}
	public static SetCameraPresetsList getStandardConfig() throws Exception {
		if(cameraPresetList == null) {
			cameraPresetList = (SetCameraPresetsList)getConfig(ConfigFileWrangler.getInstance().getCameraPresetsListPath());
		}
		return cameraPresetList;
	}

	public static SetCameraPresetsList loadFromFile(String filename) throws Exception {
		return (SetCameraPresetsList) loadFromFileGeneric(filename);
	}

	public static String[] getNamesOfPresetsFor(String camName) throws Exception{
		OptionsForOneCamera opts = getOptionsFor(camName);
		if(opts != null ) {
			return opts.getPresetNames();
		} else {
			return new String[0];
		}
	}

	public static OptionsForOneCamera getOptionsFor(String camName) throws Exception {
		SetCameraPresetsList cameraPresetList = getStandardConfig();
		if(camName == null) {
			return null;
		}
		for(OptionsForOneCamera opt : cameraPresetList.getOptionsForOneCamera()) {
			if(camName.equals(opt.getCameraName())) {
				return opt;
			}
		}	
		return null;
	}
}
