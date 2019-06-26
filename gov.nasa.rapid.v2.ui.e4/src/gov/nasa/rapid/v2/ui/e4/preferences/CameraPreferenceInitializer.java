///*******************************************************************************
// * Copyright (c) 2013 United States Government as represented by the 
// * Administrator of the National Aeronautics and Space Administration. 
// * All rights reserved.
// * 
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * 
// *   http://www.apache.org/licenses/LICENSE-2.0
// * 
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// ******************************************************************************/
//package gov.nasa.rapid.v2.ui.e4.preferences;
//
//
//import gov.nasa.rapid.v2.ui.RapidV2UiActivator;
//
//import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
//import org.eclipse.jface.preference.IPreferenceStore;
//
//public class CameraPreferenceInitializer extends AbstractPreferenceInitializer {
//    
//        /** NOTE: this string is assumed to terminate with a separator character */
//		public final static String defaultCameraNames = 
//		        "Default"     + CameraPreferenceKeys.DEFAULT_CAMERA_SEPARATOR +
//                "HazCamFront" + CameraPreferenceKeys.DEFAULT_CAMERA_SEPARATOR +
//                "HazCamRear"  + CameraPreferenceKeys.DEFAULT_CAMERA_SEPARATOR +
//                "HazCamLeft"  + CameraPreferenceKeys.DEFAULT_CAMERA_SEPARATOR +
//                "HazCamRight" + CameraPreferenceKeys.DEFAULT_CAMERA_SEPARATOR + 
//                "NavCamLeft"  + CameraPreferenceKeys.DEFAULT_CAMERA_SEPARATOR +
//                "NavCamRight" + CameraPreferenceKeys.DEFAULT_CAMERA_SEPARATOR;
//                
////		
////                "GroundCam"   + CameraPreferenceKeys.DEFAULT_CAMERA_SEPARATOR +
////				"usbPanTilt"  + CameraPreferenceKeys.DEFAULT_CAMERA_SEPARATOR +
////				"usbMi";
//		
//	@Override
//	public void initializeDefaultPreferences() {
//		IPreferenceStore store = RapidV2UiActivator.getDefault().getPreferenceStore();
//		store.setDefault(CameraPreferenceKeys.DEFAULT_CAMERAS, defaultCameraNames);
//	}
//	
//	public static String[] getCameras(){
//		IPreferenceStore store = RapidV2UiActivator.getDefault().getPreferenceStore();
//		String allCameras = store.getString(CameraPreferenceKeys.DEFAULT_CAMERAS);
//		String[] retVal = allCameras.split(CameraPreferenceKeys.DEFAULT_CAMERA_SEPARATOR);
//		if(retVal.length == 0) {
//		    retVal = new String[] { "Default" };
//		}
//		return retVal;
//	}
//}
