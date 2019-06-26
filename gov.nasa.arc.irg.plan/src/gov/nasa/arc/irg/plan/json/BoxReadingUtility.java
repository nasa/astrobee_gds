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
package gov.nasa.arc.irg.plan.json;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;

public class BoxReadingUtility {
	private static Logger logger = Logger.getLogger(BoxReadingUtility.class);
	private static DecimalFormat formatter = new DecimalFormat("#.#####");
	public static final int POINTS_IN_A_BOX = 6;
	
	public static double[] readBoxFromJsonArray(JSONArray jsonBox) {
		formatter.setRoundingMode(RoundingMode.HALF_UP);
		
		if(jsonBox.size() != POINTS_IN_A_BOX) {
			logger.info("Obstacle must be defined with "+ POINTS_IN_A_BOX + " numbers.");
			double[] box = {0, 0, 0, 0, 0, 0};
			return box;
		}
		
		double x0 = convertToDouble(jsonBox.get(0));
		double y0 = convertToDouble(jsonBox.get(1));
		double z0 = convertToDouble(jsonBox.get(2));
		double x1 = convertToDouble(jsonBox.get(3));
		double y1 = convertToDouble(jsonBox.get(4));
		double z1 = convertToDouble(jsonBox.get(5));
		
		// humans make mistakes
		double lowX, lowY, lowZ, highX, highY, highZ;
		lowX = Math.min(x0, x1);
		lowY = Math.min(y0, y1);
		lowZ = Math.min(z0, z1);
		highX = Math.max(x0, x1);
		highY = Math.max(y0, y1);
		highZ = Math.max(z0, z1);
		
		double[] box =  {lowX, lowY, lowZ, highX, highY, highZ};
		return box;
	}

	private static double convertToDouble(Object in) {
		if(in instanceof Double) {
			double num = (double) in;
			return Double.parseDouble(formatter.format(num));
		}
		if(in instanceof Long) {
			Long l = (Long) in;
			return l.doubleValue();
		}
		return 0;
	}
	
}
