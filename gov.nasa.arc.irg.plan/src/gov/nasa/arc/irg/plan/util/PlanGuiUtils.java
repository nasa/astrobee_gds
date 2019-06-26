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

public class PlanGuiUtils {
	private static String blankDash = "-";

	// ONEWORD -> Oneword
	// TWO_WORDS -> Two Words
	// oneword -> Oneword
	// two_words -> Two Words
	public static String toTitleCase(String input) {
		if(input == null) {
			return blankDash;
		}

		if(input.length() < 2) {
			return input;
		}


		int spaceIndex = input.indexOf("_");

		if(spaceIndex < 0) {
			// no spaces
			return input.substring(0, 1).toUpperCase()
					+ input.substring(1).toLowerCase();
		} else {
			return input.substring(0, 1).toUpperCase()
					+ input.substring(1, spaceIndex).toLowerCase()
					+ " "
					+ input.substring(spaceIndex+1, spaceIndex+2).toUpperCase()
					+ input.substring(spaceIndex+2).toLowerCase();
		}
	}
}
