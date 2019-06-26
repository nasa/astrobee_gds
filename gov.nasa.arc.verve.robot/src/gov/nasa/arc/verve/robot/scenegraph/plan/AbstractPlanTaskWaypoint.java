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
package gov.nasa.arc.verve.robot.scenegraph.plan;

import gov.nasa.arc.verve.common.ardor3d.Ardor3D;
import gov.nasa.arc.verve.ardor3d.scenegraph.shape.LathedCylinder;
import gov.nasa.arc.verve.ardor3d.scenegraph.shape.TexRing;
import gov.nasa.arc.verve.robot.AbstractRobot;
import gov.nasa.arc.verve.robot.parts.concepts.plan.AbstractPlanOverview.Verbosity;
import gov.nasa.arc.verve.robot.parts.concepts.plan.PlanState;
import gov.nasa.arc.verve.robot.parts.concepts.plan.Task;
import gov.nasa.arc.verve.robot.scenegraph.shape.concepts.OffsetQuad;
import gov.nasa.util.Colors;

import java.util.Random;

import com.ardor3d.image.Texture.WrapMode;
import com.ardor3d.image.Texture2D;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector2;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.scenegraph.hint.PickingHint;

public abstract class AbstractPlanTaskWaypoint extends Task {
    protected LathedCylinder    m_centerCylinder;
    protected TexRing           m_toleranceRing;
    protected OffsetQuad        m_directionMarker;

    static protected Texture2D  s_toleranceTexture;
    static protected Texture2D  s_directionTexture;

    protected ColorRGBA         m_baseColor  = new ColorRGBA(1,1,1,1);
    float                       m_toleranceHeight = -0.1f;
    float                       m_toleranceThickness = 0.3f;
    boolean                     m_isDirectional = false;
    
    final static Random random = new Random();

    public AbstractPlanTaskWaypoint(String taskName, Object data, PlanState planState, Object parentKey) {
        super(taskName, data, planState, parentKey);
        synchronized(this) {
            if(s_directionTexture == null) {
                s_directionTexture = AbstractRobot.getTex("pointRight.png");
                s_directionTexture.setAnisotropicFilterPercent(1);
            }
            if(s_toleranceTexture == null) {
                s_toleranceTexture = AbstractRobot.getTex("WaypointRingThinDots.png");
                s_toleranceTexture.setWrap(WrapMode.Repeat);
                s_toleranceTexture.setAnisotropicFilterPercent(1);
            }
        }
        m_centerCylinder = createCenterCylinder();
        m_toleranceRing = createToleranceRing();
        m_directionMarker = createDirectionMarker();

        CullState cs = new CullState();
        cs.setCullFace(CullState.Face.None);
        m_node.setRenderState(cs);

        BlendState bs = new BlendState();
        bs.setBlendEnabled(false);
        bs.setTestEnabled(true);
        bs.setTestFunction(BlendState.TestFunction.GreaterThan);
        bs.setReference(0.3f);
        m_node.setRenderState(bs);

        m_node.attachChild(m_centerCylinder);
        m_node.attachChild(m_toleranceRing);
        m_node.attachChild(m_directionMarker);

        setStatus(m_taskStatus);
    }

    @Override
    public void setStatus(TaskStatus status) {
        if(status != m_taskStatus) {
            setColorFromStatus(status);
            setVisibilityFromStatus(status);
        }
        super.setStatus(status);
    }

    @Override
    public void setVerbosity(Verbosity verbosity) {
        super.setVerbosity(verbosity);
        setStatus(m_taskStatus);
    }

    public void setTolerance(float radius) {
        float th = m_toleranceThickness/2;
        m_toleranceRing.setRadius(radius-th, radius+th);
    }

    protected void setIsDirectional(boolean isDirectional) {
        m_isDirectional = isDirectional;
        if(m_isDirectional) {
            m_directionMarker.getSceneHints().setCullHint(CullHint.Inherit);
        }
        else {
            m_directionMarker.getSceneHints().setCullHint(CullHint.Always);
        }
    }

    protected void setDirection(double heading) {
        Matrix3 rot = Matrix3.fetchTempInstance();
        rot.fromAngleNormalAxis(heading, Vector3.UNIT_Z);
        m_directionMarker.setRotation(rot);
        Matrix3.releaseTempInstance(rot);
        
//        Vector3 vec = new Vector3();
//        rot.getColumn(0, vec);
//        vec.setZ(0);
//        vec.normalizeLocal();
//        double a1 = Math.atan(vec.getY()/vec.getX());
//        double a2 = Math.atan2(vec.getY(), vec.getX());
//        double a3 = heading%(2*Math.PI);
//        if(a1 < 0) a1 += 2*Math.PI;
//        if(a2 < 0) a2 += 2*Math.PI;
//        if(a3 < 0) a3 += 2*Math.PI;
//        System.err.println(a1+" "+a2+" "+a3);
    }

    public LathedCylinder createCenterCylinder() {
        LathedCylinder retVal = new LathedCylinder("wpCenter", 0.2f) { 
            @Override
            protected  void lathe(Vector3 vert, Vector2 tex, float rowpos, float colpos) {
                double z = vert.getZ();
                if(z <= -0.25) {
                    vert.setX(vert.getX() * 0.4 * (1+z) );
                    vert.setY(vert.getY() * 0.4 * (1+z) );
                }
                float rl = 1-rowpos;
                float rr = rl*rl;
                vert.setZ(m_bot + (1-rr) * m_rng);
            }
        };
        //retVal.setExtents(-0.15f, 0.27f);
        retVal.setExtents(-0.4f, -0.1f);
        retVal.setTesselation(16, 3);
        retVal.setEndCaps(true,false);
        retVal.initialize();
        retVal.setRenderState(new MaterialState());
        //double amt = 0.1;
        //retVal.setTranslation(random.nextDouble()*amt,random.nextDouble()*amt,random.nextDouble()*amt);
        return retVal;
    }

    public TexRing createToleranceRing() {
        float r  = 1f;
        float th = m_toleranceThickness/2;
        TexRing toleranceRing = new TexRing("wpTolerance", 4, 32);
        toleranceRing.setRadius(r-th, r+th);
        toleranceRing.setTexMultiplier(8);
        toleranceRing.setConcave(0.0f);
        toleranceRing.setTexture(s_toleranceTexture);
        toleranceRing.initialize();
        toleranceRing.getSceneHints().setLightCombineMode(LightCombineMode.Off);
        toleranceRing.getSceneHints().setPickingHint(PickingHint.Pickable, false);
        toleranceRing.getSceneHints().setPickingHint(PickingHint.Collidable, false);
        toleranceRing.setTranslation( 0, 0, m_toleranceHeight);
        return toleranceRing;
    }

    protected OffsetQuad createDirectionMarker() {
        OffsetQuad dq = new OffsetQuad("wpDirection", 1.0f, 1.25f);
        dq.getQuad().setTexture(s_directionTexture);
        dq.getQuad().getSceneHints().setLightCombineMode(LightCombineMode.Off);
        dq.getQuad().getSceneHints().setPickingHint(PickingHint.Pickable, false);
        dq.getQuad().getSceneHints().setPickingHint(PickingHint.Collidable, false);
        dq.getQuad().setTranslation(0.75, 
                                    0, 
                                    m_toleranceHeight-0.005);
        return dq;
    }


    /**
     * Alter color based on TaskStatus
     * @param status
     */
    protected void setColorFromStatus(TaskStatus status) {
        final ReadOnlyColorRGBA clr = getColor(status);
        float dAmt;
        float eAmt;
        dAmt = 0.5f;
        dclr.set(clr.getRed()*dAmt,
                 clr.getGreen()*dAmt,
                 clr.getBlue()*dAmt,
                 1);
        eAmt = 0.8f;
        eclr.set(dclr.getRed()*eAmt, 
                 dclr.getGreen()*eAmt, 
                 dclr.getBlue()*eAmt, 
                 1);
        MaterialState matl;
        matl = (MaterialState)m_centerCylinder.getLocalRenderState(RenderState.StateType.Material);
        matl.setEmissive(eclr);
        matl.setSpecular(dclr);
        matl.setDiffuse(dclr);
        matl.setShininess(10);
        m_centerCylinder.setRenderState(matl);

        eAmt = 1.2f;
        tmpClr.set( 0.2f + eAmt*dclr.getRed(), 
                    0.2f + eAmt*dclr.getGreen(), 
                    0.2f + eAmt*dclr.getBlue(), 
                    dclr.getAlpha());
        tmpClr.clampLocal();
        m_toleranceRing.setDefaultColor(tmpClr);
        m_directionMarker.setDefaultColor(tmpClr);

    }

    static final ReadOnlyColorRGBA COLOR_PENDING   = Ardor3D.color(Colors.X11.Gray80);
    static final ReadOnlyColorRGBA COLOR_EXECUTING = Ardor3D.color(Colors.X11.GreenYellow);
    static final ReadOnlyColorRGBA COLOR_PAUSED    = Ardor3D.color(Colors.X11.CornflowerBlue);
    static final ReadOnlyColorRGBA COLOR_COMPLETED = Ardor3D.color(Colors.X11.Gray20);
    static final ReadOnlyColorRGBA COLOR_ABORTED   = Ardor3D.color(Colors.X11.Indigo);
    static final ReadOnlyColorRGBA COLOR_FAILED    = Ardor3D.color(Colors.X11.IndianRed);
    static final ReadOnlyColorRGBA COLOR_UNKNOWN   = Ardor3D.color(Colors.X11.BurlyWood);
    ColorRGBA dclr   = new ColorRGBA();
    ColorRGBA eclr   = new ColorRGBA();
    ColorRGBA tmpClr = new ColorRGBA();

    protected ReadOnlyColorRGBA getColor(TaskStatus status) {
        switch(status) {
        case Pending:   return COLOR_PENDING;
        case Executing: return COLOR_EXECUTING;
        case Paused:    return COLOR_PAUSED;
        case Completed: return COLOR_COMPLETED;
        case Aborted:   return COLOR_ABORTED;
        case Failed:    return COLOR_FAILED;
        case Unknown:   return COLOR_UNKNOWN;
        }
        return ColorRGBA.GRAY;
    }

    protected void setVisibilityFromStatus(TaskStatus status) {
        m_toleranceRing.getSceneHints().setCullHint(toleranceRingVisible(status) ? CullHint.Inherit : CullHint.Always);
        m_centerCylinder.getSceneHints().setCullHint(centerCylinderVisible(status) ? CullHint.Inherit : CullHint.Always);
        m_directionMarker.getSceneHints().setCullHint(directionMarkerVisible(status) ? CullHint.Inherit : CullHint.Always);
    }

    protected boolean centerCylinderVisible(TaskStatus status) {
        return true;
    }

    protected boolean toleranceRingVisible(TaskStatus status) {
        switch(status) {
        case Aborted:   return false;
        case Completed: return false;
        case Executing: return true;
        case Failed:    return true;
        case Paused:    return true;
        case Pending:   return false;
        case Unknown:   return false;
        }
        return false;
    }

    protected boolean directionMarkerVisible(TaskStatus status) {
        if(m_isDirectional) {
            switch(status) {
            case Aborted:   return false;
            case Completed: return false;
            case Executing: return true;
            case Failed:    return true;
            case Paused:    return true;
            case Pending:   return true;
            case Unknown:   return false;
            }
        }
        return false;
    }
}
