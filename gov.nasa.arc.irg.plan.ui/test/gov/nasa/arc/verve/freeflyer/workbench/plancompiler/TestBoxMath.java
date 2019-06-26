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

import java.util.Vector;

import gov.nasa.arc.irg.plan.ui.plancompiler.BoxMath;
import gov.nasa.arc.irg.plan.ui.plancompiler.BoxMath.BoxWall;
import gov.nasa.arc.irg.plan.ui.plancompiler.BoxMath.Contains;
import junit.framework.TestCase;

import org.junit.Test;

public class TestBoxMath extends TestCase {
	final double EPSILON = 1e-3;
	private BoxMath box;

	@Override
	public void setUp() {
		box = new BoxMath(new double[]{0, 0, 0},
				new double[]{2, 2, 2});
	}
	
	@Test
	public void testSegmentIntersectsKeepoutZone() {
		// All on one side
		boolean shouldBeFalse = box.segmentIntersectsKeepoutZone(new double[]{-1, 1, 1}, new double[]{-2, 1, 1});
		assertFalse(shouldBeFalse);

		// at least one end inside box
		boolean shouldBeTrue = box.segmentIntersectsKeepoutZone(new double[]{1, 1, 1}, new double[]{-2, 1, 1});
		assertTrue(shouldBeTrue);
		shouldBeTrue = box.segmentIntersectsKeepoutZone(new double[]{1.5, 1.3, 1}, new double[]{0.75, 1, 0.8});
		assertTrue(shouldBeTrue);
		shouldBeTrue = box.segmentIntersectsKeepoutZone(new double[]{-1, 1, 1}, new double[]{1, 1, 1});
		assertTrue(shouldBeTrue);

		// clips the edge - hit max faces
		shouldBeTrue = box.segmentIntersectsKeepoutZone(new double[]{3, 0, 1}, new double[]{0, 3, 1});
		assertTrue(shouldBeTrue);

		// clips the edge - hit min faces
		shouldBeTrue = box.segmentIntersectsKeepoutZone(new double[]{2, -1, 1}, new double[]{-1, 2, 1});
		assertTrue(shouldBeTrue);

		// miss
		shouldBeFalse = box.segmentIntersectsKeepoutZone(new double[]{1, 0, 5}, new double[]{1, 5, 0});
		assertFalse(shouldBeFalse);
	}

	@Test
	public void testFindSegmentKeepinWallIntersection() {
		// All on one side
		Vector<double[]> shouldBeNull = box.findSegmentKeepinWallIntersection(new double[]{1, 1, 8}, new double[]{1, 1, 9});
		assertNull(shouldBeNull);

		// both inside box
		shouldBeNull = box.findSegmentKeepinWallIntersection(new double[]{0.1, 1.4, 1.8}, new double[]{1.2, 0.3, 1.7});
		assertNull(shouldBeNull);

		// one in, one out
		double[] inside = new double[]{1, 1, 1};
		double[] intersectsMaxY = new double[]{2, 3, 1};
		Vector<double[]> shouldBe1521Vector = box.findSegmentKeepinWallIntersection(inside, intersectsMaxY);
		assertEquals(1, shouldBe1521Vector.size());
		double[] shouldBe1521 = shouldBe1521Vector.get(0);
		assertTrue(box.vectorsAreEqual(shouldBe1521, new double[]{1.5, 2, 1}));

		double[] intersectsMinX = new double[]{-1,1,1};
		Vector<double[]> shouldBe011Vector = box.findSegmentKeepinWallIntersection(intersectsMinX, inside);
		assertEquals(1, shouldBe011Vector.size());
		double[] shouldBe011 = shouldBe011Vector.get(0);
		assertTrue(box.vectorsAreEqual(shouldBe011, new double[]{0, 1, 1}));

		double[] intersectsCorner = new double[]{3, 3, 3};
		Vector<double[]> shouldBe222Vector = box.findSegmentKeepinWallIntersection(inside, intersectsCorner);
		assertEquals(1, shouldBe222Vector.size());
		double[] shouldBe222 = shouldBe222Vector.get(0);
		assertTrue(box.vectorsAreEqual(shouldBe222, new double[]{2, 2, 2}));

		// two out, two hits
		Vector<double[]> shouldBeCenterZVector = box.findSegmentKeepinWallIntersection(new double[]{1, 1, 3}, new double[]{1, 1, -1});
		assertNotNull(shouldBeCenterZVector);
		assertEquals(2, shouldBeCenterZVector.size());
		double[] centerZ1 = shouldBeCenterZVector.get(0);
		double[] centerZ2 = shouldBeCenterZVector.get(1);
		double[] centerTopFace = new double[]{1,1,2};
		double[] centerBottomFace = new double[]{1,1,0};
		assertTrue(firstTwoEqualSecondTwo(centerZ1, centerZ2, centerTopFace, centerBottomFace));
		
		Vector<double[]> shouldBeDiagonalVector = box.findSegmentKeepinWallIntersection(new double[]{0, 4, 1}, new double[]{3, -2, 1});
		assertNotNull(shouldBeDiagonalVector);
		assertEquals(2, shouldBeDiagonalVector.size());
		double[] r1 = shouldBeDiagonalVector.get(0);
		double[] r2 = shouldBeDiagonalVector.get(1);
		double[] i1 = new double[]{1,2,1};
		double[] i2 = new double[]{2,0,1};
		assertTrue(firstTwoEqualSecondTwo(r1, r2, i1, i2));
		
		// two out, no hits
		shouldBeNull = box.findSegmentKeepinWallIntersection(new double[]{0, 4, 2}, new double[]{5, 0, 0});
		assertNull(shouldBeNull);
		
	}

	public boolean firstTwoEqualSecondTwo(double[] a1, double[] a2, double[] b1, double[] b2) {
		if(box.vectorsAreEqual(a1, b1)) {
			if(!box.vectorsAreEqual(a2, b2)) {
				return false;
			}
		} else if(box.vectorsAreEqual(a2, b1)) {
			if(!box.vectorsAreEqual(a1, b2)) {
				return false;
			}

		}
		return true;
	}

	@Test
	public void testEliminateDuplicates() {
		double[] vec = new double[]{37, 17, 57};
		double[] sameVec = new double[]{37, 17, 57};
		double[] sameVecAgain = new double[]{37, 17, 57};

		Vector<double[]> list = new Vector<double[]>();
		list.add(vec);
		list.add(sameVec);
		list.add(sameVecAgain);

		assertEquals(3, list.size());

		Vector<double[]> trimmedList = box.eliminateDuplicates(list);
		assertEquals(1, trimmedList.size());
		double[] pt = trimmedList.get(0);
		assertEquals(37, pt[0], EPSILON);
		assertEquals(17, pt[1], EPSILON);
		assertEquals(57, pt[2], EPSILON);
	}

	@Test
	public void testVectorsAreEqual() {
		double[] vec = new double[]{37, 17, 57};
		double[] sameVec = new double[]{37, 17, 57};
		double[] otherVec = new double[]{44, 29, 30};

		boolean shouldBeTrue = box.vectorsAreEqual(vec, sameVec);
		assertTrue(shouldBeTrue);

		boolean shouldBeFalse = box.vectorsAreEqual(vec, otherVec);
		assertFalse(shouldBeFalse);
	}

	@Test
	public void testFindSegmentFaceIntersection() {
		double[] inside = new double[]{1, 1, 1};
		double[] other = new double[]{-1, 1, 1};
		// t > 0
		double[] shouldBeNull = box.findSegmentFaceIntersection(other, inside, 0, BoxWall.MAX);
		assertNull(shouldBeNull);

		// t < 0
		shouldBeNull = box.findSegmentFaceIntersection(inside, other, 0, BoxWall.MAX);
		assertNull(shouldBeNull);

		// t would be infinity
		shouldBeNull = box.findSegmentFaceIntersection(other, inside, 2, BoxWall.MAX);
		assertNull(shouldBeNull);

		// ret inside
		double[] intersectsMinX = new double[]{-1,1,1};
		double[] shouldBe011 = box.findSegmentFaceIntersection(inside, intersectsMinX, 0, BoxWall.MIN);
		assertEquals(shouldBe011.length, 3);
		assertEquals(0, shouldBe011[0], EPSILON);
		assertEquals(1, shouldBe011[1], EPSILON);
		assertEquals(1, shouldBe011[2], EPSILON);

		double[] intersectsMaxZ = new double[]{1,1,3};
		double[] shouldBe112 = box.findSegmentFaceIntersection(inside, intersectsMaxZ, 2, BoxWall.MAX);
		assertEquals(3, shouldBe112.length);
		assertEquals(1, shouldBe112[0], EPSILON);
		assertEquals(1, shouldBe112[1], EPSILON);
		assertEquals(2, shouldBe112[2], EPSILON);

		// ret outside
		double[] doesntIntersectMaxZ = new double[]{2,1,1.5};
		shouldBeNull = box.findSegmentFaceIntersection(inside, doesntIntersectMaxZ, 2, BoxWall.MAX);
		assertNull(shouldBeNull);
	}

	@Test
	public void testSegmentIntersectsPlane() {
		double[] inside = new double[]{1,1,1};
		double[] intersectsMinX = new double[]{-1,1,1};
		double[] intersectsMaxZ = new double[]{1,1,3};
		double[] noIntersection = new double[]{1,3,1};

		boolean hitMinX = box.segmentIntersectsPlane(inside, intersectsMinX, 0, BoxWall.MIN);
		assertTrue(hitMinX);

		boolean hitMaxZ = box.segmentIntersectsPlane(inside, intersectsMaxZ, 2, BoxWall.MAX);
		assertTrue(hitMaxZ);

		boolean hitMaxX = box.segmentIntersectsPlane(inside, intersectsMinX, 0, BoxWall.MAX);
		assertFalse(hitMaxX);

		boolean hitMinZ = box.segmentIntersectsPlane(inside, intersectsMinX, 2, BoxWall.MIN);
		assertFalse(hitMinZ);

		boolean hitMaxXfalse = box.segmentIntersectsPlane(noIntersection, inside, 0, BoxWall.MAX);
		assertFalse(hitMaxXfalse);
	}

	public void testAllToOneSide() {
		boolean shouldBeTrue = box.allToOneSide(new double[]{-1, 1, 1}, new double[]{-2, 1, 1});
		assertTrue(shouldBeTrue);
		shouldBeTrue = box.allToOneSide(new double[]{5, 1, 1}, new double[]{8, 1, 1});
		assertTrue(shouldBeTrue);
		shouldBeTrue = box.allToOneSide(new double[]{1, -1, 1}, new double[]{1, -2, 1});
		assertTrue(shouldBeTrue);
		shouldBeTrue = box.allToOneSide(new double[]{1, 4, 1}, new double[]{1, 6, 1});
		assertTrue(shouldBeTrue);
		shouldBeTrue = box.allToOneSide(new double[]{1, 1, -1}, new double[]{1, 1, -7});
		assertTrue(shouldBeTrue);
		shouldBeTrue = box.allToOneSide(new double[]{1, 1, 8}, new double[]{1, 1, 9});
		assertTrue(shouldBeTrue);

		boolean shouldBeFalse = box.allToOneSide(new double[]{1, 1, 1}, new double[]{1, 1, 1});
		assertFalse(shouldBeFalse);
	}

	@Test
	public void testContainsEnds() {
		double[] inside1 = new double[]{1,1,1};
		double[] inside2 = new double[]{0.25,1.3,1.8};
		double[] outside1 = new double[]{1,1,3};
		double[] outside2 = new double[]{1,3,1};

		Contains shouldBeBoth = box.containsEnds(inside1, inside2);
		assertEquals(Contains.BOTH, shouldBeBoth);

		Contains shouldBeStart = box.containsEnds(inside1, outside1);
		assertEquals(Contains.START_ONLY, shouldBeStart);

		Contains shouldBeEnd = box.containsEnds(outside1, inside2);
		assertEquals(Contains.END_ONLY, shouldBeEnd);

		Contains shouldBeNeither = box.containsEnds(outside1, outside2);
		assertEquals(Contains.NEITHER, shouldBeNeither);
	}

	@Test
	public void testPointContainment() {
		BoxMath box =
				new BoxMath(new double[]{-3, -2, -2},
						new double[]{4, 7, 9});
		assertTrue(box.contains(new double[]{1, 1, 2}));
		assertFalse(box.contains(new double[]{-4, 3, -1}));
		assertFalse(box.contains(new double[]{5, 3, -1}));
		assertFalse(box.contains(new double[]{-2, -3, -1}));
		assertFalse(box.contains(new double[]{-1, 9, -1}));
		assertFalse(box.contains(new double[]{0, 3, -3}));
		assertFalse(box.contains(new double[]{1, 3, 11}));
	}
	

	@Test
	public void testPointContainmentWithMargin() {
		BoxMath box =
				new BoxMath(new double[]{0, 0, 0},
						new double[]{1, 1, 1});
		assertTrue(box.containsWithMargin( new double[]{0.7, 0.5, 0.5}, 0.2));
		assertFalse(box.containsWithMargin(new double[]{0.5, 0.9, 0.5}, 0.2));
		assertFalse(box.containsWithMargin(new double[]{0.3, 0.5, 1}, 0.2));
	}

	@Test
	public void testSolveQuadratic() {
		// two solutions
		double[] sol = BoxMath.solveQuadratic(2, 5, -3);
		assertEquals(2, sol.length);
		java.util.Arrays.sort(sol);
		assertEquals(-3, sol[0], 1e-3);
		assertEquals(0.5, sol[1], 1e-3);

		// one solution
		sol = BoxMath.solveQuadratic(2, 5, 3.125);
		assertEquals(2, sol.length);
		java.util.Arrays.sort(sol);
		assertEquals(-1.25, sol[0], 1e-3);
		assertEquals(-1.25, sol[1], 1e-3);

		// no solutions
		sol = BoxMath.solveQuadratic(2, 5, 4);
		assertEquals(0, sol.length);

		// Non quadratic function
		sol = BoxMath.solveQuadratic(0, 2, 1);
		assertEquals(1, sol.length);
		assertEquals(-0.5, sol[0], 1e-3);
	}

	@Test
	public void testDotProduct() {
		double sol = BoxMath.dotProd(new double[]{0, 0, 0},
				new double[]{3, 4, 2});
		assertEquals(0, sol, 1e-3);
		sol = BoxMath.dotProd(new double[]{-1, 2, 1},
				new double[]{0.5, 1, 3});
		assertEquals(4.5, sol, 1e-3);
	}

	@Test
	public void testPropagatePosition() {
		double[] pt1 =
				BoxMath.propagatePosition(new double[]{1, 2, 1},
						new double[]{-1, -1, 1},
						new double[]{0, 1, 0}, 1);
		assertEquals(3, pt1.length);
		assertEquals(0, pt1[0], 1e-3);
		assertEquals(1.5, pt1[1], 1e-3);
		assertEquals(2, pt1[2], 1e-3);
	}

	@Test
	public void testCurveIntersectsWalls() {
		BoxMath box = new BoxMath(new double[]{0, 0, 0},
				new double[]{1, 1, 1});

		// Straight lines
		assertTrue(box.curveIntersectsWalls(new double[]{-0.5, 0.5, 0.5},
				new double[]{1, 0, 0},
				new double[]{0, 0, 0}, 2));
		assertFalse(box.curveIntersectsWalls(new double[]{-0.5, 0.5, 0.5},
				new double[]{-1, 0, 0},
				new double[]{0, 0, 0}, 2));
		assertTrue(box.curveIntersectsWalls(new double[]{1.5, 0.5, 0.5},
				new double[]{-1, 0, 0},
				new double[]{0, 0, 0}, 1));
		assertTrue(box.curveIntersectsWalls(new double[]{0.5, -0.5, 0.5},
				new double[]{0, 1, 0},
				new double[]{0, 0, 0}, 1));
		assertTrue(box.curveIntersectsWalls(new double[]{0.5, 1.5, 0.5},
				new double[]{0, -1, 0},
				new double[]{0, 0, 0}, 1));
		assertTrue(box.curveIntersectsWalls(new double[]{0.5, 0.5, -0.5},
				new double[]{0, 0, 1},
				new double[]{0, 0, 0}, 1));
		assertTrue(box.curveIntersectsWalls(new double[]{0.5, 0.5, 1.5},
				new double[]{0, 0, -1},
				new double[]{0, 0, 0}, 1));

		// Straight line that is entirely inside box
		assertFalse(box.curveIntersectsWalls(new double[]{0.5, 0.5, 0.5},
				new double[]{0.1, 0.1, 0.1},
				new double[]{0, 0, 0}, 3));
		assertTrue(box.curveIntersectsWalls(new double[]{0.5, 0.5, 0.5},
				new double[]{1, 1, 1},
				new double[]{0, 0, 0}, 2));
		assertFalse(box.curveIntersectsWalls(new double[]{0.5, 0.5, 0.5},
				new double[]{1, 1, 1},
				new double[]{0, 0, 0}, 0));

		// Look at a curve that loops through just one wall, both end points
		// outside box.
		assertTrue(box.curveIntersectsWalls(new double[]{0.25, 0.25, -0.5},
				new double[]{0.2, 0.2, 1.4},
				new double[]{0, 0, -1.4}, 2));

		// Look at a curve that loops through two walls, both end points outside
		// box.
		assertTrue(box.curveIntersectsWalls(new double[]{0.8, 0.25, -0.5},
				new double[]{0.2, 0, 1.4},
				new double[]{0, 0, -1.4}, 2));

		// Look at a curve that loops through just one wall, both end points inside
		// box.
		assertTrue(box.curveIntersectsWalls(new double[]{0.25, 0.25, 0.5},
				new double[]{0.2, 0.2, 1.4},
				new double[]{0, 0, -1.4}, 2));
	}

	@Test
	public void testContainsCurve() {
		BoxMath box = new BoxMath(new double[]{0, 0, 0},
				new double[]{1, 1, 1});
		// Look at a curve that loops through just one wall, both end points
		// outside box.
		assertFalse(box.containsCurve(new double[]{0.25, 0.25, -0.5},
				new double[]{0.2, 0.2, 1.4},
				new double[]{0, 0, -1.4}, 2));

		// Look at a curve that loops through two walls, both end points outside
		// box.
		assertFalse(box.containsCurve(new double[]{0.8, 0.25, -0.5},
				new double[]{0.2, 0, 1.4},
				new double[]{0, 0, -1.4}, 2));

		// Look at a curve that loops through just one wall, both end points inside
		// box.
		assertFalse(box.containsCurve(new double[]{0.25, 0.25, 0.5},
				new double[]{0.2, 0.2, 1.4},
				new double[]{0, 0, -1.4}, 2));

		// Curve that is entirely inside box
		assertTrue(box.containsCurve(new double[]{0.25, 0.25, 0.1},
				new double[]{0.2, 0.3, 1.4},
				new double[]{0, 0, -1.4}, 2));

		// Curve that is entirely outside box
		assertFalse(box.containsCurve(new double[]{0.25, 0.25, 1.1},
				new double[]{0.2, 0.3, 1.4},
				new double[]{0, 0, -1.4}, 2));

		// Troublesome test
		BoxMath box2 = new BoxMath(new double[]{0, 0, 0},
				new double[]{2, 2, 10});
		assertTrue(box2.containsCurve(new double[]{1, 1, 1.16666667},
				new double[]{0, 0, 1},
				new double[]{0, 0, 0}, 6.6666667));
	}

	@Test
	public void testDoesntContainCurve() {
		BoxMath box = new BoxMath(new double[]{0, 0, 0},
				new double[]{1, 1, 1});
		// Look at a curve that loops through just one wall, both end points
		// outside box.
		assertFalse(box.doesNotContainCurve(new double[]{0.25, 0.25, -0.5},
				new double[]{0.2, 0.2, 1.4},
				new double[]{0, 0, -1.4}, 2));

		// Look at a curve that loops through two walls, both end points outside
		// box.
		assertFalse(box.doesNotContainCurve(new double[]{0.8, 0.25, -0.5},
				new double[]{0.2, 0, 1.4},
				new double[]{0, 0, -1.4}, 2));

		// Look at a curve that loops through just one wall, both end points inside
		// box.
		assertFalse(box.doesNotContainCurve(new double[]{0.25, 0.25, 0.5},
				new double[]{0.2, 0.2, 1.4},
				new double[]{0, 0, -1.4}, 2));

		// Curve that is entirely inside box
		assertFalse(box.doesNotContainCurve(new double[]{0.25, 0.25, 0.1},
				new double[]{0.2, 0.3, 1.4},
				new double[]{0, 0, -1.4}, 2));

		// Curve that is entirely outside box
		assertTrue(box.doesNotContainCurve(new double[]{0.25, 0.25, 1.1},
				new double[]{0.2, 0.3, 1.4},
				new double[]{0, 0, -1.4}, 2));
	}
}
