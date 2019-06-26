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
package gov.nasa.arc.verve.ardor3d.e4.framework;

import java.util.ArrayList;

public class VerveScenarioStarted {
    protected static ArrayList<IVerveScenarioStartedListener> s_listeners = new ArrayList<IVerveScenarioStartedListener>();
    protected static IVerveScenario s_scenario = null;
    
    public synchronized static void addListener(IVerveScenarioStartedListener listener) {
        if(s_scenario == null) {
            s_listeners.add(listener);
        }
        else {
            listener.onScenarioStarted(s_scenario);
        }
    }
    
    /** should only be called by a Scenario after initialization */
    public synchronized static void scenarioStarted(IVerveScenario scenario) {
        s_scenario = scenario;
        if(s_listeners != null) {
        	for(IVerveScenarioStartedListener listener : s_listeners) {
        		listener.onScenarioStarted(s_scenario);
        	}
        s_listeners.clear();
        s_listeners = null;
        }
    }
}
