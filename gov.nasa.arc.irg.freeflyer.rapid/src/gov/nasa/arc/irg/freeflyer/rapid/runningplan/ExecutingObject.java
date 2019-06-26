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
package gov.nasa.arc.irg.freeflyer.rapid.runningplan;

import rapid.AckCompletedStatus;
import rapid.ext.astrobee.Status;

public abstract class ExecutingObject {
	private String name;
	protected long duration;
	protected AckCompletedStatus completedStatus;
	protected int noCommand = -1;
	protected ExecutingSequenceable[] children;
	
	public void setChildren(ExecutingSequenceable[] children) {
		this.children = children;
	}
	
	public abstract ExecutingObject getParent();
	
	public boolean hasChildren() {
		if(children != null && children.length > 0) {
			return true;
		}
		return false;
	}

	public ExecutingSequenceable[] getChildren() {
		return children;
	}
	
	public void setStatus(Status stat) {
		duration = stat.duration;
		completedStatus = stat.status;
	}
	
	public abstract String getName();
	
	public long getDuration() {
		return duration;
	}

	public AckCompletedStatus getStatus() {
		return completedStatus;
	}
	
	@Override
	public String toString() {
		if(completedStatus != null) {
			return getName() + " " + completedStatus.name();
		}
		return getName();
	}
}
