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
package gov.nasa.arc.irg.plan.freeflyer.plan;

/**
 * Class to keep track of the id of a plan element in a way that corresponds to what 
 * FSW is using.  For Astrobee.
 * @author ddwheele
 *
 */
public class PointCommand {
	public int point;
	public int command;

	public PointCommand(int point, int command) {
		this.point = point;
		this.command  = command;
	}
	
	public PointCommand(PointCommand other) {
		if(other != null) {
			this.point = other.point;
			this.command = other.command;
		} else {
			this.point = 0;
			this.command = -1;
		}
	}

	public void set(int point, int command) {
		this.point = point;
		this.command  = command;
	}

	/** returns true if this PointCommand is strictly less than other*/
	public boolean isLessThan(PointCommand other) {
		if(point < other.point) {
			return true;
		}
		if(point > other.point) {
			return false;
		}

		// points are equal
		if(command < other.command) {		
			return true;
		}
		return false;
	}
	
	/** returns true if you need to skip something to get from other to this*/
	// same as greaterThan, except 2,-1 is not greaterThan 2,0
	public boolean requiresSkipFrom(PointCommand current) {
		if(point == current.point) {
			if(command == 0 && current.command == -1) {
				return false;
			}
		} 
		return isGreaterThan(current);
	}
	
	/** returns true if this PointCommand is strictly greater than other*/
	public boolean isGreaterThan(PointCommand other) {
		if(other == null) {
			return false;
		}
		if(point > other.point) {
			return true;
		}
		if(point < other.point) {
			return false;
		}

		// points are equal
		if(command > other.command) {		
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Point: " + point + ", Command: " + command;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + point;
		result = prime * result + command;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if(obj instanceof PointCommand) {
			PointCommand other = (PointCommand)obj;
			if(point == other.point && command == other.command) {
				return true;
			}
		}
		return false;
	}
	
	public boolean equals(int point, int command) {
		if(this.point == point && this.command == command) {
			return true;
		}
		return false;
	}
}