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
package gov.nasa.arc.simulator.freeflyer.subsystem.command;

import gov.nasa.arc.simulator.freeflyer.FreeFlyer;
import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.system.RapidEntityFactory;
import gov.nasa.rapid.v2.e4.util.RapidUtil;

import org.apache.log4j.Logger;

import rapid.Ack;
import rapid.Command;
import rapid.CommandDataWriter;

import com.rti.dds.infrastructure.InstanceHandle_t;

public class CommandEchoPublisher {
	private static final Logger logger = Logger.getLogger(CommandEchoPublisher.class);

	protected final String srcName    = CommandEchoPublisher.class.getSimpleName();

	protected Command           sample;
	protected CommandDataWriter sampleWriter;
	protected InstanceHandle_t sampleInstance;

	private static volatile CommandEchoPublisher INSTANCE = null;
	private int count = 0;

	public static CommandEchoPublisher getInstance() {
		if(INSTANCE == null) {
			synchronized (CommandEchoPublisher.class) {
				if (INSTANCE == null) {
					final CommandEchoPublisher temp;
					temp = new CommandEchoPublisher();
					try {
						temp.createWriters();
						temp.initializeDataTypes();
					} catch (DdsEntityCreationException e) {
						System.err.println(e);
					}
					
					INSTANCE = temp;
				}
			}
		}
		return INSTANCE;
	}

	private CommandEchoPublisher() {
	}


	/**
	 * create the endpoints (i.e. readers and writers)
	 */
	public void createWriters() throws DdsEntityCreationException {   	

		sampleWriter = (CommandDataWriter)
				RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID, 
						MessageTypeExtAstro.COMMAND_ECHO_TYPE, 
						FreeFlyer.getPartition());
	}

	/**
	 * initialize the data types that we will be publishing
	 */
	public void initializeDataTypes() {
		final int serialId = 0;

		//-- Initialize an AgentSample
		sample = new Command();
		RapidUtil.setHeader(sample.hdr, FreeFlyer.getPartition(), srcName, serialId);
		

		//-- register the data instances *after* we have set
		//   assetName and participantName in headers (i.e. the keyed fields)
		if(sampleWriter != null) {
			sampleInstance = sampleWriter.register_instance(sample);
		}
	}

	public void publishCommandEcho(Command cmd){
		sample = (Command) cmd.copy_from(cmd);
		RapidUtil.setHeader(sample.hdr, FreeFlyer.getPartition(), srcName, count++);
		sample.hdr.timeStamp = System.currentTimeMillis();

		publishThisSample(sample, sampleInstance);
	}
	
	private void publishThisSample(Command sample, InstanceHandle_t sampleInstance) {
		if(sampleWriter != null) {
			sampleWriter.write(sample, sampleInstance);
		}
		else {
			logger.info("sampleWriter is null");
		}
	}
}





