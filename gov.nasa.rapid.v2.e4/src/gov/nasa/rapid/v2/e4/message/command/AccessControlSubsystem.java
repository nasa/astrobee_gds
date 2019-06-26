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
import rapid.ACCESSCONTROL_METHOD_GRABCONTROL;
import rapid.ACCESSCONTROL_METHOD_RELEASECONTROL;
import rapid.ACCESSCONTROL_METHOD_REQUESTCONTROL;
import rapid.ACCESSCONTROL_METHOD_TRANSFERCONTROL;
import rapid.ACCESSCONTROL_METHOD_TRANSFERCONTROL_PARAM_RECIPIENT;
import rapid.Command;
import rapid.DataType;

/**
 * experimental wrapper for RAPID Commands
 * @author mallan
 */
public class AccessControlSubsystem extends AbstractSubsystem {
    public static final String name = "AccessControl";

    public static final String p_controlRecipient = ACCESSCONTROL_METHOD_TRANSFERCONTROL_PARAM_RECIPIENT.VALUE;

    public static final String f_requestControl  = ACCESSCONTROL_METHOD_REQUESTCONTROL.VALUE;
    public static final String f_releaseControl  = ACCESSCONTROL_METHOD_RELEASECONTROL.VALUE;
    public static final String f_grabControl     = ACCESSCONTROL_METHOD_GRABCONTROL.VALUE;
    public static final String f_transferControl = ACCESSCONTROL_METHOD_TRANSFERCONTROL.VALUE;

    public AccessControlSubsystem(CommandConfigHelper cmdCfgHelper, 
                                  String participantId, Agent agent, String commanderName,
                                  MessageType cmdMsgType) {
        super(name, cmdCfgHelper, participantId, agent, commanderName, cmdMsgType);
        initParameterLists();
    }

    @Override
    public void initParameterLists() {
        m_params.put(f_requestControl,  new ParameterList());
        m_params.put(f_releaseControl,  new ParameterList());
        m_params.put(f_grabControl,     new ParameterList());
        m_params.put(f_transferControl, new ParameterList().add(p_controlRecipient, DataType.RAPID_STRING));
    }

    public Command requestControl() {
        final RapidMessagePublisher pub = RapidMessagePublisher.get(m_agent);
        final ParameterList params = m_params.get(f_requestControl);
        Command cmd = createCommand(m_ssName, f_requestControl, params);
        pub.writeMessage(m_participantId, m_cmdMsgType, cmd);
        return cmd;
    }

    public Command releaseControl() {
        final RapidMessagePublisher pub = RapidMessagePublisher.get(m_agent);
        final ParameterList params = m_params.get(f_releaseControl);
        Command cmd = createCommand(m_ssName, f_releaseControl, params);
        pub.writeMessage(m_participantId, m_cmdMsgType, cmd);
        return cmd;
    }

    public Command grabControl() {
        final RapidMessagePublisher pub = RapidMessagePublisher.get(m_agent);
        final ParameterList params = m_params.get(f_grabControl);
        Command cmd = createCommand(m_ssName, f_grabControl, params);
        pub.writeMessage(m_participantId, m_cmdMsgType, cmd);
        return cmd;
    }

    public Command transferControl(String controlRecipient) {
        final RapidMessagePublisher pub = RapidMessagePublisher.get(m_agent);
        final ParameterList params = m_params.get(f_transferControl);
        params.set(p_controlRecipient, controlRecipient);
        Command cmd = createCommand(m_ssName, f_transferControl, params);
        pub.writeMessage(m_participantId, m_cmdMsgType, cmd);
        return cmd;
    }
}
