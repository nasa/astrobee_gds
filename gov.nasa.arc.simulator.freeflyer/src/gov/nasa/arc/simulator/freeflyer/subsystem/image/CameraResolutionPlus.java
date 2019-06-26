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
package gov.nasa.arc.simulator.freeflyer.subsystem.image;

import rapid.ext.astrobee.CameraResolution;

// Class to correlate all the ways we have of referring to resolution
public enum CameraResolutionPlus {
	
	RES_640(CameraResolution.RESOLUTION_640_480, "640_480"),
	RES_1024(CameraResolution.RESOLUTION_1024_768, "1024_768"),
	RES_1280(CameraResolution.RESOLUTION_1280_720, "1280_720"),
	RES_1920(CameraResolution.RESOLUTION_1920_1080, "1920_1080");
	
	/** The one sent by the TelemetryPublisher */
	private final CameraResolution cameraResolution;
	
	/** Name that the folder with images should be called */
	private final String folderName;
	
	/** The one sent in the setCamera command */ 
	private final String commandParameter;

	CameraResolutionPlus(CameraResolution camRes, String folderName) {
		this.cameraResolution = camRes;
		this.folderName = folderName;
		this.commandParameter = cameraResolutionToString(camRes);
	}

	public CameraResolution getCameraResolution() {
		return cameraResolution;
	}

	public String getFolderName() {
		return folderName;
	}
	
	public static CameraResolutionPlus fromString(String in) {
		if(in.equals(RES_640.toString())) {
			return RES_640;
		}
		else if(in.equals(RES_1024.toString())) {
			return RES_1024;
		}
		else if(in.equals(RES_1280.toString())) {
			return RES_1280;
		}
		else if(in.equals(RES_1920.toString())) {
			return RES_1920;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return commandParameter;
	}
	
	public String cameraResolutionToString(CameraResolution resolution) {
		if(resolution == null) {
			return "";
		}
		String raw = resolution.toString();
		String[] tokens = raw.split("_");
		return tokens[1] + "_" + tokens[2];
	}
}
