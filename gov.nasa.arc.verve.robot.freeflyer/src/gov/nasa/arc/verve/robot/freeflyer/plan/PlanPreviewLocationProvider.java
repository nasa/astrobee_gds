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
package gov.nasa.arc.verve.robot.freeflyer.plan;

import gov.nasa.arc.irg.plan.freeflyer.plan.FreeFlyerPlan;
import gov.nasa.arc.irg.plan.model.Segment;
import gov.nasa.arc.irg.plan.model.Sequenceable;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyQuaternion;
import com.ardor3d.math.type.ReadOnlyTransform;

public class PlanPreviewLocationProvider {
	private static final Logger logger = Logger.getLogger(PlanPreviewLocationProvider.class);
	private WaypointList waypoints;
	private long startedMillis; // absolute time plan playback started
	private boolean startedMillisCorrect = false; // planStartedMillis is correct
	private boolean planPreviewRunning = false; // told to run
	private static PlanPreviewLocationProvider INSTANCE;
	private int currentIndex = 1;

	private PlanPreviewLocationProvider() {}
	
	public static PlanPreviewLocationProvider getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new PlanPreviewLocationProvider();
		}
		return INSTANCE;
	}
	
	public void ingestPlan(final FreeFlyerPlan plan){
		waypoints = new WaypointList();
		for(final Sequenceable seq : plan.getSequence()){
			// ignore Stations
			if(seq instanceof Segment) {
				waypoints.addSegment((Segment)seq);
			}
		}
	}

	public void previewThisPlan(final FreeFlyerPlan plan) {
		ingestPlan(plan);
		planPreviewRunning = true;
	}

	public ReadOnlyTransform getTransformAtMillis(long currentMillis) {
		if(!planPreviewRunning) {
			return null;
		}

		if(!startedMillisCorrect) {
			startedMillis = currentMillis;
			startedMillisCorrect = true;
			currentIndex=1;
			return null; // so not interpolating from 0
		}
		// seconds into plan playback
		double currentRelativeSecond = (currentMillis - startedMillis)/100.0;

		// look at waypoints from iterator and interpolate latest two
		Waypoint wp1 = waypoints.get(currentIndex - 1);
		while(currentIndex < waypoints.getWaypoints().size()) {
			Waypoint wp2 = waypoints.get(currentIndex);

			if(currentRelativeSecond < wp2.getTimeSeconds()) {
				//return sendThisWaypoint(wp);
				ReadOnlyTransform tfm = interpolateTwoWaypoints(wp1, wp2, currentRelativeSecond);
				return tfm;
			}
			wp1 = wp2;
			currentIndex++;
		}

		// we're at the end
		stopPreview();
		return null;
	}
	
	public void stopPreview() {
		planPreviewRunning = false;
		startedMillisCorrect = false;
	}

	public ReadOnlyTransform sendThisWaypoint(Waypoint wp) {
		if(wp == null) {
			return null;
		}

		Vector3 endPoint = wp.getPos();
		Quaternion endAngle = wp.getQuat();

		Transform tfm = new Transform();
		tfm.setTranslation(endPoint.getX(), endPoint.getY(), endPoint.getZ());
		ReadOnlyQuaternion q = (ReadOnlyQuaternion) new Quaternion(endAngle.getX(),endAngle.getY(),endAngle.getZ(),endAngle.getW());
		tfm.setRotation(q);
		return tfm;
	}

	public ReadOnlyTransform interpolateTwoWaypoints(Waypoint wp1, Waypoint wp2, double currentRelativeSecond) {
		double totalTimeSeconds = wp2.getTimeSeconds() - wp1.getTimeSeconds(); // time btw wp1 and wp2
		double deltaTimeSeconds = currentRelativeSecond - wp1.getTimeSeconds(); // time since wp1
		double percent = deltaTimeSeconds/totalTimeSeconds;

		Vector3 startPoint = wp1.getPos();
		Quaternion startAngle = wp1.getQuat();
		Vector3 endPoint = wp2.getPos();
		Quaternion endAngle = wp2.getQuat();

		Transform tfm = new Transform();
		final Vector3 same = new Vector3();
		startPoint.subtract(endPoint, same);
		if(same.getX() == 0 && same.getY() == 0 && same.getZ() == 0){
			//position hasn't changed so we must have rotated
			ReadOnlyQuaternion q = interpolateAngles(startAngle, endAngle, percent);
			tfm.setTranslation(startPoint);
			tfm.setRotation(q);

		} else {
			Vector3 curPoints = lerp(startPoint, endPoint, percent);
			tfm.setTranslation(curPoints);
			tfm.setRotation(endAngle);
		}
		return tfm;
	}

	private ReadOnlyQuaternion interpolateAngles(Quaternion startAngle, Quaternion endAngle, double percentInto) {
		if(endAngle.getX() != startAngle.getX() || endAngle.getY() != startAngle.getY() || endAngle.getZ() != startAngle.getZ() || endAngle.getW() != startAngle.getW()){

			final Quaternion currentQuat = slerp(startAngle, endAngle, (float) percentInto);

			return (ReadOnlyQuaternion) new Quaternion(currentQuat.getX(),
					currentQuat.getY(),
					currentQuat.getZ(),
					currentQuat.getW());
		}
		else {
			return endAngle; // start and end the same
		}
	}


	public Quaternion slerp(final Quaternion q1, final Quaternion q2, final float t) {

		final Quaternion newQuat = new Quaternion();
		// Create a local quaternion to store the interpolated quaternion
		if (q1.getX() == q2.getX() && q1.getY() == q2.getY() && q1.getZ() == q2.getZ() && q1.getW() == q2.getW()) {
			return q1;
		}

		double result = (q1.getX() * q2.getX()) + (q1.getY() * q2.getY()) + (q1.getZ() * q2.getZ())
				+ (q1.getW() * q2.getW());

		if (result < 0.0f) {
			// Negate the second quaternion and the result of the dot product
			q2.setX(-q2.getX());
			q2.setY(-q2.getY());
			q2.setZ(-q2.getZ());
			q2.setW(-q2.getW());
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
		newQuat.setX((scale0 * q1.getX()) + (scale1 * q2.getX()));
		newQuat.setY((scale0 * q1.getY()) + (scale1 * q2.getY()));
		newQuat.setZ((scale0 * q1.getZ()) + (scale1 * q2.getZ()));
		newQuat.setW((scale0 * q1.getW()) + (scale1 * q2.getW()));

		// Return the interpolated quaternion
		return newQuat;
	}

	private Vector3 lerp (final Vector3 start, final Vector3 end, final double percent){
		Vector3 intermediate = new Vector3();
		final Vector3 finalPos = new Vector3();
		end.subtract(start, intermediate);
		intermediate = new Vector3(intermediate.getX()*percent,intermediate.getY()*percent,intermediate.getZ()*percent);
		start.add(intermediate, finalPos);
		return finalPos;
	}

	private class Waypoint {
		private double t;
		public final float[] pos;
		public final float[] quat;

		public Waypoint(final List<Number> nums) {
			t = nums.get(0).doubleValue();
			pos = new float[]{nums.get(1).floatValue(), nums.get(2).floatValue(), nums.get(3).floatValue()};
			quat = new float[]{nums.get(10).floatValue(), nums.get(11).floatValue(), nums.get(12).floatValue(), nums.get(13).floatValue()};
		}

		public void offsetTime(double offset) {
			t += offset;
		}

		public double getTimeSeconds() {
			return t;
		}

		public Vector3 getPos() {
			return new Vector3(pos[0],
					pos[1],
					pos[2]);
		}

		public Quaternion getQuat() {
			return new Quaternion(quat[0], 
					quat[1],
					quat[2], 
					quat[3]);
		}

	}

	/** Holds all the waypoints from all the Segments in a Plan */
	private class WaypointList {
		private final List<Waypoint> waypoints;
		private double totalDuration = 0; // cumulative duration of all added Segments

		public WaypointList() {
			waypoints = new ArrayList<Waypoint>();
		}

		public void addSegment(final Segment seg) {
			for(final List<Number> nums : seg.getWaypoints()) {
				Waypoint wp = new Waypoint (nums);
				wp.offsetTime(totalDuration);
				waypoints.add( wp );
			}
			totalDuration += seg.getCalculatedDuration();
		}

		public List<Waypoint> getWaypoints() {
			return waypoints;
		}

		public Waypoint get(int ind) {
			return waypoints.get(ind);
		}

	}
}
