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
package gov.nasa.arc.irg.plan.ui.modulebay;

import static org.junit.Assert.*;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayPoint;
import gov.nasa.arc.irg.plan.modulebay.ModuleBayStation;

import org.junit.Test;


// This class should be in the gov.nasa.arc.irg.plan plugin, but when it is there it doesn't run.
public class TestModuleBayStation {
	
	@Test
	public void testConstructModuleBayStation() {
		ModuleBayStation mbs = new ModuleBayStation();
		assertTrue("coordinate is not ModuleBayPoint", (mbs.getCoordinate() instanceof ModuleBayPoint));
	}
}
