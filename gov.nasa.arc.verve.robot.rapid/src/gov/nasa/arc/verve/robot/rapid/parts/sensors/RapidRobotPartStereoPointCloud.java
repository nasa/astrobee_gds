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
package gov.nasa.arc.verve.robot.rapid.parts.sensors;

import gov.nasa.arc.verve.robot.rapid.RapidRobot;
import gov.nasa.arc.verve.robot.scenegraph.shape.sensors.PointCloud;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.framestore.dds.RapidFrameStore;
import gov.nasa.rapid.v2.framestore.tree.FrameTreeNode;

import org.apache.log4j.Logger;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Transform;
import com.ardor3d.math.type.ReadOnlyTransform;


/**
 * 
 * @author mallan
 *
 */
public class RapidRobotPartStereoPointCloud extends RapidRobotPartPointCloud {
    private static final Logger logger    = Logger.getLogger(RapidRobotPartStereoPointCloud.class);

    protected final Transform   m_scamXfm = new Transform().setIdentity();
    protected final Transform   m_xfm     = new Transform().setIdentity();

    protected final String      m_frameName;

    public RapidRobotPartStereoPointCloud(String partName, RapidRobot parent, String participantId, MessageType msgType, String frameName) {
        super(partName, parent, participantId, msgType);
        m_historySize = 10;
        m_pointSize = 2;
        m_newColor.set(1.0f, 0.8f, 0.8f, 1.0f);
        m_oldColor.set(0.3f, 0.1f, 0.1f, 1.0f);
        allocateColors(m_historySize);
        m_frameName = frameName;
    }

    @Override
    public void updateTransform(PointCloud cloud, ReadOnlyTransform robotXfm) {
        if(m_scamXfm.isIdentity()) {
            updateStereoTransform();
        }
        robotXfm.multiply(m_scamXfm, m_xfm);
        cloud.setTransform(m_xfm);
    }

    Transform updateStereoTransform() {
        FrameTreeNode source   = RapidFrameStore.get().lookup(m_frameName);
        FrameTreeNode wrtFrame = RapidFrameStore.get().lookup(getRapidRobot().getAgent().name());
        if(!(source == null || wrtFrame == null)) {
            Transform axfm = RapidFrameStore.get().getTransform(wrtFrame, source);
            //Transform axfm = RapidVerve.toArdorTransform(xfm, new Transform());
            Matrix3 rot = new Matrix3( 1, 0, 0,
                                       0, 1, 0,
                                       0, 0, 1);
            Transform tmp = new Transform();
            tmp.setRotation(rot);
            tmp.multiply(axfm, m_scamXfm);
        }
        else {
            logger.debug("could not locate frame "+m_frameName);
        }
        return m_scamXfm;
    }

}
