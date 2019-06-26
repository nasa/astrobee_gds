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
package gov.nasa.arc.verve.common.preferences.rcp;

import gov.nasa.arc.verve.common.Activator;
import gov.nasa.arc.verve.common.VerveDataDir;
import gov.nasa.arc.verve.common.VervePreferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;


public class VervePreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setDefault(VervePreferences.P_DEBUG_AXIS_USE_BOUND_CENTER, "false");
        store.setDefault(VervePreferences.P_DEBUG_SHOW_BOUNDS_ON_SELECT, "false");
        store.setDefault(VervePreferences.P_USE_FALLBACK_SHADERS, false);
        //0.1f, 0.1f, 0.3f, 1
        store.setDefault(VervePreferences.P_DEFAULT_BACKGROUND_COLOR, "26,26,77");
        store.setDefault(VervePreferences.P_TARGET_FRAMERATE, "24");
        store.setDefault(VervePreferences.P_SHADOWMAP_SIZE, "2048");
        
        String home = System.getenv("HOME");
        if(home == null || home.isEmpty()) {
            home = System.getProperty("user.home");
        }
        home = home.replace('\\', '/');
        String defaultVerveDataDir = home + VerveDataDir.DEFAULT_HOME_ROOT;
        store.setDefault(VervePreferences.P_VERVE_DATA_DIR, defaultVerveDataDir);

    }
}
