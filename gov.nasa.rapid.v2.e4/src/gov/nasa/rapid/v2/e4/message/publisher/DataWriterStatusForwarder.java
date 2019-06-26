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

import gov.nasa.rapid.v2.e4.message.IDdsWriterStatusListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.WriterStatus;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.rti.dds.infrastructure.Cookie_t;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.Locator_t;
import com.rti.dds.publication.AcknowledgmentInfo;
import com.rti.dds.publication.DataWriter;
import com.rti.dds.publication.DataWriterListener;
import com.rti.dds.publication.LivelinessLostStatus;
import com.rti.dds.publication.OfferedDeadlineMissedStatus;
import com.rti.dds.publication.OfferedIncompatibleQosStatus;
import com.rti.dds.publication.PublicationMatchedStatus;
import com.rti.dds.publication.ReliableReaderActivityChangedStatus;
import com.rti.dds.publication.ReliableWriterCacheChangedStatus;

public class DataWriterStatusForwarder implements DataWriterListener {
    private final ReadWriteLock m_lock = new ReentrantReadWriteLock();
    private final ArrayList<IDdsWriterStatusListener> m_listeners = new ArrayList<IDdsWriterStatusListener>();

    private final String            m_partition;
    private final MessageType       m_msgType;
    private EnumSet<WriterStatus>   m_statusSet = EnumSet.noneOf(WriterStatus.class);
    
    public DataWriterStatusForwarder(String partition, MessageType msgType) {
        this.m_partition = partition;
        this.m_msgType = msgType;
    }
    
    public int mask() {
        return WriterStatus.mask(m_statusSet);
    }
    
    public boolean addListener(IDdsWriterStatusListener in) {
        try {
            m_lock.writeLock().lock();
            if(!m_listeners.contains(in)) {
                m_statusSet.addAll(in.writerStatusSet());
                return m_listeners.add(in);
            }
        }
        finally {
            m_lock.writeLock().unlock();
        }
        return false;
    }

    public boolean removeListener(IDdsWriterStatusListener in) {
        try {
            m_lock.writeLock().lock();
            boolean retVal = m_listeners.remove(in);
            m_statusSet.clear();
            for(IDdsWriterStatusListener listener : m_listeners) { // rebuild status set
                m_statusSet.addAll(listener.writerStatusSet());
            }
            return retVal;
        }
        finally {
            m_lock.writeLock().unlock();
        }
    }

    public boolean containsListener(IDdsWriterStatusListener in) {
        try {
            m_lock.readLock().lock();
            return m_listeners.contains(in);
        }
        finally {
            m_lock.readLock().unlock();
        }
    }

    @Override
    public void on_instance_replaced(DataWriter arg0, InstanceHandle_t arg1) {
//        try {
//            m_lock.readLock().lock();
//            for(IDdsWriterStatusListener listener : m_listeners) {
//                listener.onWriterStatusReceived(m_partition, m_msgType, WriterStatus.InstanceReplaced, arg1);
//            }
//        }
//        finally {
//            m_lock.readLock().unlock();
//        }
    }

    @Override
    public void on_liveliness_lost(DataWriter arg0, LivelinessLostStatus arg1) {
        try {
            m_lock.readLock().lock();
            for(IDdsWriterStatusListener listener : m_listeners) {
                listener.onWriterStatusReceived(m_partition, m_msgType, WriterStatus.LivelinessLost, arg1);
            }
        }
        finally {
            m_lock.readLock().unlock();
        }
    }

    @Override
    public void on_offered_deadline_missed(DataWriter arg0, OfferedDeadlineMissedStatus arg1) {
        try {
            m_lock.readLock().lock();
            for(IDdsWriterStatusListener listener : m_listeners) {
                listener.onWriterStatusReceived(m_partition, m_msgType, WriterStatus.OfferedDeadlineMissed, arg1);
            }
        }
        finally {
            m_lock.readLock().unlock();
        }
    }

    @Override
    public void on_offered_incompatible_qos(DataWriter arg0, OfferedIncompatibleQosStatus arg1) {
        try {
            m_lock.readLock().lock();
            for(IDdsWriterStatusListener listener : m_listeners) {
                listener.onWriterStatusReceived(m_partition, m_msgType, WriterStatus.OfferedIncompatibleQos, arg1);
            }
        }
        finally {
            m_lock.readLock().unlock();
        }
    }

    @Override
    public void on_publication_matched(DataWriter arg0, PublicationMatchedStatus arg1) {
        try {
            m_lock.readLock().lock();
            for(IDdsWriterStatusListener listener : m_listeners) {
                listener.onWriterStatusReceived(m_partition, m_msgType, WriterStatus.PublicationMatched, arg1);
            }
        }
        finally {
            m_lock.readLock().unlock();
        }
    }

    @Override
    public void on_reliable_reader_activity_changed(DataWriter arg0, ReliableReaderActivityChangedStatus arg1) {
//        try {
//            m_lock.readLock().lock();
//            for(IDdsWriterStatusListener listener : m_listeners) {
//                listener.onWriterStatusReceived(m_partition, m_msgType, WriterStatus., arg1);
//            }
//        }
//        finally {
//            m_lock.readLock().unlock();
//        }
    }

    @Override
    public void on_reliable_writer_cache_changed(DataWriter arg0, ReliableWriterCacheChangedStatus arg1) {
//        try {
//            m_lock.readLock().lock();
//            for(IDdsWriterStatusListener listener : m_listeners) {
//                listener.onWriterStatusReceived(m_partition, m_msgType, WriterStatus., arg1);
//            }
//        }
//        finally {
//            m_lock.readLock().unlock();
//        }
    }

    @Override
    public Object on_data_request(DataWriter arg0, Cookie_t arg1) {
        return null;
    }

    @Override
    public void on_data_return(DataWriter arg0, Object arg1, Cookie_t arg2) {   
        //
    }

    @Override
    public void on_sample_removed(DataWriter arg0, Cookie_t arg1) {
        // 
    }

    @Override
    public void on_destination_unreachable(DataWriter arg0, InstanceHandle_t arg1, Locator_t arg2) {
        //
    }

    @Override
    public void on_application_acknowledgment(DataWriter arg0,
                                              AcknowledgmentInfo arg1) {
        // TODO Auto-generated method stub
        
    }

   // public void on_service_request_accepted(DataWriter arg0, ServiceRequestAcceptedStatus arg1) { /**/ } 
}
