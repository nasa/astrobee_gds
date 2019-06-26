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

import gov.nasa.arc.irg.plan.model.modulebay.Module.ModuleName;


public class CoordinatesGenerator {

	private CoordinatesGenerator() {
	}

	public static Point3D getCoordinates(ModuleName module) {
		int minBay = LocationMap.getInstance().getMinBayNum(module);
		int numBays = LocationMap.getInstance().numBays(module);
		if(numBays == 0) {
			return LocationMap.getInstance().getModuleOffset(module);
		} else {
			return LocationMap.getInstance().getBay(module, (numBays / 2) + minBay).getCenter();
		}
	}
	
	public static Point3D getCoordinates(ModuleName module, BayNumber bayNumber) {
		if(!LocationMap.getInstance().validateBayNumber(module, bayNumber)) {
			throw new IllegalArgumentException("Invalid bay number.");
		}
		if(bayNumber.isSplit()) {
			Point3D inBay1 = LocationMap.getInstance().getBay(module, bayNumber.getBayIntegerOne()).getCenter();
			Point3D inBay2 = LocationMap.getInstance().getBay(module, bayNumber.getBayIntegerTwo()).getCenter();
			return Point3D.getMidpoint(inBay1, inBay2);
		}
		return LocationMap.getInstance().getBay(module, bayNumber.getBayIntegerOne()).getCenter();
	}

	public static Point3D getCoordinates(ModuleName module, BayNumber bayNumber, LocationMap.Wall wall, double distanceFromWall) {
		if(!LocationMap.getInstance().validateBayNumber(module, bayNumber)) {
			throw new IllegalArgumentException("Invalid bay number.");
		}
		Point3D centered = getCoordinates( module, bayNumber );
		double distanceToWall = LocationMap.getInstance().findGreatestDistanceToWall(module, bayNumber, wall);
		return movePointFromWall(centered, wall, distanceFromWall, distanceToWall);		
	}
	
	public static Point3D getCoordinates(ModuleName module, BayNumber bayNumber, LocationMap.Wall wallOne, double distanceOne, LocationMap.Wall wallTwo, double distanceTwo) {
		if(!LocationMap.getInstance().validateBayNumber(module, bayNumber)) {
			throw new IllegalArgumentException("Invalid bay number.");
		}
		if(LocationMap.getInstance().validateWallSelections(wallOne, wallTwo)) {
			Point3D centered = getCoordinates( module, bayNumber );
			double distanceToWallOne = LocationMap.getInstance().findGreatestDistanceToWall(module, bayNumber, wallOne);
			Point3D temp = movePointFromWall(centered, wallOne, distanceOne, distanceToWallOne);
			
			double distanceToWallTwo = LocationMap.getInstance().findGreatestDistanceToWall(module, bayNumber, wallTwo);
			return movePointFromWall(temp, wallTwo, distanceTwo, distanceToWallTwo);		
		} else {
			throw new IllegalArgumentException("Invalid wall selection, walls on same axis.");
		}
	}
	
	/**
	 * Keeping two of three coordinates of point, move the third coordinate to given distance from given wall.
	 */
	public static Point3D movePointFromWall(Point3D point, LocationMap.Wall wall, double fromWall, double toWall) {
		if (fromWall < 0 ) {
			throw new IllegalArgumentException("Cannot move to negative offset");
		}

		switch (wall) {
		case PORT:
			return new Point3D(point.getX(), 
					point.getY() - (toWall - fromWall), 
					point.getZ());
		case STBD:
			return new Point3D(point.getX(), 
					point.getY() + (toWall - fromWall), 
					point.getZ());
		case AFT:
			return new Point3D(point.getX() - (toWall - fromWall),
					point.getY(), 
					point.getZ());
		case FWD:
			return new Point3D(point.getX() + (toWall - fromWall),
					point.getY(), 
					point.getZ());
		case OVHD:
			return new Point3D(point.getX(), 
					point.getY(), 
					point.getZ() - (toWall - fromWall));
		case DECK:
			return new Point3D(point.getX(), 
					point.getY(), 
					point.getZ() + (toWall - fromWall));
		default:
			throw new IllegalArgumentException("Invalid wall specified.");
		}
	}
}
