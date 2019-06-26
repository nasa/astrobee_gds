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
package gov.nasa.arc.verve.utils.rapid;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.infrastructure.InstanceHandleSeq;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.topic.builtin.TopicBuiltinTopicData;

public class RapidDdsUtil {
    private static Logger logger = Logger.getLogger(RapidDdsUtil.class);

    public int getAppId(DomainParticipant participant) {
        if(participant != null) {
            DomainParticipantQos qos = new DomainParticipantQos();
            participant.get_qos(qos);
            return qos.wire_protocol.rtps_app_id;
        }
        return -1;
    }

    /**
     * This call is essentially useless. get_discovered_topics returns instance
     * handles for discovered topics, but get_discovered_topic_data only returns data
     * for local topics. 
     * @param participant
     * @return
     */
    public static String[] getDiscoveredTopicNamesUseless(DomainParticipant participant) {
        if(participant != null) {
            DomainParticipantQos qos = new DomainParticipantQos();
            participant.get_qos(qos);
            InstanceHandleSeq instanceHandles = new InstanceHandleSeq();
            participant.get_discovered_topics(instanceHandles);
            Object[] topicHandles = instanceHandles.toArray();
            String[] topicNames = new String[topicHandles.length];
            TopicBuiltinTopicData topicData = new TopicBuiltinTopicData();
            for(int i = 0; i < topicHandles.length; i++) {
                try {
                    InstanceHandle_t handle = (InstanceHandle_t)topicHandles[i];
                    participant.get_discovered_topic_data(topicData, handle);
                    topicNames[i] = topicData.name;
                }
                catch(Throwable t) {
                    //System.err.println("InstanceHandle"+i+", exception:"+t.getClass().getSimpleName()+":"+t.getMessage());
                }
            }
            return topicNames;
        }
        return null;
    }

    /**
     * @return the list of discovery peers for the default participant
     */
    public static List<String> getDiscoveryPeersList(DomainParticipant participant) {
        ArrayList<String> retVal = new ArrayList<String>();
        DomainParticipantQos dpQoS = new DomainParticipantQos();
        participant.get_qos(dpQoS);
        StringSeq initialPeers = dpQoS.discovery.initial_peers;
        for(int i = 0; i < initialPeers.size(); i++) {
            Object o = initialPeers.get(i);
            retVal.add((String)o);
        }
        return retVal;
    }

    /**
     */
    public static void checkDiscoveryPeers(DomainParticipant participant) {
        List<String> peers = getDiscoveryPeersList(participant);
        logger.info("initial_peers");
        for(int i = 0; i < peers.size(); i++) {
            String s = peers.get(i);
            logger.info(String.format("  %02d : peer = %s", i, s));
        }
    }

}
