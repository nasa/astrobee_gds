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
package gov.nasa.arc.simulator.freeflyer.datatodisk;

import gov.nasa.arc.irg.plan.freeflyer.config.DataToDiskSetting;
import gov.nasa.arc.irg.plan.ui.io.DataToDiskSettingsLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.Inflater;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import rapid.ext.astrobee.CompressedFile;
import rapid.ext.astrobee.DownlinkOption;
import rapid.ext.astrobee.SaveSetting;
import rapid.ext.astrobee.SaveSettingSequence64;

import com.rti.dds.infrastructure.ByteSeq;


/**
 * Will keep track of what should be stored on disk
 * @author ddwheele
 *
 */
public class DataToDiskSubsystem {

	private final String[] topicNames = {"RosTopic0", "RosTopic1", "RosTopic2", 
			"RosTopic3", "RosTopic4", "RosTopic5", "RosTopic6"};
	
	private SaveSettingSequence64 settingSeq;

	private DataTopicsListPublisher dataToDiskPublisher;
	private final String DATA_TO_DISK_JSON_TYPE = "DataConfigurationFile";
	private final int FIELDS_IN_SETTINGS_ENTRY = 3;
	private final String IMMEDIATE_DOWNLINK = "immediate";
	private final String DELAYED_DOWNLINK = "delayed";
	private String defaultsFile = "/files/default.json";
	
	private static DataToDiskSubsystem INSTANCE;
	
	public static DataToDiskSubsystem getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new DataToDiskSubsystem();
		}
		return INSTANCE;
	}

	private DataToDiskSubsystem() {
		dataToDiskPublisher = DataTopicsListPublisher.getInstance();
		dataToDiskPublisher.setTopicNamesAndPublishConfig(topicNames);
		setupSettings();
	}
	
	private void setupSettings() {
		settingSeq = new SaveSettingSequence64();
		
		// dummy values
		for(int i=0; i<topicNames.length; i++) {
			SaveSetting ss = new SaveSetting();
			ss.downlinkOption = DownlinkOption.valueOf(i%3);
			ss.frequency = i;
			settingSeq.userData.add(ss);
		}
		
		Path currentRelativePath = Paths.get("");
		try {
			DataToDiskSetting setting = DataToDiskSettingsLoader.loadFromFile(currentRelativePath.toAbsolutePath().toString() + defaultsFile);
			ingestSetting(setting);
			dataToDiskPublisher.publishSample(settingSeq);
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	public void acceptCompressedFile(CompressedFile file) {
		try{
			final ByteSeq seq = file.compressedFile.userData;

			final Inflater inflater = new Inflater();
			inflater.setInput(seq.toArrayByte(new byte[seq.size()]));

			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			final byte[] buf = new byte[1024];
			while(!inflater.finished()){
				baos.write(buf,0,inflater.inflate(buf));
			}
			inflater.end();
			final File receivedFile = File.createTempFile("tmpPlan-"+System.currentTimeMillis(), ".json");
			FileUtils.writeByteArrayToFile(receivedFile, baos.toByteArray());

			DataToDiskSetting setting = DataToDiskSettingsLoader.loadFromFile(receivedFile.getAbsolutePath());
			ingestSetting(setting);
			dataToDiskPublisher.publishSample(settingSeq);
			
			receivedFile.delete();
			
		}catch(Exception e){
			System.err.println(e);
			System.err.print(e.getStackTrace());
		}
	}

	private void ingestSetting(DataToDiskSetting dataToDiskSetting) {
		for(DataToDiskSetting.RosTopicSetting rts : dataToDiskSetting.getTopicSettings()) {
			
			String topic = rts.getTopicName();
			String downlink = rts.getDownlinkOption();
			double frequency = rts.getFrequency();
			
			// find number of topic
			int num = Integer.valueOf(topic.substring(topic.length()-1));
			if(!topicNames[num].equals(topic)) {
				continue;
			}
			
			SaveSetting saveSetting = (SaveSetting)settingSeq.userData.get(num);
			saveSetting.frequency = (float)frequency;
			if(IMMEDIATE_DOWNLINK.equals(downlink)) {
				saveSetting.downlinkOption = DownlinkOption.DATA_IMMEDIATE;
			} else if(DELAYED_DOWNLINK.equals(downlink)) {
				saveSetting.downlinkOption = DownlinkOption.DATA_DELAYED;
			}
		}
	}
	
	private void ingestAndPublishSettings(InputStream inputStream) throws Exception {
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(new InputStreamReader(inputStream));
		JSONObject jsonObject = (JSONObject) obj;
		String type = (String) jsonObject.get("type");
		if(!type.equals(DATA_TO_DISK_JSON_TYPE)) {
			throw new Exception("Data To Disk configuration file wrong type");
		}
		
		JSONArray seq = (JSONArray)jsonObject.get("sequence");
		
		for(int i=0; i<seq.size(); i++) {
			JSONArray settingsEntry = (JSONArray) seq.get(i);
			
			if(settingsEntry.size() != FIELDS_IN_SETTINGS_ENTRY) {
				throw new Exception("Data To Disk must have "+FIELDS_IN_SETTINGS_ENTRY+" fields in settingSeq entry");
			}
			
			String topic = (String) settingsEntry.get(0);
			String downlink = (String) settingsEntry.get(1);
			double frequency = (double) settingsEntry.get(2);
			
			// find number of topic
			int num = Integer.valueOf(topic.substring(topic.length()-1));
			if(!topicNames[num].equals(topic)) {
				continue;
			}
			
			SaveSetting saveSetting = (SaveSetting)settingSeq.userData.get(num);
			saveSetting.frequency = (float)frequency;
			if(IMMEDIATE_DOWNLINK.equals(downlink)) {
				saveSetting.downlinkOption = DownlinkOption.DATA_IMMEDIATE;
			} else if(DELAYED_DOWNLINK.equals(downlink)) {
				saveSetting.downlinkOption = DownlinkOption.DATA_DELAYED;
			}
		}
		
		dataToDiskPublisher.publishSample(settingSeq);
	}
}
