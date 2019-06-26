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

import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.dds.rti.system.ContainedEntities;
import gov.nasa.dds.rti.system.DdsEntityFactory;
import gov.nasa.dds.rti.util.TypeSupportUtil;
import gov.nasa.dds.system.Dds;
import gov.nasa.dds.system.DdsTask;
import gov.nasa.dds.system.IDdsRestartListener;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.exception.NonExistentEntity;
import gov.nasa.rapid.v2.e4.exception.NotSubscribedException;
import gov.nasa.rapid.v2.e4.message.IDdsInstanceListener;
import gov.nasa.rapid.v2.e4.message.IDdsMessageListener;
import gov.nasa.rapid.v2.e4.message.IDdsReaderStatusListener;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.MessageType.Category;
import gov.nasa.rapid.v2.e4.message.ReaderStatus;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.log4j.Logger;

import com.google.common.collect.Maps;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderQos;
import com.rti.dds.subscription.LivelinessChangedStatus;
import com.rti.dds.subscription.RequestedDeadlineMissedStatus;
import com.rti.dds.subscription.RequestedIncompatibleQosStatus;
import com.rti.dds.subscription.SampleLostStatus;
import com.rti.dds.subscription.SampleRejectedStatus;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriptionMatchedStatus;

/**
 * @author mallan
 */
public class PartitionedMessageCollector implements IMessageReceiver, IDdsRestartListener {
    private static final Logger logger = Logger.getLogger(PartitionedMessageCollector.class);

    protected final Agent  m_agent;
    protected final String m_partition;
    protected final String m_participantId;

    protected MessageListenerFactory m_messageListenerFactory = new MessageListenerFactory();
    protected final HashMap<MessageType,MessageListener> m_messageListeners = new HashMap<MessageType,MessageListener>();
    protected final HashMap<MessageType,DataReader>      m_dataReaders = new HashMap<MessageType,DataReader>();

    class RapidMessageListeners    extends ListenerList<IRapidMessageListener>    { /**/ }
    class DdsMessageListeners      extends ListenerList<IDdsMessageListener>      { /**/ }
    class DdsReaderStatusListeners extends ListenerList<IDdsReaderStatusListener> { /**/ }
    class DdsInstanceListeners     extends ListenerList<IDdsInstanceListener>     { /**/ }

    protected final HashMap<MessageType,RapidMessageListeners>     m_rapidListeners = new HashMap<MessageType,RapidMessageListeners>();
    protected final HashMap<MessageType,DdsMessageListeners>         m_ddsListeners = new HashMap<MessageType,DdsMessageListeners>();
    protected final HashMap<MessageType,DdsReaderStatusListeners> m_statusListeners = new HashMap<MessageType,DdsReaderStatusListeners>();
    protected final HashMap<MessageType,DdsInstanceListeners>   m_instanceListeners = new HashMap<MessageType,DdsInstanceListeners>();

    protected final Map<MessageType,QosPolicyCounts> m_incompatibleQos = Maps.newHashMap();

    protected final HashMap<MessageType,SizeAccumulator> m_sizes = new HashMap<MessageType,SizeAccumulator>();

    private final ArrayBlockingQueue<MessagePair> m_msgQueue = new ArrayBlockingQueue<MessagePair>(1024);

    private boolean m_synchronousDispatch;
            
    int readers = 0;

    /**
     * @param participantId
     * @param partition
     */
    public PartitionedMessageCollector(String participantId, String partition, boolean synchronousDispatch) {
        m_synchronousDispatch = synchronousDispatch;
        m_participantId = participantId;
        m_partition = partition;
        if(Agent.exists(partition)) {
            m_agent = Agent.valueOf(partition);
        }
        else {
            m_agent = null;
        }
        Dds.addRestartListener(this);

        QueueThread thread = new QueueThread(m_msgQueue, partition);
        thread.start();
    }

    public PartitionedMessageCollector(String participantId, Agent agent, boolean synchronousDispatch) {
        m_synchronousDispatch = synchronousDispatch;
        m_participantId = participantId;
        m_agent = agent;
        m_partition = agent.name();
        Dds.addRestartListener(this);
    }
    
    public void setSynchronousDispatch(boolean state) {
        m_synchronousDispatch = state;
    }

    /** 
     * use a custom MessageListenerFactory
     */
    public void setMessageListenerFactory(MessageListenerFactory factory) {
        m_messageListenerFactory = factory;
    }
    
    public boolean hasRecentMessages(long currentTime, long timeWindow) {
        long lowTime = currentTime - timeWindow;
        for(MessageListener ml : m_messageListeners.values()) {
            if(ml.getLastTimestamp() > lowTime) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @param msgType
     * @return
     * @throws NotSubscribedException 
     */
    public Object getLastMessage(MessageType msgType) throws NotSubscribedException {
        DataReader dr = m_dataReaders.get(msgType);
        if(dr != null) {
            // if we have a reader, ml should never be null
            MessageListener ml = m_messageListeners.get(msgType);
            return ml.getLastSample();
        }
        throw new NotSubscribedException("Not subscribed to "+msgType.name()+" on partition "+m_partition);
    }

    /**
     * 
     * @param msgType
     * @return
     * @throws NotSubscribedException 
     */
    public Map<InstanceHandle_t,Object> getLastMessages(MessageType msgType) throws NotSubscribedException {
        DataReader dr = m_dataReaders.get(msgType);
        if(dr != null) {
            // if we have a reader, ml should never be null
            MessageListener ml = m_messageListeners.get(msgType);
            return ml.getLastSamples();
        }
        throw new NotSubscribedException("Not subscribed to "+msgType.name()+" on partition "+m_partition);
    }

    public Set<MessageType> getSubscribedMessageTypes() {
        return m_dataReaders.keySet();
    }

    public void printDebugInfo(MessageType msgType) {
        MessageListener ml = m_messageListeners.get(msgType);
        if(ml == null) {
            logger.debug("MessageListener for "+msgType.name()+" is null");
        }
        else {
            ml.printDebugInfo();
        }
    }

    //==== ADD LISTENERS ================================================

    public synchronized void addRapidMessageListener(final MessageType msgType, final IRapidMessageListener listener) {
        //-- if there is a corresponding Config type, subscribe to that, too
        if(msgType.getConfigName() != null) {
            MessageType cfgType = MessageType.valueOf(msgType.getConfigName());
            RapidMessageListeners cfgListeners = m_rapidListeners.get(cfgType);
            if(cfgListeners == null) {
                cfgListeners = new RapidMessageListeners();
                m_rapidListeners.put(cfgType, cfgListeners);
            }
            getMessageListener(cfgType);
        }
        
        RapidMessageListeners listeners = m_rapidListeners.get(msgType);
        if(listeners == null) {
            listeners = new RapidMessageListeners();
            m_rapidListeners.put(msgType, listeners);
        }
        final MessageListener msgListener = getMessageListener(msgType);

        try {
            listeners.lock.writeLock().lock();
            listeners.add(listener);
        }
        finally {
            listeners.lock.writeLock().unlock();
        }

        DdsTask.dispatchExec(new Runnable() {
            @Override
            public void run() {
                // give listener last received sample from every instance, if they exist
                final Object[] msgObjs = msgListener.getLastSamples().values().toArray(new Object[0]);
                Object cfgObj = null;
                if(msgType.getConfigName() != null) {
                    MessageType cfgType = MessageType.valueOf(msgType.getConfigName());
                    cfgObj = getMessageListener(cfgType).getLastSample();
                }
                for(Object msgObj : msgObjs) {
                    listener.onRapidMessageReceived(m_agent, msgType, msgObj, cfgObj);
                }
            }
        });
    }

    public synchronized void addDdsMessageListener(final MessageType msgType, final IDdsMessageListener listener) {
        DdsMessageListeners listeners = m_ddsListeners.get(msgType);
        if(listeners == null) {
            listeners = new DdsMessageListeners();
            m_ddsListeners.put(msgType, listeners);
        }
        final MessageListener msgListener = getMessageListener(msgType);
        try {
            listeners.lock.writeLock().lock();
            listeners.add(listener);
        }
        finally {
            listeners.lock.writeLock().unlock();
        }

        // give listener last received sample from each instance, if they exist
        int num = msgListener.getLastSamples().size();
        if(num > 0) {
            final Object[] msgObjs = msgListener.getLastSamples().values().toArray(new Object[num]);
            DdsTask.dispatchExec(new Runnable() {
                @Override
                public void run() {
                    for(Object msgObj : msgObjs) {
                        listener.onDdsMessageReceived(m_agent.name(), msgType, msgObj);
                    }
                }
            });
        }
    }

    public synchronized void getDdsReaderStatus(MessageType msgType, Object status) throws NonExistentEntity, IllegalArgumentException  {
        DataReader reader = m_dataReaders.get(msgType);
        if(reader != null) {
            if(status instanceof SampleRejectedStatus) {
                reader.get_sample_rejected_status((SampleRejectedStatus)status);
            }
            else if(status instanceof LivelinessChangedStatus) {
                reader.get_liveliness_changed_status((LivelinessChangedStatus)status);
            }
            else if(status instanceof RequestedDeadlineMissedStatus) {
                reader.get_requested_deadline_missed_status((RequestedDeadlineMissedStatus)status);
            }
            else if(status instanceof RequestedIncompatibleQosStatus) {
                reader.get_requested_incompatible_qos_status((RequestedIncompatibleQosStatus)status);
            }
            else if(status instanceof SampleLostStatus) {
                reader.get_sample_lost_status((SampleLostStatus)status);
            }
            else if(status instanceof SubscriptionMatchedStatus) {
                reader.get_subscription_matched_status((SubscriptionMatchedStatus)status);
            }
            else {
                throw new IllegalArgumentException("Don't know how to query data reader for "+status.getClass().getSimpleName());
            }
            return;
        }
        throw new NonExistentEntity("No DataReader for "+msgType.name());
    }

    public synchronized void addDdsReaderStatusListener(MessageType msgType, IDdsReaderStatusListener listener) {
        DdsReaderStatusListeners listeners = m_statusListeners.get(msgType);
        if(listeners == null) {
            listeners = new DdsReaderStatusListeners();
            m_statusListeners.put(msgType, listeners);
        }
        getMessageListener(msgType);
        try {
            listeners.lock.writeLock().lock();
            listeners.add(listener);
        }
        finally {
            listeners.lock.writeLock().unlock();
        }
    }

    public synchronized void addDdsInstanceListener(MessageType msgType, IDdsInstanceListener listener) {
        DdsInstanceListeners listeners = m_instanceListeners.get(msgType);
        if(listeners == null) {
            listeners = new DdsInstanceListeners();
            m_instanceListeners.put(msgType, listeners);
        }
        getMessageListener(msgType);
        try{ 
            listeners.lock.writeLock().lock();
            listeners.add(listener);
        }
        finally {
            listeners.lock.writeLock().unlock();
        }
    }

    /**
     * Get status information from a message reader, if it exists. Caller must pass in a 
     * struct that matches the status requested (e.g. LivelinessChangedStatus stuct for 
     * ReaderStatus.LivelinessChanged)
     * @param status reader status being requested
     * @param msgType message type for which we want to request status 
     * @param retVal DDS status struct. Must not be null. 
     * @return retVal 
     * @throws NonExistentEntity if a reader for the given type does not exist
     */
    public Object getRapidMessageListenerStatus(ReaderStatus status, MessageType msgType, Object retVal) throws NonExistentEntity {
        DataReader reader = m_dataReaders.get(msgType);
        if(reader == null) {
            throw new NonExistentEntity("reader for "+msgType.name()+" does not exist");
        }
        switch(status) {
        case RequestedDeadlineMissed:
            reader.get_requested_deadline_missed_status((RequestedDeadlineMissedStatus)retVal);
            break;
        case ReqestedIncompatibleQos:
            reader.get_requested_incompatible_qos_status((RequestedIncompatibleQosStatus)retVal);
            break;
        case SampleRejected:
            reader.get_sample_rejected_status((SampleRejectedStatus)retVal);
            break;
        case LivelinessChanged:
            reader.get_liveliness_changed_status((LivelinessChangedStatus)retVal);
            break;
        case SampleLost:
            reader.get_sample_lost_status((SampleLostStatus)retVal);
            break;
        case SubscriptionMatched:
            reader.get_subscription_matched_status((SubscriptionMatchedStatus)retVal);
            break;
        }
        return retVal;
    }

    //==== REMOVE LISTENERS ================================================

    /**
     * remove a IRapidMessageListener for the given MessageType
     */
    public synchronized boolean removeRapidMessageListener(MessageType msgType, IRapidMessageListener listener) {
        RapidMessageListeners listeners = m_rapidListeners.get(msgType);
        if(listeners == null) 
            return false;
        try {
            listeners.lock.writeLock().lock();
            boolean retVal = listeners.remove(listener);
            destroyDataReaderIfNoLongerNeeded(msgType);
            return retVal;
        }
        finally {
            listeners.lock.writeLock().unlock();
        }
    }

    /**
     * remove a IRapidMessageListener for all message types
     */
    public synchronized int removeRapidMessageListener(IRapidMessageListener listener) {
        int count = 0;
        for(MessageType msgType : m_rapidListeners.keySet()) {
            RapidMessageListeners listeners = m_rapidListeners.get(msgType);
            if(listeners.contains(listener)) {
                try {
                    listeners.lock.writeLock().lock();
                    if(listeners.remove(listener)) {
                        destroyDataReaderIfNoLongerNeeded(msgType);
                        count++;
                    }
                }
                finally {
                    listeners.lock.writeLock().unlock();
                }
            }
        }
        return count;
    }


    /**
     * remove a IDdsMessageListener for the given MessageType
     */
    public synchronized boolean removeDdsMessageListener(MessageType msgType, IDdsMessageListener listener) {
        DdsMessageListeners listeners = m_ddsListeners.get(msgType);
        if(listeners == null) 
            return false;
        try {
            listeners.lock.writeLock().lock();
            if( listeners.remove(listener) ) {
                destroyDataReaderIfNoLongerNeeded(msgType);
                return true;
            }
            return false;
        }
        finally {
            listeners.lock.writeLock().unlock();
        }
    }

    /**
     * remove a IRapidMessageListener for all message types
     */
    public synchronized int removeDdsMessageListener(IDdsMessageListener listener) {
        int count = 0;
        for(MessageType msgType : m_ddsListeners.keySet()) {
            DdsMessageListeners listeners = m_ddsListeners.get(msgType);
            if(listeners.contains(listener)) {
                try {
                    listeners.lock.writeLock().lock();
                    if(listeners.remove(listener)) {
                        destroyDataReaderIfNoLongerNeeded(msgType);
                        count++;
                    }
                }
                finally {
                    listeners.lock.writeLock().unlock();
                }
            }
        }
        return count;
    }

    /**
     * remove a IDdsMessageListener for the given MessageType
     */
    public synchronized boolean removeDdsReaderStatusListener(MessageType msgType, IDdsReaderStatusListener listener) {
        DdsReaderStatusListeners listeners = m_statusListeners.get(msgType);
        if(listeners == null) 
            return false;
        try {
            listeners.lock.writeLock().lock();
            if(listeners.remove(listener)) {
                destroyDataReaderIfNoLongerNeeded(msgType);
                return true;
            }
            return false;
        }
        finally {
            listeners.lock.writeLock().unlock();
        }
    }

    /**
     * remove a IRapidMessageListener for all message types
     */
    public synchronized int removeDdsReaderStatusListener(IDdsReaderStatusListener listener) {
        int count = 0;
        for(MessageType msgType : m_statusListeners.keySet()) {
            DdsReaderStatusListeners listeners = m_statusListeners.get(msgType);
            if(listeners.contains(listener)) {
                try {
                    listeners.lock.writeLock().lock();
                    if(listeners.remove(listener)) {
                        destroyDataReaderIfNoLongerNeeded(msgType);
                        count++;
                    }
                }
                finally {
                    listeners.lock.writeLock().unlock();
                }
            }
        }
        return count;
    }

    /**
     * remove a IDdsInstanceListener for the given MessageType
     */
    public synchronized boolean removeDdsInstanceListener(MessageType msgType, IDdsInstanceListener listener) {
        DdsInstanceListeners listeners = m_instanceListeners.get(msgType);
        if(listeners == null) 
            return false;
        try {
            listeners.lock.writeLock().lock();
            if(listeners.remove(listener)) {
                destroyDataReaderIfNoLongerNeeded(msgType);
                return true;
            }
            return false;
        }
        finally {
            listeners.lock.writeLock().unlock();
        }
    }

    /**
     * remove a IDdsInstanceListener for all message types
     */
    public synchronized int removeDdsInstanceListener(IDdsInstanceListener listener) {
        int count = 0;
        for(MessageType msgType : m_instanceListeners.keySet()) {
            DdsInstanceListeners listeners = m_instanceListeners.get(msgType);
            if(listeners.contains(listener)) {
                try {
                    listeners.lock.writeLock().lock();
                    if(listeners.remove(listener)) {
                        destroyDataReaderIfNoLongerNeeded(msgType);
                        count++;
                    }
                }
                finally {
                    listeners.lock.writeLock().unlock();
                }
            }
        }
        return count;
    }

    /**
     * Destroy the data reader if there are no more listeners. If the Category is 
     * Sample or State, also check if the Config reader should be destroyed
     * @param msgType
     * @return
     */
    protected boolean destroyDataReaderIfNoLongerNeeded(MessageType msgType) {
        StringBuilder builder = new StringBuilder();
        DdsMessageListeners ddsListeners = m_ddsListeners.get(msgType);
        if(ddsListeners != null && ddsListeners.size() > 0) {
            for(IDdsMessageListener listener : ddsListeners.values()) {
                builder.append(listener.getClass().getSimpleName()+",");
            }
            //logger.debug("NoDestroy : "+msgType.name()+":"+m_participantId+" has "+ddsListeners.size()+" dds listeners : "+builder.toString());
            return false;
        }
        RapidMessageListeners rapidListeners = m_rapidListeners.get(msgType);
        if(rapidListeners != null && rapidListeners.size() > 0) {
            for(IRapidMessageListener listener : rapidListeners.values()) {
                builder.append(listener.getClass().getSimpleName()+",");
            }
            //logger.debug("NoDestroy : "+msgType.name()+":"+m_participantId+" has "+rapidListeners.size()+" rapid listeners : "+builder.toString());
            return false;
        }
        DdsReaderStatusListeners statusListeners = m_statusListeners.get(msgType);
        if(statusListeners != null && statusListeners.size() > 0) {
            for(IDdsReaderStatusListener listener : statusListeners.values()) {
                builder.append(listener.getClass().getSimpleName()+",");
            }
            //logger.debug("NoDestroy : "+msgType.name()+":"+m_participantId+" has "+statusListeners.size()+" status listeners : "+builder.toString());
            return false;
        }

        DataReader reader = m_dataReaders.get(msgType);
        if(reader == null) {
            logger.debug("Destroyed : "+msgType.name()+":"+m_participantId+" has already been destroyed");
        }
        else {
            deleteDataReader(msgType);
        }

        //-- If Sample or State, check if Config should be cleaned up
        if(msgType.getCategory() == Category.Sample || msgType.getCategory() == Category.State) {
            MessageType config = MessageType.valueOf(msgType.getConfigName());
            if(config != null) {
                destroyDataReaderIfNoLongerNeeded(config);
            }
        }
        return true;
    }

    @Override
    public void newMessage(final MessageType msgType, final Object msgObj) {
        //distributeMessage(msgType, message);
        final MessagePair pair = new MessagePair(msgType, msgObj);
        if(!m_msgQueue.offer(pair)) {
            // XXX FIXME This code should go away when wait sets are implemented
            logger.error("!!! Message Queue is full !!!");
            if(!msgType.getCategory().equals(Category.Sample)) { // drop samples, wait to queue others
                try {
                    m_msgQueue.put(pair);
                } 
                catch (InterruptedException e) {
                    logger.error(e);
                }
            }
        }
    }

    public void distributeMessage(final MessageType msgType, final Object message) {
        Object lastListener = null;
        final DdsMessageListeners   dListeners = m_ddsListeners.get(msgType);
        final RapidMessageListeners rListeners = m_rapidListeners.get(msgType);
        if(dListeners != null && dListeners.size() > 0) {
            try {
                dListeners.lock.readLock().lock();
                for(IDdsMessageListener listener : dListeners.values()) {
                    lastListener = listener;
                    listener.onDdsMessageReceived(m_partition, msgType, message);
                }
            }
            catch(Throwable t) {
                String msg = "Error distributing "+msgType.name()+" to "+lastListener;
                logger.error(msg, t);
                //throw new RuntimeException(t);
            }
            finally{
                dListeners.lock.readLock().unlock();
            }
        }
        if(rListeners != null && rListeners.size() > 0 && m_agent != null) {
            // FIXME need better lookup of Configs
            Object configMsg = null;
            MessageType configType = MessageType.valueOf(msgType.getConfigName());
            if(configType == null) {
                //logger.debug("no config type for "+msgType);
            }
            else {
                MessageListener cl = m_messageListeners.get(configType);
                if(cl == null) {
                    logger.debug("*** No MessageListener for "+configType.name());
                    logger.debug("*** This should not have happened. Add logging to ");
                    logger.debug("*** subscription code to figure out why this is happening ");
                    RapidMessageListeners cfgListeners = m_rapidListeners.get(configType);
                    if(cfgListeners == null) {
                        cfgListeners = new RapidMessageListeners();
                        m_rapidListeners.put(configType, cfgListeners);
                    }
                    getMessageListener(configType);
                }
                else {
                    configMsg = cl.getLastSample();
                }
            }

            //long startTime, diffTime;
            try {
                rListeners.lock.readLock().lock();
                if(RapidMessageCollector.INSTANCE.isMeasureSizes) {
                    measureSize(msgType, message);
                }
                for(IRapidMessageListener listener : rListeners.values()) {
                    lastListener = listener;
                    //startTime = System.currentTimeMillis();
                    listener.onRapidMessageReceived(m_agent, msgType, message, configMsg);
                    //if((diffTime = System.currentTimeMillis()-startTime) > 50) {
                    //    logger.debug("listener "+listener.getClass().getSimpleName()+" took "+diffTime+" msecs to process "+msgType.name());
                    //}
                }
            }
            catch(Throwable t) {
                String msg = "Error distributing "+msgType.name()+" to "+lastListener;
                logger.error(msg, t);
                //throw new RuntimeException(t);
            }
            finally {
                rListeners.lock.readLock().unlock();
            }
        }
    }

    @Override
    public void newStatus(MessageType msgType, ReaderStatus statusType, Object status) {
        final DdsReaderStatusListeners sListeners = m_statusListeners.get(msgType);
        if(sListeners != null && sListeners.size() > 0 && m_agent != null) {
            try {
                sListeners.lock.readLock().lock();
                for(IDdsReaderStatusListener listener : sListeners.values()) {
                    listener.onReaderStatusReceived(m_partition, msgType, statusType, status);
                }
            }
            finally {
                sListeners.lock.readLock().unlock();
            }
        }
    }

    @Override
    public void instanceAlive(MessageType msgType, Object sample) {
        final DdsInstanceListeners iListeners = m_instanceListeners.get(msgType);
        if(iListeners != null && iListeners.size() > 0 && m_agent != null) {
            try {
                iListeners.lock.readLock().lock();
                for(IDdsInstanceListener listener : iListeners.values()) {
                    listener.onDdsInstanceAlive(m_partition, msgType, sample);
                }
            }
            finally {
                iListeners.lock.readLock().unlock();
            }
        }
    }

    @Override
    public void instanceDead(MessageType msgType, Object sample) {
        final DdsInstanceListeners iListeners = m_instanceListeners.get(msgType);
        if(iListeners != null && iListeners.size() > 0 && m_agent != null) {
            try {
                iListeners.lock.readLock().lock();
                for(IDdsInstanceListener listener : iListeners.values()) {
                    listener.onDdsInstanceDead(m_partition, msgType, sample);
                }
            }
            finally {
                iListeners.lock.readLock().unlock();
            }
        }
    }

    /**
     * Get the MessageListener if it exists; if not create new MessageListener
     * and a DataReader
     * @param msgType
     * @return
     */
    public synchronized MessageListener getMessageListener(MessageType msgType) {
        MessageListener msgListener = m_messageListeners.get(msgType);
        if(msgListener == null) {
            msgListener = m_messageListenerFactory.create(this, msgType);
            m_messageListeners.put(msgType, msgListener);
            createDataReader(msgListener);
        }
        DataReader dataReader = m_dataReaders.get(msgType);
        if(dataReader == null) {
            createDataReader(msgListener);
        }
        return msgListener;
    }

    protected DataReader createDataReader(MessageListener msgListener) {
        DataReader retVal = null;
        final MessageType msgType = msgListener.getMessageType();
        try {
            if(DdsEntityFactory.getParticipant(m_participantId) == null) {
                return null;
            }
            //logger.debug(" - creating DataReader for "+m_agent.name()+"::"+msgType.name()+" "+m_participantId+" - "+readers);
            retVal = DdsEntityFactory.createDataReader(m_participantId, 
                                                       msgType.getTopicName(), 
                                                       msgType.getDataTypeClass(), 
                                                       msgType.getQosProfile(), 
                                                       m_partition, msgListener);
            readers++;
        }
        catch (DdsEntityCreationException e) {
            logger.error("Failed to create DataReader for MessageType:"+msgType.toString()+" : "+e.getMessage());
        }
        // attempt to create data reader with fallback profile
        if(retVal == null) {
            logger.debug("Attempt to create data reader with fallback qos profile for "+msgType.toString());
            try {
                //logger.debug("Creating data reader on "+m_participantId+"/"+m_partition+" for "+msgType.toString()+" with fallback profile "+msgType.getQosProfileFallback());
                retVal = DdsEntityFactory.createDataReader(m_participantId, 
                                                           msgType.getTopicName(), 
                                                           msgType.getDataTypeClass(), 
                                                           msgType.getQosProfileFallback(), 
                                                           m_partition, msgListener);
            }
            catch (DdsEntityCreationException e) {
                logger.error("Failed to create DataReader for MessageType:"+msgType.name()+" with fallback profile "+msgType.getQosProfileFallback(), e);
            }
            if(retVal != null) {
                logger.warn("Fallback QoS profile for "+msgType.name()+" was successful.\n" +
                        "This means that either the MessageType has not been created with the proper qosProfile name, \n" +
                        "or that the participant's QoS library does not include the appropriate profile.");
            }
        }

        if(retVal != null) {
            m_dataReaders.put(msgType, retVal);
        }
        else {
            logger.error("Failed to create DataReader for "+msgType.name()+" with specified OR fallback QoS profiles.\nSomething is seriously wrong. ");
        }

        return retVal;
    }    

    protected void deleteDataReader(MessageType msgType) {
        try {
            ContainedEntities ce  = DdsEntityFactory.getContainedEntities(m_participantId);
            Subscriber subscriber = ce.getSubscriberForPartition(m_agent.name());
            DataReader reader = m_dataReaders.remove(msgType);
            if(reader != null) {
                //logger.debug(" - deleting DataReader for "+m_agent.name()+"::"+msgType.name()+" "+m_participantId+" - "+readers);
                subscriber.delete_datareader(reader);
                readers--;
            }
            else {
                logger.debug(" - DataReader doesn't exist "+m_agent.name()+"::"+msgType.name()+" "+m_participantId+" - "+readers);
            }
        }
        catch(Throwable t) {
            logger.warn("***Could not delete DataReader for "+msgType, t);
        }
    }

    /**
     * recreate data readers with our listeners on restart 
     */
    @Override
    public synchronized void onDdsStarted() throws Exception {
        for(MessageListener msgListener : m_messageListeners.values()) {
            createDataReader(msgListener);
        }
    }

    /**
     * @return estimated size (in MB) of DataReader buffer per instance
     */
    protected double estimateBufferSize(DataReader reader, MessageType msgType) {
        DataReaderQos qos = new DataReaderQos();
        reader.get_qos(qos);
        int depth     = qos.history.depth;
        int size      = TypeSupportUtil.getMaxSerializedSizeFor(msgType.getDataTypeClass());
        double mb     = 1024*1024;
        double retVal = depth*size/mb;
        return retVal;
    }

    @Override
    public void onDdsAboutToStop() throws Exception {
        // explicitly delete all data readers
        ContainedEntities ce  = DdsEntityFactory.getContainedEntities(m_participantId);
        if(ce != null) {
            Subscriber subscriber = ce.getSubscriberForPartition(m_partition);
            MessageType[] msgTypes = m_dataReaders.keySet().toArray(new MessageType[m_dataReaders.keySet().size()]);
            for(MessageType msgType : msgTypes) {
                try {
                    DataReader reader = m_dataReaders.get(msgType);

                    //logger.debug(" - deleting DataReader for "+m_agent.name()+"::"+msgType.name()+" "+m_participantId+" - "+readers);
                    subscriber.delete_datareader(reader);
                    readers--;
                }
                catch(Throwable t) {
                    logger.warn("***Could not delete DataReader for "+msgType, t);
                }
            }
            m_dataReaders.clear();
        }
    }

    @Override
    public void onDdsStopped() throws Exception {
        for(MessageListener ml : m_messageListeners.values()) {
            ml.clearLastSample();
        }
    }

    protected class QueueThread extends Thread {
        private final ArrayBlockingQueue<MessagePair> m_queue;
        public QueueThread(ArrayBlockingQueue<MessagePair> queue, String partition) {
            super("PartitionedMessageCollector-"+partition);
            m_queue = queue;
        }
        @Override
        public void run() {
            try {
                while(!isInterrupted()) {
                    final MessagePair pair = m_queue.take();
                    if(m_synchronousDispatch) {
                        distributeMessage(pair.msgType, pair.msgObj);
                    }
                    else {
                        try {
                            DdsTask.dispatchExec(new Runnable() {
                                @Override
                                public void run() {
                                    distributeMessage(pair.msgType, pair.msgObj);
                                }
                            });
                        }
                        catch(Throwable t) {
                            logger.debug("Error distributing "+pair.msgType.toString(), t);
                        }
                    }
                }
            }
            catch(InterruptedException e) {
                logger.debug("Interrupted.");
            }
        }
    }

    protected void measureSize(MessageType msgType, Object sample) {
        SizeAccumulator sz = m_sizes.get(msgType);
        if(sz == null) {
            sz = new SizeAccumulator(sample);
            m_sizes.put(msgType, sz);
            return;
        }
        sz.add(sample);
    }

    public void writeSizes(String string) {
        if(m_sizes.keySet().size() > 0) {
            PrintStream       ps;
            if(string == null)
                string = "";
            String filename = String.format("%s/RAPID-MsgSize-%s-%s", System.getProperty("user.home"), string, m_agent.name());
            try {
                ps = new PrintStream(new FileOutputStream (filename));
                ps.format("#%s,%s,%s,%s,%s\n", "MessageType", "Count", "Min", "Max", "Ave");
                for(MessageType msgType : m_sizes.keySet()) {
                    SizeAccumulator sz = m_sizes.get(msgType);
                    ps.format("%s,%d,%d,%d,%d\n", msgType.name(), sz.getCount(), sz.getMin(), sz.getMax(), sz.getAverage());
                }
                ps.close();
            }
            catch (FileNotFoundException e) {
                logger.error("Could not open file for write: "+filename, e);
            }
        }
    }

    Map<MessageType,QosPolicyCounts> getIncompatibleQosRequests() {
        return m_incompatibleQos;
    }

    @Override
    public void incompatibleQosRequested(MessageType msgType, QosPolicyCounts policyCounts) {
        m_incompatibleQos.put(msgType, policyCounts);
    }

}
