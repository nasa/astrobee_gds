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
package gov.nasa.arc.irg.plan.freeflyer.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Loads inertia config file and holds possible values
 * @author ddwheele
 *
 */
public class InertiaConfigList {
	private String type;
	private List<InertiaConfig> inertiaConfigs = new ArrayList<InertiaConfig>();
	
	public InertiaConfigList() {
		// for json deserializing
	}
	
	public int size() {
		return inertiaConfigs.size();
	}
	
	public InertiaConfig getConfigFromNumber(int index) {
		return inertiaConfigs.get(index);
	}
	
	public int getConfigIndex(InertiaConfig config) {
		return inertiaConfigs.indexOf(config);
	}
	
	public void setInertiaConfigs(List<InertiaConfig> configs) {
		this.inertiaConfigs = configs;
	}
	
	public List<InertiaConfig> getInertiaConfigs() {
		return inertiaConfigs;
	}
	
	public void addInertiaConfig(InertiaConfig config) {
		inertiaConfigs.add(config);
	}
	
	public InertiaConfig getConfigNamed(String name) {
		for(InertiaConfig config : inertiaConfigs) {
			if(config.getName().equals(name)) {
				return config;
			}
		}
		
		return null;
	}
	
	public String[] getArrayOfNames() {
		Vector<String> vec = new Vector<String>();
		
		for(InertiaConfig config : inertiaConfigs) {
			vec.add(config.getName());
		}
		
		return vec.toArray(new String[inertiaConfigs.size()]);
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("InertiaConfigList: ");
		for(InertiaConfig config : inertiaConfigs) {
			sb.append(config.name + ", ");
		}
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + type.hashCode();
		for(InertiaConfig opt : inertiaConfigs) {
			result = prime * result + ((opt == null) ? 0 : opt.hashCode());
		}
		return result;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(!(o instanceof InertiaConfigList)) {
			return false;
		}
		InertiaConfigList other = (InertiaConfigList)o;
		
		if(type == null) {
			if(other.getType() != null) {
				return false;
			}
		} else if(!type.equals(other.getType())) {
			return false;
		}
		
		if(inertiaConfigs.size() != other.size()) {
			return false;
		}
		
		List<InertiaConfig> otherOptions = other.getInertiaConfigs();
		
		for(int i=0; i<inertiaConfigs.size(); i++) {
			if(!inertiaConfigs.get(i).equals(otherOptions.get(i))){
				return false;
			}
		}
		return true;
	}
	
	public static class InertiaConfig {
		private String name;
		private float mass;
		private float[] matrix;
		
		public InertiaConfig() {}
		
		public InertiaConfig(String name, float mass, float[] matrix) {
			this.name = name;
			this.mass = mass;
			this.matrix = matrix;
		}
		
		public String getName() {
			return name;
		}

		public float getMass() {
			return mass;
		}

		public float[] getMatrix() {
			return matrix;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setMass(float mass) {
			this.mass = mass;
		}

		public void setMatrix(float[] matrix) {
			this.matrix = matrix;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(name + ", ");
			sb.append(mass + " kg, ");
			sb.append("[");
			for(int i=0; i<matrix.length-1; i++) {
				sb.append(matrix[i] + ", ");
			}
			sb.append(matrix[matrix.length-1] + "]");
			return sb.toString();
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + Float.floatToIntBits(mass);
			result = prime * result + ((matrix == null) ? 0 : matrix.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object o) {
			float EPSILON = 0.0001f;
			
			if(this == o) {
				return true;
			}
			if(!(o instanceof InertiaConfig)) {
				return false;
			}
			InertiaConfig other = (InertiaConfig)o;
			
			if(!getName().equals(other.getName())) {
				return false;
			}
			
			if(Math.abs(getMass()- other.getMass())>EPSILON) {
				return false;
			}
			float[] otherMatrix = other.getMatrix();
			if(matrix.length != otherMatrix.length) {
				return false;
			}

			for(int i=0; i<matrix.length; i++) {
				if(matrix[i] != otherMatrix[i]) {
					return false;
				}
			}
			return true;
		}
	}
}
