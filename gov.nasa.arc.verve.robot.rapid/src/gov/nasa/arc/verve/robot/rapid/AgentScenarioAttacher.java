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
package gov.nasa.arc.verve.robot.rapid;

import gov.nasa.arc.verve.common.ardor3d.framework.IVerveScenario;
import gov.nasa.arc.verve.common.VerveTask;
import gov.nasa.arc.verve.robot.RobotRegistry;
import gov.nasa.arc.verve.robot.scenegraph.RobotNode;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.agent.DiscoveredAgentRepository;
import gov.nasa.rapid.v2.e4.agent.IDiscoveredAgentListener;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.ardor3d.scenegraph.Node;

/**
 * adds a robot to the scenario when discovered by DDS
 * @author mallan
 *
 */
public class AgentScenarioAttacher implements IDiscoveredAgentListener {
    private static final Logger logger = Logger.getLogger(AgentScenarioAttacher.class);

    protected final IVerveScenario m_scenario;

    public AgentScenarioAttacher(IVerveScenario scenario) {
        m_scenario = scenario;
        DiscoveredAgentRepository.INSTANCE.addListener(this);
        Agent[] agents = DiscoveredAgentRepository.INSTANCE.getDiscoveredAgents();
        attachNewAgents(Arrays.asList(agents));
    }
    
    /** remove this from the DiscoveredAgentRepository listener list */
    public void detatch() {
        DiscoveredAgentRepository.INSTANCE.removeListener(this);
    }

    @Override
    public void newAgentsDiscovered(String participantId, Collection<Agent> agents) {
        //logger.debug("AgentScenatioAttacher discovered new agent(s)");
        attachNewAgents(agents);
    }
    
    synchronized protected void attachNewAgents(Collection<Agent> agents) {
        final HashSet<Agent> agentSet = new HashSet<Agent>();
        agentSet.addAll(agents);
        VerveTask.asyncExec(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                agentSet.removeAll(getRegisteredRapidRobots());
                for (Agent agent : agentSet) {
                    try {
                        RobotNode robotNode = RapidAvatarFactory.buildAvatar(agent);
                        Node rapidSiteFrame = SiteFrameHack.getSiteFrameNode(agent, m_scenario.getRoot());
                        rapidSiteFrame.attachChild(robotNode);
                    } 
                    catch (Exception e) {
                        logger.error("Error while creating robot for " + agent.toString(), e);
                    }
                }
                return null;
            }
        });
    }

    @Override
    public void newPartitionsDiscovered(String participantId, Collection<String> partitions) {
        // ignore, we don't care about partitions
    }

    public static Set<Agent> getRegisteredRapidRobots() {
        HashSet<Agent> agents = new HashSet<Agent>();
        String[] robots = RobotRegistry.getRegisteredRobots();
        for(String robotString : robots) {
            if(robotString.startsWith(RapidRobot.NAME_PREFIX)) {
                String agentName = robotString.substring(RapidRobot.NAME_PREFIX.length());
                //logger.debug("agentName = "+agentName);
                try {
                    agents.add(Agent.valueOf(agentName));
                }
                catch(IllegalArgumentException e) {
                    logger.debug("bad agent name: "+agentName);
                }
            }
        }
        return agents;
    }

	@Override
	public void agentsDisappeared(String participantId, Collection<Agent> agents) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void partitionsDisappeared(String participantId,
			Collection<String> partitions) {
		// TODO Auto-generated method stub
		
	}
}
