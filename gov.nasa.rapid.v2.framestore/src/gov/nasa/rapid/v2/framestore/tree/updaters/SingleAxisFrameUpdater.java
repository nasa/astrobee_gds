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
package gov.nasa.rapid.v2.framestore.tree.updaters;

import com.ardor3d.math.Transform;
import com.ardor3d.math.Matrix3;

import gov.nasa.rapid.v2.framestore.tree.Frame;
import gov.nasa.rapid.v2.framestore.tree.FrameTreeNode;

public class SingleAxisFrameUpdater implements IFrameUpdater {
    protected final Transform m_trans = new Transform();
    protected Matrix3         m_rot = new Matrix3();
    protected double          m_column[] = new double[4];
    protected FrameTreeNode   m_node  = null;
    protected Frame.Axis      m_axis  = Frame.Axis.Z;
    protected double          m_radians = 0;
    
    public SingleAxisFrameUpdater(FrameTreeNode node, Frame.Axis axis, double radians) {
        set(node, axis, radians);
    }

    public void set(FrameTreeNode node, Frame.Axis axis, double radians) {
        m_node  = node;
        m_axis  = axis;
        m_radians = radians;
    }
    
    public void setRadians(double radians) {
        m_radians = radians;
    }
    
    public void apply() {
        m_rot.fromAngleAxis(m_radians, m_axis.vector);
        m_trans.setRotation(m_rot);
        m_trans.setTranslation(m_node.getFrame().getTransform().getTranslation());
        m_node.getFrame().setTransform(m_trans);
    }
}
