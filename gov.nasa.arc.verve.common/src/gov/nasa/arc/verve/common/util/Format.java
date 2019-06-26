/*******************************************************************************
 * Copyright (c) 2013 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package gov.nasa.arc.verve.common.util;

import com.ardor3d.math.Matrix3;

public class Format {

	public static String rotationMatrix(Matrix3 m33) {
		double[] m = new double[9];
		m33.toArray(m);
		return rotationMatrixArrayd(m);
	}
	
	public static String rotationMatrixArrayd(double[] m) {
		return String.format(""+
				"%.2f %.2f %.2f\n"+
				"%.2f %.2f %.2f\n"+
				"%.2f %.2f %.2f\n", 
				m[0], m[1], m[2],
				m[3], m[4], m[5],
				m[6], m[7], m[8]);
	}
	public static String rotationMatrixArrayf(float[] m) {
		return String.format(""+
				"%.2f %.2f %.2f\n"+
				"%.2f %.2f %.2f\n"+
				"%.2f %.2f %.2f\n", 
				m[0], m[1], m[2],
				m[3], m[4], m[5],
				m[6], m[7], m[8]);
	}
}
