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
package gov.nasa.arc.verve.freeflyer.workbench.plancompiler;

import static org.junit.Assert.*;
import gov.nasa.arc.irg.plan.freeflyer.plan.PointCommand;

import org.junit.Test;

public class TestPointCommand {

	@Test
	public void testRequiresSkipFrom() {
		PointCommand is1minus1 = new PointCommand(1, -1);
		PointCommand is2minus1 = new PointCommand(2, -1);
		PointCommand is20 = new PointCommand(2, 0);

		assertTrue("1,-1 didn't skip to 2,0", is20.requiresSkipFrom(is1minus1));
		assertFalse("2,-1 skipped to 2,0", is20.requiresSkipFrom(is2minus1));
	}
	
	@Test
	public void testPointCommandLessThan() {
		PointCommand is0minus1 = new PointCommand(0, -1);
		PointCommand is1minus1 = new PointCommand(1, -1);
		PointCommand is2minus1 = new PointCommand(2, -1);
		PointCommand is20 = new PointCommand(2, 0);
		
		assertFalse("Point less than didn't work", is1minus1.isLessThan(is0minus1));
		assertTrue("Point greater than didn't work", is1minus1.isLessThan(is2minus1));
		
		assertTrue("Command greater than didn't work", is2minus1.isLessThan(is20));
		assertFalse("Command greater than didn't work", is20.isLessThan(is2minus1));
		
		assertFalse("Command greater than didn't work", is20.isLessThan(is20));
	}
	
	@Test
	public void testPointCommandGreaterThan() {
		PointCommand is0minus1 = new PointCommand(0, -1);
		PointCommand is1minus1 = new PointCommand(1, -1);
		PointCommand is2minus1 = new PointCommand(2, -1);
		PointCommand is20 = new PointCommand(2, 0);
		
		assertTrue("Point less than didn't work", is1minus1.isGreaterThan(is0minus1));
		assertFalse("Point greater than didn't work", is1minus1.isGreaterThan(is2minus1));
		
		assertFalse("Command greater than didn't work", is2minus1.isGreaterThan(is20));
		assertTrue("Command greater than didn't work", is20.isGreaterThan(is2minus1));
		
		assertFalse("Command greater than didn't work", is20.isGreaterThan(is20));
	}
}
