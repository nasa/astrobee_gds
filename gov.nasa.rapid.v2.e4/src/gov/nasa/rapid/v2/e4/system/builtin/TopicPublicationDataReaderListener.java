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
package gov.nasa.rapid.v2.e4.system.builtin;

import gov.nasa.dds.system.DdsTask;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.publication.builtin.PublicationBuiltinTopicData;
import com.rti.dds.publication.builtin.PublicationBuiltinTopicDataDataReader;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderListener;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.LivelinessChangedStatus;
import com.rti.dds.subscription.RequestedDeadlineMissedStatus;
import com.rti.dds.subscription.RequestedIncompatibleQosStatus;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleLostStatus;
import com.rti.dds.subscription.SampleRejectedStatus;
import com.rti.dds.subscription.SubscriptionMatchedStatus;

/**
 * DataReaderListener to be added to the builtin topic discovery topic. 
 * When new publication information is received, it will notify its
 * list of ITopicPublicationListener instances.
 * @author mallan
 *
 */
public class TopicPublicationDataReaderListener implements DataReaderListener {
    private static final Logger logger = Logger.getLogger(TopicPublicationDataReaderListener.class);
    protected final ArrayList<PublicationBuiltinTopicData> m_newData = new ArrayList<PublicationBuiltinTopicData>();
    protected final ArrayList<PublicationBuiltinTopicData> m_deadData = new ArrayList<PublicationBuiltinTopicData>();
    protected final ArrayList<ITopicPublicationListener> m_listeners = new ArrayList<ITopicPublicationListener>();
    
    protected final HashMap<InstanceHandle_t,PublicationBuiltinTopicData> m_instanceMap = new HashMap<InstanceHandle_t,PublicationBuiltinTopicData>();

    //protected final SampleInfo m_info = new SampleInfo();
    protected String m_participantId;
    
    public TopicPublicationDataReaderListener() {
    }
    
    public TopicPublicationDataReaderListener(String participantId) {
        setParticipantId(participantId);
    }
    
    public TopicPublicationDataReaderListener(ITopicPublicationListener... listeners) {
        for(ITopicPublicationListener listener : listeners) {
            m_listeners.add(listener);
        }
    }
    
    public TopicPublicationDataReaderListener(String participantId, ITopicPublicationListener... listeners) {
        setParticipantId(participantId);
        for(ITopicPublicationListener listener : listeners) {
            m_listeners.add(listener);
        }
    }
    
    public void setParticipantId(String participantId) {
        m_participantId = participantId;
    }
    
    public String getParticipantId() {
        return m_participantId;
    }
    
    @Override
    public synchronized void on_data_available(DataReader reader) {
        //logger.debug("TopicPublicationDataReaderListener::on_data_available");
        PublicationBuiltinTopicDataDataReader pubReader = null;
        try {
            pubReader = (PublicationBuiltinTopicDataDataReader)reader;
            m_newData.clear();
            m_deadData.clear();
            boolean keepReading = true;
            // this is not the preferred way to take data from the reader
            // because we allocate a new object before doing the take (which can fail)
            // However, PublicationBuiltinTopicData does not implement Copyable, which
            // makes safely copying the data difficult. Because we expect publication 
            // announcements to be slow and low volume, we tolerate the inefficiency. 
            while(keepReading) {
                try {
                    PublicationBuiltinTopicData data = new PublicationBuiltinTopicData();
                    SampleInfo info = new SampleInfo();
                    pubReader.take_next_sample(data, info);
                    if(info.valid_data) {
                        m_newData.add(data);
                        m_instanceMap.put(info.instance_handle, data);
                    }
                    if(info.instance_state != InstanceStateKind.ALIVE_INSTANCE_STATE) {
                        PublicationBuiltinTopicData dead = m_instanceMap.remove(info.instance_handle);
                        if(dead == null) {
                            logger.error("could not get data for dead instance!");
                        }
                        else {
                            m_deadData.add(dead);
                        }
                    }
                    //logger.debug("instanceMap.size() = "+m_instanceMap.size());
                }
                catch(RETCODE_NO_DATA e) {
                    keepReading = false;
                }
            }
            final PublicationBuiltinTopicData[] newDataArray  = m_newData.toArray(new PublicationBuiltinTopicData[m_newData.size()]); 
            final PublicationBuiltinTopicData[] deadDataArray = m_deadData.toArray(new PublicationBuiltinTopicData[m_deadData.size()]); 

            // call the listeners in a separate thread
            DdsTask.dispatchExec(new Runnable() {
                @Override
                public void run() {
                    synchronized(m_listeners) {
                        for(PublicationBuiltinTopicData data : newDataArray) {
                            for(ITopicPublicationListener listener : m_listeners) {
                                listener.onTopicPublicationDiscovered(m_participantId, data);
                            }
                        }
                        for(PublicationBuiltinTopicData data : deadDataArray) {
                            for(ITopicPublicationListener listener : m_listeners) {
                                listener.onTopicPublicationDisappeared(m_participantId, data);
                            }
                        }
                    }
                }
            });
        }
        catch(Throwable t) {
            logger.debug(t);
        }
    }

    /**
     * Add listener to list (if listener does not already exist in list)
     * @param listener
     * @return true if listener was added to list
     */
    public boolean addListener(ITopicPublicationListener listener) {
        synchronized(m_listeners) {
            if(!m_listeners.contains(listener)) {
                return m_listeners.add(listener);
            }
            return false;
        }
    }

    /**
     * remove listener from list
     * @param listener
     * @return true if listener was in list and successfully removed
     */
    public boolean removeListener(ITopicPublicationListener listener) {
        synchronized(m_listeners) {
            return m_listeners.remove(listener);
        }
    }

    @Override
    public void on_liveliness_changed(DataReader arg0, LivelinessChangedStatus arg1) {
        // empty
    }

    @Override
    public void on_requested_deadline_missed(DataReader arg0, RequestedDeadlineMissedStatus arg1) {
        // empty
    }

    @Override
    public void on_requested_incompatible_qos(DataReader arg0, RequestedIncompatibleQosStatus arg1) {
        // empty
    }

    @Override
    public void on_sample_lost(DataReader arg0, SampleLostStatus arg1) {
        // empty
    }

    @Override
    public void on_sample_rejected(DataReader arg0, SampleRejectedStatus arg1) {
        // empty
    }

    @Override
    public void on_subscription_matched(DataReader arg0, SubscriptionMatchedStatus arg1) {
        // empty
    }
}
