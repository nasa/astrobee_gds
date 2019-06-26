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
package gov.nasa.arc.verve.robot.parts.tools;

import gov.nasa.arc.verve.ardor3d.scenegraph.shape.AxisLines;
import gov.nasa.arc.verve.robot.AbstractRobot;
import gov.nasa.arc.verve.robot.parts.AbstractRobotPart;

import java.util.Random;

import com.ardor3d.bounding.BoundingVolume;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Node;

public class RobotPartAxes extends AbstractRobotPart {
    private static Random random = new Random();

    private long          m_lastTime = 0;
    private long          m_updateTime = 10000 - random.nextLong()%500;
    private AxisLines     m_axis = null;
    private final Vector3 m_offset = new Vector3();

    public RobotPartAxes(String partId, AbstractRobot parent) {
        super(partId, parent);
    }

    public RobotPartAxes(String partId, AbstractRobot parent, ReadOnlyVector3 offset) {
        super(partId, parent);
        m_offset.set(offset);
    }

    @Override
    public void attachToNodesIn(Node model) throws IllegalStateException {
        m_node = new Node(getRobot().getName()+"AxesNode");
        m_axis = new AxisLines("Axes", 0.5f);
        m_node.attachChild(m_axis);
        getRobot().getRobotNode().getModelNode().attachChild(m_node);
    }

    @Override
    public void handleFrameUpdate(long currentTime) {
        if( (currentTime-m_lastTime) > m_updateTime ) {
            setDirty(true);
        }

        if(isDirty() && m_axis != null) {
            m_axis.setTranslation(m_offset);
            Node model = getRobot().getRobotNode().getModelNode();
            model.updateWorldBound(true);
            BoundingVolume bound = model.getWorldBound();
            if(bound != null) {
                double size = 1;
                double volume = bound.getVolume();
                size = 0.5 * Math.cbrt(volume); 
                if( !(Double.isInfinite(size) || Double.isNaN(size)) ) {
                    m_axis.setSize(size);
                    setDirty(false);
                    m_lastTime = currentTime;
                }
            }
        }
    }
    
    public void setOffset(double x, double y, double z) {
        m_offset.set(x,y,z);
    }

    public void setOffset(ReadOnlyVector3 vec) {
        m_offset.set(vec);
    }
    
    public ReadOnlyVector3 getOffset() {
        return m_offset;
    }

    @Override 
    public void connectTelemetry() {
        // no telemetry connection required
    }

    @Override
    public void disconnectTelemetry() {
        // no telemetry connection required
    }

    @Override
    public void reset() {
        setDirty(true);
    }

}
