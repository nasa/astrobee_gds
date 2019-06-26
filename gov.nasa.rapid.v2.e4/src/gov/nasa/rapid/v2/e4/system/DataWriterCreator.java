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
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;

import com.rti.dds.infrastructure.Copyable;
import com.rti.dds.publication.DataWriter;
import com.rti.dds.publication.DataWriterListener;

public class DataWriterCreator {
    public final String                     participantId;
    public final String                     topicName;
    public final Class<? extends Copyable>  typeClass;
    public final String                     qosProfile;
    public final String                     partition;
    public final DataWriterListener         listener;
    
    public DataWriterCreator(final String participantId, 
                             final MessageType msgType,                                                           
                             final Agent agent) {
        this(participantId, msgType, agent.name(), null);
    }

    public DataWriterCreator(final String participantId, 
                             MessageType msgType,                                                           
                             final String partition) {
        this(participantId, msgType, partition, null);
    }

    public DataWriterCreator(final String participantId, 
                             MessageType msgType,                                                           
                             final String partition, 
                             final DataWriterListener listener) {
        this.participantId = participantId;
        this.topicName     = msgType.getTopicName();
        this.typeClass     = msgType.getDataTypeClass();
        this.qosProfile    = msgType.getQosProfile();
        this.partition     = partition;
        this.listener      = listener;
    }

    public DataWriterCreator(final String participantId,
                             final String topicName,  
                             final Class<? extends Copyable> typeClass, 
                             final String qosProfile, 
                             final String partition) {
        this(participantId, topicName, typeClass, qosProfile, partition, null);
    }
    
    public DataWriterCreator(final String participantId,
                             final String topicName,  
                             final Class<? extends Copyable> typeClass, 
                             final String qosProfile, 
                             final String partition, 
                             final DataWriterListener listener) {
        this.participantId = participantId;
        this.topicName     = topicName;
        this.typeClass     = typeClass;
        this.qosProfile    = qosProfile;
        this.partition     = partition;
        this.listener      = listener;
    }
    
    /**
     * create the data writer and store value in public dataWriter field
     * @return newly created writer
     * @throws DdsEntityCreationException
     */
    public DataWriter create() throws DdsEntityCreationException {
        return DdsEntityFactory.createDataWriter(participantId, topicName, typeClass, qosProfile, partition, listener);
    }
    
    @Override
    public String toString() {
        return String.format("[%s,%s,%s,%s,%s]", participantId, typeClass.getSimpleName(), topicName, qosProfile, partition);
    }
}
