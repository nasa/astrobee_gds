/*******************************************************************************
 * Copyright (c) 2013 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package gov.nasa.arc.verve.ardor3d.e4;

import gov.nasa.arc.verve.ardor3d.e4.preferences.CameraPreferenceKeys;


@SuppressWarnings("unchecked")
public class VerveArdor3dPreferences {	
    private static boolean prefBool(String str) {
        try {
            String val = Ardor3D.getPreference(str);
            return Boolean.parseBoolean(val);
        }
        catch(Throwable t) {
            return false;
        }
    }
    private static int  prefInt(String str) {
        try {
            String val = Ardor3D.getPreference(str);
            return Integer.parseInt(val);
        }
        catch(Throwable t) {
            return 0;
        }
    }

    /**
     */
    public static boolean isCameraNadirSnap()	{
        return prefBool(CameraPreferenceKeys.P_NADIR_SNAP);
    }

    public static int getAntialiasingSamples() {
        return prefInt(CameraPreferenceKeys.P_ANTIALIASING);
    }
    
}
