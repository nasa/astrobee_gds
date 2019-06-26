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
package gov.nasa.arc.verve.robot;

import gov.nasa.arc.verve.ardor3d.scenegraph.util.SpatialPath;
import gov.nasa.arc.verve.common.interest.InterestPointListener;
import gov.nasa.arc.verve.common.interest.InterestPointProvider;
import gov.nasa.arc.verve.robot.exception.TelemetryException;
import gov.nasa.arc.verve.robot.parts.IRobotPart;
import gov.nasa.arc.verve.robot.scenegraph.RobotNode;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.ardor3d.image.Texture;
import com.ardor3d.image.Texture.MagnificationFilter;
import com.ardor3d.image.Texture.MinificationFilter;
import com.ardor3d.image.Texture.WrapMode;
import com.ardor3d.image.Texture2D;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.resource.URLResourceSource;

public abstract class AbstractRobot implements InterestPointProvider {
    private static Logger logger = Logger.getLogger(AbstractRobot.class);

    protected final ColorRGBA m_iconColor   = new ColorRGBA(1,1,0,1);
    protected Transform       m_siteToWorld = null;
    protected Transform       m_worldToSite = null;

    public abstract String       getName();
    public abstract String[]     getPartNames();
    public abstract IRobotPart   getPart(String name);

    /** 
     * name of node in model where 'concept' parts can attach, e.g. 
     * pose history, survey plan, etc. 
     */
    public final static String CONCEPTS_ROOT_NAME = "ConceptsRoot";
    public final static String SENSORS_ROOT_NAME  = "SensorsRoot";
    public final static String MODEL_ROOT_NAME    = "ModelRoot";
    public final static String ROOT_NAME          = "RobotRoot";

    public final static String SEP = ":";

    public abstract IPoseProvider getPoseProvider();
    
    public abstract boolean isTelemetryEnabled();
    public abstract void setTelemetryEnabled(boolean state) throws TelemetryException;

    /** this method attaches the logical model to the 3D model in the scene 
     * @throws TelemetryException */
    public abstract void attachToNodesIn(Node model) throws IllegalStateException, TelemetryException;
    /** 
     * this method should be called every frame to sync scenegraph components with logical model 
     * Also, a call should be made to updateInterestPointListeners()
     */
    public abstract void handleFrameUpdate(long currentTimeMillis);

    /**
     * matrix to go from robot site frame to world coords. 
     * @return null if robot has not yet been attached to scenegraph 
     */
    public ReadOnlyTransform siteToWorldTransform() {
        if(m_siteToWorld == null) {
            if(worldToSiteTransform() != null) { 
                m_siteToWorld = worldToSiteTransform().invert(new Transform());
                //logger.debug("siteToWorld="+Ardor3D.format(m_siteToWorld));
            }
            else {
                logger.warn(getRobotNode().getName()+" does not have a parent");
            }
        }
        return m_siteToWorld;
    }

    /**
     * matrix to go from world to robot site frame coords. 
     * @return null if robot has not yet been attached to scenegraph 
     */
    public ReadOnlyTransform worldToSiteTransform() {
        if(m_worldToSite == null) {
            if(getRobotNode().getParent() != null) {
                SpatialPath path = new SpatialPath(getRobotNode(), null, false);
                m_worldToSite = path.getTransform(new Transform());
            }
            else {
                logger.warn(getRobotNode().getName()+" does not have a parent");
            }
        }
        return m_worldToSite;
    }

    /** get the top node representing the robot in the scenegraph */
    public abstract RobotNode getRobotNode();

    public ReadOnlyColorRGBA getIconColor() {
        return m_iconColor;
    }

    //== Interest Point ===================================
    protected HashMap<InterestPointListener,String> m_ipListeners = new HashMap<InterestPointListener,String>();
    protected enum InterestPointMode {
        Free,
        Follow,
        NavGoal,
        ;
    }
    public class InterestPointValues {
        public Vector3 primary = new Vector3();
        public Vector3 secondary = new Vector3();
    }
    protected InterestPointValues[] m_interestPoints = new InterestPointValues[]  {
            new InterestPointValues(), 
            new InterestPointValues(), 
            new InterestPointValues() };

    /** return robot name by default */
    public String getInterestPointName() {
        return getName();
    }
    /** validate mode and add listener */
    public void addInterestPointListener(InterestPointListener listener, String mode) {
        m_ipListeners.put(listener, mode);
        updateInterestPointListeners();
    }
    public void removeInterestPointListener(InterestPointListener listener) {
        m_ipListeners.remove(listener);  
    }

    /** get default interest point modes */
    public String[] getInterestPointModes() {
        String[] retVal = new String[InterestPointMode.values().length];
        int i = 0;
        for(InterestPointMode mode : InterestPointMode.values())
            retVal[i++] = mode.toString();
        return retVal;
    }

    public void updateInterestPointListeners() {
        if( m_ipListeners.size() > 0 ) {
            updateInterestPoints();
            for(InterestPointListener ipl : m_ipListeners.keySet()) {
                try {
                    InterestPointMode mode  = InterestPointMode.valueOf(m_ipListeners.get(ipl));
                    InterestPointValues ipv = m_interestPoints[mode.ordinal()];
                    ipl.interestPointUpdated(this, mode.toString(), ipv.primary, ipv.secondary);
                }
                catch(Throwable t) {
                    logger.warn("error updating InterestPointListener", t);
                }
            }
        }
    }
    
    public int getInterestPointFollowAxis() {
        return m_interestPointFollowAxis;
    }
    /** set interest point axis. 0=x, 1=y, 2=z */
    public void setInterestPointFollowAxis(int axis) {
        m_interestPointFollowAxis = axis;
    }
    /** get interest point vector multiplier */
    public float getInterestPointFollowMult() {
        return m_interestPointFollowMult;
    }
    /**
     * Set interest point vector multiplier. Typically 5 or -5
     * @param mult
     */
    public void setInterestPointFollowMult(float mult) {
        m_interestPointFollowMult = mult;
    }

    protected int   m_interestPointFollowAxis = 2;
    protected float m_interestPointFollowMult = 5;
    
    @SuppressWarnings("deprecation")
    protected void updateInterestPoints() {
        InterestPointValues ip;
        Node base = getRobotNode().getBaseNode();

        // XXX FIXME : deprecate getBaseNode()
        if(base == null) {
            base = getRobotNode().getModelNode();
            m_interestPointFollowAxis = 0;
        }
        if(base == null) {
            logger.debug(getName() + " could not update interest points, base is null");
            return;
        }
        if(base.getNumberOfChildren() < 1) {
            logger.debug(getName() + " could not update interest points, base has no children");
            return;
        }
        Spatial child = base.getChild(0);
        ReadOnlyTransform xfm = child.getWorldTransform();
        t_primary.set(xfm.getTranslation());

        ip = m_interestPoints[InterestPointMode.Free.ordinal()];
        ip.primary.set(t_primary);
        ip.secondary = null;

        ip = m_interestPoints[InterestPointMode.Follow.ordinal()];
        xfm.getMatrix().getColumn(m_interestPointFollowAxis, t_secondary);
        //xfm.getMatrix().getColumn(0, t_secondary);
        //System.out.println("t_secondary = "+xfm.getMatrix());
        t_secondary.multiplyLocal(m_interestPointFollowMult);
        t_secondary.addLocal(t_primary);
        ip.primary.set(t_primary);
        ip.secondary.set(t_secondary);

        ip = m_interestPoints[InterestPointMode.NavGoal.ordinal()];
        ReadOnlyVector3 pos = getNextGoalPosition();
        ip.primary.set(t_primary);
        if(pos != null) {
            t_nextgoal.set(pos);
            double dSq = t_nextgoal.distanceSquared(t_primary);
            if( dSq < 4 ) {
                double a = 1-dSq/4;
                t_nextgoal.lerpLocal(t_secondary, a);
            }
            ip.secondary.set(t_nextgoal);
        }
        else {
            ip.secondary.set(t_secondary);
        }
    }
    
    protected final Vector3 t_primary   = new Vector3();
    protected final Vector3 t_secondary = new Vector3();
    protected final Vector3 t_nextgoal  = new Vector3();

    abstract protected ReadOnlyVector3 getNextGoalPosition();
    
    /**
     * compare by name
     */
	public int compareTo(InterestPointProvider arg0) {
		return getInterestPointName().compareTo(arg0.getInterestPointName());
	}

    public static Texture2D getTex(String imageName) {
        return getTex(imageName, WrapMode.EdgeClamp, MinificationFilter.Trilinear, MagnificationFilter.Bilinear, 0);
    }
    
    public static Texture2D getTex(String imageName,
                                   Texture.WrapMode wrapMode, 
                                   Texture.MinificationFilter minFilter, 
                                   Texture.MagnificationFilter magFilter, 
                                   float anisotropy) {
        Texture2D texture = null;
        URLResourceSource rs = null;
        rs = new URLResourceSource(AbstractRobot.class.getResource(imageName));
        texture = (Texture2D)TextureManager.load(rs, minFilter, false);
        texture.setMagnificationFilter(magFilter);
        texture.setWrap(wrapMode);
        texture.setAnisotropicFilterPercent(anisotropy);
        return texture;
    }
}
