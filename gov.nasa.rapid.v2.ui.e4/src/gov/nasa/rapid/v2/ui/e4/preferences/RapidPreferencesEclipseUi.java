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
//import gov.nasa.rapid.v2.preferences.IRapidPreferences;
//import gov.nasa.rapid.v2.ui.RapidV2UiActivator;
//import gov.nasa.rapid.v2.ui.RapidV2UiPreferences;
//
//import org.apache.log4j.Logger;
//import org.eclipse.jface.preference.IPreferenceStore;
//
//public class RapidPreferencesEclipseUi implements IRapidPreferences {
//    private static Logger logger = Logger.getLogger(RapidPreferencesEclipseUi.class);
//    
//    private static String prefString(String str) {
//        return RapidV2UiActivator.getDefault().getPreferenceStore().getString(str);
//    }
//
//    @Override
//    public String getDefaultLogDir() {
//        String retVal = prefString(RapidPreferenceKeys.P_DEFAULT_LOG_DIRECTORY);
//        return retVal;
//    }
//
//    public Agent getAgentOfInterest() {
//        final IPreferenceStore store = RapidV2UiActivator.getDefault().getPreferenceStore();
//        Agent retVal = null;
//        try {
//            retVal = Agent.valueOf(store.getString(RapidPreferenceKeys.P_AGENT_OF_INTEREST));
//        }
//        catch(IllegalArgumentException e) {
//            logger.debug("getAgentOfInterest() - "+e.getMessage());
//        }
//        if(retVal == null) { 
//            retVal = RapidV2UiPreferences.getDefaultAgentOfInterest();
//        }
//        if(retVal == null) { // this should not happen
//            throw new RuntimeException("getDefaultAgentOfInterest() failed in RapidV2UiPreferences. This should not happen.");
//        }
//        return retVal;
//    }
//
//    public void setAgentOfInterest(Agent agent) {
//        // TODO Auto-generated method stub
//        
//    }
//
//}
