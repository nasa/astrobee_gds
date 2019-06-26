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
package gov.nasa.arc.irg.util.log;

import org.apache.log4j.Level;

public class IrgLevel extends Level {

	private static final long serialVersionUID = 1L;

	public static final IrgLevel ALERT = new IrgLevel(35000, "ALERT", 35000);
		
	protected IrgLevel(int level, String levelStr, int syslogEquivalent) {
		super(level, levelStr, syslogEquivalent);
	}
	
	public static Level toLevel(int level){
		if (level == ALERT.toInt()){
			return ALERT;
		}
		return Level.toLevel(level);
	}
	
	public static Level toLevel(String level){
		if (level.equals(ALERT.toString())){
			return ALERT;
		}
		return Level.toLevel(level);
	}

}
