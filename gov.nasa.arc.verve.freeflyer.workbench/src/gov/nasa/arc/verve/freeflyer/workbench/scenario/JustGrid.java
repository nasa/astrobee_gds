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

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.scenegraph.Line;
import com.ardor3d.scenegraph.Node;

public class JustGrid extends Node {
	private double m_width, m_height;
	private int m_warpCount, m_weftCount;
	private double m_spacing;
	protected ReadOnlyColorRGBA m_color;
	private float m_lineWidth;
	
	public JustGrid(double width, double height, double spacing, float lineWidth) {
		super("JustGrid" + spacing);
		m_width = width;
		m_height = height;
		m_spacing = spacing;
		m_warpCount = (int) (m_width/m_spacing) + 1;
		m_weftCount = (int) (m_height/m_spacing) + 1;
		m_color = ColorRGBA.WHITE;
		m_lineWidth = lineWidth;
		init();
	}

	public void init() {
		Vector3 vert1, vert2;
		Vector3[] lineEnds;
		Line line;
		double xOffset = m_width/2;
		double yOffset = m_height/2;
		
		for(int i=0; i<m_weftCount; i++) {
			vert1 = new Vector3(-xOffset, i*m_spacing - yOffset, 0);
			vert2 = new Vector3( xOffset, i*m_spacing - yOffset, 0);
			
			lineEnds = new Vector3[]{vert1, vert2};
			line = new Line("weft" + i, lineEnds, null, null, null);
			line.setDefaultColor(m_color);
			line.setLineWidth(m_lineWidth);
			attachChild(line);
		}

		
		for(int j=0; j<m_warpCount; j++) {
			vert1 = new Vector3(j*m_spacing - xOffset, -yOffset, 0);
			vert2 = new Vector3(j*m_spacing - xOffset,  yOffset, 0);
			
			lineEnds = new Vector3[]{vert1, vert2};
			line = new Line("warp" + j, lineEnds, null, null, null);
			line.setDefaultColor(m_color);
			line.setLineWidth(m_lineWidth);
			attachChild(line);
		}
	}
}
