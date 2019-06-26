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

import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Node;

public class RobotPartSiteStableAxes extends AbstractRobotPart {

    private AxisLines     m_axis = null;
    private float         m_size = 5;
    private float         m_width = 1;

    public RobotPartSiteStableAxes(String partId, AbstractRobot parent) {
        super(partId, parent);
    }

    public RobotPartSiteStableAxes(String partId, AbstractRobot parent, ReadOnlyVector3 offset) {
        super(partId, parent);
    }

    @Override
    public void attachToNodesIn(Node model) throws IllegalStateException {
        m_node = new Node(getRobot().getName()+"AxesNode");
        m_axis = new AxisLines("Axes", 1);
        m_axis.setLineWidth(m_width);
        m_axis.setSize(m_size);
        m_node.attachChild(m_axis);
        getRobot().getRobotNode().getSensorsNode().attachChild(m_node);
    }

    public float getSize() {
        return m_size;
    }

    public void setSize(float size) {
        m_size = size;
        if(m_axis != null) {
            m_axis.setSize(m_size);
        }
    }
    
    public float getLineWidth() {
        return m_width;
    }

    public void setLineWidth(float width) {
        m_width = width;
        if(m_axis != null) {
            m_axis.setLineWidth(m_width);
        }
    }

    @Override
    public void handleFrameUpdate(long currentTime) {
        if(m_isVisible && m_axis != null) {
            m_axis.setTranslation(getRobot().getPoseProvider().getXyz());
        }
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
