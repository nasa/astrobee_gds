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
package gov.nasa.arc.verve.common.ardor3d.input.control;

import gov.nasa.arc.irg.util.ui.UiTarget;
import gov.nasa.arc.verve.common.SceneHack;
import gov.nasa.arc.verve.common.VerveBaseMap;
import gov.nasa.arc.verve.common.VerveUserData;
import gov.nasa.arc.verve.common.ardor3d.Ardor3D;
import gov.nasa.arc.verve.common.ardor3d.framework.IVerveCanvasView;
import gov.nasa.arc.verve.common.interest.CenterOfInterest;
import gov.nasa.arc.verve.common.interest.InterestPointListener;
import gov.nasa.arc.verve.common.interest.InterestPointProvider;
import gov.nasa.arc.verve.common.util.LightManager;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ardor3d.framework.Canvas;
import com.ardor3d.input.ButtonState;
import com.ardor3d.input.InputState;
import com.ardor3d.input.Key;
import com.ardor3d.input.KeyboardState;
import com.ardor3d.input.MouseButton;
import com.ardor3d.input.MouseState;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.input.logical.MouseButtonClickedCondition;
import com.ardor3d.input.logical.MouseWheelMovedCondition;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TriggerConditions;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.light.SpotLight;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.shape.AxisRods;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * 
 * @author mallan
 *
 */
public class BoomCamControl extends AbstractCamControl implements InterestPointListener {
    static Logger logger = Logger.getLogger(BoomCamControl.class.getName());

    Spatial               m_spatial        = null;
    final Vector3         m_spatialOffset  = new Vector3(0, 0, 0);

    final Vector3         m_actualCtr      = new Vector3(0, 0, 0);
    final Vector3         m_targetCtr      = new Vector3(m_actualCtr);

    final Vector3         m_actualLoc      = new Vector3(-7, -10, 10);
    final Vector3         m_targetLoc      = new Vector3(m_actualLoc);
    double                m_distance       = m_actualLoc.length();

    double                m_dollySpeed     = 0.05;


    double                m_maxEl          = Math.PI * 0.4999;
    double                m_az             = 0;
    double                m_el             = 0;

    protected Vector3 m_targetXyz       = new Vector3();
    protected Vector3 m_lastSpatialXyz  = new Vector3();
    protected Vector3 m_thisSpatialXyz  = new Vector3();
    protected Vector3 m_diffSpatialXyz  = new Vector3();
    protected Vector3 m_instantVelocity = new Vector3();
    protected Vector3 m_spatialVelocity = new Vector3();

    static int instanceCount = 0;
    public final int instance;

    protected enum Trigger {
        Dragged,
        Wheel,
        LeftClick,
        MiddleClick,
        Release,
        ;
    }
    public final InputTrigger[] m_triggers = new InputTrigger[Trigger.values().length];

    SpotLight m_headLight = null;
    AxisRods  m_axes = new AxisRods();

    //=========================================================================
    public BoomCamControl(final IVerveCanvasView canvasView, final ReadOnlyVector3 upAxis) {
        super(canvasView, upAxis);
        instance = instanceCount++;

        m_triggers[Trigger.Dragged.ordinal()]      = createDraggedInputTrigger();
        m_triggers[Trigger.Wheel.ordinal()]        = createWheelInputTrigger();
        m_triggers[Trigger.LeftClick.ordinal()]    = createPickInputTrigger();
        m_triggers[Trigger.MiddleClick.ordinal()]  = createMiddleClickTrigger();
        m_triggers[Trigger.Release.ordinal()]      = createMouseReleasedTrigger();

        m_el = Math.asin(m_actualLoc.getZ()/m_distance);
    }

    @Override
    public ReadOnlyVector3 getCenter() {
        return m_actualCtr;
    }

    @Override
    public AbstractCamControl setCenter(ReadOnlyVector3 ctr) {
        m_actualCtr.set(ctr);
        return updateCamValues();
    }

    @Override
    public ReadOnlyVector3 getLocation() {
        return m_actualLoc;
    }

    @Override
    public AbstractCamControl setLocation(ReadOnlyVector3 loc) {
        m_actualLoc.set(loc);
        return updateCamValues();
    }

    @Override
    public AbstractCamControl setLocationAndCenter(ReadOnlyVector3 loc, ReadOnlyVector3 ctr) {
        m_actualLoc.set(loc);
        m_actualCtr.set(ctr);
        return updateCamValues();
    }

    protected AbstractCamControl updateCamValues() {
        m_camera.setLocation(m_actualLoc);
        m_camera.lookAt(m_actualCtr, m_upAxis);
        double dist = m_actualLoc.distance(m_actualCtr);
        m_el = Math.asin(m_actualLoc.getZ()/dist);
        if(m_el > m_maxEl) 
            m_el = m_maxEl;
        return this;
    }

    @Override
    public double getDistance() {
        return m_distance;
    }

    public double calcDistance() {
        Vector3 tmp = Vector3.fetchTempInstance();
        m_actualLoc.subtract(m_actualCtr, tmp);
        m_distance = tmp.length();
        Vector3.releaseTempInstance(tmp);
        return m_distance;
    }

    @Override
    public AbstractCamControl setDistance(double distance) {
        m_distance = distance;
        return this;
    }

    @Override
    public Spatial getCenterOfInterest() {
        return m_spatial;
    }

    @Override
    public ReadOnlyVector3 getCenterOfInterestOffset() {
        return m_spatialOffset;
    }

    @Override
    public ReadOnlyVector3 getUpAxis() {
        return m_upAxis;
    }

    public void setUpAxis(final Vector3 upAxis) {
        m_upAxis.set(upAxis);
    }

    @Override
    public void reset() {
        m_spatialOffset.set(0,0,0);

        float z = 0;
        if( VerveBaseMap.getBaseMap() != null) {
            z = VerveBaseMap.getBaseMap().getHeightAt(0, 0);
        }
        m_targetCtr.set(m_actualCtr.set(0,   0,    z));
        m_targetLoc.set(m_actualLoc.set(0, -10, 10+z));

        calcDistance();
        reinit(null);
    }

    @Override
    public void init() {
        reinit(null);
    }

    @Override
    public void reinit(AbstractCamControl pcc) {
        m_camera = getCanvasView().getCanvas().getCanvasRenderer().getCamera();
        m_ip.provider = null;
        m_spatial     = null;
        if(pcc == null) {
            m_spatialOffset.set(0,0,0);
            m_actualCtr.set(m_targetCtr);
            m_actualLoc.set(m_targetLoc);
        }
        else {
            //m_ip.set(pcc.m_ip);
            m_ip.provider = null;
            m_distance = pcc.getDistance();
            m_actualCtr.set(pcc.getCenter());
            m_actualLoc.set(pcc.getLocation());
            m_targetCtr.set(m_actualCtr);
            m_targetLoc.set(m_actualLoc);
            Vector3 look = new Vector3(m_camera.getDirection());
            if(Math.abs(look.getZ()) > 0.98) {
                Vector3 tmp  = new Vector3(m_camera.getUp());
                tmp.multiplyLocal(-0.1*m_distance); 
                m_actualLoc.addLocal(tmp);  // bump location if we're coincident with Z
                m_targetLoc.addLocal(tmp);
            }
            setCenterOfInterest(pcc.getCenterOfInterest(), pcc.getCenterOfInterestOffset());
        }
        calcAzEl(m_actualLoc, m_actualCtr);

        m_camera.setLocation(m_actualLoc);
        m_camera.lookAt(m_actualCtr, m_upAxis);
        m_camera.normalize();
        m_camera.update();

        m_headLight = LightManager.instance().getSpotLight("headLight");
        SceneHack.getMainScene().getRoot().attachChild(m_axes);
        adjustNearFarPlanes(m_distance);
    }

    @Override
    public void registerWith(LogicalLayer logicalLayer) {
        for(InputTrigger trigger : m_triggers ) {
            logicalLayer.registerTrigger(trigger);
        }
        Ardor3D.getCameraControlUpdater().registerCameraControl(this);
        CenterOfInterest.setCamera(this);
    }

    @Override
    public void deregisterFrom(LogicalLayer logicalLayer) {
        for(InputTrigger trigger : m_triggers ) {
            logicalLayer.deregisterTrigger(trigger);
        }
        Ardor3D.getCameraControlUpdater().removeCameraControl(this);
        if(m_ip.provider != null) {
            m_ip.provider.removeInterestPointListener(this);
        }
    }

    @Override
    public List<InterestPointProvider> getInterestPointProviders() {
        List<InterestPointProvider> list;
        FindInterestPointVisitor ippv = new FindInterestPointVisitor();
        list = ippv.execute(getCanvasView().getScene().getRoot());
        return list;
    }

    protected void setSpatial(Spatial newSpatial) {
        m_spatial = newSpatial;
        m_spatialVelocity.set(0,0,0);
        if(m_spatial != null) {
            m_thisSpatialXyz.set(m_lastSpatialXyz.set(m_spatial.getWorldTranslation()));
        }

    }

    //-----------------------------------------------------
    @Override
    public void setCenterOfInterest(Spatial spatial, ReadOnlyVector3 offset) {
        if(m_ip.provider != null) {
            m_ip.provider.removeInterestPointListener(this);
        }

        setSpatial(spatial);
        if(offset != null) {
            m_spatialOffset.set(offset);
        }

        // XXX In lieu of proper pick delegation, walk up the graph and see if there's an interest point provider
        m_ip.provider = null;
        if(m_spatial != null) {
            while(spatial != null) {
                if(InterestPointProvider.class.isAssignableFrom(spatial.getClass()) ) {
                    InterestPointProvider ipp = (InterestPointProvider)spatial;
                    if(ipp.isInterestPointEnabled()) {
                        if(m_ccUi != null) {
                            m_ccUi.setFollow(ipp);
                        }
                        else {
                            setCenterOfInterest(ipp);
                        }
                    }
                    else {
                        // TODO: the isInterestPointEnabled() semantics are 
                        // too vague. We still want to notify the UI that
                        // an InterestPoint has been selected in most cases
                        UiTarget.targetChanged(spatial.getName());
                    }
                    break;
                }
                spatial = spatial.getParent();
            }
            if(VerveUserData.isCameraFollowable(m_spatial)) {
                handleFollowSpatial(0);
                calcTargetLocFromAzEl();
            }
            m_camera.lookAt(m_actualCtr, m_upAxis);
        }
        //// if object is not followable, we still want to center on it when we select it
        //if(!VerveUserData.isCameraFollowable(m_spatial) && m_noFollowCenterOnSelect) {
        //    handleFollowSpatial();
        //}
    }

    /**
     * 
     */
    @Override
    public void setCenterOfInterest(InterestPointProvider ipp) {
        final InterestPointProvider oldIpp = m_ip.provider;
        if(oldIpp != null) {
            oldIpp.removeInterestPointListener(this);
        }
        if(ipp instanceof Spatial) {
            setSpatial((Spatial)ipp);
        }
        m_spatialOffset.set(0,0,0);   
        m_ip.provider = ipp;

        if(m_ip.provider != null) {
            m_ip.provider.addInterestPointListener(this, m_ip.mode);
        }
        if(m_ccUi != null && ipp != oldIpp) {
            m_ccUi.setFollow(ipp);
        }
    }

    private void handleFollowSpatial(double timePerFrame) {
        m_thisSpatialXyz.set(m_spatial.getWorldTranslation());
        if(timePerFrame > 0 && timePerFrame < 1) {
            m_thisSpatialXyz.subtract(m_lastSpatialXyz, m_diffSpatialXyz);
            m_diffSpatialXyz.divide(timePerFrame, m_instantVelocity);
            m_spatialVelocity.lerpLocal(m_instantVelocity, 0.05);
            
            m_spatialVelocity.multiply(timePerFrame, m_instantVelocity);
            m_targetXyz.addLocal(m_instantVelocity);
            m_targetCtr.set(m_targetXyz);
            m_targetCtr.addLocal(m_spatialOffset);
        }
        else {
            m_targetXyz.set(m_thisSpatialXyz);
            m_targetCtr.set(m_targetXyz);
            m_targetCtr.addLocal(m_spatialOffset);
        }
        m_lastSpatialXyz.set(m_thisSpatialXyz);
    }

    /**
     * called each frame to update camera position
     */
    //------------------------------------------------------------------------------------------
    @Override
    public void handleFrameUpdate(double timePerFrame) {
        m_camera = getCanvas().getCanvasRenderer().getCamera();

        if(m_spatial != null && VerveUserData.isCameraFollowable(m_spatial)) {
            handleFollowSpatial(timePerFrame);
            calcTargetLocFromAzEl();
        }

        m_actualLoc.set(m_targetLoc);
        m_actualCtr.set(m_targetCtr);

        m_camera.setLocation(m_actualLoc);

        //m_camera.lookAt(m_actualCtr, m_upAxis);
    }

    protected void calcAzEl(ReadOnlyVector3 loc, ReadOnlyVector3 ctr) {
        Vector3 diff = new Vector3(loc);
        diff.subtractLocal(ctr);
        diff.normalizeLocal();
        m_az = yawFromVector(diff);
        m_el = Math.asin(diff.getZ());
        if(m_el > m_maxEl) 
            m_el = m_maxEl;

    }

    protected void calcTargetLocFromAzEl() {
        Vector3 tmp = new Vector3(0,1,0);
        Vector3 vec = new Vector3(0,1,0);
        Matrix3 azRot = new Matrix3();
        Matrix3 elRot = new Matrix3();
        azRot.fromAngleNormalAxis(m_az, Vector3.NEG_UNIT_Z);
        elRot.fromAngleNormalAxis(m_el, Vector3.UNIT_X);
        elRot.applyPost(vec, tmp);
        azRot.applyPost(tmp, vec);
        vec.multiplyLocal(m_distance);
        vec.addLocal(m_actualCtr);
        m_targetLoc.set(vec);
    }


    /**
     */
    protected void dolly(final double dwheel) {
        CenterOfInterest.setCamera(this);
        double dist;
        Vector3 tgtLoc = Vector3.fetchTempInstance();
        Vector3 tmpDiff = Vector3.fetchTempInstance();

        dist = m_distance + m_distance * dwheel * m_dollySpeed;
        double min = m_distanceRange[0];
        double max = m_distanceRange[1];
        if(dist > min && dist < max) {
            m_distance = dist;
            // update targetLoc to new distance
            tgtLoc.set(m_targetLoc);
            tmpDiff = tgtLoc.subtract(m_targetCtr, tmpDiff);
            dist    = tmpDiff.length();
            tmpDiff.multiplyLocal(m_distance/dist);
            tgtLoc = tmpDiff.add(m_targetCtr, tgtLoc);
            m_targetLoc.set(tgtLoc);
        }
        tmpDiff = m_targetLoc.subtract(m_targetCtr, tmpDiff);
        m_el = Math.asin(tmpDiff.getZ()/m_distance);

        adjustNearFarPlanes(m_distance);

        Vector3.releaseTempInstance( tgtLoc );
        Vector3.releaseTempInstance( tmpDiff );
    }

    protected void mouseRelease(MouseState mouse) {
    }


    protected void move(MouseState mouse) {
    }

    protected void rotate(MouseState mouse) {
        double amt = 0.005;
        m_az += amt*mouse.getDx();
        m_el -= amt*mouse.getDy();
        if( m_el >  m_maxEl) m_el =  m_maxEl;
        if( m_el < -m_maxEl) m_el = -m_maxEl;
        calcTargetLocFromAzEl();
    }

    protected void leftMouseClick(MouseState mouse) {
        switch(mouse.getClickCount(MouseButton.LEFT)) {
        case 2: pick(mouse); break;
        default:
            break;
        }
    }

    protected void middleMouseClick(MouseState mouse) {
        List<InterestPointProvider> ippList = fipVisitor.execute(getCanvasView().getScene().getRoot());
        System.out.println("InterestPointProviders:");
        for(InterestPointProvider ipp : ippList) {
            System.out.println("    "+ipp.getInterestPointName());
        }
    }
    FindInterestPointVisitor fipVisitor = new FindInterestPointVisitor();

    /**
     * 
     * @param mouse
     */
    protected void pick(MouseState mouse) {
        Ray3 pickRay = getPickRay(mouse);
        getCanvas().getCanvasRenderer().getScene().doPick(pickRay);
    }

    /**
     * 
     */
    InputTrigger createDraggedInputTrigger() {
        final BoomCamControl control = this;
        final Predicate<TwoInputStates> someMouseDown = Predicates.or(TriggerConditions.leftButtonDown(), 
                                                                      Predicates.or(TriggerConditions.rightButtonDown(), 
                                                                                    TriggerConditions.middleButtonDown()));

        final Predicate<TwoInputStates> dragged = Predicates.and(TriggerConditions.mouseMoved(), 
                                                                 someMouseDown);

        final TriggerAction dragAction = new TriggerAction() {

            @Override
            public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
                if( source.equals(control.getCanvas()) ) {
                    final MouseState mouse = inputStates.getCurrent().getMouseState();
                    final KeyboardState keybd = inputStates.getCurrent().getKeyboardState();
                    if (mouse.getDx() != 0 || mouse.getDy() != 0) {
                        Set<Key> keySet = keybd.getKeysDown();
                        if(keySet.size() == 0) {
                            if(mouse.getButtonState(MouseButton.LEFT).equals(ButtonState.DOWN)) {
                                control.rotate(mouse);
                            }
                            else if(mouse.getButtonState(MouseButton.MIDDLE).equals(ButtonState.DOWN)) {
                                control.rotate(mouse);
                            }
                            else if(mouse.getButtonState(MouseButton.RIGHT).equals(ButtonState.DOWN)) {
                                control.move(mouse);
                            }
                        }
                        else if(keybd.isDown(Key.LCONTROL) || keybd.isDown(Key.RCONTROL)) {
                            control.dolly((int)(mouse.getDy()*0.6));
                        }
                    }
                }
            }
        };
        return new InputTrigger(dragged, dragAction);
    }

    InputTrigger createWheelInputTrigger() {
        final BoomCamControl control = this;

        final TriggerAction wheelAction = new TriggerAction() {
            @Override
            public void perform(Canvas source, TwoInputStates inputStates, double tpf) {
                if( source.equals(control.getCanvas()) ) {
                    final MouseState mouse = inputStates.getCurrent().getMouseState();
                    if(mouse.getDwheel() != 0) {
                        control.dolly(mouse.getDwheel());
                    }
                }
            }
        };
        return new InputTrigger(new MouseWheelMovedCondition(), wheelAction);
    }

    InputTrigger createPickInputTrigger() {
        final BoomCamControl control = this;
        //-- left click action --------------------------
        final TriggerAction leftClickAction = new TriggerAction() {
            @Override
            public void perform(Canvas source, TwoInputStates inputStates, double tpf) {
                if( source.equals(control.getCanvas()) ) {
                    final MouseState mouse = inputStates.getCurrent().getMouseState();
                    control.leftMouseClick(mouse);
                }
            }
        };
        return new InputTrigger(new MouseButtonClickedCondition(MouseButton.LEFT), leftClickAction);
    }

    InputTrigger createMiddleClickTrigger() {
        //-- middle click action --------------------------
        final TriggerAction clickAction = new TriggerAction() {
            @Override
            public void perform(Canvas source, TwoInputStates inputStates, double tpf) {
                if( source.equals(BoomCamControl.this.getCanvas()) ) {
                    final MouseState mouse = inputStates.getCurrent().getMouseState();
                    BoomCamControl.this.middleMouseClick(mouse);
                }
            }
        };
        return new InputTrigger(new MouseButtonClickedCondition(MouseButton.MIDDLE), clickAction);
    }

    public class AnyMouseReleasedCondition implements Predicate<TwoInputStates> {
        public boolean apply(final TwoInputStates states) {
            final InputState currentState = states.getCurrent();
            final InputState previousState = states.getPrevious();
            if (currentState == null || previousState == null) {
                return false;
            }
            if(!previousState.getMouseState().hasButtonState(ButtonState.DOWN)) {
                return false;
            }
            EnumSet<MouseButton> released = currentState.getMouseState().getButtonsReleasedSince(previousState.getMouseState());
            return released.size() > 0;
        }
    }

    InputTrigger createMouseReleasedTrigger() {
        final TriggerAction triggerAction = new TriggerAction() {
            @Override
            public void perform(Canvas source, TwoInputStates inputStates, double tpf) {
                if( source.equals(BoomCamControl.this.getCanvas()) ) {
                    final MouseState mouse = inputStates.getCurrent().getMouseState();
                    BoomCamControl.this.mouseRelease(mouse);
                }
            }
        };
        return new InputTrigger(new AnyMouseReleasedCondition(), triggerAction);
    }

}
