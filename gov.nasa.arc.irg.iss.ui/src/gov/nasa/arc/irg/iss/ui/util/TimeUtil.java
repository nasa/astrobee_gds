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
package gov.nasa.arc.irg.iss.ui.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {

	public static SimpleDateFormat s_fullFormatUTC = new SimpleDateFormat("ddMMMyy HH:mm:ss");
	{
		s_fullFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	protected static SimpleDateFormat s_timeFormatUTC = new SimpleDateFormat("HH:mm:ss");
	{
		s_timeFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	public static final SimpleDateFormat s_dateFormatUTC = new SimpleDateFormat("ddMMMyy");
	{
		s_dateFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	/**
	 * @return now in UTC printed as date with time
	 */
	public static String now() {
		return s_fullFormatUTC.format(new Date());
	}
	
	/**
	 * @param date
	 * @return given date in UTC printed as date with time
	 */
	public static String format(Date date) {
		return s_fullFormatUTC.format(date);
	}
	
	/**
	 * @return new in UTC printed as time only
	 */
	public static String time() {
		return s_timeFormatUTC.format(new Date());
	}
	
}
