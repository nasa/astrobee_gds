package gov.nasa.arc.verve.ui.e4.widget;
/*******************************************************************************
 * Copyright (c) 2013 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
import gov.nasa.ensemble.ui.databinding.model.AbstractModelObject;
import gov.nasa.ensemble.ui.databinding.widgets.customization.annotations.ReadOnly;

import com.ardor3d.math.Vector3;

/**
 * Wrap the Vector3 with a class that supports notifications so undo/redo will work.
 * @author tecohen
 *
 */
public class Vector3Model extends AbstractModelObject {
	
	protected Vector3 m_vector3;
	
	public Vector3Model(Vector3 vector){
		m_vector3 = vector;
	}
	
	@ReadOnly
	public Vector3 getVector3() {
		return m_vector3;
	}

	
	public void setVector3(Vector3 vector3) {
		m_vector3 = vector3;
	}
	
	public double getX() {
		if (m_vector3 != null){
			return m_vector3.getX();
		}
		return 0;
	}
	
	public double getY() {
		if (m_vector3 != null){
			return m_vector3.getY();
		}
		return 0;
	}
	
	public double getZ() {
		if (m_vector3 != null){
			return m_vector3.getZ();
		}
		return 0;
	}
	
	public void setX(double x){
		if (m_vector3 != null){
			double oldx = getX();
			m_vector3.setX(x);
			firePropertyChange("x", oldx, x);
		}
	}
	
	public void setY(double y){
		if (m_vector3 != null){
			double oldy = getY();
			m_vector3.setY(y);
			firePropertyChange("y", oldy, y);
		}
	}
	
	public void setZ(double z){
		if (m_vector3 != null){
			double oldz = getZ();
			m_vector3.setZ(z);
			firePropertyChange("z", oldz, z);
		}
	}
}
