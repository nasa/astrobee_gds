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
package gov.nasa.dds.rti.system;

import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.dds.rti.util.TypeSupportUtil;

import java.util.ArrayList;
import java.util.HashMap;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.Copyable;
import com.rti.dds.infrastructure.RETCODE_ERROR;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.publication.PublisherQos;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriberQos;

/**
 * holds Publishers and Subscribers for a DomainParticipant's partitions
 * @author mallan
 */
public class ContainedEntities {
    protected final DomainParticipant m_participant;

    protected final HashMap<String,Publisher>   m_publishers  = new HashMap<String,Publisher>();
    protected final HashMap<String,Subscriber>  m_subscribers = new HashMap<String,Subscriber>();
    protected final ArrayList<String>       m_registeredTypes = new ArrayList<String>();

    public ContainedEntities(DomainParticipant participant) {
        m_participant = participant;
    }

    public synchronized Subscriber getSubscriberForPartition(String partition) 
            throws DdsEntityCreationException {
        Subscriber retVal = null;
        retVal = m_subscribers.get(partition);
        if(retVal == null) {
            retVal = createSubscriberForPartition(partition);
        }
        return retVal;
    }
    
    public synchronized Publisher getPublisherForPartition(String partition) 
            throws DdsEntityCreationException {
        Publisher retVal = null;
        retVal = m_publishers.get(partition);
        if(retVal == null) {
            retVal = createPublisherForPartition(partition);
        }
        return retVal;
    }

    /**
     * create new subscriber using the default Qos 
     * @param partition
     * @return
     * @throws DdsEntityCreationException
     */
    protected Subscriber createSubscriberForPartition(String partition) 
            throws DdsEntityCreationException {
        Subscriber retVal = null;
        try {
            SubscriberQos qos = new SubscriberQos();
            m_participant.get_default_subscriber_qos(qos);
            qos.partition.name.add(partition);
            retVal = m_participant.create_subscriber(qos, null, StatusKind.STATUS_MASK_NONE);
            m_subscribers.put(partition, retVal);
        }
        catch(RETCODE_ERROR e) {
            throw new DdsEntityCreationException("Error creating Subscriber for partition \""+partition+"\"", e);
        }
        return retVal;
    }

    /**
     * create new publisher using default Qos 
     * @param partition
     * @return
     * @throws DdsEntityCreationException
     */
    protected Publisher createPublisherForPartition(String partition) 
            throws DdsEntityCreationException {
        Publisher retVal = null;
        try {
            PublisherQos qos = new PublisherQos();
            m_participant.get_default_publisher_qos(qos);
            qos.partition.name.add(partition);
            retVal = m_participant.create_publisher(qos, null, StatusKind.STATUS_MASK_NONE);
            m_publishers.put(partition, retVal);
        }
        catch(RETCODE_ERROR e) {
            throw new DdsEntityCreationException("Error creating Publisher for partition \""+partition+"\"", e);
        }
        return retVal;
    }
    
    public void registerType(final Class<? extends Copyable> typeClass) {
        String typeName = TypeSupportUtil.getTypeNameFor(typeClass);
        if(!m_registeredTypes.contains(typeName)) {
            TypeSupportUtil.registerType(m_participant, typeClass);
        }
    }


}
