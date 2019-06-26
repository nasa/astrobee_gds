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
package gov.nasa.arc.irg.iss.ui.view.log.internal;

import gov.nasa.arc.irg.util.log.IrgLevel;

import org.apache.log4j.Level;

public class IssLogEntry {
	
	protected String m_time;
	protected Level m_level;
	protected String m_description;
	protected boolean m_ack;

	public IssLogEntry(String time, String level, String description, boolean ack) {
		m_time = time;
		m_level = IrgLevel.toLevel(level);
		m_description = description;
		m_ack = ack;
	}

	public String getTime() {
		return m_time;
	}

	public String getLevelString() {
		return m_level.toString();
	}
	
	public Level getLevel() {
		return m_level;
	}

	public String getDescription() {
		return m_description;
	}

	public boolean isAcknowledged() {
		return m_ack;
	}
	
	public void acknowledge() {
		m_ack = true;
	}
	
	
	@Override
	public String toString() {
		return m_time + "," + m_level.toString() + "," + m_description + "," + m_ack;
	}
	
	
}
