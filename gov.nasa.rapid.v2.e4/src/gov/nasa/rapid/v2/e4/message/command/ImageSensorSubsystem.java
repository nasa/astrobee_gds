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
import rapid.IMAGESENSOR_CAPTURE_SINGLE_SHOT;

/**
 * @author mallan
 */
public class ImageSensorSubsystem extends AbstractSubsystem {
    public static final String name = "ImageSensor";

    public static final String f_singleImage  = IMAGESENSOR_CAPTURE_SINGLE_SHOT.VALUE;

    public ImageSensorSubsystem(String subsysName, CommandConfigHelper cmdCfgHelper, 
                                String participantId, Agent agent, String commanderName,
                                MessageType cmdMsgType) {
        super(subsysName, cmdCfgHelper, participantId, agent, commanderName, cmdMsgType);
        initParameterLists();
    }

    @Override
    public void initParameterLists() {
        m_params.put(f_singleImage,  new ParameterList());
    }

    public Command singleImage() {
        final RapidMessagePublisher pub = RapidMessagePublisher.get(m_agent);
        final ParameterList params = m_params.get(f_singleImage);
        Command cmd = createCommand(m_ssName, f_singleImage, params);
        pub.writeMessage(m_participantId, m_cmdMsgType, cmd);
        return cmd;
    }

}
