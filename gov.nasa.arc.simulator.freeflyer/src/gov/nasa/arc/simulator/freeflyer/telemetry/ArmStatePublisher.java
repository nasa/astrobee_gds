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
package gov.nasa.arc.simulator.freeflyer.telemetry;

import gov.nasa.arc.simulator.freeflyer.FreeFlyer;
import gov.nasa.arc.simulator.freeflyer.datatodisk.DataTopicsListPublisher;
import gov.nasa.arc.simulator.freeflyer.publishers.JointPublisher;
import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.system.RapidEntityFactory;
import gov.nasa.rapid.v2.e4.util.RapidUtil;

import org.apache.log4j.Logger;

import rapid.ext.astrobee.ArmGripperState;
import rapid.ext.astrobee.ArmJointState;
import rapid.ext.astrobee.ArmState;
import rapid.ext.astrobee.ArmStateDataWriter;

import com.rti.dds.infrastructure.InstanceHandle_t;

public class ArmStatePublisher {
	private static final Logger logger = Logger.getLogger(ArmStatePublisher.class);
	private static ArmStatePublisher INSTANCE;

	private ArmState           sample;
	private ArmStateDataWriter sampleWriter;
	private InstanceHandle_t   sampleInstance;
	private final float STOWED_PAN_VALUE = 0;
	private final float STOWED_TILT_VALUE = (float)Math.toRadians(180);
	private final float PERCHED_PAN_VALUE = 0;
	private final float PERCHED_TILT_VALUE = 0;
	private JointPublisher jointPublisher;
	
	protected final String srcName    = DataTopicsListPublisher.class.getSimpleName();
	String[] topicNames;

	public static ArmStatePublisher getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new ArmStatePublisher();
			try {
				INSTANCE.initializeDataTypes();
				INSTANCE.createWriters();
				INSTANCE.publishSample();
			} catch (DdsEntityCreationException e) {
				System.err.println(e);
				System.err.print(e.getStackTrace());
			}
		}
		return INSTANCE;
	}

	private ArmStatePublisher() {
		jointPublisher = JointPublisher.getInstance();
	}

	/**
	 * create the endpoints (i.e. readers and writers)
	 */
	 public void createWriters() throws DdsEntityCreationException {

		 //-- Create the data writers. The Publisher is created automatically
		 sampleWriter = (ArmStateDataWriter)
				 RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID, 
						 MessageTypeExtAstro.ARM_STATE_TYPE, 
						 FreeFlyer.getPartition());
	 }

	 /**
	  * initialize the data types that we will be publishing
	  */
	 public void initializeDataTypes() {
		 final int serialId = 0;
		 sample = new ArmState();
		 RapidUtil.setHeader(sample.hdr, FreeFlyer.getPartition(), srcName, serialId);
		 sample.jointState = ArmJointState.ARM_JOINT_STATE_STOWED;
		 sample.gripperState = ArmGripperState.ARM_GRIPPER_STATE_CLOSED;
		 
		 //-- register the data instances *after* we have set
		 //   assetName and participantName in headers (i.e. the keyed fields)
		 if(sampleWriter != null)
			 sampleInstance = sampleWriter.register_instance(sample);
	 }
	 
	 public void publishStateChangeToPerching() {
		 // move arm
		 sample.jointState = ArmJointState.ARM_JOINT_STATE_DEPLOYING;
		 publishSample();
		 jointPublisher.publishPanAndTiltTo(PERCHED_PAN_VALUE, PERCHED_TILT_VALUE);
		 
		 // open gripper
		 jointPublisher.publishOpenGripper();
		 sample.gripperState = ArmGripperState.ARM_GRIPPER_STATE_OPEN;
		 publishSample();
	 }
	 
	 public void publishGripperOpen() {
		 // open gripper
		 jointPublisher.publishOpenGripper();
		 sample.gripperState = ArmGripperState.ARM_GRIPPER_STATE_OPEN;
	 }
	 
	 public void publishGripperClosed() {
		 jointPublisher.publishClosedGripper();
		 sample.gripperState = ArmGripperState.ARM_GRIPPER_STATE_CLOSED;
	 }
	 
	 public void publishStateChangeToPerched() {
		 // close gripper
		 jointPublisher.publishOpenGripper();
		 sample.gripperState = ArmGripperState.ARM_GRIPPER_STATE_OPEN;
		 
		 // report perched state
		 sample.jointState = ArmJointState.ARM_JOINT_STATE_STOPPED;
		 publishSample();
	 }
	 
	 public void publishStateChangeToUnperched() {
		 // open gripper
		 jointPublisher.publishOpenGripper();
		 sample.jointState = ArmJointState.ARM_JOINT_STATE_STOWING;
		 sample.gripperState = ArmGripperState.ARM_GRIPPER_STATE_OPEN;
		 publishSample();
		 
		 jointPublisher.publishPanAndTiltTo(STOWED_PAN_VALUE, STOWED_TILT_VALUE);
		 
		 jointPublisher.publishClosedGripper();
		 sample.gripperState = ArmGripperState.ARM_GRIPPER_STATE_CLOSED;
		 sample.jointState = ArmJointState.ARM_JOINT_STATE_STOWED;
		 publishSample();
	 }
	 
	 public void publishArmAngleChange(float pan, float tilt) {
		 sample.jointState = ArmJointState.ARM_JOINT_STATE_MOVING;
		 publishSample();
		 jointPublisher.publishPanAndTiltTo(pan, tilt);
		 sample.jointState = ArmJointState.ARM_JOINT_STATE_STOPPED;
		 publishSample();
	 }
	
	 public void publishMobilityStateChange(ArmJointState amState) {
		 sample.jointState = amState;
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
}
