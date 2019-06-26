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
package gov.nasa.arc.verve.freeflyer.workbench.parts.guestscience;

import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import rapid.ext.astrobee.GuestScienceData;
import rapid.ext.astrobee.GuestScienceDataType;

public class GuestScienceDataGds {
	private static final Logger logger = Logger.getLogger(GuestScienceDataGds.class);
	protected String apkName;
	protected rapid.ext.astrobee.GuestScienceDataType dataType;
	protected String topic;
	protected rapid.OctetSequence2K octetData;
	protected String jsonString;
	
	public GuestScienceDataGds(GuestScienceData in) {
		apkName = in.apkName;
		dataType = in.type;
		topic = in.topic;
		octetData = in.data;
		
		init();
	}

	protected void init() {
		if(dataType.equals(GuestScienceDataType.GUEST_SCIENCE_JSON)) {
			final byte[] bytes = new byte[octetData.userData.size()];
			for(int i = 0 ; i < octetData.userData.size(); i++){
				bytes[i] = (byte)octetData.userData.get(i);
			}
			jsonString = new String(bytes);
		}
	}
	
	public String getApkName() {
		return apkName;
	}

	public rapid.ext.astrobee.GuestScienceDataType getDataType() {
		return dataType;
	}

	public String getTopic() {
		return topic;
	}

	public rapid.OctetSequence2K getOctetData() {
		return octetData;
	}

	public String getJsonString() {
		if(!dataType.equals(GuestScienceDataType.GUEST_SCIENCE_JSON)) {
			logger.error("GuestScienceData is not of type JSON String");
		}
		return jsonString;
	}

	public Iterator<Entry<String, JsonNode>> getJsonIterator() {
		if(dataType.equals(GuestScienceDataType.GUEST_SCIENCE_JSON)) {
			final ObjectMapper mapper = new ObjectMapper(new JsonFactory());
			try {
				 return mapper.readTree(jsonString).getFields();
			}catch(Exception e){
				logger.error(e.getMessage());
			}
		}
		logger.error("GuestScienceData is not of type JSON String");
		return null;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(dataType.equals(GuestScienceDataType.GUEST_SCIENCE_JSON)) {
			sb.append(apkName + ", ");
			sb.append(topic + ": ");
			sb.append(jsonString);
		} else {
			sb.append(apkName + ", ");
			sb.append(topic + " (");
			sb.append(dataType + "): ");
			sb.append(octetData);
		}
		return sb.toString();
	}
}
