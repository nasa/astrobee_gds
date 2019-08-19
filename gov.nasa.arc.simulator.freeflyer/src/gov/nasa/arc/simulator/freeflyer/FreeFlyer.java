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
package gov.nasa.arc.simulator.freeflyer;

import gov.nasa.arc.simulator.freeflyer.compress.SimulatorCommandHandler;
import gov.nasa.arc.simulator.freeflyer.datatodisk.DataToDiskSubsystem;
import gov.nasa.arc.simulator.freeflyer.faults.FaultStateSubsystem;
import gov.nasa.arc.simulator.freeflyer.inertia.InertialPropertiesSubsystem;
import gov.nasa.arc.simulator.freeflyer.plan.PlanSimulator;
import gov.nasa.arc.simulator.freeflyer.publishers.AgentStatePublisher;
import gov.nasa.arc.simulator.freeflyer.publishers.CommStatePublisher;
import gov.nasa.arc.simulator.freeflyer.publishers.ComponentStatePublisher;
import gov.nasa.arc.simulator.freeflyer.publishers.DiskStatePublisher;
import gov.nasa.arc.simulator.freeflyer.publishers.EpsStatePublisher;
import gov.nasa.arc.simulator.freeflyer.publishers.GuestScienceApkStatePublisher;
import gov.nasa.arc.simulator.freeflyer.publishers.GuestScienceDataPublisher;
import gov.nasa.arc.simulator.freeflyer.publishers.PositionPublisher;
import gov.nasa.arc.simulator.freeflyer.subsystem.accesscontrol.AccessControlSubsystem;
import gov.nasa.arc.simulator.freeflyer.subsystem.command.CommandSubsystem;
import gov.nasa.arc.simulator.freeflyer.telemetry.TelemetryPublisher;
import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.dds.rti.preferences.DdsPreferences;
import gov.nasa.dds.rti.system.DdsEntityFactory;
import gov.nasa.dds.rti.system.DomainParticipantFactoryConfig;
import gov.nasa.dds.rti.system.RtiDds;
import gov.nasa.dds.rti.util.TypeSupportUtil;
import gov.nasa.dds.system.Dds;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.agent.Agent.Tag;
import gov.nasa.rapid.v2.e4.util.RapidTypeSupportUtil;
import gov.nasa.rapid.v2.util.RapidExtArcTypeSupportUtil;

import java.io.File;

import com.rti.dds.target.RtiDdsTarget;


public class FreeFlyer {
	public final static String PARTICIPANT_ID  = "FreeFlyerParticipant";
	public final static String AGENT_FLAG = "-agent";
	public final static String THROW_FAULTS_FLAG = "-faults";
	public final static String BERTH_FLAG = "-berth";
	private static Agent agent = Agent.FreeFlyerA;
	private boolean throw_faults = false;
	static float startX = 0;
	static float startY = 0;
	static float startZ = 0;
	
	public static void main(final String args[]) {
		boolean faults = false;
		// setup the agent
		for(int i=0; i<args.length; i++) {
			if(args[i].equals(AGENT_FLAG)) {
				if(args.length > i+1) {
					String agentName = args[i+1];
					try {
						agent = Agent.valueOf(agentName);
					} catch(Exception e) {
						agent = Agent.newAgent(agentName,Tag.ASTROBEE, Tag.FREE_FLYER);
//						System.err.println("Enter the name of a valid Agent");
//						return;
					}
				}
			}
			else if(args[i].equals(THROW_FAULTS_FLAG)) {
				faults = true;
			}
			else if(args[i].equals(BERTH_FLAG)) {
				if(args[i+1].equals("1")) {
					startX = 10.18f;
					startY = -10.53f;
					startZ = 4.35f;
				} else if(args[i+1].equals("2")) {
					startX = 10.18f;
					startY = -11.07f;
					startZ = 4.35f;
				}
			}
		}

		final FreeFlyer freeFlyer = new FreeFlyer(faults);
		freeFlyer.publishData();

		//causing error with simulator not sending anything
		//DdsEntityFactory.destroyAllParticipants();
	}
	
	public static Agent getAgent() {
		return agent;
	}
	
	public static String getPartition() {
		return agent.name();
	}
	
	public FreeFlyer(Agent a){
		agent = a;
		try {
			// setup the DDS
			setupDds();
			// create the participant
			createParticipant(PARTICIPANT_ID);
		} catch (final DdsEntityCreationException e) {
			e.printStackTrace();
		}
	}
	public FreeFlyer(boolean faults) {
		throw_faults = faults;
		try {
			// setup the DDS
			setupDds();
			// create the participant
			createParticipant(PARTICIPANT_ID);
		} catch (final DdsEntityCreationException e) {
			e.printStackTrace();
		} 
	}

	/**
	 * General setup required at initialization
	 */
	public void setupDds() {
		//-- Load the native RTI DDS libraries
		RtiDdsTarget.loadNativeLibraries();

		//-- Set the implementation
		Dds.setDdsImpl(new RtiDds());

		String os = System.getProperty("os.name");
		String discoveryPeersFileName;
		if(os.startsWith("Windows")) {
			discoveryPeersFileName = "NDDS_DISCOVERY_PEERS";
		} else {
			discoveryPeersFileName = new File("").getAbsolutePath() + File.separator + "NDDS_DISCOVERY_PEERS";
		}

		//-- Set the preferences implementation
		FreeFlyerPreferences freeflyerPreferences = new FreeFlyerPreferences();
		freeflyerPreferences.setPeersFile(discoveryPeersFileName);
		DdsPreferences.setImpl(freeflyerPreferences);
		
		//-- Make the RAPID types visible 
		TypeSupportUtil.addImpl(new RapidTypeSupportUtil());
		TypeSupportUtil.addImpl(new RapidExtArcTypeSupportUtil());

		//-- Create the default factory configuration
		final DomainParticipantFactoryConfig dpfConfig = new DomainParticipantFactoryConfig();

		if(os.startsWith("Windows")) {
			dpfConfig.qosUrlGroups = new String[]{"RAPID_QOS_PROFILES.xml"};
		} else {
			String rapidQosPath = new File("").getAbsolutePath() + File.separator + "RAPID_QOS_PROFILES.xml";
			dpfConfig.qosUrlGroups = new String[]{rapidQosPath};
		}
		
		DdsEntityFactory.initDomainParticipantFactory(dpfConfig);
	}

	/**
	 * create the main participant
	 */
	public void createParticipant(final String srcName) throws DdsEntityCreationException {
		//-- create the partitipant
		DdsEntityFactory.createParticipant(srcName, 
				srcName, 
				FreeFlyerPreferences.DOMAIN_ID,//DdsPreferences.getDomainId(FreeFlyer.PARTICIPANT_ID),
				DdsPreferences.getQosLibrary(FreeFlyer.PARTICIPANT_ID),
				DdsPreferences.getQosProfile(FreeFlyer.PARTICIPANT_ID),
				null, null);
	}

	public void publishData(){
		try {
			AccessControlSubsystem.getInstance().publishSample();

			AgentStatePublisher.getInstance().publishSimulatorAstrobeeState();

			SimulatorCommandHandler zip = new SimulatorCommandHandler();
			zip.createReaders();
			PlanSimulator.getInstance();

			CommandSubsystem commandSubsystemPublisher = new CommandSubsystem();
			commandSubsystemPublisher.publishTelemetry();

			CommStatePublisher.getInstance().publishSample();

			ComponentStatePublisher.getInstance().startComponentStateGenerator();

			DataToDiskSubsystem.getInstance();

			DiskStatePublisher.getInstance().startDiskStateGenerator();

			EpsStatePublisher.getInstance().startEpsStateGenerator();

			if(throw_faults) {
				FaultStateSubsystem faultStateSubsystem = FaultStateSubsystem.getInstance();
				faultStateSubsystem.startFaultGenerator();
			}
			
			// Just until we get the GuestScience messages straightened out
			GuestScienceApkStatePublisher.getInstance().publishSimulatorGuestScienceApkState();
			GuestScienceDataPublisher.getInstance().startPublisher();
			InertialPropertiesSubsystem.getInstance();
			
			PositionPublisher.getInstance().publishTelemetry(startX, startY, startZ);

			TelemetryPublisher.getInstance();

		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
