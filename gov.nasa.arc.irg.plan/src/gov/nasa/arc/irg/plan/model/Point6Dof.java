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

import gov.nasa.arc.irg.plan.modulebay.ModuleBayPoint;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo(  
		use = JsonTypeInfo.Id.NAME,  
		include = JsonTypeInfo.As.PROPERTY,  
		property = "type")
@JsonSubTypes({  
	@Type(value = ModuleBayPoint.class, name = "ModuleBayPoint"),  
})
public class Point6Dof extends Geometry {
	private Logger logger = Logger.getLogger(Point6Dof.class);
	protected float x;
	protected float y;
	protected float z;
	protected float roll;
	protected float pitch;
	protected float yaw;
	
	public Point6Dof() {
		x = 0;
		y = 0;
		z = 0;
		setAnglesToZero();
	}
	
	public Point6Dof(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		setAnglesToZero();
	}
	
	public Point6Dof(float x, float y, float z, float roll, float pitch, float yaw) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.roll = roll;
		this.pitch = pitch;
		this.yaw = yaw;
	}
	
	public Point6Dof(Position pos) {
		x = pos.getCoordinates().get(0);
		y = pos.getCoordinates().get(1);
		z = pos.getCoordinates().get(2);
		if(pos.hasOrientation()) {
			roll = pos.getOrientation().get(0);
			pitch = pos.getOrientation().get(1);
			yaw = pos.getOrientation().get(2);
		} else {
			setAnglesToZero();
		}
	}
	
	protected void setAnglesToZero() {
		roll = 0;
		pitch = 0;
		yaw = 0;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		float oldx = this.x;
		this.x = x;
		firePropertyChange("x", oldx, x);
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		float oldy = this.y;
		this.y = y;
		firePropertyChange("y", oldy, y);
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		float oldz = this.z;
		this.z = z;
		firePropertyChange("z", oldz, z);
	}

	public float getRoll() {
		return roll;
	}

	public void setRoll(float roll) {
		float oldRoll = this.roll;
		this.roll = roll;
		firePropertyChange("roll", oldRoll, roll);
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		float oldPitch = this.pitch;
		this.pitch = pitch;
		firePropertyChange("pitch", oldPitch, pitch);
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		float oldYaw = this.yaw;
		this.yaw = yaw;
		firePropertyChange("yaw", oldYaw, yaw);
	}

	@Override
	protected List<Float> getCenter() {
		List<Float> ret = new ArrayList<Float>();
		ret.add(x);
		ret.add(y);
		ret.add(z);
		return ret;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		result = prime * result + Float.floatToIntBits(z);
		result = prime * result + Float.floatToIntBits(roll);
		result = prime * result + Float.floatToIntBits(pitch);
		result = prime * result + Float.floatToIntBits(yaw);
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if(!(obj instanceof Point6Dof)) {
			return false;
		}
		Point6Dof other = (Point6Dof) obj;
		if(x != other.getX()) {
			return false;
		}
		if(y != other.getY()) {
			return false;
		}
		if(z != other.getZ()) {
			return false;
		}
		if(roll != other.getRoll()) {
			return false;
		}
		if(pitch != other.getPitch()) {
			return false;
		}
		if(yaw != other.getYaw()) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer(getClass().getSimpleName());
		result.append(" ");
		result.append(x + ", " + y + ", " + z);
		result.append("; ");
		result.append(roll + ", " + pitch + ", " + yaw);
		
		return result.toString();
	}

	public String toShortString() {
		StringBuffer result = new StringBuffer(x + ", " + y + ", " + z);
		result.append("; ");
		result.append(roll + ", " + pitch + ", " + yaw);
		
		return result.toString();
	}
	
	@Override
	public Point6Dof clone() throws CloneNotSupportedException{
		Point6Dof newPoint =  (Point6Dof)super.clone();
		newPoint.setX(x);
		newPoint.setY(y);
		newPoint.setZ(z);
		newPoint.setRoll(roll);
		newPoint.setPitch(pitch);
		newPoint.setYaw(yaw);
		return newPoint;
	}
}
