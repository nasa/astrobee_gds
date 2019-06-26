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
//import gov.nasa.rapid.v2.agent.Agent;
//import gov.nasa.rapid.v2.ui.RapidV2UiActivator;
//
//import java.io.File;
//
//import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
//import org.eclipse.jface.preference.IPreferenceStore;
//
//public class RapidPreferenceInitializer extends AbstractPreferenceInitializer {  
//    protected static String defaultLogDirectory = System.getProperty("user.home");
//    
//    /**
//     * Override the default preference for default log directory. 
//     * @param dirname
//     * @return true if set succeeded (directory exists and is writable)
//     */
//    public static boolean setDefaultLogDirectory(String dirname) {
//        File file = new File(dirname);
//        if(file.isDirectory() && file.canWrite()) {
//            defaultLogDirectory = dirname;
//            return true;
//        }
//        return false;
//    }
//    
//    @Override
//    public void initializeDefaultPreferences() {
//        IPreferenceStore store = RapidV2UiActivator.getDefault().getPreferenceStore();
//        //		Agent[] defaultAgentSet = new Agent[] { Agent.K10Black,   Agent.KRex, 
//        //		                                        Agent.LerA,       Agent.LerB, 
//        //		                                        Agent.Robonaut2A, Agent.Centaur2 };
//        //		String defaultAgentsString = StrUtil.arrayToString(defaultAgentSet, DdsPreferenceKeys.LIST_SEPARATOR);
//
//        store.setDefault(RapidPreferenceKeys.P_AGENT_OF_INTEREST, Agent.GenericSim.name());
//        //store.setDefault(RapidPreferenceKeys.P_AGENT_SET, defaultAgentsString);
//        
//        store.setDefault(RapidPreferenceKeys.P_DEFAULT_LOG_DIRECTORY, defaultLogDirectory);
//
//    }
//
//}
