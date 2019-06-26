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
package gov.nasa.arc.verve.freeflyer.workbench.scenario;

import gov.nasa.arc.irg.plan.json.JsonBox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.shape.Box;

public class BoxesNode extends Node {
	private List<JsonBox> jsonBoxes = new ArrayList<JsonBox>();
	private Box[] boxes;
	private String shortname;
	private MaterialState mtl;
	private float alpha = 1f;
	private float grayLevel = 125f;
	private Float[] hc = new Float[] {(grayLevel/255f), (grayLevel/255f), (grayLevel/255f)};

	// Never called
//	public BoxesNode(String urlString) {
//		jsonBoxes.add(new JsonBox(urlString));
//		makeMaterialState();
//		makeTheBoxes();
//	}

	/** Node with all the keepout zones specified in files in keepouts folder */
	public BoxesNode(String nodeName, File[] boxFiles) { 
		super(nodeName);
		//File[] keepoutFiles = ConfigFileWrangler.getInstance().getKeepoutFiles();
		for(int i=0; i<boxFiles.length; i++) {
			jsonBoxes.add(new JsonBox(boxFiles[i]));
		}
		makeMaterialState();
		makeTheBoxes();
	}
	
	public BoxesNode(String nodeName, File[] boxFiles, int[] color) { 
		super(nodeName);
		//File[] keepoutFiles = ConfigFileWrangler.getInstance().getKeepoutFiles();
		for(int i=0; i<boxFiles.length; i++) {
			jsonBoxes.add(new JsonBox(boxFiles[i]));
		}
		hc = new Float[] {(color[0]/255f), (color[1]/255f), (color[2]/255f)};
		makeMaterialState();
		makeTheBoxes();
	}

	private void makeTheBoxes() {

		for(JsonBox box : jsonBoxes) {
			List<double[]> seq = box.getBoxes();
			shortname = box.getName();
			boxes = new Box[seq.size()];
			for(int i=0; i<seq.size(); i++) {
				double[] oneBox = seq.get(i);

				boxes[i] = new Box(shortname + i,
						new Vector3(oneBox[0],oneBox[1],oneBox[2]),
						new Vector3(oneBox[3],oneBox[4],oneBox[5]));
				boxes[i].setRenderState(mtl);
				attachChild(boxes[i]);
			}
		}
	}

	private void makeMaterialState() {
		float diff = 0.5f; // was 0.2
		float spec = 0.0f; // was 1.0
		float emis = 0.5f; // was 0.3
		float ambt = (spec+diff)/4;

		mtl = new MaterialState();
		mtl.setShininess(5); // was 5
		mtl.setDiffuse (new ColorRGBA(diff*hc[0], diff*hc[1], diff*hc[2], alpha ));
		mtl.setSpecular(new ColorRGBA(spec*hc[0], spec*hc[1], spec*hc[2], alpha ));
		mtl.setEmissive(new ColorRGBA(emis*hc[0], emis*hc[1], emis*hc[2], alpha ));
		mtl.setAmbient (new ColorRGBA(ambt*hc[0], ambt*hc[1], ambt*hc[2], alpha ));
	}
}