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
package gov.nasa.ensemble.ui.databinding.util;


/**
 * StringUtil for databinding
 * @author tecohen
 *
 */
public class StringUtil {
	
	

	/**
	 * Take a string.  Return a string that has the first character uppercase.
	 * eg first becomes First
	 * eg FIRST becomes First
	 * @param string
	 * @param lowerTheRest to lowercase the rest of the word, ie Camelcase vs CamelCase
	 * @return
	 */
	public static String upperFirstChar(String string, boolean lowerTheRest){
		if (string == null){
			return null;
		}
		if (string.length() == 0){
			return "";
		}
		
		StringBuffer result = new StringBuffer();
		result.setLength(1);
		String upperFirst = string.substring(0, 1);
		result.setCharAt(0, upperFirst.toUpperCase().charAt(0));
		
		if (string.length() == 1){
			return result.toString();
		}
		
		String rest;
		if (lowerTheRest) {
			rest = string.substring(1).toLowerCase();
		} else {
			rest = string.substring(1);
		}
		result.append(rest);
		return result.toString();
	}
	
	/**
	 * Take a string.  Return a string that has the first character lowercase.
	 * eg First becomes first
	 * eg FIRST stays FIRST
	 * eg ZOffset stays ZOffset
	 * @param string
	 * @param lowerTheRest ie camelCase vs camelcase
	 * @return
	 */
	public static String lowerFirstChar(String string, boolean lowerTheRest){
		
		if (string == null){
			return null;
		}
		if (string.length() == 0){
			return "";
		}
		
		if (string.length() >= 2) {
			if (Character.isUpperCase(string.charAt(1))) {
				return string;
			}
		}
		
		StringBuffer result = new StringBuffer();
		result.setLength(1);
		String upperFirst = string.substring(0, 1);
		result.setCharAt(0, upperFirst.toLowerCase().charAt(0));
		
		if (string.length() == 1){
			return result.toString();
		}
		
		String rest;
		if (lowerTheRest) {
			rest = string.substring(1).toLowerCase();
		} else {
			rest = string.substring(1);
		}
		result.append(rest);
		return result.toString();
	}
	
}
