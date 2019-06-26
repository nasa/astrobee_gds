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
package gov.nasa.arc.irg.freeflyer.rapid.faults;

import java.util.List;

import rapid.ext.astrobee.Fault;
import rapid.ext.astrobee.FaultInfo;

public class GdsFault {
	// permanent things
	private final long faultId;
	private final String description;
	private final short subsystemCode;
	private String subsystem;
	private final short nodeCode;
	private String node;
	private final boolean warning;
	
	// changing things
	private String message;
	private long timestamp;
	
	public String toString() {
		return faultId + " " + description + " is warning " + warning;
	}
	
	public GdsFault(GdsFault original) {
		faultId = original.faultId;
		description = new String(original.description);
		subsystemCode = original.subsystemCode;
		subsystem = new String(original.subsystem);
		nodeCode = original.nodeCode;
		if(original.node != null) {
			node = new String(original.node);
		}
		warning = original.warning;
		
		if(original.message != null) {
			message = new String(original.message);
		} else {
			message = null;
		}
		timestamp = original.timestamp;
	}
	
	public GdsFault(FaultInfo original) {
		subsystemCode = original.subsystem;
		nodeCode = original.node;
		faultId = original.faultId;
		warning = original.warning;
		description = original.faultDescription;
	}
	
	public void setSubsystemName(List<String> subsystems) {
		subsystem = subsystems.get(subsystemCode);
	}
	
	public void setNodeName(List<String> nodes) {
		node = nodes.get(nodeCode);
	}
	
	public void setTo(Fault fault) {
		if(fault.code != faultId) {
			System.err.println("Wrong fault code applied");
		}
		timestamp = fault.timestamp;
		message = fault.message;
	}

	public long getFaultId() {
		return faultId;
	}

	public String getDescription() {
		return description;
	}

	public short getSubsystemCode() {
		return subsystemCode;
	}

	public short getNodeCode() {
		return nodeCode;
	}
	
	public String getSubsystem() {
		return subsystem;
	}

	public String getNode() {
		return node;
	}

	public boolean isWarning() {
		return warning;
	}

	public String getMessage() {
		return message;
	}

	public long getTimestamp() {
		return timestamp;
	}
}
