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

import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.framestore.dds.RapidFrameHelper;
import gov.nasa.rapid.v2.framestore.dds.RapidFrameStore;
import gov.nasa.rapid.v2.framestore.tree.FrameTreeNode;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;

import org.apache.log4j.Logger;

import rapid.FRAME_NAME_ROVER;
import rapid.FRAME_NAME_SITE;
import rapid.PositionConfig;
import rapid.PositionSample;

import com.ardor3d.math.Transform;

/**
 * Updates Joint frames for an Agent 
 * @author mallan
 *
 */
public class PositionFrameUpdater implements IRapidMessageListener {
    private static final Logger logger = Logger.getLogger(PositionFrameUpdater.class);

    protected final String      m_participant;
    protected final Agent       m_agent;
    protected final MessageType m_sampleType;
    protected String 			m_jointGroupName;
    protected int               m_lastConfigSerialId = -13;
    protected FrameTreeNode     m_frame = null;
    protected final Transform   m_m44 = new Transform();

    FrameTreeNode               m_agentFrame;
    FrameTreeNode               m_siteFrame;

    /**
     * ROVER
     * @param agent
     * @param sample
     */
    public PositionFrameUpdater(String participantId, Agent agent, MessageType sample) {
        m_participant = participantId;
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
    public void onRapidMessageReceived(Agent agent, MessageType msgType, Object eventObj, Object configObj) {
        if(msgType.equals(m_sampleType)) {
            PositionConfig config = (PositionConfig)configObj;
            PositionSample sample = (PositionSample)eventObj;
            if(config != null) {
                if(config.hdr.serial != m_lastConfigSerialId) {
                    setupFromConfig(config);
                }
                if(m_frame != null) {
                    RapidFrameHelper.setTransform(sample.pose, m_m44);
                    m_frame.getFrame().setTransform(m_m44);
                }
            }
        }

    }

    protected void lookupAgentFrame() {
        m_agentFrame = RapidFrameStore.instance().lookup(".../"+m_agent.name());
        if(m_agentFrame != null) {
            // XXX is this always valid?
            m_siteFrame = m_agentFrame.getParent();
        }
    }

    public String substitute(String inString) {
        if(inString.equals(FRAME_NAME_ROVER.VALUE)) {
            return m_agent.name();
        }
        if(inString.equals(FRAME_NAME_SITE.VALUE)) {
            if(m_siteFrame != null) {
                return m_siteFrame.getFrame().getName();
            }
        }
        return inString;
    }

    public synchronized void setupFromConfig(PositionConfig config) {
        m_frame = null;
        FrameTreeNode frame;
        if(m_siteFrame == null) {
            lookupAgentFrame();
        }
        if(m_siteFrame != null) {
            String frameName = substitute(config.frameName);
            if(frameName.startsWith("/")) {
                frame = RapidFrameStore.instance().lookup(frameName, null);
            }
            else {
                frame = RapidFrameStore.instance().lookup(".../"+frameName);
            }
            if(frame == null) {
                logger.warn("could not locate frame for frameName: "+frameName);
                //agentFrame.traversePreOrder(new PrintFrameNamesVisitor());
            }
            else {
                m_frame = frame;
                m_lastConfigSerialId = config.hdr.serial;
                //logger.debug("  "+m_agent.name()+" position frame specified as "+config.frameName);
                //logger.debug("  "+m_agent.name()+" position frame resolved as "+FrameTree.getFullNameOf(m_frame));
            }
        }
    }

}
