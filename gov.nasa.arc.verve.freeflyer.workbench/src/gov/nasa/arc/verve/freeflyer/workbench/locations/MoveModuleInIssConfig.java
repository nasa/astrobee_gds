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
package gov.nasa.arc.verve.freeflyer.workbench.locations;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Instant;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class MoveModuleInIssConfig {
	
	String configFilename = "resources" + File.separator + "IssWorld" + File.separator + "IssConfiguration.json";
	//JSON order: [FWD, AFT, STBD, PORT, DECK, OVHD]
	
	String theModuleName = "NODE3"; // name of the module to be moved
	double theOffsetX = 0; // meters to move the module
	double theOffsetY = 4.19; // meters to move the module in Y
	
	public static void main(String[] args) {
		MoveModuleInIssConfig generator = new MoveModuleInIssConfig();
		
		JSONObject keepin;
		keepin = generator.createMovedObject();
		
		try {
			System.out.println(keepin);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			File resultFile = new File("resources" + File.separator + "IssWorld" + File.separator + "movedModule.json");
			mapper.writer().withDefaultPrettyPrinter().writeValue(resultFile, keepin);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private JSONObject createMovedObject() {	
		JSONArray modules = new JSONArray();
		File configFile = new File(configFilename);
		JSONParser parser = new JSONParser();
		try {
			URL url = configFile.toURI().toURL();
			InputStream is = url.openConnection().getInputStream();
			modules = (JSONArray) parser.parse(new InputStreamReader(is));
		} catch(Exception e) {
			System.out.println("Error reading IssConfiguration.json file.");
			e.printStackTrace();
			return null;
		}
		
		JSONObject movedModule= new JSONObject();
		
		JSONArray dividersSequence = new JSONArray();
		JSONArray keepinsSequence = new JSONArray();
		for(Object moduleObj : modules.toArray()) {
			JSONObject module = (JSONObject) moduleObj;
			String nameString = (String) module.get("name");
			if(theModuleName.equals(nameString)) {
				
				// Do the dividers
				JSONArray dividers = (JSONArray) module.get("dividers");
				for(Object dividerObj : dividers.toArray()) {
					double[] originalDivider = parseArray((JSONArray) dividerObj);
					JSONArray movedDivider = new JSONArray();
					for(int i = 0; i < originalDivider.length; i++) {
						if((i%3) == 0) {
							movedDivider.add(originalDivider[i] + theOffsetX);
						} else if((i%3) == 1) {
							movedDivider.add(originalDivider[i] + theOffsetY);
						} else {
							movedDivider.add(originalDivider[i]);
						}				
					}
					if(movedDivider.size() > 0) {
						dividersSequence.add(movedDivider);
					}
				}
				
				// Do the keepins
				JSONArray keepins = (JSONArray) module.get("keepin");
				for(Object keepinsObj : keepins.toArray()) {
					double[] originalBox = parseArray((JSONArray) keepinsObj);
					JSONArray movedBox = new JSONArray();
					for(int i = 0; i < originalBox.length; i++) {
						if((i%3) == 0) {
							movedBox.add(originalBox[i] + theOffsetX);
						} else if((i%3) == 1) {
							movedBox.add(originalBox[i] + theOffsetY);
						} else {
							movedBox.add(originalBox[i]);
						}				
					}
					if(movedBox.size() > 0) {
						keepinsSequence.add(movedBox);
					}
				}
			}
		}
		
		movedModule.put("name", theModuleName);
		movedModule.put("dividers", dividersSequence);
		movedModule.put("keepin", keepinsSequence);
		return movedModule;
	}
	
	private double[] parseArray(JSONArray jArray) {
		double[] parsed = new double[jArray.size()];
		
		for(int i=0; i < parsed.length; i ++) {
			Object num = jArray.get(i);
			if(num instanceof Long) {
				parsed[i] = (double) ((Long) num).doubleValue();
			} else if(num instanceof Double) {
				parsed[i] = (double) jArray.get(i);
			}
		}
		
		return parsed;
	}
}