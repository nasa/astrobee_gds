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
package gov.nasa.arc.simulator.freeflyer.compress;

import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateGds;
import gov.nasa.arc.simulator.freeflyer.FreeFlyer;
import gov.nasa.arc.simulator.freeflyer.datatodisk.DataToDiskSubsystem;
import gov.nasa.arc.simulator.freeflyer.inertia.InertialPropertiesSubsystem;
import gov.nasa.arc.simulator.freeflyer.plan.PlanSimulator;
import gov.nasa.arc.simulator.freeflyer.publishers.AgentStatePublisher;
import gov.nasa.arc.simulator.freeflyer.publishers.GuestScienceApkStatePublisher;
import gov.nasa.arc.simulator.freeflyer.publishers.GuestScienceDataPublisher;
import gov.nasa.arc.simulator.freeflyer.subsystem.accesscontrol.AccessControlSubsystem;
import gov.nasa.arc.simulator.freeflyer.subsystem.command.AckPublisher;
import gov.nasa.arc.simulator.freeflyer.subsystem.command.CommandEchoPublisher;
import gov.nasa.arc.simulator.freeflyer.subsystem.command.TeleopSubsystem;
import gov.nasa.arc.simulator.freeflyer.telemetry.ArmStatePublisher;
import gov.nasa.arc.simulator.freeflyer.telemetry.CompressedFileAckPublisher;
import gov.nasa.arc.simulator.freeflyer.telemetry.SimulatorAggregateAstrobeeState;
import gov.nasa.arc.simulator.freeflyer.telemetry.SimulatorAstrobeeStateGds;
import gov.nasa.arc.simulator.freeflyer.telemetry.TelemetryPublisher;
import gov.nasa.rapid.idl.ext.astrobee.message.MessageTypeExtAstro;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.zip.Inflater;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import rapid.ACCESSCONTROL_METHOD_REQUESTCONTROL;
import rapid.ADMIN_METHOD_NOOP;
import rapid.ADMIN_METHOD_SHUTDOWN;
import rapid.Command;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF;
import rapid.MOBILITY_METHOD_STOPALLMOTION;
import rapid.ParameterUnion;
import rapid.PositionSample;
import rapid.ext.astrobee.ACCESSCONTROL_METHOD_GRAB_CONTROL;
import rapid.ext.astrobee.ADMIN_METHOD_REACQUIRE_POSITION;
import rapid.ext.astrobee.ADMIN_METHOD_WAKE;
import rapid.ext.astrobee.ARM_METHOD_ARM_PAN_AND_TILT;
import rapid.ext.astrobee.ARM_METHOD_GRIPPER_CONTROL;
import rapid.ext.astrobee.CompressedFile;
import rapid.ext.astrobee.GUESTSCIENCE_METHOD_CUSTOM_GUEST_SCIENCE;
import rapid.ext.astrobee.GUESTSCIENCE_METHOD_START_GUEST_SCIENCE;
import rapid.ext.astrobee.GUESTSCIENCE_METHOD_STOP_GUEST_SCIENCE;
import rapid.ext.astrobee.MOBILITY_METHOD_AUTO_RETURN;
import rapid.ext.astrobee.MOBILITY_METHOD_DOCK;
import rapid.ext.astrobee.MOBILITY_METHOD_PERCH;
import rapid.ext.astrobee.MOBILITY_METHOD_UNDOCK;
import rapid.ext.astrobee.MOBILITY_METHOD_UNPERCH;
import rapid.ext.astrobee.PLAN_METHOD_PAUSE_PLAN;
import rapid.ext.astrobee.PLAN_METHOD_RUN_PLAN;
import rapid.ext.astrobee.PLAN_METHOD_SET_PLAN;
import rapid.ext.astrobee.PLAN_METHOD_SKIP_PLAN_STEP;
import rapid.ext.astrobee.SETTINGS_METHOD_SET_CAMERA;
import rapid.ext.astrobee.SETTINGS_METHOD_SET_CAMERA_STREAMING;
import rapid.ext.astrobee.SETTINGS_METHOD_SET_CHECK_OBSTACLES;
import rapid.ext.astrobee.SETTINGS_METHOD_SET_CHECK_ZONES;
import rapid.ext.astrobee.SETTINGS_METHOD_SET_ENABLE_AUTO_RETURN;
import rapid.ext.astrobee.SETTINGS_METHOD_SET_HOLONOMIC_MODE;
import rapid.ext.astrobee.SETTINGS_METHOD_SET_INERTIA;
import rapid.ext.astrobee.SETTINGS_METHOD_SET_OPERATING_LIMITS;
import rapid.ext.astrobee.SETTINGS_METHOD_SET_TELEMETRY_RATE;
import rapid.ext.astrobee.SETTINGS_METHOD_SET_ZONES;

import com.rti.dds.infrastructure.ByteSeq;

public class SimulatorCommandHandler implements IRapidMessageListener {
	private static final Logger logger = Logger.getLogger(SimulatorCommandHandler.class);
	
	protected int numLoops            = 100;
	protected int sleepTime           = 500;

	protected final String srcName    = AgentStatePublisher.class.getSimpleName();

	protected boolean planLoaded = false;
	private Thread planRunningThread;
	private CompressedFile compressed, compressedZones;

	protected SimulatorAstrobeeStateGds simAstrobeeState= (SimulatorAstrobeeStateGds) SimulatorAggregateAstrobeeState.getInstance().getAstrobeeState();

	private PositionSample positionSample;
	
	public void createReaders(){
		RapidMessageCollector.instance().addRapidMessageListener(FreeFlyer.PARTICIPANT_ID, FreeFlyer.getAgent(), MessageTypeExtAstro.ZONES_COMPRESSED_TYPE, this);
		RapidMessageCollector.instance().addRapidMessageListener(FreeFlyer.PARTICIPANT_ID, FreeFlyer.getAgent(), MessageTypeExtAstro.COMPRESSED_FILE_TYPE, this);
		RapidMessageCollector.instance().addRapidMessageListener(FreeFlyer.PARTICIPANT_ID, FreeFlyer.getAgent(), MessageTypeExtAstro.DATA_TO_DISK_COMPRESSED_TYPE, this);
		RapidMessageCollector.instance().addRapidMessageListener(FreeFlyer.PARTICIPANT_ID, FreeFlyer.getAgent(), MessageType.COMMAND_TYPE, this);
		RapidMessageCollector.instance().addRapidMessageListener(FreeFlyer.PARTICIPANT_ID, FreeFlyer.getAgent(), MessageType.POSITION_SAMPLE_TYPE, this);
	}

	private boolean loadPlan(CompressedFile file){
		try{
			final ByteSeq seq = file.compressedFile.userData;

			final Inflater inflater = new Inflater();
			inflater.setInput(seq.toArrayByte(new byte[seq.size()]));

			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			final byte[] buf = new byte[1024];
			while(!inflater.finished()){
				baos.write(buf,0,inflater.inflate(buf));
			}
			inflater.end();
			final File recievedFile = File.createTempFile("tmpPlan-"+System.currentTimeMillis(), ".fplan");
			FileUtils.writeByteArrayToFile(recievedFile, baos.toByteArray());

			PlanSimulator.getInstance().readPlan(recievedFile);
			recievedFile.delete();
			planLoaded = true;
			return true;
		}catch(Exception e){
			System.err.println(e);
			return false;
		}
	}

	private void runPlan(final String cmdId) {
		if(planLoaded) {
			AckPublisher.getInstance().publishExecutingAck(cmdId);
			publishPlanExecutingState();

			Runnable r = new Runnable() {
				public void run() {
					try {
						runThePlan();
					} catch(InterruptedException e) {
						System.out.println("The plan was paused");
						publishPlanPausedState();
					}
					AckPublisher.getInstance().publishCompletedGoodAck(cmdId);
				}
			};

			planRunningThread = new Thread(r);
			planRunningThread.start();
		}
	}

	private void runThePlan() throws InterruptedException {
		PlanSimulator.getInstance().sendPlanData();
		planLoaded = false;
		publishPlanDoneState();
	}

	@Override
	public void onRapidMessageReceived(Agent agent, MessageType msgType,
			Object msgObj, Object cfgObj) {
		if(msgType.equals(MessageTypeExtAstro.COMPRESSED_FILE_TYPE)){
			compressed = (CompressedFile)msgObj;
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			publishFileType(compressed.id);
			logger.warn("published CompressedFileAck");
			
		} else if(msgType.equals(MessageTypeExtAstro.DATA_TO_DISK_COMPRESSED_TYPE)) {
			
			CompressedFile compressed = (CompressedFile)msgObj;
			DataToDiskSubsystem.getInstance().acceptCompressedFile(compressed);
			
		} else if(msgType.equals(MessageTypeExtAstro.ZONES_COMPRESSED_TYPE)) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			compressedZones = (CompressedFile)msgObj;
			publishFileType(compressedZones.id);
			System.out.println("############### Received KeepoutsFile ################");
//			AckPublisher.getInstance().publishCompletedGoodAck("Keepouts Received");
			
		} else if(msgType.equals(MessageType.POSITION_SAMPLE_TYPE)) {
			PositionSample ps = (PositionSample) msgObj;
			positionSample = ps;
		} else if(msgType.equals(MessageType.COMMAND_TYPE)) {
			Command cmd = (Command)msgObj;
			CommandEchoPublisher.getInstance().publishCommandEcho(cmd);
			if(cmd.cmdName.equals(PLAN_METHOD_SET_PLAN.VALUE)){
//				AckPublisher.getInstance().publishCompletedFailedAck(cmd.cmdId);
				if(loadPlan(compressed)) {
					publishPlanReadyState();
					AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
				} else {
					AckPublisher.getInstance().publishCompletedBadAck(cmd.cmdId);
				}
			}else if(cmd.cmdName.equals(PLAN_METHOD_RUN_PLAN.VALUE)) {
				runPlan(cmd.cmdId);
			} 
			else if(cmd.cmdName.equals(PLAN_METHOD_SKIP_PLAN_STEP.VALUE)) {
				boolean success = PlanSimulator.getInstance().skipPlanStep();
				if(success) {
					AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
				} else {
					AckPublisher.getInstance().publishCompletedBadAck(cmd.cmdId);
				}
			}
			else if(cmd.cmdName.equals(PLAN_METHOD_PAUSE_PLAN.VALUE)) {
				executePausePlan(cmd);
			}
			else if(cmd.cmdName.equals(SETTINGS_METHOD_SET_ZONES.VALUE)) {
				// Send back the current compressedZones we have
				CompressZonesPublisher.getInstance(agent.name()).publishCompressedFile(compressedZones);
				AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
			}
			else if(cmd.cmdName.equals(ACCESSCONTROL_METHOD_REQUESTCONTROL.VALUE)) {
				AccessControlSubsystem.getInstance().sendCookie();
				AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
			}
			else if(cmd.cmdName.equals(ACCESSCONTROL_METHOD_GRAB_CONTROL.VALUE)) {
				if(AccessControlSubsystem.getInstance().grabControl(cmd)) {
					AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
					logger.info("Sending Completed Ack for Grab Control");
				} else {
					AckPublisher.getInstance().publishCompletedBadAck(cmd.cmdId);
					logger.info("Sending Bad Ack for Grab Control");
				}
			}
			else if(cmd.cmdName.equals(MOBILITY_METHOD_SIMPLEMOVE6DOF.VALUE)) {
				AckPublisher.getInstance().publishExecutingAck(cmd.cmdId);
				TeleopSubsystem.getInstance().doMoveCommand(cmd, positionSample);
				AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
//				AckPublisher.getInstance().publishCompletedFailedAck(cmd.cmdId);
			}
			else if(cmd.cmdName.equals(ADMIN_METHOD_WAKE.VALUE)) {
				publishJustWokeUpState();
				AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
			}
			else if(cmd.cmdName.equals(ADMIN_METHOD_SHUTDOWN.VALUE)) {
				AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
				System.exit(0);
			}
			else if(cmd.cmdName.equals(MOBILITY_METHOD_AUTO_RETURN.VALUE)) {
				AckPublisher.getInstance().publishCompletedFailedAck(cmd.cmdId, "Simulator has not implemented Dock Automatically");
			}
			else if(cmd.cmdName.equals(ADMIN_METHOD_REACQUIRE_POSITION.VALUE)) {
				AckPublisher.getInstance().publishCompletedFailedAck(cmd.cmdId, "Simulator has not implemented Reacquire Position");
			}
			else if(cmd.cmdName.equals(MOBILITY_METHOD_DOCK.VALUE)) {
				AckPublisher.getInstance().publishExecutingAck(cmd.cmdId);
				publishDockingState();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				publishDockedState();
				AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
			}
			else if(cmd.cmdName.equals(MOBILITY_METHOD_UNDOCK.VALUE)) {
				AckPublisher.getInstance().publishExecutingAck(cmd.cmdId);
				publishUndockingState();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				publishUndockedState();
				AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
			}
			else if(cmd.cmdName.equals(MOBILITY_METHOD_PERCH.VALUE)) {
				AckPublisher.getInstance().publishExecutingAck(cmd.cmdId);
				publishPerchingState();
				publishPerchedState();
				AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
			}
			else if(cmd.cmdName.equals(MOBILITY_METHOD_UNPERCH.VALUE)) {
				AckPublisher.getInstance().publishExecutingAck(cmd.cmdId);
				publishUnperchedState();
				AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
			}
			else if(cmd.cmdName.equals(ARM_METHOD_GRIPPER_CONTROL.VALUE)) {
				
				AckPublisher.getInstance().publishExecutingAck(cmd.cmdId);
				publishGripperControl(cmd);
				AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
			}
			else if(cmd.cmdName.equals(ARM_METHOD_ARM_PAN_AND_TILT.VALUE)) {
				AckPublisher.getInstance().publishExecutingAck(cmd.cmdId);
				executePanAndTilt(cmd);
				AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
			}
			else if(cmd.cmdName.equals(SETTINGS_METHOD_SET_CAMERA.VALUE)) {
				TelemetryPublisher.getInstance().setCameraAndPublishSample(cmd);
				AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
			}
			else if(cmd.cmdName.equals(SETTINGS_METHOD_SET_CAMERA_STREAMING.VALUE)) {
				TelemetryPublisher.getInstance().setCameraStreamingAndPublishSample(cmd);
				AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
			}
			else if(cmd.cmdName.equals(SETTINGS_METHOD_SET_CHECK_OBSTACLES.VALUE)) {
				setCheckObstacles(cmd);
				AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
			}
			else if(cmd.cmdName.equals(SETTINGS_METHOD_SET_CHECK_ZONES.VALUE)) {
				setCheckZones(cmd);
				AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
			}
			else if(cmd.cmdName.equals(SETTINGS_METHOD_SET_ENABLE_AUTO_RETURN.VALUE)) {
				setEnableAutoreturn(cmd);
				AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
			}
			else if(cmd.cmdName.equals(SETTINGS_METHOD_SET_HOLONOMIC_MODE.VALUE)) {
				setHolonomicMode(cmd);
				AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
			}
			else if(cmd.cmdName.equals(SETTINGS_METHOD_SET_INERTIA.VALUE)) {
				InertialPropertiesSubsystem.getInstance().updateInertialProperties(cmd);
				AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
			}
			else if(cmd.cmdName.equals(SETTINGS_METHOD_SET_OPERATING_LIMITS.VALUE)) {
				updateAndPublishOperatingLimits(cmd);
				AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
			}
			else if(cmd.cmdName.equals(SETTINGS_METHOD_SET_TELEMETRY_RATE.VALUE)) {
				TelemetryPublisher.getInstance().changeTelemetryRateAndPublishSample(cmd);
				AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
			}
			else if(cmd.cmdName.equals(ADMIN_METHOD_NOOP.VALUE)) {
				AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
			}
			else if(cmd.cmdName.equals(GUESTSCIENCE_METHOD_START_GUEST_SCIENCE.VALUE)) {
				GuestScienceApkStatePublisher.getInstance().startApk(cmd);
				AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
			}
			else if(cmd.cmdName.equals(GUESTSCIENCE_METHOD_STOP_GUEST_SCIENCE.VALUE)) {
				GuestScienceApkStatePublisher.getInstance().stopApk(cmd);
				AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
			}
			else if(cmd.cmdName.equals(GUESTSCIENCE_METHOD_CUSTOM_GUEST_SCIENCE.VALUE)) {
				AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
				final ParameterUnion pu = (ParameterUnion)cmd.arguments.userData.get(0);
				final ParameterUnion ar = (ParameterUnion)cmd.arguments.userData.get(1);
				GuestScienceDataPublisher.getInstance().sendManuallyUploadSuccess(pu.s,ar.s);
			}
			else if(cmd.cmdName.equals(MOBILITY_METHOD_STOPALLMOTION.VALUE)) {
				executePausePlan(cmd);
			}
		}
	}
	private void executePausePlan(Command cmd) {
		if(planRunningThread != null && planRunningThread.isAlive()) {
			planRunningThread.interrupt();
			AckPublisher.getInstance().publishCompletedGoodAck(cmd.cmdId);
			logger.info("** Sending Completed Ack for Pause");
		} else {
			AckPublisher.getInstance().publishCompletedBadAck(cmd.cmdId);
			logger.info("** Sending Bad Ack for Pause");
		}
	}
	
	private void setEnableAutoreturn(Command cmd) {
		AstrobeeStateGds asg = SimulatorAggregateAstrobeeState.getInstance().getAstrobeeState();
		
		if(asg instanceof SimulatorAstrobeeStateGds) {
			((SimulatorAstrobeeStateGds) asg).setEnableAutoReturn((((ParameterUnion)cmd.arguments.userData.get(0)).b()));
		}
		AgentStatePublisher.getInstance().publishSimulatorAstrobeeState();
	}
	
	private void setHolonomicMode(Command cmd) {
		AstrobeeStateGds asg = SimulatorAggregateAstrobeeState.getInstance().getAstrobeeState();
		
		if(asg instanceof SimulatorAstrobeeStateGds) {
			((SimulatorAstrobeeStateGds) asg).setEnableHolonomic(((ParameterUnion)cmd.arguments.userData.get(0)).b());
		}
		AgentStatePublisher.getInstance().publishSimulatorAstrobeeState();
		
	}
	
	private void setCheckObstacles(Command cmd) {
		AstrobeeStateGds asg = SimulatorAggregateAstrobeeState.getInstance().getAstrobeeState();
		
		if(asg instanceof SimulatorAstrobeeStateGds) {
			((SimulatorAstrobeeStateGds) asg).setCheckObstacles(((ParameterUnion)cmd.arguments.userData.get(0)).b());
		}
		AgentStatePublisher.getInstance().publishSimulatorAstrobeeState();
		
	}
	
	private void setCheckZones(Command cmd) {
		AstrobeeStateGds asg = SimulatorAggregateAstrobeeState.getInstance().getAstrobeeState();
		
		if(asg instanceof SimulatorAstrobeeStateGds) {
			((SimulatorAstrobeeStateGds) asg).setCheckKeepouts(((ParameterUnion)cmd.arguments.userData.get(0)).b());
		}
		AgentStatePublisher.getInstance().publishSimulatorAstrobeeState();
	}
	
	private void updateAndPublishOperatingLimits(Command cmd) {
		simAstrobeeState.setProfileName(((ParameterUnion)cmd.arguments.userData.get(0)).s());
		simAstrobeeState.setFlightMode(((ParameterUnion)cmd.arguments.userData.get(1)).s());
		simAstrobeeState.setTargetLinearVelocity(((ParameterUnion)cmd.arguments.userData.get(2)).f());
		simAstrobeeState.setTargetLinearAccel(((ParameterUnion)cmd.arguments.userData.get(3)).f());
		simAstrobeeState.setTargetAngularVelocity(((ParameterUnion)cmd.arguments.userData.get(4)).f());
		simAstrobeeState.setTargetAngularAccel(((ParameterUnion)cmd.arguments.userData.get(5)).f());
		simAstrobeeState.setCollisionDistance(((ParameterUnion)cmd.arguments.userData.get(6)).f());
		AgentStatePublisher.getInstance().publishSimulatorAstrobeeState();
	}

	private void publishPlanPausedState() {
		// pausing plan does not change mobility state
		simAstrobeeState.setOperatingState(AstrobeeStateGds.OperatingState.READY);
		simAstrobeeState.setPlanExecutionState(AstrobeeStateGds.ExecutionState.PAUSED);
		AgentStatePublisher.getInstance().publishSimulatorAstrobeeState();
	}

	private void publishPerchedState() {
		simAstrobeeState.setSubMobilityState(0);
		simAstrobeeState.setMobilityState(AstrobeeStateGds.MobilityState.PERCHING);
		AgentStatePublisher.getInstance().publishSimulatorAstrobeeState();
		ArmStatePublisher.getInstance().publishStateChangeToPerched();
	}

	private void publishPerchingState() {
		simAstrobeeState.setSubMobilityState(1);
		simAstrobeeState.setMobilityState(AstrobeeStateGds.MobilityState.PERCHING);
		AgentStatePublisher.getInstance().publishSimulatorAstrobeeState();
		ArmStatePublisher.getInstance().publishStateChangeToPerching();
	}
	
	private void publishUnperchedState() {
		simAstrobeeState.setSubMobilityState(1);
		simAstrobeeState.setMobilityState(AstrobeeStateGds.MobilityState.STOPPING);
		simAstrobeeState.setSubMobilityState(0);
		AgentStatePublisher.getInstance().publishSimulatorAstrobeeState();
		ArmStatePublisher.getInstance().publishStateChangeToUnperched();
	}
	
	private void publishGripperControl(Command cmd) {
		boolean open = ((ParameterUnion)cmd.arguments.userData.get(0)).b();
		if(open) {
			ArmStatePublisher.getInstance().publishGripperOpen();
		} else {
			ArmStatePublisher.getInstance().publishGripperClosed();
		}
	}
	
	private void executePanAndTilt(Command cmd) {
		// now this comes in as degrees
		float pan = ((ParameterUnion)cmd.arguments.userData.get(0)).f() * (float)Math.PI / 180.0f;
		float tilt = ((ParameterUnion)cmd.arguments.userData.get(1)).f() * (float)Math.PI / 180.0f;
		
		ArmStatePublisher.getInstance().publishArmAngleChange(pan, tilt);
		
	}
	
	private void publishDockedState() {
		simAstrobeeState.setSubMobilityState(0);
		simAstrobeeState.setMobilityState(AstrobeeStateGds.MobilityState.DOCKING);
		AgentStatePublisher.getInstance().publishSimulatorAstrobeeState();
	}

	private void publishDockingState() {
		simAstrobeeState.setSubMobilityState(1);
		simAstrobeeState.setMobilityState(AstrobeeStateGds.MobilityState.DOCKING);
		AgentStatePublisher.getInstance().publishSimulatorAstrobeeState();
	}

	private void publishUndockingState() {
		simAstrobeeState.setSubMobilityState(-1);
		simAstrobeeState.setMobilityState(AstrobeeStateGds.MobilityState.DOCKING);
		AgentStatePublisher.getInstance().publishSimulatorAstrobeeState();
	}

	private void publishUndockedState() {
		simAstrobeeState.setSubMobilityState(0);
		simAstrobeeState.setMobilityState(AstrobeeStateGds.MobilityState.STOPPING);
		AgentStatePublisher.getInstance().publishSimulatorAstrobeeState();
	}

	private void publishTerminatedState() {
		// XXX NEED TO UPDATE THIS TO ACCOUNT FOR SMART DOCK
		simAstrobeeState.setMobilityState(AstrobeeStateGds.MobilityState.FLYING);
		simAstrobeeState.setOperatingState(AstrobeeStateGds.OperatingState.FAULT);
		simAstrobeeState.setPlanExecutionState(AstrobeeStateGds.ExecutionState.IDLE);
		AgentStatePublisher.getInstance().publishSimulatorAstrobeeState();
	}

	private void publishUnterminatedState() {
		simAstrobeeState.setMobilityState(AstrobeeStateGds.MobilityState.FLYING);
		simAstrobeeState.setOperatingState(AstrobeeStateGds.OperatingState.READY);
		simAstrobeeState.setPlanExecutionState(AstrobeeStateGds.ExecutionState.IDLE);
		AgentStatePublisher.getInstance().publishSimulatorAstrobeeState();
	}

	private void publishJustWokeUpState() {
		simAstrobeeState.setSubMobilityState(0);
		simAstrobeeState.setOperatingState(AstrobeeStateGds.OperatingState.READY);
		simAstrobeeState.setPlanExecutionState(AstrobeeStateGds.ExecutionState.IDLE);
		AgentStatePublisher.getInstance().publishSimulatorAstrobeeState();
	}

	private void publishFileType(int id){
		//simAstrobeeState.setMobilityState(AstrobeeStateGds.MobilityState.IDLE);
		//simAstrobeeState.setOperatingState(AstrobeeStateGds.OperatingState.READY);
		//simAstrobeeState.setPlanExecutionState(AstrobeeStateGds.ExecutionState.IDLE);
		//AgentStatePublisher.getInstance().publishSimulatorAstrobeeState();
		CompressedFileAckPublisher.getInstance().publishFileAckSample(id);
		
	}
	private void publishPlanReadyState() {
		simAstrobeeState.setSubMobilityState(0);
		simAstrobeeState.setOperatingState(AstrobeeStateGds.OperatingState.READY);
		simAstrobeeState.setPlanExecutionState(AstrobeeStateGds.ExecutionState.PAUSED);
		AgentStatePublisher.getInstance().publishSimulatorAstrobeeState();
	}

	private void publishPlanExecutingState() {
		simAstrobeeState.setMobilityState(AstrobeeStateGds.MobilityState.FLYING);
		simAstrobeeState.setOperatingState(AstrobeeStateGds.OperatingState.PLAN_EXECUTION);
		simAstrobeeState.setPlanExecutionState(AstrobeeStateGds.ExecutionState.EXECUTING);
		simAstrobeeState.setSubMobilityState(0);
		AgentStatePublisher.getInstance().publishSimulatorAstrobeeState();
	}

	private void publishPlanDoneState() {
		simAstrobeeState.setMobilityState(AstrobeeStateGds.MobilityState.FLYING);
		simAstrobeeState.setOperatingState(AstrobeeStateGds.OperatingState.READY);
		simAstrobeeState.setPlanExecutionState(AstrobeeStateGds.ExecutionState.IDLE);
		AgentStatePublisher.getInstance().publishSimulatorAstrobeeState();
	}
}
