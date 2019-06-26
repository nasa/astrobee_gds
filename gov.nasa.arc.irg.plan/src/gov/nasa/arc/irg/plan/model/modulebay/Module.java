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

public class Module {
	public enum ModuleName {
		COLUMBUS("Columbus"), JEM("JPM"), NODE1("Node 1"), NODE2("Node 2"), NODE3("Node 3"), US_LAB("US Lab"), CUPOLA("Cupola");
		
		private final String name;
		
		ModuleName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
		
		public static ModuleName fromString(String input) {
			if(input == null) {
				throw new IllegalArgumentException("ModuleName name cannot be null");
			}
			for(ModuleName bn : ModuleName.values()) {
				if(input.equals(bn.name)) {
					return bn;
				}
			}
			throw new IllegalArgumentException("Not a valid ModuleName");
		}
	}
	
	private ModuleName name;
	private String model_file;
	private double[] module_offset;
	private double[][] module_radii;
	private Point3D[] bay_dividers;
	private ArrayList<Bay> bays;
	private double[][] keepins;
	
	public Module(ModuleName name, String file, double[] offsets, double[][] radii, Point3D[] dividers, double[][] keepins) {
		this.name = name;
		this.model_file = file;
		this.module_offset = offsets;
		this.module_radii = radii;
		this.bay_dividers = dividers;
		this.keepins = keepins;
		
		bays = new ArrayList<Bay>();
		for(int i = 0; i < bay_dividers.length - 1; i++) {
			if(validBay(module_radii[i])) {
				bays.add(new Bay(new Point3D[] {bay_dividers[i], bay_dividers[i + 1]}, module_radii[i], offsets, i));
			}
		}
	}
	
	public ModuleName getName() {
		return name;
	}
	
	public String getModelFile() {
		return model_file;
	}
	
	public double[] getModuleOffset() {
		return module_offset;
	}
	
	public double[][] getModuleRadii() {
		return module_radii;
	}
	
	public Point3D[] getBayDividers() {
		return bay_dividers;
	}
	
	public double[][] getKeepinZones() {
		return keepins;
	}
	
	public Bay getBay(int bayNum) {
		for(int i = 0; i < bays.size(); i++) {
			if(bays.get(i).getBayNumber() == bayNum) {
				return bays.get(i);
			}
		}
		
		return null;
	}
	
	public boolean containsBay (int bayNum) {
		for(int i = 0; i < bays.size(); i ++) {
			if(bays.get(i).getBayNumber() == bayNum) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<Bay> getBays() {
		return bays;
	}
	
	public int getMinBayNum() {
		if(bays.size() == 0) {
			return -1;
		}
		
		int min = bays.get(0).getBayNumber();
		for(int i = 1; i < bays.size(); i++) {
			if(bays.get(i).getBayNumber() < min) {
				min = bays.get(i).getBayNumber();
			}
		}
		
		return min;
	}
	
	private boolean validBay(double[] radii) {
		for(double r : radii) {
			if( r != -1) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return getName() + ", " + getBays().size() + " bays";
	}
	
}
