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

/**
 * This is stupid but we do not yet have official permission that gov.nasa.util is non ITAR so these are the methods from StrUtil that we use.
 * 
 * @author tecohen
 *
 */
public class StrUtil {
	/**
     * Get the ascii number for a character
     * @param c
     * @return
     */
    public static int getAscii(char c){
		return Character.codePointAt(new char[]{c}, 0);
    }
    
    /**
     * Get a char array by converting the number
     * @param ascii
     * @return
     */
    public static char[] getCharFromAscii(int ascii){
    	return Character.toChars(ascii);
    }
}
