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
import rapid.ext.LMCTRL;
import rapid.ext.LMCTRL_METHOD_CLEARMAP;
import rapid.ext.LMCTRL_METHOD_CLEARUNDERROVER;
import rapid.ext.LMCTRL_METHOD_PARAM_FLAG;
import rapid.ext.LMCTRL_METHOD_PARAM_RADIUS;
import rapid.ext.LMCTRL_METHOD_SETACTIVE;

/**
 * experimental wrapper for RAPID Commands
 * @author mallan
 */
public class LocalMapCtrlSubsystem extends AbstractSubsystem {
    public static final String name = LMCTRL.VALUE;

    protected static final String p_flag   = LMCTRL_METHOD_PARAM_FLAG.VALUE;
    protected static final String p_radius = LMCTRL_METHOD_PARAM_RADIUS.VALUE;

    protected static final String f_clearMap        = LMCTRL_METHOD_CLEARMAP.VALUE;
    protected static final String f_clearUnderRover = LMCTRL_METHOD_CLEARUNDERROVER.VALUE;
    protected static final String f_setActive       = LMCTRL_METHOD_SETACTIVE.VALUE;

    public LocalMapCtrlSubsystem(CommandConfigHelper cmdCfgHelper, 
                                 String participantId, Agent agent, String srcName,
                                 MessageType cmdMsgType) {
        super(name, cmdCfgHelper, participantId, agent, srcName, cmdMsgType);
        initParameterLists();
    }

    @Override
    public void initParameterLists() {
        m_params.put(f_clearMap,        new ParameterList());
        m_params.put(f_clearUnderRover, new ParameterList().add(p_radius, DataType.RAPID_FLOAT));
        m_params.put(f_setActive,       new ParameterList().add(p_flag, DataType.RAPID_BOOL));
    }

    public Command setActive(boolean flag) {
        final RapidMessagePublisher pub = RapidMessagePublisher.get(m_agent);
        final ParameterList params = m_params.get(f_setActive);
        params.set(p_flag, flag);
        Command cmd = createCommand(m_ssName, f_setActive, params);
        pub.writeMessage(m_participantId, m_cmdMsgType, cmd);
        return cmd;
    }

    public Command clearMap() throws UnsupportedOperationException {
        final RapidMessagePublisher pub = RapidMessagePublisher.get(m_agent);
        final ParameterList params = m_params.get(f_clearMap);
        Command cmd = createCommand(m_ssName, f_clearMap, params);
        pub.writeMessage(m_participantId, m_cmdMsgType, cmd);
        return cmd;
    }

    public Command clearUnderRover(float radius) {
        final RapidMessagePublisher pub = RapidMessagePublisher.get(m_agent);
        final ParameterList params = m_params.get(f_clearUnderRover);
        params.set(p_radius, radius);
        Command cmd = createCommand(m_ssName, f_clearUnderRover, params);
        pub.writeMessage(m_participantId, m_cmdMsgType, cmd);
        return cmd;
    }

}
