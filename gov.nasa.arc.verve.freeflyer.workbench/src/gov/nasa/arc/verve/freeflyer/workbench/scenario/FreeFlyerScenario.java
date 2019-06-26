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
package gov.nasa.arc.verve.freeflyer.workbench.scenario;

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.state.AggregateAstrobeeState;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateListener;
import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateManager;
import gov.nasa.arc.irg.plan.model.modulebay.LocationGenerator;
import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.arc.irg.util.ui.IrgUI;
import gov.nasa.arc.verve.ardor3d.e4.Ardor3D;
import gov.nasa.arc.verve.ardor3d.e4.Ardor3DEclipseNotPlugin;
import gov.nasa.arc.verve.ardor3d.e4.framework.IVerveScenario;
import gov.nasa.arc.verve.ardor3d.e4.framework.VerveScenarioStarted;
import gov.nasa.arc.verve.freeflyer.workbench.helpers.ScenarioToggleListener;
import gov.nasa.arc.verve.freeflyer.workbench.parts.handraileditor.HandrailModelingNode;
import gov.nasa.arc.verve.freeflyer.workbench.parts.keepouteditor.KeepoutModelingNode;
import gov.nasa.arc.verve.freeflyer.workbench.plantrace.AbstractGuestSciencePlanTrace;
import gov.nasa.arc.verve.freeflyer.workbench.plantrace.CreatePlanTrace;
import gov.nasa.arc.verve.freeflyer.workbench.plantrace.GuestSciencePlanTraceOne;
import gov.nasa.arc.verve.freeflyer.workbench.plantrace.GuestSciencePlanTraceThree;
import gov.nasa.arc.verve.freeflyer.workbench.plantrace.GuestSciencePlanTraceTwo;
import gov.nasa.arc.verve.freeflyer.workbench.plantrace.RunPlanTrace;
import gov.nasa.arc.verve.freeflyer.workbench.utils.AgentsFromCommandLine;
import gov.nasa.arc.verve.rcp.e4.scenario.ScenarioUpdater;
import gov.nasa.arc.verve.robot.AbstractRobot;
import gov.nasa.arc.verve.robot.exception.TelemetryException;
import gov.nasa.arc.verve.robot.freeflyer.FreeFlyerAvatarBuilder;
import gov.nasa.arc.verve.robot.freeflyer.RapidFreeFlyerRobot;
import gov.nasa.arc.verve.robot.freeflyer.SmartDockModel;
import gov.nasa.arc.verve.robot.freeflyer.parts.RobotPartDraggablePreview;
import gov.nasa.arc.verve.robot.freeflyer.scenery.CompositeIssModel;
import gov.nasa.arc.verve.robot.freeflyer.scenery.GraniteLab;
import gov.nasa.arc.verve.robot.rapid.RapidRobot;
import gov.nasa.arc.verve.ui.e4.view.SceneGraphTreeViewPart;
import gov.nasa.arc.viz.io.ImportExportManager;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.agent.ActiveAgentSet;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.agent.IActiveAgentSetListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;

import com.ardor3d.light.DirectionalLight;
import com.ardor3d.light.PointLight;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.renderer.state.CullState.Face;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.MaterialState.ColorMaterial;
import com.ardor3d.renderer.state.RenderState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.event.DirtyEventListener;
import com.ardor3d.scenegraph.event.DirtyType;
import com.ardor3d.scenegraph.hint.CullHint;

public class FreeFlyerScenario implements IVerveScenario, IActiveAgentSetListener, AstrobeeStateListener{

	private static Logger logger = Logger.getLogger(FreeFlyerScenario.class);

	protected Node 	  root      = null; 
	protected Node    kmlRoot	= null;
	protected boolean started   = false;
	protected RapidFreeFlyerRobot primaryRobot = null;// PRIMARY_BEE, selection on single-robot tabs

	protected Vector<RapidFreeFlyerRobot> robotsList;
	protected AbstractGuestSciencePlanTrace[] guestSciencePlanTraces;

	protected Node issModel;
	protected Node modulesLightingNode;
	protected Node handrailsLightingNode;
	protected Node[] modules;
	protected Spatial[] textNodes;
	protected Spatial[] defaultNodes;
	protected Spatial keepoutTrace;
	private Map<String,BoxesNode> keepouts;
	private Map<String,BoxesNode> keepins;
	private ColoredBoxListNode coloredBoxes;
	private List<ScenarioToggleListener> listeners = new ArrayList<>();
	private boolean previewTeleop = false;
	private boolean showingAllRobots = true;
	
	double xLightsOffset = LightsCameraProperties.getPropertyAsDouble("xLightsOffset");
	double yLightsOffset = LightsCameraProperties.getPropertyAsDouble("yLightsOffset");
	double zLightsOffset = LightsCameraProperties.getPropertyAsDouble("zLightsOffset");
	
	@Inject
	public FreeFlyerScenario(Ardor3DEclipseNotPlugin a3enp, MApplication app) {
		super(); // <-- do we need this??
		robotsList = new Vector<RapidFreeFlyerRobot>();
		guestSciencePlanTraces = new AbstractGuestSciencePlanTrace[AgentsFromCommandLine.INSTANCE.getNumAgents()];
		
		Ardor3D.initialize(a3enp);
		start();
		hideAllRobots();

		app.getContext().set(FreeFlyerScenario.class, this);
		showingAllRobots = true;
		ActiveAgentSet.INSTANCE.addListener(this);
	}
	
	@Inject @Optional
	public void acceptAstrobeeStateManager(AstrobeeStateManager manager) {
		manager.addListener(this, MessageTypeExtAstro.CURRENT_ZONES_COMPRESSED_TYPE);
	}

	public Node getRoot() {
		return root;
	}
	
	/**
	 * Start the scenario. 
	 * @return true if start was successful or if scenario was already started
	 */
	public synchronized boolean start() 
	{
		try {
			if(!started) {	
				ImportExportManager.INSTANCE.startup();
				root =  new Node("FreeFlyerScenarioRoot");
				root.getSceneHints().setCullHint(CullHint.Never);
				
				modulesLightingNode = new Node("EvenLightingNode");
				handrailsLightingNode = new Node("TrueColorLightingNode");
				root.attachChild(modulesLightingNode);
				root.attachChild(handrailsLightingNode);
				addCoordinateFrame();
				setupDefaultRenderStates();

				//===================================================
				try {
					makeAndAttachThreeRobots();
				}
				catch(Exception e) {
					logAnError("Error While Building robot Avatar", e);
				} 

				if(WorkbenchConstants.worldIsGraniteLab()) {
					GraniteLab graniteLab =  new GraniteLab();
					root.attachChild(graniteLab);
				} else {
					LocationGenerator generator = LocationGenerator.getInstance();
					CompositeIssModel iss = new CompositeIssModel(generator.getModelsToLoad());
					issModel = iss.getModel();
					modulesLightingNode.attachChild(issModel);
					
					coloredBoxes = new ColoredBoxListNode();
					root.attachChild(coloredBoxes);

					handrailsLightingNode.attachChild(HandrailModelingNode.getStaticInstance());
					
					SmartDockModel smartDock = new SmartDockModel();
					root.attachChild(smartDock.getModel());
					
				}
//					setupLights();		
				
				initializeKeepouts();
				

				root.attachChild(CreatePlanTrace.getStaticInstance());
				
				root.attachChild(RunPlanTrace.getStaticInstance());
				
				guestSciencePlanTraces[0] = GuestSciencePlanTraceOne.getGuestScienceInstance();
				guestSciencePlanTraces[1] = GuestSciencePlanTraceTwo.getGuestScienceInstance();
				guestSciencePlanTraces[2] = GuestSciencePlanTraceThree.getGuestScienceInstance();

				for(int i=0; i<AgentsFromCommandLine.INSTANCE.getNumAgents(); i++) {
					root.attachChild(guestSciencePlanTraces[i]);
				}
				
				root.attachChild(AddViaMapPlane.getStaticInstance());

				root.attachChild(KeepoutModelingNode.getStaticInstance());

				ScenarioUpdater updater = new ScenarioUpdater(root);
				Ardor3D.getFrameHandler().addUpdater(updater);

				started = true;

				setupDefaultLights();

				setupAltLights(modulesLightingNode, 0.4f);
				setupAltLights(handrailsLightingNode, 0.4f);

				//initializePreferenceListeners();
				initializeDirtyEventListeners();
				initializeToggleNodes();
			}
		}
		catch(Throwable t) {
			logAnError("Error starting "+this.getClass().getSimpleName(), t);
		}

		if(started) {
			VerveScenarioStarted.scenarioStarted(this);
		}
		return started;
	}
	
	private void initializeKeepouts() {
		keepouts = new HashMap<String,BoxesNode>();
		keepins = new HashMap<String,BoxesNode>();
		
		for(int i=0; i<AgentsFromCommandLine.INSTANCE.getNumAgents(); i++) {
			String agentName = AgentsFromCommandLine.INSTANCE.getAgent(i).name();
			ZonesNodesHelper znh = ZonesNodesHelper.get(agentName);
			keepins.put(agentName, znh.getKeepins());
			root.attachChild(keepins.get(agentName));
			keepins.get(agentName).getSceneHints().setCullHint(CullHint.Always);
			
			keepouts.put(agentName, znh.getKeepouts());
			root.attachChild(keepouts.get(agentName));
			keepouts.get(agentName).getSceneHints().setCullHint(CullHint.Always);
		}
		
		ZonesNodesHelper znh = ZonesNodesHelper.get("Config");
		keepins.put("Config", znh.getKeepins());
		root.attachChild(znh.getKeepins());
		keepouts.put("Config", znh.getKeepouts());
		root.attachChild(znh.getKeepouts());
		
		hideAllKeepins();
	}
	
	private void makeAndAttachThreeRobots() throws Exception {
		FreeFlyerAvatarBuilder ffab = new FreeFlyerAvatarBuilder();

		for(int i=0; i<AgentsFromCommandLine.INSTANCE.getNumAgents(); i++) {
			RapidFreeFlyerRobot rffr = ffab.buildAvatarGetRobot(AgentsFromCommandLine.INSTANCE.getColor(i), AgentsFromCommandLine.INSTANCE.getAgent(i));
			robotsList.add(rffr);
			root.attachChild(rffr.getRobotNode());
			// setting visibleByDefault doesn't actually make these invisible on startup
			rffr.getPart(RapidFreeFlyerRobot.DRAGGABLE_PREVIEW).setVisible(false);
		}
		hideAllPreviewModels();
	}
	
	public RapidFreeFlyerRobot[] getAllRobots() {
		// not sure why using toArray() and casting threw exception at runtime
		RapidFreeFlyerRobot[] ret = new RapidFreeFlyerRobot[robotsList.size()];
		int i=0;
		for(RapidFreeFlyerRobot rffr : robotsList) {
			ret[i] = rffr;
			i++;
		}
		return ret;
	}

	public void setPrimaryRobot(Agent primaryBee) {
		hideAllPreviewModels();
		for(int i=0; i<AgentsFromCommandLine.INSTANCE.getNumAgents(); i++) {
			if(primaryBee.equals(AgentsFromCommandLine.INSTANCE.getAgent(i))) {
				primaryRobot = robotsList.get(i);
				showPrimaryRobot();
				showPreviewModelOfPrimaryRobot();
				return;
			}
		}
	}

	private void initializeDirtyEventListeners() {
		//-- When spatials are added/deleted from the root node, 
		//-- trigger an update of the SceneGraphTreePanel
		DirtyEventListener foo = new DirtyEventListener () {
			long m_lastTime = System.currentTimeMillis();
			public boolean spatialDirty(Spatial spatial, DirtyType dirtyType) {
				switch(dirtyType) {
				case Transform:
					break;
				case Attached:
				case Detached:
					long thisTime = System.currentTimeMillis();
					if(thisTime > m_lastTime + 5000) {
						m_lastTime = thisTime;
						SceneGraphTreeViewPart.refreshTree();
					}
					break;
				default:
					break;
				}
				return false;
			}

			public boolean spatialClean(Spatial spatial,
					DirtyType dirtyType) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		root.setListener(foo);
	}

	private void logAnError(String msg, Throwable t) {
		logger.error(msg, t);
		IrgUI.errorDialog(msg, msg, t);
	}

	public AbstractRobot getPrimaryRobot() {
		return primaryRobot;
	}
	
	// takes the "rapid/Robot" form of string and returns "Robot"
	protected String shorten(String tooLong) {
		String[] tokens = tooLong.split("/");
		String shortName = tokens[tokens.length-1];
		return shortName;
	}
	
	protected String getPrimaryRobotShortName() {
		if(primaryRobot == null) {
			return "";
		}
		return shorten(primaryRobot.getName());
	}

	/** Show CreatePlanTrace, no robots or traces */
	public void planEditorTabActive() {
		previewTeleop = false;

		showCreatePlanTrace();
		showConfigKeepouts();

		hideAllRobots();
		hideAllPreviewModels();
		hideRunPlanTrace();
		hideGuestSciencePlanTraces();
	}

	/** Show RunPlanTrace and all connected robots, hide other things */
	public void showRobotsRunPlanTraceAndKeepoutsNoPreview() {
		previewTeleop = false;

		showRobotsRunPlanTraceAndKeepouts();
	}
	
	public void showRobotsGuestSciencePlanTraceAndKeepoutsNoPreview() {
		previewTeleop = false;
		
		showAllRobots();
		hideRunPlanTrace();		
		showConfigKeepouts(); // need showKeepoutsOfAllRobots();
		hideCreatePlanTrace();
		hideAllPreviewModels();
		showGuestSciencePlanTraces();
	}

	/** Show all connected robots and RunPlanTrace; and translation preview if true */
	public void teleopTranslateTabActive(boolean previewing) {
		previewTeleop = previewing;
		showRobotsRunPlanTraceAndKeepouts();

		showPreviewModelOfPrimaryRobot();
	}

	private void showRobotsRunPlanTraceAndKeepouts() {
		showAllRobots();
		showRunPlanTrace();		
		showConfigKeepouts(); // need showKeepoutsOfSelectedRobot();
		hideCreatePlanTrace();
		hideAllPreviewModels();
		hideGuestSciencePlanTraces();
	}

	/** Hide everything but the ISS model (including hiding the keepout zones) */
	public void modellingTabActive() {
		hideAllRobots();
		hideAllPreviewModels();
		hideRunPlanTrace();
		hideCreatePlanTrace();
		hideConfigKeepouts();// need hideAllKeepouts()
		hideGuestSciencePlanTraces();
	}

	private void hideAllRobots() {
		showingAllRobots = false;
		for(RapidFreeFlyerRobot rffr : robotsList) {
			if(rffr != null) {
				rffr.getRobotNode().getSceneHints().setCullHint(CullHint.Always);
				rffr.getRobotNode().getSensorsNode().getSceneHints().setCullHint(CullHint.Always);
			}
		}
	}

	private void showAllRobots() {
		showingAllRobots = true;
		showPrimaryRobot();
		for(RapidFreeFlyerRobot rffr : robotsList) {
			if(rffr != null) {
				Agent robotAgent = rffr.getAgent();
				if(ActiveAgentSet.contains(robotAgent)) {
					rffr.getRobotNode().getSceneHints().setCullHint(CullHint.Inherit);
					rffr.getRobotNode().getSensorsNode().getSceneHints().setCullHint(CullHint.Inherit);
				}
			}
		}
	}

	private void showPrimaryRobot() {
		if(primaryRobot == null) {
			return;
		}
		Spatial baseRobot = primaryRobot.getRobotNode();
		baseRobot.getSceneHints().setCullHint(CullHint.Inherit);
	}
	
	public void showPreviewModelOfPrimaryRobot() {
		if(primaryRobot != null) {
			if(previewTeleop) {
				RobotPartDraggablePreview robT = ((RobotPartDraggablePreview)primaryRobot.getPart(RapidFreeFlyerRobot.DRAGGABLE_PREVIEW));
				robT.showDraggablePreview();
			}
		}
	}
	
	@Inject @Optional
	public void acceptPrimaryBee(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent newPrimary) {
		if(newPrimary == null) {
			return;
		}
		setPrimaryRobot(newPrimary);
	}

	public void hideAllPreviewModels() {
		RobotPartDraggablePreview robT;

		for(RapidFreeFlyerRobot rffr : robotsList) {
			robT = ((RobotPartDraggablePreview)rffr.getPart(RapidFreeFlyerRobot.DRAGGABLE_PREVIEW));
			robT.hideDraggablePreview();
		}
	}

	private void addCoordinateFrame() {
		CoordinateFrame cf = new CoordinateFrame();
		root.attachChild(cf);
		cf.getSceneHints().setCullHint(CullHint.Never);
	}

	private void setupDefaultRenderStates() {
		// set up some default render states
		final ZBufferState buf = new ZBufferState();
		buf.setEnabled(true);
		buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
		root.setRenderState(buf);

		CullState cs = new CullState();
		cs.setCullFace(Face.Back);
		root.setRenderState(cs);

		MaterialState ms = new MaterialState();
		ms.setColorMaterial(ColorMaterial.Diffuse);
		ms.setAmbient(ColorRGBA.GRAY);
		ms.setDiffuse(ColorRGBA.WHITE);
		ms.setEmissive(ColorRGBA.BLACK);
		ms.setSpecular(ColorRGBA.WHITE);
		ms.setShininess(64);
		root.setRenderState(ms);
	}

	private void setupDefaultLights() {
		/** Attach the light to a lightState and the lightState to rootNode. */
		DirectionalLight dl = new DirectionalLight();
		dl.setDirection(new Vector3(-0.5f,-0.1f,1));//0.0,0.0,1));
		dl.setDiffuse(ColorRGBA.WHITE);
		dl.setAmbient(ColorRGBA.WHITE);//new ColorRGBA(0.3f, 0.3f, 0.3f, 1));
		dl.setDiffuse(new ColorRGBA(0.8f, 0.8f, 0.8f, 1));
		//dl.setAmbient(ColorRGBA.BLACK);
		dl.setAttenuate(false);
		dl.setEnabled(true);

		DirectionalLight dl2 = new DirectionalLight();
		//SpotLight dl2 = new SpotLight();
		dl2.setDirection(new Vector3(0.5f,0.1f,1));
		dl2.setDiffuse(new ColorRGBA(0.3f, 0.3f, 0.3f, 1));//ColorRGBA.WHITE);
		dl2.setAmbient(new ColorRGBA(0.3f, 0.3f, 0.3f, 1));
		dl2.setAttenuate(false);
		dl2.setEnabled(true);

		DirectionalLight dl3 = new DirectionalLight();
		//SpotLight dl2 = new SpotLight();
		dl3.setDirection(new Vector3(-18.0f, 7.430f, -4.9));
		dl3.setDiffuse(new ColorRGBA(ColorRGBA.WHITE));
		dl3.setAmbient(new ColorRGBA(ColorRGBA.WHITE));
		dl3.setAttenuate(false);
		dl3.setEnabled(true);
		
		for(Spatial child : root.getChildren()) {
			Node node = (Node) child;

			final LightState lightState = new LightState();
			lightState.setEnabled(true);
			node.setRenderState(lightState);

			LightState ls = (LightState) node.getLocalRenderState(RenderState.StateType.Light);
			ls.setSeparateSpecular(true);
			ls.attach(dl);
			ls.attach(dl2);
			ls.attach(dl3);
		}
	}

	private void setupAltLights(Node node, float intensity) {
		final LightState lightState = new LightState();
		lightState.setEnabled(true);
		node.setRenderState(lightState);
		LightState ls = (LightState) node.getLocalRenderState(RenderState.StateType.Light);
		
		final List<Vector3> WHITE_POS = new ArrayList<Vector3>();
		WHITE_POS.add(new Vector3(-8.3 + xLightsOffset, 0 + yLightsOffset, 0 + zLightsOffset));
		WHITE_POS.add(new Vector3(-7.2 + xLightsOffset, -5 + yLightsOffset, 0 + zLightsOffset));
		WHITE_POS.add(new Vector3(-1f + xLightsOffset, 0 + yLightsOffset, 0 + zLightsOffset));
		WHITE_POS.add(new Vector3(7.5 + xLightsOffset, 0 + yLightsOffset, 0 + zLightsOffset));

		WHITE_POS.add(new Vector3(9 + xLightsOffset, 7.5 + yLightsOffset, 0 + zLightsOffset));
		
		WHITE_POS.add(new Vector3(8.5 + xLightsOffset, -4 + yLightsOffset, 0 + zLightsOffset));
		
		WHITE_POS.add(new Vector3(7.5 + xLightsOffset, -10.5 + yLightsOffset, 0 + zLightsOffset));
		
		for(final Vector3 w : WHITE_POS){
			final PointLight point = new PointLight();
			point.setDiffuse(new ColorRGBA(intensity,intensity,intensity,1));
			point.setAmbient(new ColorRGBA(intensity,intensity,intensity,1));
			point.setEnabled(true);
			point.setAttenuate(true);
			point.setConstant(0.1f);
			point.setQuadratic(0.01f);
			point.setLinear(0.1f);
			point.setLocation(w);
			ls.attach(point);
		}
		
		final List<Vector3> BLUE_POS = new ArrayList<Vector3>();
		BLUE_POS.add(new Vector3(-7.2 + xLightsOffset, -3.5 + yLightsOffset, 0 + zLightsOffset));
		BLUE_POS.add(new Vector3(-6.8 + xLightsOffset, -1.0 + yLightsOffset, 0 + zLightsOffset));
		BLUE_POS.add(new Vector3(-5 + xLightsOffset, 0 + yLightsOffset, 0 + zLightsOffset));
		BLUE_POS.add(new Vector3(-3 + xLightsOffset, 0 + yLightsOffset, 0 + zLightsOffset));
		
		BLUE_POS.add(new Vector3(3 + xLightsOffset, 0 + yLightsOffset, 0 + zLightsOffset));
		
		BLUE_POS.add(new Vector3(5.5 + xLightsOffset, 0 + yLightsOffset, 0 + zLightsOffset));
		
		BLUE_POS.add(new Vector3(8 + xLightsOffset, 1.5 + yLightsOffset, 0 + zLightsOffset));
		BLUE_POS.add(new Vector3(8.5 + xLightsOffset, -1.5 + yLightsOffset, 0 + zLightsOffset));
		
		BLUE_POS.add(new Vector3(8 + xLightsOffset, 5 + yLightsOffset, 0 + zLightsOffset));
		BLUE_POS.add(new Vector3(8.5 + xLightsOffset, -7 + yLightsOffset, 0 + zLightsOffset));
		
		BLUE_POS.add(new Vector3(7.5 + xLightsOffset, -10.5 + yLightsOffset, 0 + zLightsOffset));
		BLUE_POS.add(new Vector3(8.5 + xLightsOffset, -11 + yLightsOffset, -1 + zLightsOffset));
		
		for(final Vector3 p : BLUE_POS){
			final PointLight point = new PointLight();
			point.setDiffuse(new ColorRGBA(0,0,1,1));
			point.setAmbient(new ColorRGBA(0,0,1,1));
			point.setEnabled(true);
			point.setAttenuate(false);
			point.setConstant(0.1f);
			point.setQuadratic(2.0f);
			point.setLinear(0.1f);
			point.setLocation(p);
			ls.attach(point);
		}
	}

	public void showKeepoutsOfPrimaryRobot() {
		if(getPrimaryRobot() == null) {
			return;
		}
		keepouts.get(getPrimaryRobotShortName()).getSceneHints().setCullHint(CullHint.Inherit);
	}
	
	public void showConfigKeepouts() {
		keepouts.get("Config").getSceneHints().setCullHint(CullHint.Inherit);
	}
	
	public void showAllKeepouts() {
		for(RapidFreeFlyerRobot rffr : robotsList) {
			keepouts.get(shorten(rffr.getName())).getSceneHints().setCullHint(CullHint.Always);
		}
		keepouts.get("Config").getSceneHints().setCullHint(CullHint.Always);
	}
	
	public void hideKeepoutsOfPrimaryRobot() {
		keepouts.get(getPrimaryRobotShortName()).getSceneHints().setCullHint(CullHint.Always);
	}
	
	public void hideConfigKeepouts() {
		keepouts.get("Config").getSceneHints().setCullHint(CullHint.Always);
	}
	
	public void hideAllKeepouts() {
		for(RapidFreeFlyerRobot rffr : robotsList) {
			keepouts.get(shorten(rffr.getName())).getSceneHints().setCullHint(CullHint.Always);
		}
		keepouts.get("Config").getSceneHints().setCullHint(CullHint.Always);
	}

	public void showKeepinsOfPrimaryRobot() {
		keepins.get(getPrimaryRobotShortName()).getSceneHints().setCullHint(CullHint.Inherit);
	}
	
	public void showConfigKeepins() {
		keepins.get("Config").getSceneHints().setCullHint(CullHint.Inherit);
	}
	
	public void hideKeepinsOfPrimaryRobot() {
		keepins.get(getPrimaryRobotShortName()).getSceneHints().setCullHint(CullHint.Always);
	}
	
	public void hideConfigKeepins() {
		keepins.get("Config").getSceneHints().setCullHint(CullHint.Always);
	}
	
	public void hideAllKeepins() {
		for(RapidFreeFlyerRobot rffr : robotsList) {
			keepins.get(shorten(rffr.getName())).getSceneHints().setCullHint(CullHint.Always);
		}
		keepins.get("Config").getSceneHints().setCullHint(CullHint.Always);
	}
	
	public void addToggleListener(ScenarioToggleListener part) {
		if(listeners.size() > 0) {
			ScenarioToggleListener listener = listeners.get(0);
			part.setText(listener.isTextSelected());
			part.setDefault(listener.isDefaultSelected());
			part.setUsos(listener.isUsosSelected());
			part.setPlanTrace(listener.isPlanTraceSelected());
			for(int i = 0; i < modules.length; i++) {
				part.setModule(listener.isModuleSelected(i), i);
			}
		}
		listeners.add(part);
	}

	public void removeToggleListener(ScenarioToggleListener part) {
		listeners.remove(part);
	}

	private void initializeToggleNodes() {
		if(issModel == null) {
			return;
		}
		//modules[0].getSceneHints().setLightCombineMode(LightCombineMode.Off);
		modules = new Node[issModel.getChildren().size()];
		textNodes = new Spatial[modules.length];
		defaultNodes = new Spatial[modules.length];

		int i = 0;
		for(Spatial node : issModel.getChildren()) {
			Node module = (Node) node;
			module = (Node) module.getChild(0);
			modules[i] = (Node) module.getChild(0);

			//If they have a text material (Cupola doesn't)
			if(modules[i].getChildren().size() > 3) {
				textNodes[i] = modules[i].getChild(3);
			} else {
				textNodes[i] = null;
			}

			defaultNodes[i] = modules[i].getChild(0);
			i++;
		}
	}

	public void hideText() {
		for(int i = 0; i < modules.length; i ++) {
			Spatial node = textNodes[i];
			if(node != null && node.getParent() != null)  {
				node.removeFromParent();
				for(ScenarioToggleListener part : listeners) {
					part.setText(false);
				}
			}
		}
	}

	public void showText() {
		for(int i = 0; i < modules.length; i ++) {
			Spatial node = textNodes[i];
			if(node != null && node.getParent() == null)  {
				modules[i].attachChild(node);
				for(ScenarioToggleListener part : listeners) {
					part.setText(true);
				}
			}
		}
	}

	public void hideUsos() {
		if(issModel != null) {
			issModel.getSceneHints().setCullHint(CullHint.Always);
			for(ScenarioToggleListener part : listeners) {
				part.setUsos(false);
			}
		}
	}

	public void showUsos() {
		if(issModel != null) {
			issModel.getSceneHints().setCullHint(CullHint.Inherit);
			for(ScenarioToggleListener part : listeners) {
				part.setUsos(true);
			}
		}
	}
	
	public void showGuestSciencePlanTraces() {
		for(int i=0; i<AgentsFromCommandLine.INSTANCE.getNumAgents(); i++) {
			if(guestSciencePlanTraces[i] != null) {
				guestSciencePlanTraces[i].show();
			}
		}
	}
	
	public void hideGuestSciencePlanTraces() {
		for(int i=0; i<AgentsFromCommandLine.INSTANCE.getNumAgents(); i++) {
			if(guestSciencePlanTraces[i] != null) {
				guestSciencePlanTraces[i].hide();
			}
		}
	}

	public void hideRunPlanTrace() {
		if(RunPlanTrace.getStaticInstance() != null) {
			RunPlanTrace.getStaticInstance().hide();
			for(ScenarioToggleListener part : listeners) {
				part.setPlanTrace(false);
			}
		}
	}

	public void showRunPlanTrace() {
		if(RunPlanTrace.getStaticInstance() != null) {
			RunPlanTrace.getStaticInstance().show();
			for(ScenarioToggleListener part : listeners) {
				part.setPlanTrace(true);
			}
		}
	}

	public void showCreatePlanTrace() {
		if(CreatePlanTrace.getStaticInstance() != null) {
			CreatePlanTrace.getStaticInstance().show();
		}
	}

	public void hideCreatePlanTrace() {
		if(CreatePlanTrace.getStaticInstance() != null) {
			CreatePlanTrace.getStaticInstance().hide();
		}
	}


	public void showHandrails() {
		HandrailModelingNode.getStaticInstance().getSceneHints().setCullHint(CullHint.Inherit);
	}

	public void hideHandrails() {
		HandrailModelingNode.getStaticInstance().getSceneHints().setCullHint(CullHint.Always);
	}

	public String[] getFOVCameraNames() {
		return robotsList.get(0).getFOVCameraNames();
	}

	public void hideFOVCamera(String name) {
		for(RapidFreeFlyerRobot robot : robotsList) {
			robot.hideFOVCamera(name);
			for(ScenarioToggleListener part : listeners) {
				part.setCamera(false, name);
			}
		}
	}

	public void showFOVCamera(String name) {
		for(RapidFreeFlyerRobot robot : robotsList) {
			robot.showFOVCamera(name);
			for(ScenarioToggleListener part : listeners) {
				part.setCamera(true, name);
			}
		}
	}

	public void resetPoseHistory() {
		for(RapidFreeFlyerRobot rffr : robotsList) {
			if(rffr != null) {
				rffr.resetPart(RapidRobot.POSE_HISTORY);
			}
		}
	}

	public IVerveScenario getSingleton() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void activeAgentSetChanged() {
		for(RapidFreeFlyerRobot rffr : robotsList) {
			if(rffr != null) {
				Agent robotAgent = rffr.getAgent();
				if(ActiveAgentSet.contains(robotAgent)) {
					try {
						rffr.setTelemetryEnabled(true);
					} catch (TelemetryException e) {
						logger.error("Couldn't connect to telemetry for " + robotAgent);
						e.printStackTrace();
					}
				}
			}
		}
		if(showingAllRobots) {
			showAllRobots();
		}
	}

	@Override
	public void activeAgentAdded(Agent agent, String participantId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activeAgentRemoved(Agent agent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAstrobeeStateChange(AggregateAstrobeeState stateKeeper) {
		// TODO Auto-generated method stub
		
	}
	
	
}
