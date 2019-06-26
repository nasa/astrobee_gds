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

import org.apache.log4j.Logger;

import gov.nasa.arc.verve.freeflyer.workbench.scenario.ColoredBoxList.ColoredBox;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.shape.Box;

public class ColoredBoxListNode extends Node {
	private static final Logger logger = Logger.getLogger(ColoredBoxListNode.class);
	private ColoredBoxList coloredBoxes;
	private float alpha = 1f;
	private Box[] boxes;

	public ColoredBoxListNode() {
		super("ColoredBoxesList");
		try {
			coloredBoxes = ColoredBoxListLoader.getColoredBoxes();
		} catch (Exception e) {
			logger.error("Error reading Colored Box Config file");
		}

		makeTheBoxes();
	}

	private void makeTheBoxes() {
		boxes = new Box[coloredBoxes.size()];
		int i=0;
		for(ColoredBox box : coloredBoxes.getColoredBoxes()) {

			float[] pos = box.getPosition();
			boxes[i] = new Box(box.getName(),
					new Vector3(pos[0],pos[1],pos[2]),
					new Vector3(pos[3],pos[4],pos[5]));
			boxes[i].setRenderState(makeMaterialState(box.getColor()));
			attachChild(boxes[i]);
			i++;
		}
	}

	private MaterialState makeMaterialState(float[] color) {
		float diff = 0.5f; // was 0.2
		float spec = 0.0f; // was 1.0
		float emis = 0.5f; // was 0.3
		float ambt = (spec+diff)/4;

		MaterialState mtl = new MaterialState();
		mtl.setShininess(5); // was 5
		mtl.setDiffuse (new ColorRGBA(diff*color[0], diff*color[1], diff*color[2], alpha ));
		mtl.setSpecular(new ColorRGBA(spec*color[0], spec*color[1], spec*color[2], alpha ));
		mtl.setEmissive(new ColorRGBA(emis*color[0], emis*color[1], emis*color[2], alpha ));
		mtl.setAmbient (new ColorRGBA(ambt*color[0], ambt*color[1], ambt*color[2], alpha ));
		return mtl;
	}

}
