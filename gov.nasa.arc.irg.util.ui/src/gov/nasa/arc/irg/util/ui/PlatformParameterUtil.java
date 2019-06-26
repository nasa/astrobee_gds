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
package gov.nasa.arc.irg.util.ui;

import org.eclipse.core.runtime.Platform;

public class PlatformParameterUtil {

	private static boolean m_initialized = false;
	private static boolean m_debug  = false;
	private static boolean m_record = false;
	
	public static synchronized void readCommandLineArgs() {
		String[] clArgs = Platform.getApplicationArgs();
		m_initialized = true;
		for (String s : clArgs){
			if (s.indexOf("DEBUG")>=0){
				m_debug = true;
				continue;
			}
			if (s.indexOf("record")>=0) {
				m_record = true;
				continue;
			}
		}
	}
	
	public static boolean isDebug() {
		if (!m_initialized){
			readCommandLineArgs();
		}
		return m_debug;
	}
	

	/**
	 * should the vnc recording start?
	 * 
	 * @return
	 */
	public static boolean shouldRecord() {
		if (!m_initialized){
			readCommandLineArgs();
		}
		return m_record;
	}
}
