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
package gov.nasa.rapid.v2.framestore.dds.updaters;

import gov.nasa.rapid.v2.framestore.dds.RapidFrameStore;
import gov.nasa.rapid.v2.framestore.tree.Frame;
import gov.nasa.rapid.v2.framestore.tree.FrameTreeNode;
import gov.nasa.rapid.v2.framestore.tree.updaters.SingleAxisFrameUpdater;
import gov.nasa.rapid.v2.framestore.tree.visitors.PrintFrameNamesVisitor;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;
import gov.nasa.util.StrUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import rapid.JointConfig;
import rapid.JointDef;
import rapid.JointSample;

/**
 * Updates Joint frames for an Agent 
 * @author mallan
 *
 */
public class JointFrameUpdater implements IRapidMessageListener {
    private static final Logger logger = Logger.getLogger(JointFrameUpdater.class);

    protected final String      m_participant;
    protected final Agent       m_agent;
    protected final MessageType m_sampleType;
    protected String 			m_jointGroupName;
    public final int            BAD_SERIAL = -13;
    protected int               m_lastConfigSerialId = BAD_SERIAL;

    class FrameDefinition {
        public final FrameTreeNode frame;
        public final Frame.Axis    axis;
        public FrameDefinition(FrameTreeNode frame, Frame.Axis axis) {
            this.frame = frame;
            this.axis = axis;
        }
    }
    protected final List<FrameDefinition> m_frameDefs = new ArrayList<FrameDefinition>();

    /**
     * 
     * @param agent
     * @param sample
     */
    public JointFrameUpdater(String participant, Agent agent, MessageType sample) {
        m_participant = participant;
        m_agent       = agent;
        m_sampleType  = sample;
    }

    public void subscribe() {
        RapidMessageCollector.instance().addRapidMessageListener(m_participant, m_agent, m_sampleType, this);
    }

    public void unsubscribe() {
        RapidMessageCollector.instance().removeRapidMessageListener(m_participant, m_agent, m_sampleType, this);
    }

    @Override
    public void onRapidMessageReceived(Agent agent, MessageType msgType, Object event, Object configObj) {
        ArrayList<SingleAxisFrameUpdater> updates = new ArrayList<SingleAxisFrameUpdater>();
        if(msgType.equals(m_sampleType)) {
            JointSample sample = (JointSample)event;
            JointConfig config = (JointConfig)configObj;
            if(config != null) {
                if(config.hdr.serial != m_lastConfigSerialId) {
                    setupFromConfig(config);
                }
                else {
                    float[] pos = sample.anglePos.userData.toArrayFloat(new float[sample.anglePos.userData.size()]);
                    if(pos.length == m_frameDefs.size()) {
                        for(int i = 0; i < pos.length; i++) {
                            FrameDefinition fd = m_frameDefs.get(i);
                            if(fd.frame != null) {
                                updates.add(new SingleAxisFrameUpdater(fd.frame, fd.axis, pos[i]));
                            }
                        }
                    }
                    else {
                        logger.warn("length of joint position array does not match joint definitions");
                    }
                }
            }
            // update all the frames at once, this should go in a runnable
            // so we return from the Collector callback quickly
            RapidFrameStore.instance().updateFrames(updates);
        }

    }


    public synchronized void setupFromConfig(JointConfig jointConfig) {
        m_lastConfigSerialId = BAD_SERIAL;
        m_frameDefs.clear();
        m_jointGroupName = jointConfig.jointGroupName;
        FrameTreeNode agentFrame = RapidFrameStore.instance().lookup(".../"+m_agent.name());
        if(agentFrame != null) {
            for(Object jointDefObject : jointConfig.jointDefinitions.userData ) {
                JointDef jointDef = (JointDef)jointDefObject;
                FrameTreeNode frame = null;
                String frameName = jointDef.frameName;
                frameName = StrUtil.upperFirstChar(frameName, false);
                //logger.debug("forcing name to upper first character");
                if(frameName.startsWith("/")) {
                    frame = RapidFrameStore.instance().lookup(frameName, null);
                }
                else {
                    frame = RapidFrameStore.instance().lookup(".../"+frameName, agentFrame);
                }

                if(frame == null) {
                    logger.warn("could not locate frame for frameName: "+frameName);
                    agentFrame.traversePreOrder(new PrintFrameNamesVisitor());
                }
                Frame.Axis axis = getAxis(jointDef.dof, frameName);
                m_frameDefs.add(new FrameDefinition(frame, axis));
            }
            m_lastConfigSerialId = jointConfig.hdr.serial;
        }
    }

    protected Frame.Axis getAxis(String dof, String frameName) {
        String lower = dof.toLowerCase();
        if(lower.equals("x"))      return Frame.Axis.X;
        else if(lower.equals("y")) return Frame.Axis.Y;
        else if(lower.equals("z")) return Frame.Axis.Z;
        else {
            //logger.warn("could not interpret dof : "+dof);
            if(lower.equals("roll"))  return Frame.Axis.X;
            if(lower.equals("pitch")) return Frame.Axis.NEG_Y;
            if(lower.equals("yaw"))   return Frame.Axis.Z;
        }
        return Frame.Axis.Z;
    }


}
