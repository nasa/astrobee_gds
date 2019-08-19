package gov.nasa.arc.verve.robot.freeflyer;

import gov.nasa.arc.verve.robot.exception.TelemetryException;
import gov.nasa.arc.verve.robot.freeflyer.camera.CameraConfigList;
import gov.nasa.arc.verve.robot.freeflyer.camera.CameraConfigList.CameraConfig;
import gov.nasa.arc.verve.robot.freeflyer.camera.CameraConfigListLoader;
import gov.nasa.arc.verve.robot.freeflyer.camera.FOVCameraModel;
import gov.nasa.arc.verve.robot.freeflyer.parts.RapidFreeFlyerPartFactory;
import gov.nasa.arc.verve.robot.parts.IRobotPart;
import gov.nasa.arc.verve.robot.parts.PartInfo;
import gov.nasa.arc.verve.robot.rapid.RapidRobot;
import gov.nasa.rapid.v2.e4.agent.Agent;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.CullHint;

public class RapidFreeFlyerRobot extends RapidRobot {
	private static final Logger 	logger = Logger.getLogger(RapidFreeFlyerRobot.class);

	public static final String ABSOLUTE_DRAGGABLE_PREVIEW		  = "DragPreview";
	public static final String RELATIVE_DRAGGABLE_PREVIEW	  = "LocalDragPreview";

	private Node m_model;
	private Node camerasNode = new Node("FOVCamerasNode");
	private List<FOVCameraModel> cameras;

	public RapidFreeFlyerRobot(Agent agent) {
		super(agent, new RapidFreeFlyerPoseProvider(primaryParticipant, agent), new RapidFreeFlyerPartFactory());
		createFOVCameras();
	}

	@Override
	public void setupPartInfo() {
		m_partInfo.put(BASE,            new PartInfo(true));
		m_partInfo.put(AXES,            new PartInfo(true));
		m_partInfo.put(POSE_HISTORY,    new PartInfo(true));
		m_partInfo.put(JOINTS, 			new PartInfo(true));
		m_partInfo.put(ABSOLUTE_DRAGGABLE_PREVIEW,	new PartInfo(false));
		m_partInfo.put(RELATIVE_DRAGGABLE_PREVIEW,	new PartInfo(false));
	}

	@Override
	public void attachToNodesIn(Node model)  throws IllegalStateException, TelemetryException {
		super.attachToNodesIn(model);
		if(model != null) {
			m_model = model;
			m_model.attachChild(camerasNode);

			for(FOVCameraModel camera : cameras) {
				camerasNode.attachChild(camera);
				camera.getSceneHints().setCullHint(CullHint.Always);
			}
			
		} else {
			logger.error("model is null");
		}
	}

	public Node getFreeFlyerModel() {
		return m_model;
	}

	/**
	 * Should be called every frame
	 */
	@Override
	public void handleFrameUpdate(long currentTimeMillis) {
		if(!m_robotNode.getSceneHints().getCullHint().equals(CullHint.Always)) {
			m_poseProvider.calculateTransform();

			try {
				for(IRobotPart part : m_parts.values()) {
					if(part.isVisible()) {
						part.handleFrameUpdate(currentTimeMillis);
					}
				}
			}
			catch (Throwable t) {
				logger.error("Exception in "+this.getClass().getSimpleName()+".handleUpdate()", t);
			}

			updateInterestPointListeners();
		}
	}

	public void showFOVCamera(String name) {
		for(FOVCameraModel camera : cameras) {
			if(camera.getName().equals(name)) {
				int childIndex = camerasNode.getChildIndex(camera);
				if(childIndex >-1) {	
					camera.getSceneHints().setCullHint(CullHint.Inherit);
				}
			}
		}
	}

	public void hideFOVCamera(String name) {
		for(FOVCameraModel camera : cameras) {

			if(camera.getName().equals(name)) {
				camera.getSceneHints().setCullHint(CullHint.Always);
			}
		}
	}

	public String[] getFOVCameraNames() {
		String[] names = new String[cameras.size()];
		for(int i = 0; i < names.length; i++) {
			names[i] = cameras.get(i).getName();
		}
		return names;
	}

	private void createFOVCameras() {
		try{
			cameras = new ArrayList<FOVCameraModel>();
			CameraConfigList cameraConfigList = CameraConfigListLoader.getStandardConfig();
			for(CameraConfig cameraConfig : cameraConfigList.getCameraConfigs()) {
				FOVCameraModel fovCameraModel = new FOVCameraModel(cameraConfig);
				cameras.add(fovCameraModel);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return m_agent.name();
	}
}
