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
package gov.nasa.arc.irg.plan.bookmarks;

import gov.nasa.arc.irg.plan.model.Point6Dof;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPoint;

/** Stores a location and a name for easy plan editing */
public class StationBookmark {

	private String name;
	// can also be a ModuleBayPoint, since that is a subclass of Point6Dof
	private ModuleBayPoint location;

	public StationBookmark() {
		
	}
	
	public StationBookmark(String name, Point6Dof location) {
		this.name = name;
		setLocation(location);
	}

	public String getName() {
		return name;
	}
	
	public Point6Dof getLocation() {
		return location;
	}
	
	// meant only for json
	public void setName(String name) {
		this.name = name;
	}

	// meant only for json
	public void setLocation(Point6Dof location) {
		if(location instanceof ModuleBayPoint) {
			try {
				this.location = (ModuleBayPoint) location.clone();
			} catch (CloneNotSupportedException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		} else {
			this.location = new ModuleBayPoint(location);
		}
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if( !(obj instanceof StationBookmark)) {
			return false;
		}
		StationBookmark other = (StationBookmark) obj;
		
		if(!other.getName().equals(name)) {
			return false;
		}
		if(!other.getLocation().equals(location)) {
			return false;
		}
		return true;
	}
}
