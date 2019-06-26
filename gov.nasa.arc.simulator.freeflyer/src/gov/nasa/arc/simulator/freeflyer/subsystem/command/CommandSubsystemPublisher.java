package gov.nasa.arc.simulator.freeflyer.subsystem.command;

/*******************************************************************************
 * Copyright (c) 2014 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerCommands;
import gov.nasa.arc.simulator.freeflyer.FreeFlyer;
import gov.nasa.arc.simulator.freeflyer.publishers.PositionPublisher;
import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;
import gov.nasa.rapid.v2.e4.message.helpers.ParameterList;
import gov.nasa.rapid.v2.e4.system.RapidEntityFactory;
import gov.nasa.rapid.v2.e4.util.RapidUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import rapid.Ack;
import rapid.AckCompletedStatus;
import rapid.AckDataWriter;
import rapid.Command;
import rapid.CommandConfig;
import rapid.CommandConfigDataWriter;
import rapid.CommandDataWriter;

import com.rti.dds.infrastructure.InstanceHandle_t;

/**
 * Publishes CommandConfig
 */
public class CommandSubsystemPublisher implements IRapidMessageListener{
    private static final Logger logger = Logger.getLogger(CommandSubsystemPublisher.class);

    protected int sleepTime           = 100;
    public static final int MESSAGE_LIFECYCLE_LIMIT = 100;
    
    protected final String srcName    = PositionPublisher.class.getSimpleName();
    
    protected CommandConfig 		  config;
    protected CommandConfigDataWriter configWriter;
    protected InstanceHandle_t        configInstance;
    
    protected Command				  sample;
    protected CommandDataWriter		  sampleWriter;
    protected InstanceHandle_t        sampleInstance;
    
    protected Ack					  ack;
    protected AckDataWriter			  ackWriter;
    protected InstanceHandle_t		  ackInstance;
    
    
    public CommandSubsystemPublisher() {
		try{
			createWriters();
			createReaders();
			initializeDataTypes();
		} catch (DdsEntityCreationException e) {
			e.printStackTrace();
		}
	}
    
    /**
     * create the endpoints (i.e. readers and writers)
     */
    public void createWriters() throws DdsEntityCreationException {
        //-- Create the data writers. The Publisher is created automatically
        configWriter = (CommandConfigDataWriter)
                RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID,
                                                    MessageType.COMMAND_CONFIG_TYPE,
                                                    FreeFlyer.getPartition());
        sampleWriter = (CommandDataWriter)
                RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID, 
                                                    MessageType.COMMAND_TYPE, 
                                                    FreeFlyer.getPartition());
        
		ackWriter = (AckDataWriter) RapidEntityFactory.createDataWriter(
													FreeFlyer.PARTICIPANT_ID, MessageType.ACK_TYPE,
													FreeFlyer.getPartition());
    }
    
    public void createReaders(){
    	RapidMessageCollector.instance().addRapidMessageListener(FreeFlyer.PARTICIPANT_ID, FreeFlyer.getAgent(), MessageType.COMMAND_TYPE, this);
    }
    
    /**
     * initialize the data types that we will be publishing
     */
    public void initializeDataTypes() {
        final int serialId = 0;
        //-- Initialize a CommandConfig
        config = new CommandConfig();
        RapidUtil.setHeader(config.hdr, FreeFlyer.getPartition(), srcName, serialId);
        config.availableSubsystemTypes.userData.add(FreeFlyerCommands.getInstance().getFreeFlyerSubsystemType());
        config.availableSubsystems.userData.add(FreeFlyerCommands.getInstance().getFreeFlyerSubsystem());
        
		// -- Initialize a Command
		sample = new Command();
		RapidUtil.setHeader(sample.hdr, FreeFlyer.getPartition(), srcName, serialId);

		ack = new Ack();
		RapidUtil.setHeader(ack.hdr, FreeFlyer.getPartition(), srcName, serialId);
		ack.completedStatus = AckCompletedStatus.ACK_COMPLETED_OK;
		

        //-- register the data instances *after* we have set
        //   assetName and participantName in headers (i.e. the keyed fields)
        if(configWriter != null)
            configInstance = configWriter.register_instance(config);
        if(sampleWriter != null)
            sampleInstance = sampleWriter.register_instance(sample);
        if(ackWriter != null)
        	ackInstance = ackWriter.register_instance(ack);
    }
    
    public void publishConfig() {
        //-- publish the Config
        logger.info("Publishing CommandConfig...");
        configWriter.write(config, configInstance);
    }
    
   
    
//    public void publishSample(final FreeFlyerCommands.COMMANDS command,final String arg) throws InterruptedException {
//    	
//    	switch (command) {
//		case GO_CMD:
//				publishSample(FreeFlyerCommands.getInstance().createGoCommand(arg));
//			break;
//		case STOP_CMD:
//				publishSample(FreeFlyerCommands.getInstance().createStopCommand());
//			break;
//		default:
//			break;
//		}
//    	
//    }
    
//    public void publishSample(final Command command) throws InterruptedException {
//        sample.hdr.timeStamp = System.currentTimeMillis();
//        sample = command;
//
//        if(sampleWriter != null) {
//            sampleWriter.write(sample, sampleInstance);
//        }
//        else {
//            logger.info("sampleWriter is null");
//        }
//
//    }
    
    public void publishStatus(final AckCompletedStatus status ){
    	if(ackWriter != null){
    		ack.completedStatus = status;
    		ackWriter.write(ack, ackInstance);
    		System.out.println(status.toString());
    	}else{
    		logger.info("ackWriter is null");
    	}
    	
    }

	@Override
	public void onRapidMessageReceived(Agent agent, MessageType msgType,
			Object msgObj, Object cfgObj) {
//		if(msgType.equals(MessageType.COMMAND_TYPE)) {
//			try{
//				publishStatus(AckCompletedStatus.ACK_COMPLETED_NOT);
//				Command command = (Command)msgObj;
//				String cmdMessage = "Command: "+command.cmdName+" - arg:";
//				ParameterList para = new ParameterList();
//				para.set(command.arguments.userData);
//				for(String arg : para.names()){
//					cmdMessage += arg+" ";
//				}
//				System.out.println(cmdMessage);
//				publishStatus(AckCompletedStatus.ACK_COMPLETED_OK);
//			}catch(Exception e){
//				publishStatus(AckCompletedStatus.ACK_COMPLETED_EXEC_FAILED);
//			}
//		}else{
//			publishStatus(AckCompletedStatus.ACK_COMPLETED_EXEC_FAILED);
//		}
	}
}

