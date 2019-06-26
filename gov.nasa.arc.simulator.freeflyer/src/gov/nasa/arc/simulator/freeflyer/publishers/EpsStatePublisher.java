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

import rapid.ext.astrobee.BatteryInfo;
import rapid.ext.astrobee.BatteryInfoConfig;
import rapid.ext.astrobee.BatterySlot;
import rapid.ext.astrobee.EPS_CONFIG_TOPIC;
import rapid.ext.astrobee.EPS_STATE_TOPIC;
import rapid.ext.astrobee.EpsConfig;
import rapid.ext.astrobee.EpsConfigDataWriter;
import rapid.ext.astrobee.EpsState;
import rapid.ext.astrobee.EpsStateDataWriter;

import com.rti.dds.infrastructure.InstanceHandle_t;

public class EpsStatePublisher {
	   private static final Logger logger = Logger.getLogger(DiskStatePublisher.class);
		private static EpsStatePublisher INSTANCE;
		
	    private EpsConfig           config;
	    private EpsConfigDataWriter configWriter;
	    private InstanceHandle_t     configInstance;

	    private EpsState           sample;
	    private EpsStateDataWriter sampleWriter;
	    private InstanceHandle_t    sampleInstance;
	    
	    protected final String srcName    = EpsStatePublisher.class.getSimpleName();
	    
		private Random random = new Random();
		
		final int initial_battery_minutes = 240;
		final int eps_state_interval_ms = 10000;
		final int battery_minutes_decrement = 1;
		
		boolean batteryDecrementing = true;
		
		int NUM_BATTERY_SLOTS = 4;
		
		public static EpsStatePublisher getInstance() {
			if(INSTANCE == null) {
				INSTANCE = new EpsStatePublisher();
				try {
					INSTANCE.createWriters(EPS_CONFIG_TOPIC.VALUE, EPS_STATE_TOPIC.VALUE);
					INSTANCE.initializeDataTypes();
					INSTANCE.publishConfig();
				} catch (DdsEntityCreationException e) {
					System.err.println(e);
				}
			}
			return INSTANCE;
		}
		
		protected EpsStatePublisher() {
		}

		 /**
	     * create the endpoints (i.e. readers and writers)
	     */
	    public void createWriters(final String positionConfigTopicName,String positionSampleTopicName) throws DdsEntityCreationException {
	    		
	        //-- Create the data writers. The Publisher is created automatically
	        configWriter = (EpsConfigDataWriter)
	                RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID,
	                                                    MessageTypeExtAstro.EPS_CONFIG_TYPE,
	                                                    FreeFlyer.getPartition());
	        sampleWriter = (EpsStateDataWriter)
	                RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID, 
	                									MessageTypeExtAstro.EPS_STATE_TYPE, 
	                                                    FreeFlyer.getPartition());
	    }
	    
	    /**
	     * initialize the data types that we will be publishing
	     */
	    public void initializeDataTypes() {
	        final int serialId = 0;
	        //-- Initialize an EpsConfig
	        config = new EpsConfig();
	        RapidUtil.setHeader(config.hdr, FreeFlyer.getPartition(), srcName, serialId);
	        
	        for(int i=0; i<NUM_BATTERY_SLOTS; i++) {
	        	BatteryInfoConfig bic = new BatteryInfoConfig();
	        	bic.slot = BatterySlot.from_int(i);
	        	bic.currentMaxCapacity = random.nextFloat() * 100;
	        	bic.designedCapacity = 200;
	        	config.batteries.userData.add(bic); 
	        }

			// -- Initialize an EpsSample
			sample = new EpsState();
			RapidUtil.setHeader(sample.hdr, FreeFlyer.getPartition(), srcName, serialId);
			sample.estimatedMinutesRemaining = initial_battery_minutes;
		
			for(int i=0; i<NUM_BATTERY_SLOTS; i++) {
				BatteryInfo bi = new BatteryInfo();
				makeFakeBatteryInfoValues(bi);
				sample.batteries.userData.add(bi);
			}

	        //-- register the data instances *after* we have set
	        //   assetName and participantName in headers (i.e. the keyed fields)
	        if(configWriter != null)
	            configInstance = configWriter.register_instance(config);
	        if(sampleWriter != null)
	            sampleInstance = sampleWriter.register_instance(sample);
	    }
	    
		public void startEpsStateGenerator() {
			Thread generator = new Thread() {			
				@Override
				public void run() {
					
					while(true) {
						makeFakeBatteryMinutesRemainingValues();
						
						for(int i=0; i<NUM_BATTERY_SLOTS; i++) {
							makeFakeBatteryInfoValues((BatteryInfo)sample.batteries.userData.get(i));
						}
						
						try {
							Thread.sleep(eps_state_interval_ms);
						} catch (InterruptedException e) {
							break;
						}
						
						publishSample();					
					}
				}
			};
			generator.start();
		}
		
		private void makeFakeBatteryMinutesRemainingValues() {
			if(batteryDecrementing) {
				sample.estimatedMinutesRemaining -= battery_minutes_decrement;
			} else {
				sample.estimatedMinutesRemaining += battery_minutes_decrement;
			}
			
			if(sample.estimatedMinutesRemaining > initial_battery_minutes) {
				batteryDecrementing = true;
			} else if(sample.estimatedMinutesRemaining < 0){
				batteryDecrementing = false;
			}
		}
	    
	    private void makeFakeBatteryInfoValues(BatteryInfo bi) {
	    	bi.percentage = random.nextFloat() * 100;
	    	bi.temperature = random.nextFloat() * 50;
	    	bi.voltage = random.nextFloat() * 12;
	    	bi.current = random.nextFloat() * 17;
	    	bi.remainingCapacity = random.nextFloat() * 24;
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
	        logger.info("Publishing PositionConfig...");
	        configWriter.write(config, configInstance);
	    }
}
