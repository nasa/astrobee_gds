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
package gov.nasa.arc.irg.freeflyer.rapid.state;

import java.util.Arrays;

import rapid.ext.astrobee.InertialProperties;


public class InertialPropertiesGds {
	/** name of loaded config */
	private String name; 
	/** mass of Astrobee */
	private float mass;
	/** Astrobee inertia matrix */
	private float[] inertiaMatrix;

	public InertialPropertiesGds() {
		name = "None";
		mass = 0;
		inertiaMatrix = new float[9];
	}

	public InertialPropertiesGds copyFrom(InertialPropertiesGds original) {
		if(original != null) {
			name = new String(original.getName());
			mass = original.getMass();
			inertiaMatrix = Arrays.copyOf(original.getInertiaMatrix(), original.getInertiaMatrix().length);
		}
		return this;
	}

	public void ingestInertialProperties(InertialProperties ip) {
		name = ip.name;
		mass = ip.mass;
		inertiaMatrix = ip.matrix.userData;
	}

	public String getName() {
		return name;
	}

	public float getMass() {
		return mass;
	}

	public float[] getInertiaMatrix() {
		return inertiaMatrix;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMass(float mass) {
		this.mass = mass;
	}

	public void setInertiaMatrix(float[] inertiaMatrix) {
		this.inertiaMatrix = inertiaMatrix;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = ((name == null) ? 0 : name.hashCode());
		result = prime * result + Float.floatToIntBits(mass);
		result = prime * result + ((inertiaMatrix == null) ? 0 : inertiaMatrix.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if( !(obj instanceof InertialPropertiesGds)) {
			return false;
		}
		InertialPropertiesGds other = (InertialPropertiesGds) obj;

		if(!name.equals(other.getName())) {
			return false;
		}
		if(mass != other.getMass()) {
			return false;
		}
		if(!Arrays.equals(inertiaMatrix,other.getInertiaMatrix()) ) {
			return false;
		}
		return true;
	}

}
