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
package gov.nasa.arc.irg.freeflyer.utils;

import org.eclipse.core.runtime.Platform;

public class VerveConstants {
	public static final String SWING_SPEED = "-swing";
	public static final String DOLLY_SPEED = "-dolly";
	public static final String CRAB_SPEED = "-crab";
	public static final String ABSOLUTE_CRAB = "-absoluteCrab";
	public static final String ZOOM_TO_CLICK = "-zoomToClick";
	
	private static final VerveConstants instance = new VerveConstants();
	
	/** true if that flag is in the command line args */
	public static boolean isFlagPresent(String flag) {
		return instance.isFlagPresentInternal(flag);
	}
	
	/** returns string value associated with that command-line parameter */
	public static String getStringValueOfParameter(String param) {
		return instance.getValueOfParameterInternal(param);
	}
	
	public static Float getFloatValueOfParameter(String param) {
		String raw = instance.getValueOfParameterInternal(param);
		if(raw != null) {
			return Float.valueOf(raw);
		}
		return null;
	}
	
	private String getValueOfParameterInternal(String param) {
		String[] args = Platform.getCommandLineArgs();

		for(int i=0; i<args.length-1; i++) {
			if(args[i].equals(param)) {
				return args[i+1];
			}
		}
		return null;
	}

	private boolean isFlagPresentInternal(String flag) {
		String[] args = Platform.getCommandLineArgs();

		for(int i=0; i<args.length; i++) {
			if(args[i].equals(flag)) {
				return true;
			}
		}
		return false;
	}
	
}
