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

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.plan.freeflyer.plan.FreeFlyerPlan;
import gov.nasa.arc.irg.plan.freeflyer.plan.PointCommand;
import gov.nasa.arc.irg.plan.model.SequenceHolder;
import gov.nasa.arc.irg.plan.model.Sequenceable;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.MApplication;

import rapid.AckCompletedStatus;
import rapid.AckStatus;
import rapid.ext.astrobee.PlanStatus;
import rapid.ext.astrobee.Status;

/** updates as we get Acks and PlanStatus messages from the Astrobee */
public class RunningPlanInfo {
	private static final Logger logger = Logger.getLogger(RunningPlanInfo.class);
	protected FreeFlyerPlan plan;
	protected PlanStatus planStatus;
	protected ExecutingSequenceable[] executingSequenceables;
	protected ExecutingObject executingPlan;
	protected PointCommand isPlayingNow;
	private IEclipseContext context;
	protected RunningPlanInfoListener listener;

	@Inject
	public RunningPlanInfo(IEclipseContext context) {
		MApplication application = context.get(MApplication.class);
		if(application == null) {
			this.context = context;
		} else {
			this.context = application.getContext();
		}

		isPlayingNow = new PointCommand(0, -1);
	}

	public RunningPlanInfo() {
		isPlayingNow = new PointCommand(0, -1);
	}

	public void clear() {
		plan = null;
		planStatus = null;
		executingSequenceables = null;
		executingPlan = null;
		isPlayingNow = new PointCommand(0, -1);
		//		runPlanTrace = ???
	}

	public boolean isValid() {
		return !(plan == null);
	}
	
	public void ingestPlan(FreeFlyerPlan plan) {
		this.plan = plan;
		if(plan == null) {
			executingSequenceables = new ExecutingSequenceable[0];
			return;
		}

//		System.out.println(this.getClass() + " received non-null FreeFlyerPlan");

		executingPlan = new ExecutingPlan(plan);
		executingSequenceables = new ExecutingSequenceable[plan.getSequence().size()];
		isPlayingNow = new PointCommand(0, -1);

		int index = 0;
		int childIndex = 0;
		ExecutingSequenceable[] children;
		for(Sequenceable s : plan.getSequence()) {
			SequenceHolder sh = (SequenceHolder)s;
			executingSequenceables[index] = new ExecutingSequenceable(s, executingPlan, index, -1);
			if(!sh.getSequence().isEmpty()) {
				childIndex = 0;
				children = new ExecutingSequenceable[sh.getSequence().size()];
				for(Sequenceable child : sh.getSequence()) {
					children[childIndex] = new ExecutingSequenceable(child, executingSequenceables[index], index, childIndex);
					childIndex++;
				}
				executingSequenceables[index].setChildren(children);

			} 
			index++;
		}

		executingPlan.setChildren(executingSequenceables);
	}

	@Inject @Optional
	public void acceptPlan(@Named(FreeFlyerStrings.FREE_FLYER_PLAN) FreeFlyerPlan plan) {
		ingestPlan(plan);
		
		if(context != null) {
			context.set(FreeFlyerStrings.RUNNING_PLAN_INFO, null);
			context.set(FreeFlyerStrings.RUNNING_PLAN_INFO, this);
		}
	}

	public void setRunnningPlanInfoListener(RunningPlanInfoListener rpt) {
		listener = rpt;
	}

	public FreeFlyerPlan getPlan() {
		return plan;
	}

	public ExecutingSequenceable[] getExecutingSequenceables() {
		return executingSequenceables;
	}

	public Object[] toArray() {
		if(executingPlan == null) {
			return null;
		}
		return new Object[] {executingPlan};//executingSequenceables;
	}

	public PlanStatus getPlanStatus() {
		return planStatus;
	}

	private boolean isNotCompleted(AckCompletedStatus acs) {
		if(acs == null || acs.equals(AckCompletedStatus.ACK_COMPLETED_NOT)) {
			return true;
		}
		else {
			return false;
		}
	}

	public ExecutingSequenceable getExecutingExecutingSequenceable() {
		return getThisSequenceable(isPlayingNow);
	}

	protected void printSequenceables() {
		System.out.println("%%%%%%%%%%%%%% Executing Sequenceables %%%%%%%%%%%%%% - RunningPlanInfo");

		for(int i=0; i<executingSequenceables.length; i++) {

			if(executingSequenceables[i] == null) {
				continue;
			}

			System.out.println("--" + executingSequenceables[i].getPoint() + ", " 
					+ executingSequenceables[i].getCommand() + " - " 
					+ executingSequenceables[i].getStatus());

			if(executingSequenceables[i].hasChildren()) {
				ExecutingSequenceable[] kids = executingSequenceables[i].getChildren();
				for(int j=0; j<kids.length; j++) {
					System.out.println("\t-" + kids[j].getPoint() + ", " 
							+ kids[j].getCommand() + " - " 
							+ kids[j].getStatus());
				}
			}
		}
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
	}

	private ExecutingSequenceable getThisSequenceable(PointCommand pc) {
		if(pc == null || executingSequenceables == null) {
			return null;
		}
		if(pc.command == -1) {
			if(executingSequenceables.length > pc.point) {
				return executingSequenceables[pc.point];
			}
		}
		if(pc.command > -1) {
			if(executingSequenceables.length > pc.point) {
				ExecutingSequenceable[] kids = executingSequenceables[pc.point].getChildren();
				if(kids.length > pc.command) {
					return kids[pc.command];
				}
			}
		}
		return null;
	}

	/** @return true if success, false if there is a problem */
	public synchronized boolean setPlanStatus( PlanStatus ps ) {
		planStatus = ps;
		// check PlanStatus is for correct plan
		if( plan != null && !planStatus.planName.equals(plan.getName()) ) {
			return false;
		}
		// set the current point and command
		updateCurrentSpot();

		for(int planStatusIndex=0; planStatusIndex<planStatus.statusHistory.userData.size(); planStatusIndex++) {
			Status stat = (Status)planStatus.statusHistory.userData.get( planStatusIndex );
			ExecutingSequenceable eseq = getThisSequenceable( new PointCommand(stat.point, stat.command) );

			if(eseq == null) {
				//logger.debug(stat.point + ", " + stat.command + " was null");
			}
			else if(eseq.getStatus() == null || eseq.getStatus().equals(AckCompletedStatus.ACK_COMPLETED_NOT)) {
				eseq.setStatus( stat );
			}
		}

		if(listener != null) {
			listener.updateRunningPlanInfo();
		}
		return true;
	}

	public boolean isPointCommandExecuting(PointCommand pc) {
		if(pc == null || isPlayingNow == null || planStatus == null || planStatus.currentStatus == null) {
			return false;
		}
		if(isPlayingNow.equals(pc) && planStatus.currentStatus.equals(AckStatus.ACK_EXECUTING)) {
			if(getThisSequenceable(pc) != null) {
				if(isNotCompleted(getThisSequenceable(pc).completedStatus)) {
					return true;
				}
			}
		}
		return false;
	}

	public int getCurrentPoint() {
		if(isPlayingNow == null) {
			return -1;
		}
		return isPlayingNow.point;
	}

	public int getCurrentCommand() {
		if(isPlayingNow == null) {
			return -1;
		}
		return isPlayingNow.command;
	}

	public AckStatus getCurrentStatus() {
		if(planStatus != null) {
			return planStatus.currentStatus;
		} else {
			return null;
		}
	}

	// we define current as "what would start running if you pushed play right now"
	private void updateCurrentSpot() {
		int point = planStatus.currentPoint;
		int command = planStatus.currentCommand;

		if(new PointCommand(point, command).isGreaterThan(isPlayingNow)) {
			if(planStatus.currentStatus.equals(AckStatus.ACK_COMPLETED)) {
				if(plan != null) { //????????
					isPlayingNow = plan.getNextExecutableElement(new PointCommand(point,command));
				}
			}
			else {
				isPlayingNow.set(point, command);
			}
		}
	}
}


