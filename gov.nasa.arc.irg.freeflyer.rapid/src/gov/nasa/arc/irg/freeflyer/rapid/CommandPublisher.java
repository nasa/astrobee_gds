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
package gov.nasa.arc.irg.freeflyer.rapid;

import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.WriterStatus;
import gov.nasa.rapid.v2.e4.message.helpers.ParameterList;
import gov.nasa.rapid.v2.e4.message.publisher.RapidMessagePublisher;
import gov.nasa.rapid.v2.e4.util.RapidUtil;

import java.util.HashMap;

import org.apache.log4j.Logger;

import rapid.ACCESSCONTROL;
import rapid.Command;
import rapid.CommandDef;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_END_LOCATION;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_END_LOCATION_TOLERANCE;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_FRAME_NAME;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_ROT;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_END_LOCATION;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_END_LOCATION_TOLERANCE;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_FRAME_NAME;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_ROT;
import rapid.Mat33f;
import rapid.QueueAction;
import rapid.Vec3d;
import rapid.ext.astrobee.*;

import com.ardor3d.math.Quaternion;
import com.rti.dds.publication.PublicationMatchedStatus;
public class CommandPublisher {
	private static final Logger logger = Logger.getLogger(CommandPublisher.class);
	 
	protected RapidMessagePublisher rapidMessagePublisher;
	protected static HashMap<Agent,CommandPublisher> instances = new HashMap<Agent,CommandPublisher>();
	protected Agent        				self;
	protected Agent						freeFlyerAgent;
	protected String       				partition;
	protected String        			participant;
	protected HashMap<String,String> 	cmdIdAndName = new HashMap<String,String>();
	private ParameterList				paramsSimpleMove6dof, paramsRelativeMove6dof;
	private ParameterList 				powerOnParams, powerOffParams;
	private ParameterList				panAndTiltParams, openGripperParams;
	private ParameterList				grabControlParams;
	private ParameterList				setInertiaParams, setOperatingLimitsParams;
	private ParameterList				setTelemetryParams, setFlashlightBrightnessParams;
	private ParameterList				setCameraParamsParams, setCameraStreamingParams;
	private ParameterList				setHolonomicParams, setCheckObstaclesParams, setCheckKeepoutsParams;
	private ParameterList				startGuestScienceParams, stopGuestScienceParams, guestScienceParams;
	private ParameterList				smartDockWakeParams, smartDockWakeSafeParams;
	private int 						uniqueCounter = 0;

	public static CommandPublisher getInstance(Agent agent) {
		if(instances.get(agent) == null) {
			instances.put(agent, new CommandPublisher(agent));
		}
		return instances.get(agent);
	}

	private CommandPublisher(Agent agent) {
		partition = agent.name();
		participant = Rapid.PrimaryParticipant;
		self = Agent.getEgoAgent();
		freeFlyerAgent = agent;

		rapidMessagePublisher = new RapidMessagePublisher(agent);
		rapidMessagePublisher.createWriter(participant, MessageType.COMMAND_TYPE);
		initializeParams();
	}

	public void sendCommand(Command cmd) {
		waitForMatch(cmd);
		rapidMessagePublisher.writeMessage(participant, MessageType.COMMAND_TYPE, cmd);
		//AckListener.getStaticInstance().onCommandSent(cmd);
	}
	
	protected void waitForMatch(Command cmd) {
		boolean waitingForMatch = true;
		while(waitingForMatch) {
			logger.debug("Querying for PublicationMatched Status for " + cmd.cmdName + ", " + partition);
			PublicationMatchedStatus matchedStatus = 
					(PublicationMatchedStatus) rapidMessagePublisher.getWriterStatus(
							WriterStatus.PublicationMatched, 
							participant, 
							MessageType.COMMAND_TYPE,  
							new PublicationMatchedStatus());

			if(matchedStatus != null && matchedStatus.current_count > 0) {
				waitingForMatch = false;
			} else {
				logger.debug("\tNo matched readers for - " + cmd.cmdName + " on " + rapidMessagePublisher.getAgent());
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public Command buildCommand(CommandDef cdef, String subsystemName) {
		return buildCommand(cdef.name, subsystemName);
	}

	public void sendCheckKeepoutsCommand(boolean enable) {
		Command cmd = buildCommand(SETTINGS_METHOD_SET_CHECK_ZONES.VALUE, SETTINGS.VALUE);
		setCheckKeepoutsParams.set(SETTINGS_METHOD_SET_CHECK_ZONES_PARAM_CHECK_ZONES.VALUE, enable);
		setCheckKeepoutsParams.assign(cmd.arguments.userData);
		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}

	public void sendCheckObstaclesCommand(boolean enable) {
		Command cmd = buildCommand(SETTINGS_METHOD_SET_CHECK_OBSTACLES.VALUE, SETTINGS.VALUE);
		setCheckObstaclesParams.set(SETTINGS_METHOD_SET_CHECK_OBSTACLES_PARAM_CHECK_OBSTACLES.VALUE, enable);
		setCheckObstaclesParams.assign(cmd.arguments.userData);
		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}

	public void sendEnableHolonomicCommand(boolean enable) {
		Command cmd = buildCommand(SETTINGS_METHOD_SET_HOLONOMIC_MODE.VALUE, SETTINGS.VALUE);
		setHolonomicParams.set(SETTINGS_METHOD_SET_HOLONOMIC_MODE_PARAM_ENABLE_HOLONOMIC.VALUE, enable);
		setHolonomicParams.assign(cmd.arguments.userData);
		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}

	public void sendGenericBooleanCommand(String commandName, String subsystemName, String paramName, boolean bool) {
		Command cmd = buildCommand(commandName, subsystemName);
		ParameterList paramsGenericBoolean = new ParameterList();
		paramsGenericBoolean.add(paramName, rapid.DataType.RAPID_BOOL);
		paramsGenericBoolean.set(paramName, bool);
		paramsGenericBoolean.assign(cmd.arguments.userData);
		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}

	public void sendGenericNoParamsCommand(String commandName, String subsystemName) {
		Command cmd = buildCommand(commandName, subsystemName);
		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}

	public void sendGenericOneIntCommand(String commandName, String subsystemName, String paramName, int param) {
		Command cmd = buildCommand(commandName, subsystemName);
		ParameterList paramsGeneric = new ParameterList();
		paramsGeneric.add(paramName, rapid.DataType.RAPID_INT);
		paramsGeneric.set(paramName, param);
		paramsGeneric.assign(cmd.arguments.userData);
		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}

	public void sendGenericOneStringCommand(String commandName, String subsystemName, String paramName, String param) {
		Command cmd = buildCommand(commandName, subsystemName);
		ParameterList paramsGeneric = new ParameterList();
		paramsGeneric.add(paramName, rapid.DataType.RAPID_STRING);
		paramsGeneric.set(paramName, param);
		paramsGeneric.assign(cmd.arguments.userData);
		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}
	
	public void sendGenericStringBooleanCommand(String commandName, String subsystemName, String stringParamName, String stringParam,
			String boolParamName, boolean boolParam) {
		Command cmd = buildCommand(commandName, subsystemName);
		ParameterList paramsGeneric = new ParameterList();
		paramsGeneric.add(stringParamName, rapid.DataType.RAPID_STRING);
		paramsGeneric.set(stringParamName, stringParam);
		paramsGeneric.add(boolParamName, rapid.DataType.RAPID_BOOL);
		paramsGeneric.set(boolParamName, boolParam);
		paramsGeneric.assign(cmd.arguments.userData);
		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}

	public void sendGenericTwoFloatsCommand(String commandName, String subsystemName, 
			String paramName1, float param1, String paramName2, float param2) {
		Command cmd = buildCommand(commandName, subsystemName);
		ParameterList paramsGeneric = new ParameterList();
		paramsGeneric.add(paramName1, rapid.DataType.RAPID_FLOAT);
		paramsGeneric.set(paramName1, param1);
		paramsGeneric.add(paramName2, rapid.DataType.RAPID_FLOAT);
		paramsGeneric.set(paramName2, param2);
		paramsGeneric.assign(cmd.arguments.userData);
		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}
	
	

	public void sendGrabControlCommand(String cookie) {
		Command cmd = buildCommand(ACCESSCONTROL_METHOD_GRAB_CONTROL.VALUE, ACCESSCONTROL.VALUE);
		grabControlParams.set(ACCESSCONTROL_METHOD_GRAB_CONTROL_PARAM_COOKIE.VALUE, cookie);
		grabControlParams.assign(cmd.arguments.userData);	

		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}

	public void sendGuestScienceCommand(String apkName, String commandBody) {
		Command cmd = buildCommand(GUESTSCIENCE_METHOD_CUSTOM_GUEST_SCIENCE.VALUE, GUESTSCIENCE.VALUE);
		guestScienceParams.set(GUESTSCIENCE_METHOD_CUSTOM_GUEST_SCIENCE_PARAM_APK_NAME.VALUE, apkName);
		guestScienceParams.set(GUESTSCIENCE_METHOD_CUSTOM_GUEST_SCIENCE_PARAM_COMMAND.VALUE, commandBody);
		guestScienceParams.assign(cmd.arguments.userData);

		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}

	public void sendOpenGripperCommand(boolean open) {
		Command cmd = buildCommand(ARM_METHOD_GRIPPER_CONTROL.VALUE, ARM.VALUE);
		openGripperParams.set(ARM_METHOD_GRIPPER_CONTROL_PARAM_OPEN.VALUE, open);
		openGripperParams.assign(cmd.arguments.userData);	
		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}

	/** All in degrees */
	public void sendPanAndTiltCommand(double panDegrees, double tiltDegrees) {
		Command cmd = buildCommand(ARM_METHOD_ARM_PAN_AND_TILT.VALUE, ARM.VALUE);
		panAndTiltParams.set(ARM_METHOD_ARM_PAN_AND_TILT_PARAM_PAN.VALUE, (float)panDegrees);
		panAndTiltParams.set(ARM_METHOD_ARM_PAN_AND_TILT_PARAM_TILT.VALUE, (float)tiltDegrees);
		panAndTiltParams.set(ARM_METHOD_ARM_PAN_AND_TILT_PARAM_WHICH.VALUE, ARM_ACTION_TYPE_BOTH.VALUE);
		panAndTiltParams.assign(cmd.arguments.userData);
		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}

	public void sendPowerOffCommand(String item) {
		Command cmd = buildCommand(POWER_METHOD_POWER_OFF_ITEM.VALUE, POWER.VALUE);
		powerOffParams.set(POWER_METHOD_POWER_OFF_ITEM_PARAM_WHICH.VALUE, item);
		powerOffParams.assign(cmd.arguments.userData);
		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}

	public void sendPowerOnCommand(String item) {
		Command cmd = buildCommand(POWER_METHOD_POWER_ON_ITEM.VALUE, POWER.VALUE);
		powerOnParams.set(POWER_METHOD_POWER_ON_ITEM_PARAM_WHICH.VALUE, item);
		powerOnParams.assign(cmd.arguments.userData);
		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}

	public void sendSetCameraParamsCommand(String name, String resolution,
			String cameraMode, float frameRate, float bandwidth) {
		Command cmd = buildCommand(SETTINGS_METHOD_SET_CAMERA.VALUE, SETTINGS.VALUE);

		setCameraParamsParams.set(SETTINGS_METHOD_SET_CAMERA_PARAM_CAMERA_NAME.VALUE, name);
		setCameraParamsParams.set(SETTINGS_METHOD_SET_CAMERA_PARAM_CAMERA_MODE.VALUE, cameraMode);
		setCameraParamsParams.set(SETTINGS_METHOD_SET_CAMERA_PARAM_RESOLUTION.VALUE, resolution);
		setCameraParamsParams.set(SETTINGS_METHOD_SET_CAMERA_PARAM_FRAME_RATE.VALUE, frameRate);
		setCameraParamsParams.set(SETTINGS_METHOD_SET_CAMERA_PARAM_BANDWIDTH.VALUE, bandwidth);

		setCameraParamsParams.assign(cmd.arguments.userData);
		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}

	public void sendSetCameraStreamingCommand(String name, boolean stream) {
		Command cmd = buildCommand(SETTINGS_METHOD_SET_CAMERA_STREAMING.VALUE, SETTINGS.VALUE);

		setCameraStreamingParams.set(SETTINGS_METHOD_SET_CAMERA_STREAMING_PARAM_CAMERA_NAME.VALUE, name);
		setCameraStreamingParams.set(SETTINGS_METHOD_SET_CAMERA_STREAMING_PARAM_STREAM.VALUE, stream);

		setCameraStreamingParams.assign(cmd.arguments.userData);
		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}
	
	public void sendSetFlashlightBrightnessCommand(String flashlightName, float brightness) {
		Command cmd = buildCommand(SETTINGS_METHOD_SET_FLASHLIGHT_BRIGHTNESS.VALUE, SETTINGS.VALUE);
		setFlashlightBrightnessParams.set(SETTINGS_METHOD_SET_FLASHLIGHT_BRIGHTNESS_PARAM_WHICH.VALUE, flashlightName);
		setFlashlightBrightnessParams.set(SETTINGS_METHOD_SET_FLASHLIGHT_BRIGHTNESS_PARAM_BRIGHTNESS.VALUE, brightness);

		setFlashlightBrightnessParams.assign(cmd.arguments.userData);
		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}

	public void sendSetInertiaCommand(String name, float mass, float[] matrix) {
		Command cmd = buildCommand(SETTINGS_METHOD_SET_INERTIA.VALUE, SETTINGS.VALUE);

		Mat33f mat = new Mat33f();
		mat.userData = matrix;

		setInertiaParams.set(SETTINGS_METHOD_SET_INERTIA_PARAM_NAME.VALUE, name);
		setInertiaParams.set(SETTINGS_METHOD_SET_INERTIA_PARAM_MASS.VALUE, mass);
		setInertiaParams.set(SETTINGS_METHOD_SET_INERTIA_PARAM_MATRIX.VALUE, mat);

		setInertiaParams.assign(cmd.arguments.userData);
		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}

	public void sendSetOperatingLimitsCommand(String profileName, String flightMode, 
			float targetLinearVelocity, float targetLinearAccel, float targetAngularVelocity, float targetAngularAccel,
			float collisionDistance) {
		Command cmd = buildCommand(SETTINGS_METHOD_SET_OPERATING_LIMITS.VALUE, SETTINGS.VALUE);

		setOperatingLimitsParams.set(SETTINGS_METHOD_SET_OPERATING_LIMITS_PARAM_PROFILE_NAME.VALUE, profileName);
		setOperatingLimitsParams.set(SETTINGS_METHOD_SET_OPERATING_LIMITS_PARAM_FLIGHT_MODE.VALUE, flightMode);
		setOperatingLimitsParams.set(SETTINGS_METHOD_SET_OPERATING_LIMITS_PARAM_TARGET_LINEAR_VELOCITY.VALUE, targetLinearVelocity);
		setOperatingLimitsParams.set(SETTINGS_METHOD_SET_OPERATING_LIMITS_PARAM_TARGET_LINEAR_ACCELERATION.VALUE, targetLinearAccel);
		setOperatingLimitsParams.set(SETTINGS_METHOD_SET_OPERATING_LIMITS_PARAM_TARGET_ANGULAR_VELOCITY.VALUE, targetAngularVelocity);
		setOperatingLimitsParams.set(SETTINGS_METHOD_SET_OPERATING_LIMITS_PARAM_TARGET_ANGULAR_ACCELERATION.VALUE, targetAngularAccel);
		setOperatingLimitsParams.set(SETTINGS_METHOD_SET_OPERATING_LIMITS_PARAM_COLLISION_DISTANCE.VALUE, collisionDistance);

		setOperatingLimitsParams.assign(cmd.arguments.userData);
		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}

	public void sendSetTelemetryRateCommand(String name, float rate) {
		Command cmd = buildCommand(SETTINGS_METHOD_SET_TELEMETRY_RATE.VALUE, SETTINGS.VALUE);

		setTelemetryParams.set(SETTINGS_METHOD_SET_TELEMETRY_RATE_PARAM_NAME.VALUE, name);
		setTelemetryParams.set(SETTINGS_METHOD_SET_TELEMETRY_RATE_PARAM_RATE.VALUE, rate);

		setTelemetryParams.assign(cmd.arguments.userData);
		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}
	
	public void sendSmartDockWakeCommand(int berthNum){
		Command cmd = buildCommand(ADMIN_METHOD_WAKE.VALUE, ADMIN.VALUE );
		
		smartDockWakeParams.set(ADMIN_METHOD_WAKE_PARAM_BERTH_NUMBER.VALUE,berthNum);
		
		smartDockWakeParams.assign(cmd.arguments.userData);
		sendCommand(cmd);
		
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}
	
	public void sendSmartDockWakeSafeCommand(int berthNum){
		Command cmd = buildCommand(ADMIN_METHOD_WAKE_SAFE.VALUE, ADMIN.VALUE );
		
		smartDockWakeSafeParams.set(ADMIN_METHOD_WAKE_SAFE_PARAM_BERTH_NUMBER.VALUE,berthNum);
		
		smartDockWakeSafeParams.assign(cmd.arguments.userData);
		sendCommand(cmd);
		
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}
	
	public void sendSkipPlanStepCommand() {
		Command cmd = buildCommand(PLAN_METHOD_SKIP_PLAN_STEP.VALUE, PLAN.VALUE);
		
		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}
	
	public void sendStartGuestScienceCommand(String apkName) {
		Command cmd = buildCommand(GUESTSCIENCE_METHOD_START_GUEST_SCIENCE.VALUE, GUESTSCIENCE.VALUE);
		startGuestScienceParams.set(GUESTSCIENCE_METHOD_START_GUEST_SCIENCE_PARAM_APK_NAME.VALUE, apkName);
		startGuestScienceParams.assign(cmd.arguments.userData);
		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}

	public void sendStopGuestScienceCommand(String apkName) {
		Command cmd = buildCommand(GUESTSCIENCE_METHOD_STOP_GUEST_SCIENCE.VALUE, GUESTSCIENCE.VALUE);
		stopGuestScienceParams.set(GUESTSCIENCE_METHOD_STOP_GUEST_SCIENCE_PARAM_APK_NAME.VALUE, apkName);
		stopGuestScienceParams.assign(cmd.arguments.userData);
		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}
	
	
	public static Quaternion toQuaternion( double yaw, double pitch, double roll) // yaw (Z), pitch (Y), roll (X)
	{
	    // Abbreviations for the various angular functions
	    double cy = Math.cos(yaw * 0.5);
	    double sy = Math.sin(yaw * 0.5);
	    double cp = Math.cos(pitch * 0.5);
	    double sp = Math.sin(pitch * 0.5);
	    double cr = Math.cos(roll * 0.5);
	    double sr = Math.sin(roll * 0.5);
	    
	    double w = cy * cp * cr + sy * sp * sr;
	    double x = cy * cp * sr - sy * sp * cr;
	    double y = sy * cp * sr + cy * sp * cr;
	    double z = sy * cp * cr - cy * sp * sr;
	    Quaternion q = new Quaternion();
	    q.set(x, y, z, w);
	    return q;
	}
	
	/** Takes xyz displacement in axis aligned world coordinates (m) 
	 * and absolute rotation in RADIANS */
	public void sendRelative(double x, double y, double z, float qx, float qy, float qz, float qw) {
		Command cmd = buildCommand(MOBILITY_METHOD_SIMPLEMOVE6DOF.VALUE, MOBILITY.VALUE);
		Vec3d xyz = new Vec3d();
		xyz.userData[0] = x;
		xyz.userData[1] = y;
		xyz.userData[2] = z;
		paramsRelativeMove6dof.set(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_END_LOCATION.VALUE, xyz);
		
		Mat33f mat = new Mat33f();
		mat.userData[0] = qx;
		mat.userData[1] = qy;
		mat.userData[2] = qz;
		mat.userData[3] = qw;

		paramsRelativeMove6dof.set(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_ROT.VALUE, mat);
		paramsRelativeMove6dof.assign(cmd.arguments.userData);

		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}

	/** Takes xyz displacement in body coordinates (m) 
	 * and body relative rotation in RADIANS */
	public void sendTranslateRotateCommandInRelativeCoordinates(double x, double y, double z, double rollRad, double pitchRad, double yawRad) {
		Command cmd = buildCommand(MOBILITY_METHOD_SIMPLEMOVE6DOF.VALUE, MOBILITY.VALUE);
		Vec3d xyz = new Vec3d();
		xyz.userData[0] = x;
		xyz.userData[1] = y;
		xyz.userData[2] = z;
		paramsRelativeMove6dof.set(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_END_LOCATION.VALUE, xyz);
		
		Quaternion change = toQuaternion(yawRad, pitchRad, rollRad);

		Mat33f mat = new Mat33f();
		mat.userData[0] = change.getXf();
		mat.userData[1] = change.getYf();
		mat.userData[2] = change.getZf();
		mat.userData[3] = change.getWf();

		paramsRelativeMove6dof.set(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_ROT.VALUE, mat);
		paramsRelativeMove6dof.assign(cmd.arguments.userData);

		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}


	/** Takes xyz displacement in axis aligned world coordinates (m) 
	 * and absolute rotation in RADIANS */
	public void sendTranslateRotateCommandFromAbsoluteCoordinates(double x, double y, double z, double rollRad, double pitchRad, double yawRad) {
		Command cmd = buildCommand(MOBILITY_METHOD_SIMPLEMOVE6DOF.VALUE, MOBILITY.VALUE);
		Vec3d xyz = new Vec3d();
		xyz.userData[0] = x;
		xyz.userData[1] = y;
		xyz.userData[2] = z;
		paramsSimpleMove6dof.set(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_END_LOCATION.VALUE, xyz);
		
		Quaternion change = toQuaternion(yawRad, pitchRad, rollRad);

		Mat33f mat = new Mat33f();
		mat.userData[0] = change.getXf();
		mat.userData[1] = change.getYf();
		mat.userData[2] = change.getZf();
		mat.userData[3] = change.getWf();

		paramsSimpleMove6dof.set(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_ROT.VALUE, mat);
		paramsSimpleMove6dof.assign(cmd.arguments.userData);

		sendCommand(cmd);
		LogPoster.postToLog(LogEntry.COMMAND, cmd, freeFlyerAgent.name());
	}

	private Command buildCommand(String commandName, String subsystemName) {
		Command cmd = new Command();
		RapidUtil.setHeader(cmd.hdr, rapidMessagePublisher.getAgent(), self, -1);

		cmd.cmdName = commandName;
		cmd.subsysName = subsystemName;
		cmd.cmdId = uniqueCounter++ + participant + System.currentTimeMillis()/1000; 
		cmd.cmdSrc = self.name();// Agent.getEgoAgent().name().split(":")[0]; 
		cmd.cmdAction = QueueAction.QUEUE_BYPASS;
		cmd.targetCmdId = ""; // unused for QUEUE_BYPASS
		return cmd;
	}

	public String getTranslateLogString(double x, double y, double z) {
		return "Translate " + x + ", " + y + ", " + z;
	}

	public String getRotateLogString(double roll, double pitch, double yaw) {
		return "Rotate " + roll + ", " + pitch + ", " + yaw;
	}

	protected void initializeParams() {
		paramsSimpleMove6dof = new ParameterList();
		paramsSimpleMove6dof.add(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_FRAME_NAME.VALUE, MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_FRAME_NAME.VALUE);
		paramsSimpleMove6dof.add(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_END_LOCATION.VALUE, MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_END_LOCATION.VALUE);
		paramsSimpleMove6dof.add(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_END_LOCATION_TOLERANCE.VALUE, MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_END_LOCATION_TOLERANCE.VALUE);
		paramsSimpleMove6dof.add(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_ROT.VALUE, MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_ROT.VALUE);

		paramsSimpleMove6dof.set(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_FRAME_NAME.VALUE, FreeFlyerCommands.ABSOLUTE_FRAME_NAME);
		Vec3d tol = new Vec3d();
		tol.userData[0] =  FreeFlyerCommands.TRANSLATION_TOLERANCE;
		tol.userData[1] =  FreeFlyerCommands.TRANSLATION_TOLERANCE;
		tol.userData[2] =  FreeFlyerCommands.TRANSLATION_TOLERANCE;
		paramsSimpleMove6dof.set(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_END_LOCATION_TOLERANCE.VALUE, tol);
		
		paramsRelativeMove6dof = new ParameterList();
		paramsRelativeMove6dof.add(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_FRAME_NAME.VALUE, MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_FRAME_NAME.VALUE);
		paramsRelativeMove6dof.add(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_END_LOCATION.VALUE, MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_END_LOCATION.VALUE);
		paramsRelativeMove6dof.add(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_END_LOCATION_TOLERANCE.VALUE, MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_END_LOCATION_TOLERANCE.VALUE);
		paramsRelativeMove6dof.add(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_ROT.VALUE, MOBILITY_METHOD_SIMPLEMOVE6DOF_DTYPE_ROT.VALUE);

		paramsRelativeMove6dof.set(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_FRAME_NAME.VALUE, FreeFlyerCommands.RELATIVE_FRAME_NAME);
		paramsRelativeMove6dof.set(MOBILITY_METHOD_SIMPLEMOVE6DOF_PARAM_END_LOCATION_TOLERANCE.VALUE, tol);

		setCameraStreamingParams = new ParameterList();
		setCameraStreamingParams.add(SETTINGS_METHOD_SET_CAMERA_STREAMING_PARAM_CAMERA_NAME.VALUE, SETTINGS_METHOD_SET_CAMERA_STREAMING_DTYPE_CAMERA_NAME.VALUE);
		setCameraStreamingParams.add(SETTINGS_METHOD_SET_CAMERA_STREAMING_PARAM_STREAM.VALUE, SETTINGS_METHOD_SET_CAMERA_STREAMING_DTYPE_STREAM.VALUE);
		
		grabControlParams = new ParameterList();
		grabControlParams.add(ACCESSCONTROL_METHOD_GRAB_CONTROL_PARAM_COOKIE.VALUE, ACCESSCONTROL_METHOD_GRAB_CONTROL_DTYPE_COOKIE.VALUE);

		powerOnParams = new ParameterList();
		powerOnParams.add(POWER_METHOD_POWER_ON_ITEM_PARAM_WHICH.VALUE, POWER_METHOD_POWER_ON_ITEM_DTYPE_WHICH.VALUE);

		powerOffParams = new ParameterList();
		powerOffParams.add(POWER_METHOD_POWER_OFF_ITEM_PARAM_WHICH.VALUE, POWER_METHOD_POWER_OFF_ITEM_DTYPE_WHICH.VALUE);

		openGripperParams = new ParameterList();
		openGripperParams.add(ARM_METHOD_GRIPPER_CONTROL_PARAM_OPEN.VALUE, ARM_METHOD_GRIPPER_CONTROL_DTYPE_OPEN.VALUE);

		panAndTiltParams = new ParameterList();
		panAndTiltParams.add(ARM_METHOD_ARM_PAN_AND_TILT_PARAM_PAN.VALUE, ARM_METHOD_ARM_PAN_AND_TILT_DTYPE_PAN.VALUE);
		panAndTiltParams.add(ARM_METHOD_ARM_PAN_AND_TILT_PARAM_TILT.VALUE, ARM_METHOD_ARM_PAN_AND_TILT_DTYPE_TILT.VALUE);
		panAndTiltParams.add(ARM_METHOD_ARM_PAN_AND_TILT_PARAM_WHICH.VALUE, ARM_METHOD_ARM_PAN_AND_TILT_DTYPE_WHICH.VALUE);

		setCameraParamsParams = new ParameterList();
		setCameraParamsParams.add(SETTINGS_METHOD_SET_CAMERA_PARAM_CAMERA_NAME.VALUE, SETTINGS_METHOD_SET_CAMERA_DTYPE_CAMERA_NAME.VALUE);
		setCameraParamsParams.add(SETTINGS_METHOD_SET_CAMERA_PARAM_CAMERA_MODE.VALUE, SETTINGS_METHOD_SET_CAMERA_DTYPE_CAMERA_MODE.VALUE);
		setCameraParamsParams.add(SETTINGS_METHOD_SET_CAMERA_PARAM_RESOLUTION.VALUE, SETTINGS_METHOD_SET_CAMERA_DTYPE_RESOLUTION.VALUE);
		setCameraParamsParams.add(SETTINGS_METHOD_SET_CAMERA_PARAM_FRAME_RATE.VALUE, SETTINGS_METHOD_SET_CAMERA_DTYPE_FRAME_RATE.VALUE);
		setCameraParamsParams.add(SETTINGS_METHOD_SET_CAMERA_PARAM_BANDWIDTH.VALUE, SETTINGS_METHOD_SET_CAMERA_DTYPE_BANDWIDTH.VALUE);

		setCameraStreamingParams = new ParameterList();
		setCameraStreamingParams.add(SETTINGS_METHOD_SET_CAMERA_STREAMING_PARAM_CAMERA_NAME.VALUE, SETTINGS_METHOD_SET_CAMERA_STREAMING_DTYPE_CAMERA_NAME.VALUE);
		setCameraStreamingParams.add(SETTINGS_METHOD_SET_CAMERA_STREAMING_PARAM_STREAM.VALUE, SETTINGS_METHOD_SET_CAMERA_STREAMING_DTYPE_STREAM.VALUE);
		
		setFlashlightBrightnessParams = new ParameterList();
		setFlashlightBrightnessParams.add(SETTINGS_METHOD_SET_FLASHLIGHT_BRIGHTNESS_PARAM_WHICH.VALUE, SETTINGS_METHOD_SET_FLASHLIGHT_BRIGHTNESS_DTYPE_WHICH.VALUE);
		setFlashlightBrightnessParams.add(SETTINGS_METHOD_SET_FLASHLIGHT_BRIGHTNESS_PARAM_BRIGHTNESS.VALUE, SETTINGS_METHOD_SET_FLASHLIGHT_BRIGHTNESS_DTYPE_BRIGHTNESS.VALUE);

		setInertiaParams = new ParameterList();
		setInertiaParams.add(SETTINGS_METHOD_SET_INERTIA_PARAM_NAME.VALUE, SETTINGS_METHOD_SET_INERTIA_DTYPE_NAME.VALUE);
		setInertiaParams.add(SETTINGS_METHOD_SET_INERTIA_PARAM_MASS.VALUE, SETTINGS_METHOD_SET_INERTIA_DTYPE_MASS.VALUE);
		setInertiaParams.add(SETTINGS_METHOD_SET_INERTIA_PARAM_MATRIX.VALUE, SETTINGS_METHOD_SET_INERTIA_DTYPE_MATRIX.VALUE);

		setTelemetryParams = new ParameterList();
		setTelemetryParams.add(SETTINGS_METHOD_SET_TELEMETRY_RATE_PARAM_NAME.VALUE, SETTINGS_METHOD_SET_TELEMETRY_RATE_DTYPE_NAME.VALUE);
		setTelemetryParams.add(SETTINGS_METHOD_SET_TELEMETRY_RATE_PARAM_RATE.VALUE, SETTINGS_METHOD_SET_TELEMETRY_RATE_DTYPE_RATE.VALUE);

		setOperatingLimitsParams = new ParameterList();
		setOperatingLimitsParams.add(SETTINGS_METHOD_SET_OPERATING_LIMITS_PARAM_PROFILE_NAME.VALUE, SETTINGS_METHOD_SET_OPERATING_LIMITS_DTYPE_PROFILE_NAME.VALUE);
		setOperatingLimitsParams.add(SETTINGS_METHOD_SET_OPERATING_LIMITS_PARAM_FLIGHT_MODE.VALUE, SETTINGS_METHOD_SET_OPERATING_LIMITS_DTYPE_FLIGHT_MODE.VALUE);
		setOperatingLimitsParams.add(SETTINGS_METHOD_SET_OPERATING_LIMITS_PARAM_TARGET_LINEAR_VELOCITY.VALUE, SETTINGS_METHOD_SET_OPERATING_LIMITS_DTYPE_TARGET_LINEAR_VELOCITY.VALUE);
		setOperatingLimitsParams.add(SETTINGS_METHOD_SET_OPERATING_LIMITS_PARAM_TARGET_LINEAR_ACCELERATION.VALUE, SETTINGS_METHOD_SET_OPERATING_LIMITS_DTYPE_TARGET_LINEAR_ACCELERATION.VALUE);
		setOperatingLimitsParams.add(SETTINGS_METHOD_SET_OPERATING_LIMITS_PARAM_TARGET_ANGULAR_VELOCITY.VALUE, SETTINGS_METHOD_SET_OPERATING_LIMITS_DTYPE_TARGET_ANGULAR_VELOCITY.VALUE);
		setOperatingLimitsParams.add(SETTINGS_METHOD_SET_OPERATING_LIMITS_PARAM_TARGET_ANGULAR_ACCELERATION.VALUE, SETTINGS_METHOD_SET_OPERATING_LIMITS_DTYPE_TARGET_ANGULAR_ACCELERATION.VALUE);
		setOperatingLimitsParams.add(SETTINGS_METHOD_SET_OPERATING_LIMITS_PARAM_COLLISION_DISTANCE.VALUE, SETTINGS_METHOD_SET_OPERATING_LIMITS_DTYPE_COLLISION_DISTANCE.VALUE);

		setHolonomicParams = new ParameterList();
		setHolonomicParams.add(SETTINGS_METHOD_SET_HOLONOMIC_MODE_PARAM_ENABLE_HOLONOMIC.VALUE, SETTINGS_METHOD_SET_HOLONOMIC_MODE_DTYPE_ENABLE_HOLONOMIC.VALUE);

		setCheckObstaclesParams = new ParameterList();
		setCheckObstaclesParams.add(SETTINGS_METHOD_SET_CHECK_OBSTACLES_PARAM_CHECK_OBSTACLES.VALUE, SETTINGS_METHOD_SET_CHECK_OBSTACLES_DTYPE_CHECK_OBSTACLES.VALUE);

		setCheckKeepoutsParams = new ParameterList();
		setCheckKeepoutsParams.add(SETTINGS_METHOD_SET_CHECK_ZONES_PARAM_CHECK_ZONES.VALUE, SETTINGS_METHOD_SET_CHECK_ZONES_DTYPE_CHECK_ZONES.VALUE);

		startGuestScienceParams = new ParameterList();
		startGuestScienceParams.add(GUESTSCIENCE_METHOD_START_GUEST_SCIENCE_PARAM_APK_NAME.VALUE, GUESTSCIENCE_METHOD_START_GUEST_SCIENCE_DTYPE_APK_NAME.VALUE);

		stopGuestScienceParams = new ParameterList();
		stopGuestScienceParams.add(GUESTSCIENCE_METHOD_STOP_GUEST_SCIENCE_PARAM_APK_NAME.VALUE, GUESTSCIENCE_METHOD_STOP_GUEST_SCIENCE_DTYPE_APK_NAME.VALUE);

		guestScienceParams = new ParameterList();
		guestScienceParams.add(GUESTSCIENCE_METHOD_CUSTOM_GUEST_SCIENCE_PARAM_APK_NAME.VALUE, GUESTSCIENCE_METHOD_CUSTOM_GUEST_SCIENCE_DTYPE_APK_NAME.VALUE);
		guestScienceParams.add(GUESTSCIENCE_METHOD_CUSTOM_GUEST_SCIENCE_PARAM_COMMAND.VALUE, GUESTSCIENCE_METHOD_CUSTOM_GUEST_SCIENCE_DTYPE_COMMAND.VALUE);
	
		smartDockWakeParams = new ParameterList();
		smartDockWakeParams.add(ADMIN_METHOD_WAKE_PARAM_BERTH_NUMBER .VALUE, ADMIN_METHOD_WAKE_DTYPE_BERTH_NUMBER.VALUE);
		
		smartDockWakeSafeParams = new ParameterList();
		smartDockWakeSafeParams.add(ADMIN_METHOD_WAKE_SAFE_PARAM_BERTH_NUMBER .VALUE, ADMIN_METHOD_WAKE_SAFE_DTYPE_BERTH_NUMBER.VALUE);
	}

	@Override
	public String toString() {
		return "CommandPublisher for " + freeFlyerAgent.name();
	}
}
