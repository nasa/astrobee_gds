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

import gov.nasa.arc.simulator.freeflyer.FreeFlyer;
import gov.nasa.arc.simulator.freeflyer.publishers.DiskStatePublisher;
import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.system.RapidEntityFactory;
import gov.nasa.rapid.v2.e4.util.RapidUtil;

import org.apache.log4j.Logger;

import rapid.ext.astrobee.DATA_TOPICS_LIST_TOPIC;
import rapid.ext.astrobee.DATA_TO_DISK_STATE_TOPIC;
import rapid.ext.astrobee.DataToDiskState;
import rapid.ext.astrobee.DataToDiskStateDataWriter;
import rapid.ext.astrobee.DataTopicsList;
import rapid.ext.astrobee.DataTopicsListDataWriter;
import rapid.ext.astrobee.SaveSettingSequence64;

import com.rti.dds.infrastructure.InstanceHandle_t;

/**
 * Careful: you must call setTopicNamesAndPublishConfig before using this class
 * @author ddwheele
 *
 */
public class DataTopicsListPublisher {
	private static final Logger logger = Logger.getLogger(DiskStatePublisher.class);
	private static DataTopicsListPublisher INSTANCE;

	private DataTopicsList           config;
	private DataTopicsListDataWriter configWriter;
	private InstanceHandle_t     	   configInstance;

	private DataToDiskState           sample;
	private DataToDiskStateDataWriter sampleWriter;
	private InstanceHandle_t    	  sampleInstance;

	protected final String srcName    = DataTopicsListPublisher.class.getSimpleName();
	String[] topicNames;

	public static DataTopicsListPublisher getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new DataTopicsListPublisher();
			try {
				INSTANCE.createWriters(DATA_TOPICS_LIST_TOPIC.VALUE, DATA_TO_DISK_STATE_TOPIC.VALUE);
			} catch (DdsEntityCreationException e) {
				System.err.println(e);
				System.err.print(e.getStackTrace());
			}
		}
		return INSTANCE;
	}

	private DataTopicsListPublisher() {
	}

	public void setTopicNamesAndPublishConfig(String[] topicNames) {
		this.topicNames = topicNames;
		initializeDataTypes();
		publishConfig();
	}

	/**
	 * create the endpoints (i.e. readers and writers)
	 */
	 public void createWriters(final String positionConfigTopicName,String positionSampleTopicName) throws DdsEntityCreationException {

		 //-- Create the data writers. The Publisher is created automatically
		 configWriter = (DataTopicsListDataWriter)
				 RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID,
						 MessageTypeExtAstro.DATA_TOPICS_LIST_TYPE,
						 FreeFlyer.getPartition());
		 sampleWriter = (DataToDiskStateDataWriter)
				 RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID, 
						 MessageTypeExtAstro.DATA_TO_DISK_STATE_TYPE, 
						 FreeFlyer.getPartition());
	 }

	 /**
	  * initialize the data types that we will be publishing
	  */
	 public void initializeDataTypes() {
		 final int serialId = 0;
		 //-- Initialize a DataToDiskConfig
		 config = new DataTopicsList();
		 RapidUtil.setHeader(config.hdr, FreeFlyer.getPartition(), srcName, serialId);

		 for(int i=0; i<topicNames.length; i++) {

			 config.topics.userData.add(topicNames[i]);
		 }

		 // -- Initialize a DataToDiskSample
		 sample = new DataToDiskState();
		 RapidUtil.setHeader(sample.hdr, FreeFlyer.getPartition(), srcName, serialId);
		 //			Random random = new Random();
		 //			for(int i=0; i<topicNames.length; i++) {
		 //				SaveSetting saveSetting = new SaveSetting();
		 //				saveSetting.downlinkOption = DownlinkOption.valueOf(i%3);
		 //				saveSetting.frequency = random.nextFloat() * 5;
		 //				sample.topicSaveSettings.userData.add(saveSetting);
		 //			}

		 //-- register the data instances *after* we have set
		 //   assetName and participantName in headers (i.e. the keyed fields)
		 if(configWriter != null)
			 configInstance = configWriter.register_instance(config);
		 if(sampleWriter != null)
			 sampleInstance = sampleWriter.register_instance(sample);
	 }

	 public void publishSample(SaveSettingSequence64 settings) {
		 sample.topicSaveSettings = settings;
		 publishSample();
	 }

	 private void publishSample() {
		 if(sampleWriter != null) {
			 sampleWriter.write(sample, sampleInstance);
		 }
		 else {
			 logger.info("sampleWriter is null");
		 }
	 }

	 public void publishConfig() {
		 //-- publish the Config
		 logger.info("Publishing DataToDiskConfig...");
		 configWriter.write(config, configInstance);
	 }
}
