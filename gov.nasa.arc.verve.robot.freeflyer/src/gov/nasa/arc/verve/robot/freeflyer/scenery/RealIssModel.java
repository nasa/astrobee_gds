package gov.nasa.arc.verve.robot.freeflyer.scenery;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import org.eclipse.core.runtime.FileLocator;

import com.ardor3d.extension.model.collada.jdom.data.AssetData;
import com.ardor3d.extension.model.collada.jdom.data.ColladaStorage;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.resource.ResourceLocator;
import com.ardor3d.util.resource.ResourceSource;
import com.ardor3d.util.resource.URLResourceSource;

public class RealIssModel extends Node {
	protected Node model = null;
	protected Vector<Node> components = new Vector<Node>();
	protected String basePath = "platform:/plugin/gov.nasa.arc.verve.robot.freeflyer/models/";
	protected String modelsToLoadFilename = "ModelsToLoad.txt";
	
	public Node getModel() {
		return model;
	}

	public RealIssModel() {
		try {
			model = new Node();
			
			com.ardor3d.extension.model.collada.jdom.ColladaImporter colladaImporter;
			colladaImporter             = getImporter(null);
			
			URL modelsToLoadUrl = new URL( basePath + modelsToLoadFilename );
			File file = new File(FileLocator.toFileURL(modelsToLoadUrl).getPath());
			Scanner reader = new Scanner(file);
			int counter = 0;
			while(reader.hasNextLine()) {
				String modelFile = reader.nextLine();
				URL modelUrl = new URL( basePath + modelFile );
				
				ResourceSource fileResource = new URLResourceSource(modelUrl);
				ColladaStorage storage      = colladaImporter.load(fileResource);
				components.add(processStorage(storage));
				model.attachChild(components.get(counter));
				counter++;
			}
			reader.close();
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
