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
package gov.nasa.arc.irg.util.ui.browser;

import gov.nasa.arc.irg.util.ui.UtilUIActivator;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;


public class SimpleBrowserPreferenceInitializer extends
		AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = UtilUIActivator.getDefault().getPreferenceStore();
		
        store.setDefault(SimpleBrowserPreferenceKeys.P_BROWSER_HOME_PAGE, "about:blank");
        store.setDefault(SimpleBrowserPreferenceKeys.P_BROWSER_RECENT_SITES,
                "http://radish.arc.nasa.gov/irgdeployments" + "\n" +
                "http://radish.arc.nasa.gov/gdsManager/status.html" + "\n" +
                "http://radish.arc.nasa.gov/gdsManager/pyraptordStatus.html" + "\n" + 
                "http://radish.arc.nasa.gov/GDSStatusBoard.html" + "\n" +
                "https://babelfish.arc.nasa.gov/trac/irg" + "\n" +
                "https://babelfish.arc.nasa.gov/trac/verve" + "\n" +
                "https://babelfish.arc.nasa.gov/trac/roversw" + "\n" +
                "https://babelfish.arc.nasa.gov/trac/irg_cmake" + "\n" +
                "https://babelfish.arc.nasa.gov/trac/visionworkbench" + "\n" +
                "https://jplis-ahs-003.jpl.nasa.gov:5843/confluence/display/HSI/RAPID" + "\n" +
                "http://nasawatch.com" + "\n" +
                "http://slashdot.org" + "\n" +
                "http://www.google.com"
                );
		
	}
}
