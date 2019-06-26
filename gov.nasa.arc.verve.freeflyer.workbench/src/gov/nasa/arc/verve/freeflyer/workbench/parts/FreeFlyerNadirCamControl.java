/******************************************************************************
 * Copyright © 2019, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration. All 
 * rights reserved.
 * 
 * The Astrobee Control Station platform is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0. 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 *****************************************************************************/
package gov.nasa.arc.verve.freeflyer.workbench.parts;

import gov.nasa.arc.verve.ardor3d.e4.Ardor3D;
import gov.nasa.arc.verve.ardor3d.e4.framework.IVerveCanvasView;
import gov.nasa.arc.verve.ardor3d.e4.input.control.AbstractCamControl;
import gov.nasa.arc.verve.ardor3d.e4.input.control.CamControlType;
import gov.nasa.arc.verve.ardor3d.e4.input.control.FindInterestPointVisitor;
import gov.nasa.arc.verve.common.interest.CenterOfInterest;
import gov.nasa.arc.verve.common.interest.InterestPointProvider;
import gov.nasa.arc.verve.common.util.LightManager;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ardor3d.framework.Canvas;
import com.ardor3d.input.ButtonState;
import com.ardor3d.input.Key;
import com.ardor3d.input.KeyEvent;
import com.ardor3d.input.KeyState;
import com.ardor3d.input.KeyboardState;
import com.ardor3d.input.MouseButton;
import com.ardor3d.input.MouseState;
import com.ardor3d.input.logical.AnyKeyCondition;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.input.logical.MouseButtonClickedCondition;
import com.ardor3d.input.logical.MouseButtonPressedCondition;
import com.ardor3d.input.logical.MouseButtonReleasedCondition;
import com.ardor3d.input.logical.MouseWheelMovedCondition;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TriggerConditions;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.light.SpotLight;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector2;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Spatial;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * 
 * @author mallan, tecohen
 *
 */
public class FreeFlyerNadirCamControl extends AbstractCamControl {
    static Logger logger = Logger.getLogger(FreeFlyerNadirCamControl.class.getName());

    protected Spatial       m_spatial       = null;
    protected final Vector3 m_spatialOffset = new Vector3(0,0,0);
    protected final Vector3 m_spatialVec    = new Vector3();

    protected final Vector3 m_actualCtr     = new Vector3(0,0,0);
    protected final Vector3 m_targetCtr     = new Vector3(0,0,0);

    protected final Vector3 m_actualLoc     = new Vector3(0,0,20);
    protected double        m_targetDistance= 10;
    protected double        m_distance      = m_targetDistance;

    protected double        m_targetYaw     = 0;
    protected double        m_actualYaw     = 0;

    protected boolean       m_watchEnable   = true;
    protected double        m_watchSpeed    = 0.2;
    protected final Vector3 m_watchCenter   = new Vector3(16.3, 0, 0);

    protected double        m_tension       = 5;

    protected double        m_dollySpeed    = 0.05;
    protected double        m_crabSpeed     = 0.001; 

    static int instanceCount = 0;
    public final int instance;

    protected final Vector3 m_camUp   = new Vector3();
    protected final Vector3 m_camLeft = new Vector3();
    protected final Vector3 m_camLook = new Vector3();

    boolean m_zeroTo2Pi = false;
    
    protected SpotLight m_headLight = null;

    protected enum Trigger {
        Dragged,
        Wheel,
        LeftClick,
        RightClick,
        MiddleClick,
        KeyInput,
        WatchEnable,
        WatchDisable;
    }
    public final InputTrigger[] m_triggers = new InputTrigger[Trigger.values().length];

    /**
     * 
     * @param canvas
     * @param upAxis
     * @param scene
     */
    public FreeFlyerNadirCamControl(final IVerveCanvasView canvasView, final ReadOnlyVector3 upAxis) {
        super(canvasView, upAxis);
        
        m_camera = getCanvas().getCanvasRenderer().getCamera();

        instance = instanceCount++;
        m_triggers[Trigger.Dragged.ordinal()]      = createDraggedInputTrigger();
        m_triggers[Trigger.Wheel.ordinal()]        = createWheelInputTrigger();
        m_triggers[Trigger.LeftClick.ordinal()]    = createLeftClickInputTrigger();
        m_triggers[Trigger.RightClick.ordinal()]   = createRightClickInputTrigger();
        m_triggers[Trigger.WatchEnable.ordinal()]  = createWatchEnableTrigger();
        m_triggers[Trigger.KeyInput.ordinal()]     = createKeyInputTrigger();
        m_triggers[Trigger.WatchDisable.ordinal()] = createWatchDisableTrigger();
        m_triggers[Trigger.MiddleClick.ordinal()]  = createMiddleClickTrigger();

       setUpAxis(upAxis);
    }

    @Override
    public ReadOnlyVector3 getCenter() {
        return m_actualCtr;
    }

    @Override
    public AbstractCamControl setCenter(ReadOnlyVector3 ctr) {
        m_targetCtr.set(ctr);
        m_actualCtr.set(ctr);
        return this;
    }

    @Override
    public ReadOnlyVector3 getLocation() {
        return m_actualLoc;
    }

    @Override
    public AbstractCamControl setLocation(ReadOnlyVector3 loc) {
        m_actualLoc.set(loc);
        return this;
    }

    @Override
    public AbstractCamControl setLocationAndCenter(ReadOnlyVector3 loc, ReadOnlyVector3 ctr) {
        m_actualLoc.set(loc);
        m_targetCtr.set(ctr);
        m_actualCtr.set(ctr);
        return this;
    }

    @Override
    public double getDistance() {
        return m_distance;
    }
    
    @Override 
    public AbstractCamControl setDistance(double distance) {
        m_targetDistance = distance;
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

    @Override
	public void setUpAxis(ReadOnlyVector3 upAxis) {
    	 m_upAxis.set ( 0, 0, 1);
         m_camLook.set( 0, 0,-1);
         m_camLeft.set(-1, 0, 0);
         m_camUp.set  ( 0, 1, 0);
    }
    
    public void flip() {
    	m_upAxis.set(-1 * m_upAxis.getX(), -1 * m_upAxis.getY(), -1 * m_upAxis.getZ());
    	m_camLook.set(-1 * m_camLook.getX(), -1 * m_camLook.getY(), -1 * m_camLook.getZ());
    	m_camLeft.set(-1* m_camLeft.getX(), -1 * m_camLeft.getY(), -1 * m_camLeft.getZ());
    	m_actualLoc.set(m_actualCtr.getX() + (m_upAxis.getX() * m_distance), m_actualCtr.getY() + (m_upAxis.getY() * m_distance), m_actualCtr.getZ() + (m_upAxis.getZ() * m_distance));
		m_targetYaw = yawFromVector(m_camera.getDirection());
		m_actualYaw = m_targetYaw;
		updateYaw(m_actualYaw);
		adjustNearFarPlanes(m_distance);
    }

    @Override
    public void reset() {
        m_spatialOffset.set(0,0,0);
        m_spatialVec.set(1,0,0);
        m_actualCtr.set(m_targetCtr.set(0,0,0));
        m_actualLoc.set(0 + (m_upAxis.getX() * 100),0 + (m_upAxis.getY() * 100), 0+ (m_upAxis.getZ() * 100));
        //m_targetDistance= 10;
        //m_distance      = m_targetDistance;

        m_targetYaw     = 0;
        m_actualYaw     = 0;

        m_watchEnable   = true;
        m_watchSpeed    = 0.2;
        m_watchCenter.set(16.3, 0, 0);
 
    }
    
    @Override
    public void init() {
        reinit(null);
    }

    @Override
	public void reinit(AbstractCamControl pcc) {
		if(pcc != this) {
			m_camera = getCanvasView().getCanvas().getCanvasRenderer().getCamera();
			m_ip.provider = null;
			//m_spatial     = null;
			if(pcc == null) {
				logger.debug("previous camera was null");
				m_spatialOffset.set(0,0,0);
				m_actualCtr.set(m_targetCtr);
				m_actualLoc.set(m_actualCtr.getX() + (m_upAxis.getX() * m_distance), m_actualCtr.getY() + (m_upAxis.getY() * m_distance), m_actualCtr.getZ() + (m_upAxis.getZ() * m_distance));
				m_actualYaw = m_targetYaw = 0;
			}
			else {
				m_ip.set(pcc.getIp());
				m_targetDistance = m_distance = pcc.getDistance();
				m_actualCtr.set(pcc.getCenter());
				m_targetCtr.set(pcc.getCenter());
				m_actualLoc.set(m_actualCtr.getX() + (m_upAxis.getX() * m_distance), m_actualCtr.getY() + (m_upAxis.getY() * m_distance), m_actualCtr.getZ() + (m_upAxis.getZ() * m_distance));
				m_targetYaw = yawFromVector(m_camera.getDirection());
				m_actualYaw = m_targetYaw;
				setCenterOfInterest(pcc.getCenterOfInterest(), pcc.getCenterOfInterestOffset());
			}
			updateYaw(m_actualYaw);
		}
		m_headLight = LightManager.instance().getSpotLight("headLight");
		adjustNearFarPlanes(m_distance);
	}


    /**
     * Register triggers
     */
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

    //-----------------------------------------------------
    @Override
    public void setCenterOfInterest(Spatial spatial, ReadOnlyVector3 offset) {
        if(m_ip.provider != null) {
            m_ip.provider.removeInterestPointListener(this);
        }

        m_spatial = spatial;
        m_spatialOffset.set(offset);

        // XXX In lieu of proper pick delegation, walk up the graph and see if there's an interest point provider
        m_ip.provider = null;
        if(spatial != null) {
            while(spatial != null) {
                if(InterestPointProvider.class.isAssignableFrom(spatial.getClass()) ) {
                    InterestPointProvider ipp = (InterestPointProvider)spatial;
                    setCenterOfInterest(ipp);
                    break;
                }
                spatial = spatial.getParent();
            }
        }
    }

    /**
     * 
     */
    @Override
    public void setCenterOfInterest(InterestPointProvider ipp) {
        if(m_ip.provider != null) {
            m_ip.provider.removeInterestPointListener(this);
        }
        if(ipp instanceof Spatial) {
            m_spatial = (Spatial)ipp;
        }
        m_spatialOffset.set(0,0,0);   
        m_ip.provider = ipp;

        if(m_ip.provider != null) {
            m_ip.provider.addInterestPointListener(this, m_ip.mode);
        }
    }

    /**
     * if appropriate, follow a spatial
     */
    protected void handleFollowSpatial() {
        Vector3 dir = Vector3.fetchTempInstance();
        if(m_spatial.getParent() == null) {
            m_spatial = null;
        }
        else {
            m_targetCtr.set(m_spatial.getWorldTranslation());
            m_targetCtr.addLocal(m_spatialOffset);
        }
        Vector3.releaseTempInstance(dir);
    }

    double lastYaw = 0;
    /**
     * if appropriate, follow interest point
     */
    protected void handleFollowInterestPoint() {
        m_targetCtr.set(m_ip.primary);
        m_targetCtr.addLocal(m_spatialOffset);
        if(m_ip.hasSecondary) {
            t_ipUp.set(m_ip.secondary);
            t_ipUp.subtractLocal(m_ip.primary);
            t_ipUp.normalizeLocal();
            m_targetYaw = yawFromVector(t_ipUp);
            noYawTweak();
//            if(lastYaw != m_targetYaw) {
//                System.out.println(String.format("%2.2f (%2.2f) [%2.2f %2.2f %2.2f]", 
//                        m_targetYaw,
//                        m_actualYaw,
//                        t_ipUp.getX(),
//                        t_ipUp.getY(),
//                        t_ipUp.getZ()) );
//            }
            lastYaw = m_targetYaw;
        }
        if(debugIcons != null) {
            debugIcons.primary.setTranslation(m_ip.primary);
            debugIcons.secondary.setTranslation(m_ip.secondary);
        }
    }
    Vector3 t_ipUp = new Vector3();

    /**
     * update the camera's location and orientation each frame
     * @param tpf the number of seconds that have passed since the last frame
     */
    @Override
    public void handleFrameUpdate(double tpf) {
        Vector3 tmp = Vector3.fetchTempInstance();
        Vector3 dir = Vector3.fetchTempInstance();
        Matrix3 rot = Matrix3.fetchTempInstance();

        if(m_ip.provider != null) {
            handleFollowInterestPoint();
        }
        else if(m_spatial != null) {
            handleFollowSpatial();
        }

        final double max = 1;
        double amt;

        // move actual values towards primary values based
        // on tension parameter
        amt = tpf * m_tension;
        if(amt > max) amt = max;

        tmp = m_targetCtr.multiply(amt, tmp);
        m_actualCtr.multiplyLocal(1-amt).addLocal(tmp);
        m_distance = m_distance*(1-amt) + m_targetDistance*amt;
        m_actualLoc.set(m_actualCtr.getX() + (m_upAxis.getX() * m_distance), m_actualCtr.getY() + (m_upAxis.getY() * m_distance), m_actualCtr.getZ() + (m_upAxis.getZ() * m_distance));

        m_camera.setLocation(m_actualLoc);

        final double eps = 0.001;
        final double yawDiff = m_targetYaw-m_actualYaw;
        if( yawDiff < eps || yawDiff > eps ) {
            final double yamt = amt * 0.5;
            m_actualYaw = m_actualYaw*(1-yamt) + m_targetYaw*yamt;
            updateYaw(m_actualYaw);
        }

        if(m_headLight != null) {
            m_headLight.setLocation(m_camera.getLocation());
            m_headLight.setDirection(m_camera.getDirection());
        }

        Vector3.releaseTempInstance(tmp);
        Vector3.releaseTempInstance(dir);
        Matrix3.releaseTempInstance(rot);
    }

    protected void updateYaw(double yaw) {
//      double a = Math.sin(yaw);
//      double b = Math.cos(yaw);
//      m_camUp.set(a, b, 0);
//      m_camUp.cross(m_camLook, m_camLeft);
      m_camera.setAxes(m_camLeft, m_camUp, m_camLook);
      m_camera.update();
  }

    void noYawTweak() {
        if(Math.abs(m_targetYaw-m_actualYaw) > Math.PI)
            m_zeroTo2Pi = !m_zeroTo2Pi;
          
        if(m_zeroTo2Pi) {
            m_targetYaw = zeroTo2Pi(m_targetYaw);
            m_actualYaw = zeroTo2Pi(m_actualYaw);
        }
        else {
            m_targetYaw = negPiToPi(m_targetYaw);
            m_actualYaw = negPiToPi(m_actualYaw);
        }
    }
    
    /**
     * 
     */
    private void crab(final double dx, final double dy) {
        m_crabSpeed = 0.83/m_camera.getHeight();
        moveInCameraPlane(dx, dy, m_crabSpeed);
    }

    /**
     * Move the camera in the camera's xy plane
     */
    private void moveInCameraPlane(final double dx, final double dy, final double speed) {
        CenterOfInterest.setCamera(this);
        Vector3 tmp  = Vector3.fetchTempInstance();
        Vector3 tmpY = Vector3.fetchTempInstance();

        ReadOnlyVector3 camX = m_camera.getLeft();
        ReadOnlyVector3 camY = m_camera.getUp();

        tmp = camX.multiply( speed*dx*m_distance, tmp);
        tmp.addLocal(camY.multiply(-speed*dy*m_distance, tmpY));

        m_targetCtr.addLocal(tmp);
        if(m_spatial != null) {
            m_spatialOffset.addLocal(tmp);
        }
        m_actualCtr.addLocal(tmp.multiplyLocal(0.9));

        Vector3.releaseTempInstance( tmp );
        Vector3.releaseTempInstance( tmpY );
    }


    /**
     * directly adjust the camera's yaw
     */
    private void spin(final double dx, final double dy) {
        final double amt = 0.002;
        m_actualYaw = angleValue(m_actualYaw += dx * amt);
        m_targetYaw = angleValue(m_targetYaw += dx * amt);
        updateYaw(m_actualYaw);
    }

    /**
     * adjust camera's height above center of interest
     */
    protected void dolly(final double dwheel) {
        CenterOfInterest.setCamera(this);
        m_targetDistance = m_targetDistance + m_targetDistance * dwheel * m_dollySpeed;
        
        double min = m_distanceRange[0];
        double max = m_distanceRange[1];
        if(m_targetDistance < min)
            m_targetDistance = min;
        if(m_targetDistance > max) 
            m_targetDistance = max;
        
        adjustNearFarPlanes(m_targetDistance);
    }


    // Trigger actions
    //===============================================================
    protected void keyInput(KeyboardState keyState) {
        KeyEvent kev = keyState.getKeyEvent();
        if(kev.getState().equals(KeyState.DOWN)) {
            switch(kev.getKey()) {
            case RIGHT: m_targetYaw -= 0.2; break;
            case LEFT:  m_targetYaw += 0.2; break;
            case UP:    m_targetYaw = 0;    break;
            default:
                break;
            }
        }
        noYawTweak();
    }

    protected void leftMouseClick(MouseState mouse) {
        switch(mouse.getClickCount(MouseButton.LEFT)) {
        case 2: pick(mouse); break;
        default:
            break;
        }
    }

    protected void rightMouseClick(MouseState mouse) {
        switch(mouse.getClickCount(MouseButton.RIGHT)) {
        case 2: 
            m_targetYaw = 0;
            m_actualYaw = negPiToPi(m_actualYaw);
            m_zeroTo2Pi = false;
            break;
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
        requestCameraControlChange(CamControlType.FollowCam);
    }
    FindInterestPointVisitor fipVisitor = new FindInterestPointVisitor();

    protected void pick(MouseState mouse) {
        logger.debug("pick");
        Vector2 tmpXY = Vector2.fetchTempInstance();
        Vector3 tmpDir = Vector3.fetchTempInstance();
        Vector3 tmpCoord0 = Vector3.fetchTempInstance();
        Vector3 tmpCoord1 = Vector3.fetchTempInstance();

        tmpXY.set(mouse.getX(), mouse.getY());

        tmpCoord0 = m_camera.getWorldCoordinates(tmpXY, 0, tmpCoord0);
        tmpCoord1 = m_camera.getWorldCoordinates(tmpXY, 1, tmpCoord1);
        tmpDir = tmpCoord1.subtract(tmpCoord0, tmpDir).normalizeLocal();
        Ray3 pickRay = new Ray3(tmpCoord0, tmpDir);
        //PickData closestPick = null;
        //PickResults results = 
        getCanvas().getCanvasRenderer().getScene().doPick(pickRay);

        Vector2.releaseTempInstance( tmpXY );
        Vector3.releaseTempInstance( tmpDir );
        Vector3.releaseTempInstance( tmpCoord0 );
        Vector3.releaseTempInstance( tmpCoord1 );
    }

    // Create Triggers
    //====================================================================
    InputTrigger createDraggedInputTrigger() {
        final FreeFlyerNadirCamControl control = this;
        final Predicate<TwoInputStates> someMouseDown 
        = Predicates.or(TriggerConditions.leftButtonDown(), Predicates.or(TriggerConditions.rightButtonDown(), TriggerConditions.middleButtonDown()));

        final Predicate<TwoInputStates> dragged = Predicates.and(TriggerConditions.mouseMoved(), someMouseDown);

        final TriggerAction dragAction = new TriggerAction() {

            @Override
            public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
                if( source.equals(control.getCanvas()) ) {
                    final MouseState mouse = inputStates.getCurrent().getMouseState();
                    final KeyboardState keybd = inputStates.getCurrent().getKeyboardState();
                    if (mouse.getDx() != 0 || mouse.getDy() != 0) {
                        Set<Key> keySet = keybd.getKeysDown();
                        if(keySet.size() == 0) {
                            if(mouse.getButtonState(MouseButton.RIGHT).equals(ButtonState.DOWN)) {
                                //control.spin(mouse.getDx(), mouse.getDy());
                            }
                            else {
                                //control.crab(mouse.getDx(), mouse.getDy());
                            }
                        }
                        else if(keybd.isDown(Key.LCONTROL) || keybd.isDown(Key.RCONTROL)) {
                            //control.dolly((int)(mouse.getDy()*0.6));
                        }
                    }
                }
            }
        };
        return new InputTrigger(dragged, dragAction);
    }

    InputTrigger createWheelInputTrigger() {
        final FreeFlyerNadirCamControl control = this;

        final TriggerAction wheelAction = new TriggerAction() {
            @Override
            public void perform(Canvas source, TwoInputStates inputStates, double tpf) {
                if( source.equals(control.getCanvas()) ) {
                    final MouseState mouse = inputStates.getCurrent().getMouseState();
                    if(mouse.getDwheel() != 0) {
                       // control.dolly(mouse.getDwheel());
                    }
                }
            }
        };
        return new InputTrigger(new MouseWheelMovedCondition(), wheelAction);
    }

    InputTrigger createLeftClickInputTrigger() {
        final FreeFlyerNadirCamControl control = this;
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

    InputTrigger createRightClickInputTrigger() {
        final FreeFlyerNadirCamControl control = this;
        //-- left click action --------------------------
        final TriggerAction leftClickAction = new TriggerAction() {
            @Override
            public void perform(Canvas source, TwoInputStates inputStates, double tpf) {
                if( source.equals(control.getCanvas()) ) {
                    final MouseState mouse = inputStates.getCurrent().getMouseState();
                    control.rightMouseClick(mouse);
                }
            }
        };
        return new InputTrigger(new MouseButtonClickedCondition(MouseButton.RIGHT), leftClickAction);
    }

    InputTrigger createKeyInputTrigger() {
        final FreeFlyerNadirCamControl control = this;
        final TriggerAction triggerAction = new TriggerAction() {
            @Override
            public void perform(Canvas source, TwoInputStates inputStates, double tpf) {
                if( source.equals(control.getCanvas()) ) {
                    keyInput(inputStates.getCurrent().getKeyboardState());
                }
            }
        };
        return new InputTrigger(new AnyKeyCondition(), triggerAction);
    }

    InputTrigger createWatchEnableTrigger() {
        final FreeFlyerNadirCamControl control = this;
        final TriggerAction triggerAction = new TriggerAction() {
            @Override
            public void perform(Canvas source, TwoInputStates inputStates, double tpf) {
                if( source.equals(control.getCanvas()) ) {
                    control.m_watchEnable = true;
                }
            }
        };
        return new InputTrigger(new MouseButtonReleasedCondition(MouseButton.LEFT), triggerAction);
    }
    InputTrigger createWatchDisableTrigger() {
        final FreeFlyerNadirCamControl control = this;
        final TriggerAction triggerAction = new TriggerAction() {
            @Override
            public void perform(Canvas source, TwoInputStates inputStates, double tpf) {
                if( source.equals(control.getCanvas()) ) {
                    control.m_watchEnable = false;
                }
            }
        };
        return new InputTrigger(new MouseButtonPressedCondition(MouseButton.LEFT), triggerAction);
    }

    InputTrigger createMiddleClickTrigger() {
        final FreeFlyerNadirCamControl control = this;
        //-- middle click action --------------------------
        final TriggerAction clickAction = new TriggerAction() {
            @Override
            public void perform(Canvas source, TwoInputStates inputStates, double tpf) {
                if( source.equals(control.getCanvas()) ) {
                    final MouseState mouse = inputStates.getCurrent().getMouseState();
                    control.middleMouseClick(mouse);
                }
            }
        };
        return new InputTrigger(new MouseButtonClickedCondition(MouseButton.MIDDLE), clickAction);
    }

}

