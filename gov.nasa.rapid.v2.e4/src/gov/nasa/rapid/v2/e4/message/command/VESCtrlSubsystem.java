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
import rapid.Command;
import rapid.DataType;
import rapid.ext.VESCTRL;
import rapid.ext.VESCTRL_METHOD_PARAM_FLAG;
import rapid.ext.VESCTRL_METHOD_SETACTIVE;
import rapid.ext.VESCTRL_METHOD_SETADVISORYMODE;

/**
 * experimental wrapper for RAPID Commands
 * @author mallan
 */
public class VESCtrlSubsystem extends AbstractSubsystem {
    public static final String name = VESCTRL.VALUE;

    protected static final String p_flag            = VESCTRL_METHOD_PARAM_FLAG.VALUE;

    protected static final String f_setActive       = VESCTRL_METHOD_SETACTIVE.VALUE;
    protected static final String f_setAdvisoryMode = VESCTRL_METHOD_SETADVISORYMODE.VALUE;

    public VESCtrlSubsystem(CommandConfigHelper cmdCfgHelper, 
                            String participantId, Agent agent, String commanderName,
                            MessageType cmdMsgType) {
        super(name, cmdCfgHelper, participantId, agent, commanderName, cmdMsgType);
        initParameterLists();
    }

    @Override
    public void initParameterLists() {
        m_params.put(f_setActive,       new ParameterList().add(p_flag, DataType.RAPID_BOOL));
        m_params.put(f_setAdvisoryMode, new ParameterList().add(p_flag, DataType.RAPID_BOOL));
    }

    public Command setActive(boolean flag) {
        final RapidMessagePublisher pub = RapidMessagePublisher.get(m_agent);
        final ParameterList params = m_params.get(f_setActive);
        params.set(p_flag, flag);
        Command cmd = createCommand(m_ssName, f_setActive, params);
        pub.writeMessage(m_participantId, m_cmdMsgType, cmd);
        return cmd;
    }

    public Command setAdvisoryMode(boolean flag) {
        final RapidMessagePublisher pub = RapidMessagePublisher.get(m_agent);
        final ParameterList params = m_params.get(f_setAdvisoryMode);
        params.set(p_flag, flag);
        Command cmd = createCommand(m_ssName, f_setAdvisoryMode, params);
        pub.writeMessage(m_participantId, m_cmdMsgType, cmd);
        return cmd;
    }

}
