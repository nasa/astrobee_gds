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
package gov.nasa.arc.verve.freeflyer.workbench.plantrace;

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.freeflyer.rapid.runningplan.ExecutingSequenceable;
import gov.nasa.arc.irg.freeflyer.rapid.runningplan.RunningPlanInfo;
import gov.nasa.arc.irg.freeflyer.rapid.runningplan.RunningPlanInfoListener;
import gov.nasa.arc.irg.plan.model.Plan;
import gov.nasa.arc.irg.plan.model.Point6Dof;
import gov.nasa.arc.irg.plan.model.Segment;
import gov.nasa.arc.irg.plan.model.SequenceHolder;
import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.model.Station;
import gov.nasa.arc.verve.robot.freeflyer.plan.AbstractPlanTrace;
import gov.nasa.arc.verve.robot.freeflyer.plan.SegmentModel;
import gov.nasa.arc.verve.robot.freeflyer.plan.StationModel;
import gov.nasa.rapid.v2.e4.agent.Agent;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;

import rapid.AckCompletedStatus;
import rapid.AckStatus;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;

public class RunPlanTrace extends AbstractPlanTrace implements RunningPlanInfoListener {
	protected Sequenceable lastSequenceable;
	protected boolean traceDrawn = false;
	protected RunningPlanInfo runningPlanInfo = null;
	protected Agent oldBee = null;

	protected float[] idle = new float[] { 0f, 0f, 0f, 1f };
	protected float[] running = new float[] { (0f/255f), (255f/255f), (127f/255f), 1f };
	protected float[] runningNum = new float[] { (186f/255f), (224f/255f), (180f/255f), 1f };
	//35 79 DC
	protected float[] skipped = new float[] { (53f/255f), (121f/255f), (220f/255f), 1f };
	protected float[] complete = new float[] { (212f/255f), (212f/255f), (212f/255f), 1f };
	protected float[] failed = new float[] { (255f/255f), (133f/255f), (36f/255f), 1f };

	protected MaterialState idleMtl, runningMtl, runningSegMtl, skippedMtl, completeMtl, failedMtl;
	protected ColorRGBA idleCol, runningCol, runningTextCol, skippedCol, completeCol, failedCol;

	@Inject
	public RunPlanTrace() {
		super("RunPlanTrace");
		instance = this;
		setupColors();
	}

	public RunPlanTrace(String nodeName) {
		super(nodeName);
		setupColors();
	}
	
	@Inject
	@Optional
	public void onAgentSelected(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent primaryBee) {
		if(primaryBee == null) {
			return;
		}
		if(oldBee == null) {
			oldBee = primaryBee;
			return;
		}

		if(oldBee.equals(primaryBee)) {
			// do nothing
			return;
		} else {
			// different agent was selected.  Clear the trace.
			this.hide();
			primaryBee = oldBee;
		}
	}

	@Inject @Optional
	public void acceptRunningPlanInfo(@Named(FreeFlyerStrings.RUNNING_PLAN_INFO)RunningPlanInfo rpi) {
		ingestRunningPlanInfo(rpi);
	}

	protected synchronized void ingestRunningPlanInfo(RunningPlanInfo rpi) {
		if(rpi == null) {
			eraseTheTrace();
			traceDrawn = false;
			return;
		}
		runningPlanInfo = rpi;
		runningPlanInfo.setRunnningPlanInfoListener(this);
		plan = rpi.getPlan();
		if(!traceDrawn) {
			drawTheTrace();
			traceDrawn = true;
		}
	}

	@Override
	protected StationModel drawBigAndLittleStationMarker( Station current ) {
		Vector3 verveCurrPos = changePositionToVector3( current.getEndPosition() );
		Point6Dof endPt = current.getCoordinate();
		StationModel forMarker = makeRotatedNode( current, 
				verveCurrPos,
				endPt.getRoll(), endPt.getPitch(), endPt.getYaw() );
		forMarker.hideBigMarker();
		forMarker.showLittleLabel();
		forMarker.showColor(idleCol, idleMtl);
		attachChild( forMarker );
		return forMarker;
	}

	@Override
	protected Node drawASegment(Segment segment, String name) {
		SegmentModel segNode = (SegmentModel) super.drawASegment(segment, name);
		segNode.showColor(idleCol, idleMtl);
		return segNode;
	}

	public synchronized void updateRunningPlanInfo() {
		if(plan == null) {
			return;
		}
		ExecutingSequenceable[] elems = runningPlanInfo.getExecutingSequenceables();
		
		if(elems == null) {
			return;
		}

		for(int i=0; i<elems.length; i++) {
			if(elems[i].getCommand() > -1) {
				continue;
			}
			Sequenceable candidate = findSequenceable(elems[i].getPoint());

			if(elems[i].getStatus() != null &&
					elems[i].getStatus().equals(AckCompletedStatus.ACK_COMPLETED_OK)) {
				setCompleteMaterial((SequenceHolder)candidate);
			} else if(elems[i].getStatus() != null &&
					elems[i].getStatus().equals(AckCompletedStatus.ACK_COMPLETED_CANCELED)) {
				setSkippedMaterial((SequenceHolder)candidate);
			} else if(elems[i].getStatus() != null &&
					elems[i].getStatus().equals(AckCompletedStatus.ACK_COMPLETED_EXEC_FAILED)) {
				setFailedMaterial((SequenceHolder)candidate);
			}
		}
		lastSequenceable = null;

		if(runningPlanInfo.getCurrentStatus().equals(AckStatus.ACK_EXECUTING)) {
			Sequenceable runner = findSequenceable(runningPlanInfo.getCurrentPoint());

			setRunningMaterial((SequenceHolder)runner);
			//if(runner instanceof Segment) {
			//	Segment segment = (Segment) runner;
			//	setRunningMaterial((SequenceHolder)segment.getNext());
			//}
		}
	}

	protected Sequenceable findSequenceable(int point) {
		if(point < 0) {
			return null;
		}
		Sequenceable trial;
		if(lastSequenceable == null) {
			if(plan != null) {
				trial = plan.getFirstSequenceable();
			} else {
				System.err.println("RunPlanTrace: Can't find Sequenceable in null Plan");
				return null;
			}
		} else {
			trial = Plan.getNext(lastSequenceable);
		}
		if(trial != null && trial.getRapidNumber() == point) {
			lastSequenceable = trial;
			return trial;
		}
		else {
			for(Sequenceable trial2 : plan.getSequence()) {
				if(trial2.getRapidNumber() == point) {
					lastSequenceable = trial2;
					return trial2;
				}
			}
		}
		System.err.println("RunPlanTrace: Couldn't match Sequenceable");
		return null;
	}

	protected void setIdleMaterial(SequenceHolder seq) {
		boolean showLabel = seq instanceof Station;
		setMaterial(seq, idleCol,null, idleMtl, showLabel);
	}

	protected void setRunningMaterial(SequenceHolder seq) {

		if(seq == null) {
			return;
		}

		if(seq instanceof Segment) {
			setMaterial(seq, runningCol,runningTextCol, runningSegMtl, true);
		} else {
			setMaterial(seq, runningCol,null, runningMtl, true);
		}
	}

	protected void setSkippedMaterial(SequenceHolder seq) {
		if(seq == null) {
			return;
		}
		boolean showLabel = seq instanceof Station;
		setMaterial(seq, skippedCol, null, skippedMtl, showLabel);
	}

	protected void setCompleteMaterial(SequenceHolder seq) {
		boolean showLabel = seq instanceof Station;
		setMaterial(seq, completeCol, null, completeMtl, showLabel);
	}

	protected void setFailedMaterial(SequenceHolder seq) {
		boolean showLabel = seq instanceof Station;
		setMaterial(seq, failedCol, null, failedMtl, showLabel);
	}

	protected void setMaterial(SequenceHolder seq, ColorRGBA col,ColorRGBA textCol, MaterialState mtl, boolean showLabel) {
		if(seq == null) {
			return;
		}

		showChild(seq, "");

		if(seq instanceof Segment) {
			Spatial segment = getChild(getSequenceableName(seq) + arrowString);
			SegmentModel segModel = (SegmentModel) segment;
			if(textCol != null)
				segModel.showColor(col,textCol, mtl);
			else
				segModel.showColor(col, mtl);
			if(showLabel) {
				segModel.showLabel();
			} else {
				segModel.hideLabel();
			}
		}

		if(seq instanceof Station) {
			Spatial station = getChild(getSequenceableName(seq) + stationString);
			StationModel statModel = (StationModel) station;
			statModel.showColor(col, mtl);

			if(showLabel) {
				statModel.showLittleLabel();
			} else {
				statModel.hideLittleLabel();
			}
		}
	}

	protected void setupColors() {
		idleMtl = makeMaterialState(idle[0], idle[1], idle[2], idle[3], true);
		runningMtl = makeMaterialState(running[0], running[1], running[2], running[3]);
		runningSegMtl = makeMaterialState(running[0], running[1], running[2], running[3], true);
		skippedMtl = makeMaterialState(skipped[0], skipped[1], skipped[2], skipped[3]);
		completeMtl = makeMaterialState(complete[0], complete[1], complete[2], complete[3]);
		failedMtl = makeMaterialState(failed[0], failed[1], failed[2], failed[3]);

		idleCol = new ColorRGBA(idle[0], idle[1], idle[2], idle[3]);
		runningCol = new ColorRGBA(running[0], running[1], running[2], running[3]);
		runningTextCol = new ColorRGBA(runningNum[0], runningNum[1], runningNum[2], runningNum[3]);
		skippedCol = new ColorRGBA(skipped[0], skipped[1], skipped[2], skipped[3]);
		completeCol = new ColorRGBA(complete[0], complete[1], complete[2], complete[3]);
		failedCol = new ColorRGBA(failed[0], failed[1], failed[2], failed[3]);
	}
}
