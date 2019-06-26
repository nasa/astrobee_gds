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
package gov.nasa.arc.simulator.freeflyer.publishers;

import gov.nasa.arc.simulator.freeflyer.FreeFlyer;
import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.system.RapidEntityFactory;
import gov.nasa.rapid.v2.e4.util.RapidUtil;

import java.util.Random;

import org.apache.log4j.Logger;

import rapid.ext.astrobee.DISK_CONFIG_TOPIC;
import rapid.ext.astrobee.DISK_STATE_TOPIC;
import rapid.ext.astrobee.DiskConfig;
import rapid.ext.astrobee.DiskConfigDataWriter;
import rapid.ext.astrobee.DiskInfo;
import rapid.ext.astrobee.DiskInfoConfig;
import rapid.ext.astrobee.DiskState;
import rapid.ext.astrobee.DiskStateDataWriter;

import com.rti.dds.infrastructure.InstanceHandle_t;

public class DiskStatePublisher {
    private static final Logger logger = Logger.getLogger(DiskStatePublisher.class);
	private static DiskStatePublisher INSTANCE;
	
    private DiskConfig           config;
    private DiskConfigDataWriter configWriter;
    private InstanceHandle_t     configInstance;

    private DiskState           sample;
    private DiskStateDataWriter sampleWriter;
    private InstanceHandle_t    sampleInstance;
    
    protected final String srcName    = DiskStatePublisher.class.getSimpleName();
    
    private String[] diskNames = {"Disk A", "Disk B", "Disk C", "Disk D", "Disk E", "Disk F", "Disk G"};
    private int[] diskSizes = { 2000, 3000, 3000, 4000, 5000, 6000, 7000 };
    
	public static DiskStatePublisher getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new DiskStatePublisher();
			try {
				INSTANCE.createWriters(DISK_CONFIG_TOPIC.VALUE, DISK_STATE_TOPIC.VALUE);
				INSTANCE.initializeDataTypes();
				INSTANCE.publishConfig();
			} catch (DdsEntityCreationException e) {
				System.err.println(e);
			}
		}
		return INSTANCE;
	}
	
	protected DiskStatePublisher() {
	}

	 /**
     * create the endpoints (i.e. readers and writers)
     */
    public void createWriters(final String positionConfigTopicName,String positionSampleTopicName) throws DdsEntityCreationException {
    		
        //-- Create the data writers. The Publisher is created automatically
        configWriter = (DiskConfigDataWriter)
                RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID,
                                                    MessageTypeExtAstro.DISK_CONFIG_TYPE,
                                                    FreeFlyer.getPartition());
        sampleWriter = (DiskStateDataWriter)
                RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID, 
                									MessageTypeExtAstro.DISK_STATE_TYPE, 
                                                    FreeFlyer.getPartition());
    }
    
    /**
     * initialize the data types that we will be publishing
     */
    public void initializeDataTypes() {
        final int serialId = 0;
        //-- Initialize a PositionConfig
        config = new DiskConfig();
        RapidUtil.setHeader(config.hdr, FreeFlyer.getPartition(), srcName, serialId);
        
        for(int i=0; i<diskNames.length; i++) {
        	DiskInfoConfig diskInfoConfig = new DiskInfoConfig();
        	diskInfoConfig.name = diskNames[i];
        	diskInfoConfig.capacity = diskSizes[i];
        	config.filesystems.userData.add(diskInfoConfig);
        }

		// -- Initialize a PositionSample
		sample = new DiskState();
		RapidUtil.setHeader(sample.hdr, FreeFlyer.getPartition(), srcName, serialId);
		Random random = new Random();
		for(int i=0; i<diskSizes.length; i++) {
			DiskInfo diskInfo = new DiskInfo();
			diskInfo.used = random.nextInt(diskSizes[i]);
			sample.filesystems.userData.add(diskInfo);
		}

        //-- register the data instances *after* we have set
        //   assetName and participantName in headers (i.e. the keyed fields)
        if(configWriter != null)
            configInstance = configWriter.register_instance(config);
        if(sampleWriter != null)
            sampleInstance = sampleWriter.register_instance(sample);
    }
    
	public void startDiskStateGenerator() {
		Thread generator = new Thread() {			
			@Override
			public void run() {
				Random random = new Random();
				
				while(true) {
					for(int i=0; i<diskSizes.length; i++) {
						DiskInfo ds = (DiskInfo) sample.filesystems.userData.get(i);
						ds.used += random.nextInt(100);
					}
					
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						break;
					}
					
					publishSample();					
				}
			}
		};
		generator.start();
	}
    
    public void publishSample() {
        if(sampleWriter != null) {
            sampleWriter.write(sample, sampleInstance);
        }
        else {
            logger.info("sampleWriter is null");
        }
    }
   
    public void publishConfig() {
        //-- publish the Config
        logger.info("Publishing DiskConfig...");
        configWriter.write(config, configInstance);
    }
}
