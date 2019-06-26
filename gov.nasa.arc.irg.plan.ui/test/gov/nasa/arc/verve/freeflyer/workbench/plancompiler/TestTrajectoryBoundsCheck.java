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

import gov.nasa.arc.irg.plan.model.Point;
import gov.nasa.arc.irg.plan.model.Point6Dof;
import gov.nasa.arc.irg.plan.model.Position;
import gov.nasa.arc.irg.plan.ui.plancompiler.BoxMath;
import gov.nasa.arc.irg.plan.ui.plancompiler.FullState;
import gov.nasa.arc.irg.plan.ui.plancompiler.TrajectoryBoundsCheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

import org.junit.Test;

import com.ardor3d.math.Quaternion;

public class TestTrajectoryBoundsCheck extends TestCase {
	private java.util.List<BoxMath> keepins;
	private java.util.List<BoxMath> keepouts;
	private Vector<double[]> unpairedList;
	private final double DEG_TO_RAD = Math.PI / 180;

	@Override
	public void setUp() {
		keepins = new java.util.ArrayList<BoxMath>();
		keepouts = new java.util.ArrayList<BoxMath>();
		keepins.add(new BoxMath(new double[]{0, 0, 0},
				new double[]{2, 2, 10}));
		keepouts.add(new BoxMath(new double[]{0, 0, 0},
				new double[]{0.1, 0.1, 0.1}));
		keepouts.add(new BoxMath(new double[]{1.8, 1.8, 9},
				new double[]{2.0, 2.0, 10}));
		
		unpairedList = new Vector<double[]>();
		unpairedList.add(new double[]{1,2,3});
		unpairedList.add(new double[]{2,3,4});
		unpairedList.add(new double[]{3,4,5});
		unpairedList.add(new double[]{3,4,5});
		unpairedList.add(new double[]{4,5,6});
		unpairedList.add(new double[]{4,5,6});
	}
	
	@Test
	public void testRemoveInteriorPoints() {
		List<BoxMath> station = new ArrayList<BoxMath>();
		BoxMath model = new BoxMath(new double[] {-5, -5, -5}, new double[] {5, 5, 5});
		BoxMath interior = new BoxMath(new double[] {0, 0, 0}, new double[] {10, 10, 5});
		station.add(model);
		station.add(interior);
		
		TrajectoryBoundsCheck tbc = new TrajectoryBoundsCheck(station, null);
		
		Vector<double[]> points = new Vector<double[]>();
		points.add(new double[] {0.0, 0.0, 0.0});
		
		Vector<double[]> answer = tbc.removeInteriorPoints(points);
		
		assertTrue(answer.size() == 0);
	}
	
	@Test
	public void testAstrobeeSafe() {
		BoxMath model = new BoxMath(new double[] {-10, -10, -10}, new double[] {10, 10, 10});
		List<BoxMath> station = new ArrayList<BoxMath>();
		station.add(model);
		
		BoxMath keepout = new BoxMath(new double[] {-1, 0, -1}, new double[] {1, 1, 1});
		List<BoxMath> keepouts = new ArrayList<BoxMath>();
		keepouts.add(keepout);
		
		TrajectoryBoundsCheck tbc = new TrajectoryBoundsCheck(station, keepouts);
		
		//half of astrobee inside keepout
		assertFalse(tbc.isAstrobeeSafeRadians(0, 0, 0, 0, 0, 0));
		
		//corner of astrobee intersecting face of keepout
		assertFalse(tbc.isAstrobeeSafeRadians(0, -0.20, 0, 45 * DEG_TO_RAD, 0, 0));

		//corner of astrobee just outside keepout
		assertTrue(tbc.isAstrobeeSafeRadians(0, -0.306, 0, 45 * DEG_TO_RAD, 0, 0));
		
		//edge of keepout is intersecting astrobee, but no corners of astrobee inside of keepout
		assertFalse(tbc.isAstrobeeSafeRadians(0, -0.2, -1.1, 45 * DEG_TO_RAD, 0, 0));
		
		//astrobee just outside of the edge of the keepout
		assertTrue(tbc.isAstrobeeSafeRadians(0, -0.4, -1.1, 45 * DEG_TO_RAD, 0, 0));		
	}
	
	@Test
	public void testSegmentSafeBoxOffcenter() {
		BoxMath model = new BoxMath(new double[] {-10, -10, -10}, new double[] {10, 10, 10});
		List<BoxMath> station = new ArrayList<BoxMath>();
		station.add(model);
		
		BoxMath keepout = new BoxMath(new double[] {-1, 0, -1}, new double[] {1, 1, 1});
		List<BoxMath> keepouts = new ArrayList<BoxMath>();
		keepouts.add(keepout);
		
		TrajectoryBoundsCheck tbc = new TrajectoryBoundsCheck(station, keepouts);
		
		Position start;
		Position end;
				
		start = new Position(new Point(-2f, -0.1f, 0f));
		end = new Position(new Point(2f, -0.1f, 0f));
		assertFalse(tbc.isSegmentSafe(start, end));
	}
	
	@Test
	public void testIsSegmentSafe() {
		BoxMath mod1 = new BoxMath(new double[]{0,0,0}, new double[]{10,10,10});
		BoxMath mod2 = new BoxMath(new double[]{10,0,0}, new double[]{20,10,10});
		BoxMath mod3 = new BoxMath(new double[]{10,10,0}, new double[]{20,20,10});
		List<BoxMath> theStation = new ArrayList<BoxMath>();
		theStation.add(mod1);
		theStation.add(mod2);
		theStation.add(mod3);
		BoxMath keepout1 = new BoxMath(new double[]{0,0,0}, new double[]{5,5,5});
		List<BoxMath> nogo = new ArrayList<BoxMath>();
		nogo.add(keepout1);
		
		TrajectoryBoundsCheck checker = new TrajectoryBoundsCheck(theStation, nogo);
		
		// hits keepout zone
		assertFalse(checker.isSegmentSafe(new double[]{2,6,0}, new double[]{6,1,3}));
		
		// one box contains both ends
		assertTrue(checker.isSegmentSafe(new double[]{11,3,4}, new double[]{19,1,8}));
		
		// start outside all boxes
		assertFalse(checker.isSegmentSafe(new double[]{8,13,4}, new double[]{19,1,8}));
		
		// end outside all boxes
		assertFalse(checker.isSegmentSafe(new double[]{11,3,4}, new double[]{22,1,8}));
		
		// goes through two adjacent boxes
		assertTrue(checker.isSegmentSafe(new double[]{8,8,3}, new double[]{15,5,1}));
		
		// goes through three adjacent boxes
		assertTrue(checker.isSegmentSafe(new double[]{7,3,9}, new double[]{18,12,6}));
		
		// ends in boxes but misses the corner
		assertFalse(checker.isSegmentSafe(new double[]{1,9,2}, new double[]{11,18,7}));
	}
	
	@Test
	public void testAllPointsHavePartners() {
		Vector<double[]> pairedList = new Vector<double[]>();
		pairedList.add(new double[]{1,2,3});
		pairedList.add(new double[]{1,2,3});
		pairedList.add(new double[]{3,4,5});
		pairedList.add(new double[]{3,4,5});
		pairedList.add(new double[]{4,5,6});
		pairedList.add(new double[]{4,5,6});
		
		Vector<double[]> overpairedList = new Vector<double[]>();
		overpairedList.add(new double[]{3,4,5});
		overpairedList.add(new double[]{3,4,5});
		overpairedList.add(new double[]{3,4,5});
		
		TrajectoryBoundsCheck checker = new TrajectoryBoundsCheck(keepins, keepouts);
		
		assertTrue(checker.allPointsHavePartners(pairedList));
		assertFalse(checker.allPointsHavePartners(unpairedList));
		assertTrue(checker.allPointsHavePartners(overpairedList));
	}
	
	@Test
	public void testHasPartnerInList() {
		double[] testPositive = new double[]{4,5,6};
		double[] testNegative = new double[]{8,2,1};
		double[] testNegative2 = new double[]{1,2,3};
		
		TrajectoryBoundsCheck checker = new TrajectoryBoundsCheck(keepins, keepouts);
		
		assertTrue(checker.hasPartnerInList(testPositive, unpairedList));
		assertFalse(checker.hasPartnerInList(testNegative, unpairedList));
		assertFalse(checker.hasPartnerInList(testNegative2, unpairedList));
	}

	@Test
	public void testGoodTrajectory() {
		FullState[] fstates =
				FullState.createTrajectoryTrapezoid(new double[] {1, 1, 1},
						new double[] {1, 1, 8},
						1.0, 3.0,
						new Quaternion(0, 0, 0, 1),
						new Quaternion(0.707, 0, 0.707, 0),
						0.1, 0.01);
		TrajectoryBoundsCheck checker = new TrajectoryBoundsCheck(keepins, keepouts);
		assertTrue(checker.isTrajectorySafe(fstates));
	}

	@Test
	public void testOutsideTrajectory() {
		FullState[] fstates =
				FullState.createTrajectoryTrapezoid(new double[] {10, 1, 1},
						new double[] {10, 1, 8},
						1.0, 3.0,
						new Quaternion(0, 0, 0, 1),
						new Quaternion(0.707, 0, 0.707, 0),
						0.1, 0.01);
		TrajectoryBoundsCheck checker = new TrajectoryBoundsCheck(keepins, keepouts);
		assertFalse(checker.isTrajectorySafe(fstates));
	}

	@Test
	public void testViolatingTrajectory() {
		FullState[] fstates =
				FullState.createTrajectoryTrapezoid(new double[] {1, 1, 1},
						new double[] {1.9, 1.9, 9.5},
						1.0, 3.0,
						new Quaternion(0, 0, 0, 1),
						new Quaternion(0.707, 0, 0.707, 0),
						0.1, 0.01);
		TrajectoryBoundsCheck checker = new TrajectoryBoundsCheck(keepins, keepouts);
		assertFalse(checker.isTrajectorySafe(fstates));
	}

}
