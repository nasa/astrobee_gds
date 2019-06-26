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
package gov.nasa.arc.verve.robot.rapid.scenario;

import gov.nasa.arc.verve.common.VerveTask;
import gov.nasa.arc.verve.robot.rapid.RapidAvatarFactory;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;

/** 
 * quick hack for attaching robots to scenegraph. 
 * will use framestore later
 */
class RapidAvatarCreator implements IRapidMessageListener {
    private static Logger logger = Logger.getLogger(RapidAvatarCreator.class);
    
    boolean called = false;
    final Node        siteFrame; 
    final MessageType type = MessageType.POSITION_CONFIG_TYPE;
    final String      participantId;

    public RapidAvatarCreator(String participantId, Agent agent, Node siteFrame) {
        this.participantId = participantId;
        this.siteFrame = siteFrame;
        RapidMessageCollector.instance().addRapidMessageListener(participantId, agent, type, this);
    }
    public synchronized void onRapidMessageReceived(final Agent agent, MessageType type, Object eventObj, Object configObj) {
        logger.debug("RapidAvatarCreator "+agent.name()+" "+type.name()+" received");
        // don't create the robot here, because it needs to 
        // register with the RapidMessageCollector (and we're already in a 
        // RapidMessageCollector callback)
        if(!called) {
            logger.debug("RapidAvatarCreatorCallable : "+agent.name());
            VerveTask.asyncExec(new RapidAvatarCreatorCallable(siteFrame, agent, this, type));
        }
        called = true;
    }
    
    public class RapidAvatarCreatorCallable implements Callable<Integer> {
        final Node  siteFrame;
        final Agent agent;
        final MessageType type;
        IRapidMessageListener listener;

        public RapidAvatarCreatorCallable(Node siteFrame, Agent agent, IRapidMessageListener listener, MessageType type) {
            this.siteFrame = siteFrame;
            this.agent     = agent;
            this.type      = type;
        }
        public Integer call() throws Exception {
            int retVal = -1;
            try {
                logger.debug("RapidAvatarCreatorCallable: RapidAvatarFactory.buildAvatar("+agent.name()+")");
                Spatial robot = RapidAvatarFactory.buildAvatar(agent);
                retVal = siteFrame.attachChild(robot);
                logger.debug("RapidAvatarCreatorCallable: attached to site frame");
                logger.debug("RapidAvatarCreatorCallable: unsubscribing "+agent.name()+" from "+type.name());
                if(listener != null) {
                    RapidMessageCollector.instance().removeRapidMessageListener(participantId, agent, type, listener);
                }
                retVal = 0;
            }
            catch(Throwable t) {
                logger.error("Error creating robot avatar for "+agent.name(), t);
            }
            return new Integer(retVal);
        }

    }


}

