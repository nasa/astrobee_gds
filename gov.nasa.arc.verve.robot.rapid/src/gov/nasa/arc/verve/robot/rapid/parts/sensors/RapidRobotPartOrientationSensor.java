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

import gov.nasa.arc.verve.ardor3d.scenegraph.shape.TexRing;
import gov.nasa.arc.verve.common.ardor3d.util.RotUtil;
import gov.nasa.arc.verve.common.DataBundleHelper;
import gov.nasa.arc.verve.robot.rapid.RapidRobot;
import gov.nasa.arc.verve.robot.rapid.parts.RapidRobotPart;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.ardor3d.image.Texture.MagnificationFilter;
import com.ardor3d.image.Texture.MinificationFilter;
import com.ardor3d.image.Texture.WrapMode;
import com.ardor3d.image.Texture2D;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.scenegraph.hint.PickingHint;

/**
 * base class for orientation sensors such as Compass and SunTracker
 *
 */
public abstract class RapidRobotPartOrientationSensor extends RapidRobotPart {
	protected static Logger logger = Logger.getLogger(RapidRobotPartOrientationSensor.class);
	
    static Texture2D s_tex = null;

    TexRing			m_ring = null;
    long			m_telemTime = 0;
    long			m_resetTime = 5000;
    long 			m_fadeTime  = 2000;
    Vector3			m_xyz 		= new Vector3();
    Vector3			m_rpy		= new Vector3();
    Matrix3			m_rot		= new Matrix3();
    int				m_isActive	= 1;
    float           m_ringRadius;
    float           m_ringThick;
    float           m_ringZOff;

    public RapidRobotPartOrientationSensor(String partId, RapidRobot parent, String participantId, float radius, float thick, float zOff) {
        super(partId, parent, participantId);
        m_ringRadius = radius;
        m_ringThick = thick;
        m_ringZOff = zOff;
    }

    /**
     * initialization of the TexRing must be specialized in the subclass
     * @return a new()ed TexRing
     */
    protected abstract TexRing createTexRing(float radius, float thickness, float zOff);

    /**
     * attach to robot model
     */
    @Override
    public void attachToNodesIn(Node model) throws IllegalStateException {
        getRobot().getPoseProvider().getXyz(m_xyz);

        if(s_tex == null) {
            try {
                s_tex = DataBundleHelper.loadTexture("robot", "images/OrientationSensor.png",
                        WrapMode.EdgeClamp, 
                        MinificationFilter.Trilinear, 
                        MagnificationFilter.Bilinear, 
                        0.5f);
            } 
            catch (IOException e) {
                throw new IllegalStateException(this.getClass().getSimpleName()+": Could not locate texture", e);
            }
        }

        m_ring = createTexRing(m_ringRadius, m_ringThick, m_ringZOff);
        m_ring.getSceneHints().setPickingHint(PickingHint.Pickable, false);
        m_ring.getSceneHints().setPickingHint(PickingHint.Collidable, false);

        m_node = new Node(getPartName());
        m_node.setTranslation(0,0,-0.2);
        m_node.attachChild(m_ring);

        getRobot().getRobotNode().getSensorsNode().attachChild(m_node);
    }

    /**
     * @return true if visible, false if invisible or in transition to invisible
     */
    boolean isActive() { 
        return m_isActive == 1;
    }

    /**
     * set visibility state and apply transition effect
     */
    void setActive(boolean visible, long elapsed) {
        if(visible) {
            m_ring.getSceneHints().setCullHint(CullHint.Inherit);
            m_ring.setAlpha(1);
            m_isActive = 1;
        }
        else {
            if(m_isActive == 1) { // state change to fade
                m_isActive = -1;
            }

            if(m_isActive == -1) {
                if(elapsed > m_resetTime+m_fadeTime) {
                    m_isActive = 0;
                    m_ring.getSceneHints().setCullHint(CullHint.Always);
                }
                else {
                    float fade = 1f - ((float)(elapsed-m_resetTime))/(float)m_fadeTime;
                    m_ring.setAlpha(fade);
                }				
            }
        }
    }

    @Override
    public void handleFrameUpdate(long currentTime) {
        if(isDirty()) {
            m_telemTime = currentTime;
        }
        
        // determine visibility state
        long elapsed = currentTime - m_telemTime;
        if(this.isActive()) {			
            if(elapsed > m_resetTime) {
                setActive(false, elapsed);
            }
        }
        else {
            if(elapsed < m_resetTime) {
                setActive(true, elapsed);
            }
            else { // transition animation requires repeated calls
                setActive(false, elapsed);
            }
        }

        // if visible, update transform
        if(isActive()) {
            getRobot().getPoseProvider().getXyz(m_xyz);
            m_xyz.addLocal(0,0,m_ringZOff);
            m_ring.setTranslation(m_xyz);
            if(isDirty()) {
                synchronized(m_rpy) {
                    m_ring.setRotation(RotUtil.toMatrixXYZ(m_rpy, m_rot));
                }
                setDirty(false);
            }
        }
    }

    boolean hasNaN(float[] values) {
        boolean retVal = false;
        for(int i = 0; i < values.length; i++) {
            if(values[i] != values[i]) {
                retVal = true;
                break;
            }
        }
        return retVal;
    }

    @Override
    public void reset() {
        synchronized(m_rpy) {
            m_rpy.set(0,0,0);
        }
    }

}
