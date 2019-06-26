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
package gov.nasa.arc.verve.robot.parts.concepts;

import gov.nasa.arc.verve.common.VerveBaseMap;
import gov.nasa.arc.verve.robot.AbstractRobot;
import gov.nasa.arc.verve.robot.exception.TelemetryException;
import gov.nasa.arc.verve.robot.parts.AbstractRobotPart;

import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.scenegraph.shape.Sphere;

/**
 * 
 * @author mallan
 *
 */
public class RobotPartZTester extends AbstractRobotPart {
    //private static final Logger logger = Logger.getLogger(RobotPartZTester.class);

    protected float     m_zOff = 0;
    protected Vector3   m_xyzSite  = new Vector3();
    protected Vector3   m_xyzWrld  = new Vector3();

    protected double    m_scale = 10;
    protected double    m_speed = 1;

    protected Sphere    m_siteSphere;
    protected Sphere    m_wrldSphere;

    public RobotPartZTester(String partName, AbstractRobot parent, float zOffset) {
        super(partName, parent);
        m_zOff = zOffset;

        if(getRobot().getName().contains("Centaur2")) m_scale = 11;
        if(getRobot().getName().contains("KRex"))     m_scale = 13;
    }

    public double getScale() {
        return m_scale;
    }

    public void setScale(double scale) {
        m_scale = scale;
    }

    public double getSpeed() {
        return m_speed;
    }

    public void setSpeed(double speed) {
        m_speed = speed;
    }

    public float getZOffset() {
        return m_zOff;
    }

    public void setZOffset(float zOffset) {
        m_zOff = zOffset;
    }

    @Override
    public void attachToNodesIn(Node model) throws IllegalStateException {
        m_node = new Node("ZTester");

        MaterialState ms = new MaterialState();
        ms.setSpecular(getRobot().getIconColor());

        m_siteSphere = new Sphere("siteSphere", 10, 20, 1);
        m_siteSphere.setRenderState(ms);
        m_wrldSphere = new Sphere("wrldSphere", 3, 4, 1.4);
        m_wrldSphere.setRenderState(ms);

        m_node.attachChild(m_siteSphere);

        getRobot().getRobotNode().getConceptsNode().attachChild(m_node);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if(m_wrldSphere != null) {
            if(visible) {
                m_wrldSphere.getSceneHints().setCullHint(CullHint.Inherit);
            }
            else {
                m_wrldSphere.getSceneHints().setCullHint(CullHint.Always);
            }
        }
    }

    @Override
    public void handleFrameUpdate(long currentTime) {
        Vector3 tmp = new Vector3();
        if(m_wrldSphere.getParent() == null) {
            Node root = m_node;
            while(root.getParent() != null) {
                root = root.getParent();
            }
            root.attachChild(m_wrldSphere);
        }
        m_xyzSite.setX( m_scale * Math.sin(m_scale*9999 + m_speed*currentTime*0.0001) );
        m_xyzSite.setY( m_scale * Math.sin(m_scale*9999 + m_speed*currentTime*0.00017) );
        m_xyzSite.setZ( 0 );

        final ReadOnlyTransform siteToWorld = getRobot().siteToWorldTransform();

        siteToWorld.applyInverse(m_xyzSite, m_xyzWrld);
        VerveBaseMap.setZFromMap(m_xyzWrld, 0f);
        siteToWorld.applyForward(m_xyzWrld, tmp);
        m_xyzSite.setZ(tmp.getZ());

        m_wrldSphere.setTranslation(m_xyzWrld);
        m_siteSphere.setTranslation(m_xyzSite);   
    }

    @Override
    public void reset() {
        //
    }

    /**
     * @throws TelemetryException  
     */
    @Override
    public void connectTelemetry() throws TelemetryException {
        //  nothing
    }

    /**
     * @throws TelemetryException  
     */
    @Override
    public void disconnectTelemetry() throws TelemetryException {
        //  nothing
    }

}
