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

import gov.nasa.arc.irg.plan.model.Plan;
import rapid.AckCompletedStatus;

public class ExecutingPlan extends ExecutingObject {
	private String name;
	private Plan plan;
	protected long duration;
	protected AckCompletedStatus completedStatus;
	protected int noCommand = -1;
	
	public ExecutingPlan(Plan plan) {
		this.name = plan.getName();
		duration = -1;
		completedStatus = null;
		children = null;
	}
	
	@Override
	public ExecutingObject getParent() {
		return null;
	}
	
	@Override
	public boolean hasChildren() {
		if(children != null && children.length > 0) {
			return true;
		}
		return false;
	}

	@Override
	public ExecutingSequenceable[] getChildren() {
		return children;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public long getDuration() {
		return duration;
	}

	@Override
	public AckCompletedStatus getStatus() {
		return completedStatus;
	}
}
