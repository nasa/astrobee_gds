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

public class PlatformInfo {
	protected static PlatformInfo s_instance = null;

	protected static PlatformInfo instance() {
		if (s_instance == null) {
			s_instance = new PlatformInfo();
		}
		return s_instance;
	}

	private OS m_os;

	public enum OS {
		Linux("linux"), Mac("macosx"), Windows("win32"), Unknown("unknown");

		String os;

		OS(String osName) {
			os = osName;
		}
	}

	public static OS getOS() {
		return instance().m_os;
	}

	public static boolean invertButtonOrder() {
		if (instance().m_os == OS.Mac) {
			return true;
		}
		return false;
	}

	protected PlatformInfo() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.indexOf("mac") >= 0) {
			m_os = OS.Mac;
		} else if (osName.indexOf("linux") >= 0) {
			m_os = OS.Linux;
		} else if (osName.indexOf("win") >= 0) {
			m_os = OS.Windows;
		} else {
			m_os = OS.Unknown;
		}
	}
}
