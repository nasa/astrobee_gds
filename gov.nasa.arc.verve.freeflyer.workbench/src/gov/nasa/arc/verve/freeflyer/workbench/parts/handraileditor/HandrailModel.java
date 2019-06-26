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

import gov.nasa.arc.irg.plan.model.modulebay.LocationMap.Wall;
import gov.nasa.arc.irg.util.bean.IHasPropertyChangeListeners;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ardor3d.extension.model.collada.jdom.data.AssetData;
import com.ardor3d.extension.model.collada.jdom.data.ColladaStorage;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.type.ReadOnlyMatrix3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.RenderState.StateType;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.util.resource.ResourceLocator;
import com.ardor3d.util.resource.ResourceSource;
import com.ardor3d.util.resource.URLResourceSource;

public class HandrailModel extends Mesh implements IHasPropertyChangeListeners, Cloneable {
	
	private String filepath;
	private Wall wall;
	private boolean isHorizontal;
	private double x, y, z;
	
	private MaterialState def, highlight;
	private Float[] highlightColor = new Float[] {1f, 1f, 0f};
	private float alpha = 1.0f;
	private float diff = 0.5f; // was 0.2
	private float spec = 0.0f; // was 1.0
	private float emis = 0.5f; // was 0.3
	private float ambt = (spec+diff)/4;
	
	private final double DEG_TO_RAD = Math.PI / 180;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private DecimalFormat formatter = new DecimalFormat("#.##");
	
	public HandrailModel(String filepath, double[] pos, Wall wall, boolean isHorizontal) {
		super("HandrailModel");
		
		formatter.setRoundingMode(RoundingMode.HALF_UP);
		load(filepath);
		setPosition(pos);
		setWall(wall);
		setIsHorizontal(isHorizontal);
		updateModelBound();
		
		def = (MaterialState) getLocalRenderState(StateType.Material);
		makeHighlightMaterial();
	}
	
	private void load(String filepath) {
		this.filepath = filepath;
		try {
			com.ardor3d.extension.model.collada.jdom.ColladaImporter colladaImporter;
			colladaImporter             = getImporter(null);
			
			URL modelUrl = new URL(filepath);
				
			ResourceSource fileResource = new URLResourceSource(modelUrl);
			ColladaStorage storage = colladaImporter.load(fileResource);
			
			Node handrail = processStorage(storage);
			Mesh handrailMesh = findMesh(handrail);
			copyMesh(handrailMesh);
		} catch (Throwable t) {
			IOException e =  new IOException("Failed to load COLLADA file");
			e.initCause(t);
			e.printStackTrace();
		}	
	}
	
	private Mesh findMesh(Node handrail) throws Throwable {
		for(Spatial child : handrail.getChildren()) {
			if(child instanceof Node) {
				return findMesh((Node) child);
			} else if (child instanceof Mesh) {
				return (Mesh) child;
			}
		}
		throw new Throwable("Could not locate mesh in COLLADA resource. "
				+ "This method makes assumptions about imported handrail structure.");
	}
	
	private void copyMesh(Mesh handrailMesh) {
		//is this everything we need?
		setModelBound(handrailMesh.getModelBound());
		setMeshData(handrailMesh.getMeshData());
		setDefaultColor(handrailMesh.getDefaultColor());
		setRenderState(handrailMesh.getLocalRenderState(StateType.Material));
		setLightState(handrailMesh.getLightState());
	}
	
	public double[] getAngles(Wall wall) {
		switch(wall) {
			case FWD:
				if(isHorizontal) {
					return new double[] {90, -90, 0};
				} else {
					return new double[] {-90, 90, 90};
				}
			case AFT:
				if(isHorizontal) {
					return new double[] {90, 90, 0};
				} else {
					return new double[] {90, 90, 90};
				}
			case PORT:
				if(isHorizontal) {
					return new double[] {0, 90, 0};
				} else {
					return new double[] {0, 0, 0};
				}
			case STBD:
				if(isHorizontal) {
					return new double[] {180, 90, 0};
				} else {
					return new double[] {180, 0, 0};
				}
			case OVHD:
				if(isHorizontal) {
					return new double[] {0, 90, 90};
				} else {
					return new double[] {90, 0, 0};
				}
			case DECK:
				if(isHorizontal) {
					return new double[] {0, -90, 90};
				} else {
					return new double[] {90, 180, 0};
				}
			default:
				System.err.println("Invalid wall in HandrailModel");
				return null;
		}
	}
	
	public void setHighlighted(boolean highlighted) {
		if(highlighted) {
			setRenderState(highlight);
		} else {
			setRenderState(def);
		}
	}
	
	private void makeHighlightMaterial() {			
		highlight = new MaterialState();
		highlight.setShininess(5); // was 5
		highlight.setDiffuse (new ColorRGBA(diff*highlightColor[0], diff*highlightColor[1], diff*highlightColor[2], alpha ));
		highlight.setSpecular(new ColorRGBA(spec*highlightColor[0], spec*highlightColor[1], spec*highlightColor[2], alpha ));
		highlight.setEmissive(new ColorRGBA(emis*highlightColor[0], emis*highlightColor[1], emis*highlightColor[2], alpha ));
		highlight.setAmbient (new ColorRGBA(ambt*highlightColor[0], ambt*highlightColor[1], ambt*highlightColor[2], alpha ));
	}
	
	public void setRotation(double[] angles) {
		Matrix3 rot = new Matrix3();
		rot.fromAngles(angles[0] * DEG_TO_RAD, angles[1] * DEG_TO_RAD, angles[2] * DEG_TO_RAD);
		setRotation(rot);
		updateModelBound();
	}
	
	public double[] getPosition() {
		return new double[] {x, y, z};
	}
	
	public void setPosition(double[] pos) {
		double[] oldPos = new double[] {x, y, z};		
		x = round(pos[0]);
		y = round(pos[1]);
		z = round(pos[2]);
		
		translate();
		firePropertyChange("position", oldPos, pos);
	}
	
	private void translate() {
		setTranslation(x, y, z);
		updateModelBound();
	}
	
	public Wall getWall() {
		return wall;
	}
	
	public void setWall(Wall wall) {
		Wall oldWall = this.wall;		
		this.wall = wall;
		double[] angles = getAngles(wall);
		setRotation(angles);
		firePropertyChange("wall", oldWall, this.wall);
	}
	
	public boolean getIsHorizontal() {
		return isHorizontal;
	}
	
	public void setIsHorizontal(boolean isVertical) {
		boolean oldHorizontal = this.isHorizontal;
		this.isHorizontal = isVertical;
		double[] angles = getAngles(wall);
		setRotation(angles);
		firePropertyChange("horizontal", oldHorizontal, this.isHorizontal);
	}
	
	private double round(double num) {
		return Double.parseDouble(formatter.format(num));
	}
	
	private com.ardor3d.extension.model.collada.jdom.ColladaImporter getImporter(Map<String,Object> params) {
		com.ardor3d.extension.model.collada.jdom.ColladaImporter colladaImporter;
		colladaImporter = new com.ardor3d.extension.model.collada.jdom.ColladaImporter();
		ResourceLocator textureLocator = null;
		ResourceLocator modelLocator = null;
		if(params != null) {
			textureLocator = (ResourceLocator)params.get("textureLocator");
			modelLocator = (ResourceLocator)params.get("modelLocator");
		}
		if(textureLocator != null) {
			colladaImporter.setTextureLocator(textureLocator);
		}
		if(modelLocator != null) {
			colladaImporter.setModelLocator(modelLocator);
		}
		colladaImporter.setOptimizeMeshes(true);
		return colladaImporter;
	}
	
	private Node processStorage(ColladaStorage storage) {
		Node model = storage.getScene();
		AssetData assetData = storage.getAssetData();
		double unitMeter = assetData.getUnitMeter();
		if(unitMeter != 0 && unitMeter != 1) {
			model.setScale(unitMeter, unitMeter, unitMeter);
		}
		return model;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof HandrailModel) {
			HandrailModel hm = (HandrailModel) o;
						
			if(!Arrays.equals(hm.getPosition(), getPosition())) {
				return false;
			}
			
			if(!hm.getWall().equals(wall)) {
				return false;
			}
			
			if(hm.isHorizontal != isHorizontal) {
				return false;
			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 13;
		hash = hash * 17 + wall.hashCode();
		hash = hash * 31 + (isHorizontal ? 1 : 0);
		hash = hash * 7 + getPosition().hashCode();
		return hash;
	}
	
	@Override
	public String toString() {
		return "Handrail: ["+ x + ", " + y + ", " + z + "], " + wall + ", " + isHorizontal;
	}
	
	@Override
	public HandrailModel clone() {
		return new HandrailModel(filepath, getPosition(), getWall(), getIsHorizontal());
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public void setX(double x) {
		if(round(this.x) == x) {
			return;
		}
		
		setPosition(new double[] {x, y, z});
	}
	
	public void setY(double y) {
		if(round(this.y) == y) {
			return;
		}
		
		setPosition(new double[] {x, y, z});
	}
	
	public void setZ(double z) {
		if(round(this.z) == z) {
			return;
		}
		
		setPosition(new double[] {x, y, z});
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	@Override
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);		
	}

	@Override
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);		
	}

	@Override
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);		
	}
}
