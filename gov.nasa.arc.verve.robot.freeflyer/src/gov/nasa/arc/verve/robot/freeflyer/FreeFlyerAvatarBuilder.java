package gov.nasa.arc.verve.robot.freeflyer;

import gov.nasa.arc.irg.plan.ui.io.ConfigFileWrangler;
import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.arc.verve.robot.IRobotAvatarBuilder;
import gov.nasa.arc.verve.robot.exception.TelemetryException;
import gov.nasa.arc.verve.robot.scenegraph.RobotNode;
import gov.nasa.rapid.v2.e4.agent.Agent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import com.ardor3d.extension.model.collada.jdom.data.AssetData;
import com.ardor3d.extension.model.collada.jdom.data.ColladaStorage;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.resource.ResourceLocator;
import com.ardor3d.util.resource.ResourceSource;
import com.ardor3d.util.resource.URLResourceSource;

public class FreeFlyerAvatarBuilder implements IRobotAvatarBuilder  {
	public static final String CONTEXT = "freeflyer";
	
	private Node makeRobotNode(String robotName) {
		Node model = null;
		URL url;
		try {
			// import the model
			com.ardor3d.extension.model.collada.jdom.ColladaImporter colladaImporter;
			colladaImporter             = getImporter(null);
			url = new URL(translateRobotNameToUrlString(robotName));
			ResourceSource fileResource = new URLResourceSource(url);
			ColladaStorage storage      = colladaImporter.load(fileResource);
			model = processStorage(storage);
			model.updateWorldBound(true);
		}
		catch(Throwable t) {
			IOException e =  new IOException("Failed to load COLLDADA file");
			e.initCause(t);
			e.printStackTrace();
			//	            throw e;
		}
		return model;
	}

	private String translateRobotNameToUrlString(String robotName) {
		if(WorkbenchConstants.HONEY.equals(robotName) || WorkbenchConstants.YELLOW.equals(robotName)) {
			return "file:///" + ConfigFileWrangler.getInstance().getAstrobeeModelsPath() + File.separator + "AstrobeeHoney.dae";
		}
		else if(WorkbenchConstants.QUEEN.equals(robotName) || WorkbenchConstants.GREEN.equals(robotName)) {
			return "file:///" + ConfigFileWrangler.getInstance().getAstrobeeModelsPath() + File.separator + "AstrobeeQueen.dae";
		}
		else if(WorkbenchConstants.BUMBLE.equals(robotName) || WorkbenchConstants.BLUE.equals(robotName)) {
			return "file:///" + ConfigFileWrangler.getInstance().getAstrobeeModelsPath() + File.separator + "AstrobeeBumble.dae";
		}
		if(WorkbenchConstants.PINK.equals(robotName)) {
			return "file:///" + ConfigFileWrangler.getInstance().getAstrobeeModelsPath() + File.separator + "AstrobeeMelissa.dae";
		}
		else if(WorkbenchConstants.PURPLE.equals(robotName)) {
			return "file:///" + ConfigFileWrangler.getInstance().getAstrobeeModelsPath() + File.separator + "AstrobeeBSharp.dae";
		}
		else if(WorkbenchConstants.ORANGE.equals(robotName)) {
			return "file:///" + ConfigFileWrangler.getInstance().getAstrobeeModelsPath() + File.separator + "AstrobeeKiller.dae";
		}
		return "platform:/plugin/gov.nasa.arc.verve.robot.freeflyer/models/astrobeeModel/AstrobeeHoney.dae";
	}
	
	private com.ardor3d.extension.model.collada.jdom.ColladaImporter getImporter(Map<String,Object> params) {
		com.ardor3d.extension.model.collada.jdom.ColladaImporter colladaImporter;
		colladaImporter = new com.ardor3d.extension.model.collada.jdom.ColladaImporter();
		ResourceLocator textureLocator = null;
		ResourceLocator modelLocator = null;
		if(params != null) {
			textureLocator = (ResourceLocator)params.get("textureLocator");
			modelLocator = (ResourceLocator)params.get("modelLocator");
		}
		if(textureLocator != null) {
			colladaImporter.setTextureLocator(textureLocator);
		}
		if(modelLocator != null) {
			colladaImporter.setModelLocator(modelLocator);
		}
		colladaImporter.setOptimizeMeshes(true);
		return colladaImporter;
	}

	private Node processStorage(ColladaStorage storage) {
		Node model = storage.getScene();
		AssetData assetData = storage.getAssetData();
		double unitMeter = assetData.getUnitMeter();
		if(unitMeter != 0 && unitMeter != 1) {
			model.setScale(unitMeter, unitMeter, unitMeter);
		}
		return model;
	}

	public RapidFreeFlyerRobot buildAvatarGetRobot(String robotName, Agent agent)
			throws IllegalStateException, TelemetryException {

		Node model = makeRobotNode(robotName);
		RapidFreeFlyerRobot robot = new RapidFreeFlyerRobot(agent);
		robot.attachToNodesIn(model);
		
//		robot.setTelemetryEnabled(true);
		return robot;
	}
	
	// XXX Nobody calls this
	public boolean canBuild(String robotName, String context) {
		String lowerContext = context.toLowerCase();
		if(lowerContext.equals(CONTEXT))	
			return true;
		return false;
	}

	public RobotNode buildAvatar(String robotName, String context)
			throws IllegalStateException, TelemetryException, IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
