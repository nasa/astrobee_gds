package gov.nasa.arc.verve.rcp.e4.canvas;

import gov.nasa.arc.irg.freeflyer.utils.VerveConstants;
import gov.nasa.arc.irg.util.ui.IrgUI;
import gov.nasa.arc.verve.ardor3d.e4.Ardor3D;
import gov.nasa.arc.verve.ardor3d.e4.VerveArdor3dPreferences;
import gov.nasa.arc.verve.ardor3d.e4.framework.IVerveCanvasListener;
import gov.nasa.arc.verve.ardor3d.e4.framework.IVerveCanvasView;
import gov.nasa.arc.verve.ardor3d.e4.input.control.AbstractCamControl;
import gov.nasa.arc.verve.ardor3d.e4.input.control.CamControlInfoText;
import gov.nasa.arc.verve.ardor3d.e4.input.control.CamControlType;
import gov.nasa.arc.verve.ardor3d.e4.input.control.EarthCamControl;
import gov.nasa.arc.verve.ardor3d.e4.input.control.FirstPersonCamControl;
import gov.nasa.arc.verve.ardor3d.e4.input.control.FollowCamControl;
import gov.nasa.arc.verve.ardor3d.e4.input.control.NadirCamControl;
import gov.nasa.arc.verve.common.IVerveScene;
import gov.nasa.arc.verve.common.PickInfo;
import gov.nasa.arc.verve.common.ScenePickListener;
import gov.nasa.arc.verve.common.ardor3d.framework.VerveInteractUpdater;
import gov.nasa.arc.verve.common.ardor3d.interact.VerveCompoundInteractWidget;
import gov.nasa.arc.verve.common.ardor3d.interact.VerveInteractManager;
import gov.nasa.arc.verve.common.ardor3d.interact.VerveInteractWidgets;
import gov.nasa.arc.verve.common.interest.InterestPointProvider;
import gov.nasa.arc.verve.rcp.e4.canvas.control.FollowCamControlActions;
import gov.nasa.arc.verve.rcp.e4.canvas.control.ICamControlUiEclipsePlugin;
import gov.nasa.arc.verve.ui3d.hud.VerveHud;
import gov.nasa.arc.verve.ui3d.hud.VerveHudRegistry;
import gov.nasa.util.ui.LastPath;
import gov.nasa.util.ui.TextInputDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
//import org.eclipse.ui.IActionBars;
//import org.eclipse.ui.IMemento;
//import org.eclipse.ui.IViewSite;
//import org.eclipse.ui.PartInitException;
//import org.eclipse.ui.PlatformUI;
import org.lwjgl.LWJGLException;

import com.ardor3d.framework.Canvas;
import com.ardor3d.framework.CanvasRenderer;
import com.ardor3d.framework.lwjgl.LwjglCanvasCallback;
import com.ardor3d.framework.lwjgl.LwjglCanvasRenderer;
import com.ardor3d.input.PhysicalLayer;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.StereoCamera;
import com.ardor3d.renderer.TextureRendererProvider;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.renderer.state.WireframeState;

public abstract class AbstractArdor3dView implements IVerveCanvasView, ScenePickListener, DisposeListener {
	private static Logger logger = Logger.getLogger(AbstractArdor3dView.class);
	public static final String ID = AbstractArdor3dView.class.getName();

	protected final String CAMERA = "Camera";
	protected final String TOOLS  = "Tools";

	final Map<String,String>        m_options = new HashMap<String,String>();

	protected Ardor3dCanvas         m_a3dCanvas = null;
	protected static Ardor3dCanvas  s_shareCanvas = null;

	protected final Map<CamControlType,AbstractCamControl> m_camControls = 
			new HashMap<CamControlType,AbstractCamControl>();
	protected final Map<AbstractCamControl,ICamControlUiEclipsePlugin> m_camControlUis = 
			new HashMap<AbstractCamControl,ICamControlUiEclipsePlugin>();
	protected CamControlType        m_camControlType = CamControlType.FollowCam;
	protected CamControlType        m_lastCamControl = CamControlType.NadirCam;

	protected String                m_initCamTarget;
	protected String                m_initCamMode;

	protected CamControlInfoText    m_cameraInfo;

	protected VerveHud              m_hud = null;
	protected String                m_sceneName = null;
	
    public VerveInteractManager            m_interactManager;
    VerveInteractUpdater            m_interactUpdater;

	protected static File s_screenShotDir = new File(LastPath.get(AbstractArdor3dView.class));
	protected static TextureRendererProvider s_textureRenderProvider = null;

	private final ArrayList<IVerveCanvasListener> m_canvasListeners = new ArrayList<IVerveCanvasListener>();

	/**
	 * get a scene, keyed on sceneName parameter.
	 * passing null or empty string should return default scene
	 * @return
	 */
	public abstract IVerveScene getScene(String sceneName);

	@Override
	public IVerveScene getScene() {
		return getScene(getSceneName());
	}

	/**
	 * get a scene name key - this should be encoded in the secondary id,
	 * extracted here and returned. 
	 * @return
	 */
	public String getSceneName() {
		return m_sceneName;
	}

	@Override
	public Canvas getCanvas() {
		return m_a3dCanvas;
	}

	public int getCanvasWidth() {
		return m_a3dCanvas.getSize().x;
	}

	public int getCanvasHeight() {
		return m_a3dCanvas.getSize().y;
	}

    public void createPartControl(Composite parent) {
    	// is this all just here for show ? DW 9/10/14
        IVerveScene scene = getScene(getSceneName());
        LwjglCanvasRenderer canvasRenderer = new LwjglCanvasRenderer(scene);
        
        // Copied from AbstractArdor3dViewLwjgl.java
        canvasRenderer.setCanvasCallback(new LwjglCanvasCallback() {
            @Override
            public void makeCurrent() throws LWJGLException {
                //Display.makeCurrent();
            }
            
			@Override
            public void releaseContext() throws LWJGLException {
                //Display.releaseContext();
            }
        });
        
        createPartControl(parent, scene, canvasRenderer);
    }

    public void createPartControl(Composite parent, IVerveScene scene, CanvasRenderer canvasRenderer) {
        synchronized(AbstractArdor3dView.class) {		    
            // what does this line do anyway?
        	// ViewID.getOptionMap(this, m_options);
            CamControlType cct = CamControlType.fromString(m_options.get("cam"));
            if(cct != null) 
                m_camControlType = cct;
            m_sceneName = m_options.get("scene");

            final GLData glData = new GLData();
            glData.depthSize = 8;
            glData.samples   = VerveArdor3dPreferences.getAntialiasingSamples();
            glData.doubleBuffer = true;
            glData.shareContext = s_shareCanvas;

            m_a3dCanvas = new Ardor3dCanvas(canvasRenderer, parent, SWT.NONE, glData);
            Runnable onInit = createOnInitRunnable(m_a3dCanvas);
            m_a3dCanvas.setOnInitRunnable(onInit);
            
            Ardor3D.getFrameHandler().addCanvas(m_a3dCanvas);
            Ardor3D.getLogicalLayerUpdater().registerLogicalLayer(m_a3dCanvas.getLogicalLayer());

            scene.addScenePickListener(this);

            if(s_shareCanvas == null) {
                s_shareCanvas = m_a3dCanvas;
            }

            m_a3dCanvas.addDisposeListener(this);
            m_a3dCanvas.addControlListener(createResizeListener(m_a3dCanvas));
            
        }
    }
    
    /**
     * 
     * @param lwjglCanvas
     * @return
     */
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

                cam = new FollowCamControl(canvasView, Vector3.UNIT_Z);
                m_camControls.put(CamControlType.FollowCam, cam);
                m_camControlUis.put(cam, new FollowCamControlActions(cam, shell));

                cam = new NadirCamControl(canvasView, Vector3.UNIT_Z);
                m_camControls.put(CamControlType.NadirCam, cam);
                m_camControlUis.put(cam, new FollowCamControlActions(cam, shell));

                cam = new EarthCamControl(canvasView, Vector3.UNIT_Z);
                m_camControls.put(CamControlType.EarthCam, cam);
                m_camControlUis.put(cam, new FollowCamControlActions(cam, shell));

                // kill for now - DW 9/9/14
//                IActionBars actionBars = getViewSite().getActionBars();
//
//                actionBars.getMenuManager().add(new Separator(TOOLS));
//                actionBars.getMenuManager().add(new Separator(CAMERA));
//                actionBars.getToolBarManager().add(new Separator(TOOLS));
//                actionBars.getToolBarManager().add(new Separator(CAMERA));
//
//                // Add measure tool
//                Action measureAction = new Action("Measure", Action.AS_CHECK_BOX) {
//                    @Override
//                    public void runWithEvent(Event e) {
//                        getScene(getSceneName()).setMeasureMode(isChecked());
//                    }
//                };
//                measureAction.setToolTipText("Toggle measure mode\n(double-click two points in scene to measure distance)");
//                measureAction.setImageDescriptor(VerveArdor3dActivator.getImageDescriptorFromRegistry("ruler"));
//                actionBars.getToolBarManager().prependToGroup(TOOLS, new ActionContributionItem(measureAction));
//
//                // Add camera select
//                Action camSelectAction = new ActionCombo("camera", "Select Camera Type.\n - Press to toggle to last camera.\n - Use dropdown to select new camera type.") {
//                    @Override 
//                    public void runWithEvent(Event e) {
//                        //logger.debug("toggle cam control, switching to: "+m_lastCamControl);
//                        selectCameraControl(m_lastCamControl);
//                        //showMenu(e);
//                    }
//                };
//                camSelectAction.setImageDescriptor(VerveArdor3dActivator.getImageDescriptorFromRegistry("camera"));
//                camSelectAction.setMenuCreator(new CameraSelectMenuCreator(camSelectAction, lwjglCanvas.getShell()));
//                camSelectAction.setAccelerator(SWT.CTRL + 'C');
//                actionBars.getToolBarManager().prependToGroup(CAMERA, new ActionContributionItem(camSelectAction));

                // Activate camera
                selectCameraControl(m_camControlType, true);
                getCameraControl().init();

                m_cameraInfo = new CamControlInfoText(canvasView);
                m_cameraInfo.setCameraString(m_camControlType.toString());
                scene.getMarkupRoot().attachChild(m_cameraInfo);

                final String lastTarget = Ardor3D.getPreference(Ardor3D.P_LAST_TARGET);
                getCameraControl().setCenterOfInterest(lastTarget);

//                actionBars.getMenuManager().appendToGroup(TOOLS, createCameraResetAction());
//                actionBars.getMenuManager().appendToGroup(TOOLS, new Separator());
//                actionBars.getMenuManager().appendToGroup(TOOLS, createToggleWireframeStateAction());
//                actionBars.getMenuManager().appendToGroup(TOOLS, createToggleShowNormalsAction());
//                actionBars.getMenuManager().appendToGroup(TOOLS, new Separator());
//                actionBars.getMenuManager().appendToGroup(TOOLS, createScreenShotAction());
//                actionBars.getMenuManager().appendToGroup(TOOLS, createScreenShotDirAction());
//                actionBars.getMenuManager().appendToGroup(TOOLS, createMultiScreenShotAction());
//                actionBars.getMenuManager().appendToGroup(TOOLS, new Separator());
//                actionBars.getMenuManager().appendToGroup(TOOLS, createChangeBackgroundColorAction());
//                actionBars.getMenuManager().appendToGroup(TOOLS, new Separator());
//                actionBars.getMenuManager().appendToGroup(TOOLS, createHudMenu());
//
//                actionBars.updateActionBars();


                PhysicalLayer physicalLayer = m_a3dCanvas.getPhysicalLayer();
                LogicalLayer  forwardTo     = m_a3dCanvas.getLogicalLayer();

                //-- Add an interaction manager
                if(true) {
                    m_interactManager = new VerveInteractManager();
                    setupInteractManager(m_a3dCanvas, physicalLayer, forwardTo);
                    forwardTo = m_interactManager.getLogicalLayer();
                    scene.setInteractManager(m_interactManager);
                }
                
                
                //-- Add a HUD, if necessary
//                try {
//                    final String hudId = m_options.get("hud");
//                    m_hud = VerveHudFactory.createHud(hudId, AbstractArdor3dView.this);
//                    if(m_hud != null) {
//                        logger.debug(String.format("adding HUD \"%s\"", hudId));
//                        scene.getUiRoot().attachChild(m_hud);
//                    }
//                } 
//                catch (IOException e1) {
//                    logger.error("Error creating HUD", e1);
//                }

                //-- attempt to restore state
                if(m_initCamTarget != null) {
                    getCameraControl().setCenterOfInterest(m_initCamTarget);
                }
                if(m_initCamMode != null) {
                    getCameraControl().setInterestPointMode(m_initCamMode);
                }
                
                //-- implementation callback, default does nothing
                onInitComplete();

                //                //-- adjust z for height map
                //                AbstractCamControl cam = m_camControls.get(m_camControlType);
                //                Vector3 ctr = new Vector3(cam.getCenter());
                //                Vector3 loc = new Vector3(cam.getLocation());
                //                float z = VerveBaseMap.getHeightAt(ctr.getXf(), ctr.getYf());
                //                float off = z - ctr.getZf();
                //                //cam.setLocationAndCenter(loc.addLocal(0,0,off), ctr.addLocal(0,0,off));
                //                cam.setLocationAndCenter(loc.addLocal(0,0,10), ctr.addLocal(0,0,10));
            }
        };
        return retVal;
    }

    /**
     * 
     * @param canvas
     * @param physicalLayer
     * @param forwardTo
     */
    protected void setupInteractManager(Ardor3dCanvas canvas, PhysicalLayer physicalLayer, LogicalLayer forwardTo) {
        m_interactManager.setupInput(canvas, physicalLayer, forwardTo);

        VerveCompoundInteractWidget widget;
        m_interactManager.addWidget(widget = VerveInteractWidgets.newMultiPlanarWidget());
        m_interactManager.addWidget(widget = VerveInteractWidgets.newMoveXYZRotXYZWidget());
        m_interactManager.addWidget(widget = VerveInteractWidgets.newRotXYZWidget());
        m_interactManager.addWidget(widget = VerveInteractWidgets.newWaypointWidget());
        m_interactManager.addWidget(widget = VerveInteractWidgets.newMoveXYZWidget());
        m_interactManager.addWidget(widget = VerveInteractWidgets.newMoveXYZRotXYZWidget());
        m_interactManager.addWidget(widget = VerveInteractWidgets.newMoveXYTerrainFollowWidget());
        m_interactManager.addWidget(widget = VerveInteractWidgets.newMoveXYZTerrainFollowWidget());
        m_interactManager.addWidget(widget = VerveInteractWidgets.newMoveXYRotZTerrainFollowWidget());
        m_interactManager.setActiveWidget(widget);

        // replace delegated logical layer
        Ardor3D.getLogicalLayerUpdater().removeLogicalLayer(forwardTo);
        Ardor3D.getLogicalLayerUpdater().registerLogicalLayer(m_interactManager.getLogicalLayer());

        m_interactUpdater = new VerveInteractUpdater(m_interactManager);
        Ardor3D.getFrameHandler().addUpdater(m_interactUpdater);
    }
    
    protected void onInitComplete() {
        // empty
    }

    private Action createScreenShotAction() {
        Action retVal = new Action("Take Screen Shot", Action.AS_PUSH_BUTTON) {
            @Override
            public void runWithEvent(Event e) {
                getScene(getSceneName()).setScreenShot(1, s_screenShotDir);
            }
        };
        return retVal;
    }

    private Action createMultiScreenShotAction() {
        Action retVal = new Action("Take Screen Shot Sequence...", Action.AS_PUSH_BUTTON) {
            int defaultCount = 900;
            @Override
            public void runWithEvent(Event e) {
                TextInputDialog dialog = new TextInputDialog("Screen Shot Count",
                                                             "About to capture a sequence of screenshots to directory \n"+
                                                                     "    "+s_screenShotDir.toString()+"\n\n\n"+
                                                                     "Please enter number of Screen Shots to capture: ", ""+defaultCount);
                dialog.dialogWidth = 250;
                if(dialog.open() == TextInputDialog.OK) {
                    String countString = dialog.getValue();
                    try {
                        defaultCount = Integer.valueOf(countString);
                        logger.debug("About to capture "+defaultCount+" screens to "+s_screenShotDir.getAbsolutePath());
                        getScene(getSceneName()).setScreenShot(defaultCount, s_screenShotDir);
                    }
                    catch(Throwable t) {
                        IrgUI.warnDialog("Invalid Value", 
                                         "Please enter a valid integer value.\n"+
                                                 "Value entered was \""+countString+"\"");
                    }
                }
                else {
                    //logger.debug("Multi Screen Shot Dialog Cancelled");
                }
            }
        };
        return retVal;
    }

//    private Action createScreenShotDirAction() {
//        Action retVal = new Action("Choose Screen Shot Directory...", Action.AS_PUSH_BUTTON) {
//            @Override
//            public void runWithEvent(Event e) {
//                DirectoryDialog dialog = new DirectoryDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OPEN | SWT.SINGLE);
//                dialog.setFilterPath(LastPath.get(AbstractArdor3dView.class));
//                dialog.setText("Choose VERVE Screen Shot Directory");
//                if (dialog.open() != null) {
//                    final String filePath = dialog.getFilterPath();
//                    LastPath.set(AbstractArdor3dView.class, filePath);
//                    s_screenShotDir = new File(filePath);
//                }
//            }
//        };
//        return retVal;
//    }


    private Action createToggleWireframeStateAction() {
        Action retVal = new Action("Toggle &Wireframe", Action.AS_PUSH_BUTTON) {
            @Override
            public void runWithEvent(Event e) {
                WireframeState ws = (WireframeState)getScene().getRoot().getLocalRenderState(StateType.Wireframe);
                ws.setEnabled(!ws.isEnabled());
            }
        };
        return retVal;
    }

    private Action createToggleShowNormalsAction() {
        Action retVal = new Action("Toggle &Normals", Action.AS_PUSH_BUTTON) {
            @Override
            public void runWithEvent(Event e) {
                getScene().toggleShowNormals();
            }
        };
        return retVal;
    }

    protected final Action createCameraResetAction() {
        Action retVal = new Action("&Reset Camera", Action.AS_PUSH_BUTTON) {
            @Override
            public void runWithEvent(Event e) {
                getCameraControl().reset();
            }
        };
        return retVal;
    }

//    private ActionCombo createHudMenu() {
//        ActionCombo retVal = new ActionCombo("Select HUD", "Select HUD (Heads Up Display)");
//        retVal.setMenuCreator(new HudMenuCreator(retVal, getViewSite().getShell()) );
//        return retVal;
//    }

    //=====================================================
    class HudMenuCreator implements IMenuCreator {
        final Action m_action;
        final Menu   m_menu;
        HudMenuCreator(Action action, Shell shell) {
            m_action = action;
            m_menu = new Menu(shell, SWT.POP_UP);
        }
        @Override
        public void dispose() {
            MenuItem[] menuItems = m_menu.getItems();
            for (int i = 0; i < menuItems.length; i++) {
                menuItems[i].dispose();
            }
        }
        public Menu getMenu() { 
            return m_menu; 
        }
        @Override
        public Menu getMenu(Control parent) {
            return m_menu;
        }
        @Override
        public Menu getMenu(Menu parent) {
            return m_menu;
        }

        public void fillMenu(final InterestPointProvider ipp) {
            MenuItem[] menuItems = m_menu.getItems();
            for (int i = 0; i < menuItems.length; i++) {
                menuItems[i].dispose();
            }
//            List<String> hudIds = VerveHudFactory.getAvailableHudIds(null);
            MenuItem item = new MenuItem(m_menu, SWT.RADIO);
            item.setText("No HUD");
            item.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    selectHud("No HUD");
                }
            } );
            new MenuItem(m_menu, SWT.SEPARATOR);
//            for(final String hudId : hudIds) {
//                item = new MenuItem(m_menu, SWT.RADIO);
//                item.setText(hudId);
//                item.addListener(SWT.Selection, new Listener() {
//                    @Override
//                    public void handleEvent(Event event) {
//                        selectHud(hudId);
//                    }
//                } );
//            }
        }
    }

    private void selectHud(String hudId) {
        logger.debug("HUD = "+hudId);
    }


    @Focus
    public void setFocus() {
        m_a3dCanvas.setFocus();
        // make sure this camera control is at top of list
        AbstractCamControl acc = m_camControls.get(m_camControlType);
        if(acc != null) {
            Ardor3D.getCameraControlUpdater().registerCameraControl(acc);
        }
    }

    public boolean hasFocus() {
        return m_a3dCanvas.isFocusControl();
    }

    @Override
    public AbstractCamControl getCameraControl() {
        return m_camControls.get(m_camControlType);
    }

    /**
     * wrap call in Runnable to Display to ensure thread safety
     */
    @Override
    public void selectCameraControl(final CamControlType cameraType) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                selectCameraControl(cameraType, false);
            }
        });
    }

    public Map<CamControlType,AbstractCamControl> getCameraControls() {
        return m_camControls;
    }

    /**
     * 
     * @param cameraType
     * @return
     */
    public synchronized boolean selectCameraControl(CamControlType cameraType, boolean init) {
        AbstractCamControl oldCControl = getCameraControl();
        if(init) {
            oldCControl = null;
        }
        AbstractCamControl newCControl = m_camControls.get(cameraType);
        //logger.debug("new cameraType="+cameraType+", m_camControlType="+m_camControlType+" change="+(newCControl != oldCControl));
        //logger.debug("    newCControl="+newCControl);
        //logger.debug("    oldCControl="+oldCControl);
        ActionContributionItem[] actionItems = null;
        ICamControlUiEclipsePlugin ccui;
        if( newCControl != null && newCControl != oldCControl ) {
            if(oldCControl != null) 
                m_lastCamControl = m_camControlType;
//            IActionBars actionBars = getViewSite().getActionBars();
            if(oldCControl != null) {
                oldCControl.deregisterFrom(getLogicalLayer());
                actionItems = new ActionContributionItem[0];
                if( (ccui = m_camControlUis.get(oldCControl)) != null) {
                    actionItems = ccui.getActionItems();
                }
//                for(ActionContributionItem actionItem : actionItems) {
//                    //actionBars.getMenuManager().remove(actionItem);
//                    actionBars.getToolBarManager().remove(actionItem);
//                }
            }
            m_camControlType = cameraType;
            newCControl.registerWith(getLogicalLayer());
            actionItems = new ActionContributionItem[0];
            if( (ccui = m_camControlUis.get(newCControl)) != null) {
                actionItems = ccui.getActionItems();
            }
//            for(ActionContributionItem actionItem : actionItems) {
//                //actionBars.getMenuManager().appendToGroup(CAMERA, actionItem);
//                actionBars.getToolBarManager().appendToGroup(CAMERA, actionItem);
//            }
//            actionBars.updateActionBars();
            newCControl.reinit(oldCControl);

            //logger.debug("new cameraType="+cameraType+", m_camControlType="+m_camControlType);

            if(m_cameraInfo != null) {
                m_cameraInfo.setCameraString(m_camControlType.toString());
            }
            return true;
        }
        return false;
    }

    //=====================================================
    protected class CameraSelectMenuCreator implements IMenuCreator {
        final Action m_action;
        final Menu   m_menu;

        public CameraSelectMenuCreator(Action action, Shell shell) {
            m_action = action;
            m_menu = new Menu(shell, SWT.POP_UP);
            //fillMenu();
        }
        @Override
        public void dispose() {
            MenuItem[] menuItems = m_menu.getItems();
            for (int i = 0; i < menuItems.length; i++) {
                menuItems[i].dispose();
            }
        }

        public Menu getMenu() { return fillMenu(); }
        @Override
        public Menu getMenu(Control parent) { return fillMenu(); }
        @Override
        public Menu getMenu(Menu parent) { return fillMenu(); }

        private Menu fillMenu() {
            MenuItem[] menuItems = m_menu.getItems();
            for (int i = 0; i < menuItems.length; i++) {
                menuItems[i].dispose();
            }
            ArrayList<CamControlType> ccList = new ArrayList<CamControlType>(m_camControls.keySet());
            Collections.sort(ccList);
            for(final CamControlType cct : ccList) {
                MenuItem item = new MenuItem(m_menu, SWT.RADIO);
                item.setText(cct.description);
                item.setSelection(cct.equals(m_camControlType));
                item.addListener(SWT.Selection, new Listener() {
                    @Override
                    public void handleEvent(Event event) {
                        selectCameraControl(cct);
                    }
                } );
            }
            return m_menu;
        }
    }

    /**
     * 
     */
    @Override
    public void processPick(PickInfo pickInfo) {
    	if(VerveConstants.isFlagPresent(VerveConstants.ZOOM_TO_CLICK)) {
    		if(getCameraControl() != null) {
    			getCameraControl().setCenterOfInterest(pickInfo.getSpatial(), pickInfo.getSpatialOffset());
    		}
    	}
    }

    @PreDestroy
    public void dispose() {
//        try {
            getScene().getMarkupRoot().detachChild(m_cameraInfo);
            if(m_hud != null) {
                getScene().getMarkupRoot().detachChild(m_hud);
                VerveHudRegistry.remove(m_hud);
            }
            //logger.debug("dispose");
//        }
//        finally {
            // TODO We don't have a super any more ... was this important?? DW 9/9/14
//            super.dispose();
//        }
    }

    @Override
    public void widgetDisposed(DisposeEvent e) {
        if(e.widget == s_shareCanvas) {
            //logger.debug("The primary OpenGL context has been destroyed.");
            s_shareCanvas = null;
        }
    }

    public static Ardor3dCanvas getShareCanvas() {
        return s_shareCanvas;
    }

    public PhysicalLayer getPhysicalLayer() {
        return m_a3dCanvas.getPhysicalLayer();
    }

    public LogicalLayer getLogicalLayer() {
        return m_a3dCanvas.getLogicalLayer();
    }

    public void addListener(IVerveCanvasListener listener) {
        if(!m_canvasListeners.contains(listener)) {
            m_canvasListeners.add(listener);
        }
    }

    public void removeListener(IVerveCanvasListener listener) {
        m_canvasListeners.remove(listener);
    }

    /**
     * Create a ControlListener to do a proper resize of the GL canvas
     */
    private ControlListener createResizeListener(final Ardor3dCanvas canvas) {
        return new ControlListener() {      
            @Override
            public void controlMoved(ControlEvent e) { //
            }
            @Override
            public void controlResized(final ControlEvent event) {
                final Rectangle size = canvas.getClientArea();
                if ((size.width == 0) && (size.height == 0)) {
                    return;
                }
                final Camera camera = canvas.getCanvasRenderer().getCamera();
                if (camera != null) {
                    final float aspect = (float) size.width / (float) size.height;
                    //logger.debug("Canvas resized to "+size.width+", "+size.height);
                    final double fovY = camera.getFovY(); 
                    final double near = camera.getFrustumNear();
                    final double far  = camera.getFrustumFar();
                    //logger.debug(size.toString());
                    camera.resize(size.width, size.height);
                    //logger.debug(String.format("camera size = %d,%d", camera.getWidth(), camera.getHeight()) );
                    if(camera instanceof StereoCamera) {
                        StereoCamera stereoCamera = (StereoCamera)camera;
                        stereoCamera.setupLeftRightCameras();
                    }
                    else {
                        camera.setFrustumPerspective(fovY, aspect, near, far);
                    }

                    // notify listeners of new canvas size
                    for(IVerveCanvasListener listener : m_canvasListeners) {
                        listener.canvasResized(size.width, size.height);
                    }
                }
            }
        };
    }


    //-- State Persistence --------------------------------------------------------------
    public static final String SAVE_CAMCONTROL      = "camControl";
    public static final String SAVE_LASTCAMCONTROL  = "lastCamControl";
    public static final String SAVE_CAMTARGET       = "camTarget";
    public static final String SAVE_CAMMODE         = "camMode";

    // TODO State persistence is going to change - not sure how to do it yet DW 9/9/14
//    @Override
//    public void saveState(IMemento memento) {
//        super.saveState(memento);
//        memento.putString(SAVE_CAMCONTROL,     m_camControlType.toString());
//        memento.putString(SAVE_LASTCAMCONTROL, m_lastCamControl.toString());
//        try {
//            AbstractCamControl acc = m_camControls.get(m_camControlType);
//            memento.putString(SAVE_CAMMODE, acc.getInterestPointMode());
//            memento.putString(SAVE_CAMTARGET,  acc.getIp().provider.getInterestPointName());
//        }
//        catch(Throwable t) {
//            // ignore
//        }
//    }
//
//    @Override
//    public void init(IViewSite site, IMemento memento) throws PartInitException {
//        super.init(site, memento);
//        if (memento != null){
//            String camControl;
//            camControl = memento.getString(SAVE_CAMCONTROL);
//            try { m_camControlType = CamControlType.valueOf(camControl); }
//            catch(Throwable t) { /* ignore */ }
//            camControl = memento.getString(SAVE_LASTCAMCONTROL);
//            try { m_lastCamControl = CamControlType.valueOf(camControl); }
//            catch(Throwable t) { /* ignore */ }
//            m_initCamTarget  = memento.getString(SAVE_CAMTARGET);
//            m_initCamMode    = memento.getString(SAVE_CAMMODE);
//        }
//    }


}
