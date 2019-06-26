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

import gov.nasa.arc.irg.plan.model.modulebay.Bay;
import gov.nasa.arc.irg.plan.model.modulebay.Point3D;

import java.util.List;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.scenegraph.shape.Quad;

public class ModulePlane extends Node {
	
	private float[] hc = new float[]{ (191.0f/255.0f), (213.0f/225.0f), (232.0f/255.0f) };
	private float alpha = 0.5f;
	protected Quad quadDown, quadUp;
	protected String quadDownName = "planeDown";
	protected String quadUpName = "planeUp";
	private ModuleGrid grid;	
	private MaterialState matState;
	
	public ModulePlane(String name, List<Bay> bays, Point3D[] dividers, Point3D offset, double[][] keepins) {
		super(name);
		
		if(bays.size() == 0) {
			return;
		}
		
		grid = new ModuleGrid(name + "Grid");
		attachChild(grid);
		
		matState = makeMaterialState(hc);
		
		createPlane(bays, offset.toArray(), keepins);
		createGrid(bays, dividers, offset.toArray(), keepins);
	}
	
	private void createPlane(List<Bay> bays, double[] offset, double[][] keepins) { 		
		for(double[] box : keepins) {
			if(box.length == 0) {
				continue;
			}
			
			double length = box[3] - box[0];
			double height = box[4] - box[1];
			double x = (box[3] + box[0]) / 2 + offset[0];
			double y = (box[4] + box[1]) / 2 + offset[1];
			makeQuads(x, y, length, height);
		}
	}
	
	private void createGrid(List<Bay> bays, Point3D[] dividers, double[] offset, double[][] keepins) {
		if(bays.size() == 0) {
			return;
		}
		
		boolean xAligned = isXAligned(bays);
		makeCenterLine(dividers, offset, xAligned);
				
		int index = 0;
		for(int i = 0; i < dividers.length; i ++) {
			if(!isValid(dividers[i])) {
				continue;
			}
			
			double[] one;
			double[] two;
			if(index <= 0) {
				one = bays.get(index).getBounds();
				two = bays.get(index).getBounds();
			} else if(index >= bays.size()) {
				one = bays.get(index - 1).getBounds();
				two = bays.get(index - 1).getBounds();
			} else {
				one = bays.get(index - 1).getBounds();
				two = bays.get(index).getBounds();
			}
			
			
			if(xAligned) {
				double x = dividers[i].getX() + offset[0];
				double yMin = Math.min(one[2], two[2]);
				double yMax = Math.max(one[3], two[3]);
				grid.addLine(new Vector3(x, yMin, 0), new Vector3(x, yMax, 0));
			} else {
				double y = dividers[i].getY() + offset[1];
				double xMin = Math.min(one[0], two[0]);
				double xMax = Math.max(one[1], two[1]);
				grid.addLine(new Vector3(xMin, y, 0), new Vector3(xMax, y, 0));
			}
			
			index ++;
		}
	}
	
	private void makeCenterLine(Point3D[] dividers, double[] offset, boolean xAligned) {
		if(xAligned) {
			double y = offset[1];
			
			double xMin = dividers[0].getX() + offset[0];
			double xMax = dividers[0].getX() + offset[0];
			for(Point3D divider : dividers) {
				double x = divider.getX() + offset[0];				
				if(x < xMin) {
					xMin = x;
				}				
				if(x > xMax) {
					xMax = x;
				}
			}
			
			grid.addLine(new Vector3(xMin, y, 0), new Vector3(xMax, y, 0));
		} else {
			double x = offset[0];
			
			double yMin = dividers[0].getY() + offset[1];
			double yMax = dividers[0].getY() + offset[1];
			for(Point3D divider : dividers) {
				double y = divider.getY() + offset[1];				
				if(y < yMin) {
					yMin = y;
				}				
				if(y > yMax) {
					yMax = y;
				}
			}
			
			grid.addLine(new Vector3(x, yMin, 0), new Vector3(x, yMax, 0));
		}
	}
	
	private boolean isValid(Point3D divider) {
		for(double i : divider.toArray()) {
			if(i != -1) {
				return true;
			}
		}
		return false;
	}
	
	private void makeQuads(double x, double y, double length, double height) {
		Quad quadUp = new Quad("QuadUp" + x + "x" + y, length, height);
		Quad quadDown = new Quad("QuadDown" + x + "x" + y, length, height);
		
		quadUp.setRenderState(matState);
		quadDown.setRenderState(matState);
		
		Transform xfm = new Transform();
		Matrix3 rot = new Matrix3();
		rot.fromAngles(Math.PI, 0,0);
		xfm.setRotation(rot);
		quadUp.setTransform(xfm);

		movePlane(quadUp, quadDown, x, y, 0);
		attachChild(quadUp);
		attachChild(quadDown);
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
	
	public void movePlane(Quad quadUp, Quad quadDown, double x, double y, double z) {
		Matrix3 rotation = new Matrix3();
		rotation.fromAngles(Math.PI, 0,0);
		
		Transform xfmUp = new Transform();
		xfmUp.setTranslation(x,y,z);
		xfmUp.setRotation(rotation);
		quadUp.setTransform(xfmUp);
		quadUp.updateModelBound();

		Transform xfmDown = new Transform();
		xfmDown.setTranslation(x,y,z);
		quadDown.setTransform(xfmDown);
		quadDown.updateModelBound();
	}
	
	private boolean isXAligned(List<Bay> bays) {
		double[] one = bays.get(0).getCenter().toArray();
		double[] two = bays.get(1).getCenter().toArray();
		
		if(one[0] != two[0]) {
			return true;
		}
		
		return false;
	}

	public void showPlane() {
		for(Spatial child : getChildren()) {
			child.getSceneHints().setCullHint(CullHint.Inherit);
		}		
	}

	public void hidePlane() {
		for(Spatial child : getChildren()) {
			child.getSceneHints().setCullHint(CullHint.Always);
		}
	}
	
	protected double roundToFiveCm(double d) {
		int bigD = (int) (d * 100);
		int remainder = bigD % 5;
		if(0 < remainder) {
			if(remainder < 2.5) {
				bigD -= remainder;
			} else {
				bigD += (5 - remainder);
			}
		} else {
			if(remainder < -2.5) {
				bigD -= (5 + remainder);
			} else {
				bigD -= remainder;
			}
		}
		double ret = bigD/100.0;

		return ret;
	}	
}
