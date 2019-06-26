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
package gov.nasa.arc.irg.util.undo;

/**
 * Simple status for undo operation results
 * @author tecohen
 *
 */
public enum UndoStatus {
	OK(0),
	INFO(0x01),
	WARNING(0x02),
	ERROR(0x04),
	CANCEL(0x08);
	
    private final int m_severity; 
    
	UndoStatus(int severity) {
        m_severity = severity;
    }
	
	public int getSeverity() {
		return m_severity;
	}

}
