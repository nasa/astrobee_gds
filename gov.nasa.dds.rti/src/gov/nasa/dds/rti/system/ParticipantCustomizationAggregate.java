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

import java.util.ArrayList;
import java.util.List;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactoryQos;
import com.rti.dds.domain.DomainParticipantQos;

/**
 * 
 *
 */
public class ParticipantCustomizationAggregate implements IParticipantCustomization {
    List<IParticipantCustomization> m_customizations = new ArrayList<IParticipantCustomization>();
    
    public ParticipantCustomizationAggregate(IParticipantCustomization... customizations) {
        for(IParticipantCustomization customization : customizations) {
            m_customizations.add(customization);
        }
    }
    
    public ParticipantCustomizationAggregate add(IParticipantCustomization customization) {
        m_customizations.add(customization);
        return this;
    }
    
    @Override
    public boolean customizeFactory(String participantId, DomainParticipantFactoryQos factoryQos) {
        boolean retVal = false;
        for(IParticipantCustomization c : m_customizations) {
            if(c.customizeFactory(participantId, factoryQos)) {
                retVal = true;
            }
        }
        return retVal;
    }

    @Override
    public boolean customizePreCreation(String participantId, DomainParticipantQos participantQos) {
        boolean retVal = false;
        for(IParticipantCustomization c : m_customizations) {
            if(c.customizePreCreation(participantId, participantQos)) {
                retVal = true;
            }
        }
        return retVal;
    }

    @Override
    public boolean customizePostCreation(String participantId, DomainParticipant participant) {
        boolean retVal = false;
        for(IParticipantCustomization c : m_customizations) {
            if(c.customizePostCreation(participantId, participant)) {
                retVal = true;
            }
        }
        return retVal;
    }

}
