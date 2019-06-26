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
/**
 * Copyright (c) 2008, 2011 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package gov.nasa.arc.verve.ardor3d.e4.input.control;

import gov.nasa.arc.verve.ardor3d.e4.Ardor3D;
import gov.nasa.arc.verve.ardor3d.e4.framework.IVerveCanvasView;
import gov.nasa.arc.verve.common.VerveBaseMap;
import gov.nasa.arc.verve.common.interest.CenterOfInterest;
import gov.nasa.arc.verve.common.interest.InterestPointProvider;

import org.apache.log4j.Logger;

import com.ardor3d.framework.Canvas;
import com.ardor3d.input.Key;
import com.ardor3d.input.KeyboardState;
import com.ardor3d.input.MouseState;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.input.logical.MouseWheelMovedCondition;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TriggerConditions;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.intersection.PickData;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Spatial;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class FirstPersonCamControl extends AbstractCamControl {
    private static Logger logger = Logger.getLogger(FirstPersonCamControl.class);
    
    private double _mouseRotateSpeed = .005;
    private double _moveSpeed = 5;
    private double _keyRotateSpeed = 2.25;
    private final Matrix3 workerMatrix = new Matrix3();
    private final Vector3 workerStoreA = new Vector3();
    private final Vector3 workerStoreB = new Vector3();

    private final InputTrigger _dragInputTrigger;
    private final InputTrigger _moveInputTrigger;
    private final InputTrigger _wheelInputTrigger;
    
    private double m_dist = 10;
    
    /**
     * 
     * @param canvas
     * @param upAxis
     * @param dragOnly
     */
    public FirstPersonCamControl(final IVerveCanvasView canvasView, final ReadOnlyVector3 upAxis, final boolean dragOnly) {
        super(canvasView, upAxis);        
        _dragInputTrigger = createDragInputTrigger(dragOnly);
        _moveInputTrigger = createMoveInputTrigger();
        _wheelInputTrigger = createWheelInputTrigger();
    }
    
    @Override 
    public void reset() {
        m_camera = getCanvas().getCanvasRenderer().getCamera();
        if(m_camera != null) {
            float z = 0;
            if( VerveBaseMap.getBaseMap() != null) {
                z = VerveBaseMap.getBaseMap().getHeightAt(0, 0);
            }
            m_camera.setLocation(10, 2, 10+z);
            m_camera.lookAt(0, 0, z, Vector3.UNIT_Z);
        }
    }

    @Override
    public void init() {
        // do nothing
    }

    @Override
    public void reinit(AbstractCamControl previousCamControl) {
        m_dist = previousCamControl.getDistance();
    }

    @Override
    public void registerWith(LogicalLayer logicalLayer) {
        logicalLayer.registerTrigger(_dragInputTrigger);
        logicalLayer.registerTrigger(_moveInputTrigger);
        logicalLayer.registerTrigger(_wheelInputTrigger);
        CenterOfInterest.setCamera(this);
        Ardor3D.getCameraControlUpdater().registerCameraControl(this);
    }

    @Override
    public void deregisterFrom(LogicalLayer logicalLayer) {
        logicalLayer.deregisterTrigger(_dragInputTrigger);
        logicalLayer.deregisterTrigger(_moveInputTrigger);
        logicalLayer.deregisterTrigger(_wheelInputTrigger);
        Ardor3D.getCameraControlUpdater().removeCameraControl(this);
    }

    @Override
    public ReadOnlyVector3 getUpAxis() {
        return m_upAxis;
    }

    public void setUpAxis(final Vector3 upAxis) {
        m_upAxis.set(upAxis);
    }

    public double getMouseRotateSpeed() {
        return _mouseRotateSpeed;
    }

    public void setMouseRotateSpeed(final double speed) {
        _mouseRotateSpeed = speed;
    }

    public double getMoveSpeed() {
        return _moveSpeed;
    }

    public void setMoveSpeed(final double speed) {
        _moveSpeed = speed;
    }

    public double getKeyRotateSpeed() {
        return _keyRotateSpeed;
    }

    public void setKeyRotateSpeed(final double speed) {
        _keyRotateSpeed = speed;
    }
    
    public void adjustMoveSpeed(int wheelVal) {
        logger.info("moveSpeed = "+_moveSpeed);
        float amt = 1 - wheelVal*0.05f;
        _moveSpeed = _moveSpeed * amt;
        if(_moveSpeed < 3) {
            _moveSpeed = 3;
        }
        else if(_moveSpeed > 100) {
            _moveSpeed = 100;
        }
    }

    protected void move(final KeyboardState kb, final double tpf) {
        // MOVEMENT
        int moveFB = 0, strafeLR = 0;
        if (kb.isDown(Key.W)) {
            moveFB += 1;
        }
        if (kb.isDown(Key.S)) {
            moveFB -= 1;
        }
        if (kb.isDown(Key.A)) {
            strafeLR += 1;
        }
        if (kb.isDown(Key.D)) {
            strafeLR -= 1;
        }
        m_camera = getCanvas().getCanvasRenderer().getCamera();
        final Vector3 loc = workerStoreA.set(m_camera.getLocation());
        if (moveFB != 0) {
            loc.addLocal(workerStoreB.set(m_camera.getDirection()).multiplyLocal(moveFB * _moveSpeed * tpf));
        }
        if (strafeLR != 0) {
            loc.addLocal(workerStoreB.set(m_camera.getLeft()).multiplyLocal(strafeLR * _moveSpeed * tpf));
        }
        m_camera.setLocation(loc);

        // ROTATION
        int rotX = 0, rotY = 0;
        if (kb.isDown(Key.UP)) {
            rotY -= 1;
        }
        if (kb.isDown(Key.DOWN)) {
            rotY += 1;
        }
        if (kb.isDown(Key.LEFT)) {
            rotX += 1;
        }
        if (kb.isDown(Key.RIGHT)) {
            rotX -= 1;
        }
        if (rotX != 0 || rotY != 0) {
            rotate(rotX * (_keyRotateSpeed / _mouseRotateSpeed) * tpf, rotY * (_keyRotateSpeed / _mouseRotateSpeed)
                    * tpf);
        }
    }

    protected void rotate(final double dx, final double dy) {
        m_camera = getCanvas().getCanvasRenderer().getCamera();
        if (dx != 0) {
            workerMatrix.fromAngleNormalAxis(_mouseRotateSpeed * dx, m_upAxis != null ? m_upAxis : workerStoreA.set(m_camera.getUp()));
            workerMatrix.applyPost(workerStoreA.set(m_camera.getLeft()), workerStoreA);
            m_camera.setLeft(workerStoreA);
            workerMatrix.applyPost(workerStoreA.set(m_camera.getDirection()), workerStoreA);
            m_camera.setDirection(workerStoreA);
            workerMatrix.applyPost(workerStoreA.set(m_camera.getUp()), workerStoreA);
            m_camera.setUp(workerStoreA);
        }

        if (dy != 0) {
            workerMatrix.fromAngleNormalAxis(_mouseRotateSpeed * dy, workerStoreA.set(m_camera.getLeft()));
            workerMatrix.applyPost(workerStoreA.set(m_camera.getLeft()), workerStoreA);
            m_camera.setLeft(workerStoreA);
            workerMatrix.applyPost(workerStoreA.set(m_camera.getDirection()), workerStoreA);
            m_camera.setDirection(workerStoreA);
            workerMatrix.applyPost(workerStoreA.set(m_camera.getUp()), workerStoreA);
            m_camera.setUp(workerStoreA);
        }

        m_camera.normalize();
    }

    /**
     * Create an InputTrigger for mouse look
     * @param dragOnly only perform mouse look when a button is down
     * @return 
     */
    private InputTrigger createDragInputTrigger(boolean dragOnly) {
        final FirstPersonCamControl control = this;

        final Predicate<TwoInputStates> someMouseDown = Predicates.or(TriggerConditions.leftButtonDown(), Predicates.or(TriggerConditions.rightButtonDown(), TriggerConditions.middleButtonDown()));
        final Predicate<TwoInputStates> dragged = Predicates.and(TriggerConditions.mouseMoved(), someMouseDown);

        TriggerAction dragAction = new TriggerAction() {

            @Override
            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                if(source.equals(control.getCanvas())) {
                    final MouseState mouse = inputState.getCurrent().getMouseState();
                    if (mouse.getDx() != 0 || mouse.getDy() != 0) {
                        control.rotate(-mouse.getDx(), -mouse.getDy());
                    }
                }
            }
        };
        return new InputTrigger(dragOnly ? dragged : TriggerConditions.mouseMoved(), dragAction);
    }

    /**
     * Create an input trigger for the WASD move keys
     * @return
     */
    private InputTrigger createMoveInputTrigger() {
        final FirstPersonCamControl control = this;
        // WASD control
        final Predicate<TwoInputStates> keysHeld = new Predicate<TwoInputStates>() {
            Key[] keys = new Key[] { Key.W, Key.A, Key.S, Key.D, Key.LEFT, Key.RIGHT, Key.UP, Key.DOWN };

            @Override
            public boolean apply(final TwoInputStates states) {
                for (final Key k : keys) {
                    if (states.getCurrent() != null && states.getCurrent().getKeyboardState().isDown(k)) {
                        return true;
                    }
                }
                return false;
            }
        };

        final TriggerAction moveAction = new TriggerAction() {
            @Override
            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                if(source.equals(control.getCanvas())) {
                    control.move(inputState.getCurrent().getKeyboardState(), tpf);
                }
            }
        };
        return new InputTrigger(keysHeld, moveAction);
    }

    protected Spatial       m_spatial       = null;
    protected final Vector3 m_spatialOffset = new Vector3(0,0,0);
    protected final Vector3 m_actualLoc     = new Vector3();
    protected final Vector3 m_actualCtr     = new Vector3();

    @Override
    public void setCenterOfInterest(Spatial spatial, ReadOnlyVector3 offset) {
        if(spatial != null) {
            m_camera = getCanvas().getCanvasRenderer().getCamera();
            m_camera.setLocation(spatial.getWorldTranslation());
            Vector3 dir   = new Vector3();
            Vector3 up   = new Vector3();
            Vector3 left = new Vector3();
            spatial.getWorldRotation().getColumn(0, left);
            spatial.getWorldRotation().getColumn(1, up);
            spatial.getWorldRotation().getColumn(2, dir);
            m_camera.setDirection(dir);
            m_camera.setLeft(left);
            m_camera.setUp(up);
            m_spatial = spatial;
            m_spatialOffset.set(offset);
        }
    }
    
    @Override
    public void setCenterOfInterest(InterestPointProvider ipp) {
        if(ipp instanceof Spatial) {
            setCenterOfInterest((Spatial)ipp, Vector3.ZERO);
        }
    }

    @Override
    public void handleFrameUpdate(double tpf) {
        // TODO Auto-generated method stub

    }

    @Override
    public ReadOnlyVector3 getCenter() {
        m_camera = getCanvas().getCanvasRenderer().getCamera();
        Vector3 temp = new Vector3(m_camera.getDirection());
        temp.multiplyLocal(m_dist);
        m_actualCtr.set(m_camera.getLocation());
        m_actualCtr.addLocal(temp);
        return m_actualCtr;
    }
    
    /** NOOP */
    @Override
    public AbstractCamControl setCenter(ReadOnlyVector3 vec) {
        return this;
    }

    @Override
    public ReadOnlyVector3 getLocation() {
        m_camera = getCanvas().getCanvasRenderer().getCamera();
        m_actualLoc.set(m_camera.getLocation());
        return m_actualLoc;
    }

    @Override
    public AbstractCamControl setLocation(ReadOnlyVector3 vec) {
        m_camera = getCanvas().getCanvasRenderer().getCamera();
        m_actualLoc.set(vec);
        m_camera.setLocation(vec);
        return this;
    }

    @Override
    public AbstractCamControl setLocationAndCenter(ReadOnlyVector3 loc, ReadOnlyVector3 ctr) {
        return setLocation(loc);
    }

    protected Vector3 getPickIntersection() {
        Vector3 retVal = null;
        try {
            m_camera = getCanvas().getCanvasRenderer().getCamera();
            Ray3 pickRay = new Ray3(m_camera.getLocation(), m_camera.getDirection());
            PickResults results = getCanvas().getCanvasRenderer().getScene().doPick(pickRay);
            if(results.getNumber() > 0) {
                PickData pick = results.getPickData(0);
                double dist = pick.getIntersectionRecord().getClosestDistance();
                if( dist > 0 && dist < 90000 ) {
                    retVal = new Vector3();
                    retVal = m_camera.getDirection().multiply(dist, retVal);
                    retVal.addLocal(m_camera.getLocation());
                }
            }
        }
        catch(Throwable t) {
            // XXX ignore
        }
        return retVal;
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
    public double getDistance() {
        Vector3 retVal = new Vector3();
        retVal.set(getCenter());
        retVal.subtractLocal(getLocation());
        return retVal.length();
    }
    
    @Override
    public AbstractCamControl setDistance(double distance) {
        m_dist = distance;
        return this;
    }

    @Override
    public String getInterestPointMode() {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public void setInterestPointMode(String mode) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void interestPointUpdated(InterestPointProvider ipp,
            String viewMode, ReadOnlyVector3 interestPoint,
            ReadOnlyVector3 secondaryInterestPoint) {
        // TODO Auto-generated method stub
        
    }

    InputTrigger createWheelInputTrigger() {
        final FirstPersonCamControl control = this;
        final TriggerAction wheelAction = new TriggerAction() {
            @Override
            public void perform(Canvas source, TwoInputStates inputStates, double tpf) {
                if( source.equals(control.getCanvas()) ) {
                    final MouseState mouse = inputStates.getCurrent().getMouseState();
                    if(mouse.getDwheel() != 0) {
                        control.adjustMoveSpeed(mouse.getDwheel());
                    }
                }
            }
        };
        return new InputTrigger(new MouseWheelMovedCondition(), wheelAction);
    }


}
