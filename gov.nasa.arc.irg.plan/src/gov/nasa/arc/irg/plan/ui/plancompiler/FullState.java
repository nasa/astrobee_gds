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
package gov.nasa.arc.irg.plan.ui.plancompiler;

import gov.nasa.arc.irg.plan.model.Position;
import gov.nasa.arc.irg.plan.util.NumberUtil;
import gov.nasa.rapid.v2.framestore.ConvertUtils;
import gov.nasa.rapid.v2.framestore.EulerAngles;
import gov.nasa.rapid.v2.framestore.ReadOnlyEulerAngles.Type;

import org.apache.log4j.Logger;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;

public class FullState {
	private static Logger logger = Logger.getLogger(FullState.class);
	public double t;
	public double[] pos, vel, accel;
	public Quaternion orient;
	public double[] ang_vel, ang_accel;
	public static double DEG_TO_RAD = 3.14159 / 180.0;
	static double epsilon_ = 1e-3;

	public FullState() {
		pos = new double[3];
		vel = new double[3];
		accel = new double[3];
		ang_vel = new double[3];
		ang_accel = new double[3];
	}

	public static FullState[] createTrajectoryTrapezoid(Position startPos, Position endPos, double max_vel, double max_accel,double max_avel, double max_aaccel) {
		double[] pos_0 = positionToDoubleArrayTranslation(startPos);
		double[] pos_1 = positionToDoubleArrayTranslation(endPos);

		Quaternion orient_0 = positionToQuaternion(startPos);
		Quaternion orient_1 = positionToQuaternion(endPos);

		return createTrajectoryTrapezoid(pos_0, pos_1, max_vel, max_accel,
				orient_0, orient_1, max_avel, max_aaccel);
	}

	public static FullState[] createTrajectoryTrapezoid(Position startPos, Vector3 endVec, Quaternion endQuat, double max_vel, double max_accel,double max_avel, double max_aaccel) {
		double[] pos_0 = positionToDoubleArrayTranslation(startPos);
		double[] pos_1 = endVec.toArray(null);

		Quaternion orient_0 = positionToQuaternion(startPos);
		logger.debug(String.format("pos to quaternion = %.2f, %.2f, %.2f %.2f", 
				orient_0.getX(), orient_0.getY(), orient_0.getZ(), orient_0.getW()));

		return createTrajectoryTrapezoid(pos_0, pos_1, max_vel, max_accel,
				orient_0, endQuat, max_avel, max_aaccel);
	}

	public static FullState[] createTrajectoryTrapezoid(Vector3 startVec, Quaternion startQuat, Position endPos, double max_vel, double max_accel,double max_avel, double max_aaccel) {
		double[] pos_0 = startVec.toArray(null);
		double[] pos_1 = positionToDoubleArrayTranslation(endPos);

		Quaternion orient_1 = positionToQuaternion(endPos);

		return createTrajectoryTrapezoid(pos_0, pos_1, max_vel, max_accel,
				startQuat, orient_1, max_avel, max_aaccel);
	}

	public static FullState[] createTrajectoryTrapezoid(Vector3 startVec, Quaternion startQuat, Vector3 endVec, Quaternion endQuat, double max_vel, double max_accel,double max_avel, double max_aaccel) {
		double[] pos_0 = startVec.toArray(null);
		double[] pos_1 = endVec.toArray(null);

		return createTrajectoryTrapezoid(pos_0, pos_1, max_vel, max_accel,
				startQuat, endQuat, max_avel, max_aaccel);
	}

	/** get the translation out of the Position as an array of doubles */
	public static double[] positionToDoubleArrayTranslation(Position pos) {
		double[] arr = {pos.getCoordinates().get(0), pos.getCoordinates().get(1), pos.getCoordinates().get(2)};
		return arr;
	}

	/** get the rotation out of the Position and make it into a Quaternion */
	public static Quaternion positionToQuaternion(Position pos) {
		Quaternion quat = new Quaternion();
		Matrix3 m33 = ConvertUtils.toRotationMatrixXYZr(EulerAngles.fromDegrees(Type.XYZr, pos.getOrientation().get(0),
				pos.getOrientation().get(1),
				pos.getOrientation().get(2)), null);
		quat.fromRotationMatrix(m33);
		logger.debug(String.format("matrix =\n\t\t\t\t\t\t\t\t\t\t\t[%.2f, %.2f, %.2f\n\t\t\t\t\t\t\t\t\t\t\t"
				+ "%.2f, %.2f, %.2f\n\t\t\t\t\t\t\t\t\t\t\t%.2f, %.2f, %.2f]", 
				m33.getValuef(0, 0), m33.getValuef(0, 1), m33.getValuef(0, 2),
				m33.getValuef(1, 0), m33.getValuef(1, 1), m33.getValuef(1, 2),
				m33.getValuef(2, 0), m33.getValuef(2, 1), m33.getValuef(2, 2)));
		return quat;
	}

	public static FullState
	createInterpolatedState(SimplifiedState translation_delta,
			double[] pos_0, double[] unit_velocity,
			SimplifiedState orientation_delta,
			Quaternion orient_0, ReadOnlyVector3 axis, double t) {
		FullState output = new FullState();
		output.t = t;
		double trans_dt = output.t - translation_delta.t;
		double orient_dt = output.t - orientation_delta.t;

		// Advance translation
		SimplifiedState trans_advanced = SimplifiedState.createInterpolatedState(translation_delta, trans_dt);

		// Write translation's position
		for (int i = 0; i < 3; i++) {
			output.pos[i] = pos_0[i] + unit_velocity[i] * trans_advanced.x;
		}
		// Write translation's velocity
		for (int i = 0; i < 3; i++) {
			output.vel[i] = unit_velocity[i] * trans_advanced.x_dot;
		}
		// Write translation's accel
		for (int i = 0; i < 3; i++) {
			output.accel[i] = unit_velocity[i] * trans_advanced.x_dotdot;
		}

		// Advance orientation
		SimplifiedState orient_advanced = SimplifiedState.createInterpolatedState(orientation_delta, orient_dt);

		// Write orient's position
		Quaternion advanced = new Quaternion();
		advanced.fromAngleAxis(orient_advanced.x, axis);
		output.orient = advanced.multiply(orient_0, output.orient);

		// Write orient's velocity
		for (int i = 0; i < 3; i++) {
			output.ang_vel[i] = axis.toArray(null)[i] * orient_advanced.x_dot;
		}

		// Write orient's accel
		for (int i = 0; i < 3; i++) {
			output.ang_accel[i] = axis.toArray(null)[i] * orient_advanced.x_dotdot;
		}

		Quaternion invOrient = output.orient.invert(null);
		Vector3 old_ang_vel = new Vector3();
		old_ang_vel.set(output.ang_vel[0], output.ang_vel[1], output.ang_vel[2]);
		Vector3 new_ang_vel = invOrient.apply(old_ang_vel, null);

		Vector3 old_ang_accel = new Vector3();
		old_ang_accel.set(output.ang_accel[0], output.ang_accel[1], output.ang_accel[2]);
		Vector3 new_ang_accel = invOrient.apply(old_ang_accel, null);

		for(int i=0; i<3; i++) {
			output.ang_vel[i] = new_ang_vel.getValue(i);
			output.ang_accel[i] = new_ang_accel.getValue(i);
		}
		return output;
	}

	public static FullState[]
			createTrajectoryTrapezoid(double[] pos_0, double[] pos_1, double max_vel, double max_accel,
					Quaternion orient_0, Quaternion orient_1, double max_avel, double max_aaccel) {
		// Check input arguments
		if (pos_0.length != 3 || pos_1.length != 3) {
			throw new IllegalArgumentException("Position vectors must be size 3 in length.");
		}

		// Solve for translation trapezoid
		double[] direction_unitv = new double[3];
		for (int i = 0; i < 3; i++) {
			direction_unitv[i] = pos_1[i] - pos_0[i];
		}
		double delta_distance =
				Math.sqrt(direction_unitv[0] * direction_unitv[0] +
						direction_unitv[1] * direction_unitv[1] +
						direction_unitv[2] * direction_unitv[2]);
		if (delta_distance > 1e-9) {
			for (int i = 0; i < 3; i++) {
				direction_unitv[i] /= delta_distance;
			}
		}

		// Solve for angular trapezoid
		// Find the radian distance between the two quaternions.
		//
		// First sanitize the input .. verify they are unit length
		orient_0.normalizeLocal(); 
		orient_1.normalizeLocal();

		Quaternion invOrient_0 = orient_0.invert(null);
		Quaternion delta_q = orient_1.multiply(invOrient_0, null);
		Vector3 delta_axis = new Vector3();
		double delta_angle = delta_q.toAngleAxis(delta_axis);
		if (delta_angle < -Math.PI) {
			delta_angle += (2 * Math.PI);
		}
		if (delta_angle > Math.PI) {
			delta_angle -= (2 * Math.PI);
		}

		// find greedy ramp for linear and angular
		double[] linear_greedy_ramp =  GreedyRamp(delta_distance,     // Distance
				max_vel,     // Max velocity
				max_accel)  ;   // Max acceleration

		double[] angular_greedy_ramp =  GreedyRamp(delta_angle,     // Distance
				max_avel,     // Max velocity
				max_aaccel)  ;   // Max acceleration

		// see which one takes longer (time is first)
		double angle_time = angular_greedy_ramp[0];
		double distance_time = linear_greedy_ramp[0];

		if(angle_time > distance_time) {
			// angle is slow, so slow down linear velocity
			max_vel = FairRamp(angle_time,     // Time
					delta_distance,     // Distance
					max_vel,     // Max velocity
					max_accel);
		} else {
			// distance is slow, so slow down angular velocity
			max_avel = FairRamp(distance_time,     // Time
					delta_angle,     // Distance
					max_avel,     // Max velocity
					max_aaccel);
		}


		SimplifiedState[] translation_states =
				SimplifiedState.createTrajectoryTrapezoid(0, delta_distance,
						max_vel, max_accel);


		SimplifiedState[] orientation_states =
				SimplifiedState.createTrajectoryTrapezoid(0, delta_angle, max_avel, max_aaccel);

		// Upsample everything to a common timeline
		FullState[] states = new FullState[translation_states.length + orientation_states.length - 1];

		// They always share the same stepping at t=0
		states[0] = createInterpolatedState(translation_states[0], pos_0, direction_unitv,
				orientation_states[0], orient_0, delta_axis, 0);
		int translation_idx = 0, orientation_idx = 0;
		for (int write_idx = 1; write_idx < states.length; write_idx++) {
			if (orientation_idx + 1 == orientation_states.length) {
				translation_idx++;
			} else if (translation_idx + 1 == translation_states.length) {
				orientation_idx++;
			} else if (translation_idx + 1 < translation_states.length &&
					orientation_idx + 1 < orientation_states.length) {
				// Increment the read indices to whom ever has the next lowest time
				if (translation_states[translation_idx+1].t < orientation_states[orientation_idx+1].t) {
					translation_idx++;
				} else {
					orientation_idx++;
				}
			} else {
				throw new IllegalStateException("Shouldn't be possible to get here");
			}

			// Interpolate and write another state
			states[write_idx] =
					createInterpolatedState(translation_states[translation_idx],
							pos_0, direction_unitv,
							orientation_states[orientation_idx],
							orient_0, delta_axis,
							Math.max(translation_states[translation_idx].t,
									orientation_states[orientation_idx].t));
		}
		return states;
	}

	//////////////////////////////////////////////////////////////////////////////
	// Given some linear or angular displacement (d) and a maximum acceleration //
	// (a) and velocity (v), return the time taken (t) to achieve the motion as //
	// quickly as possible. Also return the time (r) needed to accelerate to or //
	// decelerate from the cruise phase, which lasts a given time (c) at a      //
	// constant velocity (h).                                                   //
	//////////////////////////////////////////////////////////////////////////////
	static double[] GreedyRamp(double d,     // Distance
			double v,     // Max velocity
			double a) {     // Max acceleration
		d = Math.abs(d);
		if (d < epsilon_)             // Doesn't work for small / negative numbers
			return new double[]{0.0, 0.0};
		double r, c, h;

		h = Math.sqrt(a * d);              // The max vel required if one had zero dwell
		if (h > v) {                  // If the required velocity is too high
			h = v;                      // Clamp the velocity to maximum
			r = h / a;                  // Time taken to ramp up and down to max vel
			c = (d - h * h / a) / h;    // Dwell time at maxmimum velocity
		} else {                      // If we don't need to achieve max velocity
			r = d / h;                  // Time taken to ramp up/down to right vel
			c = 0.0;                    // Time at constant velocity
		}
		double min_time =  (r * 2.0 + c);         // Minimum time required to complete action

		return new double[]{min_time, h}; 
	}

	//////////////////////////////////////////////////////////////////////////////
	// Smear a linear or angular displacement (d) over some time (t) taking     //
	// into account a maximum acceleration (a) and velocity (v). The algorithm  //
	// finds resultant time (r) needed  to accelerate to / decelerate from      //
	// cruise time (c) at a constant velocity (h).                              //
	//////////////////////////////////////////////////////////////////////////////
	static double FairRamp(double t,     // Time
			double d,     // Distance
			double v,     // Max velocity
			double a) {     // Max acceleration
		d = Math.abs(d);
		if (t < epsilon_ ||
				d < epsilon_)         // This doesn't work for small / negative numbers
			return 0;
		double r,c,h;
		// If a triangle ramp-up results in correct displacement, then pyramidal
		if (Math.abs(t * t * a / 4.0 - d) < epsilon_) {
			h = Math.sqrt(a * d);
			r = t / 2.0;
			c = 0.0;
			// In the case of a trapezoidal ramp, things are more complex to calculate
		} else {
			h = (a * t - Math.sqrt(a * a * t * t - 4.0 * d * a)) / 2.0;
			r = h / a;
			c = (d - r * h) / h;
		}
		return h;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		long tHash = Double.doubleToLongBits(t);
		result = prime * result + (int)(tHash ^ tHash >>> 32);

		for(int i=0; i<3; i++) {
			long aHash = Double.doubleToLongBits(pos[i]);
			result = prime * (int)(aHash ^ aHash >>> 32);
			aHash = Double.doubleToLongBits(vel[i]);
			result = prime * (int)(aHash ^ aHash >>> 32);
			aHash = Double.doubleToLongBits(accel[i]);
			result = prime * (int)(aHash ^ aHash >>> 32);
			aHash = Double.doubleToLongBits(ang_vel[i]);
			result = prime * (int)(aHash ^ aHash >>> 32);
			aHash = Double.doubleToLongBits(ang_accel[i]);
			result = prime * (int)(aHash ^ aHash >>> 32);
		}

		result = prime * result + ((orient == null) ? 0 : orient.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(o == null) {
			return false;
		}
		if(!(o instanceof FullState)) {
			return false;
		}
		FullState other = (FullState)o;

		if(!NumberUtil.equals(t, other.t)) {
			return false;
		}

		if(!NumberUtil.equals(pos, other.pos)) {
			return false;
		}

		if(!NumberUtil.equals(vel, other.vel)) {
			return false;
		}

		if(!NumberUtil.equals(accel, other.accel)) {
			return false;
		}

		if(!NumberUtil.equals(ang_vel, other.ang_vel)) {
			return false;
		}

		if(!NumberUtil.equals(ang_accel, other.ang_accel)) {
			return false;
		}

		if(!orient.equals(other.orient)) {
			return false;
		}

		return true;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(pos[0]+","+pos[1]+","+pos[2] + " / ");
		sb.append(vel[0]+","+vel[1]+","+vel[2] + " / ");
		sb.append(accel[0]+","+accel[1]+","+accel[2] + " / ");
		sb.append(ang_vel[0]+","+ang_vel[1]+","+ang_vel[2] + " / ");
		sb.append(ang_accel[0]+","+ang_accel[1]+","+ang_accel[2] + " / ");
		return sb.toString();
	}

}
