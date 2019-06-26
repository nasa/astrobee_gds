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
package gov.nasa.arc.verve.freeflyer.workbench.parts.keepouteditor;

import gov.nasa.arc.irg.plan.json.JsonBox;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class KeepoutBuilder extends JsonBox {
	private KeepoutModelingNode modeler;
	private File file;
	private List<KeepoutBox> keepouts;
	
	public KeepoutBuilder(File file) {
		super(file);
		this.file = file;
		modeler = KeepoutModelingNode.getStaticInstance();
		keepouts = new ArrayList<KeepoutBox>();
		init();
	}
	
	private void init() {
		if(boxes == null) {
			return;
		}
		for(double[] box : boxes) {
			keepouts.add(new KeepoutBox("KeepoutBox" + keepouts.size(), box));
		}
	}
	
	public List<KeepoutBox> getKeepouts() {
		return keepouts;
	}
	
	public String getName() {
		return name;
	}
	
	public void addKeepout(KeepoutBox keepout) {
		keepouts.add(keepout);
		modeler.addBox(keepout);
	}
	
	public void addKeepout(double[] bounds) {
		KeepoutBox keepout = new KeepoutBox("KeepoutBox" + keepouts.size(), bounds);
		addKeepout(keepout);
	}
	
	public void deleteKeepout(KeepoutBox keepout) {
		for(int i = 0; i < keepouts.size(); i++) {
			if(keepout.equals(keepouts.get(i))) {
				keepouts.remove(i);
				break;
			}
		}
		
		modeler.deleteBox(keepout);
	}
	
	public void cancelTransform() {
		KeepoutBox[] transform = modeler.cancelChanges();
		deleteKeepout(transform[0]);
		addKeepout(transform[1]);
	}
	
	public boolean saveToFile() {
		dateModified = Instant.now().getEpochSecond() + "";
		
		JSONObject keepout = new JSONObject();
		keepout.put("name", name);
		keepout.put("safe", false);
		keepout.put("author", author);
		keepout.put("dateCreated", dateCreated);
		keepout.put("dateModified", dateModified);
		keepout.put("notes", notes);
		keepout.put("sequence", createKeepoutSequence());
		
		try {
			PrintWriter writer = new PrintWriter(file);
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
		
			String listAsString = mapper.writeValueAsString(keepout);
		
			writer.println(listAsString);

			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private JSONArray createKeepoutSequence() {
		JSONArray sequence = new JSONArray();		
		for(KeepoutBox box : keepouts) {
			JSONArray item = new JSONArray();
			for(double point : box.getBounds()) {
				item.add(point);
			}
			sequence.add(item);
		}
		
		return sequence;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	public void setDateModified(String dateModified) {
		this.dateCreated = dateModified;
	}
	
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public void setSafe(String safe) {
		this.safe = false;
	}
	
	public void setKeepouts(List<KeepoutBox> keepouts) {
		this.keepouts = keepouts;
	}
}
