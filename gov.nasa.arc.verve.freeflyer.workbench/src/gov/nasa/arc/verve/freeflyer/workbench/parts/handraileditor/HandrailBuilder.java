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
package gov.nasa.arc.verve.freeflyer.workbench.parts.handraileditor;

import gov.nasa.arc.irg.plan.model.modulebay.LocationMap;
import gov.nasa.arc.irg.plan.model.modulebay.LocationMap.Wall;
import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.eclipse.core.runtime.FileLocator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class HandrailBuilder {
	private String basePath = "platform:/plugin/gov.nasa.arc.verve.robot.freeflyer/models/";
	private String handleFileName = "new_handle.dae";
	File file;
	URL fileURL;
	JSONArray jArray;
	String shortname;
	
	HandrailModelingNode modeler = HandrailModelingNode.getStaticInstance();
	List<HandrailModel> handrails;
	
	public HandrailBuilder(File file) {
		handrails = new ArrayList<HandrailModel>();
		
		if(file.exists()) {
			try{
				fileURL = file.toURI().toURL();
				String worldFolder = WorkbenchConstants.getWorldFolderName();
				basePath = basePath + "/" + worldFolder + "/";
				parseJsonFile();
			} catch (MalformedURLException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		} else {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		this.file = file;
		
		try {
			this.fileURL = file.toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public HandrailBuilder(URL url) {
		handrails = new ArrayList<HandrailModel>();
		fileURL = url;
		try {
			file = new File(FileLocator.resolve(fileURL).toURI());
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed to connect to HandrailConfiguration.json, saving may not work correctly.");
		} 
		parseJsonFile();
	}
	
	public List<HandrailModel> getHandrails() {
		return handrails;
	}
	
	private void parseJsonFile() {
		JSONParser parser = new JSONParser();
		try {
			InputStream is = fileURL.openConnection().getInputStream();
			Object obj = parser.parse(new InputStreamReader(is));
			
			jArray = (JSONArray) obj;
			
			findTheShortName(fileURL.getPath());
			loadHandrails();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void loadHandrails() {
		for(Object obj : jArray.toArray()) {
			JSONObject jObj = (JSONObject) obj;
			
			String fileName = (String) jObj.get("file");
			double[] pos = parseArray((JSONArray) jObj.get("position"));
			Wall wall = LocationMap.getInstance().getWall((String) jObj.get("wall"));
			boolean isHorizontal = (boolean) jObj.get("horizontal");
			
			addHandrail(fileName, pos, wall, isHorizontal);
		}
	}
	
	public boolean saveToFile() {
		JSONArray output = new JSONArray();
		for(HandrailModel handrail : handrails) {
			JSONObject obj = new JSONObject();
			obj.put("file", handleFileName);
			obj.put("position", handrail.getPosition());
			obj.put("wall", handrail.getWall().toString());
			obj.put("horizontal", handrail.getIsHorizontal());
			output.add(obj);
		}
		
		try {
			PrintWriter writer = new PrintWriter(file);
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
		
			String listAsString = mapper.writeValueAsString(output);
		
			writer.println(listAsString);

			writer.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
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
	
	public void addHandrail(String fileName, double[] pos, Wall wall, boolean isVertical) {
		HandrailModel handrail = new HandrailModel(basePath + fileName, pos, wall, isVertical);
		addHandrail(handrail);
	}
	
	public void addHandrail(HandrailModel handrail) {
		handrails.add(handrail);
		modeler.addHandrail(handrail);
	}
	
	public void deleteHandrail(HandrailModel handrail) {
		handrails.remove(handrail);
		modeler.deleteHandrail(handrail);
	}
	
	public void cancelTransform() {
		HandrailModel[] transform = modeler.cancelChanges();
		deleteHandrail(transform[0]);
		addHandrail(transform[1]);
	}
	
	public void addGenericHandrail(double[] pos) {
		addHandrail(handleFileName, pos, Wall.DECK, false);
	}
		
	private void findTheShortName(String filepath) {
		String[] pathparts = filepath.split("/");
		int numparts = pathparts.length;
		String filename = pathparts[numparts - 1]; 
		String[] nameParts = filename.split("\\.");
		shortname = nameParts[0];
	}
	
}
