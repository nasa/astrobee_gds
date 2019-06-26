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
package gov.nasa.arc.irg.plan.json;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Object to parse and store the data from a json keepout zone file
 * @author DW
 *
 */
public class JsonBox {
	private static Logger logger = Logger.getLogger(JsonBox.class);

	protected String name;
	protected String author;
	protected String dateCreated;
	protected String dateModified;
	protected String notes;
	protected boolean safe;
	
	protected JSONObject jsonObject;
	protected List<double[]> boxes;
	protected String shortname;
	
	protected URL fileURL;
	
	public JsonBox(String urlName) {
		try {
			fileURL = new URL( urlName );
			initializeFromUrl();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public JsonBox(File file) {
		if(file.exists()) {
			try {
				fileURL = file.toURI().toURL();
				initializeFromUrl();
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
	}
		
	private void initializeFromUrl() {
		JSONParser parser = new JSONParser();
		try {
			InputStream is = fileURL.openConnection().getInputStream();
			Object obj = parser.parse(new InputStreamReader(is));
			
			jsonObject = (JSONObject) obj;

			findTheShortName(fileURL.getPath());
			fillStringFields();

			makeTheBoxes();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void makeTheBoxes() {
		JSONArray seq = (JSONArray) jsonObject.get("sequence");
		boxes = new ArrayList<double[]>();
		for(int i=0; i<seq.size(); i++) {
			JSONArray jsonBox = (JSONArray) seq.get(i);
			double[] box = BoxReadingUtility.readBoxFromJsonArray(jsonBox);
			boxes.add(box);
		}
	}
	
	public void printBoxes() {
		System.out.println(getName() + ":");
		System.out.println("\tsafe: " + safe);
		System.out.println("\tboxes:");
		for(int i=0; i<boxes.size(); i++) {
			for(int j=0; j<BoxReadingUtility.POINTS_IN_A_BOX; j++) {
				System.out.print(boxes.get(i)[j] + ", ");
			}
			System.out.println();
		}
	}
	
	public List<double[]> getBoxes() {
		return boxes;
	}
	
	public boolean isSafe() {
		return safe;
	}
	
	public String getName() {
		if(name != null) {
			return name;
		}
		return shortname;
	}
		
	public String getAuthor() {
		return author;
	}
	
	public String getDateCreated() {
		return dateCreated;
	}
	
	public String getDateModified() {
		return dateModified;
	}
	
	public String getNotes() {
		return notes;
	}
	
	private void findTheShortName(String filepath) {
		String[] pathparts = filepath.split("/");
		int numparts = pathparts.length;
		String filename = pathparts[numparts - 1]; 
		String[] nameParts = filename.split("\\.");
		shortname = nameParts[0];
	}

	private void fillStringFields() {
		safe = (boolean) jsonObject.get("safe");
		
		name = (String) jsonObject.get("name");
		author = (String) jsonObject.get("author");
		dateCreated = (String) jsonObject.get("dateCreated");
		dateModified = (String) jsonObject.get("dateModified");
		notes = (String) jsonObject.get("notes");
	}
}
