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

import gov.nasa.dds.system.Dds;
import gov.nasa.dds.system.IDdsRestartListener;
import gov.nasa.rapid.v2.e4.system.builtin.ITopicPublicationListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.rti.dds.publication.builtin.PublicationBuiltinTopicData;

/**
 * Repository for partitions and agents 
 * @author mallan
 */
public class DiscoveredAgentRepository implements ITopicPublicationListener, IDdsRestartListener {
    private static final Logger logger = Logger.getLogger(DiscoveredAgentRepository.class);

    public static final DiscoveredAgentRepository INSTANCE = new DiscoveredAgentRepository();

    public class PartitionData {
        final HashMap<String,Integer> partitions = new HashMap<String,Integer>();
        final HashSet<Agent>          agents     = new HashSet<Agent>();
    }

    protected final HashMap<String,PartitionData>   m_partitionData     = new HashMap<String,PartitionData>(); // key=participantId
    protected final HashMap<String,HashSet<String>> m_partitionTopics   = new HashMap<String,HashSet<String>>();
    protected final List<IDiscoveredAgentListener>  m_listeners         = new LinkedList<IDiscoveredAgentListener>();
    protected final List<IDynamicAgentRecognizer>   m_dynAgentRecognizerList   = new LinkedList<IDynamicAgentRecognizer>();

    protected final List<Agent> m_ignoreAgents = new ArrayList<Agent>();

    public DiscoveredAgentRepository() {
        Dds.addRestartListener(this);
        addListener(new ActiveAgentSetUpdater());
    }

    /**
     * Clear out all discovered partitions and agents. Does not change listener list.
     */
    public void clear() {
        m_partitionData.clear();
        m_partitionTopics.clear();
    }

    public synchronized boolean addListener(IDiscoveredAgentListener listener) {
        if(!m_listeners.contains(listener)) {
            return m_listeners.add(listener);
        }
        return false;
    }

    public synchronized boolean removeListener(IDiscoveredAgentListener listener) {
        return m_listeners.remove(listener);
    }

    /**
     * @param rec
     * @return false if rec reference already exists in list
     */
    public synchronized boolean addDynamicAgentRecognizer(IDynamicAgentRecognizer rec) {
        if(m_dynAgentRecognizerList.contains(rec)) {
            return false;
        }
        return m_dynAgentRecognizerList.add(rec);
    }

    public synchronized boolean removeDynamicAgentRecognizer(IDynamicAgentRecognizer rec) {
        return m_dynAgentRecognizerList.remove(rec);
    }

    /**
     * Check if name matches an IDynamicAgentRecognizer. If so, create new Agent and return
     * @param name
     * @return
     */
    public Agent checkForDynamicAgent(String name) {
        Agent agent = null;
        for(IDynamicAgentRecognizer recognizer : getDynamicAgentRecognizers()) {
            if(recognizer.isAgentName(name)) {
                agent = recognizer.getAgent(name);
            }
        }
        return agent;
    }

    /**
     * copies IDynamicAgentRecognizer references into returned array
     * @return array of all IDynamicAgentRecognizers collected so far
     */
    public synchronized IDynamicAgentRecognizer[] getDynamicAgentRecognizers() {
        int num = m_dynAgentRecognizerList.size();
        IDynamicAgentRecognizer[] retVal = m_dynAgentRecognizerList.toArray(new IDynamicAgentRecognizer[num]);
        return retVal;
    }

    public void addIgnoreAgents(Collection<Agent> agents) {
        m_ignoreAgents.addAll(agents);
    }

    public void addIgnoreAgent(Agent agent) {
        m_ignoreAgents.add(agent);
    }

    // TODO: more accessors for ignore list

    /**
     * @return array of all partitions discovered thus far
     */
    public String[] getDiscoveredPartitions() {
        HashSet<String> retData = new HashSet<String>();
        for(PartitionData pd : m_partitionData.values()) {
            retData.addAll(pd.partitions.keySet());
        }
        String[] retVal = retData.toArray(new String[retData.size()]);
        Arrays.sort(retVal);
        return retVal;
    }

    /**
     * Get the set of agents discovered thus far
     * @return sorted array of Agents
     */
    public Agent[] getDiscoveredAgents() {
        HashSet<Agent> retData = new HashSet<Agent>();
        for(PartitionData pd : m_partitionData.values()) {
            pd.agents.removeAll(m_ignoreAgents);
            retData.addAll(pd.agents);
        }
        Agent[] retVal = retData.toArray(new Agent[retData.size()]);
        Arrays.sort(retVal, Agent.alphabeticalComparator);
        return retVal;
    }

    /**
     * @return sorted array of all partitions discovered thus far for participantId
     */
    public String[] getDiscoveredPartitions(String participantId) {
        PartitionData pd = m_partitionData.get(participantId);
        if(pd != null) {
            String[] retVal = pd.partitions.keySet().toArray(new String[pd.partitions.keySet().size()]);
            Arrays.sort(retVal);
            return retVal;
        }
        else {
            return new String[0];
        }
    }

    /**
     * Get all participant ids that have partitions
     */
    public String[] getParticipantIds() {
        Set<String> keys = m_partitionData.keySet();
        return keys.toArray(new String[keys.size()]);
    }

    /**
     * for a given Agent, return the Participant(s) which discovered them. 
     * @param agent
     * @return
     */
    public String[] getParticipants(Agent agent) {
        LinkedList<String> participantList = new LinkedList<String>();
        Set<String> keys = m_partitionData.keySet();
        for(String key : keys) {
            PartitionData pd = m_partitionData.get(key);
            if(pd.agents.contains(agent)) {
                participantList.add(key);
            }
        }
        return participantList.toArray(new String[participantList.size()]);
    }

    /**
     * @return sorted array of all agents discovered thus far for participantId
     */
    public Agent[] getDiscoveredAgents(String participantId) {
        PartitionData pd = m_partitionData.get(participantId);
        if(pd != null) {
            pd.agents.removeAll(m_ignoreAgents);
            Agent[] retVal = pd.agents.toArray(new Agent[pd.agents.size()]);
            Arrays.sort(retVal, Agent.alphabeticalComparator);
            return retVal;
        }
        else {
            return new Agent[0];
        }
    }

    /**
     * @param partition
     * @return sorted list of topics that have been discovered for the given partition
     */
    public String[] getTopicsFor(String partition) {
        Set<String> topics = m_partitionTopics.get(partition);
        if(topics != null) {
            String[] retVal = topics.toArray( new String[topics.size()] );
            Arrays.sort(retVal);
            return retVal;
        }
        return new String[0];
    }
    
    public synchronized void rediscoverAgents() {
    	for(final String participant : m_partitionData.keySet()) {
            rediscoverAgents(participant);
        }
    }

    public synchronized void rediscoverAgents(final String participantId) {
    	final PartitionData pd = m_partitionData.get(participantId);
    	final List<Agent> newAgents = new LinkedList<Agent>();

    	synchronized(m_partitionData) {
	    	for (final String partition : pd.partitions.keySet()) {
	            Agent agent = null;
	            try {
	                agent = Agent.valueOf(partition);
	            }
	            catch(IllegalArgumentException e) {
	                agent = checkForDynamicAgent(partition);
	            }
	            if(agent != null) {
	                if(!m_ignoreAgents.contains(agent)) {
	                    if(pd.agents.add(agent)) {
	                        newAgents.add(agent);
	                        logger.debug(participantId+": rediscovered new agent: "+agent.name());
	                    }
	                }
	            }
	    	}
    	}
    	if(newAgents.size() > 0) {
            for(IDiscoveredAgentListener listener : m_listeners) {
                try { 
                    listener.newAgentsDiscovered(participantId, newAgents);
                }
                catch(Throwable t) {
                    logger.warn("Exception while notifying "+listener.getClass().getSimpleName()+" of new partitions and/or Agents", t);
                }
            }
        }
    }

    @Override
    public synchronized void onTopicPublicationDiscovered(final String participantId, final PublicationBuiltinTopicData data) {
        final List<String> newPartitions = new LinkedList<String>();
        final List<Agent> newAgents = new LinkedList<Agent>();

        synchronized(m_partitionData) {
            PartitionData pd = m_partitionData.get(participantId);
            if(pd == null) {
                pd = new PartitionData();
                m_partitionData.put(participantId, pd);
            }
            final Map<String,Integer> partitions = pd.partitions;
            final Set<Agent>          agents     = pd.agents;

            for(Object obj : data.partition.name ) {
                String partition = (String)obj;

//                // increment count for every topic on given partition
//                Integer count = m_partitionTopicCount.get(partition);
//                if(count == null) {
//                    m_partitionTopicCount.put(partition, 1);
//                }
//                else {
//                    m_partitionTopicCount.put(partition, count+1);
//                }

                // add partition info 
                Integer count = partitions.get(partition);
                if(count == null) {
                    newPartitions.add(partition);
                    partitions.put(partition, 1);
                    logger.debug(participantId+": new partition: "+partition);
                }
                else {
                    partitions.put(partition, count+1);
                }
                HashSet<String> topics = m_partitionTopics.get(partition);
                if(topics == null) {
                    topics = new HashSet<String>();
                    m_partitionTopics.put(partition, topics);
                }
                topics.add(data.topic_name);
                //                if (false) {
                //                    ParticipantInfo pi = DiscoveredParticipants.getDiscoveredParticipantInfo(data.participant_key);
                //                    if (pi != null) {
                //                        logger.debug("topic " + data.topic_name + " is from participant " + pi.name);
                //                    }
                //                }
            }
            // go over *all* partitions, not just new ones because
            // new Agents may have been added that match old 
            // discovered partitions
            for(String partition : partitions.keySet()) {
                Agent agent = null;
                try {
                    agent = Agent.valueOf(partition);
                }
                catch(IllegalArgumentException e) {
                    agent = checkForDynamicAgent(partition);
                }
                if(agent != null) {
                    if(!m_ignoreAgents.contains(agent)) {
                        if(agents.add(agent)) {
                            newAgents.add(agent);
                            logger.debug(participantId+": new agent: "+agent.name());
                        }
                    }
                }
            }
        }
        if(newPartitions.size() > 0 || newAgents.size() > 0) {
            for(IDiscoveredAgentListener listener : m_listeners) {
                try { 
                    listener.newPartitionsDiscovered(participantId, newPartitions);
                    listener.newAgentsDiscovered(participantId, newAgents);
                }
                catch(Throwable t) {
                    logger.warn("Exception while notifying "+listener.getClass().getSimpleName()+" of new partitions and/or Agents", t);
                }
            }
        }
    }

    public void onTopicPublicationDisappeared(String participantId, PublicationBuiltinTopicData data) {
        synchronized(m_partitionData) {
            PartitionData pd = m_partitionData.get(participantId);
            if(pd == null) {
                logger.error("FATAL: PartitionData is null for "+participantId);
            }
            final Map<String,Integer> partitions = pd.partitions;
            final Set<Agent>          agents     = pd.agents;

            final List<String>  delPartitions = new LinkedList<String>();
            final List<Agent>   delAgents = new LinkedList<Agent>();

            for(Object obj : data.partition.name ) {
                String partition = (String)obj;
                // decrement count for every topic on given partition
                Integer count = partitions.get(partition);
                if(count == null) {
                    logger.error("NULL count in m_partitionTopicCount");
                }
                else {
                    int newCount = count - 1;
                    if(newCount > 0) {
                        partitions.put(partition, newCount);
                    }
                    else {
                        delPartitions.add(partition);
                        for(Agent agent : agents) {
                            if(agent.name().equals(partition)) {
                                delAgents.add(agent);
                                break;
                            }
                        }
                        logger.debug("Partition has no topic publications: "+partition);
                    }
                }
            }
            
            if(delPartitions.size() > 0 || delAgents.size() > 0) {
                for(String partition : delPartitions) {
                    partitions.remove(partition);
                }
                for(Agent agent : delAgents) {
                    agents.remove(agent);
                }
                for(IDiscoveredAgentListener listener : m_listeners) {
                    try { 
                        listener.partitionsDisappeared(participantId, delPartitions);
                        listener.agentsDisappeared(participantId, delAgents);
                    }
                    catch(Throwable t) {
                        logger.warn("Exception while notifying "+listener.getClass().getSimpleName()+" of new partitions and/or Agents", t);
                    }
                }
            }
        }
    }



    @Override
    public void onDdsStarted() throws Exception {
        // ignore
    }

    @Override
    public void onDdsAboutToStop() throws Exception {
        // ignore
    }

    @Override
    public void onDdsStopped() throws Exception {
        clear();
    }

}
