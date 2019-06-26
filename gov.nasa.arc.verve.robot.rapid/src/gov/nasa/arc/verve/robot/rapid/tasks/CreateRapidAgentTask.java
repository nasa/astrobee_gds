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
package gov.nasa.arc.verve.robot.rapid.tasks;

import gov.nasa.arc.irg.util.ui.IrgUI;
import gov.nasa.arc.verve.robot.rapid.RapidAvatarFactory;
import gov.nasa.arc.verve.robot.scenegraph.RobotNode;
import gov.nasa.rapid.v2.e4.agent.Agent;

import java.util.concurrent.Callable;

import com.ardor3d.scenegraph.Node;

/**
 * Task to create and attach a rapid robot 
 * @see com.ardor3d.util.GameTaskQueueManager
 * @author mallan
 *
 */
public class CreateRapidAgentTask implements Callable<Boolean> {
    final Node  m_siteFrame;
    final Agent m_agent;

    public CreateRapidAgentTask(Node siteFrame, Agent agent) { 
        m_siteFrame = siteFrame;
        m_agent = agent;
    }

    public Boolean call() throws Exception {
        boolean retVal = false;
        try {
            RobotNode robotNode = RapidAvatarFactory.buildAvatar(m_agent);
            m_siteFrame.attachChild(robotNode);
        }
        catch(Exception e) {
            IrgUI.errorDialog("Error creating RAPID agent", e.getMessage(), e);
            throw e;
        }
        return new Boolean(retVal);
    }
}
