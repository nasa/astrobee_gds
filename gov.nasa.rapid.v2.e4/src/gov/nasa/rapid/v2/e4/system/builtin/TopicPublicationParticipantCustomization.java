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

import gov.nasa.dds.rti.system.IParticipantCustomization;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactoryQos;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.builtin.PublicationBuiltinTopicDataTypeSupport;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.Subscriber;

/**
 * @author mallan
 */
public class TopicPublicationParticipantCustomization implements IParticipantCustomization {
    protected final TopicPublicationDataReaderListener m_dataListener;
    
    public TopicPublicationParticipantCustomization(TopicPublicationDataReaderListener dataListener) {
        m_dataListener = dataListener;
    }
    
    @Override
    public boolean customizeFactory(String participantId, DomainParticipantFactoryQos factoryQos) {
        return false;
    }

    @Override
    public boolean customizePreCreation(String participantId, DomainParticipantQos participantQos) {
        return false;
    }
    
    /**
     * After the participant has been created, but before it is activated, 
     * add a listener for new publications
     */
    @Override
    public boolean customizePostCreation(String participantId, DomainParticipant participant) {
        Subscriber subscriber = participant.get_builtin_subscriber();
        String topicName = PublicationBuiltinTopicDataTypeSupport.PUBLICATION_TOPIC_NAME;
        DataReader pubReader;
        pubReader = subscriber.lookup_datareader(topicName);
        m_dataListener.setParticipantId(participantId);
        pubReader.set_listener(m_dataListener, StatusKind.DATA_AVAILABLE_STATUS);
        return false; // we did not modify participant
    }

}
