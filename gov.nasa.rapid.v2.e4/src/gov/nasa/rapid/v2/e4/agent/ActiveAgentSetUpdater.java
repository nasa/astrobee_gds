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
package gov.nasa.rapid.v2.e4.agent;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * TODO: add accessors for allow/deny lists 
 * @author mallan
 */
public class ActiveAgentSetUpdater implements IDiscoveredAgentListener {
    private static final Logger logger = Logger.getLogger(ActiveAgentSetUpdater.class);
    
    protected final Set<Agent> m_allow = new HashSet<Agent>();
    protected final Set<Agent> m_deny  = new HashSet<Agent>();

    @Override
    public void newAgentsDiscovered(String participantId, Collection<Agent> agents) {
        if(m_allow.size() == 0) {
            for(Agent agent : agents) {
                if(!m_deny.contains(agent)) {
                    ActiveAgentSet.INSTANCE.addAgent(agent, participantId);
                }
            }
        }
        else {
            for(Agent agent : agents) {
                if(m_allow.contains(agent)) {
                    ActiveAgentSet.INSTANCE.addAgent(agent, participantId);
                }
            }
        }

    }

    @Override
    public void newPartitionsDiscovered(String participantId, Collection<String> partitions) {
        // ignore
    }

    public void agentsDisappeared(String participantId, Collection<Agent> agents) {
        for(Agent agent : agents) {
            if(!m_deny.contains(agent)) {
                ActiveAgentSet.INSTANCE.removeAgent(agent);
            }
            logger.info("Agent disappeared on "+participantId+" : "+agent);
        }
    }

    public void partitionsDisappeared(String participantId, Collection<String> partitions) {
        // TODO Auto-generated method stub
    }

}
