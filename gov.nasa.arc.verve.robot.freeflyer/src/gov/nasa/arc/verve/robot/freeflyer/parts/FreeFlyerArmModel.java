package gov.nasa.arc.verve.robot.freeflyer.parts;

import gov.nasa.arc.irg.plan.ui.io.ConfigFileWrangler;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import com.ardor3d.extension.model.collada.jdom.data.AssetData;
import com.ardor3d.extension.model.collada.jdom.data.ColladaStorage;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Transform;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.resource.ResourceLocator;
import com.ardor3d.util.resource.URLResourceSource;

/**
 * Loads .dae files from the arm
 * 
 * @author rrgoetz
 *
 */
public class FreeFlyerArmModel extends Node {
	protected Node model = null;
	protected String basePath = ConfigFileWrangler.getInstance().getIssModelsPath();

	public Node getModel() {
		return model;
	}

	public FreeFlyerArmModel() {
		try {
			model = new Node("FreeFlyer Arm");

			final com.ardor3d.extension.model.collada.jdom.ColladaImporter colladaImporter = getImporter(null);
			
			final Node baseLink = processStorage(colladaImporter.load(new URLResourceSource(new URL("platform:/plugin/gov.nasa.arc.verve.robot.freeflyer/models/astrobeeModel/base_link_gray75.dae"))));
			Transform baseTfm = new Transform();
			Matrix3 m = new Matrix3();
			m.applyRotationY(Math.PI);
			baseTfm.setRotation(m);
			baseTfm.setTranslation(0.0, 0, -0.05);
			baseLink.setTransform(baseTfm);
			
			final Node armProximal = processStorage(colladaImporter.load(new URLResourceSource(new URL("platform:/plugin/gov.nasa.arc.verve.robot.freeflyer/models/astrobeeModel/arm_proximal_link_gray75.dae"))));
			armProximal.setName("arm_proximal_link");
			armProximal.setTranslation(0.12, 0, 0.065);
			final Node armDistal = processStorage(colladaImporter.load(new URLResourceSource(new URL("platform:/plugin/gov.nasa.arc.verve.robot.freeflyer/models/astrobeeModel/arm_distal_link_gray75.dae"))));
			armDistal.setName("arm_distal_link");
			armDistal.setTranslation(-0.1, 0, 0);
			final Node gripperRightProximal = processStorage(colladaImporter.load(new URLResourceSource(new URL("platform:/plugin/gov.nasa.arc.verve.robot.freeflyer/models/astrobeeModel/gripper_right_proximal_link_gray25.dae"))));
			gripperRightProximal.setName("gripper_right_proximal_link");
			
			Transform gripperRightProximalTfm = new Transform();
//			Matrix3 gripperRightProximalM = new Matrix3();
//			gripperRightProximalM.applyRotationZ(Math.PI  / 2.0);
//			gripperRightProximalTfm.setRotation(gripperRightProximalM);
			gripperRightProximalTfm.setTranslation(-0.045, 0.01, -0.05);
			gripperRightProximal.setTransform(gripperRightProximalTfm);
			
			final Node gripperLeftProximal = processStorage(colladaImporter.load(new URLResourceSource(new URL("platform:/plugin/gov.nasa.arc.verve.robot.freeflyer/models/astrobeeModel/gripper_left_proximal_link_gray25.dae"))));
			gripperLeftProximal.setName("gripper_left_proximal_link");
			gripperLeftProximal.setTranslation(-0.045, -0.01, -0.05);
			
			final Node gripperRightDistal = processStorage(colladaImporter.load(new URLResourceSource(new URL("platform:/plugin/gov.nasa.arc.verve.robot.freeflyer/models/astrobeeModel/gripper_right_distal_link_gray25.dae"))));
			gripperRightDistal.setName("gripper_right_distal_link");
			Transform gripperRightDistalTfm = new Transform();
			Matrix3 gripperRightDistalM = new Matrix3();
			gripperRightDistalM.applyRotationZ(Math.PI  / 3.0);
			gripperRightDistalTfm.setRotation(gripperRightDistalM);
			gripperRightDistalTfm.setTranslation(-0.05, 0, 0.0);
			gripperRightDistal.setTransform(gripperRightDistalTfm);
			
			final Node gripperLeftDistal = processStorage(colladaImporter.load(new URLResourceSource(new URL("platform:/plugin/gov.nasa.arc.verve.robot.freeflyer/models/astrobeeModel/gripper_left_distal_link_gray25.dae"))));
			gripperLeftDistal.setName("gripper_left_distal_link");
			Transform gripperLeftDistalTfm = new Transform();
			Matrix3 gripperLeftDistalM = new Matrix3();
			gripperLeftDistalM.applyRotationZ(-Math.PI  / 3.0);
			gripperLeftDistalTfm.setRotation(gripperLeftDistalM);
			gripperLeftDistalTfm.setTranslation(-0.05, 0, 0.0);
			gripperLeftDistal.setTransform(gripperLeftDistalTfm);
			
			gripperRightProximal.attachChild(gripperRightDistal);
			gripperLeftProximal.attachChild(gripperLeftDistal);
			
			armDistal.attachChild(gripperRightProximal);
			armDistal.attachChild(gripperLeftProximal);
			armProximal.attachChild(armDistal);
			baseLink.attachChild(armProximal);
			model.attachChild(baseLink);
			
		} catch (final Throwable t) {
			final IOException e =  new IOException("Failed to load COLLADA file");
			e.initCause(t);
			e.printStackTrace();
		}		
	}

	// copied from FreeFlyerAvatarBuilder
	private com.ardor3d.extension.model.collada.jdom.ColladaImporter getImporter(final Map<String,Object> params) {
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

	// copied from FreeFlyerAvatarBuilder
	private Node processStorage(final ColladaStorage storage) {
		final Node model = storage.getScene();
		final AssetData assetData = storage.getAssetData();
		final double unitMeter = assetData.getUnitMeter();
		if(unitMeter != 0 && unitMeter != 1) {
			model.setScale(unitMeter, unitMeter, unitMeter);
		}
		return model;
	}
}
