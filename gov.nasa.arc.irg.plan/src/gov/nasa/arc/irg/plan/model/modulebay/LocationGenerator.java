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
package gov.nasa.arc.irg.plan.model.modulebay;

import gov.nasa.arc.irg.plan.model.modulebay.Module.ModuleName;
import gov.nasa.arc.irg.plan.ui.io.ConfigFileWrangler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Loads configuration information about model files for each node (name of file, position of model, bays,
 * bay centers, and keepin boxes)
 */
public class LocationGenerator {
	private static LocationGenerator INSTANCE;
	private HashMap<ModuleName, Module> map;
	private HashMap<String, double[]> modelsToLoad;
	private JSONArray jArray;

	private String configURL;
	private String testURL = ".." + File.separator + "gov.nasa.arc.verve.freeflyer.workbench" + File.separator + 
			"resources" + File.separator + "IssWorld" + File.separator + "IssConfiguration.json";
	
	private String simURL = "IssConfiguration.json";

	private LocationGenerator() {

		try {
			configURL = ConfigFileWrangler.getInstance().getIssConfigurationPath();
		} catch(Exception e) {
			// happens when the simulator calls this, because ConfigFileWrangler assumes a workbench is running
			// just load it later like the unit tests do
		}

		map = new HashMap<ModuleName, Module>();
		try {
			createMap();
		} catch (IOException e) {

		}

		modelsToLoad = new HashMap<String, double[]>();
		generateModulesToLoad();		
	}

	public static LocationGenerator getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new LocationGenerator();
		}

		return INSTANCE;
	}

	/** Returns name of model file and offset of that model */
	public HashMap<String, double[]> getModelsToLoad() {
		return modelsToLoad;
	}

	public HashMap<ModuleName, Module> getLocationMap() {
		return map;
	}

	private void generateModulesToLoad() {
		for(Module module : map.values()) {
			modelsToLoad.put(module.getModelFile(), module.getModuleOffset());
		}
	}

	private void createMap() throws IOException {
		JSONParser parser = new JSONParser();

		boolean loaded = false;
		try {
			File configFile = new File(configURL);
			URL url = configFile.toURI().toURL();
			InputStream is = url.openConnection().getInputStream();
			jArray = (JSONArray) parser.parse(new InputStreamReader(is));
			loaded = true;
		} catch(Exception e) {
			System.out.println("Failed to load IssConfiguration in workbench, attempting to load from working directory");
		}
		
		if( !loaded ){
			try {
				File configFile = new File( simURL );
				URL url = configFile.toURI().toURL();
				InputStream is = url.openConnection().getInputStream();
				jArray = (JSONArray) parser.parse(new InputStreamReader(is));
				loaded = true;
			} catch (Exception e) {
				System.out.println("Failed to load IssConfiguration.json from working directory, attempting to open in test mode.");
			}
			
		}

		if( !loaded ) {
			try {
				File configFile = new File( testURL );
				URL url = configFile.toURI().toURL();
				InputStream is = url.openConnection().getInputStream();
				jArray = (JSONArray) parser.parse(new InputStreamReader(is));
				loaded = true;
			} catch (Exception e) {
				e.printStackTrace();
				throw new IOException("Failed to load IssConfiguration.json.");
			}
		}

		if(loaded) {
			for(Object jObject : jArray) {
				addModule((JSONObject) jObject);
			}
        } else {
            System.out.println("Unable to load IssConfiguration.json ... entering unknown territory");
        }
	}

	private void addModule(JSONObject jObject) {
		String nameString = (String) jObject.get("name");
		ModuleName name = ModuleName.valueOf(ModuleName.class, nameString);

		String file = (String) jObject.get("file");

		JSONArray jOffset = (JSONArray) jObject.get("offset");
		double[] offset = parseArray(jOffset);

		JSONArray jRadii = (JSONArray) jObject.get("radii");
		double[][] radii = parse2DArray(jRadii);

		JSONArray jDividers = (JSONArray) jObject.get("dividers");
		double[][] dividersDoubles = parse2DArray(jDividers);
		Point3D[] dividers = doublesToPoints(dividersDoubles);

		JSONArray jKeepins = (JSONArray) jObject.get("keepin");
		double[][] keepins = parse2DArray(jKeepins);

		Module module = new Module(name, file, offset, radii, dividers, keepins);
		map.put(name, module);
	}

	private Point3D[] doublesToPoints(double[][] doubles) {
		Point3D[] points = new Point3D[doubles.length];

		for(int i = 0; i < points.length; i++) {
			if(doubles[i].length == 3) {
				points[i] = new Point3D(doubles[i][0], doubles[i][1], doubles[i][2]);
			}
		}

		return points;
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

	private double[][] parse2DArray(JSONArray jArray) {
		JSONArray firstRow = (JSONArray) jArray.get(0);
		double[][] parsed = new double[jArray.size()][firstRow.size()];

		for(int i = 0; i < parsed.length; i++) {
			parsed[i] = parseArray((JSONArray) jArray.get(i));
		}

		return parsed;
	}
}
