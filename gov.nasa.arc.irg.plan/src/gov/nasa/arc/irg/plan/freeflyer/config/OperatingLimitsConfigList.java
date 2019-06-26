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
 * Loads operating limits config file and holds possible values
 * @author ddwheele
 *
 */
public class OperatingLimitsConfigList {
	private String type;
	private List<OperatingLimitsConfig> operatingLimitsConfigs = new ArrayList<OperatingLimitsConfig>();

	public OperatingLimitsConfigList() {
		// for json deserializing
	}

	public int size() {
		return operatingLimitsConfigs.size();
	}

	public OperatingLimitsConfig getConfigFromNumber(int index) {
		return operatingLimitsConfigs.get(index);
	}

	public int getOperatingLimitsConfigIndex(OperatingLimitsConfig option) {
		return operatingLimitsConfigs.indexOf(option);
	}

	public void setOperatingLimitsConfigs(List<OperatingLimitsConfig> options) {
		this.operatingLimitsConfigs = options;
	}

	public List<OperatingLimitsConfig> getOperatingLimitsConfigs() {
		return operatingLimitsConfigs;
	}

	public void addOperatingLimitsConfig(OperatingLimitsConfig opt) {
		operatingLimitsConfigs.add(opt);
	}

	public OperatingLimitsConfig getConfigNamed(String name) {
		for(OperatingLimitsConfig opt : operatingLimitsConfigs) {
			if(opt.getProfileName().equals(name)) {
				return opt;
			}
		}
		return null;
	}

	public String[] getArrayOfNames() {
		Vector<String> vec = new Vector<String>();

		for(OperatingLimitsConfig opt : operatingLimitsConfigs) {
			vec.add(opt.getProfileName());
		}

		return vec.toArray(new String[operatingLimitsConfigs.size()]);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for(OperatingLimitsConfig opt : operatingLimitsConfigs) {
			result = prime * result + ((opt == null) ? 0 : opt.hashCode());
		}
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(!(o instanceof OperatingLimitsConfigList)) {
			return false;
		}
		OperatingLimitsConfigList other = (OperatingLimitsConfigList)o;

		if(operatingLimitsConfigs.size() != other.size()) {
			return false;
		}

		List<OperatingLimitsConfig> otherOptions = other.getOperatingLimitsConfigs();

		for(int i=0; i<operatingLimitsConfigs.size(); i++) {
			if(!operatingLimitsConfigs.get(i).equals(otherOptions.get(i))){
				return false;
			}
		}
		return true;
	}

	public static class OperatingLimitsConfig {
		private String profileName;
		private String flightMode;
		private float targetLinearVelocity;
		private float targetLinearAccel;
		private float targetAngularVelocity;
		private float targetAngularAccel;
		private float collisionDistance;

		public OperatingLimitsConfig() {}

		public OperatingLimitsConfig(String profileName, String flightMode, 
				float targetLinearVelocity, float targetLinearAccel, 
				float targetAngularVelocity, float targetAngularAccel,
				float collisionDistance) {
			this.profileName = profileName;
			this.flightMode = flightMode;
			this.targetLinearVelocity = targetLinearVelocity;
			this.targetLinearAccel = targetLinearAccel;
			this.targetAngularVelocity = targetAngularVelocity;
			this.targetAngularAccel = targetAngularAccel;
			this.collisionDistance = collisionDistance;
		}
		
		public String getProfileName() {
			return profileName;
		}

		public void setProfileName(String profileName) {
			this.profileName = profileName;
		}

		public String getFlightMode() {
			return flightMode;
		}

		public void setFlightMode(String flightMode) {
			this.flightMode = flightMode;
		}

		public float getTargetLinearVelocity() {
			return targetLinearVelocity;
		}

		public void setTargetLinearVelocity(float targetLinearVelocity) {
			this.targetLinearVelocity = targetLinearVelocity;
		}

		public float getTargetLinearAccel() {
			return targetLinearAccel;
		}

		public void setTargetLinearAccel(float targetLinearAccel) {
			this.targetLinearAccel = targetLinearAccel;
		}

		public float getTargetAngularVelocity() {
			return targetAngularVelocity;
		}

		public void setTargetAngularVelocity(float targetAngularVelocity) {
			this.targetAngularVelocity = targetAngularVelocity;
		}

		public float getTargetAngularAccel() {
			return targetAngularAccel;
		}

		public void setTargetAngularAccel(float targetAngularAccel) {
			this.targetAngularAccel = targetAngularAccel;
		}

		public float getCollisionDistance() {
			return collisionDistance;
		}

		public void setCollisionDistance(float collisionDistance) {
			this.collisionDistance = collisionDistance;
		}

		@Override
		public String toString() {
			return getProfileName();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((profileName == null) ? 0 : profileName.hashCode());
			result = prime * result + ((flightMode == null) ? 0 : flightMode.hashCode());
			result = prime * result + Float.floatToIntBits(targetLinearVelocity);
			result = prime * result + Float.floatToIntBits(targetLinearAccel);
			result = prime * result + Float.floatToIntBits(targetAngularVelocity);
			result = prime * result + Float.floatToIntBits(targetAngularAccel);
			result = prime * result + Float.floatToIntBits(collisionDistance);
			return result;
		}

		@Override
		public boolean equals(Object o) {
			float EPSILON = 0.00001f;
			
			if(this == o) {
				return true;
			}
			if(!(o instanceof OperatingLimitsConfig)) {
				return false;
			}
			OperatingLimitsConfig other = (OperatingLimitsConfig)o;

			if(!getProfileName().equals(other.getProfileName())) {
				return false;
			}
			if(!getFlightMode().equals(other.getFlightMode())) {
				return false;
			}
			if(Math.abs(getTargetLinearVelocity()-other.getTargetLinearVelocity())>EPSILON) {
				return false;
			}
			if(Math.abs(getTargetLinearAccel()-other.getTargetLinearAccel())>EPSILON) {
				return false;
			}
			if(Math.abs(getTargetAngularVelocity()-other.getTargetAngularVelocity())>EPSILON) {
				return false;
			}
			if(Math.abs(getTargetAngularAccel()-other.getTargetAngularAccel())>EPSILON) {
				return false;
			}
			if(Math.abs(getCollisionDistance()-other.getCollisionDistance())>EPSILON) {
				return false;
			}
			return true;
		}
	}
}
