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
package gov.nasa.arc.simulator.freeflyer.plan;

import gov.nasa.arc.irg.plan.freeflyer.command.AbstractGuestScienceCommand;
import gov.nasa.arc.irg.plan.freeflyer.command.PausePlan;
import gov.nasa.arc.irg.plan.freeflyer.command.StartGuestScience;
import gov.nasa.arc.irg.plan.freeflyer.command.StopGuestScience;
import gov.nasa.arc.irg.plan.freeflyer.plan.FreeFlyerPlan;
import gov.nasa.arc.irg.plan.freeflyer.plan.PointCommand;
import gov.nasa.arc.irg.plan.model.PlanBuilder;
import gov.nasa.arc.irg.plan.model.Segment;
import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.model.Station;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPlan;
import gov.nasa.arc.simulator.freeflyer.FreeFlyer;
import gov.nasa.arc.simulator.freeflyer.compress.CompressPublisher;
import gov.nasa.arc.simulator.freeflyer.publishers.GuestScienceApkStatePublisher;
import gov.nasa.arc.simulator.freeflyer.publishers.GuestScienceDataPublisher;
import gov.nasa.arc.simulator.freeflyer.publishers.PositionPublisher;
import gov.nasa.dds.exception.DdsEntityCreationException;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import rapid.AckCompletedStatus;
import rapid.AckStatus;
import rapid.ext.astrobee.PlanStatus;
import rapid.ext.astrobee.Status;

public class PlanSimulator {

	private static final Logger logger = Logger.getLogger(PlanSimulator.class);
	private PositionPublisher positionPublisher;
	private final PlanStatusPublisher statusPublisher = new PlanStatusPublisher();
	private final CompressPublisher compressPublisher = CompressPublisher.getInstance(FreeFlyer.getPartition());
	private List<DataPoint> dataPoints;
	private String currentPlanName;
	private PointCommand currentPointCommand;
	private final int sleeptime = 2000; // Time between each point on a Segment.
	private final float speedup = 3; // divide the PlanCommand times by this.
	private Vector<Status> localStatusHistory;
	private final int historyCapacity = 64;
	private PlanStatus planStatus;
	private PointCommand finalPointCommand;
	private FreeFlyerPlan plan;
	
	private final boolean choppyMovement = false;
	
	private static PlanSimulator INSTANCE;

	public static PlanSimulator getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new PlanSimulator();
		}
		return INSTANCE;
	}

	private PlanSimulator() {
		try {
			initializePublishers();
		} catch (final DdsEntityCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void readPlan(final File planFile){
		final PlanBuilder<ModuleBayPlan> m_planBuilder = PlanBuilder.getPlanBuilder(planFile,ModuleBayPlan.class,true);
		plan = m_planBuilder.getPlan();
		currentPointCommand = new PointCommand(0, -1);
		currentPlanName = plan.getName();
		dataPoints = new ArrayList<PlanSimulator.DataPoint>();
		for(final Sequenceable seq : plan.getSequence()){
			if(seq instanceof Segment) {
				dataPoints.add(new DataPoint((Segment)seq));
			} else if(seq instanceof Station){
				//System.out.println("station");
				dataPoints.add(new DataPoint(seq.getName(),((Station) seq).getSequence()));
				
			}
		}
		finalPointCommand = plan.getFinalPointCommand();
		planStatus = new PlanStatus();
		planStatus.planName = currentPlanName;
		planStatus.currentPoint = 0;
		planStatus.currentCommand = -1;
		planStatus.currentStatus = AckStatus.ACK_QUEUED;
		localStatusHistory = new Vector<Status>();
		copyLocalStatusHistoryIntoPlanStatusHistory(planStatus);
		try {
			statusPublisher.publishSample(planStatus);
			compressPublisher.compressAndPublishSample("publishing currentPlanCompressedFile", planFile);
		} catch (final InterruptedException e) {
			System.err.println("PlanSimulator couldn't send planStatus");
		}
	}
	
	public boolean skipPlanStep() {
		if(plan == null || currentPointCommand == null) {
			return false;
		}
		final PointCommand skipTo = plan.getNextExecutableElement(currentPointCommand);
		if(skipTo == null) {
			return false;
		}
		return skipToPoint(skipTo.point, skipTo.command);
	}

	public boolean skipToPoint( final int goalPoint, final int goalCommand ) {
		System.out.println("Skipping to " + goalPoint + ", " + goalCommand);
		final PointCommand goal = new PointCommand(goalPoint, goalCommand);
		if(goal.isGreaterThan(finalPointCommand)) {
			System.out.println("Too far to skip, returning false.");
			return false;
		}

		try {
			while( goal.requiresSkipFrom(currentPointCommand)) {
				skipOneThing();
			}
			System.out.println("skipped, returning true");
			return true;
		} catch (final InterruptedException e) {
			System.err.println("Problem skipping a plan element (failed, returning false)");
			e.printStackTrace();
			return false;
		}
	}

	private void setCurrentFieldsInPlanStatus(final AckStatus st) {
		planStatus.currentPoint = currentPointCommand.point;
		planStatus.currentCommand = currentPointCommand.command;
		planStatus.currentStatus = st;
	}
	
	// returns true if there are more elements left in the plan
	private void skipOneThing() throws InterruptedException {
		// Could be a segment
		if(currentPointCommand.point % 2 == 1) {
			skipASegment();
			return;
		}

		final DataPoint data = dataPoints.get( currentPointCommand.point );
		// Could be station by itself
		if(data.getSubCommands().isEmpty()) {
			skipEmptyStation();
			return;
		}
		
		// It is a command
		if(currentPointCommand.command < 0) {
			// If first Command at a Station, put Station in the history
			putNotCompletedCommandStatusInLocalHistory( currentPointCommand );
			currentPointCommand.command++;
		}

		// put Command
		
		putCanceledCommandStatusInLocalHistory( currentPointCommand );
		
		// if it was the last command at the station
		if( currentPointCommand.command == ( data.getSubCommands().size()-1 ) ) {
			// set station completed
			final Status stationStatus = findStationStatus( currentPointCommand.point );
			stationStatus.status = AckCompletedStatus.ACK_COMPLETED_CANCELED;
			currentPointCommand.point++;
			currentPointCommand.command = -1;
		} else {
			currentPointCommand.command++;
		}
		setCurrentFieldsInPlanStatus( AckStatus.ACK_QUEUED );
		copyLocalStatusHistoryIntoPlanStatusHistory( planStatus );
		statusPublisher.publishSample(planStatus);
	}
	
	private void skipASegment() throws InterruptedException {
		putCanceledCommandStatusInLocalHistory( currentPointCommand );
		currentPointCommand.point++;
		
		final Station s = (Station) plan.getSequenceableByRapidNumber(currentPointCommand.point);
		// if the next station is empty, queue the next segment
		if(s.getSequence().isEmpty()) {
			// Skip the next station
			putCanceledCommandStatusInLocalHistory( currentPointCommand );
			currentPointCommand.point++;
		} else {
			// station is not complete
			putNotCompletedCommandStatusInLocalHistory(currentPointCommand);
			// queue the first child
			currentPointCommand.command = 0;
		}
		copyLocalStatusHistoryIntoPlanStatusHistory(planStatus);
		setCurrentFieldsInPlanStatus( AckStatus.ACK_QUEUED );
		statusPublisher.publishSample(planStatus);
	}
	
	private void skipEmptyStation() throws InterruptedException {
		// cancel the station
		putCanceledCommandStatusInLocalHistory( currentPointCommand );
		// queue the segment
		currentPointCommand.point++;
		setCurrentFieldsInPlanStatus( AckStatus.ACK_QUEUED );
		copyLocalStatusHistoryIntoPlanStatusHistory(planStatus);
		statusPublisher.publishSample(planStatus);
	}

	public void sendPlanData() throws InterruptedException {
		for(final DataPoint data : dataPoints){
			if(data.getRapidNumber() < currentPointCommand.point) {
				continue;
			}

			if(data.isStation()){
				runAStation(data);
			} else {
				runASegment(data);
			}
		}
		dataPoints = null;
	}

	private Status findStationStatus(final int point) {
		for(final Status s : localStatusHistory) {
			if(s.point == point) {
				if(s.command == -1) {
					return s;
				}
			}
		}
		return null;
	}

	private void runAStation(final DataPoint data) throws InterruptedException {
		planStatus.currentPoint = data.getRapidNumber();
		planStatus.currentStatus = AckStatus.ACK_EXECUTING;
		// don't reset this because we might be continuing a plan that was paused in the middle of 
		// a set of subcommands
		//planStatus.currentCommand = -1;

		Status stationStatus = findStationStatus(data.getRapidNumber());

		System.out.println( "Station " + data.getOriginalName() + " not complete");
		if(stationStatus == null) {
			stationStatus = putNotCompletedCommandStatusInLocalHistory(new PointCommand(data.getRapidNumber(), -1));
			copyLocalStatusHistoryIntoPlanStatusHistory(planStatus);
			statusPublisher.publishSample(planStatus);
		}
		runStationChildren(data);

		// once all the children are done, station is complete
		stationStatus.status = AckCompletedStatus.ACK_COMPLETED_OK;
		planStatus.currentStatus = AckStatus.ACK_COMPLETED;
		statusPublisher.publishSample(planStatus);
		currentPointCommand.point++;
		currentPointCommand.command = -1;
		System.out.println( "Station " + data.getRapidNumber() + " COMPLETE");
	}

	private void runStationChildren(final DataPoint data) throws InterruptedException {
		// do the children
		int cmdNum = 0;
		for(final Sequenceable cmd : data.getSubCommands()) {
			if(cmdNum < currentPointCommand.command) {
				cmdNum++;
				continue;
			}
			
			if(cmd instanceof StartGuestScience){
				final AbstractGuestScienceCommand science = (AbstractGuestScienceCommand)cmd;
				System.out.println("-- Start Guest Science "+science.getApkName());
				GuestScienceApkStatePublisher.getInstance().startApk(science.getApkName());
				GuestScienceDataPublisher.getInstance().publishData(science);
			}
			
			if(cmd instanceof StopGuestScience){
				final AbstractGuestScienceCommand science = (AbstractGuestScienceCommand)cmd;
				System.out.println("-- Stop Guest Science "+science.getApkName());
				GuestScienceApkStatePublisher.getInstance().stopApk(science.getApkName());
				GuestScienceDataPublisher.getInstance().publishData(science);
				GuestScienceDataPublisher.getInstance().stopSummaryPublishing();
			}
			if(cmd instanceof PausePlan) {
				// mark it done so we don't redo this when we resume
				
				planStatus.currentCommand = cmdNum;
				planStatus.currentStatus = AckStatus.ACK_COMPLETED;

				putOkCommandStatusInLocalHistory(new PointCommand(data.getRapidNumber(), cmdNum), 0);

				copyLocalStatusHistoryIntoPlanStatusHistory(planStatus);

				System.out.println( "--- Command " + cmd.getName() + " duration " + 0  + " COMPLETE");
				statusPublisher.publishSample(planStatus);
				cmdNum++;
				currentPointCommand.command = cmdNum;
				// pause the plan
				handleInterruption();
			}
			
			// send the executing message
			currentPointCommand.command = cmdNum;
			planStatus.currentCommand = cmdNum;
			planStatus.currentStatus = AckStatus.ACK_EXECUTING;
			statusPublisher.publishSample(planStatus);

			// wait
			final int dur = cmd.getCalculatedDuration();
			try {
				Thread.sleep((long) (dur/speedup*1000));
			} catch (final InterruptedException e) {
				handleInterruption();
			}

			// send the done message
			planStatus.currentStatus = AckStatus.ACK_COMPLETED;

			putOkCommandStatusInLocalHistory(new PointCommand(data.getRapidNumber(), cmdNum), dur);

			copyLocalStatusHistoryIntoPlanStatusHistory(planStatus);

			System.out.println( "--- Command " + cmd.getName() + " duration " + dur  + " COMPLETE");
			statusPublisher.publishSample(planStatus);
			cmdNum++;
		}
	}

	private void handleInterruption() throws InterruptedException {
		planStatus.currentStatus = AckStatus.ACK_REQUEUED;
		statusPublisher.publishSample();
		throw new InterruptedException();
	}
	
	private void runASegment(final DataPoint data) throws InterruptedException {
		planStatus.currentPoint = data.getRapidNumber();
		planStatus.currentCommand = -1;
		planStatus.currentStatus = AckStatus.ACK_EXECUTING;
		System.out.println( "Segment " + planStatus.currentPoint + " executing");

		statusPublisher.publishSample( planStatus );

		if(!choppyMovement){
			final float TOTAL_TIME = data.duration*2;//(data.duration/speedup) < 1 ? 1 : data.duration/speedup ; // in secs
			int totalWaypoints = 2;
			Vector3f startPoint = null;
			Vector3f endPoint = null;
			Quaternion startAngle = null;
			Quaternion endAngle = null;
			
			final Iterator<Waypoint> iterator = data.getWaypoints().iterator();
			int waypointCount = 0; 
			while(iterator.hasNext()){
				final Waypoint wp = iterator.next();
				
				if(waypointCount == 0){
					startPoint = new Vector3f(wp.pos[0],
											  wp.pos[1],
											  wp.pos[2]);
					startAngle = new Quaternion(wp.quat[0], 
											    wp.quat[1],
											    wp.quat[2], 
											    wp.quat[3]);
				}

				if(waypointCount == totalWaypoints){
					endPoint = new Vector3f(wp.pos[0],
											wp.pos[1],
											wp.pos[2]);
					
					endAngle = new Quaternion(wp.quat[0], 
											  wp.quat[1],
											  wp.quat[2], 
											  wp.quat[3]);
					
					//position hasn't changed so we must have rotated
					final Vector3f same = new Vector3f();
					Vector3f.sub(startPoint, endPoint, same);
					if(same.x == 0 && same.y == 0 && same.z == 0){
						if(endAngle.x != startAngle.x || endAngle.y != startAngle.y || endAngle.z != startAngle.z || endAngle.w != startAngle.w){
							for(float q = 1; q <= TOTAL_TIME; q=q+10){
								final Quaternion currentQuat = slerp(startAngle, endAngle, q/TOTAL_TIME);
								positionPublisher.publishSample(startPoint.x,startPoint.y,startPoint.z,currentQuat.x,currentQuat.y,currentQuat.z,currentQuat.w);
								try {
									Thread.sleep(500);
								} catch (final InterruptedException e) {
									handleInterruption();
								}
							}
							positionPublisher.publishSample(endPoint.x,endPoint.y,endPoint.z, endAngle.x,endAngle.y,endAngle.z,endAngle.w);
						}
					}
					//position has changed in 3 waypoints so we must have moved
					//we need to get the last position from the segment
					else if(totalWaypoints == 2 && iterator.hasNext()){
						totalWaypoints = 3;
						waypointCount++;
						continue;
						
					//lets move the robot since we finally have the last waypoint
					}else{
						for(float j = 1; j <= TOTAL_TIME; j++){
							final Vector3f curPoints = lerp(startPoint, endPoint, j/TOTAL_TIME);
							positionPublisher.publishSample(curPoints.x,curPoints.y,curPoints.z, endAngle.x,endAngle.y,endAngle.z,endAngle.w);
							try {
								Thread.sleep((long) (500/speedup));
							} catch (final InterruptedException e) {
								handleInterruption();
							}
						}
						positionPublisher.publishSample(endPoint.x,endPoint.y,endPoint.z, endAngle.x,endAngle.y,endAngle.z,endAngle.w);
						totalWaypoints = 3;	
					}
					waypointCount = 0;
					continue;
				}
				waypointCount++;	
			}
		}else{
			final List<Waypoint> waypoints = data.getWaypoints();
			for(final Waypoint  wp : waypoints){
				positionPublisher.publishSample(wp.pos[0],wp.pos[1],wp.pos[2],wp.quat[0],wp.quat[1],wp.quat[2],wp.quat[3]);
				try {
					Thread.sleep(sleeptime);
				} catch (final InterruptedException e) {
					handleInterruption();
				}
			}
			
		}
		
		

		putOkCommandStatusInLocalHistory(new PointCommand(data.getRapidNumber(), -1), data.getDuration()); 
		copyLocalStatusHistoryIntoPlanStatusHistory(planStatus);

		planStatus.currentStatus = AckStatus.ACK_COMPLETED;
		statusPublisher.publishSample(planStatus);
		currentPointCommand.point++;
		System.out.println( "Segment " + data.getOriginalName() + " COMPLETE" );
	}
	
	public Quaternion slerp(final Quaternion q1, final Quaternion q2, final float t) {
		
		final Quaternion newQuat = new Quaternion();
        // Create a local quaternion to store the interpolated quaternion
        if (q1.x == q2.x && q1.y == q2.y && q1.z == q2.z && q1.w == q2.w) {
            return q1;
        }

        float result = (q1.x * q2.x) + (q1.y * q2.y) + (q1.z * q2.z)
                + (q1.w * q2.w);

        if (result < 0.0f) {
            // Negate the second quaternion and the result of the dot product
            q2.x = -q2.x;
            q2.y = -q2.y;
            q2.z = -q2.z;
            q2.w = -q2.w;
            result = -result;
        }

        // Set the first and second scale for the interpolation
        float scale0 = 1 - t;
        float scale1 = t;

        // Check if the angle between the 2 quaternions was big enough to
        // warrant such calculations
        if ((1 - result) > 0.1f) {// Get the angle between the 2 quaternions,
            // and then store the sin() of that angle
            final float theta = (float) Math.acos(result);
            final float invSinTheta = (float) (1f / Math.sin(theta));

            // Calculate the scale for q1 and q2, according to the angle and
            // it's sine value
            scale0 = (float) (Math.sin((1 - t) * theta) * invSinTheta);
            scale1 = (float) (Math.sin((t * theta)) * invSinTheta);
        }

        // Calculate the x, y, z and w values for the quaternion by using a
        // special
        // form of linear interpolation for quaternions.
        newQuat.x = (scale0 * q1.x) + (scale1 * q2.x);
        newQuat.y = (scale0 * q1.y) + (scale1 * q2.y);
        newQuat.z = (scale0 * q1.z) + (scale1 * q2.z);
        newQuat.w = (scale0 * q1.w) + (scale1 * q2.w);

        // Return the interpolated quaternion
        return newQuat;
    }
	
	private Vector3f lerp (final Vector3f start, final Vector3f end, final float percent){
		Vector3f newPos = new Vector3f();
		final Vector3f finalPos = new Vector3f();
		Vector3f.sub(end, start, newPos);
		newPos = new Vector3f(newPos.x*percent,newPos.y*percent,newPos.z*percent);
		Vector3f.add(start, newPos, finalPos);
		return finalPos;
	}

	protected void initializePublishers() throws DdsEntityCreationException {
		positionPublisher = PositionPublisher.getInstance();

		statusPublisher.createWriters();
		statusPublisher.initializeDataTypes();

	}

	// ensure the statusHistory is no bigger than historyCapacity
	protected void copyLocalStatusHistoryIntoPlanStatusHistory(final PlanStatus ps) {
		int start = 0;
		int end = localStatusHistory.size();
		ps.statusHistory.userData.clear();

		if( localStatusHistory.size() > historyCapacity ) {
			start = localStatusHistory.size() - historyCapacity;
			end = localStatusHistory.size();
		}

		for(int i=start; i<end; i++) {
			ps.statusHistory.userData.add( localStatusHistory.elementAt(i) );
		}
	}

	private void putCanceledCommandStatusInLocalHistory(final PointCommand pc) {
		final Status cmdStatus = new Status();
		cmdStatus.point = pc.point;
		cmdStatus.command = pc.command;
		cmdStatus.duration = 0;
		cmdStatus.status = AckCompletedStatus.ACK_COMPLETED_CANCELED;
		localStatusHistory.add(cmdStatus);
	}

	private void putOkCommandStatusInLocalHistory(final PointCommand pc, final int duration) {
		final Status cmdStatus = new Status();
		cmdStatus.point = pc.point;
		cmdStatus.command = pc.command;
		cmdStatus.duration = duration;
		cmdStatus.status = AckCompletedStatus.ACK_COMPLETED_OK;
		localStatusHistory.add(cmdStatus);
	}

	private Status putNotCompletedCommandStatusInLocalHistory(final PointCommand pc) {
		final Status cmdStatus = new Status();
		cmdStatus.point = pc.point;
		cmdStatus.command = pc.command;
		cmdStatus.duration = -1;
		cmdStatus.status = AckCompletedStatus.ACK_COMPLETED_NOT;
		localStatusHistory.add(cmdStatus);
		return cmdStatus;
	}

	private class Waypoint {
		public final double t;
		public final float[] pos, vel;
		public final float[] quat;
		public final float[] ang_vel;

		public Waypoint(final List<Number> nums) {
			t = nums.get(0).doubleValue();
			pos = new float[]{nums.get(1).floatValue(), nums.get(2).floatValue(), nums.get(3).floatValue()};
			vel = new float[]{nums.get(4).floatValue(), nums.get(5).floatValue(), nums.get(6).floatValue()};
			quat = new float[]{nums.get(10).floatValue(), nums.get(11).floatValue(), nums.get(12).floatValue(), nums.get(13).floatValue()};
			ang_vel = new float[]{nums.get(14).floatValue(), nums.get(15).floatValue(), nums.get(16).floatValue()};
		}
	}

	private class DataPoint{
		private final List<Waypoint> waypoints;
		private int station;
		private int segment;
		private int duration = 23;
		private List<Sequenceable> subCommands;
		private String originalName;

		public DataPoint(final Segment seg) {
			waypoints = new ArrayList<Waypoint>();
			for(final List<Number> nums : seg.getWaypoints()) {
				waypoints.add( new Waypoint( nums ) );
			}
			parseName(seg.getName());
			duration = seg.getCalculatedDuration();
		}

		public List<Waypoint> getWaypoints() {
			return waypoints;
		}

		public DataPoint(final String station,final List<Sequenceable> subCommands){
			waypoints = new ArrayList<Waypoint>();
			this.subCommands = subCommands;
			parseName(station);
		}

		public int getDuration() {
			return duration;
		}

		public boolean isStation() {
			return (station >= 0);
		}

		public List<Sequenceable> getSubCommands() {
			return subCommands;
		}

		public int getRapidNumber() {
			if(isStation()) {
				return station*2;
			} else {
				return segment*2-1;
			}
		}

		public String getOriginalName() {
			return originalName;
		}

		private void parseName(final String name) {
			originalName = name;
			final String[] split =  name.split("-");
			if(split.length == 2) {
				station = -1;
				segment = Integer.parseInt(split[1]);
			} else {
				station = Integer.parseInt(name);
				segment = -1;
			}
		}
	}
}
