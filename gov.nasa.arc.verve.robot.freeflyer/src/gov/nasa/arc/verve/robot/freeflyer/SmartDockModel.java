package gov.nasa.arc.verve.robot.freeflyer;

import gov.nasa.arc.irg.plan.ui.io.ConfigFileWrangler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.ardor3d.extension.model.collada.jdom.data.AssetData;
import com.ardor3d.extension.model.collada.jdom.data.ColladaStorage;
import com.ardor3d.math.Matrix3;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.resource.ResourceLocator;
import com.ardor3d.util.resource.ResourceSource;
import com.ardor3d.util.resource.URLResourceSource;

public class SmartDockModel extends Node {
	
	private Node model = new Node("Smart_Dock");;
	protected String basePath = ConfigFileWrangler.getInstance().getIssModelsPath();
	private final String jsFileName = "smart_dock.json";
	public Node getModel() {
		return model;
	}

	
	private JSONArray parseJsonFile(URL fileURL) {
		JSONParser parser = new JSONParser();
		try {
			InputStream is = fileURL.openConnection().getInputStream();
			Object obj = parser.parse(new InputStreamReader(is));
			
			return (JSONArray) obj;
			
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public SmartDockModel() {
		File file = new File(ConfigFileWrangler.getInstance().getIssModelsPath()+File.separator+jsFileName);
		JSONArray jArray = null;
		if(file.exists()) {
			try{
				URL fileURL = file.toURI().toURL();
				jArray = parseJsonFile(fileURL);
			} catch (MalformedURLException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		} 
		for(Object obj : jArray.toArray()) {
			JSONObject jObj = (JSONObject) obj;
			
			String fileName = (String) jObj.get("file");
			double[] pos = parseArray((JSONArray) jObj.get("position"));
			double[] rot = parseArray((JSONArray) jObj.get("rotation"));
			
			try {
				

				com.ardor3d.extension.model.collada.jdom.ColladaImporter colladaImporter;
				colladaImporter             = getImporter(null);
			
				final URL modelUrl = new File(basePath+ "/" + fileName).toURI().toURL();
				final ResourceSource fileResource = new URLResourceSource(modelUrl);
				final ColladaStorage storage = colladaImporter.load(fileResource);
				
				final Node thisNode = processStorage(storage);
				thisNode.setTranslation(pos[0], pos[1], pos[2]);
				final Matrix3 rotMatrix = new Matrix3();
				rotMatrix.fromAngles(Math.toRadians(rot[0]), Math.toRadians(rot[1]), Math.toRadians(rot[2]));
				thisNode.setRotation(rotMatrix);
				model.attachChild(thisNode);

			} catch (final Throwable t) {
				final IOException e =  new IOException("Failed to load COLLADA file");
				e.initCause(t);
				e.printStackTrace();
			}	
			
		}
			
	}
	
	private double[] parseArray(JSONArray jArray) {
		double[] parsed = new double[jArray.size()];
		
		for(int i=0; i < parsed.length; i ++) {
			Object num = jArray.get(i);
			if(num instanceof Long) {
				parsed[i] = ((Long) num).doubleValue();
			} else if(num instanceof Double) {
				parsed[i] = ((Double) jArray.get(i)).doubleValue();
			}
		}
		
		return parsed;
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
