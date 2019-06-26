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

import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.helpers.CommandConfigHelper;
import gov.nasa.rapid.v2.e4.message.helpers.ParameterList;
import gov.nasa.rapid.v2.e4.message.publisher.RapidMessagePublisher;
import gov.nasa.rapid.v2.e4.preferences.RapidPreferences;
import gov.nasa.rapid.v2.e4.util.RapidUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import rapid.Command;
import rapid.CommandDef;
import rapid.ParameterUnion;
import rapid.QueueAction;

public abstract class AbstractSubsystem {
    private static final Logger logger = Logger.getLogger(AbstractSubsystem.class);

    public final    String    m_ssName;
    protected final Map<String,ParameterList> m_params = new HashMap<String,ParameterList>();

    protected final CommandConfigHelper   m_cmdCfgHelper;
    protected final String                m_participantId;
    protected final Agent                 m_agent;         // agent receiving the command
    protected String                      m_srcName;       // name of commanding agent
    protected final MessageType           m_cmdMsgType;
    protected static int				  s_counter = 0;   // counter for unique command ids

    protected static FileWriter           s_cmdLog     = null;



    public AbstractSubsystem(String subsystemName, CommandConfigHelper cmdCfgHelper, 
                             String participantId, Agent agent, String srcName,
                             MessageType cmdMsgType) {
        m_cmdCfgHelper  = cmdCfgHelper;
        m_ssName        = subsystemName;
        m_participantId = participantId;
        m_agent         = agent;
        m_srcName       = srcName;
        m_cmdMsgType    = cmdMsgType;

        synchronized(AbstractSubsystem.class) {
            if(s_cmdLog == null) {
                String filename = RapidPreferences.getDefaultLogDir()+"rapid_cmd_log-"+System.currentTimeMillis()/1000+".txt";
                try {
                    s_cmdLog = new FileWriter(filename);
                    logger.debug("Opened RAPID command log: "+filename);
                } 
                catch (IOException e) {
                    logger.error("Error creating "+filename, e);
                }
            }
        }
        final RapidMessagePublisher pub = RapidMessagePublisher.get(m_agent);
        pub.createWriter(m_participantId, m_cmdMsgType);
    }

    public boolean hasCommand(String commandName) {
        if(m_cmdCfgHelper != null) {
            CommandDef def = m_cmdCfgHelper.getCommandDef(m_ssName, commandName);
            return def != null;
        }
        return false;
    }

    public CommandDef getCommandDef(String commandName) {
        if(m_cmdCfgHelper != null) {
            return m_cmdCfgHelper.getCommandDef(m_ssName, commandName);
        }
        return null;
    }

    /**
     * Set the Command srcName to be used
     * @param srcName
     */
    public void setSourceName(String srcName) {
        m_srcName = srcName;
    }

    public String getSourceName() {
        return m_srcName;
    }

    /**
     * create a command that bypasses the queue
     */
    protected Command createCommand(String subsystemName,
                                    String cmdName, 
                                    ParameterList params) {
        return createCommand(subsystemName, cmdName, params, 
                             QueueAction.QUEUE_BYPASS);
    }

    /**
     * create a command 
     */
    protected Command createCommand(String subsystemName,
                                    String cmdName, 
                                    ParameterList params, 
                                    QueueAction queueAction) {
        return createCommand(subsystemName, cmdName, params, 
                             queueAction, "",
                             m_agent, m_srcName, m_cmdCfgHelper);
    }

    /**
     * create a command that can be queued by the sequencer
     */
    protected Command createCommand(String subsystemName,
                                    String cmdName, 
                                    ParameterList params, 
                                    QueueAction queueAction, 
                                    String targetCmdId,
                                    Agent agent, 
                                    String srcName,
                                    CommandConfigHelper cmdCfgHelper) {
        Command cmd = new Command();
        RapidUtil.setHeader(cmd.hdr, agent, srcName, cmdCfgHelper.getSerialId());
        cmd.subsysName  = subsystemName;
        cmd.cmdName     = cmdName;
        cmd.cmdId       = truncate(20, srcName)+System.currentTimeMillis()+getCounterValue();
        cmd.cmdSrc      = m_srcName;
        cmd.cmdAction   = queueAction;
        cmd.targetCmdId = targetCmdId;
        params.assign(cmd.arguments.userData);
        if(s_cmdLog != null) {
            try {
                StringBuilder stringBuilder = new StringBuilder(new Timestamp(System.currentTimeMillis()).toString());
                stringBuilder.append(",").append(queueAction.toString()).append(",");
                stringBuilder.append(subsystemName).append(".").append(cmdName).append("(");
                for(String name : params.names()) {
                    ParameterUnion union = params.getParam(name);
                    stringBuilder.append(name).append("=").append(RapidUtil.valueString(union)).append(",");
                }
                stringBuilder.append(")\n");
                s_cmdLog.write(stringBuilder.toString());
                s_cmdLog.flush();
            }
            catch(Throwable t) {
                logger.error("error writing to cmdLog", t);
            }
        }
        return cmd;
    }

    String truncate(int at, String in) {
        final int len = in.length();
        if(len < at) {
            return in;
        }
        return in.substring(0, at);
    }

    protected static int getCounterValue() {
        s_counter++;
        if (s_counter > 100){
            s_counter = 0;
        }
        return s_counter;
    }

    public abstract void initParameterLists();
}
