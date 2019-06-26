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
package gov.nasa.rapid.v2.ui.e4;

import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.ui.e4.preferences.CameraPreferenceKeys;
import gov.nasa.rapid.v2.ui.e4.preferences.RapidPreferenceKeys;

import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Accessor for RAPID UI Preferences
 * @author mallan
 */
public class RapidV2UiPreferences {
    private static Logger logger = Logger.getLogger(RapidV2UiPreferences.class);

	private IEclipsePreferences prefs;
	
	// doing this because static injection is iffy.
	static private RapidV2UiPreferences INSTANCE;
	
	@Inject
	public RapidV2UiPreferences(@Preference IEclipsePreferences preferences) {
		prefs = preferences;
		INSTANCE = this;
	}

    private static String prefString(String str) {
//        return RapidV2UiActivator.getDefault().getPreferenceStore().getString(str);
		return INSTANCE.prefs.get(str, "");
    }
    
//    public static Agent[] getActiveAgents() {
//        String str = prefString(RapidPreferenceKeys.P_AGENT_SET);
//        return Agent.toArray(str, DdsPreferenceKeys.LIST_SEPARATOR);
//    }
//    
//    public static void setActiveAgents(Agent[] agents) {
//        String str = Agent.toString(agents, DdsPreferenceKeys.LIST_SEPARATOR);
//        final IPreferenceStore store = RapidV2UiActivator.getDefault().getPreferenceStore();
//        store.setValue(RapidPreferenceKeys.P_AGENT_SET, str);
//    }
//    
//    public static void setActiveAgents(Collection<Agent> agents) {
//        String str = Agent.toString(agents, DdsPreferenceKeys.LIST_SEPARATOR);
//        final IPreferenceStore store = RapidV2UiActivator.getDefault().getPreferenceStore();
//        store.setValue(RapidPreferenceKeys.P_AGENT_SET, str);
//    }
    
    public static Agent getDefaultAgentOfInterest() {
//        final IPreferenceStore store = RapidV2UiActivator.getDefault().getPreferenceStore();
//        Agent retVal = Agent.valueOf(store.getDefaultString(RapidPreferenceKeys.P_AGENT_OF_INTEREST));
//        return retVal;
		Agent retVal = Agent.valueOf(INSTANCE.prefs.get(RapidPreferenceKeys.P_AGENT_OF_INTEREST, ""));
		return retVal;
    }

	public static Agent getAgentOfInterest() {
		//    	Preferences preferences = (Preferences) InstanceScope.INSTANCE.getNode("gov.nasa.rapid.v2.e4.ui.e4.parts");
		//    	Preferences rapidPreferences = preferences.node("rapid");
		//    	Agent retVal = Agent.valueOf(rapidPreferences.get(RapidPreferenceKeys.P_AGENT_OF_INTEREST, null));

		Agent retVal = Agent.valueOf(INSTANCE.prefs.get(RapidPreferenceKeys.P_AGENT_OF_INTEREST, null));
		if(retVal == null) { // this shouldn't happen
			retVal = getDefaultAgentOfInterest();
		}
		if(retVal == null) { // this should never happen
			throw new RuntimeException("getDefaultAgentOfInterest() failed in RapidV2UiPreferences. This should never happen.");
		}
		return retVal;
	}

    
    public static String[] getDefaultCameraNames() {
        StringTokenizer st = new StringTokenizer(prefString(CameraPreferenceKeys.DEFAULT_CAMERAS), CameraPreferenceKeys.DEFAULT_CAMERA_SEPARATOR);
        ArrayList<String> retVal = new ArrayList<String>();
        while(st.hasMoreTokens()) {
            String cam = st.nextToken();
            if(cam != null && cam.length() > 0) {
                retVal.add(cam);
            }
        }
        return retVal.toArray(new String[retVal.size()]);
    }

    /**
     * Store the specified Agent as a preference
     * @param agent
     */
    public static void setAgentOfInterest(Agent agent) {
//        if(agent != null) {
//            final IPreferenceStore store = RapidV2UiActivator.getDefault().getPreferenceStore();
//            store.setValue(RapidPreferenceKeys.P_AGENT_OF_INTEREST, agent.name());
//        }
		if(agent != null) {
			//        	Preferences preferences = (Preferences) InstanceScope.INSTANCE.getNode("gov.nasa.rapid.v2.e4.ui.e4.parts");
			//        	Preferences rapidPreferences = preferences.node("rapid");
			//        	rapidPreferences.put(RapidPreferenceKeys.P_AGENT_OF_INTEREST, agent.name());
			INSTANCE.prefs.put(RapidPreferenceKeys.P_AGENT_OF_INTEREST, agent.name());

//			 this try statement wasn't in the original
			        	try {
			        		// forces the application to save the preferences
			//        		preferences.flush();
			        		INSTANCE.prefs.flush();
			        	} catch (BackingStoreException e) {
			        		e.printStackTrace();
						}
		} 

    }
    
    /**
     * Set the default agent of interest in preferences (i.e. the one that will be 
     * returned when the preference has not been set by the user). 
     * @param agent
     */
//    public static void setDefaultAgentOfInterest(Agent agent) {
//        if(agent != null) {
//            final IPreferenceStore store = RapidV2UiActivator.getDefault().getPreferenceStore();
//            store.setDefault(RapidPreferenceKeys.P_AGENT_OF_INTEREST, agent.name());
//        }
//    }
	public static void setDefaultAgentOfInterest(Agent agent) {
		if(agent != null) {
			//        	Preferences preferences = (Preferences) InstanceScope.INSTANCE.getNode("gov.nasa.rapid.v2.e4.ui.e4.parts");
			//        	Preferences rapidPreferences = preferences.node("rapid");
			//        	rapidPreferences.put(RapidPreferenceKeys.P_AGENT_OF_INTEREST, agent.name());
			//        	
			//        	try {
			//        		// forces the application to save the preferences
			//        		preferences.flush();
			//        	} catch (BackingStoreException e) {
			//        		e.printStackTrace();
			//			}
			INSTANCE.prefs.put(RapidPreferenceKeys.P_AGENT_OF_INTEREST, agent.name());
        	try {
        		// forces the application to save the preferences
//        		preferences.flush();
        		INSTANCE.prefs.flush();
        	} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		} 
	}
	
    public static void add(String key, String value) {
//    	Preferences preferences = (Preferences) InstanceScope.INSTANCE.getNode("gov.nasa.rapid.v2.ui.e4.parts");
//    	Preferences rapidPreferences = preferences.node("rapid");
//    	rapidPreferences.put(key, value);
//    	
//    	try {
//    		// forces the application to save the preferences
//    		preferences.flush();
//    	} catch (BackingStoreException e) {
//    		e.printStackTrace();
//		}
    	INSTANCE.prefs.put(key, value);
    	try {
    		// forces the application to save the preferences
//    		preferences.flush();
    		INSTANCE.prefs.flush();
    	} catch (BackingStoreException e) {
    		e.printStackTrace();
		}
    }


	
    public static String get(String key) {
    	return prefString(key);
    }

}
