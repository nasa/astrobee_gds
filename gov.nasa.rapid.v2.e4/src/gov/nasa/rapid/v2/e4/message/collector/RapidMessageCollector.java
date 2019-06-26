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
package gov.nasa.rapid.v2.e4.message.collector;

import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.exception.NonExistentEntity;
import gov.nasa.rapid.v2.e4.exception.NotSubscribedException;
import gov.nasa.rapid.v2.e4.message.IDdsInstanceListener;
import gov.nasa.rapid.v2.e4.message.IDdsMessageListener;
import gov.nasa.rapid.v2.e4.message.IDdsReaderStatusListener;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.ReaderStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.rti.dds.infrastructure.Copyable;
import com.rti.dds.infrastructure.InstanceHandle_t;

public class RapidMessageCollector {
    public static final RapidMessageCollector INSTANCE = new RapidMessageCollector();

    protected boolean m_synchronousDispatch = true;

    public class CollectorHash extends HashMap<String, PartitionedMessageCollector> {
        public CollectorHash() {
            super();
        }
    }

    protected final HashMap<String, CollectorHash> m_collectors = new HashMap<String, CollectorHash>();
    protected boolean isMeasureSizes = false;

    /**
     * 
     */
    public static RapidMessageCollector instance() {
        return INSTANCE;
    }

    /**
     * ctor
     */
    private RapidMessageCollector() {
        // do nothing
    }

    /**
     * set message distribution to be either synchronous (message is delivered from single thread)
     * or asyncronous (message delivered from thread pool)
     * @param state
     */
    public void setSynchronousDispatch(boolean state) {
        m_synchronousDispatch = state;
        for(CollectorHash ch : m_collectors.values()) {
            for(PartitionedMessageCollector collector : ch.values()) {
                collector.setSynchronousDispatch(state);
            }
        }
    }
    
    /**
     * Get status information from a message reader, if it exists. Caller must pass in a 
     * struct that matches the status requested (e.g. LivelinessChangedStatus stuct for 
     * ReaderStatus.LivelinessChanged)
     * @param status reader status being requested
     * @param participantId 
     * @param agent
     * @param msgType message type for which we want to request status 
     * @param retVal DDS status struct. Must not be null. 
     * @return retVal 
     * @throws NonExistentEntity if a reader for the given type does not exist
     */

    public Object getRapidMessageListenerStatus(ReaderStatus status,
                                                String participantId, 
                                                Agent agent, 
                                                MessageType msgType,
                                                Object retVal) throws NonExistentEntity {
        PartitionedMessageCollector pec = getPartitionedEventCollector(participantId, agent.name());
        return pec.getRapidMessageListenerStatus(status, msgType, retVal);
    }

    /**
     * Get the last message (if any) of a given type. If a DataReader for the message type
     * does not exist (i.e., the topic has not been subscribed to), a NotSubscribedException
     * will be thrown.
     * @param participantId
     * @param agent
     * @param msgType
     * @return
     * @throws NotSubscribedException
     */
    public Object getLastMessage(String participantId, 
                                 Agent agent, 
                                 MessageType msgType) throws NotSubscribedException {
        PartitionedMessageCollector pec = getPartitionedEventCollector(participantId, agent.name());
        return pec.getLastMessage(msgType);
    }

    public Map<InstanceHandle_t,Object> getLastMessages(String participantId, 
                                                        String partition, 
                                                        MessageType msgType) throws NotSubscribedException {
        PartitionedMessageCollector pec = getPartitionedEventCollector(participantId, partition);
        return pec.getLastMessages(msgType);
    }

    public Set<MessageType> getSubscribedMessageTypes(String participantId, String partition) {
        PartitionedMessageCollector pec = getPartitionedEventCollector(participantId, partition);
        return pec.getSubscribedMessageTypes();
    }

    public void printDebugInfo(String participantId, 
                               Agent agent, 
                               MessageType msgType) {
        PartitionedMessageCollector pec = getPartitionedEventCollector(participantId, agent.name());
        pec.printDebugInfo(msgType);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * 
     * @param participantId
     * @param agent
     * @param msgType
     * @param listener
     */
    public void addRapidMessageListener(String participantId, 
                                        Agent agent, 
                                        MessageType msgType, 
                                        IRapidMessageListener listener) {
        PartitionedMessageCollector pec = getPartitionedEventCollector(participantId, agent.name());
        pec.addRapidMessageListener(msgType, listener);
    }

    /**
     * 
     * @param participantId
     * @param agents array of agents to subscribe to
     * @param msgType
     * @param listener
     */
    public void addRapidMessageListener(String participantId, 
                                        Agent[] agents, 
                                        MessageType msgType, 
                                        IRapidMessageListener listener) {
        for(Agent agent : agents) {
            addRapidMessageListener(participantId, agent, msgType, listener);
        }
    }

    /**
     * 
     * @param participantId
     * @param agent
     * @param msgTypes - array of msgType to subscribe to
     * @param listener
     */
    public void addRapidMessageListener(String participantId, 
                                        Agent agent, 
                                        MessageType[] msgTypes, 
                                        IRapidMessageListener listener) {
        for(MessageType msgType : msgTypes){
            addRapidMessageListener(participantId, agent, msgType, listener);
        }
    }

    public boolean removeRapidMessageListener(String participantId, 
                                              Agent agent,
                                              MessageType msgType, 
                                              IRapidMessageListener listener) {
        PartitionedMessageCollector pec = getPartitionedEventCollector(participantId, agent.name());
        return pec.removeRapidMessageListener(msgType, listener);
    }

    public int removeRapidMessageListener(String participantId, 
                                          Agent agent,
                                          IRapidMessageListener listener) {
        PartitionedMessageCollector pec = getPartitionedEventCollector(participantId, agent.name());
        return pec.removeRapidMessageListener(listener);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * 
     * @param participantId
     * @param agent
     * @param msgType
     * @param listener
     */
    public void addDdsMessageListener(String participantId, 
                                      String partition, 
                                      MessageType msgType, 
                                      IDdsMessageListener listener) {
        PartitionedMessageCollector pec = getPartitionedEventCollector(participantId, partition);
        pec.addDdsMessageListener(msgType, listener);
    }

    public boolean removeDdsMessageListener(String participantId, 
                                            String partition,
                                            MessageType msgType, 
                                            IDdsMessageListener listener) {
        PartitionedMessageCollector pec = getPartitionedEventCollector(participantId, partition);
        return pec.removeDdsMessageListener(msgType, listener);
    }

    /**
     * remove listener from specified partition under specified participant 
     * @param participantId
     * @param partition
     * @param listener
     * @return
     */
    public int removeDdsMessageListener(String participantId, 
                                        String partition,
                                        IDdsMessageListener listener) {
        PartitionedMessageCollector pec = getPartitionedEventCollector(participantId, partition);
        return pec.removeDdsMessageListener(listener);
    }

    /**
     * remove listener from all partitions under Participant participantId
     * @param participantId
     * @param listener
     * @return
     */
    public int removeDdsMessageListener(String participantId, 
                                        IDdsMessageListener listener) {
        int count = 0;
        synchronized(m_collectors) {
            CollectorHash participantCollectors = m_collectors.get(participantId);
            for(PartitionedMessageCollector pec : participantCollectors.values()) {
                count += pec.removeDdsMessageListener(listener);
            }
        }
        return count;
    }

    /**
     * remove listener by iterating through all participants and all partitions
     * @param listener
     * @return
     */
    public int removeDdsMessageListener(IDdsMessageListener listener) {
        int count = 0;
        synchronized(m_collectors) {
            for(CollectorHash participantCollectors : m_collectors.values()) {
                for(PartitionedMessageCollector pec : participantCollectors.values()) {
                    count += pec.removeDdsMessageListener(listener);
                }
            }
        }
        return count;
    }

    //----------------------------------------------------------------------------------------------

    public void getDdsReaderStatus(String participantId, 
                                   String partition, 
                                   MessageType msgType, 
                                   Object status) throws IllegalArgumentException, NonExistentEntity {
        PartitionedMessageCollector pec = getPartitionedEventCollector(participantId, partition);
        pec.getDdsReaderStatus(msgType, status);
    }

    /**
     * 
     * @param participantId
     * @param agent
     * @param msgType
     * @param listener
     */
    public void addDdsReaderStatusListener(String participantId, 
                                           String partition, 
                                           MessageType msgType, 
                                           IDdsReaderStatusListener listener) {
        PartitionedMessageCollector pec = getPartitionedEventCollector(participantId, partition);
        pec.addDdsReaderStatusListener(msgType, listener);
    }

    public boolean removeDdsReaderStatusListener(String participantId, 
                                                 String partition,
                                                 MessageType msgType, 
                                                 IDdsReaderStatusListener listener) {
        PartitionedMessageCollector pec = getPartitionedEventCollector(participantId, partition);
        return pec.removeDdsReaderStatusListener(msgType, listener);
    }

    /**
     * remove listener from specified partition under specified participant 
     * @param participantId
     * @param partition
     * @param listener
     * @return
     */
    public int removeDdsReaderStatusListener(String participantId, 
                                             String partition,
                                             IDdsReaderStatusListener listener) {
        PartitionedMessageCollector pec = getPartitionedEventCollector(participantId, partition);
        return pec.removeDdsReaderStatusListener(listener);
    }

    /**
     * remove listener from all partitions under Participant participantId
     * @param participantId
     * @param listener
     * @return
     */
    public int removeDdsReaderStatusListener(String participantId, 
                                             IDdsReaderStatusListener listener) {
        int count = 0;
        synchronized(m_collectors) {
            CollectorHash participantCollectors = m_collectors.get(participantId);
            for(PartitionedMessageCollector pec : participantCollectors.values()) {
                count += pec.removeDdsReaderStatusListener(listener);
            }
        }
        return count;
    }

    /**
     * remove listener by iterating through all participants and all partitions
     * @param listener
     * @return
     */
    public int removeDdsReaderStatusListener(IDdsReaderStatusListener listener) {
        int count = 0;
        synchronized(m_collectors) {
            for(CollectorHash participantCollectors : m_collectors.values()) {
                for(PartitionedMessageCollector pec : participantCollectors.values()) {
                    count += pec.removeDdsReaderStatusListener(listener);
                }
            }
        }
        return count;
    }

    //----------------------------------------------------------------------------------------------

    public void addDdsInstanceListener(String participantId, 
                                       String partition, 
                                       MessageType msgType, 
                                       IDdsInstanceListener listener) {
        PartitionedMessageCollector pec = getPartitionedEventCollector(participantId, partition);
        pec.addDdsInstanceListener(msgType, listener);
    }

    public boolean removeDdsInstanceListener(String participantId, 
                                             String partition,
                                             MessageType msgType, 
                                             IDdsInstanceListener listener) {
        PartitionedMessageCollector pec = getPartitionedEventCollector(participantId, partition);
        return pec.removeDdsInstanceListener(msgType, listener);
    }

    //----------------------------------------------------------------------------------------------

    /**
     * Inject a message into the RapidMessageCollector. The message will be delivered to 
     * listeners in the same manner as DDS messages
     * @param participantId
     * @param partition
     * @param msgType
     * @param msg
     */
    public void injectMessage(String participantId, String partition, MessageType msgType, Object msgObj) {
        PartitionedMessageCollector pec = getPartitionedEventCollector(participantId, partition);
        MessageListener msgListener = pec.getMessageListener(msgType);
        { // copy message so data cannot get corrupted while waiting to be delivered
            Copyable copyFrom = (Copyable)msgObj;
            Copyable copyTo   = (Copyable)msgType.newDataTypeInstance();
            copyTo.copy_from(copyFrom);
            msgObj = copyTo;
        }
        msgListener.setLastSample(msgObj);
        pec.newMessage(msgType, msgObj);
    }

    /**
     * Inject a message into the RapidMessageCollector. The message will be delivered to 
     * listeners in the same manner as DDS messages
     * @param participantId
     * @param partition
     * @param msgType
     * @param msg
     */
    public void injectMessage(String participantId, Agent agent, MessageType msgType, Object msgObj) {
        PartitionedMessageCollector pec = getPartitionedEventCollector(participantId, agent.name());
        MessageListener msgListener = pec.getMessageListener(msgType);
        { // copy message so data cannot get corrupted while waiting to be delivered
            Copyable copyFrom = (Copyable)msgObj;
            Copyable copyTo   = (Copyable)msgType.newDataTypeInstance();
            copyTo.copy_from(copyFrom);
            msgObj = copyTo;
        }
        msgListener.setLastSample(msgObj);
        pec.newMessage(msgType, msgObj);
    }

    //----------------------------------------------------------------------------------------------

    public Map<MessageType,QosPolicyCounts> getIncompatibleQosRequests(String participantId, String partition) {
        PartitionedMessageCollector pmc = getPartitionedEventCollector(participantId, partition);
        return pmc.getIncompatibleQosRequests();
    }

    /**
     * get a collector, create if necessary
     * @param participantId
     * @param partition
     * @return never returns null
     */
    protected synchronized PartitionedMessageCollector getPartitionedEventCollector(String participantId, String partition) {
        PartitionedMessageCollector retVal = null;
        synchronized(m_collectors) {
            CollectorHash participantCollectors = m_collectors.get(participantId);
            if(participantCollectors == null) {
                participantCollectors = new CollectorHash();
                m_collectors.put(participantId, participantCollectors);
            }
            retVal = participantCollectors.get(partition);
            if(retVal == null) {
                retVal = new PartitionedMessageCollector(participantId, partition, m_synchronousDispatch);
                participantCollectors.put(partition, retVal);
            }
        }
        return retVal;
    }

    public void writeMeasuredSizes() {
        if(isMeasureSizes) {
            for(String participantId : m_collectors.keySet()) {
                for(String partition : m_collectors.get(participantId).keySet()) {
                    PartitionedMessageCollector collector = m_collectors.get(participantId).get(partition);
                    collector.writeSizes(participantId);
                }
            }
        }
    }
}


