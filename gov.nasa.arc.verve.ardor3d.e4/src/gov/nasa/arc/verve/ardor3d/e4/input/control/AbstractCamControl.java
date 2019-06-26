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
package gov.nasa.arc.verve.ardor3d.e4.input.control;

import gov.nasa.arc.verve.ardor3d.e4.framework.IVerveCanvasView;
import gov.nasa.arc.verve.common.interest.CenterOfInterestCamera;
import gov.nasa.arc.verve.common.interest.InterestPointData;
import gov.nasa.arc.verve.common.interest.InterestPointListener;
import gov.nasa.arc.verve.common.interest.InterestPointProvider;

import java.util.List;

import com.ardor3d.framework.Canvas;
import com.ardor3d.framework.CanvasRenderer;
import com.ardor3d.input.MouseState;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector2;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Line;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.scenegraph.hint.LightCombineMode;
import com.ardor3d.scenegraph.shape.Box;
import com.ardor3d.scenegraph.shape.Sphere;

/**
 * 
 */
public abstract class AbstractCamControl implements CenterOfInterestCamera, InterestPointListener {
    //private static final Logger logger = Logger.getLogger(AbstractCamControl.class);
    
    public final double  M_2PI  = Math.PI*2;
    public final double  M_PI_2 = Math.PI/2;
    
    protected Camera                  m_camera;
    protected IVerveCanvasView        m_canvasView;
    protected final Vector3           m_upAxis;
    
    protected boolean                 m_zeroTo2Pi = false;

    protected final InterestPointData m_ip = new InterestPointData();
    
    protected final double[]          m_distanceRange = new double[] { 0.001, 40000 };
    
    protected ICamControlUi           m_ccUi = null;
    
    /**
     * 
     * @param canvasView
     * @param upAxis
     */
    public AbstractCamControl(final IVerveCanvasView canvasView, ReadOnlyVector3 upAxis) {
        m_upAxis = new Vector3(upAxis);
        setCanvasView(canvasView);
        //createDebugIcons(false, canvasView);
    }

    public void setCanvasView(IVerveCanvasView canvasView) {
        m_canvasView = canvasView;
        if(m_canvasView != null) {
            final Canvas canvas = getCanvas();
            final CanvasRenderer canvasRenderer = canvas.getCanvasRenderer();
            m_camera = canvasRenderer.getCamera();
        }
    }
    
    public Canvas getCanvas() {
        return m_canvasView.getCanvas();
    }

    public IVerveCanvasView getCanvasView() {
        return m_canvasView;
    }

    public ReadOnlyVector3 getUpAxis() {
        return m_upAxis;
    }
    public void setUpAxis(ReadOnlyVector3 upAxis) {
        m_upAxis.set(upAxis);
    }
    
    public double[] getDistanceRange() {
        return m_distanceRange;
    }
    public void setDistanceRange(double min, double max) {
        m_distanceRange[0] = min;
        m_distanceRange[1] = max;
    }

    public ICamControlUi getCamControlUi() {
        return m_ccUi;
    }
    public void setCamControlUi(ICamControlUi ccui) {
        m_ccUi = ccui;
    }
    
    abstract public ReadOnlyVector3 getCenter();
    abstract public ReadOnlyVector3 getLocation();
    abstract public double          getDistance();
    abstract public Spatial         getCenterOfInterest();
    abstract public ReadOnlyVector3 getCenterOfInterestOffset();
    
    abstract public AbstractCamControl setCenter(ReadOnlyVector3 v);
    abstract public AbstractCamControl setLocation(ReadOnlyVector3 v);
    abstract public AbstractCamControl setLocationAndCenter(ReadOnlyVector3 location, ReadOnlyVector3 center);
    abstract public AbstractCamControl setDistance(double distance);

    abstract public void init();
    abstract public void reinit(AbstractCamControl previousCamControl);
    abstract public void reset();

    @Override
    abstract public void setCenterOfInterest(Spatial spatial, ReadOnlyVector3 offset);
    abstract public void setCenterOfInterest(InterestPointProvider ipp);

    public void setCenterOfInterest(String interestPointName) {
        if(interestPointName != null && interestPointName.length() > 0) {
            List<InterestPointProvider> list;
            FindInterestPointVisitor ippv = new FindInterestPointVisitor();
            list = ippv.execute(getCanvasView().getScene().getRoot());
            for(InterestPointProvider ipp : list) {
                final String ippName = ipp.getInterestPointName();
                //if( interestPointName.matches(ipp.getInterestPointName()) ) {
                if( interestPointName.equals(ippName) ) {
                    setCenterOfInterest(ipp);
                    //if(ipp instanceof Spatial) {
                    //    setCenterOfInterest((Spatial)ipp, Vector3.ZERO);
                    //}
                }
            }
        }
    }
    public List<InterestPointProvider> getInterestPointProviders() {
        List<InterestPointProvider> list;
        FindInterestPointVisitor ippv = new FindInterestPointVisitor();
        list = ippv.execute(getCanvasView().getScene().getRoot());
        return list;
    }



    abstract public void registerWith(LogicalLayer logicalLayer);
    abstract public void deregisterFrom(LogicalLayer logicalLayer);

    abstract public void handleFrameUpdate(double tpf);

    public Camera getCamera() {
        return m_canvasView.getCanvas().getCanvasRenderer().getCamera();
    }

    /**
     * request a camera control change from the VerveCanvasView
     * @param newType
     */
    public void requestCameraControlChange(final CamControlType newType) {
        m_canvasView.selectCameraControl(newType);
    }

    /**
     * 
     */
    @Override
    public void interestPointUpdated(InterestPointProvider ipp,
            String viewMode, ReadOnlyVector3 primaryInterestPoint,
            ReadOnlyVector3 secondaryInterestPoint) {
        m_ip.mode = viewMode;
        m_ip.primary.set(primaryInterestPoint);
        if(secondaryInterestPoint == null) {
            m_ip.hasSecondary = false;
        }
        else {
            m_ip.hasSecondary = true;
            m_ip.secondary.set(secondaryInterestPoint);
        }
    }

    public String getInterestPointMode() {
        return m_ip.mode;
    }
    public void setInterestPointMode(String mode) {
        m_ip.mode = mode;
        if(m_ip.provider != null) {
            m_ip.provider.addInterestPointListener(this, mode);
        }
    }

    protected DebugIcons debugIcons = null;
    protected class DebugIcons {
        public Node node;
        public Mesh primary;
        public Mesh secondary;
        public Mesh tertiary;
        public Line line;

        public void setRenderState(Mesh mesh, ReadOnlyColorRGBA color) {
            mesh.setDefaultColor(color);
            mesh.getSceneHints().setLightCombineMode(LightCombineMode.Off);
        }
    }
    
    protected void createDebugIcons(boolean doIt/*avoid stupid unused warning*/, IVerveCanvasView canvasView) {
        System.err.println("createDebugIcons");
        if(doIt && debugIcons == null) {
            debugIcons = new DebugIcons();
            debugIcons.node = new Node("debugIcons");
            debugIcons.primary = new Sphere("primary", 8, 16, 0.6f);
            debugIcons.secondary = new Box("secondary", Vector3.ZERO, 0.25, 0.25, 0.25);
            debugIcons.tertiary   = new Sphere("tertiary", 8, 16, 0.6f);
            debugIcons.node.attachChild(debugIcons.primary);
            debugIcons.node.attachChild(debugIcons.secondary);
            debugIcons.node.attachChild(debugIcons.tertiary);
            canvasView.getScene().getMarkupRoot().attachChild( debugIcons.node );
            debugIcons.setRenderState(debugIcons.primary, ColorRGBA.BLUE);
            debugIcons.setRenderState(debugIcons.secondary, ColorRGBA.RED);
            debugIcons.setRenderState(debugIcons.tertiary, ColorRGBA.GREEN);

            ZBufferState zs = new ZBufferState();
            //WireframeState ws = new WireframeState();
            debugIcons.node.setRenderState(zs);
            //debugIcons.node.setRenderState(ws);
        }
    }
    
    protected void showDebugIcons(boolean doShow) {
        if(debugIcons != null) {
            if(doShow) {
                debugIcons.node.getSceneHints().setCullHint(CullHint.Dynamic);
            }
            else {
                debugIcons.node.getSceneHints().setCullHint(CullHint.Always);
            }
        }
    }
    
    protected Ray3 getPickRay(MouseState mouse) {
        return getPickRay(mouse, m_camera);
    }
    protected Ray3 getPickRay(MouseState mouse, Camera camera) {
        Vector2 tmpXY = Vector2.fetchTempInstance();
        Vector3 tmpDir = Vector3.fetchTempInstance();
        Vector3 tmpCoord0 = Vector3.fetchTempInstance();
        Vector3 tmpCoord1 = Vector3.fetchTempInstance();

        tmpXY.set(mouse.getX(), mouse.getY());

        tmpCoord0 = camera.getWorldCoordinates(tmpXY, 0, tmpCoord0);
        tmpCoord1 = camera.getWorldCoordinates(tmpXY, 1, tmpCoord1);
        tmpDir = tmpCoord1.subtract(tmpCoord0, tmpDir).normalizeLocal();
        Ray3 pickRay = new Ray3(tmpCoord0, tmpDir);

        Vector2.releaseTempInstance( tmpXY );
        Vector3.releaseTempInstance( tmpDir );
        Vector3.releaseTempInstance( tmpCoord0 );
        Vector3.releaseTempInstance( tmpCoord1 );
        return pickRay;
    }

    protected double angleValue(double angle) {
        if(m_zeroTo2Pi) return zeroTo2Pi(angle);
        else            return negPiToPi(angle);
    }
    
    /** 
     * return a value that is between 0 and 2*PI
     */
    protected double zeroTo2Pi(double angle) {
        while(angle < 0)
            angle += M_2PI;
        while(angle > M_2PI)
            angle -= M_2PI;
        return angle;
    }
    
    /** 
     * return a value that is between -PI and PI
     */
    protected double negPiToPi(double angle) {
        while(angle < -Math.PI) 
            angle += M_2PI;
        while(angle > Math.PI) 
            angle -= M_2PI;
        return angle;
    }

    /**
     * calculate a yaw value from a normalized "up" vector
     */
    protected double yawFromVector(ReadOnlyVector3 vec) {
        Vector3 calcYaw = Vector3.fetchTempInstance();
        double yaw = 0;
        calcYaw.set(vec);
        if( Math.abs(calcYaw.getZ()) < 0.999 ) {
            calcYaw.setZ(0);
            calcYaw.normalizeLocal();
            double x = calcYaw.getX();
            double y = calcYaw.getY();
            if(x >= 0) {
                if(y >= 0) yaw = Math.asin(x);
                else       yaw = Math.acos(y);
            }
            else {
                if(y >= 0) yaw = Math.PI+Math.acos(-y);
                else       yaw = Math.PI+Math.asin(-x);
            }
        }
        Vector3.releaseTempInstance(calcYaw);
        return angleValue(yaw);
    }

    private double farMax   = 100000;
    private double nearMax  = 10;
    private double farMult  = 50000; 
    private double nearMult = 1.0 / 5.0;
    
    protected void adjustNearFarPlanes(double distance) {
        double fovY = m_camera.getFovY(); 
        double near = distance * nearMult;
        double far  = distance * farMult;
        if(near > nearMax) {
            near = nearMax;
        }
        if(far > farMax) {
            far = farMax;
        }
        final double aspect = (float) m_camera.getWidth() / (float) m_camera.getHeight() ;
        m_camera.setFrustumPerspective(fovY, aspect, near, far);
    }

	/**
	 * @return the ip
	 */
	public InterestPointData getIp() {
		return m_ip;
	}


}
