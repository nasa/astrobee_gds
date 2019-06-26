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
/**
 * 
 */
package gov.nasa.rapid.v2.e4.agent;

import gov.nasa.rapid.v2.e4.Rapid;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

/**
 * Holds the set of Agents that we are interested in monitoring and/or commanding
 * TODO: discuss with others about how this is to be used. It might be
 * useful to have this backed by a preference store, but synchronizing 
 * with preference default/user selected will be tricky if the implementation
 * stays the same. Ideally, the ActiveAgentSet would listen to discovery messages
 * and add agents when it detects new partitions, but then we have the issue of 
 * Agents with dynamic names (i.e. operators) - we would not want to persist those...
 */
public class ActiveAgentSet implements IAgentOfInterestListener {
    private static final Logger logger = Logger.getLogger(ActiveAgentSet.class);

    public static final ActiveAgentSet INSTANCE = new ActiveAgentSet();

    private final HashMap<Agent,Integer> m_agentSet = new HashMap<Agent,Integer>();
    private final LinkedList<IActiveAgentSetListener> m_listeners = new LinkedList<IActiveAgentSetListener>();

    private final HashSet<Agent> m_toRemove = new HashSet<Agent>();
    
    protected ActiveAgentSet() {
        RapidAgentOfInterest.addListener(this);
    }

    public synchronized void addListener(IActiveAgentSetListener listener) {
        if(!m_listeners.contains(listener))
            m_listeners.add(listener);
        listener.activeAgentSetChanged();
    }

    public synchronized boolean removeListener(IActiveAgentSetListener listener) {
        return m_listeners.remove(listener);
    }

    private void notifyChanged() {
        IActiveAgentSetListener[] listeners;
        listeners = m_listeners.toArray(new IActiveAgentSetListener[m_listeners.size()]);
        for(IActiveAgentSetListener listener : listeners) {
            try {
                listener.activeAgentSetChanged();
            }
            catch(Throwable t) {
                logger.warn("Exception while notifying "+listener.getClass().getSimpleName()+" that active agent set has changed", t);
            }
        }
    }

    private void notifyRemoved(Agent agent) {
        for(IActiveAgentSetListener listener : m_listeners) {
            listener.activeAgentRemoved(agent);
        }
    }

    private void notifyAdded(Agent agent, String participantId) {
        for(IActiveAgentSetListener listener : m_listeners) {
            listener.activeAgentAdded(agent, participantId);
        }
    }

    public synchronized boolean addAgent(Agent agent, String participantId) {
        return addAgent(agent, participantId, true);
    }

    public boolean addAgent(Agent agent) {
    	return addAgent(agent, Rapid.DefaultParticipant, true);
    }
    
    /**
     * add an Agent to the set. 
     * @returns false if Agent is already in the set
     */
    private boolean addAgent(Agent agent, String participantId, boolean doNotifyChanged) {
        if(agent == null) {
            logger.warn("Attempt to add null agent to ActiveAgentSet. Rejected.");
            return false;
        }
        Integer count = m_agentSet.get(agent);
        if(count != null) {
            m_agentSet.put(agent, count+1);
        }
        else {
            m_agentSet.put(agent, 1);
            notifyAdded(agent, participantId);
            if(doNotifyChanged) {
                notifyChanged();
            }
        }
        return (count == null);
    }

    /**
     * add all Agents in the agents array
     * @returns true if set changed as a result of this call
     */
    public synchronized boolean addAll(Agent[] agents) {
        boolean changed = false;
        for(Agent agent : agents) {
            if(addAgent(agent, Rapid.DefaultParticipant, false)) {
                notifyAdded(agent, Rapid.DefaultParticipant);
                changed = true;
            }
        }
        if(changed) {
            notifyChanged();
        }
        return changed;
    }

    /**
     * add all Agents in the agents collection
     * @return true if set changed as a result of this call
     */
    public synchronized boolean addAll(Collection<Agent> agents) {
        boolean changed = false;
        for(Agent agent : agents) {
            if(addAgent(agent, Rapid.DefaultParticipant, false)) {
                notifyAdded(agent, Rapid.DefaultParticipant);
                changed = true; 
            }
        }
        if(changed) {
            notifyChanged();
        }
        return changed;
    }

    /**
     * clear current list of agents and add all agents
     * @param agents
     */
    public synchronized void set(Agent... agents) {
        clear();
        addAll(agents);
        notifyChanged();
    }

    /**
     * clear current list of agents and add all agents
     * @param agents
     */
    public synchronized void set(Collection<Agent> agents) {
        clear();
        addAll(agents);
        notifyChanged();
    }


    /**
     * clear the set of Agents
     * @return true if set changed as a result of this call
     */
    private boolean clear() {
        boolean changed = m_agentSet.size() > 0;
        for(Agent agent : m_agentSet.keySet()) {
            notifyRemoved(agent);
        }
        m_agentSet.clear();
        m_toRemove.clear();
        if(changed) {
            notifyChanged();
        }
        return changed;
    }

    /**
     * Remove an Agent from the set. Note that this decrements the 
     * reference count and only actually removes the agent if that
     * count goes to 0. Also note that the current AgentOfInterest 
     * cannot be removed immediately (but will happen when 
     * AgentOfInterest changes)
     * @return true if agent reference count reached 0 and was removed
     */
    public synchronized boolean removeAgent(Agent agent) {
        Integer count = m_agentSet.get(agent);
        if(count == null) {
            logger.warn("Attempted to remove Agent that was not in ActiveAgentSet");
            return false;
        }
        else if(count > 1) {
            m_agentSet.put(agent, count - 1);
            return false;
        }
        else {
            if(agent.equals(RapidAgentOfInterest.getAgentOfInterest())) {
                m_toRemove.add(agent);
                return false;
            }
            m_agentSet.remove(agent);
            notifyRemoved(agent);
            notifyChanged();
            return true;
        }
    }

    //-- static methods --------------------------------------

    public static int size() {
        return INSTANCE.m_agentSet.size();
    }

    public static boolean contains(Agent agent) {
        return INSTANCE.m_agentSet.keySet().contains(agent);
    }

    public static Set<Agent> values() {
        HashSet<Agent> retVal = new HashSet<Agent>();
        retVal.addAll(INSTANCE.m_agentSet.keySet());
        return retVal;
    }

    public static Agent[] asArray() {
        return INSTANCE.m_agentSet.keySet().toArray(new Agent[INSTANCE.m_agentSet.size()]);
    }

    public static List<Agent> asList(){
    	List<Agent> list = new CopyOnWriteArrayList<Agent>();
    	list.addAll(INSTANCE.m_agentSet.keySet());
    	return list;
    }

    	
    /**
     *
     */
    public void onAgentOfInterestChanged(Agent agent) {
        for(Agent r : m_toRemove) {
            if(!r.equals(RapidAgentOfInterest.getAgentOfInterest())) {
                m_toRemove.remove(r);
                m_agentSet.remove(r);
                notifyRemoved(r);
                notifyChanged();
            }
        }
    }

}
