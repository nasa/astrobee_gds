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

import gov.nasa.arc.irg.util.bean.IHasPropertyChangeListeners;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyMatrix3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.shape.Box;

public class KeepoutBox extends Box implements IHasPropertyChangeListeners, Cloneable {
	MaterialState def;
	MaterialState highlight;
	
	private Float[] orange = new Float[] {(128f/255f), (128f/255f), (128f/255f)};
	private Float[] highlightColor = new Float[] {1.0f, 1.0f, 0.0f};
	
	private float alpha = 1.0f;
	private float diff = 0.5f; // was 0.2
	private float spec = 0.0f; // was 1.0
	private float emis = 0.5f; // was 0.3
	private float ambt = (spec+diff)/4;
	
	private double x, y, z;
	private double p1x, p1y, p1z;
	private double p2x, p2y, p2z;
	
	private DecimalFormat formatter = new DecimalFormat("#.##");
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	public KeepoutBox(String name) {
		super(name);
	}
	
	public KeepoutBox(String name, ReadOnlyVector3 p1, ReadOnlyVector3 p2) {
		super(name);
		transform(p1, p2);
		makeDefaultMaterial();
		makeHighlightMaterial();
		setRenderState(def);
		formatter.setRoundingMode(RoundingMode.HALF_UP);
	}
	
	public KeepoutBox(String name, double[] bounds) {
		this(name, new Vector3(bounds[0], bounds[1], bounds[2]),
				new Vector3(bounds[3], bounds[4], bounds[5]));
	}
	
	private void transform(ReadOnlyVector3 p1, ReadOnlyVector3 p2) {
		double[] oldBounds = new double[] { p1x, p1y, p1z, p2x, p2y, p2z };
		p1x = round(Math.min(p1.getX(), p2.getX()));
		p1y = round(Math.min(p1.getY(), p2.getY()));
		p1z = round(Math.min(p1.getZ(), p2.getZ()));
		p2x = round(Math.max(p1.getX(), p2.getX()));
		p2y = round(Math.max(p1.getY(), p2.getY()));
		p2z = round(Math.max(p1.getZ(), p2.getZ()));
		x = round((p1.getX() + p2.getX()) / 2);
		y = round((p1.getY() + p2.getY()) / 2);
		z = round((p1.getZ() + p2.getZ()) / 2);
		setData(p1, p2);
		updateModelBound();
		firePropertyChange("bounds", oldBounds, getBounds());
	}
	
	private double round(double in) {
		return Double.parseDouble(formatter.format(in));
	}
	
	private void transform(ReadOnlyVector3 center) {
		double xExtent = Math.abs((p1x - p2x) / 2);
		double yExtent = Math.abs((p1y - p2y) / 2);
		double zExtent = Math.abs((p1z - p2z) / 2);
		Vector3 p1 = new Vector3(center.getX() - xExtent,
								 center.getY() - yExtent,
								 center.getZ() - zExtent);
		Vector3 p2 = new Vector3(center.getX() + xExtent,
								 center.getY() + yExtent,
								 center.getZ() + zExtent);
		transform(p1, p2);
	}

	public void setHighlight(boolean state) {
		if(state) {
			setRenderState(highlight);
		} else {
			setRenderState(def);
		}
	}
	
	public void setBounds(ReadOnlyVector3 p1, ReadOnlyVector3 p2) {
		transform(p1, p2);
	}
	
	public void setBounds(double[] bounds) {
		Vector3 p1 = new Vector3(bounds[0], bounds[1], bounds[2]);
		Vector3 p2 = new Vector3(bounds[3], bounds[4], bounds[5]);
		transform(p1, p2);
	}
	
	public double[] getBounds() {
		return new double[] { p1x, p1y, p1z, p2x, p2y, p2z };
	}
	
	private void makeDefaultMaterial() {			
		def = new MaterialState();
		def.setShininess(5); // was 5
		def.setDiffuse (new ColorRGBA(diff*orange[0], diff*orange[1], diff*orange[2], alpha ));
		def.setSpecular(new ColorRGBA(spec*orange[0], spec*orange[1], spec*orange[2], alpha ));
		def.setEmissive(new ColorRGBA(emis*orange[0], emis*orange[1], emis*orange[2], alpha ));
		def.setAmbient (new ColorRGBA(ambt*orange[0], ambt*orange[1], ambt*orange[2], alpha ));
	}
	
	private void makeHighlightMaterial() {			
		highlight = new MaterialState();
		highlight.setShininess(5); // was 5
		highlight.setDiffuse (new ColorRGBA(diff*highlightColor[0], diff*highlightColor[1], diff*highlightColor[2], alpha ));
		highlight.setSpecular(new ColorRGBA(spec*highlightColor[0], spec*highlightColor[1], spec*highlightColor[2], alpha ));
		highlight.setEmissive(new ColorRGBA(emis*highlightColor[0], emis*highlightColor[1], emis*highlightColor[2], alpha ));
		highlight.setAmbient (new ColorRGBA(ambt*highlightColor[0], ambt*highlightColor[1], ambt*highlightColor[2], alpha ));
	}
	
	@Override
	public KeepoutBox clone() {
		return new KeepoutBox(getName(), getBounds());
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof KeepoutBox) {
			KeepoutBox box = (KeepoutBox) o;
			if(!Arrays.equals(getBounds(), box.getBounds())) {
				return false;
			}
			
			if(!getName().equals(box.getName())) {
				return false;
			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash += Arrays.hashCode(getBounds());
		hash *= 13 + getName().hashCode();
		return hash;
	}
	
	public double getX() {
		return x;
	}
	
	public void setX(double x) {
		if(round(this.x) == x) {
			return;
		}
		
		transform(new Vector3(x, y, z));
	}
	
	public double getY() {
		return y;
	}
	
	public void setY(double y) {
		if(round(this.y) == y) {
			return;
		}
		
		transform(new Vector3(x, y, z));
	}
	
	public double getZ() {
		return z;
	}
	
	public void setZ(double z) {
		if(round(this.z) == z) {
			return;
		}
		
		transform(new Vector3(x, y, z));
	}
	
	public double getP1x() {
		return p1x;
	}
	
	public void setP1x(double p1x) {
		if(round(this.p1x) == p1x) {
			return;
		}
		
		transform(new Vector3(p1x, p1y, p1z), new Vector3(p2x, p2y, p2z));
	}
	
	public double getP1y() {
		return p1y;
	}
	
	public void setP1y(double p1y) {
		if(round(this.p1y) == p1y) {
			return;
		}
		
		transform(new Vector3(p1x, p1y, p1z), new Vector3(p2x, p2y, p2z));
	}
	
	public double getP1z() {
		return p1z;
	}
	
	public void setP1z(double p1z) {
		if(round(this.p1z) == p1z) {
			return;
		}
		
		transform(new Vector3(p1x, p1y, p1z), new Vector3(p2x, p2y, p2z));
	}
	
	public double getP2x() {
		return p2x;
	}
	
	public void setP2x(double p2x) {
		if(round(this.p2x) == p2x) {
			return;
		}
		
		transform(new Vector3(p1x, p1y, p1z), new Vector3(p2x, p2y, p2z));
	}
	
	public double getP2y() {
		return p2y;
	}
	
	public void setP2y(double p2y) {
		if(round(this.p2y) == p2y) {
			return;
		}
		
		transform(new Vector3(p1x, p1y, p1z), new Vector3(p2x, p2y, p2z));
	}
	
	public double getP2z() {
		return p2z;
	}
	
	public void setP2z(double p2z) {
		if(round(this.p2z) == p2z) {
			return;
		}
		
		transform(new Vector3(p1x, p1y, p1z), new Vector3(p2x, p2y, p2z));
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);	
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);	
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
