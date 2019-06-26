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
package gov.nasa.arc.verve.robot.rapid.parts.concepts;

import gov.nasa.arc.verve.robot.rapid.RapidRobot;
import gov.nasa.arc.verve.robot.rapid.parts.RapidRobotPart;
import gov.nasa.arc.verve.utils.rapid.scenegraph.VerveFrameNode;
import gov.nasa.arc.verve.utils.rapid.scenegraph.VerveFrameNodeController;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.framestore.dds.RapidFrameStore;
import gov.nasa.rapid.v2.framestore.dds.updaters.JointFrameUpdater;
import gov.nasa.rapid.v2.framestore.dds.updaters.PositionFrameUpdater;
import gov.nasa.rapid.v2.framestore.tree.FrameTreeNode;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.scenegraph.Node;

public class RapidRobotPartFrames extends RapidRobotPart  {
    protected boolean m_frameStoreConfigReceived = false;
    protected boolean m_updatersInitialized      = false;
    protected VerveFrameNode           m_frameNode           = null;
    protected VerveFrameNodeController m_frameNodeController = null;
    
    protected boolean m_showRelationships = true;
    protected boolean m_showFrames  = true;
    protected boolean m_showText    = true;
    protected boolean m_showTextAtX = true;


    public RapidRobotPartFrames(String partName, RapidRobot parent,
                                String participantId) {
        super(partName, parent, participantId);
        // TODO Auto-generated constructor stub
    }

    @Override
    public MessageType[] rapidMessageTypes() { 
        return new MessageType[] { MessageType.FRAMESTORE_CONFIG_TYPE };
    }

    @Override
    public void onRapidMessageReceived(Agent agent, MessageType msgType, Object msgObj, Object cfgObj) {
        m_frameStoreConfigReceived = true;
        // RobotFrames will populate FrameStore
    }

    @Override
    public void attachToNodesIn(Node model) throws IllegalStateException {
        m_node = new Node(this.getPartName());
        getRobot().getRobotNode().getConceptsNode().attachChild(m_node);
    }

    @Override
    public void handleFrameUpdate(long currentTime) {
        if(m_frameStoreConfigReceived) {
            if(!m_updatersInitialized) {
                FrameTreeNode agentFrame = RapidFrameStore.instance().lookup(".../"+getRapidRobot().getAgent().name());
                if(agentFrame != null) {
                    m_frameNode = new VerveFrameNode(agentFrame, ColorRGBA.WHITE);
                    m_frameNode.setShowRelationships(m_showRelationships);
                    m_frameNode.setShowFrames(m_showFrames);
                    m_frameNode.setShowTextAtX(m_showTextAtX);
                    m_frameNode.setShowText(m_showText);
                    m_frameNodeController = new VerveFrameNodeController();
                    m_frameNode.addController(m_frameNodeController);

                    JointFrameUpdater jfu = new JointFrameUpdater(getParticipantId(),
                                                                  getRapidRobot().getAgent(),
                                                                  MessageType.JOINT_SAMPLE_TYPE);

                    PositionFrameUpdater pfu = new PositionFrameUpdater(getParticipantId(),
                                                                        getRapidRobot().getAgent(),
                                                                        MessageType.POSITION_SAMPLE_TYPE);
                    jfu.subscribe();
                    pfu.subscribe();
                    m_node.attachChild(m_frameNode);
                    m_updatersInitialized = true;
                }
            }
            if(m_frameNode != null) {
                m_frameNode.setTransform(getRobot().getPoseProvider().getTransform());
            }
        }
    }
    
    public boolean isShowRelationships() {
        return m_showRelationships;
    }
    
    public void setShowRelationships(boolean state) {
        m_showRelationships = state;
        if(m_frameNode != null) {
            m_frameNode.setShowRelationships(state);
        }
    }
    
    public boolean isShowFrames() {
        return m_showFrames;
    }
    
    public void setShowFrames(boolean state) {
        m_showFrames = state;
        if(m_frameNode != null) {
            m_frameNode.setShowFrames(state);
        }
    }
    
    public boolean isShowText() {
        return m_showText;
    }
    
    public void setShowText(boolean state) {
        m_showText = state;
        if(m_frameNode != null) {
            m_frameNode.setShowText(state);
        }
    }
    
    public boolean isShowTextAtX() {
        return m_showTextAtX;
    }
    
    public void setShowTextAtX(boolean state) {
        m_showTextAtX = state;
        if(m_frameNode != null) {
            m_frameNode.setShowTextAtX(state);
        }
    }
    
    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }

}
