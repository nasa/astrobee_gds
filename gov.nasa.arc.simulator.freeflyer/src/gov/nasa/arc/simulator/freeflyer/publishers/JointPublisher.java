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
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.system.RapidEntityFactory;
import gov.nasa.rapid.v2.e4.util.RapidUtil;

import org.apache.log4j.Logger;

import rapid.JOINT_ENABLED;
import rapid.JointConfig;
import rapid.JointConfigDataWriter;
import rapid.JointDef;
import rapid.JointSample;
import rapid.JointSampleDataWriter;

import com.rti.dds.infrastructure.InstanceHandle_t;

public class JointPublisher {
	private static final Logger logger = Logger.getLogger(JointPublisher.class);
	private static JointPublisher INSTANCE;   

	protected int sleepTime           = 100;
	public static final int MESSAGE_LIFECYCLE_LIMIT = 100;

	protected final String srcName    = JointPublisher.class.getSimpleName();

	protected JointConfig           config;
	protected JointConfigDataWriter configWriter;
	protected InstanceHandle_t      configInstance;

	protected JointSample           sample;
	protected JointSampleDataWriter sampleWriter;
	protected InstanceHandle_t      sampleInstance;
	
	protected final float openGripperAngle = (float) (45.0 * Math.PI/180.0);
	protected final float closedGripperAngle = (float) (20.0 * Math.PI/180.0);
	

	protected final String JOINT_GROUP_NAME = "ArmJointGroup";
	protected final float NUM_INCREMENTS = 10f;

	public static JointPublisher getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new JointPublisher();
			try {
				INSTANCE.createWriters();
				INSTANCE.initializeDataTypes();
				INSTANCE.publishConfig();
				INSTANCE.publishSample();
			} catch (DdsEntityCreationException e) {
				System.err.println(e);
			}
		}
		return INSTANCE;
	}

	protected JointPublisher() {
	}

	/**
	 * create the endpoints (i.e. readers and writers)
	 */
	public void createWriters() throws DdsEntityCreationException {

		//-- Create the data writers. The Publisher is created automatically
		configWriter = (JointConfigDataWriter)
				RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID,
						MessageType.JOINT_CONFIG_TYPE,
						FreeFlyer.getPartition());
		sampleWriter = (JointSampleDataWriter)
				RapidEntityFactory.createDataWriter(FreeFlyer.PARTICIPANT_ID, 
						MessageType.JOINT_SAMPLE_TYPE, 
						FreeFlyer.getPartition());
	}

	public void initializeDataTypes() {
		final int serialId = 0;

		initializeJointConfig(serialId);
		initializeJointState(serialId);

		//-- register the data instances *after* we have set
		//   assetName and participantName in headers (i.e. the keyed fields)
		if(configWriter != null)
			configInstance = configWriter.register_instance(config);
		if(sampleWriter != null)
			sampleInstance = sampleWriter.register_instance(sample);
	}

	protected void initializeJointState(int serialId) {
		sample = new JointSample();
		RapidUtil.setHeader(sample.hdr, FreeFlyer.getPartition(), srcName, serialId);
		setUpAJoint(0);
		setUpAJoint(0);
		setUpAJoint(0);
		setUpAJoint(0);
		
	}
	
	private void setUpAJoint(float initialAngle) {
		sample.anglePos.userData.add(new Float(initialAngle));
		sample.angleVel.userData.add(new Float(0));
		sample.angleAcc.userData.add(new Float(0));
		sample.current.userData.add(new Float(0));
		sample.torque.userData.add(new Float(0));
		sample.temperature.userData.add(new Float(20));
		sample.status.userData.add(JOINT_ENABLED.VALUE);
	}
	
	/**
	 * publish the data given offsets and the number of loops to publish 
	 */
	public void publishStowTo(final float finalStowDegree){
		float startStow = sample.anglePos.userData.getFloat(0);
		float startPan = sample.anglePos.userData.getFloat(1);
		float startTilt = sample.anglePos.userData.getFloat(2);
		
		float stowSteps = ((float)Math.toRadians(finalStowDegree) - startStow) / NUM_INCREMENTS;
		float panSteps = (0 - startStow) / NUM_INCREMENTS;
		float tiltSteps = (0 - startTilt) / NUM_INCREMENTS;
		
		for(int i = 1; i <= NUM_INCREMENTS; i++) {
			sample.anglePos.userData.setFloat(0, startStow + i*stowSteps);
			sample.anglePos.userData.setFloat(1, startPan+i*panSteps);
			sample.anglePos.userData.setFloat(1, startTilt+i*tiltSteps);
			
			publishSample();        	
			logger.info("Published JointSample pan = " + sample.anglePos.userData.getFloat(0) 
					+ ", tilt = " + sample.anglePos.userData.getFloat(1) );
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				logger.info("Interrupted.");
			}
		}
		logger.info("Done, Stowing.");
	}
	public void publishPanAndTiltTo(final float finalPan, final float finalTilt) {
		
		float startTilt = sample.anglePos.userData.getFloat(0);
		float startPan = sample.anglePos.userData.getFloat(1);
		
		float tiltIncrement = (finalTilt - startTilt) / NUM_INCREMENTS;
		float panIncrement = (finalPan - startPan) / NUM_INCREMENTS;
		
		
		for(int i = 1; i <= NUM_INCREMENTS; i++) {
			sample.anglePos.userData.setFloat(0, startTilt + i*tiltIncrement);
			sample.anglePos.userData.setFloat(1, startPan + i*panIncrement);
			
			publishSample();        	
			logger.info("Published JointSample pan = " + sample.anglePos.userData.getFloat(1) 
					+ ", tilt = " + sample.anglePos.userData.getFloat(0) );

			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				logger.info("Interrupted.");
			}
		}
		logger.info("Done, exiting.");
	}
	
	public void publishUnstow(){
		
	}
	
	public void publishOpenGripper() {
		sample.anglePos.userData.setFloat(2, -openGripperAngle);
		sample.anglePos.userData.setFloat(3, openGripperAngle);
		publishSample();
	}
	
	public void publishClosedGripper() {
		sample.anglePos.userData.setFloat(2, -closedGripperAngle);
		sample.anglePos.userData.setFloat(3, closedGripperAngle);
		publishSample();
	}

	protected void initializeJointConfig(int serialId) {
		config = new JointConfig();
		RapidUtil.setHeader(config.hdr, FreeFlyer.getPartition(), srcName, serialId);
		config.jointGroupName = JOINT_GROUP_NAME;
		
		JointDef panJointDef = new JointDef();
		panJointDef.frameName = "arm pan joint";
		panJointDef.dof = "x";
		config.jointDefinitions.userData.add(panJointDef);

		JointDef tiltJointDef = new JointDef();
		tiltJointDef.frameName = "arm tilt joint";
		tiltJointDef.dof = "y";
		config.jointDefinitions.userData.add(tiltJointDef);
		
		JointDef gripperRightJointDef = new JointDef();
		gripperRightJointDef.frameName = "arm gripper joint";
		gripperRightJointDef.dof = "z";
		config.jointDefinitions.userData.add(gripperRightJointDef);
		
		JointDef gripperLeftJointDef = new JointDef();
		gripperLeftJointDef.frameName = "arm gripper joint";
		gripperLeftJointDef.dof = "z";
		config.jointDefinitions.userData.add(gripperLeftJointDef);
	}

	public void publishConfig() {
		//-- publish the Config
		logger.info("Publishing JointConfig...");
		configWriter.write(config, configInstance);
	}

	public void publishSample(){
		sample.hdr.timeStamp = System.currentTimeMillis();

		if(sampleWriter != null) {
			sampleWriter.write(sample, sampleInstance);
		}
		else {
			logger.info("sampleWriter is null");
		}
	}    

}
