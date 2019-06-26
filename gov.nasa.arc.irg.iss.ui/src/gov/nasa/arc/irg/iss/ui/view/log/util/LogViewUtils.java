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
package gov.nasa.arc.irg.iss.ui.view.log.util;

import gov.nasa.util.StrUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.log4j.Level;

public class LogViewUtils {
	public static final SimpleDateFormat s_sdf = new SimpleDateFormat("ddMMMyy HH:mm:ss.SSS");
	public static final SimpleDateFormat s_sdf_date = new SimpleDateFormat("ddMMMyy");
	public static final SimpleDateFormat s_sdf_time = new SimpleDateFormat("HH:mm:ss");
	
	public static String extractFormattedGPS(String msg) {
		StringTokenizer stok = new StringTokenizer(msg, " ");
		int tokens = stok.countTokens();
		String token = null;
		String time = null;
		
		for (int i = 0; i < tokens; i++) {
			token = stok.nextToken();
			switch (i) {
			case 0: {
				// check to see if it is actually a date or time if not it is a description
				if (checkIfTime(token))
					time = token;
				break;
			}
			case 1: {
				if (checkIfTime(token)) {
				if (time!=null)
					time += " " + token;
				else
					time = token;
				}
				break;
			}
			}
		}
		return time;
	}
	
	public static boolean checkIfTime(String t) {
		try {
			s_sdf_date.parse(t);
			return true;
		} catch (Exception e) {
			try {
				s_sdf_time.parse(t);
				// if it doensn't fail
				return true;
			} catch (Exception e2) {
				return false;
			}
		}
	}
	
	
	
	public static String convertToCorrectDateFormat(long time) {
		String ds = ""; // ensures that a null can't be returned
		Date d = new Date(time);
		ds = s_sdf.format(d);
		return ds;
	}
	
	public static Date convertToDate(String timeString) throws ParseException {
		return s_sdf.parse(timeString);
		
	}

	public static String getEventLevelString(Level level){
		if (level == null){
			return "";
		}
		if (level.equals(Level.WARN)){
			return "Alert: ";
		}
		String result =  level.toString();
		return StrUtil.upperFirstChar(result, true) + ": ";
	}
	
}
