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
package gov.nasa.arc.simulator.freeflyer.faults;

import gov.nasa.arc.irg.plan.freeflyer.config.FaultConfigList;
import gov.nasa.arc.irg.plan.freeflyer.config.FaultInfoGds;
import gov.nasa.arc.irg.plan.ui.io.FaultConfigListLoader;
import gov.nasa.arc.simulator.freeflyer.FreeFlyer;
import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.freeflyer.test.helper.TestData;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.system.RapidEntityFactory;
import gov.nasa.rapid.v2.e4.util.RapidUtil;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import rapid.Command;
import rapid.DataType;
import rapid.KeyTypeValueTriple;
import rapid.ParameterUnion;
import rapid.ext.astrobee.Fault;
import rapid.ext.astrobee.FaultConfig;
import rapid.ext.astrobee.FaultConfigDataWriter;
import rapid.ext.astrobee.FaultInfo;
import rapid.ext.astrobee.FaultStateDataWriter;

import com.rti.dds.infrastructure.InstanceHandle_t;

//Race conditions are mitigated by synchronized method declarations for locks
//on everything and thread safe/blocking queues.
public class FaultStateSubsystem {
	private final int NUM_FAULTS = 33;
	private final String srcName = FaultStateSubsystem.class.getSimpleName();
	private static final Logger logger = Logger
			.getLogger(FaultStateSubsystem.class);
	private final String BUNDLE_NAME = "gov.nasa.arc.verve.freeflyer.workbench";

	private static FaultStateSubsystem INSTANCE;
	private FaultStateDataWriter sampleWriter;
	private InstanceHandle_t sampleInstance;

	private FaultConfig           config;
	private FaultConfigDataWriter configWriter;
	private InstanceHandle_t     configInstance;

	private int count = 0;
	private Thread generator;
	private Thread clearer;
	private final int NUM_SECS_BETWEEN_FAULTS = 5;

	private ConcurrentLinkedQueue<Integer> enabledFaults;
	private ArrayBlockingQueue<Integer> faultsToClear;
	private ArrayBlockingQueue<Integer> faultsToEnable;
	private ArrayBlockingQueue<Integer> faultsToInhibit;
	private volatile boolean generateFaults;
	private volatile rapid.ext.astrobee.FaultState sample;

	public static FaultStateSubsystem getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new FaultStateSubsystem();
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

	private FaultStateSubsystem() {

	}

	private void createWriters() throws DdsEntityCreationException {
		configWriter = (FaultConfigDataWriter)
				RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID,
						MessageTypeExtAstro.FAULT_CONFIG_TYPE,
						FreeFlyer.getPartition());

		sampleWriter = (FaultStateDataWriter) RapidEntityFactory
				.createDataWriter(FreeFlyer.PARTICIPANT_ID,
						MessageTypeExtAstro.FAULT_STATE_TYPE,
						FreeFlyer.getPartition());
	}

	private void initializeDataTypes() {
		setUpQueues();
		initializeConfig();
		initializeSample();
	}

	private void setUpQueues() {
		enabledFaults = new ConcurrentLinkedQueue<Integer>();
		faultsToClear = new ArrayBlockingQueue<Integer>(NUM_FAULTS);
		faultsToEnable = new ArrayBlockingQueue<Integer>(NUM_FAULTS);
		faultsToInhibit = new ArrayBlockingQueue<Integer>(NUM_FAULTS);
	}
	
	private void initializeConfig() {
		final int serialId = 0;
		//-- Initialize a FaultConfig
		config = new FaultConfig();
		RapidUtil.setHeader(config.hdr, FreeFlyer.getPartition(), srcName, serialId);

		try {
			FaultConfigList loaded = FaultConfigListLoader.loadFromFile(TestData.getTestFile(BUNDLE_NAME, "FaultConfigurations.json").getAbsolutePath());

			config.subsystems.userData.addAll(loaded.getSubsystems());
			config.nodes.userData.addAll(loaded.getNodes());
			
			for(FaultInfoGds fig : loaded.getFaultInfos()) {
				FaultInfo fi = new FaultInfo();
				fi.subsystem = fig.getSubsystem();
				fi.node = fig.getNode();
				fi.faultId = fig.getFaultId();
				fi.warning = fig.isWarning();
				fi.faultDescription = fig.getFaultDescription();
				
				config.faults.userData.add(fi);
				
				enabledFaults.add(fig.getFaultId());
				
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	private void initializeSample() {
		final int serialId = 0;
		// -- Initialize a sample
		sample = new rapid.ext.astrobee.FaultState();
		RapidUtil.setHeader(sample.hdr, FreeFlyer.getPartition(), srcName, serialId);
		// pl.assign(sample.values.userData);

		// -- register the data instances *after* we have set
		// assetName and participantName in headers (i.e. the keyed fields)
		if (sampleWriter != null) {
			sampleInstance = sampleWriter.register_instance_untyped(sample);
		}
	}

	public void startFaultGenerator() {
		generateFaults = true;

		createGenerator();
		generator.start();

		createClearer();
		clearer.start();		
	}

	void createClearer() {
		clearer = new Thread() {
			public synchronized void run() {
				while(generateFaults) {
					try {
						//Blocks until a fault is placed by main thread.
						int code = faultsToClear.take();

						int index = -1;
						for(int i = 0; i < sample.faults.userData.size(); i++) {
							Fault fault = (Fault) sample.faults.userData.get(i);
							if(fault.code == code) {
								index = i;
							}
						}

						if(index != -1) {
							sample.faults.userData.remove(index);
						}
						enabledFaults.add(code);

						publishSample();
					} catch(InterruptedException e) { 
						return;
					}
				}
			}
		};
	}

	void createGenerator() {
		generator = new Thread() {

			public synchronized void run() {
				boolean faulting = sample.faults.userData.size() < 32;
				Random random = new Random();				

				while (generateFaults) {		

					try {
						wait(NUM_SECS_BETWEEN_FAULTS * 1000);
					} catch (InterruptedException e) { 
						return;
					}

					//Things might have changed while we were sleeping.
					if (enabledFaults.size() == 0 && faulting) {
						faulting = false;
					} else if (sample.faults.userData.size() == 0 && !faulting) {
						faulting = true;
					}

					if (faulting) {
						Fault fault = new Fault();

						Object[] temp = enabledFaults.toArray();
						fault.timestamp = System.currentTimeMillis();
						
						fault.code = (Integer) temp[random.nextInt(enabledFaults.size())];
						enabledFaults.remove(fault.code);

						fault.message = "This is the message for fault number " + fault.code;
						
						fault.data.userData.add(createData("extraData", DataType.RAPID_STRING, "Fault with ID "
								+ fault.code + " is triggered."));
						

						sample.faults.userData.add(fault);
						enabledFaults.remove(fault.code);

						faulting = sample.faults.userData.size() < 32;
					} else {
						int index = random.nextInt(sample.faults.userData
								.size());
						Fault fault = (Fault) sample.faults.userData
								.remove(index);
						enabledFaults.add(fault.code);
						faulting = sample.faults.userData.size() == 0;
					}

					publishSample();
				}
			}
		};
	}

	public void stopFaultGenerator() {
		generateFaults = false;
		generator.interrupt();
		clearer.interrupt();
	}

	public synchronized void clearFault(Command cmd) {
		int code = ((ParameterUnion) cmd.arguments.userData.get(0)).i();
		faultsToClear.add(code);
	}

	public synchronized void enableFault(Command cmd) {
		int code = ((ParameterUnion) cmd.arguments.userData.get(0)).i();
		faultsToEnable.add(code);
	}

	public synchronized void inhibitFault(Command cmd) {
		int code = ((ParameterUnion) cmd.arguments.userData.get(0)).i();
		faultsToInhibit.add(code);
	}

	private void printState() {
		System.out.println("Enabled: " + enabledFaults);
		System.out.print("Faults: ");
		for(Object o : sample.faults.userData) {
			Fault fault = (Fault) o;
			System.out.print(fault.code + " ");
		}
		System.out.println();
		System.out.println(faultsToClear);
		System.out.println(faultsToInhibit);
		System.out.println(faultsToEnable);
	}

	public void publishSample() {
		//printState();

		sample.hdr.timeStamp = System.currentTimeMillis();
		RapidUtil.setHeader(sample.hdr, FreeFlyer.getPartition(), srcName, count++);

		if (sampleWriter != null) {
			//System.out.println("published");
			sampleWriter.write(sample, sampleInstance);
		} else {
			logger.info("sampleWriter is null");
		}
	}

	private KeyTypeValueTriple createData(String key, DataType type, String data) {
		KeyTypeValueTriple ret = new KeyTypeValueTriple();
		ret.key = key;

		ParameterUnion param = new ParameterUnion();
		param._d = type;
		param.s = data;
		ret.value = param;

		return ret;
	}
	
	 public void publishConfig() {
	        //-- publish the Config
	        logger.info("Publishing FaultConfig...");
	        configWriter.write(config, configInstance);
	    }
}
