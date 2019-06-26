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
package gov.nasa.arc.irg.plan.model.modulebay;


public class Point3D {
	private double x;
	private double y;
	private double z;
	private final double EPSILON = 0.01;
	
	public Point3D (double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int)Double.doubleToLongBits(x);
		result = prime * result + (int)Double.doubleToLongBits(y);
		result = prime * result + (int)Double.doubleToLongBits(z);
		return result;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(!(o instanceof Point3D)) {
			return false;
		}
		
		Point3D other = (Point3D)o;
		
		if( !equalToEpsilon(getX(), other.getX()) ) {
			return false;
		}
		if( !equalToEpsilon(getY(), other.getY()) ) {
			return false;
		}
		if( !equalToEpsilon(getZ(), other.getZ()) ) {
			return false;
		}
		return true;
	}
	
	private boolean equalToEpsilon(double a, double b) {
		if(Math.abs(a-b) < EPSILON) {
			return true;
		}
		return false;
	}
	
	public static Point3D getMidpoint(Point3D one, Point3D two) {
		return new Point3D(	(one.getX() + two.getX()) / 2,
				(one.getY() + two.getY()) / 2,
				(one.getZ() + two.getZ()) / 2);			
	}
	
	public double[] toArray() {
		return new double[] {x, y, z};
	}
	
	@Override
	public String toString() {
		x = Math.round(x * 100) / 100.0;
		y = Math.round(y * 100) / 100.0;
		z = Math.round(z * 100) / 100.0;
		
		return "(" + x + ", " + y + ", " + z + ")";
	}
}
