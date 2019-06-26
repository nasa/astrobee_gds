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
package gov.nasa.dds.rti.util;

import gov.nasa.dds.rti.system.DdsEntityFactory;

import java.util.HashMap;
import java.util.Map;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicData;
import com.rti.dds.infrastructure.InstanceHandleSeq;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.Locator_t;
import com.rti.dds.topic.BuiltinTopicKey_t;

public class DiscoveredParticipants {
    //private static final Logger logger = Logger.getLogger(DiscoveredParticipants.class);

    public static class ParticipantInfo {
        public String            name;
        public BuiltinTopicKey_t key;
        public String[]          locatorStrings;
        public String[]          metatrafficStrings;
    }

    /**
     * For a given participantId, return a map of participants that participantId 
     * has discovered and info about the participant
     * @return
     */
    public static Map<String,ParticipantInfo> getDiscoveredParticipants(String participantId) {
        Map<String,ParticipantInfo> retVal = new HashMap<String,ParticipantInfo>();
        DomainParticipant dp = DdsEntityFactory.getParticipant(participantId);
        InstanceHandleSeq handleSeq = new InstanceHandleSeq();
        dp.get_discovered_participants(handleSeq);
        ParticipantBuiltinTopicData data = new ParticipantBuiltinTopicData();
        for(Object handleObj : handleSeq) {
            InstanceHandle_t handle = (InstanceHandle_t)handleObj;
            // work around RTI bug that doesn't clear out locators
            data.default_unicast_locators.clear();
            data.metatraffic_unicast_locators.clear();
            dp.get_discovered_participant_data(data, handle);
            ParticipantInfo pi = new ParticipantInfo();
            pi.name = data.participant_name.name;  
            //logger.debug("Participant: "+pi.name);
            pi.key  = data.key;
            int numLocators = data.default_unicast_locators.size();
            pi.locatorStrings = new String[numLocators];
            for(int i = 0; i < numLocators; i++) {
                Object locatorObj = data.default_unicast_locators.get(i);
                pi.locatorStrings[i] = RtiDdsUtil.toString((Locator_t)locatorObj);
            }
            int numMetaUnicastLocators   = data.metatraffic_unicast_locators.size();
            int numMetaMulticastLocators = data.metatraffic_multicast_locators.size();
            pi.metatrafficStrings = new String[numMetaUnicastLocators+numMetaMulticastLocators];
            for(int i = 0; i < numMetaUnicastLocators; i++) {
                Object locatorObj = data.metatraffic_unicast_locators.get(i);
                pi.metatrafficStrings[i] = RtiDdsUtil.toString((Locator_t)locatorObj);
            }
            for(int i = 0; i < numMetaMulticastLocators; i++) {
                Object locatorObj = data.metatraffic_multicast_locators.get(i);
                pi.metatrafficStrings[numMetaUnicastLocators+i] = RtiDdsUtil.toString((Locator_t)locatorObj);
            }
            retVal.put(pi.name,  pi);
        }
        return retVal;
    }

    /**
     * look up participant info from participantKey
     * @param participantKey
     * @return
     */
    public static ParticipantInfo getDiscoveredParticipantInfo(BuiltinTopicKey_t participantKey) {
        for(String participantId : DdsEntityFactory.getValidParticipantIds()) {
            Map<String,ParticipantInfo> participants = getDiscoveredParticipants(participantId);
            for(ParticipantInfo info : participants.values()) {
                if(info.key.equals(participantKey)) {
                    return info;
                }
            }
        }
        return null;
    }
}
