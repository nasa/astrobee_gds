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
package gov.nasa.rapid.v2.e4.message.command;

import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;
import gov.nasa.rapid.v2.e4.message.helpers.CommandConfigHelper;
import gov.nasa.rapid.v2.e4.message.helpers.ParameterList;
import gov.nasa.rapid.v2.e4.util.RapidUtil;
import gov.nasa.util.ProcessInfo;
import rapid.Command;
import rapid.CommandConfig;
import rapid.QueueAction;

/**
 * 
 * @author mallan
 *
 */
public class Commander implements IRapidMessageListener {
    protected String                m_participantId   = Rapid.DefaultParticipant;
    protected CommandConfig         m_cmdConfig       = null;
    CommandConfigHelper             m_cmdConfigHelper = null;
    protected final Agent           m_agent;         // agent receiving the command
    protected final String          m_commanderName; // name of commanding agent
    protected final MessageType     m_cmdMsgType;
    protected final MessageType     m_cmdConfigMsgType;

    public Commander(Agent cmdAgent, String commanderName, MessageType cmdMsgType, MessageType cmdConfigMsgType) {
        m_agent = cmdAgent;
        m_commanderName    = commanderName;
        m_cmdMsgType       = cmdMsgType;
        m_cmdConfigMsgType = cmdConfigMsgType;
    }
    
    public void subscribe() {
        RapidMessageCollector.instance().addRapidMessageListener(m_participantId, m_agent, m_cmdConfigMsgType, this);
    }
    
    public void unsubscribe() {
        RapidMessageCollector.instance().removeRapidMessageListener(m_participantId, m_agent, m_cmdConfigMsgType, this);
    }
    
    /**
     * create a command that will bypass the sequencer
     */
    protected Command createCommand(String subsystemName, String cmdName, ParameterList params, CommandConfig cmdConfig) {
        return createCommand(subsystemName, cmdName, params, cmdConfig, QueueAction.QUEUE_BYPASS, "");
    }
    
    /**
     * create a command that can be queued by the sequencer
     */
    protected Command createCommand(String subsystemName,
                                    String cmdName, ParameterList params, 
                                    CommandConfig cmdConfig, 
                                    QueueAction queueAction, 
                                    String targetCmdId) {
        Command cmd = new Command();
        RapidUtil.setHeader(cmd.hdr, m_agent, m_commanderName, cmdConfig.hdr.serial);
        cmd.subsysName = subsystemName;
        cmd.cmdName    = cmdName;
        cmd.cmdId      = ProcessInfo.username()+System.currentTimeMillis();
        cmd.cmdSrc     = m_commanderName;
        cmd.cmdAction  = queueAction;
        cmd.targetCmdId= targetCmdId;
        params.assign(cmd.arguments.userData);
        return cmd;
    }

    @Override
    public void onRapidMessageReceived(Agent agent, MessageType msgType, Object msgObj, Object cfgObj) {
        if(msgType.equals(m_cmdConfigMsgType)) {
            m_cmdConfig = (CommandConfig)msgObj;
            m_cmdConfigHelper = new CommandConfigHelper(m_cmdConfig);
        }
    }
    
    public void checkCommandConfig(CommandConfigHelper cch) {
        // empty; override in subclasses to get called when CommandConfig is received
    }
}
