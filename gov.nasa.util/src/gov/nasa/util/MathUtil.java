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
package gov.nasa.util;

public class MathUtil {

	/**
	 * return the next largest number which is a power of two. If number is a power of two, the same number will be returned.
	 * 
	 * @param value
	 * @return
	 */
	public static int nextPowerOfTwo(int value) {
		double p = log2(value);
		double sz = Math.pow(2, Math.ceil(p));
		return (int) (sz + 0.1);
	}

	/**
	 * return the next smallest number which is a power of two. If number is a power of two, the same number will be returned.
	 * 
	 * @param value
	 * @return
	 */
	public static int lastPowerOfTwo(int value) {
		double p = log2(value);
		double sz = Math.pow(2, Math.floor(p));
		return (int) (sz + 0.1);
	}

	static final double base2 = 1.0 / Math.log(2.0);

	public static double log2(double d) {
		return Math.log(d) * base2;
	}
}
