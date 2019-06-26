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
package gov.nasa.arc.verve.robot.rapid.parts;

import gov.nasa.arc.verve.robot.parts.JointInfo;
import gov.nasa.arc.verve.robot.rapid.RapidRobot;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;

import org.apache.log4j.Logger;

import rapid.FloatSequence64;
import rapid.JointSample;

import com.ardor3d.math.Matrix3;
import com.ardor3d.scenegraph.Node;

/**
 * @author mallan
 */
public abstract class RapidRobotPartJoints extends RapidRobotPart {
    protected static Logger logger = Logger.getLogger(RapidRobotPartJoints.class);

    public static final float D2RAD    = (float)(Math.PI/180.0);
    public static final float RAD2D    = (float)(180.0/Math.PI);

    protected final Matrix3              m_rot         = new Matrix3(); // temporary rotation matrix
    protected final JointInfo[]          m_joints;
    protected float[]                    m_angleData   = null;
    protected JointSample                m_jointSample = null;
    public final MessageType             SAMPLE_TYPE;
    public final MessageType             CONFIG_TYPE;


    //===============================================================
    public RapidRobotPartJoints(String partName, RapidRobot parent, String participantId, MessageType messageType, int numJoints) {
        super(partName, parent, participantId);
        SAMPLE_TYPE = messageType;
        CONFIG_TYPE = MessageType.valueOf(messageType.getConfigName());
        m_joints = new JointInfo[numJoints];
    }

    @Override
    public MessageType[] rapidMessageTypes() { 
        return new MessageType[] { CONFIG_TYPE, SAMPLE_TYPE };
    }

    @Override
    public abstract void attachToNodesIn(Node model) throws IllegalStateException;

    @Override
    public void handleFrameUpdate(long currentTime) {
        if(isDirty()) {
            try {
                float[] ja = m_angleData;
                JointInfo joint;
                for(int i = 0; i < m_joints.length; i++) {
                    if(i < m_angleData.length) {
                        joint = m_joints[i];
                        if( joint.spatial != null && !Float.isNaN(ja[i]) ) {
                            m_rot.fromAngleNormalAxis(ja[i]+joint.offset, joint.rotAxis);
                            joint.spatial.setRotation(m_rot);
                        }
                    }
                }
            }
            catch(Throwable t) {
                t.printStackTrace();
            }
            setDirty(false);
        }
    }

    @Override
    public abstract void reset();

    protected abstract void setupJointInfo();

    protected void setAngle(double angle, JointInfo ji) {
        if(ji.spatial != null) {
            m_rot.fromAngleNormalAxis(angle+ji.offset, ji.rotAxis);
            ji.spatial.setRotation(m_rot);
        }
    }

    @Override
    public void onRapidMessageReceived(Agent agent, MessageType type, Object eventObj, Object configObj) {
        try {
            if(type.equals(SAMPLE_TYPE)) {
                m_jointSample = (JointSample)eventObj;
                final FloatSequence64 joints = m_jointSample.anglePos;
                if(m_angleData == null) {
                    m_angleData = new float[joints.userData.size()];
                }
                m_angleData = joints.userData.toArrayFloat(m_angleData);
                setDirty(true);
            }
        }
        catch(Throwable t) {
            logger.error("Error receiving RAPID event", t);
        }
    }
}
