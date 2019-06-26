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
package gov.nasa.arc.irg.plan.ui.plancompiler;

import gov.nasa.arc.irg.plan.freeflyer.command.CustomGuestScience;
import gov.nasa.arc.irg.plan.freeflyer.command.FreeFlyerCommand;
import gov.nasa.arc.irg.plan.freeflyer.command.PowerOffItem;
import gov.nasa.arc.irg.plan.freeflyer.command.PowerOnItem;
import gov.nasa.arc.irg.plan.freeflyer.command.StartGuestScience;
import gov.nasa.arc.irg.plan.freeflyer.command.StopGuestScience;
import gov.nasa.arc.irg.plan.freeflyer.command.Wait;
import gov.nasa.arc.irg.plan.freeflyer.config.OperatingLimitsConfigList.OperatingLimitsConfig;
import gov.nasa.arc.irg.plan.freeflyer.plan.FreeFlyerPlan;
import gov.nasa.arc.irg.plan.json.JsonBox;
import gov.nasa.arc.irg.plan.model.Position;
import gov.nasa.arc.irg.plan.model.Segment;
import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.model.Station;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayStation;
import gov.nasa.arc.irg.plan.ui.io.ConfigFileWrangler;
import gov.nasa.rapid.v2.framestore.ConvertUtils;
import gov.nasa.rapid.v2.framestore.EulerAngles;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;

public class PlanCompiler {
	private static Logger logger = Logger.getLogger(PlanCompiler.class);
	// Defaults copied from unit tests.  Check they are reasonable.
	static final double RAD2DEG = 180.0/Math.PI;
	static final double DEG2RAD = Math.PI/180.0;
	static final double DEFAULT_MAX_ACCEL = 0.0175; // m/s^2
	static final double DEFAULT_MAX_AVEL =   0.1745; //  rad/s
	static final double DEFAULT_MAX_AACCEL = 0.2; // rad/s^2
	static final double DEFAULT_MAX_VEL = 0.2; // 20 cm/s
	static final double EPSILON = 0.001;
	static boolean madeTrajectoryBoundsCheck = false;
	static List<BoxMath> keepins;
	static List<BoxMath> keepouts;
	static TrajectoryBoundsCheck boundsChecker;
	static String lastErrorMessage = "No last error message";
	// Start and end positions in a Segment
	static Position startPos;
	static Vector3 startVec;
	static Position endPos;
	static Vector3 endVec;
	static Quaternion startFacingEndQuat;
	static List<List<Number>> subpoints; // will be Waypoints in a Segment

	static double maxAccel;
	static double maxAVel;
	static double maxAAccel;
	static double max_vel; // each Segment sets this

	static Segment invalidSegment;

	private static int idlePowerDraw = 1; //1 watt/sec 
	private static float defaultSpeed = 0.2f;

	@Inject @Optional
	static private IEclipseContext context;

	public static double generatePower(FreeFlyerPlan plan){
		double totalPower = 0;
		Map<String,Double> constantPower = new HashMap<String,Double>();

		for(Sequenceable seq : plan.getSequence()) {
			//moving
			if(seq instanceof Segment) {
				Segment seg = (Segment)seq;
				totalPower += (idlePowerDraw * seg.getCalculatedDuration())*(seg.getSpeed()/defaultSpeed);
				Iterator<Double> constantInterator = constantPower.values().iterator();
				while(constantInterator.hasNext()){
					totalPower += constantInterator.next()*seg.getCalculatedDuration();
				}
				continue;
			} else if(seq instanceof Station){
				Station station = ((Station) seq);
				List<Sequenceable> justTheCommands = station.getJustTheCommands();

				Iterator<Sequenceable> iterator = justTheCommands.iterator();
				while(iterator.hasNext()){
					Sequenceable next = iterator.next();

					//waiting
					if(next instanceof Wait){
						totalPower += idlePowerDraw * next.getCalculatedDuration();
						Iterator<Double> constantInterator = constantPower.values().iterator();
						while(constantInterator.hasNext()){
							totalPower += constantInterator.next()*next.getCalculatedDuration();
						}
					}
					/*
					 * - If a Start Guest Science command has power and duration, assume it is blocking and just add the power times the duration to the total Watt Hours
					   - If a Guest Science command has power and duration, assume it is blocking and just add the power times the duration to the total Watt Hours
					   - If a Start Guest Science command has just power, assume it is non-blocking and that it consumes that amount of power until there is a corresponding Stop Guest Science command, or until the plan ends.
					   - If a Guest Science command has just power, assume it is non-blocking and that it consumes that amount of power until there is a Stop Guest Science command for that item, or until the plan ends.

						Stop Guest Science commands just signal the end of a non-blocking Start Guest Science or Guest Science power consumer.
					 */

					if(next instanceof PowerOnItem){
						PowerOnItem payload = (PowerOnItem)next;
						if(payload.getPlanPayloadConfig() == null){ continue; }
						if(payload.getPlanPayloadConfig().getPower() != 0 && payload.getCalculatedDuration() == 0){
							constantPower.put(payload.getPlanPayloadConfig().getName(),payload.getPlanPayloadConfig().getPower());
							continue;
						}
					}
					if(next instanceof PowerOffItem){
						PowerOffItem payloadOff = (PowerOffItem)next;
						if(payloadOff.getPlanPayloadConfig() == null){ continue; }
						if(constantPower.containsKey(payloadOff.getPlanPayloadConfig().getName()))
							constantPower.remove(payloadOff.getPlanPayloadConfig().getName());
						continue;
					}

					//Start guest science 
					if(next instanceof StartGuestScience){
						StartGuestScience science = (StartGuestScience) next;
						if(science.getGuestScienceApkGds() == null){ continue; }
						if(science.getPower() != 0 && science.getCalculatedDuration() != 0 ){
							totalPower += science.getPower() * science.getCalculatedDuration();
							Iterator<Double> constantInterator = constantPower.values().iterator();
							while(constantInterator.hasNext()){
								totalPower += constantInterator.next()*science.getCalculatedDuration();
							}
							continue;
						}
						if(science.getPower() != 0 && science.getCalculatedDuration() == 0){
							constantPower.put(science.getGuestScienceApkGds().getShortName(),science.getPower());
							continue;
						}
					}

					if(next instanceof CustomGuestScience){
						CustomGuestScience science = (CustomGuestScience) next;
						if(science.getGuestScienceApkGds() == null){ continue; }
						if(science.getPower() != 0 && science.getCalculatedDuration() != 0 ){
							totalPower += science.getPower() * science.getCalculatedDuration();
							Iterator<Double> constantInterator = constantPower.values().iterator();
							while(constantInterator.hasNext()){
								totalPower += constantInterator.next();
							}
							continue;
						}
						if( science.getPower() != 0 && science.getCalculatedDuration() == 0){
							constantPower.put(science.getGuestScienceApkGds().getShortName(),science.getPower());
							continue;
						}
					}

					if(next instanceof StopGuestScience){
						StopGuestScience science = (StopGuestScience)next;
						if(science.getGuestScienceApkGds() == null){ continue; }
						if(constantPower.containsKey(science.getGuestScienceApkGds().getShortName()))
							constantPower.remove(science.getGuestScienceApkGds().getShortName());
					}

					if(next instanceof FreeFlyerCommand){
						FreeFlyerCommand arm  = (FreeFlyerCommand)next;
						Iterator<Double> constantInterator = constantPower.values().iterator();
						while(constantInterator.hasNext()){
							totalPower += constantInterator.next()*arm.getCalculatedDuration();
						}

					}
				}
			}
			if(constantPower.isEmpty()){
				Iterator<Double> iterator = constantPower.values().iterator();
				while(iterator.hasNext()){
					totalPower += iterator.next();
				}
			}

		}
		return totalPower/3600;
	}


	public static boolean compilePlan(FreeFlyerPlan plan, OperatingLimitsConfig opLimits, boolean checkBounds) {
		if(checkBounds) {
			if(!madeTrajectoryBoundsCheck) {
				makeTrajectoryBoundsCheck();
			}
		}

		if(plan.getOperatingLimits() == null || plan.getInertiaConfiguration() == null) {
			lastErrorMessage = "Selection for Inertia File or Operating Limits missing";
			return false;
		}

		try {
			// loop through all the segments
			for(Sequenceable seq : plan.getSequence()) {
				if(seq instanceof Segment) {
					Segment seg = (Segment)seq;

					setOperatingLimits(opLimits, seg);

					try {
						if(checkBounds) {
							assertSegmentSafe(plan, seg);
						}
						if(seg.isFaceForward()) {
							compileFaceForwardSegmentToSubpoints(plan, seg);
							seg.setWaypoints(subpoints);
						} else {
							compileNotFaceForwardSegmentToSubpoints(plan, seg);
							seg.setWaypoints(subpoints);
						}
					} catch (PlanCompilerException e) {
						plan.setValid(false);
						lastErrorMessage = e.getMessage();
						return false;
					}
				}
			}
			invalidSegment = null;
			plan.updateTimes(0); // update all the start times
			plan.setValid(true);
		} catch (PlanCompilerException e1) {
			lastErrorMessage = e1.getMessage();
			return false;
		}
		return true;
	}

	public static boolean compilePlanNoBoundsCheck(FreeFlyerPlan plan, OperatingLimitsConfig opLimits) {
		return compilePlan(plan, opLimits, false);
	}

	public static boolean compilePlan(FreeFlyerPlan plan, OperatingLimitsConfig opLimits) {
		return compilePlan(plan, opLimits, true);
	}

	private static void compileNotFaceForwardSegmentToSubpoints(FreeFlyerPlan plan, Segment seg) throws PlanCompilerException {
		Position startPos = seg.getStartPosition();
		Position endPos = seg.getEndPosition();

		// get the FullState and put it in the Segment
		FullState[] fullstates = 
				FullState.createTrajectoryTrapezoid(startPos, endPos, max_vel, maxAccel, maxAVel, maxAAccel);

		subpoints = new ArrayList<List<Number>>();

		addFullStateArrayToSubpoints(fullstates);
	}

	private static void compileFaceForwardSegmentToSubpoints(FreeFlyerPlan plan, Segment seg) throws PlanCompilerException {
		// reinitialize the list of waypoints for this segment
		subpoints = new ArrayList<List<Number>>();

		setStartAndEndPositionsFromSegment(seg);

		// check if start and end are same point
		if(endVec.subtract(startVec, null).length() < EPSILON) {
			// if they are, just turn from one to the end and you're done
			computeSubpointsForStartToEndRotationNoTranslation();
			return;
		} else {
			// if they aren't, turn to end, move, turn back
			computeSubpointsForStartTurningToFaceEnd();
			computeSubpointsForStartFacingEndToEndFacingEnd();
		}

		boolean ignoreOrientation = ((ModuleBayStation)seg.getNext()).getCoordinate().isIgnoreOrientation();

		if(ignoreOrientation) {
			setNextStationStartRotationAutomatically((ModuleBayStation)seg.getNext());
		} else {
			computeSubpointsForEndFacingEndToEndWithRequiredRotation();
		}
	}
	
	private static void setNextStationStartRotationAutomatically(ModuleBayStation nextStation) {
		// set the start rotation of the next station to be "end-facing rotation"
		// don't use this because they use different EulerAngles convention
//		double[] facingEndPoint = startFacingEndQuat.toEulerAngles(null);
		
		Matrix3 m33 = new Matrix3();
		startFacingEndQuat.toRotationMatrix(m33);
		logger.debug(String.format("startFacingEndQuat = %.2f, %.2f, %.2f %.2f", 
				startFacingEndQuat.getX(), startFacingEndQuat.getY(), startFacingEndQuat.getZ(), startFacingEndQuat.getW()));
		logger.debug(String.format("matrix =\n\t\t\t\t\t\t\t\t\t\t\t[%.2f, %.2f, %.2f\n\t\t\t\t\t\t\t\t\t\t\t"
				+ "%.2f, %.2f, %.2f\n\t\t\t\t\t\t\t\t\t\t\t%.2f, %.2f, %.2f]", 
				m33.getValuef(0, 0), m33.getValuef(0, 1), m33.getValuef(0, 2),
				m33.getValuef(1, 0), m33.getValuef(1, 1), m33.getValuef(1, 2),
				m33.getValuef(2, 0), m33.getValuef(2, 1), m33.getValuef(2, 2)));
		EulerAngles ea = ConvertUtils.toEulerAnglesXYZr(m33, null);
		logger.debug(String.format("euler deg = %.2f, %.2f, %.2f", ea.getAngle1()*RAD2DEG, ea.getAngle2()*RAD2DEG, ea.getAngle3()*RAD2DEG));
		

		nextStation.getCoordinate().uncheckedSetRollRadians(ea.getAngle1());
		nextStation.getCoordinate().uncheckedSetPitchRadians(ea.getAngle2());
		nextStation.getCoordinate().uncheckedSetYawRadians(ea.getAngle3());
		
		logger.debug("Station " + nextStation.getName() + " points to " +
		  nextStation.getCoordinate().getRoll() + " " + nextStation.getCoordinate().getPitch() +
		  " " + nextStation.getCoordinate().getYaw());
		logger.debug("===================================");
	}

	private static Quaternion getStartFacingEndQuaternion(Vector3 startVec, Vector3 endVec) {
		Quaternion startFacingEndQuat;
		Vector3 startToEnd = endVec.subtract(startVec, null);
		// Find the yaw - tan (y/x)
		double yaw, pitch;
		if(Math.abs(startToEnd.getX()) < EPSILON) {
			if(Math.abs(startToEnd.getY()) < EPSILON) {
				if(Math.abs(startToEnd.getZ()) < EPSILON) {
					// we're not going anywhere, do not need to calculate
					startFacingEndQuat = new Quaternion(0,0,0,1);
				}
				else {
					// Y and X are 0 and Z is non-zero, so traveling vertically
					if( startToEnd.getZ() > 0 ) {
						pitch = Math.PI / 2.0; // positive Z is down
					} else {
						pitch = -Math.PI / 2.0; // negative Z is up
					}
				}
			}
			// facing straight port/starboard, tan will blow up
			if( startToEnd.getY() > 0) {
				// facing port
				yaw = Math.PI / 2.0;
			} else {
				// facing starboard
				yaw = - Math.PI / 2.0;
			}
		} else {
			// tan won't blow up
			yaw = Math.atan2( startToEnd.getY(), startToEnd.getX() ); 
		}
		// Find the pitch
		double horz_distance = Math.sqrt(startToEnd.getX()*startToEnd.getX() + startToEnd.getY()*startToEnd.getY());
		// horz_distance should not be < EPSILON b/c we checked X and Y separately above
		pitch = -Math.atan2( startToEnd.getZ(), horz_distance);
		// humans do not appreciate when we roll
		double roll = 0;
		
		gov.nasa.arc.irg.plan.ui.plancompiler.Quaternion q = new gov.nasa.arc.irg.plan.ui.plancompiler.Quaternion(roll, pitch, yaw);
		startFacingEndQuat = new Quaternion(q.vec[0], q.vec[1], q.vec[2], q.scalar);
		return startFacingEndQuat;
	}

	private static void computeSubpointsForStartTurningToFaceEnd() {
		FullState[] fullstates1 = FullState.createTrajectoryTrapezoid(
				startPos,
				startVec, startFacingEndQuat, 
				max_vel, maxAccel, maxAVel, maxAAccel);
		addFullStateArrayToSubpoints(fullstates1);
	}

	private static void computeSubpointsForStartFacingEndToEndFacingEnd() {
		FullState[] fullstates2 = FullState.createTrajectoryTrapezoid(
				startVec, startFacingEndQuat,
				endVec, startFacingEndQuat,
				max_vel, maxAccel, maxAVel, maxAAccel);
		addFullStateArrayToSubpoints(fullstates2);
	}

	private static void computeSubpointsForEndFacingEndToEndWithRequiredRotation() {
		FullState[] fullstates3 = FullState.createTrajectoryTrapezoid(
				endVec, startFacingEndQuat,
				endPos,
				max_vel, maxAccel, maxAVel, maxAAccel);
		addFullStateArrayToSubpoints(fullstates3);
	}

	private static void computeSubpointsForStartToEndRotationNoTranslation() {
		FullState[] fullstates3 = FullState.createTrajectoryTrapezoid(
				startPos,
				endPos,
				max_vel, maxAccel, maxAVel, maxAAccel);
		addFullStateArrayToSubpoints(fullstates3);
	}

	private static void addFullStateArrayToSubpoints(FullState[] fullstates) {
		double timeoffset = 0;
		if(!subpoints.isEmpty()) {
			List<Number> lastpoint = subpoints.get(subpoints.size()-1);
			timeoffset = (double) lastpoint.get(0);
			// Andrew's fudge factor so we don't have identical timestamps would go here
		}

		for(int i=0; i<fullstates.length; i++) {
			List<Number> pt = translateFullStateToNumberList(fullstates[i], timeoffset);
			subpoints.add(pt);
		}
	}

	private static void setStartAndEndPositionsFromSegment(Segment seg) {
		// actually need to get it from prior Station, because an ignoreOrientation
		// change might not have been propagated to the Segment
		ModuleBayStation prevStation = (ModuleBayStation) seg.getPrevious();
		startPos = prevStation.getEndPosition();
		startVec = positionToVector3Translation(startPos);

		endPos = seg.getEndPosition();
		endVec = positionToVector3Translation(endPos);

		// find the rotation that faces from start point to end point
		startFacingEndQuat = getStartFacingEndQuaternion(startVec, endVec);
	}

	/** take translation out of Position and put it into Vector3 */
	public static Vector3 positionToVector3Translation(Position pos) {
		Vector3 startVec = new Vector3();
		startVec.setX(pos.getCoordinates().get(0));
		startVec.setY(pos.getCoordinates().get(1));
		startVec.setZ(pos.getCoordinates().get(2));
		return startVec;
	}

	private static void assertSegmentSafe(FreeFlyerPlan plan, Segment seg) throws PlanCompilerException {
		Position startPos = seg.getStartPosition();
		Position endPos = seg.getEndPosition();

		if(!boundsChecker.isSegmentSafe(startPos, endPos)) {
			invalidSegment = seg;
			String msg = "Potential collision in Segment " + seg.getName() +
					". Please move Station "+seg.getPrevious().getName() + " or Station " + seg.getNext().getName() + ".";
			throw new PlanCompilerException(msg);
		}
	}

	/** set class variables for either plan or segment operating limits */
	private static void setOperatingLimits(OperatingLimitsConfig opLimits, Segment seg) throws PlanCompilerException {

		if(opLimits == null) {
			throw new PlanCompilerException("No operating limits set on Plan");
		}

		max_vel = opLimits.getTargetLinearVelocity();
		maxAccel = opLimits.getTargetLinearAccel();
		maxAVel = opLimits.getTargetAngularVelocity();
		maxAAccel = opLimits.getTargetAngularAccel();

		// use segment limits if set
		if(seg.isUseCustomSpeed()) {
			max_vel = seg.getSpeed();
		} else {
			seg.setSpeed((float) max_vel);
		}
		if(seg.getMaxAVel() > 0) {
			maxAVel = seg.getMaxAVel();
		}
		if(seg.getMaxAccel() > 0) {
			maxAccel = seg.getMaxAccel();
		}
		if(seg.getMaxAAccel() > 0) {
			maxAAccel = seg.getMaxAAccel();
		}

		if(!seg.isFaceForward()) {
			maxAccel /= 2.0;
			maxAAccel /= 2.0;
		}
	}

	public static TrajectoryBoundsCheck getTrajectoryBoundsCheck() {
		if(!madeTrajectoryBoundsCheck) {
			makeTrajectoryBoundsCheck();
		}
		return boundsChecker;
	}

	public static void makeTrajectoryBoundsCheck() {
		makeKeepins();
		makeKeepouts();

		boundsChecker = new TrajectoryBoundsCheck(keepins, keepouts);
		boundsChecker.setContext(context);
		madeTrajectoryBoundsCheck = true;
	}

	@Inject @Optional
	private static void updateContext(IEclipseContext context) {
		if(boundsChecker != null) {
			boundsChecker.setContext(context);
		}
	}

	private static void makeKeepins() {
		keepins = new ArrayList<BoxMath>();

		File[] keepinFiles = ConfigFileWrangler.getInstance().getKeepinFiles();

		for(int f=0; f<keepinFiles.length; f++) {

			JsonBox jsonBox = new JsonBox(keepinFiles[f]);
			List<double[]> seq = jsonBox.getBoxes();

			for(int i=0; i<seq.size(); i++) {
				double[] oneBox = seq.get(i);

				keepins.add(
						new BoxMath(new double[]{oneBox[0],oneBox[1],oneBox[2]},
								new double[]{oneBox[3],oneBox[4],oneBox[5]}));
			}
		}
	}

	private static void makeKeepouts() {
		keepouts = new ArrayList<BoxMath>();
		File[] keepoutFiles = ConfigFileWrangler.getInstance().getKeepoutFiles();

		for(int f=0; f<keepoutFiles.length; f++) {

			JsonBox jsonBox = new JsonBox(keepoutFiles[f]);
			List<double[]> seq = jsonBox.getBoxes();

			for(int i=0; i<seq.size(); i++) {
				double[] oneBox = seq.get(i);

				keepouts.add(
						new BoxMath(new double[]{oneBox[0],oneBox[1],oneBox[2]},
								new double[]{oneBox[3],oneBox[4],oneBox[5]}));
			}
		}
	}

	public static String getLastErrorMessage() {
		return lastErrorMessage;
	}

	public static Segment getInvalidSegment() {
		return invalidSegment;
	}

	public static List<Number> translateFullStateToNumberList(FullState fs, double timeOffset) {
		List<Number> list = new ArrayList<Number>();

		list.add(fs.t + timeOffset);
		for(int i=0; i<3; i++) {
			list.add(fs.pos[i]);
		}
		for(int i=0; i<3; i++) {
			list.add(fs.vel[i]);
		}
		for(int i=0; i<3; i++) {
			list.add(fs.accel[i]);
		}

		list.add(fs.orient.getX());
		list.add(fs.orient.getY());
		list.add(fs.orient.getZ());
		list.add(fs.orient.getW());

		for(int i=0; i<3; i++) {
			list.add(fs.ang_vel[i]);
		}
		for(int i=0; i<3; i++) {
			list.add(fs.ang_accel[i]);
		}

		return list;
	}
}
