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
package gov.nasa.rapid.v2.e4.message.publisher;

import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.dds.exception.UncheckedDdsEntityException;
import gov.nasa.dds.rti.system.ContainedEntities;
import gov.nasa.dds.rti.system.DdsEntityFactory;
import gov.nasa.dds.system.Dds;
import gov.nasa.dds.system.DdsTask;
import gov.nasa.dds.system.IDdsRestartListener;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.IDdsWriterStatusListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.WriterStatus;
import gov.nasa.rapid.v2.e4.system.DataWriterCreator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.DataWriter;
import com.rti.dds.publication.LivelinessLostStatus;
import com.rti.dds.publication.OfferedDeadlineMissedStatus;
import com.rti.dds.publication.OfferedIncompatibleQosStatus;
import com.rti.dds.publication.PublicationMatchedStatus;
import com.rti.dds.publication.Publisher;

/**
 * The RapidMessagePublisher manages data writers and instance handles for a given Agent (partition)
 * Note that the RapidMessagePublisher assumes that each writer is only writing to a single 
 * data instance. 
 * @author mallan
 */
public class RapidMessagePublisher implements IDdsRestartListener {
    private static final Logger logger = Logger.getLogger(RapidMessagePublisher.class);

    protected final Agent m_agent;

    /** 
     * the DataWriters should NOT be exposed to any clients because they
     * are transitory. A DDS restart will destroy current writers and replace
     * them with new ones. 
     */
    private class WriterHolder {
        DataWriterCreator         creator  = null;
        DataWriter                writer   = null;
        DataWriterStatusForwarder forwarder = null;
        boolean                   createWriterOnRestart = false;

        void createWriter() throws DdsEntityCreationException {
            createWriterOnRestart = true;
            writer = creator.create();
            final int mask = (forwarder==null) ? StatusKind.STATUS_MASK_NONE : forwarder.mask();
            writer.set_listener(forwarder, mask);
        }
        /** destroys DataWriter but leaves listener intact */
        void destroyWriter() throws DdsEntityCreationException {
            createWriterOnRestart = false;
            ContainedEntities ce = DdsEntityFactory.getContainedEntities(creator.participantId);
            Publisher pub = ce.getPublisherForPartition(m_agent.name());
            pub.delete_datawriter(writer);
            writer = null;
            //handle = null;
        }
    }
    private class WriterMap extends HashMap<MessageType,WriterHolder> { /**/ }


    private final HashMap<String,WriterMap> m_writerMapMap = new HashMap<String,WriterMap>();
    private static final HashMap<Agent,RapidMessagePublisher> s_publishers = new HashMap<Agent,RapidMessagePublisher>();

    /** 
     * For ease of access and resource sharing, RapidMessagePublishers can be 
     * managed using the get(Agent) call rather than creating multiple publishers.  
     * @param agent
     * @return
     */
    public static RapidMessagePublisher get(Agent agent) {
        synchronized(s_publishers) {
            RapidMessagePublisher retVal = s_publishers.get(agent);
            if(retVal == null) {
                retVal = new RapidMessagePublisher(agent);
                s_publishers.put(agent, retVal);
            }
            return retVal;
        }
    }

    public RapidMessagePublisher(Agent agent) {
        m_agent = agent;
        Dds.addRestartListener(this);
    }

    public Agent getAgent() {
        return m_agent;
    }

    /**
     * @return NULL if writer does not exist yet, otherwise, retVal filled with status info
     */
    public Object getWriterStatus(WriterStatus statusType, String participantId, MessageType msgType, Object retVal) {
        final WriterMap    map    = m_writerMapMap.get(participantId);
        final WriterHolder holder = map.get(msgType);
        final DataWriter   writer = holder.writer;
        if(writer != null) {
            switch(statusType) {
            case LivelinessLost:
                writer.get_liveliness_lost_status((LivelinessLostStatus)retVal);
                break;
            case OfferedDeadlineMissed:
                writer.get_offered_deadline_missed_status((OfferedDeadlineMissedStatus)retVal);
                break;
            case OfferedIncompatibleQos:
                writer.get_offered_incompatible_qos_status((OfferedIncompatibleQosStatus)retVal);
                break;
            case PublicationMatched:
                writer.get_publication_matched_status((PublicationMatchedStatus)retVal);
                break;
            }
            return retVal;
        }
        return null;
    }

    private Object newWriterStatusObject(WriterStatus statusType) {
        switch(statusType) {
        case LivelinessLost:         return new LivelinessLostStatus();
        case OfferedDeadlineMissed:  return new OfferedDeadlineMissedStatus();
        case OfferedIncompatibleQos: return new OfferedIncompatibleQosStatus();
        case PublicationMatched:     return new PublicationMatchedStatus();
        }
        return null;
    }

    /** 
     * Add a writer status listener for this agent's message writer. If the writer does not yet
     * exist, the listener will be added upon creation. If writer already exists, status for 
     * the listener's requested types will be queried and sent to the listener's onWriterStatusReceived
     * method
     * @param participantId
     * @param msgType
     * @param listener
     */
    public void addDdsWriterStatusListener(final String participantId, 
                                           final MessageType msgType, 
                                           final IDdsWriterStatusListener listener) {
        final WriterHolder holder = getWriterHolder(participantId, msgType, false);
        holder.forwarder.addListener(listener);
        if(holder.writer != null) {
            holder.writer.set_listener(holder.forwarder, holder.forwarder.mask());
        }
        else {
            for(final WriterStatus statusType : listener.writerStatusSet()) {
                final Object statusObj = getWriterStatus(statusType, participantId, msgType, newWriterStatusObject(statusType));
                DdsTask.dispatchExec(new Runnable() {
                    public void run() {
                        listener.onWriterStatusReceived(m_agent.name(), msgType, statusType, statusObj);
                    }
                });
            }
        }
    }

    /**
     * Actively create a DDS DataWriter for the given msgType if is does not exist already. 
     * @param participantId
     * @param msgType
     * @return true if writer was created or already exists
     */
    public boolean createWriter(String participantId, MessageType msgType) {
        try {
            synchronized(m_writerMapMap) {
                getWriterHolder(participantId, msgType, true);
                return true;
            }
        }
        catch(UncheckedDdsEntityException e) {
            logger.error("Failed to create DataWriter: "+e.getMessage(), e);
            return false;
        }
    }

    /**
     * Destroy a DDS DataWriter for the given msgType if it exists.
     * @param participantId
     * @param msgType
     * @return
     */
    public boolean destroyWriter(String participantId, MessageType msgType) {
        try {
            synchronized(m_writerMapMap) {
                WriterMap map = m_writerMapMap.get(participantId);
                WriterHolder holder = map.get(msgType);
                if(holder != null) {
                    if(holder.writer != null) {
                        holder.destroyWriter();
                    }
                }
                return true;
            }
        }
        catch(DdsEntityCreationException e) {
            logger.error("Failed to destroy DataWriter: "+e.getMessage(), e);
            return false;
        }
    }

    /**
     * Write a DDS message in this Agent's partition. A DataWriter will be created if 
     * one does not already exist. 
     * @param participantId
     * @param msgType
     * @param msg
     * @returns true if message was written successfully
     */
    public boolean writeMessage(String participantId, MessageType msgType, Object msg) {
        try {
            synchronized(m_writerMapMap) {
                WriterHolder holder = getWriterHolder(participantId, msgType, true);
                //if(holder.handle == null) {
                //    holder.handle = holder.writer.register_instance_untyped(msg);
                //}
                //holder.writer.write_untyped(msg, holder.handle);
                holder.writer.write_untyped(msg, InstanceHandle_t.HANDLE_NIL);
                return true;
            }
        }
        catch(UncheckedDdsEntityException e) {
            logger.error("Failed to create DataWriter: "+e.getMessage(), e);
            return false;
        }
    }

    /**
     * get WriterHolder for participantId and msgType, create if requested and necessary
     * @param participantId
     * @param msgType
     * @param doCreate
     * @return
     * @throws DdsEntityCreationException
     */
    private WriterHolder getWriterHolder(String participantId, MessageType msgType, boolean doCreate) throws UncheckedDdsEntityException {
        WriterHolder retVal = null;
        final WriterMap writerMap = getWriterMap(participantId);
        retVal = writerMap.get(msgType);
        if(retVal == null) {
            retVal = new WriterHolder();
            retVal.creator = new DataWriterCreator(participantId, msgType, m_agent);
            retVal.forwarder = new DataWriterStatusForwarder(m_agent.name(), msgType);
            writerMap.put(msgType, retVal);
        }
        if(retVal.writer == null && doCreate) {
            try {
                retVal.createWriter();
            }
            catch(DdsEntityCreationException e) {
                throw new UncheckedDdsEntityException(e);
            }
        }
        return retVal;
    }

    /**
     * 
     */
    public void destroyAllWriters() {
        deleteAllWriters(true);
    }

    /**
     * 
     */
    private void deleteAllWriters(boolean doDestroy) {
        synchronized(m_writerMapMap) {
            for(String participantId : m_writerMapMap.keySet()) {
                try {
                    ContainedEntities ce = DdsEntityFactory.getContainedEntities(participantId);
                    if(ce != null) {
                        Publisher pub = ce.getPublisherForPartition(m_agent.name());
                        if(pub != null) {
                            WriterMap map = m_writerMapMap.get(participantId);
                            if(map != null) {
                                for(WriterHolder holder : map.values()) {
                                    if(holder.writer != null) {
                                        if(doDestroy) {
                                            holder.destroyWriter();
                                        }
                                        else {
                                            pub.delete_datawriter(holder.writer);
                                            holder.writer = null;
                                            //holder.handle = null;
                                            holder.createWriterOnRestart = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                catch (DdsEntityCreationException e) {
                    logger.error(e);
                }
            }
        }
    }

    @Override
    public void finalize() {
        Dds.removeRestartListener(this);
        destroyAllWriters();
    }

    /**
     * get WriterMap for participantId, create if necessary
     */
    private WriterMap getWriterMap(String participantId) {
        WriterMap retVal = m_writerMapMap.get(participantId);
        if(retVal == null) {
            retVal = new WriterMap();
            m_writerMapMap.put(participantId, retVal);
        }
        return retVal;
    }

    private List<WriterHolder> getAllWriterHolders() {
        List<WriterHolder> retVal = new LinkedList<WriterHolder>();
        for(WriterMap map : m_writerMapMap.values()) {
            retVal.addAll(map.values());
        }
        return retVal;
    }

    /**
     * recreate writers on DDS restart
     */
    @Override
    public void onDdsStarted() throws Exception {
        for(WriterHolder holder : getAllWriterHolders()) {
            if(holder.createWriterOnRestart) {
                try {
                    //logger.debug("create writer "+holder.creator.toString());
                    holder.createWriter();
                }
                catch(DdsEntityCreationException e) {
                    logger.error("Failed to create DataWriter: "+e.getMessage(), e);
                }
            }
        }
    }

    /**
     * explicitly delete all DataWriters 
     */
    @Override
    public void onDdsAboutToStop() throws Exception {
        deleteAllWriters(false);
    }

    @Override
    public void onDdsStopped() throws Exception {
        // nothing
    }
}
