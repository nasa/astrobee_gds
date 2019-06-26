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
package gov.nasa.arc.irg.plan.model.modulebay;

import java.util.ArrayList;
import java.util.Arrays;



public class Bay implements Comparable<Bay> {
	
	// stored as absolute
	private Point3D center;
	
	private Point3D[] bounds;
	
	//FWD, AFT, STBD, PORT, OVHD, DECK, relative distances
	private double[] distances;
	
	private int bayNumber;
	
	public Bay(Point3D[] bounds, double[] distances, double[] offset, int bayNumber) {
		this.bounds = localToWorldBounds(bounds, offset);
		this.center = calculateCenter(this.bounds);
		this.distances = distances;
		this.bayNumber = bayNumber;
	}
	
	private Point3D calculateCenter(Point3D[] bounds) {
		double x = (bounds[0].getX() + bounds[1].getX()) / 2;
		double y = (bounds[0].getY() + bounds[1].getY()) / 2;
		double z = (bounds[0].getZ() + bounds[1].getZ()) / 2;
		return new Point3D(x, y, z);
	}
	
	private Point3D[] localToWorldBounds(Point3D[] bounds, double[] offset) {
		Point3D[] worldBounds = new Point3D[bounds.length];
		for(int i = 0; i < bounds.length; i ++) {
			worldBounds[i] = new Point3D(bounds[i].getX() + offset[0], bounds[i].getY() + offset[1], bounds[i].getZ() + offset[2]);
		}
		return worldBounds;
	}
	
	/** returns absolute xyz coordinates of center of bay */
	public Point3D getCenter() {
		return center;
	}
	
	/** returns shortest distance from center of bay to specified wall */
	public double distanceToWall(LocationMap.Wall wall) {
		return distances[wall.ordinal()];
	}
	
	public int getBayNumber() {
		return bayNumber;
	}
	
	public ArrayList<LocationMap.Wall> getValidWalls() {
		ArrayList<LocationMap.Wall> validList = new ArrayList<LocationMap.Wall>();
		
		for(int i = 0; i < distances.length; i++) {
			if(distances[i] != -1) {
				validList.add(LocationMap.Wall.values()[i]);
			}
		}
		
		return validList;
	}
	
	public Point3D[] getDividers() {
		return bounds;
	}
	
	public double[] getBounds() {
		double[] bounds = new double[6];
		for(int i = 0; i < bounds.length; i += 2) {
			if(distances[i] != -1 && distances[i + 1] != -1) {
				//if there is a distance, that is the bounds
				bounds[i] = center.toArray()[i / 2] + distances[i];
				bounds[i + 1] = center.toArray()[i / 2] - distances[i + 1];
			} else {
				//else the bounds are equal to the divider bounds
				double[] extents = new double[] {this.bounds[0].toArray()[i / 2], this.bounds[1].toArray()[i / 2]};
				Arrays.sort(extents);
				bounds[i] = extents[1];
				bounds[i + 1] = extents[0];
			}
		}
		return bounds;
	}
	
	public boolean isValid() {
		//Invalid by convention if all parameters are -1.
		return center.getX() != -1 || center.getY() != -1 || center.getZ() != -1;
	}

	@Override
	public int compareTo(Bay bay) {
		return Integer.compare(bayNumber, bay.getBayNumber());
	}
}
