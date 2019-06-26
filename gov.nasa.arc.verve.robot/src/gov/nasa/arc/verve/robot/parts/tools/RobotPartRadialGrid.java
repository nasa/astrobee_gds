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

import gov.nasa.arc.verve.ardor3d.scenegraph.shape.RadialGrid;
import gov.nasa.arc.verve.robot.AbstractRobot;
import gov.nasa.arc.verve.robot.parts.AbstractRobotPart;

import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.scenegraph.Node;

/**
 * 
 * @author mallan
 *
 */
public class RobotPartRadialGrid extends AbstractRobotPart {

    double          m_innerRadius =  1;
    double          m_outerRadius = 20;
    float           m_lineWidth   =  2;
    float           m_alpha       =  1;
    float           m_radialGridAAlpha = 0.25f;
    RadialGrid      m_radialGridA;
    RadialGrid      m_radialGridB;

    enum ColorScheme {
        Icon,
        Cyan,
        Gold,
        Magenta, 
        ;
        public static ColorScheme fromOrdinal(int ordinal) {
            ordinal = Math.abs(ordinal%3);
            for(ColorScheme scheme : ColorScheme.values()) {
                if(scheme.ordinal() == ordinal) {
                    return scheme;
                }
            }
            return Icon;
        }
    }

    public RobotPartRadialGrid(String partId, AbstractRobot parent) {
        this(partId, parent, 1, 20);
    }
    public RobotPartRadialGrid(String partId, AbstractRobot parent, double innerRadius, double outerRadius) {
        super(partId, parent);
        m_innerRadius = innerRadius;
        m_outerRadius = outerRadius;
        m_radialGridA = new RadialGrid("radialGridA", m_innerRadius, m_outerRadius, getRobot().getIconColor(), m_alpha * m_radialGridAAlpha);
        m_radialGridB = new RadialGrid("radialGridB", m_innerRadius, m_outerRadius, getRobot().getIconColor(), m_alpha);
    }

//    public boolean isDepthTest() {
//        return m_radialGridA.isDepthTest();
//    }
//
//    public void setDepthTest(boolean status) {
//        m_radialGridA.setDepthTest(status);
//    }
    
    public float getAlpha() {
        return m_alpha;
    }
    
    public void setAlpha(float alpha) {
        if(alpha > 1)
            m_alpha = 1;
        else if(alpha < 0)
            m_alpha = 0;
        else
            m_alpha = alpha;
        m_radialGridA.setAlphaMultiplier(m_alpha * m_radialGridAAlpha);
        m_radialGridB.setAlphaMultiplier(m_alpha);
        setDirty(true);
    }

    public void setInnerRadius(double radius) {
        m_innerRadius = radius;
        setDirty(true);
    }

    public double getInnerRadius() {
        return m_innerRadius;
    }

    public void setOuterRadius(double radius) {
        m_outerRadius = radius;
        setDirty(true);
    }
    public double getOuterRadius() {
        return m_outerRadius;
    }

    public ColorScheme getColorScheme() {
        return ColorScheme.fromOrdinal(m_radialGridA.getColorScheme());
    }
    public void setColorScheme(ColorScheme scheme) {
        m_radialGridA.setColorScheme(scheme.ordinal());
        m_radialGridA.setRadius(m_innerRadius, m_outerRadius);
        m_radialGridB.setColorScheme(scheme.ordinal());
        m_radialGridB.setRadius(m_innerRadius, m_outerRadius);
    }
    
    public float getLineWidth() {
        return m_lineWidth;
    }
    
    public void setLineWidth(float lineWidth) {
        m_lineWidth = lineWidth;
        if(m_radialGridA != null) m_radialGridA.setLineWidth(lineWidth);
        if(m_radialGridB != null) m_radialGridB.setLineWidth(lineWidth);
    }

    @Override
    public void attachToNodesIn(Node model) throws IllegalStateException {
        m_node = new Node(getPartName());

        m_node.attachChild(m_radialGridA);
        m_node.attachChild(m_radialGridB);
        m_radialGridA.setDepthTest(false);
        m_radialGridA.setLineWidth(m_lineWidth);
        m_radialGridA.getSceneHints().setRenderBucketType(RenderBucketType.PostBucket);

        m_radialGridB.setDepthTest(true);
        m_radialGridB.setLineWidth(m_lineWidth);
        m_radialGridB.setAntialiased(true);
        m_radialGridB.getSceneHints().setRenderBucketType(RenderBucketType.Transparent);

        getRobot().getRobotNode().getModelNode().attachChild(m_node);
        setDirty(true);
    }

    @Override
    public void handleFrameUpdate(long currentTime) {
        if(isDirty()) {
            m_radialGridA.setRadius(m_innerRadius, m_outerRadius);
            m_radialGridB.setRadius(m_innerRadius, m_outerRadius);
            setDirty(false);
        }
    }

    @Override
    public void reset() {
        // nothing to do
    }

    @Override 
    public void connectTelemetry() {
        // no telemetry connection required
    }

    @Override
    public void disconnectTelemetry() {
        // no telemetry connection required
    }

}
