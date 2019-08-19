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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import rapid.ext.astrobee.SaveSetting;

public class RosTopicsList {
	protected static Map<String, ARosTopic> topicNames = new HashMap<String,ARosTopic>();
	static RosTopicsList DATA_TO_DISK_SETTINGS;
	static RosTopicsList TOPICS_LIST;
	static boolean recordingData = false;
	static String recordingName;
	public enum DownlinkOption {IMMEDIATE, DELAYED }

	public static RosTopicsList getDataToDiskSettings() {
		if(DATA_TO_DISK_SETTINGS == null) {
			DATA_TO_DISK_SETTINGS = new RosTopicsList();
		}
		return DATA_TO_DISK_SETTINGS;
	}
	
	public static RosTopicsList getTopicsList() {
		if(TOPICS_LIST == null) {
			TOPICS_LIST = new RosTopicsList();
		}
		return TOPICS_LIST;
	}
	
	public static Collection<ARosTopic> getTopics() {
		return topicNames.values();
	}

	public static void clear() {
		topicNames.clear();
	}
	
	public static void setRecordingName(String name) {
		recordingName = name;
	}
	
	public static String getRecordingName() {
		return recordingName;
	}

	public static void addTopicName(String topicName) {
		ARosTopic newTopic = new ARosTopic(topicName);
		topicNames.put(topicName, newTopic);
	}

	public static void setThisTopicsSettings(SaveSetting ss){
		ARosTopic art = new ARosTopic(ss.topicName);
		art.setDownlinkOption(ss.downlinkOption);
		art.setFrequency(ss.frequency);
		topicNames.put(ss.topicName, art);	
	}

	public static void setRecordingData(boolean rec) {
		recordingData = rec;
	}
	
	public static boolean isRecordingData() {
		return recordingData;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(ARosTopic t : topicNames.values()) {
			sb.append(t.toString() + ", ");
		}
		return sb.toString();
	}

	public static class ARosTopic {
		public final String topicName;
		private DownlinkOption downlink;
		private float frequency;

		ARosTopic(String name) {
			topicName = name;
			downlink = DownlinkOption.IMMEDIATE;
			frequency = -1;
		}
		
		public void setDownlinkOption(DownlinkOption opt) {
			downlink = opt;
		}
		
		public void setDownlinkOption(rapid.ext.astrobee.DownlinkOption opt) {
			downlink = DownlinkOption.values()[opt.value()];
		}
		
		public void setFrequency(float frequency) {
			this.frequency = frequency;
		}

		public DownlinkOption getDownlink() {
			return downlink;
		}

		public float getFrequency() {
			return frequency;
		}
		
		@Override
		public String toString() {
			return topicName;
		}
	}
}
