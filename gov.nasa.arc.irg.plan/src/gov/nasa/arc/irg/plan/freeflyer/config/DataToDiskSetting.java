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

import java.util.List;


/**
 * Class read from the JSON config file to send up to Astrobee.  Right now, just used by the simulator FreeFlyer.java
 * 
 * Don't confuse this class with DataToDiskConfig and DataToDiskState (RAPID classes) that Astrobee sends us.
 * 
 * @author ddwheele
 */
public class DataToDiskSetting {
	private String type;
	private String name;
	private List<RosTopicSetting> topicSettings;

	public DataToDiskSetting() {
		// for JSON
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<RosTopicSetting> getTopicSettings() {
		return topicSettings;
	}

	public void setTopicSettings(List<RosTopicSetting> topicSettings) {
		this.topicSettings = topicSettings;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((topicSettings == null) ? 0 : topicSettings.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(!(o instanceof DataToDiskSetting)) {
			return false;
		}
		DataToDiskSetting other = (DataToDiskSetting)o;

		if(type != null) {
			if(!type.equals(other.type)) {
				return false;
			}
		} else {
			if(other.type != null) {
				return false;
			}
		}

		if(name != null) {
			if(!name.equals(other.name)) {
				return false;
			}
		} else {
			if(other.name != null) {
				return false;
			}
		}
		if(topicSettings != null) {
			if(!topicSettings.equals(other.topicSettings)) {
				return false;
			}
		} else {
			if(other.topicSettings != null) {
				return false;
			}
		}		return true;
	}
	
	public static class RosTopicSetting {
		private String topicName;
		private String downlinkOption;
		private float frequency;

		public RosTopicSetting() {
			// for JSON
		}
		
		public RosTopicSetting(String name, String option, float freq) {
			topicName = name;
			downlinkOption = option;
			frequency = freq;
		}

		public String getTopicName() {
			return topicName;
		}

		public void setTopicName(String topicName) {
			this.topicName = topicName;
		}

		public String getDownlinkOption() {
			return downlinkOption;
		}

		public void setDownlinkOption(String downlinkOption) {
			this.downlinkOption = downlinkOption;
		}

		public float getFrequency() {
			return frequency;
		}

		public void setFrequency(float frequency) {
			this.frequency = frequency;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((topicName == null) ? 0 : topicName.hashCode());
			result = prime * result + ((downlinkOption == null) ? 0 : downlinkOption.hashCode());
			result = prime * result + Float.floatToIntBits(frequency);
			return result;
		}

		@Override
		public boolean equals(Object o) {
			float EPSILON = 0.00001f;

			if(this == o) {
				return true;
			}
			if(!(o instanceof RosTopicSetting)) {
				return false;
			}
			RosTopicSetting other = (RosTopicSetting)o;

			if(topicName != null) {
				if(!topicName.equals(other.topicName)) {
					return false;
				}
			} else {
				if(other.topicName != null) {
					return false;
				}
			}

			if(downlinkOption != null) {
				if(!downlinkOption.equals(other.downlinkOption)) {
					return false;
				}
			} else {
				if(other.downlinkOption != null) {
					return false;
				}
			}
			if(Math.abs(frequency-other.frequency)>EPSILON) {
				return false;
			}
			return true;
		}
	}
}
