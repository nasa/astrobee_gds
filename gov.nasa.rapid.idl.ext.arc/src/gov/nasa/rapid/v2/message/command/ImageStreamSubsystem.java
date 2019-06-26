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
package gov.nasa.rapid.v2.message.command;

import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.exception.CommandNotAvailableException;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.command.AbstractSubsystem;
import gov.nasa.rapid.v2.e4.message.helpers.CommandConfigHelper;
import gov.nasa.rapid.v2.e4.message.helpers.ParameterList;
import gov.nasa.rapid.v2.e4.message.publisher.RapidMessagePublisher;
import rapid.Command;
import rapid.MOBILITY;
import rapid.ext.arc.IMGSTREAM_METHOD_reconnect;
import rapid.ext.arc.IMGSTREAM_METHOD_setCrop;
import rapid.ext.arc.IMGSTREAM_METHOD_setCrop_DTYPE_cropX;
import rapid.ext.arc.IMGSTREAM_METHOD_setCrop_DTYPE_cropY;
import rapid.ext.arc.IMGSTREAM_METHOD_setCrop_DTYPE_height;
import rapid.ext.arc.IMGSTREAM_METHOD_setCrop_DTYPE_width;
import rapid.ext.arc.IMGSTREAM_METHOD_setCrop_PARAM_cropX;
import rapid.ext.arc.IMGSTREAM_METHOD_setCrop_PARAM_cropY;
import rapid.ext.arc.IMGSTREAM_METHOD_setCrop_PARAM_height;
import rapid.ext.arc.IMGSTREAM_METHOD_setCrop_PARAM_width;
import rapid.ext.arc.IMGSTREAM_METHOD_setQuality;
import rapid.ext.arc.IMGSTREAM_METHOD_setQuality_DTYPE_quality;
import rapid.ext.arc.IMGSTREAM_METHOD_setQuality_PARAM_quality;
import rapid.ext.arc.IMGSTREAM_METHOD_setResizeHeight;
import rapid.ext.arc.IMGSTREAM_METHOD_setResizeHeight_DTYPE_height;
import rapid.ext.arc.IMGSTREAM_METHOD_setResizeHeight_PARAM_height;
import rapid.ext.arc.IMGSTREAM_METHOD_setResizeWidth;
import rapid.ext.arc.IMGSTREAM_METHOD_setResizeWidth_DTYPE_width;
import rapid.ext.arc.IMGSTREAM_METHOD_setResizeWidth_PARAM_width;
import rapid.ext.arc.IMGSTREAM_METHOD_setResolution;
import rapid.ext.arc.IMGSTREAM_METHOD_setResolution_DTYPE_divisor;
import rapid.ext.arc.IMGSTREAM_METHOD_setResolution_PARAM_divisor;
import rapid.ext.arc.IMGSTREAM_METHOD_setSaveImages;
import rapid.ext.arc.IMGSTREAM_METHOD_setSaveImages_DTYPE_saveImages;
import rapid.ext.arc.IMGSTREAM_METHOD_setSaveImages_PARAM_saveImages;
import rapid.ext.arc.ROSBRIDGE_METHOD_setMinSeparation;
import rapid.ext.arc.ROSBRIDGE_METHOD_setMinSeparation_DTYPE_msec;
import rapid.ext.arc.ROSBRIDGE_METHOD_setMinSeparation_PARAM_msec;

public class ImageStreamSubsystem extends AbstractSubsystem {
    public static final String name = MOBILITY.VALUE;

    public ImageStreamSubsystem(String subsystemName, CommandConfigHelper cmdCfgHelper, 
                             String participantId, Agent agent, String commanderName,
                             MessageType cmdMsgType) {
        super(subsystemName, cmdCfgHelper, participantId, agent, commanderName, cmdMsgType);
        initParameterLists();
    }

    @Override
    public void initParameterLists() {
        m_params.put(IMGSTREAM_METHOD_setResolution.VALUE, new ParameterList()
        .add(IMGSTREAM_METHOD_setResolution_PARAM_divisor.VALUE,     IMGSTREAM_METHOD_setResolution_DTYPE_divisor.VALUE));
        
        m_params.put(IMGSTREAM_METHOD_setResizeWidth.VALUE, new ParameterList()
        .add(IMGSTREAM_METHOD_setResizeWidth_PARAM_width.VALUE,        IMGSTREAM_METHOD_setResizeWidth_DTYPE_width.VALUE));
        
        m_params.put(IMGSTREAM_METHOD_setResizeHeight.VALUE, new ParameterList()
        .add(IMGSTREAM_METHOD_setResizeHeight_PARAM_height.VALUE,        IMGSTREAM_METHOD_setResizeHeight_DTYPE_height.VALUE));

        m_params.put(IMGSTREAM_METHOD_setCrop.VALUE, new ParameterList()
        .add(IMGSTREAM_METHOD_setCrop_PARAM_cropX.VALUE,        IMGSTREAM_METHOD_setCrop_DTYPE_cropX.VALUE)
        .add(IMGSTREAM_METHOD_setCrop_PARAM_cropY.VALUE,        IMGSTREAM_METHOD_setCrop_DTYPE_cropY.VALUE)
        .add(IMGSTREAM_METHOD_setCrop_PARAM_width.VALUE,        IMGSTREAM_METHOD_setCrop_DTYPE_width.VALUE)
        .add(IMGSTREAM_METHOD_setCrop_PARAM_height.VALUE,       IMGSTREAM_METHOD_setCrop_DTYPE_height.VALUE));

        m_params.put(IMGSTREAM_METHOD_setQuality.VALUE, new ParameterList()
        .add(IMGSTREAM_METHOD_setQuality_PARAM_quality.VALUE,        IMGSTREAM_METHOD_setQuality_DTYPE_quality.VALUE));
        
        m_params.put(IMGSTREAM_METHOD_setSaveImages.VALUE, new ParameterList()
        .add(IMGSTREAM_METHOD_setSaveImages_PARAM_saveImages.VALUE,  IMGSTREAM_METHOD_setSaveImages_DTYPE_saveImages.VALUE));
        
        m_params.put(IMGSTREAM_METHOD_reconnect.VALUE, new ParameterList());

        m_params.put(ROSBRIDGE_METHOD_setMinSeparation.VALUE, new ParameterList()
        .add(ROSBRIDGE_METHOD_setMinSeparation_PARAM_msec.VALUE,     ROSBRIDGE_METHOD_setMinSeparation_DTYPE_msec.VALUE));
        
    }

    public Command setResolution(int divisor) throws CommandNotAvailableException {
        final RapidMessagePublisher pub = RapidMessagePublisher.get(m_agent);
        final String method = IMGSTREAM_METHOD_setResolution.VALUE;
        if(!m_cmdCfgHelper.hasCommand(m_ssName, method))
            throw new CommandNotAvailableException(m_ssName+"."+method+" is not in "+m_agent+" CommandConfig");
        final ParameterList params = m_params.get(method);
        params.set(IMGSTREAM_METHOD_setResolution_PARAM_divisor.VALUE, divisor);
        Command cmd = createCommand(m_ssName, method, params);
        pub.writeMessage(m_participantId, m_cmdMsgType, cmd);
        return cmd;
    }
    
    public Command setResizeWidth(int width) throws CommandNotAvailableException {
        final RapidMessagePublisher pub = RapidMessagePublisher.get(m_agent);
        final String method = IMGSTREAM_METHOD_setResizeWidth.VALUE;
        if(!m_cmdCfgHelper.hasCommand(m_ssName, method))
            throw new CommandNotAvailableException(m_ssName+"."+method+" is not in "+m_agent+" CommandConfig");
        final ParameterList params = m_params.get(method);
        params.set(IMGSTREAM_METHOD_setResizeWidth_PARAM_width.VALUE, width);
        Command cmd = createCommand(m_ssName, method, params);
        pub.writeMessage(m_participantId, m_cmdMsgType, cmd);
        return cmd;
    }
    
    public Command setResizeHeight(int height) throws CommandNotAvailableException {
        final RapidMessagePublisher pub = RapidMessagePublisher.get(m_agent);
        final String method = IMGSTREAM_METHOD_setResizeHeight.VALUE;
        if(!m_cmdCfgHelper.hasCommand(m_ssName, method))
            throw new CommandNotAvailableException(m_ssName+"."+method+" is not in "+m_agent+" CommandConfig");
        final ParameterList params = m_params.get(method);
        params.set(IMGSTREAM_METHOD_setResizeHeight_PARAM_height.VALUE, height);
        Command cmd = createCommand(m_ssName, method, params);
        pub.writeMessage(m_participantId, m_cmdMsgType, cmd);
        return cmd;
    }
    
    public Command setCrop(int x, int y, int width, int height) throws CommandNotAvailableException {
        final RapidMessagePublisher pub = RapidMessagePublisher.get(m_agent);
        final String method = IMGSTREAM_METHOD_setCrop.VALUE;
        if(!m_cmdCfgHelper.hasCommand(m_ssName, method))
            throw new CommandNotAvailableException(m_ssName+"."+method+" is not in "+m_agent+" CommandConfig");
        final ParameterList params = m_params.get(method);
        params.set(IMGSTREAM_METHOD_setCrop_PARAM_cropX.VALUE, x);
        params.set(IMGSTREAM_METHOD_setCrop_PARAM_cropY.VALUE, y);
        params.set(IMGSTREAM_METHOD_setCrop_PARAM_width.VALUE, width);
        params.set(IMGSTREAM_METHOD_setCrop_PARAM_height.VALUE, height);
        Command cmd = createCommand(m_ssName, method, params);
        pub.writeMessage(m_participantId, m_cmdMsgType, cmd);
        return cmd;
    }
    
    public Command setQuality(int quality) throws CommandNotAvailableException {
        final RapidMessagePublisher pub = RapidMessagePublisher.get(m_agent);
        final String method = IMGSTREAM_METHOD_setQuality.VALUE;
        if(!m_cmdCfgHelper.hasCommand(m_ssName, method))
            throw new CommandNotAvailableException(m_ssName+"."+method+" is not in "+m_agent+" CommandConfig");
        final ParameterList params = m_params.get(method);
        params.set(IMGSTREAM_METHOD_setQuality_PARAM_quality.VALUE, quality);
        Command cmd = createCommand(m_ssName, method, params);
        pub.writeMessage(m_participantId, m_cmdMsgType, cmd);
        return cmd;
    }
    
    public Command setSaveImages(boolean saveImages) throws CommandNotAvailableException {
        final RapidMessagePublisher pub = RapidMessagePublisher.get(m_agent);
        final String method = IMGSTREAM_METHOD_setSaveImages.VALUE;
        if(!m_cmdCfgHelper.hasCommand(m_ssName, method))
            throw new CommandNotAvailableException(m_ssName+"."+method+" is not in "+m_agent+" CommandConfig");
        final ParameterList params = m_params.get(method);
        params.set(IMGSTREAM_METHOD_setSaveImages_PARAM_saveImages.VALUE, saveImages);
        Command cmd = createCommand(m_ssName, method, params);
        pub.writeMessage(m_participantId, m_cmdMsgType, cmd);
        return cmd;
    }
    
    public Command reconnect() throws CommandNotAvailableException {
        final RapidMessagePublisher pub = RapidMessagePublisher.get(m_agent);
        final String method = IMGSTREAM_METHOD_reconnect.VALUE;
        if(!m_cmdCfgHelper.hasCommand(m_ssName, method))
            throw new CommandNotAvailableException(m_ssName+"."+method+" is not in "+m_agent+" CommandConfig");
        final ParameterList params = m_params.get(method);
        Command cmd = createCommand(m_ssName, method, params);
        pub.writeMessage(m_participantId, m_cmdMsgType, cmd);
        return cmd;
    }
    
    public Command setMinSeparation(int msec) throws CommandNotAvailableException {
        final RapidMessagePublisher pub = RapidMessagePublisher.get(m_agent);
        final String method = ROSBRIDGE_METHOD_setMinSeparation.VALUE;
        if(!m_cmdCfgHelper.hasCommand(m_ssName, method))
            throw new CommandNotAvailableException(m_ssName+"."+method+" is not in "+m_agent+" CommandConfig");
        final ParameterList params = m_params.get(method);
        params.set(ROSBRIDGE_METHOD_setMinSeparation_PARAM_msec.VALUE, msec);
        Command cmd = createCommand(m_ssName, method, params);
        pub.writeMessage(m_participantId, m_cmdMsgType, cmd);
        return cmd;
    }
    

}
