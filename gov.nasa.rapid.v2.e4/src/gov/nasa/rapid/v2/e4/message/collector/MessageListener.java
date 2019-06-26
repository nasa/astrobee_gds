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

import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.ReaderStatus;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.rti.dds.infrastructure.Copyable;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.Time_t;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderAdapter;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.LivelinessChangedStatus;
import com.rti.dds.subscription.RequestedDeadlineMissedStatus;
import com.rti.dds.subscription.RequestedIncompatibleQosStatus;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleLostStatus;
import com.rti.dds.subscription.SampleRejectedStatus;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.SubscriptionMatchedStatus;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.util.LoanableSequence;

/**
 * @author mallan
 */
public class MessageListener extends DataReaderAdapter {
    private static final Logger logger = Logger.getLogger(MessageListener.class);

    protected final SampleInfo        m_info       = new SampleInfo();
    protected final IMessageReceiver  m_collector;
    protected final MessageType       m_msgType;

    final SampleInfoSeq               m_infoSeq;
    final LoanableSequence            m_dataSeq;
    
    protected long  m_thisTimestamp = 0;
    protected long  m_lastTimestamp = 0;

    protected Object                       m_lastSample     = null;
    protected int                          m_numLastSamples = 0;
    protected Map<InstanceHandle_t,Object> m_lastSamples    = new HashMap<InstanceHandle_t,Object>();

    protected long m_dataAvailable          = 0;
    protected long m_validData              = 0;
    protected long m_deadlineMissed         = 0;
    protected long m_incompatibleQos        = 0;
    protected long m_sampleRejected         = 0;
    protected long m_sampleLost             = 0;
    protected long m_livelinessChanged      = 0;
    protected long m_subscriptionMatched    = 0;

    /**
     * TODO: figure out good way to estimate appropriate poolSize
     * @param collector
     * @param messageType
     */
    public MessageListener(IMessageReceiver collector, MessageType messageType) {
        m_collector = collector;
        m_msgType   = messageType;
        m_infoSeq   = new SampleInfoSeq();
        m_dataSeq   = new LoanableSequence(messageType.getDataTypeClass());
    }

    public MessageType getMessageType() {
        return m_msgType;
    }

    /**
     * @return time the last message was received
     */
    public long getLastTimestamp() {
        return m_lastTimestamp;
    }
    
    /**
     * @return last samples for known instances
     */
    public Map<InstanceHandle_t,Object> getLastSamples() {
        return m_lastSamples;
    }

    /**
     * @return last sample received
     */
    public void setLastSample(Object sample) {
        m_lastSample = sample;
        m_lastSamples.put(new InstanceHandle_t(), sample);
    }

    /**
     * @return last sample received
     */
    public Object getLastSample() {
        return  m_lastSample;
    }

    void clearLastSample() {
        m_lastSample = null;
        m_lastSamples.clear();
    }

    /**
     * grab samples from the reader
     */
    @Override
    public void on_data_available(DataReader reader) {
        // logger.debug("on_data_available "+m_msgType.toString());
        if (true) {
            on_bulk_data_available(reader);
//        } else {
//            // simple but inefficient and allows for data to back up
//            m_dataAvailable++;
//            try {
//                final Object sample = m_msgType.newDataTypeInstance();
//                reader.take_next_sample_untyped(sample, m_info);
//                SampleInfo info = m_info;
//                InstanceHandle_t key = new InstanceHandle_t(info.instance_handle);
//                if (info.valid_data) {
//                    m_validData++;
//                    m_lastSample = sample;
//                    m_lastSamples.put(key, sample);
//                    m_collector.newMessage(m_msgType, sample);
//                }
//                if (info.instance_state != InstanceStateKind.ALIVE_INSTANCE_STATE) {
//                    // logger.debug(m_msgType.name()+" instance no longer alive:"+info.instance_handle.toString());
//                    Object removed = m_lastSamples.remove(key);
//                    if (removed == null) {
//                        logger.error("FAILED to remove InstanceHandle " + m_msgType.name());
//                    }
//                    m_collector.instanceDead(m_msgType, removed);
//                }
//            } catch (RETCODE_NO_DATA noData) {
//                // -- ignore
//            }
        }
    }

    public long milliseconds(Time_t time) {
        return time.sec*1000 + time.nanosec/1000000;
    }
    
    /**
     * for high speed publishers, we want grab as many samples 
     * as possible for conversion and copy them into a local 
     * array, which will then be distributed to our in-process 
     * listeners. 
     */
    public synchronized void on_bulk_data_available(DataReader reader) {
        m_dataAvailable++;
        Object hadInstance;
        try {
            reader.take_untyped(m_dataSeq, m_infoSeq, 
                                ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
                                SampleStateKind.ANY_SAMPLE_STATE,
                                ViewStateKind.ANY_VIEW_STATE,
                                InstanceStateKind.ANY_INSTANCE_STATE);
            for(int i = 0; i < m_dataSeq.size(); i++) {
                SampleInfo info = (SampleInfo)m_infoSeq.get(i);
                // use reception timestamp because a remote clock may be messed up
                m_thisTimestamp = milliseconds(info.reception_timestamp);
                final Copyable loanedSample = (Copyable)m_dataSeq.get(i);
                final Copyable copiedSample = (Copyable)m_msgType.newDataTypeInstance();
                copiedSample.copy_from(loanedSample);
                
                // we *must* copy the InstanceHandle_t because the data becomes invalid after the loan is returned
                InstanceHandle_t key = new InstanceHandle_t(info.instance_handle);
                if(info.valid_data) {
                    m_validData++;
                    m_lastSample = copiedSample;
                    hadInstance = m_lastSamples.put(key, copiedSample);
                    if(hadInstance == null) {
                        m_collector.instanceAlive(m_msgType, copiedSample);
                    }
                    m_collector.newMessage(m_msgType, copiedSample); // put sample on new message queue
                }
                if(info.instance_state != InstanceStateKind.ALIVE_INSTANCE_STATE) {
                    //logger.debug(m_msgType.name()+" instance no longer alive:"+info.instance_handle.toString());
                    Object removed = m_lastSamples.remove(key);
                    if(removed == null) {
                        removed = copiedSample;
                        logger.debug("no record of InstanceHandle: "+m_msgType.name()+" - "+key.toString());
                    }
                    //removed = copiedSample;
                    m_collector.instanceDead(m_msgType, removed);
                }
                final int numLastSamples = m_lastSamples.size();
                if(numLastSamples != m_numLastSamples) {
                    //if(numLastSamples > 1) logger.debug(m_msgType.name()+" has "+numLastSamples+" stored samples");
                    m_numLastSamples = numLastSamples;
                }
            }
            m_lastTimestamp = m_thisTimestamp;
        } 
        catch (RETCODE_NO_DATA noData) {
            //-- ignore
        } 
        finally {
            //-- ALWAYS return loan
            reader.return_loan_untyped(m_dataSeq, m_infoSeq);
        }
    }

    @Override
    public void on_requested_deadline_missed (DataReader reader, RequestedDeadlineMissedStatus status) {
        m_deadlineMissed++;
        m_collector.newStatus(m_msgType, ReaderStatus.RequestedDeadlineMissed, status);
    }

    @Override
    public void on_requested_incompatible_qos (DataReader reader, RequestedIncompatibleQosStatus status) {
        m_incompatibleQos++;
        m_collector.incompatibleQosRequested(m_msgType, new QosPolicyCounts(status.policies));
        m_collector.newStatus(m_msgType, ReaderStatus.ReqestedIncompatibleQos, status);
        logger.warn(m_msgType.name()+" reader - Requested Incompatible Qos: policy="+status.last_policy_id);
    }

    private final SampleLostStatus m_lostStatus = new SampleLostStatus();
    @Override
    public void on_sample_lost (DataReader reader, SampleLostStatus status) {
        m_sampleLost++;
        // XXX: 01/23/2012 The new "last_reason" member is not initialized 
        // in the callback interface yet. In order to populate the value, we 
        // must actively call get_sample_lost_status. Hopefully this will 
        // be fixed post-4.5e
        if(true) {
            reader.get_sample_lost_status(m_lostStatus);
            status.last_reason = m_lostStatus.last_reason;
        }
//        if (false) {
//            System.err.println(m_msgType.name() + " lost to valid ratio = " + (double) m_sampleLost / (double) m_validData);
//            // resetCounters();
//        }
        m_collector.newStatus(m_msgType, ReaderStatus.SampleLost, status);
    }

    @Override
    public void on_sample_rejected (DataReader reader, SampleRejectedStatus status) {
        m_sampleRejected++;
        m_collector.newStatus(m_msgType, ReaderStatus.SampleRejected, status);
    }

    @Override
    public void on_liveliness_changed (DataReader reader, LivelinessChangedStatus status) {
        m_livelinessChanged++;
        m_collector.newStatus(m_msgType, ReaderStatus.LivelinessChanged, status);
    }

    @Override
    public void on_subscription_matched (DataReader reader, SubscriptionMatchedStatus status) {
        m_subscriptionMatched++;
        m_collector.newStatus(m_msgType, ReaderStatus.SubscriptionMatched, status);
    }

    @SuppressWarnings("unused")
    private void resetCounters() {
        m_dataAvailable          = 0;
        m_validData              = 0;
        m_deadlineMissed         = 0;
        m_incompatibleQos        = 0;
        m_sampleRejected         = 0;
        m_sampleLost             = 0;
        m_livelinessChanged      = 0;
        m_subscriptionMatched    = 0;
    }

    public void printDebugInfo() {
        logger.debug(m_msgType.toString());
        logger.debug("  m_dataAvailable = "+m_dataAvailable);
        logger.debug("  m_validData = "+m_validData);
        logger.debug("  m_deadlineMissed = "+m_deadlineMissed);
        logger.debug("  m_incompatibleQos = "+m_incompatibleQos);
        logger.debug("  m_sampleRejected = "+m_sampleRejected);
        logger.debug("  m_sampleLost = "+m_sampleLost);
        logger.debug("  m_livelinessChanged = "+m_livelinessChanged);
        logger.debug("  m_subscriptionMatched = "+m_subscriptionMatched);
        logger.debug("  m_lastSample = "+m_lastSample);
    }
}
