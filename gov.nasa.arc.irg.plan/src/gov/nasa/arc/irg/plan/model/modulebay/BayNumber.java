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

import gov.nasa.arc.irg.plan.json.BayNumberDeserializer;
import gov.nasa.arc.irg.plan.json.BayNumberSerializer;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(using = BayNumberSerializer.class)
@JsonDeserialize(using = BayNumberDeserializer.class)
public enum BayNumber {
	ZERO ("0", 0),
	BETWEEN_ZERO_ONE ("Between 0 and 1", 0, 1),
	ONE ("1", 1),
	BETWEEN_ONE_TWO ("Between 1 and 2", 1, 2),
	TWO ("2", 2),
	BETWEEN_TWO_THREE ("Between 2 and 3", 2, 3),
	THREE ("3", 3),
	BETWEEN_THREE_FOUR ("Between 3 and 4", 3, 4),
	FOUR ("4", 4),
	BETWEEN_FOUR_FIVE ("Between 4 and 5", 4, 5),
	FIVE ("5", 5),
	BETWEEN_FIVE_SIX ("Between 5 and 6", 5, 6),
	SIX ("6", 6),
	BETWEEN_SIX_SEVEN ("Between 6 and 7", 6, 7),
	SEVEN ("7", 7),
	BETWEEN_SEVEN_EIGHT ("Between 7 and 8", 7, 8),
	EIGHT ("8", 8);

	private final String descriptor;
	private final int bayIntegerOne;
	private final int bayIntegerTwo;
	/** Between two bays (ie "Between 7 and 8") */
	private final boolean split;
	
	BayNumber(String description, int bayInteger) {
		this.descriptor = description;
		this.bayIntegerOne = bayInteger;
		bayIntegerTwo = -1;
		split = false;
	}
	
	BayNumber(String description, int bayIntegerOne, int bayIntegerTwo) {
		this.descriptor = description;
		
		if(!validateIntegers(bayIntegerOne, bayIntegerTwo)) {
			throw new IllegalArgumentException("Invalid bay numbers.");
		}
		
		this.bayIntegerOne = bayIntegerOne;
		this.bayIntegerTwo = bayIntegerTwo;
		split = true;
	}

	public static BayNumber fromString(String input) {
		if(input == null) {
			throw new IllegalArgumentException("BayNumber name cannot be null");
		}
		for(BayNumber bn : BayNumber.values()) {
			if(input.equals(bn.descriptor)) {
				return bn;
			}
		}
		throw new IllegalArgumentException("Not a valid BayNumber");
	}
	
	public static BayNumber fromOrdinal(int ordinal) {
		if(ordinal >= BayNumber.values().length) {
			throw new IllegalArgumentException("Not a valid BayNumber ordinal.");
		}
		return BayNumber.values()[ordinal];
	}
	
	public String getDescriptor() {
		return descriptor;
	}
	
	/** How many bays must a module have for this BayNumber to be valid */
	public int getRequiredNumberOfBays() {
		if(split) {
			return bayIntegerTwo + 1;
		}
		return bayIntegerOne + 1;
	}

	public int getBayIntegerOne() {
		return bayIntegerOne;
	}
	
	public int getBayIntegerTwo() {
		return bayIntegerTwo;
	}
	
	public boolean isSplit() {
		return split;
	}
	
	/** a and b must be consecutive, and a < b */
	private boolean validateIntegers(int a, int b) {
		if( a < b ) {
			if( (a+1) == b ) {
				return true;
			}
		}
		return false;
	}
}
