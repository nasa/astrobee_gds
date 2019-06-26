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

import java.util.ArrayList;
import java.util.List;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.math.type.ReadOnlyVector2;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Line;
import com.ardor3d.scenegraph.Node;

public class ModuleGrid extends Node {
	
	protected ReadOnlyColorRGBA m_color;
	protected float m_lineWidth = 3;
	private int lineNum = 0;
	private String name;
	
	private List<GridLine> lines;
	
	public ModuleGrid(String name) {
		super(name);
		this.name = name;
		lines = new ArrayList<GridLine>();
		m_color = ColorRGBA.WHITE;
	}
	
	public void addLine(Vector3 start, Vector3 end) {
		GridLine line = new GridLine(name + "Line" + lineNum, 
							 new Vector3[] {start, end}, 
							 null, null, null);
		line.setDefaultColor(m_color);
		line.setLineWidth(m_lineWidth);
		lines.add(line);
		attachChild(line);
	}
	
	public float[] getYExtent() {
		float[] yExtent = new float[] {Float.MAX_VALUE, Float.MIN_VALUE};
		for(GridLine line : lines) {
			ReadOnlyVector3[] vertices = line.getVertices();
			for(ReadOnlyVector3 vertex : vertices) {
				if(vertex.getY() < yExtent[0]) {
					yExtent[0] = vertex.getYf();
				}
				if(vertex.getY() > yExtent[1]) {
					yExtent[1] = vertex.getYf();
				}
			}
		}
		return yExtent;
	}
	
	public float[] getXExtent() {
		float[] xExtent = new float[] {Float.MAX_VALUE, Float.MIN_VALUE};
		for(GridLine line : lines) {
			ReadOnlyVector3[] vertices = line.getVertices();
			for(ReadOnlyVector3 vertex : vertices) {
			//	System.out.println("Vertex: " + vertex);
				if(vertex.getX() < xExtent[0]) {
					xExtent[0] = vertex.getXf();
				}
				if(vertex.getX() > xExtent[1]) {
					xExtent[1] = vertex.getXf();
				}
			}
		}
		return xExtent;
	}
	
	class GridLine extends Line {
		protected ReadOnlyVector3[] vertices;
		protected ReadOnlyVector3 end;
		
		public GridLine(final String name, final ReadOnlyVector3[] vertex, final ReadOnlyVector3[] normal,
	            final ReadOnlyColorRGBA[] color, final ReadOnlyVector2[] texture) {
			super(name, vertex, normal, color, texture);
			vertices = vertex;
		}
		
		public ReadOnlyVector3[] getVertices() {
			return vertices;
		}
	}
}
