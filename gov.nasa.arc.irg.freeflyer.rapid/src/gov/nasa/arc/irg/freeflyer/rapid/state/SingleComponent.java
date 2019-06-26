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

import rapid.ext.astrobee.ComponentInfo;

public class SingleComponent {
	private final String componentName; /* maximum length = (32) */
	private boolean present;
	private boolean powered;
	private float temperature;
	private float current;
	 //   public rapid.KeyTypeValueSequence8 data = (rapid.KeyTypeValueSequence8) rapid.KeyTypeValueSequence8.create();
	// we'll do this one later
	    
	public SingleComponent(String name) {
		componentName = name;
	}
	
	public SingleComponent(SingleComponent other) {
		componentName = new String(other.getName());
		present = other.isPresent();
		powered = other.isPowered();
		temperature = other.getTemperature();
		current = other.getCurrent();
	}
	
	public void update(ComponentInfo ci) {
		present = ci.present;
		powered = ci.powered;
		temperature = ci.temperature;
		current = ci.current;
	}
	
	public String getName() {
		return componentName;
	}

	public boolean isPresent() {
		return present;
	}
	
	public boolean isPowered() {
		return powered;
	}

	public float getTemperature() {
		return temperature;
	}

	public float getCurrent() {
		return current;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((componentName == null) ? 0 : componentName.hashCode());
		result = prime * result + (present ? 0 : 1);
		result = prime * result + (powered ? 0 : 1);
		result = prime * result + Float.floatToIntBits(temperature);
		result = prime * result + Float.floatToIntBits(current);
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		float EPSILON = 0.0001f;
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (obj instanceof SingleComponent) {
			SingleComponent other = (SingleComponent)obj;
			if (componentName == null) {
				if (other.getName() != null) {
					return false;
				}
			} else if (!componentName.equals(other.getName())) {
				return false;
			}
			if(present != other.isPresent()) {
				return false;
			}
			if(powered != other.isPowered()) {
				return false;
			}
			if(Math.abs(temperature - other.getTemperature()) > EPSILON) {
				return false;
			}
			if(Math.abs(current - other.getCurrent()) > EPSILON) {
				return false;
			}
			return true;
		}
		return false;
	}
}
