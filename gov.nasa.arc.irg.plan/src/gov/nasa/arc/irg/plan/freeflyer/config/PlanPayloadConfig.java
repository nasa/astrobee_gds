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
package gov.nasa.arc.irg.plan.freeflyer.config;


public class PlanPayloadConfig {
	private String name;
	private double power = 0;
	
	public PlanPayloadConfig(){
	}

	public void setName(String name) {
		this.name = name;
	}

	public PlanPayloadConfig(String name) {
		this.name = name;
	}
	
	public PlanPayloadConfig(String name, double power) {
		this.name = name;
		this.power = power;
	}

	public String getName() {
		return name;
	}
	
	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder( name );
		if(power > 0) {
			sb.append(", " + power + "W");
		}
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = (int) (prime * result + Double.doubleToLongBits(power));
		return result;
	}
	
	@Override
	public boolean equals(Object o) {
		double EPSILON = 0.0001;
		
		if(this == o) {
			return true;
		}
		if(!(o instanceof PlanPayloadConfig)) {
			return false;
		}
		PlanPayloadConfig other = (PlanPayloadConfig)o;
		
		if(!getName().equals(other.getName())) {
			return false;
		}
		if(Math.abs(getPower() - other.getPower()) > EPSILON) {
			return false;
		}
		return true;
	}
	
}