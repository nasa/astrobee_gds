/******************************************************************************
 * Copyright © 2019, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration. All 
 * rights reserved.
 * 
 * The Astrobee Control Station platform is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0. 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 *****************************************************************************/
package gov.nasa.arc.irg.freeflyer.rapid.centralized;

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.IFollowedAgentListener;
import gov.nasa.arc.irg.freeflyer.rapid.LogEntry;
import gov.nasa.arc.irg.freeflyer.rapid.LogPoster;
import gov.nasa.arc.irg.rapid.ui.e4.view.CommandParamGroup;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.helpers.ParameterList;
import gov.nasa.rapid.v2.e4.message.publisher.RapidMessagePublisher;
import gov.nasa.rapid.v2.e4.util.RapidUtil;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;

import rapid.Command;
import rapid.CommandDef;
import rapid.QueueAction;

/**
 * It's "Centralized" because it listens to the Agent selected in the StatusPart
 * @author ddwheele
 *
 */
@Creatable
public class CentralizedCommandPublisher implements IFollowedAgentListener {
	protected RapidMessagePublisher m_rapidMessagePublisher;
	protected static HashMap<Agent,CentralizedCommandPublisher> s_instances = new HashMap<Agent,CentralizedCommandPublisher>();
	protected Agent        				m_self;

	protected String       				m_partition;
	protected String        			m_participant;
	protected HashMap<String,String> 	m_cmdIdAndName = new HashMap<String,String>();
	protected ParameterList				m_paramsSimpleMove6dof;
	protected ParameterList				m_currentParams;
	private int 						m_uniqueCounter = 0;
	@Inject
	protected MApplication 				m_application;
	protected static Agent				m_agent;

	@Inject
	public CentralizedCommandPublisher() {
		// make a dummy instance before the agent is selected
		s_instances.put(null, null);
	}
	
	@Inject
	public CentralizedCommandPublisher(Agent agent) {
		if(agent == null) {
			return;
		}
		m_partition = agent.name();
		m_participant = Rapid.PrimaryParticipant;
		m_self = Agent.getEgoAgent();
		m_agent = agent;

		m_rapidMessagePublisher = new RapidMessagePublisher(agent);

		s_instances.put(m_agent, this);
	}

	public void commandRequested(CommandDef cdef, String subsystemName, Object o) {
		// create a command
		Command cmd = new Command();
		RapidUtil.setHeader(cmd.hdr, m_rapidMessagePublisher.getAgent(), m_self, -1);
		cmd.cmdName = cdef.name;
		cmd.subsysName = subsystemName;
		cmd.cmdId = m_uniqueCounter++ + m_participant + System.currentTimeMillis()/1000; 
		cmd.cmdSrc = m_self.name();// Agent.getEgoAgent().name().split(":")[0]; 
		cmd.cmdAction = QueueAction.QUEUE_BYPASS;
		cmd.targetCmdId = ""; // unused for QUEUE_BYPASS

		if(o instanceof List<?>) {
			List<CommandParamGroup> cpgList = (List<CommandParamGroup>)o;
			// put the parameters in
			ParameterList pl = new ParameterList();

			for(CommandParamGroup cpg : cpgList) {
				pl = cpg.setParameterInCommand(pl);

			}
			pl.assign(cmd.arguments.userData);
		}
		sendCommand(cmd);
	}

	public void sendCommand(Command cmd) {
		m_rapidMessagePublisher.writeMessage(m_participant, MessageType.COMMAND_TYPE, cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd.cmdName, m_agent.name());
	}

	@Inject @Optional
	public void followedAgentChanged(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent followed) {
		if(followed == null) {
			return;
		}
		if(s_instances.get(followed) == null) {
			s_instances.put(followed, new CentralizedCommandPublisher(followed));
		}

		m_application.getContext().set(CentralizedCommandPublisher.class, s_instances.get(followed));
	}
}
