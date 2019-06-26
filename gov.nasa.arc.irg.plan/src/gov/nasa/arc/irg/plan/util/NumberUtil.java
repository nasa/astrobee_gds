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
package gov.nasa.arc.irg.plan.util;

public class NumberUtil {
	static float EPSILON = 0.005f;

	public static boolean equals(Float a, Float b) {
		float diff = a.floatValue() - b.floatValue();

		if(Math.abs(diff) < EPSILON) {
			return true;
		}
		return false;
	}

	public static boolean equals(double[] a, double[] b) {
		if(a.length != b.length) {
			return false;
		}
		for(int i=0; i<a.length; i++) {
			if(!equals(a[i], b[i])) {
				return false;
			}
		}
		return true;
	}

	public static boolean equals(double a, double b) {
		double diff = a - b;
		if(Math.abs(diff) > EPSILON) {
			return false;
		}
		return true;
	}
}
