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
package gov.nasa.arc.simulator.smartdock;

import gov.nasa.arc.simulator.freeflyer.FreeFlyerPreferences;
import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.dds.rti.preferences.DdsPreferences;
import gov.nasa.dds.rti.system.DdsEntityFactory;
import gov.nasa.dds.rti.system.DomainParticipantFactoryConfig;
import gov.nasa.dds.rti.system.RtiDds;
import gov.nasa.dds.rti.util.TypeSupportUtil;
import gov.nasa.dds.system.Dds;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.util.RapidTypeSupportUtil;
import gov.nasa.rapid.v2.util.RapidExtArcTypeSupportUtil;

import java.io.File;

import com.rti.dds.target.RtiDdsTarget;


public class SmartDock { 
	public final static String PARTICIPANT_ID  = "SmartDockParticipant";
	public final static String BERTH_ONE_FLAG = "-berth1";
	public final static String BERTH_TWO_FLAG = "-berth2";
	private static Agent agent = Agent.SmartDock;
	
	public static void main(final String args[]) {
		SmartDock smartdock = new SmartDock();
		//smartdock.publishData();
		
		String berthOneOccupant = getValueOfParameter(args, BERTH_ONE_FLAG);
		String berthTwoOccupant = getValueOfParameter(args, BERTH_TWO_FLAG);
		
		SmartDockPublisher.getInstance().setupBerths(berthOneOccupant, berthTwoOccupant);
		SmartDockPublisher.getInstance().publishData();
		SmartDockListener listen = new SmartDockListener();
		listen.createReader();
	}
	
	// if user gave flag param, return the next token. o.w. return null.
	private static String getValueOfParameter(String[] args, String param) {
		for(int i=0; i<args.length-1; i++) {
			if(args[i].equals(param)) {
				return args[i+1];
			}
		}
		return null;
	}
	
	public static Agent getAgent() {
		return agent;
	}
	
	public static String getPartition() {
		return agent.name();
	}

	public SmartDock() {
		try {
			// setup the DDS
			setupDds();
			// create the participant
			createParticipant(PARTICIPANT_ID);
		} catch (final DdsEntityCreationException e) {
			// TODO Auto-generated catch block
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
		SmartDockPreferences smartDockPreferences = new SmartDockPreferences();
		smartDockPreferences.setPeersFile(discoveryPeersFileName);
		DdsPreferences.setImpl(smartDockPreferences);
		
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
				SmartDockPreferences.DOMAIN_ID,
				DdsPreferences.getQosLibrary(SmartDock.PARTICIPANT_ID),
				DdsPreferences.getQosProfile(SmartDock.PARTICIPANT_ID),
				null, null);
	}

	public void publishData(){
		try {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					while(true){
						SmartDockPublisher.getInstance().publishData();
						System.out.println("Publishing SmartDock state from run()");
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				}
			}).start();
			SmartDockListener listen = new SmartDockListener();
			listen.createReader();
			
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
