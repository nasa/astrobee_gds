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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

/**
 * Does logical calculations about locations in the ISS Model.
 */
public class LocationMap {	
	private static LocationMap INSTANCE;
	private HashMap<ModuleName, Module> map;
	
	private LocationMap () {
		map = LocationGenerator.getInstance().getLocationMap();
	}
	
	public static LocationMap getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new LocationMap();
		}		
		return INSTANCE;
	}
	
	private HashMap<ModuleName, Module> getMap() {
		if(map == null) {
			INSTANCE = new LocationMap();
		}
		
		return map;
	}
	
	public enum Wall {
		FWD("Forward"), AFT("Aft"), STBD("Starboard"), PORT("Port"), DECK("Deck"), OVHD("Overhead");
		
		private final String name;
		
		Wall(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
		
		public static Wall fromString(String input) {
			if(input == null) {
				throw new IllegalArgumentException("Wall name cannot be null.");
			}
			for(Wall bn : Wall.values()) {
				if(input.equalsIgnoreCase(bn.name)) {
					return bn;
				}
			}
			throw new IllegalArgumentException("Not a valid Wall.");
		}
		
		@Override
		public String toString() {
			return getName();
		}
	}
	
	public Bay getBay(ModuleName module, int bayInteger) {	
		Bay bay = getMap().get(module).getBay(bayInteger);

		if(bay != null) {
			return bay;
		} else {
			throw new IllegalArgumentException("Bay not found, Module: " + module + ", Bay: " + bayInteger);
		}
	}
	
	public Point3D getModuleOffset(ModuleName module) {
		double[] offset = getMap().get(module).getModuleOffset();
		return new Point3D(offset[0], offset[1], offset[2]);
	}
	
	public int getMinBayNum(ModuleName module) {
		return getMap().get(module).getMinBayNum();
	}

	public boolean validateBayNumber(ModuleName moduleName, BayNumber bayNumber) {	
		Module module = getMap().get(moduleName);
		
		if(!module.containsBay(bayNumber.getBayIntegerOne())) {
			return false;
		}
		
		if(bayNumber.isSplit() && !module.containsBay(bayNumber.getBayIntegerTwo())) {
			return false;
		}
		
		if(bayNumber.getBayIntegerOne() < 0) {
			return false;
		}
		
		return true;
	}

	public int numBays(ModuleName module) {	
		ArrayList<Bay> bays = getMap().get(module).getBays();
		if(bays == null) {
			return 0;
		}
		return bays.size();
	}

	public boolean validateModuleWallAndDistance(ModuleName module, BayNumber bayNumber, Wall wall, double distance) {
		if(module == null || bayNumber == null || wall == null) {
			return false;
		}
		
		// double it because we are now allowing user to specify anywhere in the module without reselecting walls
		double baySize = 2*findGreatestDistanceToWall(module, bayNumber, wall);

		if(baySize < 0 || distance > baySize) {
			return false;
		}
		return true;
	}

	public boolean validateModuleWall(ModuleName module, Wall wall) {
		int minBayNum = getMinBayNum(module);
		if(minBayNum == -1) {
			return false;
		}
		
		Bay testBay = getBay(module, minBayNum);
		double check = testBay.distanceToWall(wall);

		if(check < 0) {
			return false;
		}
		return true;
	}

	/** returns distance from centerline to specified wall (if between bays with different radii, returns the larger) */
	public double findGreatestDistanceToWall(ModuleName module, BayNumber bayNumber, Wall wall) {
		if(!validateBayNumber(module, bayNumber)) {
			throw new IllegalArgumentException("Module "+module+" does not have bay "+bayNumber);
		}

		if(bayNumber.isSplit()) {
			double distOne = getBay(module, bayNumber.getBayIntegerOne()).distanceToWall(wall);
			double distTwo = getBay(module, bayNumber.getBayIntegerTwo()).distanceToWall(wall);
			return Math.max(distOne, distTwo);
		}
		Bay bay = getBay(module, bayNumber.getBayIntegerOne());
		return bay.distanceToWall(wall);
	}
	
	public ArrayList<Wall> getValidWalls(String moduleName) {
		ModuleName module = getModule(moduleName);
		//String[] result = new String[4];
		ArrayList<Wall> result = new ArrayList<Wall>();
		for(Wall wall : Wall.values()) {
			if(validateModuleWall(module, wall)) {
				result.add(wall);
			}
		}
		return result;
	}
	
	
	public boolean validateWallSelections(LocationMap.Wall one, LocationMap.Wall two) {		
		if(one == null || two == null) {
			return false;
		}
		switch(one) {
			case FWD:
				return !two.equals(LocationMap.Wall.AFT);
			case AFT:
				return !two.equals(LocationMap.Wall.FWD);
			case STBD:
				return !two.equals(LocationMap.Wall.PORT);
			case PORT:
				return !two.equals(LocationMap.Wall.STBD);
			case DECK:
				return !two.equals(LocationMap.Wall.OVHD);
			case OVHD:
				return !two.equals(LocationMap.Wall.DECK);
			default:
				throw new IllegalArgumentException("Invalid Wall specified.");
		}		
	}

	public ModuleName getModule(String module) {		
		for(ModuleName m : ModuleName.values()) {
			if(module.equals(m.getName())) {
				return m;
			}
		}
		throw new IllegalArgumentException();
	}

	public Wall getWall(String wall) {
		
		for(Wall w : Wall.values()) {
			if(wall.equals(w.getName())) {
				return w;
			}
		}
		return Wall.DECK;
	}
	
	public java.util.List<Bay> getAllBays(ModuleName module) {
		return map.get(module).getBays();
	}
	
	public double[][] getKeepinZones(ModuleName module) {
		return map.get(module).getKeepinZones();
	}
	
	public Point3D[] getDividers(ModuleName module) {
		return map.get(module).getBayDividers();
	}
	
	public String[] allModulesAsStringArray() {
		// ModuleName lists all possible modules, but IssConfig might not have them all.
		// Only map has correct list of modules to include
		return modulesToStringArray(map.keySet());
	}
	
	public String[] modulesToStringArray(Set<ModuleName> list) {
		String[] result = new String[list.size()];
		int index = 0;
		for(ModuleName mn : list) {
			result[index++] = mn.getName();
		}
		return result;
	}
	
	public String[] allWallsAsStringArray() {
		return wallsToStringArray(Arrays.asList(Wall.values()));
	}
	
	public String[] wallsToStringArray(java.util.List<Wall> list) {		
		String[] result = new String[list.size()];
		int index = 0;
		for(Wall w : list) {
			result[index++] = w.getName();
		}
		return result;
	}
	
}
