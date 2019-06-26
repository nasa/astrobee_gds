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
import gov.nasa.rapid.v2.e4.exception.CommandNotAvailableException;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.helpers.CommandConfigHelper;
import gov.nasa.rapid.v2.e4.message.helpers.ParameterList;
import gov.nasa.rapid.v2.e4.message.publisher.RapidMessagePublisher;
import rapid.Command;
import rapid.DataType;
import rapid.MOBILITY;
import rapid.MOBILITY_METHOD_MOVE;
import rapid.MOBILITY_METHOD_MOVE6DOF;
import rapid.MOBILITY_METHOD_MOVE6DOF_DTYPE_END_LOCATION;
import rapid.MOBILITY_METHOD_MOVE6DOF_DTYPE_END_LOCATION_TOLERANCE;
import rapid.MOBILITY_METHOD_MOVE6DOF_DTYPE_FRAME_NAME;
import rapid.MOBILITY_METHOD_MOVE6DOF_DTYPE_HINTED_SPEED;
import rapid.MOBILITY_METHOD_MOVE6DOF_DTYPE_NAV_ALGO;
import rapid.MOBILITY_METHOD_MOVE6DOF_DTYPE_ROT;
import rapid.MOBILITY_METHOD_MOVE6DOF_PARAM_END_LOCATION;
import rapid.MOBILITY_METHOD_MOVE6DOF_PARAM_END_LOCATION_TOLERANCE;
import rapid.MOBILITY_METHOD_MOVE6DOF_PARAM_FRAME_NAME;
import rapid.MOBILITY_METHOD_MOVE6DOF_PARAM_HINTED_SPEED;
import rapid.MOBILITY_METHOD_MOVE6DOF_PARAM_NAV_ALGO;
import rapid.MOBILITY_METHOD_MOVE6DOF_PARAM_ROT;
import rapid.MOBILITY_METHOD_MOVE_DTYPE_END_LOCATION;
import rapid.MOBILITY_METHOD_MOVE_DTYPE_END_LOCATION_TOLERANCE;
import rapid.MOBILITY_METHOD_MOVE_DTYPE_FRAME_NAME;
import rapid.MOBILITY_METHOD_MOVE_DTYPE_HINTED_SPEED;
import rapid.MOBILITY_METHOD_MOVE_DTYPE_NAV_ALGO;
import rapid.MOBILITY_METHOD_MOVE_PARAM_END_LOCATION;
import rapid.MOBILITY_METHOD_MOVE_PARAM_END_LOCATION_TOLERANCE;
import rapid.MOBILITY_METHOD_MOVE_PARAM_FRAME_NAME;
import rapid.MOBILITY_METHOD_MOVE_PARAM_HINTED_SPEED;
import rapid.MOBILITY_METHOD_MOVE_PARAM_NAV_ALGO;
import rapid.MOBILITY_METHOD_SIMPLEMOVE;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_END_LOCATION;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_END_LOCATION_TOLERANCE;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_FRAME_NAME;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_ROT;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_END_LOCATION;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_END_LOCATION_TOLERANCE;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_FRAME_NAME;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_ROT;
import rapid.MOBILITY_METHOD_SIMPLEMOVE_DTYPE_END_LOCATION;
import rapid.MOBILITY_METHOD_SIMPLEMOVE_DTYPE_END_LOCATION_TOLERANCE;
import rapid.MOBILITY_METHOD_SIMPLEMOVE_DTYPE_FRAME_NAME;
import rapid.MOBILITY_METHOD_SIMPLEMOVE_PARAM_END_LOCATION;
import rapid.MOBILITY_METHOD_SIMPLEMOVE_PARAM_END_LOCATION_TOLERANCE;
import rapid.MOBILITY_METHOD_SIMPLEMOVE_PARAM_FRAME_NAME;
import rapid.MOBILITY_METHOD_STOPALLMOTION;
import rapid.Mat33f;
import rapid.RotationEncoding;
import rapid.Vec3d;

public class MobilitySubsystem extends AbstractSubsystem {
    public static final String name = MOBILITY.VALUE;

    public MobilitySubsystem(String subsystemName, CommandConfigHelper cmdCfgHelper, 
                             String participantId, Agent agent, String commanderName,
                             MessageType cmdMsgType) {
        super(subsystemName, cmdCfgHelper, participantId, agent, commanderName, cmdMsgType);
        initParameterLists();
    }

    @Override
    public void initParameterLists() {
        m_params.put(MOBILITY_METHOD_STOPALLMOTION.VALUE, new ParameterList());
        
        m_params.put(MOBILITY_METHOD_SIMPLEMOVE.VALUE, new ParameterList()
        .add(MOBILITY_METHOD_SIMPLEMOVE_PARAM_FRAME_NAME.VALUE,             MOBILITY_METHOD_SIMPLEMOVE_DTYPE_FRAME_NAME.VALUE)
        .add(MOBILITY_METHOD_SIMPLEMOVE_PARAM_END_LOCATION.VALUE,           MOBILITY_METHOD_SIMPLEMOVE_DTYPE_END_LOCATION.VALUE)
        .add(MOBILITY_METHOD_SIMPLEMOVE_PARAM_END_LOCATION_TOLERANCE.VALUE, MOBILITY_METHOD_SIMPLEMOVE_DTYPE_END_LOCATION_TOLERANCE.VALUE));
        
        m_params.put(MOBILITY_METHOD_SIMPLEMOVE6DOF.VALUE, new ParameterList()
        .add(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_FRAME_NAME.VALUE,             MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_FRAME_NAME.VALUE)
        .add(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_END_LOCATION.VALUE,           MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_END_LOCATION.VALUE)
        .add(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_END_LOCATION_TOLERANCE.VALUE, MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_END_LOCATION_TOLERANCE.VALUE)
        .add(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_ROT.VALUE,                    MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_ROT.VALUE));
        
        m_params.put(MOBILITY_METHOD_MOVE.VALUE, new ParameterList()
        .add(MOBILITY_METHOD_MOVE_PARAM_FRAME_NAME.VALUE,             MOBILITY_METHOD_MOVE_DTYPE_FRAME_NAME.VALUE)
        .add(MOBILITY_METHOD_MOVE_PARAM_END_LOCATION.VALUE,           MOBILITY_METHOD_MOVE_DTYPE_END_LOCATION.VALUE)
        .add(MOBILITY_METHOD_MOVE_PARAM_END_LOCATION_TOLERANCE.VALUE, MOBILITY_METHOD_MOVE_DTYPE_END_LOCATION_TOLERANCE.VALUE)
        .add(MOBILITY_METHOD_MOVE_PARAM_HINTED_SPEED.VALUE,           MOBILITY_METHOD_MOVE_DTYPE_HINTED_SPEED.VALUE)
        .add(MOBILITY_METHOD_MOVE_PARAM_NAV_ALGO.VALUE,               MOBILITY_METHOD_MOVE_DTYPE_NAV_ALGO.VALUE));
        
        m_params.put(MOBILITY_METHOD_MOVE6DOF.VALUE, new ParameterList()
        .add(MOBILITY_METHOD_MOVE6DOF_PARAM_FRAME_NAME.VALUE,             MOBILITY_METHOD_MOVE6DOF_DTYPE_FRAME_NAME.VALUE)
        .add(MOBILITY_METHOD_MOVE6DOF_PARAM_END_LOCATION.VALUE,           MOBILITY_METHOD_MOVE6DOF_DTYPE_END_LOCATION.VALUE)
        .add(MOBILITY_METHOD_MOVE6DOF_PARAM_END_LOCATION_TOLERANCE.VALUE, MOBILITY_METHOD_MOVE6DOF_DTYPE_END_LOCATION_TOLERANCE.VALUE)
        .add(MOBILITY_METHOD_MOVE6DOF_PARAM_ROT.VALUE,                    MOBILITY_METHOD_MOVE6DOF_DTYPE_ROT.VALUE)
        .add(MOBILITY_METHOD_MOVE6DOF_PARAM_HINTED_SPEED.VALUE,           MOBILITY_METHOD_MOVE6DOF_DTYPE_HINTED_SPEED.VALUE)
        .add(MOBILITY_METHOD_MOVE6DOF_PARAM_NAV_ALGO.VALUE,               MOBILITY_METHOD_MOVE6DOF_DTYPE_NAV_ALGO.VALUE));
        
        m_params.put("resetPose", new ParameterList()
        .add("xyz",             DataType.RAPID_VEC3d)
        .add("rot",             DataType.RAPID_MAT33f)
        .add("encoding",        DataType.RAPID_INT)
        .add("frameId",         DataType.RAPID_STRING));
    }

    public Command stopAllMotion() throws CommandNotAvailableException {
        final RapidMessagePublisher pub = RapidMessagePublisher.get(m_agent);
        final String method = MOBILITY_METHOD_STOPALLMOTION.VALUE;
        if(!m_cmdCfgHelper.hasCommand(m_ssName, method))
            throw new CommandNotAvailableException(m_ssName+"."+method+" is not in "+m_agent+" CommandConfig");
        final ParameterList params = m_params.get(method);
        Command cmd = createCommand(m_ssName, method, params);
        pub.writeMessage(m_participantId, m_cmdMsgType, cmd);
        return cmd;
    }
    
    public Command simpleMove(String referenceFrame, Vec3d xyt, Vec3d xytTolerance) {
        final RapidMessagePublisher pub = RapidMessagePublisher.get(m_agent);
        final String method = MOBILITY_METHOD_SIMPLEMOVE.VALUE;
        final ParameterList params = m_params.get(method);
        params.set(MOBILITY_METHOD_SIMPLEMOVE_PARAM_FRAME_NAME.VALUE,             referenceFrame);
        params.set(MOBILITY_METHOD_SIMPLEMOVE_PARAM_END_LOCATION.VALUE,           xyt);
        params.set(MOBILITY_METHOD_SIMPLEMOVE_PARAM_END_LOCATION_TOLERANCE.VALUE, xytTolerance);
        Command cmd = createCommand(m_ssName, method, params);
        pub.writeMessage(m_participantId, m_cmdMsgType, cmd);
        return cmd;
    }
    
    
    public Command simpleMove6DOF(String referenceFrame, Vec3d xyz, Vec3d xyzTolerance, Mat33f rot) {
        final RapidMessagePublisher pub = RapidMessagePublisher.get(m_agent);
        final String method = MOBILITY_METHOD_SIMPLEMOVE6DOF.VALUE;
        final ParameterList params = m_params.get(method);
        params.set(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_FRAME_NAME.VALUE,             referenceFrame);
        params.set(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_END_LOCATION.VALUE,           xyz);
        params.set(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_END_LOCATION_TOLERANCE.VALUE, xyzTolerance);
        params.set(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_ROT.VALUE,                    rot);
        Command cmd = createCommand(m_ssName, method, params);
        pub.writeMessage(m_participantId, m_cmdMsgType, cmd);
        return cmd;
    }
    
    public Command move(String referenceFrame, Vec3d xyt, Vec3d xytTolerance, float hintedSpeed, String navAlgo) {
        final RapidMessagePublisher pub = RapidMessagePublisher.get(m_agent);
        final String method = MOBILITY_METHOD_MOVE.VALUE;
        final ParameterList params = m_params.get(method);
        params.set(MOBILITY_METHOD_MOVE_PARAM_FRAME_NAME.VALUE,             referenceFrame);
        params.set(MOBILITY_METHOD_MOVE_PARAM_END_LOCATION.VALUE,           xyt);
        params.set(MOBILITY_METHOD_MOVE_PARAM_END_LOCATION_TOLERANCE.VALUE, xytTolerance);
        params.set(MOBILITY_METHOD_MOVE_PARAM_HINTED_SPEED.VALUE,           hintedSpeed);
        params.set(MOBILITY_METHOD_MOVE_PARAM_NAV_ALGO.VALUE,               navAlgo);
        Command cmd = createCommand(m_ssName, method, params);
        pub.writeMessage(m_participantId, m_cmdMsgType, cmd);
        return cmd;
    }
    
    public Command move6DOF(String referenceFrame, Vec3d xyz, Vec3d xyzTolerance, Mat33f rot, float hintedSpeed, String navAlgo) {
        final RapidMessagePublisher pub = RapidMessagePublisher.get(m_agent);
        final String method = MOBILITY_METHOD_MOVE6DOF.VALUE;
        final ParameterList params = m_params.get(method);
        params.set(MOBILITY_METHOD_MOVE6DOF_PARAM_FRAME_NAME.VALUE,             referenceFrame);
        params.set(MOBILITY_METHOD_MOVE6DOF_PARAM_END_LOCATION.VALUE,           xyz);
        params.set(MOBILITY_METHOD_MOVE6DOF_PARAM_END_LOCATION_TOLERANCE.VALUE, xyzTolerance);
        params.set(MOBILITY_METHOD_MOVE6DOF_PARAM_ROT.VALUE,                    rot);
        params.set(MOBILITY_METHOD_MOVE6DOF_PARAM_HINTED_SPEED.VALUE,           hintedSpeed);
        params.set(MOBILITY_METHOD_MOVE6DOF_PARAM_NAV_ALGO.VALUE,               navAlgo);
        Command cmd = createCommand(m_ssName, method, params);
        pub.writeMessage(m_participantId, m_cmdMsgType, cmd);
        return cmd;
    }
    
    public Command resetPose(Vec3d xyz, Mat33f rot, RotationEncoding encoding, String frameId) throws CommandNotAvailableException {
        final RapidMessagePublisher pub = RapidMessagePublisher.get(m_agent);
        final String method = "resetPose";
        if(!m_cmdCfgHelper.hasCommand(m_ssName, method))
            throw new CommandNotAvailableException(m_ssName+"."+method+" is not in "+m_agent+" CommandConfig");
        final ParameterList params = m_params.get(method);
        params.set("xyz",             xyz);
        params.set("rot",             rot);
        params.set("encoding",        encoding.value());
        params.set("frameId",         frameId);
        Command cmd = createCommand(m_ssName, method, params);
        pub.writeMessage(m_participantId, m_cmdMsgType, cmd);
        return cmd;
    }
    
}
