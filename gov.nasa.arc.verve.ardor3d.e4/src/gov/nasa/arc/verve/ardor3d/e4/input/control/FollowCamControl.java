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

import gov.nasa.arc.irg.freeflyer.utils.VerveConstants;
import gov.nasa.arc.irg.util.ui.UiTarget;
import gov.nasa.arc.verve.ardor3d.e4.Ardor3D;
import gov.nasa.arc.verve.ardor3d.e4.VerveArdor3dPreferences;
import gov.nasa.arc.verve.ardor3d.e4.framework.IVerveCanvasView;
import gov.nasa.arc.verve.ardor3d.e4.util.DeselectListenerRegistry;
import gov.nasa.arc.verve.common.interest.CenterOfInterest;
import gov.nasa.arc.verve.common.interest.CenterOfInterestCamera;
import gov.nasa.arc.verve.common.interest.InterestPointListener;
import gov.nasa.arc.verve.common.interest.InterestPointProvider;
import gov.nasa.arc.verve.common.util.LightManager;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ardor3d.bounding.BoundingVolume;
import com.ardor3d.framework.Canvas;
import com.ardor3d.input.ButtonState;
import com.ardor3d.input.Key;
import com.ardor3d.input.KeyboardState;
import com.ardor3d.input.MouseButton;
import com.ardor3d.input.MouseState;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.input.logical.MouseButtonClickedCondition;
import com.ardor3d.input.logical.MouseButtonPressedCondition;
import com.ardor3d.input.logical.MouseButtonReleasedCondition;
import com.ardor3d.input.logical.MouseWheelMovedCondition;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TriggerConditions;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.light.SpotLight;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector2;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.StereoCamera;
import com.ardor3d.scenegraph.Spatial;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * 
 * @author mallan
 *
 */
public class FollowCamControl extends AbstractCamControl implements InterestPointListener {
	static Logger logger = Logger.getLogger(FollowCamControl.class.getName());
	private static final boolean IS_WINDOWS = System.getProperty( "os.name" ).contains( "indow" );

	StereoCamera m_stereoCamera;

	Spatial m_spatial = null;
	final Vector3 m_spatialOffset = new Vector3(0, 0, 0);

	Vector3 defaultCameraCenter;
	Vector3 m_actualCtr;
	Vector3 m_targetCtr;

	Vector3 defaultCameraLocation;
	Vector3 m_actualLoc;
	private Vector3 m_targetLoc;

	private final Vector3 m_idealViewAngle = new Vector3(5, 5, 5);

	boolean m_watchEnable = true;
	double m_watchSpeed = 0.2;
	final Vector3 m_watchCenter = new Vector3(16.3, 0, 0);

	double m_distance;
	double m_focalDistance;

	double m_tension = 4;

	double m_swingSpeed = 0.0075;
	double m_dollySpeed = 0.05;
	double m_crabSpeed = 0.0025;
	boolean absoluteCrab = false;
	
	double m_dollyDragSpeed = 0.5;

	double m_azAngle = 0;
	double m_elAngle = 0;

	boolean m_nadirSnap = true;

	static int instanceCount = 0;
	public final int instance;

	protected enum Trigger {
		Dragged, Wheel, LeftClick, MiddleClick, WatchEnable, WatchDisable;
	}

	public final InputTrigger[] m_triggers = new InputTrigger[Trigger.values().length];

	SpotLight m_headLight = null;

	// for debugging
	public static DecimalFormat s_decimalFormat = new DecimalFormat("###.##");
	{
		s_decimalFormat.setDecimalSeparatorAlwaysShown(true);
		s_decimalFormat.setMinimumFractionDigits(2);
		s_decimalFormat.setMaximumFractionDigits(2);
		s_decimalFormat.setPositivePrefix(" ");
	}
	
	public FollowCamControl(final IVerveCanvasView canvasView,
			final ReadOnlyVector3 upAxis) {
		this(canvasView, upAxis, new Vector3(), new Vector3());
	}

	// =========================================================================
	public FollowCamControl(final IVerveCanvasView canvasView,
			final ReadOnlyVector3 upAxis, ReadOnlyVector3 cameraCenter, ReadOnlyVector3 cameraLocation) {
		super(canvasView, upAxis);

		Float swing = VerveConstants.getFloatValueOfParameter(VerveConstants.SWING_SPEED);
		Float dolly = VerveConstants.getFloatValueOfParameter(VerveConstants.DOLLY_SPEED);
		Float crab = VerveConstants.getFloatValueOfParameter(VerveConstants.CRAB_SPEED);
		absoluteCrab = VerveConstants.isFlagPresent(VerveConstants.ABSOLUTE_CRAB);
		
		setupCameraCenter(cameraCenter);
		
		setupCamLocation(cameraLocation);

		if(swing != null) {
			m_swingSpeed = swing;
		}
		if(dolly != null) {
			m_dollySpeed = dolly;
		}
		if(crab != null) {
			m_crabSpeed = crab;
		}
		
//		if(IS_WINDOWS) {
//			m_swingSpeed = 0.0040;
//			m_dollySpeed = 0.005;
//			m_crabSpeed = 0.0012;
//		}
		
		if (m_camera instanceof StereoCamera) {
			m_stereoCamera = (StereoCamera) m_camera;
		} else {
			m_stereoCamera = null;
		}
		instance = instanceCount++;

		m_triggers[Trigger.Dragged.ordinal()] = createDraggedInputTrigger();
		m_triggers[Trigger.Wheel.ordinal()] = createWheelInputTrigger();
		m_triggers[Trigger.LeftClick.ordinal()] = createPickInputTrigger();
		m_triggers[Trigger.WatchEnable.ordinal()] = createWatchEnableTrigger();
		m_triggers[Trigger.WatchDisable.ordinal()] = createWatchDisableTrigger();
		m_triggers[Trigger.MiddleClick.ordinal()] = createMiddleClickTrigger();

		m_targetLoc.subtract(m_targetCtr, m_idealViewAngle);

		// should really have a preference listener, but just grab the
		// preference on camera change
		m_nadirSnap = VerveArdor3dPreferences.isCameraNadirSnap();

		// MouseState.CLICK_TIME_MS = 1500;
	}
	
	protected void setupCameraCenter(ReadOnlyVector3 cc) {
		defaultCameraCenter = new Vector3(cc.getX(), cc.getY(), cc.getZ());
		m_actualCtr = new Vector3(defaultCameraCenter);
		m_targetCtr = new Vector3(m_actualCtr);
	}
	
	protected void setupCamLocation(ReadOnlyVector3 cl) {
		defaultCameraLocation = new Vector3(cl.getX(), cl.getY(), cl.getZ());
		m_actualLoc = new Vector3(defaultCameraLocation);
		m_targetLoc = new Vector3(m_actualLoc);
		
		m_distance = m_targetLoc.length();
		m_focalDistance = m_distance;
	}

	@Override
	public ReadOnlyVector3 getCenter() {
		return m_actualCtr;
	}

	@Override
	public AbstractCamControl setCenter(ReadOnlyVector3 vec) {
		m_actualCtr.set(vec);
		m_targetCtr.set(vec);
		return updateCamValues();
	}

	@Override
	public ReadOnlyVector3 getLocation() {
		return m_actualLoc;
	}

	@Override
	public AbstractCamControl setLocation(ReadOnlyVector3 vec) {
		m_actualLoc.set(vec);
		m_targetLoc.set(vec);
		return updateCamValues();
	}

	@Override
	public AbstractCamControl setLocationAndCenter(ReadOnlyVector3 loc,
			ReadOnlyVector3 ctr) {
		m_actualLoc.set(loc);
		m_targetLoc.set(loc);
		m_actualCtr.set(ctr);
		m_targetCtr.set(ctr);

		m_distance = m_actualCtr.distance(m_actualLoc);
		return updateCamValues();
	}

	protected AbstractCamControl updateCamValues() {
		m_camera.setLocation(m_actualLoc);
		m_camera.lookAt(m_actualCtr, m_upAxis);
		double dist = m_actualLoc.distance(m_actualCtr);
		// m_elAngle = getElAngle(m_actualLoc.getZ(),dist);
		return this;
	}

	@Override
	public double getDistance() {
		return m_distance;
	}

	@Override
	public AbstractCamControl setDistance(double distance) {
		m_distance = distance;
		dolly(0);
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
		m_spatialOffset.set(0, 0, 0);

		float z = 0;
		m_targetCtr.set(m_actualCtr.set(defaultCameraCenter));
		m_targetLoc.set(m_actualLoc.set(defaultCameraLocation));

		m_watchEnable = true;
		m_watchSpeed = 0.2;
		m_watchCenter.set(10, 0, z);

		m_distance = m_actualLoc.distance(m_actualCtr);
		m_focalDistance = m_distance;
		m_targetLoc.subtract(m_targetCtr, m_idealViewAngle);
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
		m_spatial = null;
		if (pcc == null) {
			m_spatialOffset.set(0, 0, 0);
			m_actualCtr.set(m_targetCtr);
			m_actualLoc.set(m_targetLoc);
		} else {
			m_ip.set(pcc.m_ip);
			m_distance = pcc.getDistance();
			m_targetCtr.set(pcc.getCenter());
			m_targetLoc.set(pcc.getLocation());
			m_actualCtr.set(m_targetCtr);
			m_actualLoc.set(m_targetLoc);
			Vector3 look = new Vector3(m_camera.getDirection());
			if (Math.abs(look.getZ()) > 0.98) {
				Vector3 dir = new Vector3();
				Vector3 tmp = new Vector3(m_camera.getUp());
				tmp.multiplyLocal(-0.1 * m_distance);
				m_actualLoc.addLocal(tmp); // bump location if we're coincident
				// with Z
				m_targetLoc.addLocal(tmp);
				m_elAngle = 1.1;

				m_targetLoc.subtract(m_targetCtr, m_idealViewAngle);

				dir = m_idealViewAngle;
				m_targetCtr.add(dir, m_targetLoc);
			}
			Spatial coi = pcc.getCenterOfInterest();
			ReadOnlyVector3 coio = pcc.getCenterOfInterestOffset();
			setCenterOfInterest(coi, coio);
		}
		m_camera.setLocation(m_actualLoc);
		m_camera.lookAt(m_actualCtr, m_upAxis);
		m_camera.normalize();
		m_camera.update();

		m_headLight = LightManager.instance().getSpotLight("headLight");
		m_nadirSnap = VerveArdor3dPreferences.isCameraNadirSnap();

		adjustNearFarPlanes(m_distance);
	}

	@Override
	public void registerWith(LogicalLayer logicalLayer) {
		for (InputTrigger trigger : m_triggers) {
			logicalLayer.registerTrigger(trigger);
		}
		Ardor3D.getCameraControlUpdater().registerCameraControl(this);
		CenterOfInterest.setCamera(this);
	}

	@Override
	public void deregisterFrom(LogicalLayer logicalLayer) {
		for (InputTrigger trigger : m_triggers) {
			logicalLayer.deregisterTrigger(trigger);
		}
		Ardor3D.getCameraControlUpdater().removeCameraControl(this);

		if (m_ip.provider != null) {
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

	// -----------------------------------------------------
	@Override
	public void setCenterOfInterest(Spatial spatial, ReadOnlyVector3 offset) {
		if (m_ip.provider != null) {
			m_ip.provider.removeInterestPointListener(this);
		}

		m_spatial = spatial;
		m_spatialOffset.set(offset);

		// XXX In lieu of proper pick delegation, walk up the graph and see if
		// there's an interest point provider
		m_ip.provider = null;
		if (spatial != null) {
			while (spatial != null) {
				if (InterestPointProvider.class.isAssignableFrom(spatial
						.getClass())) {
					InterestPointProvider ipp = (InterestPointProvider) spatial;
					if (ipp.isInterestPointEnabled()) {
						if (m_ccUi != null) {
							m_ccUi.setFollow(ipp);
						}
						setCenterOfInterest(ipp);
					} else {
						// TODO: the isInterestPointEnabled() semantics are
						// too vague. We still want to notify the UI that
						// an InterestPoint has been selected in most cases
						UiTarget.targetChanged(spatial.getName());
					}
					break;
				}
				spatial = spatial.getParent();
			}
		}
	}

	public void seekSelection(Spatial contents) {
		if (contents != null){
			CenterOfInterestCamera c = CenterOfInterest.getCamera();
			BoundingVolume b = contents.getWorldBound();
			ReadOnlyVector3 ctr = contents.getWorldTranslation();
			if (b != null) {
				Vector3 off = b.getCenter().subtract(ctr, new Vector3());
				c.setCenterOfInterest(contents, off);
			} else {
				c.setCenterOfInterest(contents, new Vector3());
			}
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void setCenterOfInterest(InterestPointProvider ipp) {
		final InterestPointProvider oldIpp = m_ip.provider;
		if (ipp != m_ip.provider) {
			if (m_ip.provider != null) {
				m_ip.provider.removeInterestPointListener(this);
			}
			if (ipp instanceof Spatial) {
				m_spatial = (Spatial) ipp;
			}
			m_spatialOffset.set(0, 0, 0);
			m_ip.provider = ipp;

			if (m_ip.provider != null) {
				m_ip.provider.addInterestPointListener(this, m_ip.mode);
			}
			if (m_ccUi != null && oldIpp != ipp) {
				m_ccUi.setFollow(ipp);
			}
		} else {
			m_spatialOffset.set(0, 0, 0);
		}
	}

	private void handleFollowSpatial() {
		if (m_spatial.getParent() == null) {
			m_spatial = null;
		} else {
			m_targetCtr.add(m_idealViewAngle, m_targetLoc);
			m_targetCtr.set(m_spatial.getWorldTranslation());
			m_targetCtr.addLocal(m_spatialOffset);
		}
	}

	private void handleFollowInterestPoint() {
		Vector3 tmp = Vector3.fetchTempInstance();
		Matrix3 rot = Matrix3.fetchTempInstance();
		Vector3 sdir = Vector3.fetchTempInstance();
		Vector3 sper = Vector3.fetchTempInstance();
		Vector3 ldir = Vector3.fetchTempInstance();

		// -- secondary point --
		if (m_watchEnable && m_ip.hasSecondary) {
			m_watchCenter.set(m_ip.secondary);
			if (debugIcons != null)
				debugIcons.secondary.setTranslation(m_watchCenter);

			sdir = m_watchCenter.subtract(m_targetCtr, sdir);
			sdir.setZ(0);
			sdir.normalizeLocal();
			sdir.cross(Vector3.UNIT_Z, sper);
			ldir.set(m_camera.getDirection());
			ldir.setZ(0);
			ldir.normalizeLocal();
			double azDiff = ldir.smallestAngleBetween(sdir);
			if (!Double.isNaN(azDiff)) { // secondary out for NaN
				azDiff /= Math.PI;
				// azDiff = azDiff*azDiff; // dampen oscillation
				if (sper.dot(ldir) > 0) {
					azDiff = -azDiff;
				}
				rot.fromAngleAxis(azDiff * m_watchSpeed, Vector3.UNIT_Z);
				rot.applyPre(m_idealViewAngle, tmp);
				m_idealViewAngle.set(tmp);
			}
		}

		m_targetCtr.add(m_idealViewAngle, m_targetLoc);

		m_targetCtr.set(m_ip.primary);
		m_targetCtr.addLocal(m_spatialOffset);

		Vector3.releaseTempInstance(tmp);
		Matrix3.releaseTempInstance(rot);
		Vector3.releaseTempInstance(sdir);
		Vector3.releaseTempInstance(sper);
		Vector3.releaseTempInstance(ldir);
	}

	/**
	 * called each frame to update camera position
	 */
	// ------------------------------------------------------------------------------------------
	@Override
	public void handleFrameUpdate(double tpf) {
		Vector3 tmp = Vector3.fetchTempInstance();
		Vector3 dir = Vector3.fetchTempInstance();
		Matrix3 rot = Matrix3.fetchTempInstance();

		/*
		 * logger.debug(String.format(
		 * "camera loc=%.2f, %.2f, %.2f, dist=%.2f, elAngle=%.2f",
		 * m_actualLoc.getX(), m_actualLoc.getY(), m_actualLoc.getZ(),
		 * m_distance, m_el));/*
		 */

		if (m_ip.provider != null) {
			handleFollowInterestPoint();
		} else if (m_spatial != null) {
			handleFollowSpatial();
		}

		final double max = 1;
		double amt;

		// move actual values towards primary values based
		// on tension parameter
		amt = tpf * m_tension;
		if (amt > max)
			amt = max;

		tmp = m_targetLoc.multiply(amt, tmp);

		m_actualLoc.multiplyLocal(1 - amt).addLocal(tmp);

		tmp = m_targetCtr.multiply(amt, tmp); 
		m_actualCtr.multiplyLocal(1 - amt).addLocal(tmp);

		m_camera.setLocation(m_actualLoc);
		m_camera.lookAt(m_actualCtr, m_upAxis);
			
		if (debugIcons != null)
			debugIcons.primary.setTranslation(m_targetCtr);

		if (m_headLight != null && this == CenterOfInterest.getCamera()) {
			m_headLight.setLocation(m_camera.getLocation());
			m_headLight.setDirection(m_camera.getDirection());
		}

		// http://local.wasp.uwa.edu.au/~pbourke/miscellaneous/stereorender/
		if (m_stereoCamera != null) {
			m_focalDistance = m_focalDistance * (1 - amt) + (m_distance * amt);
			m_stereoCamera.setFocalDistance(m_focalDistance);
			m_stereoCamera.setEyeSeparation(m_focalDistance / 30.0);

			m_stereoCamera.setupLeftRightCameras();
		}

		Vector3.releaseTempInstance(tmp);
		Vector3.releaseTempInstance(dir);
		Matrix3.releaseTempInstance(rot);
	}

	/**
	 * 
	 * @param dx
	 * @param dy
	 * @param maintainCenter
	 */
	private void moveInCameraPlane(final double dx, final double dy,
			final double speed, final boolean maintainCenter) {
		CenterOfInterest.setCamera(this);
		Vector3 tmpX = Vector3.fetchTempInstance();
		Vector3 tmpY = Vector3.fetchTempInstance();
		Vector3 tmpLoc = Vector3.fetchTempInstance();
		Vector3 tmpDiff = Vector3.fetchTempInstance();
		Vector3 tmpLook = Vector3.fetchTempInstance();

		ReadOnlyVector3 camX = m_camera.getLeft();
		ReadOnlyVector3 camY = m_camera.getUp();

		tmpX = camX.multiply(speed * dx * m_distance, tmpX);
		tmpY = camY.multiply(-speed * dy * m_distance, tmpY);
			
		double dist;
		tmpLoc.set(m_targetLoc);
		tmpLoc.addLocal(tmpX);
		tmpLoc.addLocal(tmpY);
		if (maintainCenter) {
			tmpDiff = tmpLoc.subtract(m_targetCtr, tmpDiff);
			dist = tmpDiff.length();
			// check if new view direction will be coincident with upAxis
			tmpDiff.multiply(1 / dist, tmpLook);
			final double dot = tmpLook.dot(m_upAxis);
			final double eps = 0.0025;
			if (dot < 1 - eps && dot > -1 + eps) {
				tmpDiff.multiplyLocal(m_distance / dist);
				tmpLoc = tmpDiff.add(m_targetCtr, tmpLoc);
			} else { // if so, don't move the camera
				tmpLoc.set(m_targetLoc);
				if (m_nadirSnap && dot > 1 - eps) {
					requestCameraControlChange(CamControlType.NadirCam);
				}
			}
		} else {
			m_targetCtr.addLocal(tmpX);
			m_targetCtr.addLocal(tmpY);
			m_spatialOffset.addLocal(tmpX);
			m_spatialOffset.addLocal(tmpY);
		}

		m_targetLoc.set(tmpLoc);

		tmpDiff = m_targetLoc.subtract(m_targetCtr, tmpDiff);
		m_targetLoc.subtract(m_targetCtr, m_idealViewAngle);

		Vector3.releaseTempInstance(tmpX);
		Vector3.releaseTempInstance(tmpY);
		Vector3.releaseTempInstance(tmpLoc);
		Vector3.releaseTempInstance(tmpDiff);
		Vector3.releaseTempInstance(tmpLook);
	}

	/**
	 * 
	 * @param dx
	 * @param dy
	 * @param maintainCenter
	 */
	private void crab(final double dx, final double dy) {
		if(!absoluteCrab) {
			m_crabSpeed = 0.83 / m_camera.getHeight();
		}
		moveInCameraPlane(dx, dy, m_crabSpeed, false);
	}

	/**
	 * 
	 * @param dx
	 * @param dy
	 */
	public void swing(final double dx, final double dy) {
		moveInCameraPlane(dx, dy, m_swingSpeed, true);
	}

	/**
	 * 
	 * @param dwheel
	 */
	public void dolly(final double dwheel) {
		CenterOfInterest.setCamera(this);

		double dist;
		Vector3 tmpLoc = Vector3.fetchTempInstance();
		Vector3 tmpDiff = Vector3.fetchTempInstance();

		dist = m_distance + m_distance * dwheel * m_dollySpeed;
		// double min = m_camera.getFrustumNear() * s_dollyNearMult;
		// double max = m_camera.getFrustumFar() * s_dollyFarMult;
		if (dist < m_distanceRange[0])
			dist = m_distanceRange[0];
		if (dist > m_distanceRange[1])
			dist = m_distanceRange[1];

		m_distance = dist;
		tmpLoc.set(m_targetLoc);
		tmpDiff = tmpLoc.subtract(m_targetCtr, tmpDiff);
		dist = tmpDiff.length();
		tmpDiff.multiplyLocal(m_distance / dist);
		tmpLoc = tmpDiff.add(m_targetCtr, tmpLoc);
		m_targetLoc.set(tmpLoc);

		tmpDiff = m_targetLoc.subtract(m_targetCtr, tmpDiff);

		adjustNearFarPlanes(tmpDiff.length());
		m_targetLoc.subtract(m_targetCtr, m_idealViewAngle);

		Vector3.releaseTempInstance(tmpLoc);
		Vector3.releaseTempInstance(tmpDiff);
	}

	protected void leftMouseClick(MouseState mouse) {
		switch (mouse.getClickCount(MouseButton.LEFT)) {
		case 2:
			pick(mouse);
			break;
		case 1:
			PickResults pr = pick(mouse);
			if(pr.getNumber() == 0) {
				DeselectListenerRegistry.onDeselect();
			}
			break;
		default:
			DeselectListenerRegistry.onDeselect();
			break;
		}
	}

	protected void middleMouseClick(MouseState mouse) {
		System.out.println("in middleMouseClick(MouseState mouse)");
		List<InterestPointProvider> ippList = fipVisitor
				.execute(getCanvasView().getScene().getRoot());
		System.out.println("InterestPointProviders:");
		for (InterestPointProvider ipp : ippList) {
			System.out.println("    " + ipp.getInterestPointName());
		}

		// Display.getDefault().asyncExec(
		// new Runnable() {
		// public void run() {
		// getCanvasView().selectCamera(CameraControlType.NadirCam);
		// }
		// });
	}

	FindInterestPointVisitor fipVisitor = new FindInterestPointVisitor();

	protected PickResults pick(MouseState mouse) {
		// createDebugIcons(true, m_canvasView);
		Vector2 tmpXY = Vector2.fetchTempInstance();
		Vector3 tmpDir = Vector3.fetchTempInstance();
		Vector3 tmpCoord0 = Vector3.fetchTempInstance();
		Vector3 tmpCoord1 = Vector3.fetchTempInstance();

		tmpXY.set(mouse.getX(), mouse.getY());

		tmpCoord0 = m_camera.getWorldCoordinates(tmpXY, 0.0, tmpCoord0);
		tmpCoord1 = m_camera.getWorldCoordinates(tmpXY, 0.5, tmpCoord1);

		tmpDir = tmpCoord1.subtract(tmpCoord0, tmpDir).normalizeLocal();
		Ray3 pickRay = new Ray3(tmpCoord0, tmpDir);
		PickResults pr = m_canvasView.getScene().doPick(pickRay, mouse.getX(), mouse.getY());

		Vector2.releaseTempInstance(tmpXY);
		Vector3.releaseTempInstance(tmpDir);
		Vector3.releaseTempInstance(tmpCoord0);
		Vector3.releaseTempInstance(tmpCoord1);
		
		return pr;
	}

	/**
	 * 
	 */
	InputTrigger createDraggedInputTrigger() {
		final FollowCamControl control = this;
		final Predicate<TwoInputStates> someMouseDown = Predicates.or(
				TriggerConditions.leftButtonDown(), Predicates.or(
						TriggerConditions.rightButtonDown(),
						TriggerConditions.middleButtonDown()));

		final Predicate<TwoInputStates> dragged = Predicates.and(
				TriggerConditions.mouseMoved(), someMouseDown);

		final TriggerAction dragAction = new TriggerAction() {

			@Override
			public void perform(final Canvas source,
					final TwoInputStates inputStates, final double tpf) {
				if (source.equals(control.getCanvas())) {
					final MouseState mouse = inputStates.getCurrent()
							.getMouseState();
					final KeyboardState keybd = inputStates.getCurrent()
							.getKeyboardState();
					if (mouse.getDx() != 0 || mouse.getDy() != 0) {
						Set<Key> keySet = keybd.getKeysDown();
						if (keySet.size() == 0) {
							if (mouse.getButtonState(MouseButton.LEFT).equals(
									ButtonState.DOWN)) {
								control.swing(mouse.getDx(), mouse.getDy());
							} else if (mouse.getButtonState(MouseButton.RIGHT)
									.equals(ButtonState.DOWN)) {
								control.crab(mouse.getDx(), mouse.getDy());
								// XXX this gets called incorrectly after right-clicking to open the popup menu
							}
						} else if (keybd.isDown(Key.LCONTROL)
								|| keybd.isDown(Key.RCONTROL)) {
							// if(mouse.getButtonState(MouseButton.LEFT).equals(ButtonState.DOWN))
							// {
							control.dolly((int) (mouse.getDy() * 0.1));
							// }
						}
					}
				}
			}
		};
		return new InputTrigger(dragged, dragAction);
	}

	InputTrigger createWheelInputTrigger() {
		final FollowCamControl control = this;

		final TriggerAction wheelAction = new TriggerAction() {
			@Override
			public void perform(Canvas source, TwoInputStates inputStates,
					double tpf) {
				if (source.equals(control.getCanvas())) {
					final MouseState mouse = inputStates.getCurrent()
							.getMouseState();
					if (mouse.getDwheel() != 0) {
						control.dolly(mouse.getDwheel());
					}
				}
			}
		};
		return new InputTrigger(new MouseWheelMovedCondition(), wheelAction);
	}

	InputTrigger createPickInputTrigger() {
		final FollowCamControl control = this;
		// -- left click action --------------------------
		final TriggerAction leftClickAction = new TriggerAction() {
			@Override
			public void perform(Canvas source, TwoInputStates inputStates,
					double tpf) {
				if (source.equals(control.getCanvas())) {
					final MouseState mouse = inputStates.getCurrent()
							.getMouseState();
					control.leftMouseClick(mouse);
				}
			}
		};
		return new InputTrigger(new MouseButtonClickedCondition(
				MouseButton.LEFT), leftClickAction);
	}

	InputTrigger createWatchEnableTrigger() {
		final FollowCamControl control = this;
		final TriggerAction triggerAction = new TriggerAction() {
			@Override
			public void perform(Canvas source, TwoInputStates inputStates,
					double tpf) {
				if (source.equals(control.getCanvas())) {
					control.m_watchEnable = true;
				}
			}
		};
		return new InputTrigger(new MouseButtonReleasedCondition(
				MouseButton.LEFT), triggerAction);
	}

	InputTrigger createWatchDisableTrigger() {
		final FollowCamControl control = this;
		final TriggerAction triggerAction = new TriggerAction() {
			@Override
			public void perform(Canvas source, TwoInputStates inputStates,
					double tpf) {
				if (source.equals(control.getCanvas())) {
					control.m_watchEnable = false;
				}
			}
		};
		return new InputTrigger(new MouseButtonPressedCondition(
				MouseButton.LEFT), triggerAction);
	}

	InputTrigger createMiddleClickTrigger() {
		final FollowCamControl control = this;
		// -- middle click action --------------------------
		final TriggerAction clickAction = new TriggerAction() {
			@Override
			public void perform(Canvas source, TwoInputStates inputStates,
					double tpf) {
				if (source.equals(control.getCanvas())) {
					final MouseState mouse = inputStates.getCurrent()
							.getMouseState();
					control.middleMouseClick(mouse);
				}
			}
		};
		return new InputTrigger(new MouseButtonClickedCondition(
				MouseButton.MIDDLE), clickAction);
	}

}
