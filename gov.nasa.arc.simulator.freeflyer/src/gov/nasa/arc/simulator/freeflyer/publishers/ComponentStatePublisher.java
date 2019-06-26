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

import rapid.ext.astrobee.ComponentConfig;
import rapid.ext.astrobee.ComponentConfigDataWriter;
import rapid.ext.astrobee.ComponentInfo;
import rapid.ext.astrobee.ComponentInfoConfig;
import rapid.ext.astrobee.ComponentState;
import rapid.ext.astrobee.ComponentStateDataWriter;

import com.rti.dds.infrastructure.InstanceHandle_t;

public class ComponentStatePublisher {
	private static final Logger logger = Logger.getLogger(ComponentStatePublisher.class);
	protected final String srcName    = ComponentStatePublisher.class.getSimpleName();

	private ComponentConfig           config;
	private ComponentConfigDataWriter configWriter;
	private InstanceHandle_t     	  configInstance;

	protected ComponentState           sample;
	protected ComponentStateDataWriter sampleWriter;
	protected InstanceHandle_t     	   sampleInstance;

	private String[] components = {"HLP", "MLP", "LLP", "Fan1", "Fan2", "Cam1", "Cam2", "Cam3"};
	private Random random;

	private static ComponentStatePublisher INSTANCE;

	public static ComponentStatePublisher getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new ComponentStatePublisher();
			try {
				INSTANCE.createWriters();
				INSTANCE.initializeDataTypes();
				INSTANCE.publishConfig();
			} catch (DdsEntityCreationException e) {
				System.err.println(e);
			}
		}
		return INSTANCE;
	}

	private ComponentStatePublisher() {
		random = new Random();
	}

	/**
	 * create the endpoints (i.e. readers and writers)
	 */
	public void createWriters() throws DdsEntityCreationException {   	
		configWriter = (ComponentConfigDataWriter)
				RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID,
						MessageTypeExtAstro.COMPONENT_CONFIG_TYPE,
						FreeFlyer.getPartition());

		sampleWriter = (ComponentStateDataWriter)
				RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID, 
						MessageTypeExtAstro.COMPONENT_STATE_TYPE, 
						FreeFlyer.getPartition());
	}

	/**
	 * initialize the data types that we will be publishing
	 */
	public void initializeDataTypes() {
		final int serialId = 0;

		config = new ComponentConfig();
		RapidUtil.setHeader(config.hdr, FreeFlyer.getPartition(), srcName, serialId);
		sample = new rapid.ext.astrobee.ComponentState();
		RapidUtil.setHeader(sample.hdr, FreeFlyer.getPartition(), srcName, serialId);
		for(int i=0; i<components.length; i++) {
			ComponentInfoConfig componentInfoConfig = new ComponentInfoConfig();
			componentInfoConfig.name = components[i];
			config.components.userData.add(componentInfoConfig);

			ComponentInfo componentInfo = new ComponentInfo();
			makeFakeComponentStateValues(componentInfo);
			sample.components.userData.add(componentInfo);

			//-- register the data instances *after* we have set
			//   assetName and participantName in headers (i.e. the keyed fields)
			if(sampleWriter != null)
				sampleInstance = sampleWriter.register_instance(sample);

		}
	}

	public void startComponentStateGenerator() {
		Thread generator = new Thread() {			
			@Override
			public void run() {
				int counter = 0;
				while(true) {
					if(counter%5 == 0) {
						updateAllComponentValues();
					} else {
						updateTemperaturesAndCurrents();
					}

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						break;
					}

					publishSamples();	
					counter++;
				}
			}
		};
		generator.start();
	}

	public void publishSamples() {
		if(sampleWriter != null) {
			sampleWriter.write(sample, sampleInstance);
		}
		else {
			logger.info("sampleWriter is null");
		}
	}

	private void updateTemperaturesAndCurrents() {
		for(int i=0; i<sample.components.userData.size(); i++) {
			ComponentInfo ci = (ComponentInfo)sample.components.userData.get(i);
			if(ci.present) {
				ci.temperature += random.nextInt(4);
				ci.current += (random.nextFloat()*5 - 2.5f);
			}
		}
	}

	private void updateAllComponentValues() {
		for(int i=0; i<sample.components.userData.size(); i++) {
			makeFakeComponentStateValues((ComponentInfo)sample.components.userData.get(i));
		}
	}


	private void makeFakeComponentStateValues(ComponentInfo cs) {
		cs.present = generateTrueOrFalse();
		if(cs.present) {
			cs.powered = generateTrueOrFalse();
			cs.temperature = generateTemperatureValue();
			cs.current = generateCurrentValue();
		} else {
			cs.powered = false;
			cs.temperature = 0;
			cs.current = 0;
		}
	}

	// skew toward true
	private boolean generateTrueOrFalse() {
		if(random.nextInt(5) > 1) 
			return true;
		return false;
	}

	private float generateTemperatureValue() {
		return 17 + random.nextInt(90);
	}

	private float generateCurrentValue() {
		return random.nextInt(50)/17;
	}

	public void publishConfig() {
		//-- publish the Config
		logger.info("Publishing ComponentConfig...");
		configWriter.write(config, configInstance);
	}
}
