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
package gov.nasa.arc.verve.robot.rapid;

import gov.nasa.arc.verve.robot.exception.TelemetryException;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;
import gov.nasa.rapid.v2.e4.util.RapidUtil;
import gov.nasa.rapid.v2.framestore.FrameStoreException;
import gov.nasa.rapid.v2.framestore.dds.RapidFrameHelper;
import gov.nasa.rapid.v2.framestore.dds.RapidFrameStore;
import gov.nasa.rapid.v2.framestore.tree.FrameTreeNode;
import gov.nasa.rapid.v2.framestore.tree.visitors.PrintFrameNamesVisitor;

import java.util.Map;

import org.apache.log4j.Logger;

import rapid.FrameStoreConfig;

/**
 * 
 * @author mallan
 *
 */
public class RapidRobotFrames implements IRapidMessageListener {
    private static final Logger logger = Logger.getLogger(RapidRobotFrames.class);

    final Agent  m_agent;
    final String m_participant;
    final MessageType msgType = MessageType.FRAMESTORE_CONFIG_TYPE;
    final boolean m_enabled = true;
    
    public static boolean printFrames = false;

    public RapidRobotFrames(String participantId, Agent agent) {
        m_participant = participantId;
        m_agent       = agent;
    }

    /**
     * @throws TelemetryException 
     * 
     */
    public void connectTelemetry() throws TelemetryException {
        if(m_enabled) {
            try {
                RapidMessageCollector.instance().addRapidMessageListener(m_participant,
                                                                         m_agent, 
                                                                         msgType, 
                                                                         this );
            } 
            catch (Throwable e) {
                TelemetryException te = new TelemetryException("Could not register for "+msgType.name()+" from "+m_agent.name(), e);
                throw te;
            }
        }
        else {
            logger.debug(this.getClass().getSimpleName()+".connectTelemetry() is disabled");
        }
    }

    /**
     * @throws TelemetryException 
     * 
     */
    public void disconnectTelemetry() throws TelemetryException {
        if(m_enabled) {
            try {
                RapidMessageCollector.instance().removeRapidMessageListener(m_participant,
                                                                            m_agent, 
                                                                            msgType, 
                                                                            this );
            } 
            catch (Throwable e) {
                TelemetryException te = new TelemetryException("Could not disconnect "+msgType.name()+" from "+m_agent.name(), e);
                throw te;
            }
        }
        else {
            logger.debug(this.getClass().getSimpleName()+".disconnectTelemetry() is disabled");
        }
    }

    @Override
    public void onRapidMessageReceived(Agent agent, MessageType msgType, Object eventObj, Object configObj) {
        FrameStoreConfig config = (FrameStoreConfig)eventObj;
        //        ArrayList<FrameParentPair> frameList = new ArrayList<FrameParentPair>();
        //        for(Object frameDefObject : config.frames.userData ) {
        //            FrameDef frameDef = (FrameDef)frameDefObject;
        //            Frame frame = new Frame(frameDef.name, RapidFrameHelper.newMatrix4d(frameDef.transform));
        //            frameList.add(new FrameParentPair(frame, frameDef.parent));
        //        }
        //        Map<String,FrameTreeNode> roots = RapidFrameHelper.makeTree(frameList);
        Map<String,FrameTreeNode> roots = RapidFrameHelper.makeTree(m_agent, config);
        for(String parentName : roots.keySet()) {
            FrameTreeNode root = roots.get(parentName);
            try {
                RapidFrameStore.instance().mergeTree(root, null);
            } 
            catch (FrameStoreException e) {
                logger.error(e);
            }
        }
        if(printFrames) {
            System.err.println("-- "+m_agent.name()+" FrameStoreConfig ------------------------------");
            System.err.println(RapidUtil.toString(config.hdr));
            RapidFrameStore.instance().applyVisitorPreOrder(new PrintFrameNamesVisitor());
        }


    }
}
