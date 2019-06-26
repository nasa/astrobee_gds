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
package gov.nasa.arc.irg.plan.model;

import gov.nasa.arc.irg.plan.util.NumberUtil;

import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo(  
		use = JsonTypeInfo.Id.NAME,  
		include = JsonTypeInfo.As.PROPERTY,  
		property = "type")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Segment extends SequenceHolder implements Sequenceable {
	private static final Logger logger = Logger.getLogger(Segment.class);
	protected float tolerance;
	protected float speed = 0.2f; // 20 cm/s in m/s
	protected boolean useCustomSpeed = false; // when true, use the speed in this segment instead of from OperatingLimits
	protected float maxAVel = -1f; // if below zero, PlanCompiler will use value from Plan
	protected float maxAccel = -1f; // if below zero, PlanCompiler will use value from Plan
	protected float maxAAccel = -1f; // if below zero, PlanCompiler will use value from Plan
	protected Position startPosition;
	protected Position endPosition;
	protected boolean stopAtEnd = true;
	protected int delayTime = 0;
	protected boolean faceForward = true;
	protected List<List<Number>> waypoints = null;
	protected String waypointType = "PoseVelAccel";
	protected SequenceHolder parent;

	protected Sequenceable next;
	protected Sequenceable previous;
	
	public Segment() {
		super();
	}
	
	@JsonIgnore
	public static String getCustomName() {
		return "Segment";
	}

	@Override
	public int getRapidNumber() {
		String parts[] = getName().split("-");
		int raw = Integer.valueOf(parts[0]);
		return raw * 2 + 1;
	}
	
	public List<List<Number>> getWaypoints() {
		return waypoints;
	}

	public void setWaypoints(List<List<Number>> subpoints) {
		this.waypoints = subpoints;
	}
	
	public String getWaypointType() {
		return waypointType;
	}

	public void setWaypointType(String waypointType) {
		this.waypointType = waypointType;
	}

	public float getTolerance() {
		return tolerance;
	}

	public void setTolerance(float tolerance) {
		float oldTolerance = this.tolerance;
		float newTolerance = tolerance;
		this.tolerance = tolerance;
		if (oldTolerance != tolerance){
			firePropertyChange("tolerance", oldTolerance, newTolerance);
		}
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		if(speed < 0.0001) {
			logger.error("Cannot set speed to 0.");
			return;
		}
		float oldSpeed = this.speed;
		this.speed = speed;
		if (oldSpeed != speed){
			firePropertyChange("speed", oldSpeed, speed);
		}
	}
	

	public boolean isUseCustomSpeed() {
		return useCustomSpeed;
	}

	public void setUseCustomSpeed(boolean useCustomSpeed) {
		boolean oldUseCustomSpeed = this.useCustomSpeed;
		this.useCustomSpeed = useCustomSpeed;
		if(oldUseCustomSpeed != useCustomSpeed) {
			firePropertyChange("useCustomSpeed", oldUseCustomSpeed, useCustomSpeed);
		}
	}

	public float getMaxAVel() {
		return maxAVel;
	}

	public void setMaxAVel(float maxAVel) {
		float oldmaxAVel = this.maxAVel;
		this.maxAVel = maxAVel;
		if (oldmaxAVel != maxAVel){
			firePropertyChange("maxAVel", oldmaxAVel, maxAVel);
		}
	}
	
	public float getMaxAccel() {
		return maxAccel;
	}

	public void setMaxAccel(float maxAccel) {
		float old = this.maxAccel;
		this.maxAccel = maxAccel;
		if (old != maxAccel){
			firePropertyChange("maxAVel", old, maxAccel);
		}
	}

	public float getMaxAAccel() {
		return maxAAccel;
	}

	public void setMaxAAccel(float maxAAccel) {
		float old = this.maxAAccel;
		this.maxAAccel = maxAAccel;
		if (old != maxAAccel){
			firePropertyChange("maxAVel", old, maxAAccel);
		}
	}

	/**
	 * @return the stopAtEnd
	 */
	public boolean isStopAtEnd() {
		return stopAtEnd;
	}

	/**
	 * @param stopAtEnd the stopAtEnd to set
	 */
	public void setStopAtEnd(boolean stopAtEnd) {
		boolean oldStopAtEnd = this.stopAtEnd;
		boolean newStopAtEnd = stopAtEnd;
		this.stopAtEnd = stopAtEnd;
		if (oldStopAtEnd != stopAtEnd){
			firePropertyChange("stopAtEnd", oldStopAtEnd, newStopAtEnd);
		}
	}

	/**
	 * @return the parent
	 */
	@JsonIgnore
	public SequenceHolder getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	@JsonIgnore
	public void setParent(SequenceHolder parent) {
		this.parent = parent;
	}

	protected float calculateDistance() {

		if (getStartPosition() == null || getEndPosition() == null) {
			return 0;
		}

		int len = getStartPosition().getCoordinates().size();
		if (getEndPosition().getCoordinates().size() != len){
			return 0;
		}
		float[] diffs = new float[len];
		float sum = 0;

		for (int i = 0; i < len; i++){
			diffs[i] = getEndPosition().getCoordinates().get(i) - getStartPosition().getCoordinates().get(i);
			sum += diffs[i] * diffs[i];
		}

		return (float)Math.sqrt(sum);
	}

	@Override
	@JsonIgnore
	public int getCalculatedDuration() {

		if(waypoints != null) {
			List<Number> things = waypoints.get(waypoints.size()-1);
			Number timenumber = things.get(0);
			double time = timenumber.doubleValue();
			return (int)time;
		}
		
		int result = 0;

		//TODO handle blocking vs nonblocking sequenceable
		// By our contract, Segments should never have a sequence
		for (Sequenceable s : getSequence()){
			result += s.getCalculatedDuration();
		}

		float distance = calculateDistance(); // distance is in meters
		if (distance > 0){
			// speed is in meters per second
			float floatduration = distance / getSpeed();

			return (int)floatduration;
		}
		return result;
	}

	@JsonIgnore
	public Position getStartPosition() {
		if(startPosition == null) {
			startPosition = new Position();
		}
		return startPosition;
	}

	@JsonIgnore
	public void setStartPosition(Position startPosition) {
		Position oldPos = this.startPosition;
		if (oldPos == null || !oldPos.equals(startPosition)){
			this.startPosition = startPosition;
		}
	}

	@JsonIgnore
	public Position getEndPosition() {
		if(endPosition == null) {
			endPosition = new Position();
		}
		return endPosition;
	}

	@JsonIgnore
	public void setEndPosition(Position endPosition) {
		if (this.endPosition == null || !this.endPosition.equals(endPosition)){
			this.endPosition = endPosition;
		}
	}

	@Override
	public void inheritDefaults() {
		SequenceHolder parent = getParent();
		Plan plan = parent.getPlan();
		if (plan != null){
			setSpeed(plan.getDefaultSpeed());
			setTolerance(plan.getDefaultTolerance());
		}
	}

	@Override
	public Segment clone() throws CloneNotSupportedException {
		Segment newSegment =  (Segment)super.clone();
		newSegment.setTolerance(getTolerance());
		newSegment.setSpeed(getSpeed());
		newSegment.setStartPosition(getStartPosition().clone());
		newSegment.setEndPosition(getEndPosition().clone());
		// do not clone the parent
		return newSegment;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder(toShortString());
		result.append(" ");

		if (getClass().getSimpleName() != null && !(getClass().getSimpleName().isEmpty())){
			result.append(getClass().getSimpleName());
		}

		result.append(" (");
		result.append(calculateDistance());
		result.append(")");

		return result.toString();
	}

	@Override
	public int getEndTime() {
		return getStartTime() + getCalculatedDuration();
	}

	@Override
	public String toShortString() {
		StringBuffer result = new StringBuffer("Traverse");

		if (getName() != null && !(getName().isEmpty())){
			result.append(" ");
			result.append(getName());
		} else if (getClass().getSimpleName() != null && !(getClass().getSimpleName().isEmpty())){
			result.append(" ");
			result.append(getClass().getSimpleName());
		}

		return result.toString();
	}


	@Override
	public void autoName(int index) {
		String nextNum = String.valueOf((index+1)/2);
		String prevNum = String.valueOf((index-1)/2);
		setName(prevNum + "-" + nextNum);

		autoNameChildren();
	}

	/*
	 * Return a label to be used for this element such as in Run Plan View 
	 */
	@JsonIgnore
	public String getLabel() {
		String stationId = getClass().getSimpleName();
		String suffix = stationId;
		try {
			suffix = stationId.substring(stationId.indexOf("SEG")+3); //get the station numbers
		} catch (Exception ex) {
			// pass
		}
		return this.getClass().getSimpleName() + " " +suffix;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((m_name == null) ? 0 : m_name.hashCode());
		result = prime * result + ((m_notes == null) ? 0 : m_notes.hashCode());

		result = prime * result + Float.floatToIntBits(tolerance);
		result = prime * result + Float.floatToIntBits(speed);

		result = prime * result + (stopAtEnd ? 1:0);
		result = prime * result + (faceForward ? 1:0);
		result = prime * result + (useCustomSpeed ? 1:0);

		result = prime * result + ((startPosition == null) ? 0 : startPosition.hashCode());
		result = prime * result + ((endPosition == null) ? 0 : endPosition.hashCode());

		result = prime * result + ((waypointType == null) ? 0 : waypointType.hashCode());
		result = prime * result + ((waypoints == null) ? 0 : waypoints.hashCode());

		result = prime * result + delayTime;

		return result;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(o == null) {
			return false;
		}
		if(!super.equals(o)) {
			return false;
		}
		if(!(o instanceof Segment)) {
			return false;
		}
		Segment other = (Segment)o;
		if(!m_name.equals(other.getName())) {
			return false;
		}
		if(tolerance != other.getTolerance()) {
			return false;
		}
		if(speed != other.getSpeed()) {
			return false;
		}
		
		if(faceForward != other.isFaceForward()) {
			return false;
		}
		
		if(useCustomSpeed != other.isUseCustomSpeed()) {
			return false;
		}
		
		// XXX Checking just the xyz, not orientation
		// XXX TOTAL HACK UNTIL WE GET FACE-FORWARD IMPLEMENTED
		List<Float> me = startPosition.getCoordinates();
		List<Float> him = other.startPosition.getCoordinates();
		for(int i=0; i<me.size(); i++) {
			if(!NumberUtil.equals(me.get(i), him.get(i))) {
				return false;
			}
		}
		me = endPosition.getCoordinates();
		him = other.endPosition.getCoordinates();
		for(int i=0; i<me.size(); i++) {
			if(!NumberUtil.equals(me.get(i), him.get(i))) {
				return false;
			}
		}
		
		if(stopAtEnd != other.isStopAtEnd()) {
			return false;
		}
		if (waypointType == null) {
			if (other.getWaypointType() != null) {
				return false;
			}
		} else if (!waypointType.equals(other.getWaypointType())) {
			return false;
		}
		if (waypoints == null) {
			if (other.getWaypoints() != null) {
				return false;
			}
		} else if (!waypoints.equals(other.getWaypoints())) {
			return false;
		}
		if(delayTime != other.delayTime) {
			return false;
		}
		return true;
	}

	@JsonIgnore
	public void setPrevious(Sequenceable s) {
		previous = s;
		if (s != null){
			setStartPosition(s.getEndPosition());

			// this is probably a hint that you need to change your name
			String oldName = m_name;

			int newNumber = Integer.valueOf(s.getName());
			int newNextNumber = newNumber + 1;
			String newName = newNumber + "-" + newNextNumber;

			if(!newName.equals(oldName)) {
				m_name = newName;
			}
		}
	}

	@JsonIgnore
	public void setNext(Sequenceable s) {
		next = s;
		if (s != null){
			setEndPosition(s.getStartPosition());
		}
	}

	@JsonIgnore
	public Sequenceable getNext() {
		return next;
	}

	@JsonIgnore
	public Sequenceable getPrevious() {
		return previous;
	}

	public boolean isFaceForward() {
		return faceForward;
	}

	public void setFaceForward(boolean faceForward) {
		boolean oldvalue = this.faceForward;
		this.faceForward = faceForward;
		if (oldvalue != this.faceForward){
			firePropertyChange("faceForward", oldvalue, this.faceForward);
		}
	}

	public void refresh(Sequenceable seq) {
		// seq is either previous or next
		if(seq.equals(getPrevious())) {
			setStartPosition(seq.getEndPosition());
		} else if (seq.equals(getNext())) {
			// we need to take the start xyz of Next, but not the rpy
			setEndPosition(seq.getStartPosition());
			// actually, if next is not defaultOrient, we DO need the rpy
		}
	}
}
