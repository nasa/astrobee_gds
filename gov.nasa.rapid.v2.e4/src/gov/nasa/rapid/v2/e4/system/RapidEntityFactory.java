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
package gov.nasa.rapid.v2.e4.system;

import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.dds.rti.system.DdsEntityFactory;
import gov.nasa.rapid.v2.e4.message.MessageType;

import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.DataWriter;
import com.rti.dds.publication.DataWriterListener;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderListener;
import com.rti.dds.topic.Topic;

/**
 * Extends DdsEntityFactory by creating simpler methods that take MessageType
 * as a parameter.
 * @author mallan
 *
 */
public class RapidEntityFactory extends DdsEntityFactory {

    public static Topic getTopic(String participantId, MessageType type) throws DdsEntityCreationException {
        return DdsEntityFactory.getTopic(participantId, type.getTopicName(), type.getDataTypeClass());
    }

    /**
     * Create DataWriter from MessageType and partition
     * @param participantId
     * @param type
     * @param partition
     * @return
     * @throws DdsEntityCreationException
     */
    public static DataWriter createDataWriter(final String participantId, 
                                              MessageType type,                                                           
                                              final String partition) throws DdsEntityCreationException {
        return createDataWriter(participantId, type, partition, null, StatusKind.STATUS_MASK_NONE);
    }


    public static DataWriter createDataWriter(final String participantId, 
                                              MessageType type,                                                           
                                              final String partition, 
                                              final DataWriterListener listener, 
                                              final int listenerMask) throws DdsEntityCreationException {
        final String qosLibrary = getParticipantCreator(participantId).qosLibrary;
        return createDataWriter(participantId, 
                                type.getTopicName(), 
                                type.getDataTypeClass(), 
                                qosLibrary, 
                                type.getQosProfile(), 
                                partition,
                                listener, 
                                listenerMask,
                                true);
    }

    /**
     * Create DataReader from MessageType and partition
     * @param participantId
     * @param type
     * @param partition
     * @param listener
     * @return
     * @throws DdsEntityCreationException
     */
    public static DataReader createDataReader(final String participantId, 
                                              final MessageType type,
                                              final String partition,
                                              final DataReaderListener listener) throws DdsEntityCreationException {
        int listenerMask = (listener == null) ? StatusKind.STATUS_MASK_NONE : StatusKind.STATUS_MASK_ALL;
        return createDataReader(participantId, type, partition, listener, listenerMask);
    }

    public static DataReader createDataReader(final String participantId, 
                                              final MessageType type,
                                              final String partition,
                                              final DataReaderListener listener, 
                                              final int listenerMask) throws DdsEntityCreationException {
        String qosLibrary = s_participantCreators.get(participantId).qosLibrary;
        return createDataReader(participantId, 
                                type.getTopicName(),
                                type.getDataTypeClass(),
                                qosLibrary,
                                type.getQosProfile(),
                                partition, 
                                listener,
                                listenerMask);
    }
}
