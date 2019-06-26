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

import gov.nasa.arc.irg.plan.freeflyer.plan.PointCommand;
import gov.nasa.arc.irg.plan.model.PlanCommand;
import gov.nasa.arc.irg.plan.model.Segment;
import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.model.Station;
import rapid.AckCompletedStatus;
import rapid.ext.astrobee.Status;

public class ExecutingSequenceable extends ExecutingObject {

	private Sequenceable seq;
	private ExecutingObject parent;
	private final PointCommand pointCommand;
	
	public ExecutingSequenceable(Sequenceable seq, int point, int command) {
		this.seq = seq;
		duration = -1;
		completedStatus = null;
		children = null;
		parent = null;
		this.pointCommand = new PointCommand(point, command);
	}
	
	public ExecutingSequenceable(Sequenceable seq, ExecutingObject parent, int point, int command) {
		this.seq = seq;
		duration = -1;
		completedStatus = null;
		children = null;
		this.parent = parent;
		this.pointCommand = new PointCommand(point, command);
	}
	
	@Override
	public void setChildren(ExecutingSequenceable[] children) {
		this.children = children;
	}
	
	@Override
	public ExecutingObject getParent() {
		return parent;
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
	public void setStatus(Status stat) {
		duration = stat.duration;
		completedStatus = stat.status;
	}

	@Override
	public String getName() {
		if(seq instanceof Station) {
			return seq.getName() + " Station";
		}
		if(seq instanceof Segment) {
			return seq.getName() + " Segment";
		}
		return seq.getName();
	}
	
	public boolean matches(int currentPoint, int currentCommand) {
		if(seq instanceof PlanCommand) {
			int point = seq.getRapidNumber();
			int command = ((PlanCommand)seq).getCommandNumber();
			if(currentPoint == point && currentCommand == command) {
				return true;
			}
			return false;
		}

		// seq is Station or Segment
		int point = seq.getRapidNumber();
		if(currentPoint == point && currentCommand == noCommand) {
			return true;
		}
		return false;
	}

	public Sequenceable getSequenceable() {
		return seq;
	}

	@Override
	public long getDuration() {
		return duration;
	}

	@Override
	public AckCompletedStatus getStatus() {
		return completedStatus;
	}
	
	public PointCommand getPointCommand() {
		return pointCommand;
	}
	
	public int getPoint() {
		return pointCommand.point;
	}

	public int getCommand() {
		return pointCommand.command;
	}

	@Override
	public String toString() {
		if(completedStatus != null) {
			return getName() + " " + completedStatus.name();
		}
		return getName();
	}
}
