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



import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerCommands;
import gov.nasa.arc.simulator.freeflyer.publishers.PositionPublisher;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import rapid.Command;
import rapid.MOBILITY_METHOD_SIMPLEMOVE6DOF;
import rapid.Mat33f;
import rapid.ParameterUnion;
import rapid.ParameterUnionSeq;
import rapid.PositionSample;
import rapid.Vec3d;


public class TeleopSubsystem {
	private static TeleopSubsystem INSTANCE;
	private PositionPublisher positionPublisher;
	private Vector3f startPos;
	private Quaternion startQuat;
	public static TeleopSubsystem getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new TeleopSubsystem();
		}
		return INSTANCE;
	}

	private TeleopSubsystem() {
		positionPublisher = PositionPublisher.getInstance();
	}

	public void doMoveCommand(Command cmd, PositionSample positionSample) {
		if(cmd.cmdName.equals(MOBILITY_METHOD_SIMPLEMOVE6DOF.VALUE)) {
			ParameterUnionSeq pus = cmd.arguments.userData;
			String frame = ((ParameterUnion)pus.get(0)).s();

			if(frame.equals(FreeFlyerCommands.ABSOLUTE_FRAME_NAME)) {

				startPos = new Vector3f((float)positionSample.pose.xyz.userData[0], 
						(float)positionSample.pose.xyz.userData[1], 
						(float)positionSample.pose.xyz.userData[2]); 

				Vec3d endXYZ = ((ParameterUnion)pus.get(1)).vec3d();
				Vector3f endPos = new Vector3f((float)endXYZ.userData[0],(float)endXYZ.userData[1],(float)endXYZ.userData[2]);

				startQuat = new Quaternion( (float)positionSample.pose.rot.userData[0], 
						(float)positionSample.pose.rot.userData[1],
						(float)positionSample.pose.rot.userData[2],
						(float)positionSample.pose.rot.userData[3]);

				Mat33f endRot = ((ParameterUnion)pus.get(3)).mat33f();
				Quaternion endQuat = new Quaternion((float)endRot.userData[0],(float)endRot.userData[1],(float)endRot.userData[2],(float)endRot.userData[3]);

				float time = distance(startPos, endPos);
				if(time < 1)
					time = 1;
				if(endPos.x != startPos.x || endPos.y != startPos.y || endPos.z != startPos.z){
					for(float i = 0; i < time*100; i++){

						Vector3f currentPos = lerp(startPos, endPos, i/(time*100));
						positionPublisher.publishSample(
								currentPos.x,
								currentPos.y,
								currentPos.z,
								startQuat.x,
								startQuat.y,
								startQuat.z,
								startQuat.w);

						try {
							Thread.sleep(10);
						} catch (final InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				positionPublisher.publishSample(
						endPos.x,
						endPos.y,
						endPos.z,
						startQuat.x,
						startQuat.y,
						startQuat.z,
						startQuat.w);


				if(endQuat.x != startQuat.x || endQuat.y != startQuat.y || endQuat.z != startQuat.z || endQuat.w != startQuat.w){
					for(float i = 0; i < 100f; i++){

						Quaternion currentRot = slerp(startQuat, endQuat, i/100f);
						positionPublisher.publishSample(
								endPos.x,
								endPos.y,
								endPos.z,
								currentRot.x,
								currentRot.y,
								currentRot.z,
								currentRot.w);

						try {
							Thread.sleep(100);
						} catch (final InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

				positionPublisher.publishSample(
						endPos.x,
						endPos.y,
						endPos.z,
						endQuat.x,
						endQuat.y,
						endQuat.z,
						endQuat.w);

				startPos = endPos;
				startQuat = endQuat;
			}
			else if(frame.equals(FreeFlyerCommands.RELATIVE_FRAME_NAME)) {

				startPos = new Vector3f((float)positionSample.pose.xyz.userData[0], 
						(float)positionSample.pose.xyz.userData[1], 
						(float)positionSample.pose.xyz.userData[2]); 

				Vec3d endXYZ = ((ParameterUnion)pus.get(1)).vec3d();
				Vector3f endPos = new Vector3f(
						(float)endXYZ.userData[0] + startPos.getX(),
						(float)endXYZ.userData[1] + startPos.getY(),
						(float)endXYZ.userData[2] + startPos.getZ());
				
				startQuat = new Quaternion( 
						(float)positionSample.pose.rot.userData[0], 
						(float)positionSample.pose.rot.userData[1],
						(float)positionSample.pose.rot.userData[2],
						(float)positionSample.pose.rot.userData[3]);

				Mat33f endRot = ((ParameterUnion)pus.get(3)).mat33f();
				Quaternion endQuat = new Quaternion(
						(float)endRot.userData[0],
						(float)endRot.userData[1],
						(float)endRot.userData[2],
						(float)endRot.userData[3]);

				float time = distance(startPos, endPos);
				if(time < 1)
					time = 1;
				if(endPos.x != startPos.x || endPos.y != startPos.y || endPos.z != startPos.z){
					for(float i = 0; i < time*100; i++){
						Vector3f currentPos = lerp(startPos, endPos, i/(time*100));
						positionPublisher.publishSample(
								currentPos.x,
								currentPos.y,
								currentPos.z,
								startQuat.x,
								startQuat.y,
								startQuat.z,
								startQuat.w);

						try {
							Thread.sleep(10);
						} catch (final InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				positionPublisher.publishSample(
						endPos.x,
						endPos.y,
						endPos.z,
						startQuat.x,
						startQuat.y,
						startQuat.z,
						startQuat.w);


				if(endQuat.x != startQuat.x || endQuat.y != startQuat.y || endQuat.z != startQuat.z || endQuat.w != startQuat.w){
					for(float i = 0; i < 100f; i++){

						Quaternion currentRot = slerp(startQuat, endQuat, i/100f);
						positionPublisher.publishSample(
								endPos.x,
								endPos.y,
								endPos.z,
								currentRot.x,
								currentRot.y,
								currentRot.z,
								currentRot.w);

						try {
							Thread.sleep(100);
						} catch (final InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

				positionPublisher.publishSample(
						endPos.x,
						endPos.y,
						endPos.z,
						endQuat.x,
						endQuat.y,
						endQuat.z,
						endQuat.w);

				startPos = endPos;
				startQuat = endQuat;
			}
		}
	}

	public Quaternion slerp(final Quaternion q1, final Quaternion q2, final float t) {

		final Quaternion newQuat = new Quaternion();
		// Create a local quaternion to store the interpolated quaternion
		if (q1.x == q2.x && q1.y == q2.y && q1.z == q2.z && q1.w == q2.w) {
			return q1;
		}

		float result = (q1.x * q2.x) + (q1.y * q2.y) + (q1.z * q2.z)
				+ (q1.w * q2.w);

		if (result < 0.0f) {
			// Negate the second quaternion and the result of the dot product
			q2.x = -q2.x;
			q2.y = -q2.y;
			q2.z = -q2.z;
			q2.w = -q2.w;
			result = -result;
		}

		// Set the first and second scale for the interpolation
		float scale0 = 1 - t;
		float scale1 = t;

		// Check if the angle between the 2 quaternions was big enough to
		// warrant such calculations
		if ((1 - result) > 0.1f) {// Get the angle between the 2 quaternions,
			// and then store the sin() of that angle
			final float theta = (float) Math.acos(result);
			final float invSinTheta = (float) (1f / Math.sin(theta));

			// Calculate the scale for q1 and q2, according to the angle and
			// it's sine value
			scale0 = (float) (Math.sin((1 - t) * theta) * invSinTheta);
			scale1 = (float) (Math.sin((t * theta)) * invSinTheta);
		}

		// Calculate the x, y, z and w values for the quaternion by using a
		// special
		// form of linear interpolation for quaternions.
		newQuat.x = (scale0 * q1.x) + (scale1 * q2.x);
		newQuat.y = (scale0 * q1.y) + (scale1 * q2.y);
		newQuat.z = (scale0 * q1.z) + (scale1 * q2.z);
		newQuat.w = (scale0 * q1.w) + (scale1 * q2.w);

		// Return the interpolated quaternion
		return newQuat;
	}

	private Vector3f lerp (final Vector3f start, final Vector3f end, final float percent){
		Vector3f newPos = new Vector3f();
		final Vector3f finalPos = new Vector3f();
		Vector3f.sub(end, start, newPos);
		newPos = new Vector3f(newPos.x*percent,newPos.y*percent,newPos.z*percent);
		Vector3f.add(start, newPos, finalPos);
		return finalPos;
	}

	private float distance(Vector3f start, final Vector3f end){
		return (float) Math.abs(Math.sqrt( Math.pow((start.x - end.x),2)+Math.pow((start.y - end.y),2)+ Math.pow((start.z - end.z),2) ));
	}
}
