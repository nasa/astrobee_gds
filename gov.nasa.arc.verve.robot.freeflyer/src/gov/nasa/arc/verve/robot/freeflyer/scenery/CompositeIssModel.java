package gov.nasa.arc.verve.robot.freeflyer.scenery;

import gov.nasa.arc.irg.plan.ui.io.ConfigFileWrangler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.ardor3d.extension.model.collada.jdom.data.AssetData;
import com.ardor3d.extension.model.collada.jdom.data.ColladaStorage;
import com.ardor3d.math.Matrix3;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.resource.ResourceLocator;
import com.ardor3d.util.resource.ResourceSource;
import com.ardor3d.util.resource.URLResourceSource;

/**
 * Loads .dae files from the list input into the constructor (comes from LocationGenerator)
 * and attaches them to model (a Node).
 * 
 * @author ddwheele
 *
 */
public class CompositeIssModel extends Node {
	protected Node model = null;
	protected String basePath = ConfigFileWrangler.getInstance().getIssModelsPath();

	public Node getModel() {
		return model;
	}

	public CompositeIssModel(HashMap<String, double[]> modelsToLoad) {
		try {
			model = new Node("ISS_Model");

			com.ardor3d.extension.model.collada.jdom.ColladaImporter colladaImporter;
			colladaImporter             = getImporter(null);
			
			for(String filename : modelsToLoad.keySet()) {
				URL modelUrl = new File(basePath+ "/" + filename).toURI().toURL();
				
				ResourceSource fileResource = new URLResourceSource(modelUrl);
				ColladaStorage storage = colladaImporter.load(fileResource);
				
				Node thisNode = processStorage(storage);
				double[] offset = modelsToLoad.get(filename);
				thisNode.setTranslation(offset[0], offset[1], offset[2]);
				Matrix3 rot = new Matrix3();
				rot.fromAngles(offset[3] * Math.PI/180.0, offset[4] * Math.PI/180.0, offset[5] * Math.PI/180.0);
				thisNode.setRotation(rot);
				
				model.attachChild(thisNode);
			}
		} catch (Throwable t) {
			IOException e =  new IOException("Failed to load COLLADA file");
			e.initCause(t);
			e.printStackTrace();
		}		
	}

	// copied from FreeFlyerAvatarBuilder
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

	// copied from FreeFlyerAvatarBuilder
	private Node processStorage(ColladaStorage storage) {
		Node model = storage.getScene();
		AssetData assetData = storage.getAssetData();
		double unitMeter = assetData.getUnitMeter();
		if(unitMeter != 0 && unitMeter != 1) {
			model.setScale(unitMeter, unitMeter, unitMeter);
		}
		return model;
	}
}
