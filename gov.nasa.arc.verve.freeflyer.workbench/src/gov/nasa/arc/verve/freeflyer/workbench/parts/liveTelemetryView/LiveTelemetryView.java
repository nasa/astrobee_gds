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
package gov.nasa.arc.verve.freeflyer.workbench.parts.liveTelemetryView;

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.iss.ui.IssUiActivator;
import gov.nasa.arc.irg.plan.ui.io.EnlargeableButton;
import gov.nasa.arc.irg.util.ui.ColorProvider;
import gov.nasa.arc.irg.util.ui.IrgUI;
import gov.nasa.arc.verve.ardor3d.e4.Ardor3D;
import gov.nasa.arc.verve.ardor3d.e4.framework.IVerveCanvasView;
import gov.nasa.arc.verve.ardor3d.e4.input.control.AbstractCamControl;
import gov.nasa.arc.verve.ardor3d.e4.input.control.CamControlType;
import gov.nasa.arc.verve.ardor3d.e4.input.control.EarthCamControl;
import gov.nasa.arc.verve.ardor3d.e4.input.control.FirstPersonCamControl;
import gov.nasa.arc.verve.ardor3d.e4.input.control.FollowCamControl;
import gov.nasa.arc.verve.ardor3d.e4.input.control.NadirCamControl;
import gov.nasa.arc.verve.common.IVerveScene;
import gov.nasa.arc.verve.common.ardor3d.framework.BasicVerveScene;
import gov.nasa.arc.verve.common.ardor3d.interact.VerveInteractManager;
import gov.nasa.arc.verve.freeflyer.workbench.dialog.SceneGraphControlsDialog;
import gov.nasa.arc.verve.freeflyer.workbench.helpers.LiveTelemetryViewMovementRegistry;
import gov.nasa.arc.verve.freeflyer.workbench.parts.handraileditor.HandrailModelingNode;
import gov.nasa.arc.verve.freeflyer.workbench.parts.keepouteditor.KeepoutModelingNode;
import gov.nasa.arc.verve.freeflyer.workbench.plantrace.CreatePlanTrace;
import gov.nasa.arc.verve.freeflyer.workbench.scenario.AddViaMapPlane;
import gov.nasa.arc.verve.freeflyer.workbench.scenario.FreeFlyerScenario;
import gov.nasa.arc.verve.freeflyer.workbench.scenario.LightsCameraProperties;
import gov.nasa.arc.verve.rcp.e4.canvas.AbstractArdor3dViewLwjgl;
import gov.nasa.arc.verve.rcp.e4.canvas.Ardor3dCanvas;
import gov.nasa.arc.verve.rcp.e4.canvas.control.FollowCamControlActions;
import gov.nasa.arc.verve.robot.AbstractRobot;
import gov.nasa.arc.verve.robot.freeflyer.RapidFreeFlyerRobot;
import gov.nasa.arc.verve.robot.freeflyer.parts.RobotPartDraggablePreview;
import gov.nasa.rapid.v2.e4.agent.Agent;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.IPartListener;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.ardor3d.input.PhysicalLayer;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Spatial;


abstract public class LiveTelemetryView extends AbstractArdor3dViewLwjgl implements IPartListener {
	private static final Logger logger = Logger.getLogger(LiveTelemetryView.class);

	public static final String  ID                = LiveTelemetryView.class.getName();
	public static final String  VERVE_SCENARIO_ID = "gov.nasa.arc.verve.rcp.scenario.VerveScenario";

	private IVerveScene          scene           = null;

	private SceneGraphControlsDialog sceneGraphControlsDialog;
	public static final double MOVE_AMOUNT = 25;
	public static final double ZOOM_AMOUNT = 10;
	public static final double AUTO_ZOOM_DISTANCE = 2.5;

	protected TabName MY_TAB_NAME;
	protected boolean iAmOnTop = false;

	Shell savedShell;
	private final EPartService PART_SERVICE;
	@Inject
	protected FreeFlyerScenario freeFlyerScenario;

	protected ArrowsDialog m_arrowsDialog = null;

	@Inject
	protected MApplication application;

	private boolean planTraceRegistered = false;
	Composite m_viewParentComposite;
	protected KeyListener m_arrowKeyListener = null;
	protected boolean haveBeeToZoomTo = false;



	@Inject
	public LiveTelemetryView(EPartService eps, Shell shell,
			MApplication application) {
		eps.addPartListener(this);
		LiveTelemetryViewMovementRegistry.addView(this);
		PART_SERVICE = eps;

		savedShell = shell;

		// this leaves the last created one in the context ...
		IEclipseContext context = application.getContext();
		context.set(LiveTelemetryView.class, this);
	}

	@Inject @Optional
	public void acceptAgent(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent agent) {
		if(agent == null) {
			haveBeeToZoomTo = false;
		}
		else {
			haveBeeToZoomTo = true;
		}
		updateButtonsThatZoomToBee();
		zoomToBee();
	}

	protected void updateButtonsThatZoomToBee() {
		if(m_arrowsDialog == null) {
			return;
		}

		if(haveBeeToZoomTo()) {
			m_arrowsDialog.enableButtonsThatZoomToBee();
		} else {
			m_arrowsDialog.disableButtonsThatZoomToBee();
		}
	}

	protected boolean haveBeeToZoomTo() {
		return haveBeeToZoomTo;
	}

	@Override
	public IVerveScene getScene(String sceneName) {
		synchronized (LiveTelemetryView.class) {
			try {
				if (scene == null) {
					freeFlyerScenario.start();
					scene = new BasicVerveScene(freeFlyerScenario.getRoot());

					if(!planTraceRegistered) {
						scene.addScenePickListener(CreatePlanTrace.getStaticInstance());
						scene.addScenePickListener(AddViaMapPlane.getStaticInstance());
						scene.addScenePickListener(KeepoutModelingNode.getStaticInstance());
						scene.addScenePickListener(HandrailModelingNode.getStaticInstance());
						planTraceRegistered = true;
					}
					return scene;                }
			} catch (Throwable t) {
				IrgUI.errorDialog("getScene Failed",
						"Failed to locate a 3D scene to view", t);
			}
		}
		return scene;
	}

	@Inject @Optional
	public void centerOnSpatial(Spatial contents) {
		//		if(WorkbenchConstants.isFlagPresent(WorkbenchConstants.NO_ZOOM_TO_CLICK)) {
		//			return;
		//		}

		AbstractCamControl control = getCameraControl();
		if(control instanceof FollowCamControl) {
			((FollowCamControl) control).seekSelection(contents);
		}
	}

	@PostConstruct
	public void createPartControl(Composite parent, EMenuService menuService) {
		m_viewParentComposite = parent;
		super.createPartControl(parent);
		menuService.registerContextMenu(m_a3dCanvas, 
				"gov.nasa.arc.verve.freeflyer.workbench.popupmenu.0");
		if(haveBeeToZoomTo) {
			zoomToBee();
		}
	}

	// must be called from the display thread
	protected void toggleArrowsDialog(final boolean show){
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (show){
					if (m_arrowsDialog == null){
						m_arrowsDialog = new ArrowsDialog(savedShell);
					} 
					m_arrowsDialog.open();
				} else {
					if (m_arrowsDialog != null){
						m_arrowsDialog.close();
					}
				}
			}
		});
	}


	@Override
	public void widgetDisposed(DisposeEvent e) {
		super.widgetDisposed(e);
		if(m_arrowsDialog != null) {
			m_arrowsDialog.close();
		}
	}

	/**
	 * 
	 * @param lwjglCanvas
	 * @return
	 */
	@Override
	protected Runnable createOnInitRunnable(final Ardor3dCanvas lwjglCanvas) {
		final IVerveCanvasView canvasView = this;
		Runnable retVal = new Runnable() {
			@Override
			public void run() {
				Shell shell = ((Control)canvasView.getCanvas()).getShell();
				IVerveScene scene = getScene(getSceneName());

				AbstractCamControl cam;
				cam = new FirstPersonCamControl(canvasView, Vector3.UNIT_Z, true);
				m_camControls.put(CamControlType.EgoCam, cam);

				double cx = LightsCameraProperties.getPropertyAsDouble("cameraCenterX");
				double cy = LightsCameraProperties.getPropertyAsDouble("cameraCenterY");
				double cz = LightsCameraProperties.getPropertyAsDouble("cameraCenterZ");

				double lx = LightsCameraProperties.getPropertyAsDouble("cameraLocationX");
				double ly = LightsCameraProperties.getPropertyAsDouble("cameraLocationY");
				double lz = LightsCameraProperties.getPropertyAsDouble("cameraLocationZ");

				Vector3 camCenter = new Vector3(cx, cy, cz);
				Vector3 camLocation = new Vector3(lx, ly, lz);

				cam = new FollowCamControl(canvasView, Vector3.NEG_UNIT_Z, camCenter, camLocation);
				m_camControls.put(CamControlType.FollowCam, cam);
				m_camControlUis.put(cam, new FollowCamControlActions(cam, shell));
				application.getContext().set(FollowCamControl.class, (FollowCamControl) cam);

				cam = new NadirCamControl(canvasView, Vector3.UNIT_Z);
				m_camControls.put(CamControlType.NadirCam, cam);
				m_camControlUis.put(cam, new FollowCamControlActions(cam, shell));

				cam = new EarthCamControl(canvasView, Vector3.UNIT_Z);
				m_camControls.put(CamControlType.EarthCam, cam);
				m_camControlUis.put(cam, new FollowCamControlActions(cam, shell));

				// Activate camera
				selectCameraControl(m_camControlType, true);
				getCameraControl().init();

				//				m_cameraInfo = new CamControlInfoText(canvasView);
				//				m_cameraInfo.setCameraString(m_camControlType.toString());
				//				scene.getMarkupRoot().attachChild(m_cameraInfo);

				final String lastTarget = Ardor3D.getPreference(Ardor3D.P_LAST_TARGET);
				getCameraControl().setCenterOfInterest(lastTarget);

				//-- Add an interaction manager
				PhysicalLayer physicalLayer = m_a3dCanvas.getPhysicalLayer();
				LogicalLayer  forwardTo     = m_a3dCanvas.getLogicalLayer();


				if(true) {
					m_interactManager = new VerveInteractManager();
					setupInteractManager(m_a3dCanvas, physicalLayer, forwardTo);
					forwardTo = m_interactManager.getLogicalLayer();
					scene.setInteractManager(m_interactManager);
				}

				//-- attempt to restore state
				if(m_initCamTarget != null) {
					getCameraControl().setCenterOfInterest(m_initCamTarget);
				}
				if(m_initCamMode != null) {
					getCameraControl().setInterestPointMode(m_initCamMode);
				}

				//-- implementation callback, default does nothing
				onInitComplete();

				if(haveBeeToZoomTo) {
					zoomToBee();
				}
			}
		};

		return retVal;
	}

	@PostConstruct
	public void hideSceneGraphControlsDialog() {
		if(sceneGraphControlsDialog != null){
			sceneGraphControlsDialog.close();
		}
	}

	public void showSceneGraphControlsDialog() {
		sceneGraphControlsDialog = new SceneGraphControlsDialog(savedShell, freeFlyerScenario);
		sceneGraphControlsDialog.create();
		sceneGraphControlsDialog.setBlockOnOpen(false);
		sceneGraphControlsDialog.open();
	}

	protected void updateCameraCenterOfInterest() {
		AbstractRobot selectedRobot = freeFlyerScenario.getPrimaryRobot();
		if(selectedRobot == null) {
			return;
		}
		if(getCameraControl() == null) {
			Ardor3D.setPreference(Ardor3D.P_LAST_TARGET, selectedRobot.getInterestPointName());
		} else {
			getCameraControl().setCenterOfInterest(selectedRobot);
		}
	}

	public void reset() {
		getCameraControl().reset();
	}

	public void zoomToBee() {
		AbstractRobot selectedRobot = freeFlyerScenario.getPrimaryRobot();
		AbstractCamControl camControl = getCameraControl();
		if(camControl != null) {
			camControl.setCenterOfInterest(selectedRobot);
			camControl.setDistance(AUTO_ZOOM_DISTANCE);
		}
	}

	public void zoomToAbsolutePreview() {
		AbstractRobot selectedRobot = freeFlyerScenario.getPrimaryRobot();
		RobotPartDraggablePreview rpdp = (RobotPartDraggablePreview)selectedRobot.getPart(RapidFreeFlyerRobot.ABSOLUTE_DRAGGABLE_PREVIEW);
		Vector3 vec = new Vector3(0, 0, 0);
		getCameraControl().setCenterOfInterest(rpdp.getNode(), vec);
		getCameraControl().setDistance(AUTO_ZOOM_DISTANCE);
	}
	
	public void zoomToRelativePreview() {
		AbstractRobot selectedRobot = freeFlyerScenario.getPrimaryRobot();
		RobotPartDraggablePreview rpdp = (RobotPartDraggablePreview)selectedRobot.getPart(RapidFreeFlyerRobot.RELATIVE_DRAGGABLE_PREVIEW);
		Vector3 vec = new Vector3(0, 0, 0);
		getCameraControl().setCenterOfInterest(rpdp.getNode(), vec);
		getCameraControl().setDistance(AUTO_ZOOM_DISTANCE);
	}

	public void nudgeUp() {
		((FollowCamControl)getCameraControl()).swing(0, -MOVE_AMOUNT);
	}

	public void nudgeLeft() {
		((FollowCamControl)getCameraControl()).swing(MOVE_AMOUNT, 0);
	}

	public void nudgeRight() {
		((FollowCamControl)getCameraControl()).swing(-MOVE_AMOUNT, 0);
	}

	public void nudgeDown() {
		((FollowCamControl)getCameraControl()).swing(0, MOVE_AMOUNT);
	}

	public void zoomIn() {
		((FollowCamControl)getCameraControl()).dolly(-ZOOM_AMOUNT);
	}

	public void zoomOut() {
		((FollowCamControl)getCameraControl()).dolly(ZOOM_AMOUNT);
	}

	public boolean isPartContainedWithin(final MPart parent, final MElementContainer<MUIElement> element){
		if(element != null){
			if(!element.getElementId().equals(parent.getElementId()) ){
				return isPartContainedWithin(parent, element.getParent());
			}else{
				return true;
			}
		}
		return false;
	}
	public void partBroughtToTop(MPart partOnTop) {
		if(MY_TAB_NAME == null || partOnTop == null) {
			logger.error("Cannot match tab name, not bringing tab to top.");
		}

		if(MY_TAB_NAME.matches(partOnTop.getElementId())) {

			boolean non3DtabSelected = false;
			for(String damperName : MY_TAB_NAME.DAMPERS.getKeyPartName()){
				MPart possibleDamperChild = PART_SERVICE.findPart(damperName);
				if(possibleDamperChild != null && isPartContainedWithin(partOnTop, possibleDamperChild.getParent())){
					final MElementContainer<MUIElement> container = possibleDamperChild.getParent();
					final MUIElement selectedElement = container.getSelectedElement();

					String elementId = selectedElement.getElementId();
					if(elementId.equals(damperName)){
						non3DtabSelected = true;
						continue;
					}		
				}	
			}
			if(non3DtabSelected) {
				hideMe();
			}
			else {
				showMe();
			}
		} else if(MY_TAB_NAME.conflictsWith(partOnTop.getElementId())) {
			hideMe();
		}
	}

	// call when part is brought to top
	// return false if it was already on top
	protected boolean showMe() {
		if(iAmOnTop) {
			return false;
		}
		toggleArrowsDialog(true);
		iAmOnTop = true;
		return true;
	}

	// call when part is hidden
	// return false if it was already hidden
	protected boolean hideMe() {
		if(!iAmOnTop) {
			return false;
		}

		toggleArrowsDialog(false);
		iAmOnTop = false;
		return true;
	}

	@Override
	public void partActivated(MPart part) {//
	}

	@Override
	public void partDeactivated(MPart part) { //
	}

	public void partHidden(MPart part) { //

	}

	public void partVisible(MPart part) { //

	}

	protected class ArrowsDialog extends Window {

		protected Control controlPadArea;
		protected Canvas m_canvas;

		protected EnlargeableButton zoomInButton, zoomOutButton, resetViewButton, zoomToBeeButton;
		protected Image	zoomInImage, zoomOutImage;
		protected ImageData	zoomInImageData, zoomOutImageData;
		public static final String NUDGE_DISTANCE = " 5 degrees";
		protected final int m_numButtons = 4;
		protected int m_pressedButton = -1;
		protected Image m_buttonDownImage[] = new Image[m_numButtons];
		protected String m_buttonDownName[] = new String[]{"up", "left", "right", "down", };
		protected final Region m_buttonRegions[] = new Region[m_numButtons];
		protected Image m_buttonsUpImage;
		protected final String tooltips[] = new String[]{"Rotate View Up", "Rotate View Left", "Rotate View Right", "Rotate View Down"};

		protected DefaultToolTip m_tooltip;
		protected String m_lastTooltipText;

		protected final String ZOOM_IN_TOOLTIP = "Zoom In";
		protected final String ZOOM_OUT_TOOLTIP = "Zoom Out";
		protected final String RESET_VIEW_TOOLTIP = "Show Entire USOS";
		protected final String ZOOM_TO_BEE_TOOLTIP = "Put Astrobee at the Center of the Map";

		public ArrowsDialog(Shell parent) {
			super(parent);
			setShellStyle( SWT.MODELESS  & ~SWT.APPLICATION_MODAL);//  & ~SWT.PRIMARY_MODAL & ~SWT.SYSTEM_MODAL); //
			setBlockOnOpen(false);

			loadImages();
			setupControlPadRegions();
		}

		@Override
		public int open() {
			int result =  super.open();
			LiveTelemetryView.this.setFocus();
			return result;
		}

		@Override
		protected Point getInitialLocation(Point initialSize) {
			Rectangle bounds = m_viewParentComposite.getBounds();
			Point displayOrigin = m_viewParentComposite.toDisplay(bounds.width, 0);
			displayOrigin.x -= initialSize.x;

			return displayOrigin;
		}

		protected int getPressedButton(){
			return m_pressedButton;
		}

		protected void loadImages() {
			zoomInImage =  IssUiActivator.getImageFromRegistry("zoomin");
			zoomOutImage = IssUiActivator.getImageFromRegistry("zoomout");

			m_buttonsUpImage = IssUiActivator.getImageFromRegistry("controlpad_small_plain");

			for(int i=0; i< m_numButtons; i++) {
				m_buttonDownImage[i] = IssUiActivator.getImageFromRegistry("controlpad_small_" + m_buttonDownName[i]);
			}
		}

		protected void setupControlPadRegions() {
			int nearEdge = -5;
			int mid1 = 31;
			int mid1dimple = 27;
			int middle = 50;
			int mid2 = 69;
			int mid2dimple = 70;
			int farEdge = 100;

			final int ctlPadButtonPts[][] = new int[m_numButtons][];

			ctlPadButtonPts[0] = new int[]{ middle, nearEdge, mid2, mid1, middle, mid1dimple, mid1, mid1 }; // 0 = UP
			ctlPadButtonPts[1] = new int[]{ nearEdge, middle, mid1, mid1, mid1dimple, middle, mid1, mid2 }; // 1 = LEFT
			ctlPadButtonPts[2] = new int[]{ mid2, mid1, farEdge, middle, mid2, mid2, mid2dimple, middle };  // 2 = RIGHT
			ctlPadButtonPts[3] = new int[]{ mid1, mid2, middle, mid2dimple, mid2, mid2, middle, farEdge };  // 3 = DOWN

			for(int i=0; i<m_numButtons; i++) {
				m_buttonRegions[i] = new Region();
				m_buttonRegions[i].add(ctlPadButtonPts[i]);
			}
		}

		protected void drawControlPadImage(GC gc) {
			if(getPressedButton() > -1){
				gc.drawImage(m_buttonDownImage[m_pressedButton], 0, 0);
			} else {
				gc.drawImage(m_buttonsUpImage, 0, 0);
			}
		}

		/**
		 * The <code>Dialog</code> implementation of this <code>Window</code>
		 * method creates and lays out the top level composite for the dialog, and
		 * determines the appropriate horizontal and vertical dialog units based on
		 * the font size. It then calls the <code>createDialogArea</code> and
		 * <code>createButtonBar</code> methods to create the dialog area and
		 * button bar, respectively. Overriding <code>createDialogArea</code> and
		 * <code>createButtonBar</code> are recommended rather than overriding
		 * this method.
		 */
		@Override
		protected Control createContents(Composite parent) {
			// create the top level composite for the dialog
			Composite composite = new Composite(parent, 0);
			GridLayout layout = new GridLayout();
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			layout.verticalSpacing = 0;
			composite.setLayout(layout);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			// create the dialog area and button bar
			controlPadArea = createControlPadArea(composite);
			createLowerButtons(parent);
			return composite;
		}

		protected Control createLowerButtons(Composite c) {
			Composite parent = new Composite(c, 0);
			GridLayout layout = new GridLayout(2, true);
			parent.setLayout(layout);

			createZoomInButton(parent);
			createZoomOutButton(parent);
			createResetViewButton(parent);
			createZoomToBeeButton(parent);

			return parent;
		}

		protected void createZoomInButton(Composite parent) {
			zoomInButton = new EnlargeableButton(parent, SWT.None);
			zoomInButton.setImage(zoomInImage);
			zoomInButton.setToolTipText(ZOOM_IN_TOOLTIP);
			GridData ziGD = new GridData(SWT.FILL, SWT.TOP, true, false);
			zoomInButton.setLayoutData(ziGD);
			zoomInButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			zoomInButton.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					LiveTelemetryViewMovementRegistry.zoomIn();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e)  {/**/}
			});
		}

		protected void createZoomOutButton(Composite parent) {
			zoomOutButton = new EnlargeableButton(parent, SWT.None);
			zoomOutButton.setImage(zoomOutImage);
			zoomOutButton.setToolTipText(ZOOM_OUT_TOOLTIP);
			GridData zoGD = new GridData(SWT.FILL, SWT.TOP, true, false);
			zoomOutButton.setLayoutData(zoGD);
			zoomOutButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			zoomOutButton.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					LiveTelemetryViewMovementRegistry.zoomOut();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e)  {/**/}
			});
		}

		protected void createResetViewButton(Composite parent) {
			resetViewButton = new EnlargeableButton(parent, SWT.None);
			resetViewButton.setText("Reset View");
			resetViewButton.setToolTipText(RESET_VIEW_TOOLTIP);
			GridData rvGD = new GridData(SWT.FILL, SWT.TOP, true, false);
			rvGD.horizontalSpan = 2;
			resetViewButton.setLayoutData(rvGD);
			resetViewButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			resetViewButton.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					LiveTelemetryViewMovementRegistry.resetViews();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e)  {/**/}
			});
		}

		protected void createZoomToBeeButton(Composite parent) {
			zoomToBeeButton = new EnlargeableButton(parent, SWT.None);
			zoomToBeeButton.setText("Zoom to Bee");
			zoomToBeeButton.setToolTipText(ZOOM_TO_BEE_TOOLTIP);
			GridData cbGD = new GridData(SWT.FILL, SWT.TOP, true, false);
			cbGD.horizontalSpan = 2;
			zoomToBeeButton.setLayoutData(cbGD);
			zoomToBeeButton.setButtonLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			zoomToBeeButton.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					LiveTelemetryViewMovementRegistry.zoomToBee();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e)  {/**/}
			});
			if(!haveBeeToZoomTo()) {
				zoomToBeeButton.setEnabled(false);
			}
		}

		public void disableButtonsThatZoomToBee() {
			if(zoomToBeeButton != null && !zoomToBeeButton.isDisposed()) {
				zoomToBeeButton.setEnabled(false);
			}
		}

		public void enableButtonsThatZoomToBee() {
			if(zoomToBeeButton != null && !zoomToBeeButton.isDisposed()) {
				zoomToBeeButton.setEnabled(true);
			}
		}

		protected Control createControlPadArea(Composite parent) {
			final Composite control = new Composite(parent, SWT.NONE);

			GridLayout gl = new GridLayout(1, false);
			control.setLayout (gl);
			control.setBackground(ColorProvider.INSTANCE.gray_226);

			m_canvas = new Canvas(control, SWT.NONE);
			GridData gd3 = new GridData(SWT.CENTER, SWT.TOP, true, true);
			gd3.minimumWidth = 100;
			gd3.widthHint = 100;
			gd3.minimumHeight = 100;
			gd3.heightHint = 100;
			//gd3.verticalIndent = 5;
			m_canvas.setLayoutData(gd3);
			m_canvas.setBackground(ColorProvider.INSTANCE.grayBackground);
			m_canvas.setSize(100, 100);
			m_canvas.addPaintListener(new PaintListener() {
				public void paintControl(PaintEvent e)
				{
					GC gc = e.gc;
					drawControlPadImage(gc);
				}
			});

			Listener downListener = new Listener() {
				public void handleEvent (Event e) {	
					for(int i=0; i<m_numButtons; i++) {
						if(m_buttonRegions[i].contains(e.x, e.y)) {
							m_pressedButton = i;
							switch(i) {
							case 0:
								LiveTelemetryViewMovementRegistry.nudgeViewUp();
								break;
							case 1:
								LiveTelemetryViewMovementRegistry.nudgeViewLeft();
								break;
							case 2:
								LiveTelemetryViewMovementRegistry.nudgeViewRight();
								break;
							case 3:
								LiveTelemetryViewMovementRegistry.nudgeViewDown();
								break;
							}
							m_canvas.redraw();
						}
					}
				}
			};

			Listener upListener = new Listener () {
				public void handleEvent (Event e) {	
					m_pressedButton = -1;
					m_canvas.redraw();
				}
			};

			m_canvas.addListener(SWT.MouseUp, upListener);
			m_canvas.addListener(SWT.MouseDown, downListener);
			m_canvas.addKeyListener(getArrowKeyListener());

			//TODO this stuff makes the main menus disappear, which is WEIRD
			m_canvas.addListener(SWT.MouseEnter, new Listener() {
				public void handleEvent(Event e) {
					m_canvas.setFocus();
				}
			});

			m_canvas.addListener(SWT.MouseExit, new Listener() {
				public void handleEvent(Event e) {
					LiveTelemetryView.this.setFocus();
				}
			});

			m_tooltip = new DefaultToolTip(m_canvas, ToolTip.RECREATE, false)
			{
				@Override
				protected boolean shouldCreateToolTip(Event event) {
					for(int i=0; i<m_numButtons; i++) {
						if(m_buttonRegions[i].contains(event.x, event.y)) {
							String text = tooltips[i];
							if (!(text.equals (m_lastTooltipText))) {
								m_tooltip.setText(text);
								m_lastTooltipText = text;
								return true;
							}
						}
					}
					m_lastTooltipText = null;
					hide();
					return false;
				}
			}; 
			return control;
		}

		public KeyListener getArrowKeyListener() {
			if (m_arrowKeyListener == null){
				m_arrowKeyListener = new ArrowKeyListener();
			}
			return m_arrowKeyListener;
		}

		protected class ArrowKeyListener implements KeyListener {

			@Override
			public void keyPressed(KeyEvent e) {
				// noop
			}

			/* 
			 * \0 is right
			 * \8 is down
			 * \9 is left
			 * \7 is up
			 * (non-Javadoc)
			 * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
			 */
			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.keyCode){
				case SWT.ARROW_DOWN:
					LiveTelemetryViewMovementRegistry.nudgeViewDown();
					break;
				case SWT.ARROW_LEFT:
					LiveTelemetryViewMovementRegistry.nudgeViewLeft();
					break;
				case SWT.ARROW_RIGHT:
					LiveTelemetryViewMovementRegistry.nudgeViewRight();
					break;
				case SWT.ARROW_UP:
					LiveTelemetryViewMovementRegistry.nudgeViewUp();
					break;
				case SWT.PAGE_UP:
					LiveTelemetryViewMovementRegistry.zoomIn();
					break;
				case SWT.PAGE_DOWN:
					LiveTelemetryViewMovementRegistry.zoomOut();
					break;
				default:
					if(e.character == 'r') {
						LiveTelemetryViewMovementRegistry.resetViews();
					} else if(e.character == 'z') {
						LiveTelemetryViewMovementRegistry.zoomToBee();
					} else if(e.character == 'a') {
						LiveTelemetryViewMovementRegistry.zoomToAbsolutePreview();
					} else if(e.character == 'l') {
						LiveTelemetryViewMovementRegistry.zoomToRelativePreview();
					}
					else if(e.character == 's') {

					}
				}
			}
		}
	}
}
