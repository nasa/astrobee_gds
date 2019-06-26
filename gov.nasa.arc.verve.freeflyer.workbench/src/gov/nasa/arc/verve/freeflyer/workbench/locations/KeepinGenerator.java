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

public class KeepinGenerator {
	final static String worldName = "DetailIssWorld";
	//final static String worldName = "IssWorld";
	String configFilename = "resources" + File.separator + worldName + File.separator + "IssConfiguration.json";
	//JSON order: [FWD, AFT, STBD, PORT, DECK, OVHD]
	
	public static void main(String[] args) {
		KeepinGenerator generator = new KeepinGenerator();
		
		JSONObject keepin;
		keepin = generator.createKeepinObject();
		JSONParser parser = new JSONParser();
		
		try {
			System.out.println(keepin);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			File resultFile = new File("resources" + File.separator + worldName + File.separator + "keepins" + File.separator + "keepin.json");
			mapper.writer().withDefaultPrettyPrinter().writeValue(resultFile, keepin);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private JSONObject createKeepinObject() {	
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
		
		JSONObject keepin = new JSONObject();
		
		JSONArray sequence = new JSONArray();
		for(Object moduleObj : modules.toArray()) {
			JSONObject module = (JSONObject) moduleObj;
			JSONArray boxes = (JSONArray) module.get("keepin");
			double[] offset = parseArray((JSONArray) module.get("offset"));
			for(Object boxObj : boxes.toArray()) {
				double[] relativeBox = parseArray((JSONArray) boxObj);
				JSONArray absBox = new JSONArray();
				for(int i = 0; i < relativeBox.length; i++) {
					absBox.add(relativeBox[i] + offset[i % 3]);										
				}
				if(absBox.size() > 0) {
					sequence.add(absBox);
				}
			}
		}
		
		keepin.put("safe", true);
		keepin.put("sequence", sequence);
		keepin.put("name", "ISS_US_FREEDOM");
		keepin.put("author", "chitt");
		keepin.put("dateCreated", Instant.now().getEpochSecond() + "");
		keepin.put("dateModified", Instant.now().getEpochSecond() + "");
		keepin.put("notes", "Go here.");
		
		return keepin;
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