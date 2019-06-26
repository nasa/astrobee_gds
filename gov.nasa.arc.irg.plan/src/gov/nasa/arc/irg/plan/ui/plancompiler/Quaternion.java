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

public class Quaternion {
  public double[] vec;
  public double scalar;

  public Quaternion() {
    vec = new double[3];
    vec[0] = vec[1] = vec[2] = 0.0;
    scalar = 1.0;
  }

  public Quaternion(double x, double y, double z, double w) {
    vec = new double[3];
    vec[0] = x;
    vec[1] = y;
    vec[2] = z;
    scalar = w;
  }
  
	public Quaternion(double roll, double pitch, double yaw) //  roll (X), pitch (Y), yaw (Z),
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
	    vec = new double[3];
	    vec[0] = x;
	    vec[1] = y;
	    vec[2] = z;
	    scalar = w;
	}

  public void normalize() {
    double length = Math.sqrt(vec[0]*vec[0] +
        vec[1]*vec[1] +
        vec[2]*vec[2] +
        scalar*scalar);
    vec[0] /= length;
    vec[1] /= length;
    vec[2] /= length;
    scalar /= length;
  }

  public static Quaternion inverse(Quaternion q) {
    return new Quaternion(-q.vec[0], -q.vec[1], -q.vec[2], q.scalar);
  }

  public static Quaternion multiply(Quaternion q, Quaternion p) {
    return new Quaternion(q.scalar * p.vec[0] + q.vec[0] * p.scalar - q.vec[1] * p.vec[2] + q.vec[2] * p.vec[1],
                          q.scalar * p.vec[1] + q.vec[1] * p.scalar - q.vec[2] * p.vec[0] + q.vec[0] * p.vec[2],
                          q.scalar * p.vec[2] + q.vec[2] * p.scalar - q.vec[0] * p.vec[1] + q.vec[1] * p.vec[0],
                          q.scalar * p.scalar - q.vec[0] * p.vec[0] - q.vec[1] * p.vec[1] - q.vec[2] * p.vec[2]);
  }

  public static double angle(Quaternion q) {
    if (1.0 - Math.abs(q.scalar) < 1e-6)
      return 0;
    return Math.acos(q.scalar) * 2.0;
  }

  public static double[] axis(Quaternion q) {
    double[] axis = new double[3];
    if (1.0 - Math.abs(q.scalar) < 1e-6) {
      axis[0] = 0;
      axis[1] = 0;
      axis[2] = 0;
      return axis;
    }
    double multiplier = 1.0 / Math.sqrt(1.0 - q.scalar * q.scalar);
    axis[0] = q.vec[0] * multiplier;
    axis[1] = q.vec[1] * multiplier;
    axis[2] = q.vec[2] * multiplier;
    return axis;
  }

  public static Quaternion axisangle(double angle, double[] axis) {
    double sina = Math.sin(angle / 2.0);
    return new Quaternion(axis[0] * sina,
                          axis[1] * sina,
                          axis[2] * sina,
                          Math.cos(angle / 2.0));
  }
  
  @Override
  public int hashCode() {
	  final int prime = 19;
	  int result = Double.valueOf(scalar).hashCode();
	  result = prime * result + Double.valueOf(vec[0]).hashCode();
	  result = prime * result + Double.valueOf(vec[1]).hashCode();
	  result = prime * result + Double.valueOf(vec[2]).hashCode();
	  return result;
  }
  
  @Override
  public boolean equals(Object o) {
	  double EPSILON = 1e-3;
	  if(this == o) {
		  return true;
	  }
	  if(!(o instanceof Quaternion)) {
		  return false;
	  }
	  Quaternion other = (Quaternion)o;
	  if(Math.abs(vec[0]-other.vec[0]) > EPSILON) {
		  return false;
	  }
	  if(Math.abs(vec[1]-other.vec[1]) > EPSILON) {
		  return false;
	  }
	  if(Math.abs(vec[2]-other.vec[2]) > EPSILON) {
		  return false;
	  }
	  if(Math.abs(scalar-other.scalar) > EPSILON) {
		  return false;
	  }
	  return true;
  }
  
}
