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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class AbstractTransformPreview {
	private final PropertyChangeSupport m_propertyChangeSupport = new PropertyChangeSupport(this);

	protected double p1_x, p1_y, p1_z;
	protected double p2_x, p2_y, p2_z;
	protected double c_x, c_y, c_z;

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		m_propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		m_propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		m_propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		m_propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	protected void firePropertyChange(String propertyName, Object oldValue,	Object newValue) {
		m_propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
		m_propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
		m_propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}
	
	protected abstract void transform();
	
	public void setP1_x(double x) {
		p1_x = x;
		transform();
	}

	public void setP1_y(double y) {
		p1_y = y;
		transform();
	}
	
	public void setP1_z(double z) {
		p1_z = z;
		transform();
	}
	
	public void setP2_x(double x) {
		p2_x = x;
		transform();
	}

	public void setP2_y(double y) {
		p2_y = y;
		transform();
	}
	
	public void setP2_z(double z) {
		p2_z = z;
		transform();
	}
	
	public void setC_x(double x) {
		c_x = x;
		transform();
	}

	public void setC_y(double y) {
		c_y = y;
		transform();
	}
	
	public void setC_z(double z) {
		c_z = z;
		transform();
	}
	
	public double getP1_x() {
		return p1_x;
	}
	
	public double getP1_y() {
		return p1_y;
	}
	
	public double getP1_z() {
		return p1_z;
	}
	
	public double getP2_x() {
		return p2_x;
	}
	
	public double getP2_y() {
		return p2_y;
	}
	
	public double getP2_z() {
		return p2_z;
	}
	
	public double getC_x() {
		return c_x;
	}
	
	public double getC_y() {
		return c_y;
	}
	
	public double getC_z() {
		return c_z;
	}
	
	public void reset() {
		p1_x = 0;
		p1_y = 0;
		p1_z = 0;
		p2_x = 0;
		p2_y = 0;
		p2_z = 0;
		c_x = 0;
		c_y = 0;
		c_z = 0;
		transform();
	}
	
}
