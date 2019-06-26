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
package gov.nasa.arc.irg.plan.util;

import rapid.ext.astrobee.DATA_DOWNLOAD_METHOD_DELAYED;
import rapid.ext.astrobee.DATA_DOWNLOAD_METHOD_IMMEDIATE;
import rapid.ext.astrobee.SETTINGS_CAMERA_NAME_DOCK;
import rapid.ext.astrobee.SETTINGS_CAMERA_NAME_HAZ;
import rapid.ext.astrobee.SETTINGS_CAMERA_NAME_NAV;
import rapid.ext.astrobee.SETTINGS_CAMERA_NAME_PERCH;
import rapid.ext.astrobee.SETTINGS_CAMERA_NAME_SCI;
import rapid.ext.astrobee.SETTINGS_FLASHLIGHT_LOCATION_BACK;
import rapid.ext.astrobee.SETTINGS_FLASHLIGHT_LOCATION_FRONT;

// These can't be in gov.nasa.arc.irg.freeflyer.utils.WorkbenchConstants
// because it causes a dependency cycle
public class PlanConstants {
	public static final String BERTH_ONE = "Berth One";
	public static final String BERTH_TWO = "Berth Two";
	
	public static final String UNKNOWN_CHARACTER = "-";
	
	public static final String IMMEDIATE_STRING = DATA_DOWNLOAD_METHOD_IMMEDIATE.VALUE;
	public static final String DELAYED_STRING = DATA_DOWNLOAD_METHOD_DELAYED.VALUE;
	
	final public static String[] DATA_METHOD_STRINGS = {
		IMMEDIATE_STRING, 
		DELAYED_STRING
	};
	
	public static final String SCI_CAM_NAME = SETTINGS_CAMERA_NAME_SCI.VALUE;
	public static final String NAV_CAM_NAME = SETTINGS_CAMERA_NAME_NAV.VALUE;
	public static final String HAZ_CAM_NAME = SETTINGS_CAMERA_NAME_HAZ.VALUE;
	public static final String DOCK_CAM_NAME = SETTINGS_CAMERA_NAME_DOCK.VALUE;
	public static final String PERCH_CAM_NAME = SETTINGS_CAMERA_NAME_PERCH.VALUE;
	
	final public static String[] CAMERA_NAME_STRINGS = {
		SCI_CAM_NAME, 
		NAV_CAM_NAME,
		HAZ_CAM_NAME,
		DOCK_CAM_NAME,
		PERCH_CAM_NAME
	};
	
	public static final String FLASHLIGHT_BACK = SETTINGS_FLASHLIGHT_LOCATION_BACK.VALUE;
	public static final String FLASHLIGHT_FRONT = SETTINGS_FLASHLIGHT_LOCATION_FRONT.VALUE;
	
	final public static String[] FLASHLIGHT_LOCATION_STRINGS = {
		FLASHLIGHT_FRONT, 
		FLASHLIGHT_BACK
	};
}
