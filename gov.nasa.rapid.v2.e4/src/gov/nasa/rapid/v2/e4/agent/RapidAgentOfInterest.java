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
package gov.nasa.rapid.v2.e4.agent;

import gov.nasa.rapid.v2.e4.preferences.RapidPreferences;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * The RAPID Agent of Interest is the primary Agent that the UI
 * is interested in at any given time. Although many agents may be 
 * monitored by the components of the UI, detail views should 
 * dynamically track the most interesting agent. 
 * In the case of multiple views, each displaying the state of 
 * a different Agent, those views should implement "pin" functionality
 * so they can either track the Agent of Interest, or be set explicitly
 * by the user. 
 * @author mallan
 *
 */
public class RapidAgentOfInterest {
    private static final RapidAgentOfInterest s_instance = new RapidAgentOfInterest();

    private Agent   m_aoi = RapidPreferences.getAgentOfInterest();
    private final ArrayList<WeakReference<IAgentOfInterestListener>> m_aoiListeners = 
        new ArrayList<WeakReference<IAgentOfInterestListener>>();
    private final ArrayList<WeakReference<IAgentOfInterestListener>> m_removalList = 
        new ArrayList<WeakReference<IAgentOfInterestListener>>();

    /**
     * @return the RAPID Agent that the UI is currently focused on. This should never return null. 
     */
    public static Agent getAgentOfInterest() {
        synchronized(s_instance) {
            return s_instance.m_aoi;
        }
    }

    /**
     * Set the Agent of interest for the UI and notify all listeners
     * Will only notify listeners if the agent has changed (i.e. setting
     * the same Agent multiple times is a no-op)
     * NOTE: This only holds weak references, so they will disappear unless
     * they are referred to elsewhere
     * @throws IllegalArgumentException if agent is null
     */
    public static void setAgentOfInterest(Agent agent) {
        if(agent == null) {
            throw new IllegalArgumentException("Agent reference must not be null");
        }
        // store the agent of interest in preferences
        RapidPreferences.setAgentOfInterest(agent);
        // set agent and notify listeners
        synchronized(s_instance) {
            if(agent != s_instance.m_aoi) {
                s_instance.m_aoi = agent;
                for(WeakReference<IAgentOfInterestListener> listenerRef : s_instance.m_aoiListeners) {
                    final IAgentOfInterestListener listener = listenerRef.get();
                    if(listener != null) {
                        listener.onAgentOfInterestChanged(agent);
                    }
                }
            }
        }
    }

    /**
     * add a new IRapidAgentOfInterestListener. If the listener is already in the list, 
     * it will not be added a second time. 
     * @throws IllegalArgumentException if listener is null
     */
    public static boolean addListener(IAgentOfInterestListener listener) {
        if(listener == null) {
            throw new IllegalArgumentException("IAgentOfInterestListener reference must not be null");
        }
        synchronized(s_instance) {
            for(WeakReference<IAgentOfInterestListener> listenerRef : s_instance.m_aoiListeners) {
                final IAgentOfInterestListener l = listenerRef.get();
                if(l != null) {
                    if(l == listener) { // if reference is already in list, return
                        return false;
                    }
                }
            }
            return s_instance.m_aoiListeners.add(new WeakReference<IAgentOfInterestListener>(listener));
        }
    }

    /**
     * Remove a IRapidAgentOfInterestListener from the listener list. 
     * Also, clear out any weak listener references that are no longer valid. 
     * Calling with listener==null will clear any invalid weak references. 
     */
    public static boolean removeListener(IAgentOfInterestListener listener) {
        boolean retVal = false;
        synchronized(s_instance) {
            for(WeakReference<IAgentOfInterestListener> listenerRef : s_instance.m_aoiListeners) {
                final IAgentOfInterestListener l = listenerRef.get();
                if(l != null) {
                    if(l == listener) {
                        s_instance.m_removalList.add(listenerRef); // if reference matches, add to removal list
                        retVal = true;
                    }
                }
                else {
                    s_instance.m_removalList.add(listenerRef); // if reference is no longer valid, add to removal list
                }
            }
            s_instance.m_aoiListeners.removeAll(s_instance.m_removalList);
            s_instance.m_removalList.clear();
        }
        return retVal;
    }
}
