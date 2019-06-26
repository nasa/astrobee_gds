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

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;

public class OptionsUtil {

	private static final Logger logger      = Logger.getLogger(OptionsUtil.class);
	private HashMap<String, String> options       = new HashMap<String, String>();
	private static OptionsUtil INSTANCE     = null;
	
	private OptionsUtil() {
		readParameters();
	}
	
	public static OptionsUtil getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new OptionsUtil();
		}
		
		return INSTANCE;
	}

	public void readParameters() {
		String[] args = Platform.getCommandLineArgs();
		
		int i = 0;
		try {
			while (i < args.length) {
				String key = args[i];
			    String value;
			    if (key.startsWith("-")) {
			    	try {
			    		String test = args[i+1];
			    	    if (test.startsWith("-")) {
			    	    	value = test;
			    	    } else {
			    	    	value = key;
			    	    }
			    	} catch (Throwable t) {
			    		value = key;
			    	}
			    	
				    options.put(key, value);
			    }
			    
			    i++;
			}
		} catch (Exception e) {
			logger.error("An error occured when trying to read input parameters.");
		}
		
		System.out.println("here.");
	}
	
	public static String getOption(String optionName) throws Exception {
		 try {
			 return getInstance().options.get(optionName);
		 } catch (Exception e) {
			 //log and throw it
			 logger.error("An error occured when trying to read input parameters.");
			 throw e;
		 }
	}

}
