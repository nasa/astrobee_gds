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
package gov.nasa.arc.irg.plan.ui.plancompiler;

import java.util.Vector;

public class BoxMath {
	public enum BoxWall {MIN, MAX}
	public enum Contains { NEITHER, START_ONLY, END_ONLY, BOTH }
	final double EPSILON = 1e-3;
	final double SLOPPY_EPSILON = 0.1;
	int numWalls = 3;

	public double[] min, max;
	private double[][] normals = new double[][] {
			{1, 0, 0},
			{-1, 0, 0},
			{0, 1, 0},
			{0, -1, 0},
			{0, 0, 1},
			{0, 0, -1},
	};

	public BoxMath(double[] min, double[] max) {
		if (min.length != 3 ||
				max.length != 3) {
			throw new IllegalArgumentException("Input vectors need to be size 3");
		}
		this.min = min;
		this.max = max;
	}

	/** returns true if any part of the segment is inside box, otherwise true */
	// Unit-tested
	public boolean segmentIntersectsKeepoutZone(double[] start, double[] end) {
		if (start.length != 3 || end.length != 3)
			throw new IllegalArgumentException("Inputs start and end need to be size 3");
		
		// if start and end points are both outside one face, no intersection
		if(allToOneSide(start, end)) {
			return false;
		}

		// if either end is in the box, segment intersects box
		if(contains(start) || contains(end)) {
			return true;
		}

		// if segment hits any face, it's illegal
		for(int wall=0; wall<3; wall++) {
			if(segmentIntersectsPlane(start, end, wall, BoxWall.MIN)) {
				if(findSegmentFaceIntersection(start, end, wall, BoxWall.MIN) != null) {
					return true;
				}
			}
			if(segmentIntersectsPlane(start, end, wall, BoxWall.MAX)) {
				if(findSegmentFaceIntersection(start, end, wall, BoxWall.MAX) != null) {
					return true;
				}
			}
		}
		return false;
	}

	/** returns intersections between segment and box, or null if they don't intersect */
	// Unit-tested
	public Vector<double[]> findSegmentKeepinWallIntersection(double[] start, double[] end) {
		if (start.length != 3 || end.length != 3)
			throw new IllegalArgumentException("Inputs start and end need to be size 3");
		
		// if start and end points are both outside one face, no intersections
		if(allToOneSide(start, end)) {
			return null;
		}

		// if start and end points are both inside the box, no wall intersections
		if(contains(start) && contains(end)) {
			return null;
		}

		Vector<double[]> intersections = new Vector<double[]>();

		// for all walls, find intersections
		for(int wall=0; wall<3; wall++) {
			if(segmentIntersectsPlane(start, end, wall, BoxWall.MIN)) {
				double[] hit = findSegmentFaceIntersection(start, end, wall, BoxWall.MIN);
				if(hit != null) {
					intersections.add(hit);
				}
			}
			if(segmentIntersectsPlane(start, end, wall, BoxWall.MAX)) {
				double[] hit = findSegmentFaceIntersection(start, end, wall, BoxWall.MAX);
				if(hit != null) {
					intersections.add(hit);
				}
			}
		}
		if(intersections.size() < 1) {
			return null;
		}
		if(intersections.size() > 2) {
			return eliminateDuplicates(intersections);
		}
		
		return intersections;
	}
	
	// Unit-tested
	public boolean vectorsAreEqual(double[] a, double[] b) {
		if (a.length != 3 || b.length != 3)
			throw new IllegalArgumentException("Input start and end need to be size 3");
		
		for(int i=0; i<3; i++) {
			if(Math.abs(a[i] - b[i]) > SLOPPY_EPSILON) {
				return false;
			}
		}
		return true;
	}
	
	// Unit-tested
	public Vector<double[]> eliminateDuplicates(Vector<double[]> in) {
		if(in.size() < 2) {
			return in;
		}

		Vector<double[]> out = new Vector<double[]>();

		for (double[] candidate : in) {
			boolean areEqual = false;
			
			for( double[] outgoing : out) {
				if(vectorsAreEqual(candidate, outgoing)) {
					areEqual = true;
					break;
				}
			}

			if(!areEqual) {
				out.add(candidate);
			}
		}
		return out;
	}

	/** does segment between them cross specified plane? */
	// Unit-tested
	public boolean segmentIntersectsPlane(double[] inside, double[] outside, int wallNum, BoxWall wallSide) {		
		if (inside.length != 3 || outside.length != 3)
			throw new IllegalArgumentException("Inputs inside and outside need to be size 3");
		if( wallNum < 0 || wallNum >= numWalls) 
			throw new IllegalArgumentException("Wall number must be between 0 and " + numWalls);

		// find the value of the wall we're talking about, and see if the right coordinates of pts are on either side of it
		double wallPosition = BoxWall.MAX.equals(wallSide) ? max[wallNum] : min[wallNum];

		if( (inside[wallNum] <= wallPosition + EPSILON && outside[wallNum] >= wallPosition - EPSILON) 
				|| (inside[wallNum] >= wallPosition - EPSILON && outside[wallNum] <= wallPosition + EPSILON) ) {
			return true;
		}
		return false;
	}

	/** given points on opposite sides of the wall, returns intersection point or null if intersection outside */
	// Unit-tested
	public double[] findSegmentFaceIntersection(double[] inside, double[] outside, int wallNum, BoxWall wallSide) {
		if (inside.length != 3 || outside.length != 3)
			throw new IllegalArgumentException("Inputs inside and outside need to be size 3");
		if( wallNum < 0 || wallNum >= numWalls) 
			throw new IllegalArgumentException("Wall number must be between 0 and "+numWalls);

		double[] ret = new double[3];
		double[] dir = new double[3];
		for(int i=0; i<3; i++) {
			dir[i] = outside[i] - inside[i];
		}

		// we intersect with an axis-aligned plane, so we know one coordinate already
		ret[wallNum] = BoxWall.MAX.equals(wallSide) ? max[wallNum] : min[wallNum];

		// equation of the segment from in to out, where dir = out - in, is:
		// (x - in.x) = (y - in.y) = (z - in.z) = t
		//   dir.x        dir.y        dir.z

		if(Math.abs(dir[wallNum]) < EPSILON) {
			return null;
		}

		double t = (ret[wallNum] - inside[wallNum]) / dir[wallNum];
		if(t < 0 || t > 1) {
			return null;
		}

		int ind1 = (wallNum + 1) % 3;
		int ind2 = (wallNum + 2) % 3;

		ret[ind1] = t*dir[ind1] + inside[ind1];
		ret[ind2] = t*dir[ind2] + inside[ind2];

		if(min[ind1] <= ret[ind1] && ret[ind1] <= max[ind1]
				&& min[ind2] <= ret[ind2] && ret[ind2] <= max[ind2] ) {
			return ret;
		}
		return null;
	}

	/** returns  */
	// Unit-tested
	public Contains containsEnds(double[] start, double[] end) {
		if (start.length != 3 || end.length != 3)
			throw new IllegalArgumentException("Inputs start and end need to be size 3");

		int code = 0;
		if(contains(start)) { 
			code += 1;
		}
		if(contains(end)) {
			code += 2;
		}

		Contains answer = Contains.values()[code];

		return answer;
	}

	/** true if start and end points are both outside the plane of one face, (no intersection) */
	// Unit-tested
	public boolean allToOneSide(double[] start, double[] end) {
		if (start.length != 3 || end.length != 3)
			throw new IllegalArgumentException("Inputs start and end need to be size 3");

		if (end[0] < min[0] && start[0] < min[0]) return true;
		if (end[0] > max[0] && start[0] > max[0]) return true;
		if (end[1] < min[1] && start[1] < min[1]) return true;
		if (end[1] > max[1] && start[1] > max[1]) return true;
		if (end[2] < min[2] && start[2] < min[2]) return true;
		if (end[2] > max[2] && start[2] > max[2]) return true;

		return false;
	}

	public Boolean contains(double[] pt) {
		if (pt.length != 3)
			throw new IllegalArgumentException("Input pt needs to be size 3");

		// Test that we are inside all planes
		for (int i = 0; i < 3; i++) {
			if (pt[i] < min[i] || pt[i] >= max[i])
				return false;
		}

		return true;
	}
	
	public Boolean containsWithMargin(double[] pt, double margin) {
		if (pt.length != 3)
			throw new IllegalArgumentException("Input pt needs to be size 3");

		// Test that we are well inside all planes
		for (int i = 0; i < 3; i++) {
			if (pt[i] < (min[i]+margin) || pt[i] >= (max[i]-margin))
				return false;
		}

		return true;
	}

	static public double[] solveQuadratic(double a, double b, double c) {
		// See if this thing is actually a quadratic
		if (Math.abs(a) < 1e-6) {
			// If b is zero .. there is no solution
			if (Math.abs(b) < 1e-6) {
				return new double[0];
			}

			double[] output = new double[1];
			output[0] = -c / b;
			return output;
		}

		// See how many solutions there are for the quadratic
		double discriminant = b * b - 4 * a * c;
		if ( discriminant < 0) {
			return new double[0];
		}
		discriminant = Math.sqrt(discriminant);
		double[] output = new double[2];
		output[0] = (-b + discriminant) / (2 * a);
		output[1] = (-b - discriminant) / (2 * a);
		return output;
	}

	static public double dotProd(double[] a, double[] b) {
		double output = 0;
		for (int i = 0; i < 3; i++) {
			output += a[i] * b[i];
		}
		return output;
	}

	static public double[] propagatePosition(double[] pt, double[] vel, double[] acc, double dt) {
		double[] output = new double[3];

		for (int i = 0; i < 3; i++) {
			output[i] = pt[i] + vel[i] * dt + 0.5 * dt * dt * acc[i];
		}

		return output;
	}

	public Boolean curveIntersectsWalls(double[] pt, double[] vel, double[] acc, double dt) {
		// Now test the whole curve equation and verify that it doesn't intersect
		// through any of the 6 sides.
		for (int i = 0; i < 6; i++) {
			double plane_c = -max[i/2];
			if ((i % 2) == 1) {
				plane_c = -min[i/2];
			}
			double quad_a = 0.5 * dotProd(acc, normals[i]);
			double quad_b = dotProd(vel, normals[i]);
			double quad_c = dotProd(pt, normals[i]) + plane_c;

			double[] t_intersection = solveQuadratic(quad_a, quad_b, quad_c);

			for (double int_t : t_intersection) {
				// Does this t exist in the time frame we care about?
				if (int_t >= 0 && int_t <= dt) {
					// Let's now see where those points are
					double[] intersection_pt = propagatePosition(pt, vel, acc, int_t);
					switch (i) {
					case 0:
					case 1:
						// Was it in the bounds of Y and Z, then it left the box
						if (intersection_pt[1] >= min[1] && intersection_pt[1] <= max[1] &&
						intersection_pt[2] >= min[2] && intersection_pt[2] <= max[2]) {
							return true;
						}
						break;
					case 2:
					case 3:
						// Was it in the bounds of X and Z, then it left the box
						if (intersection_pt[0] >= min[0] && intersection_pt[0] <= max[0] &&
						intersection_pt[2] >= min[2] && intersection_pt[2] <= max[2]) {
							return true;
						}
						break;
					case 4:
					case 5:
					default:
						// Was it in the bounds of X and Y, then it left the box
						if (intersection_pt[0] >= min[0] && intersection_pt[0] <= max[0] &&
						intersection_pt[1] >= min[1] && intersection_pt[1] <= max[1]) {
							return true;
						}
						break;
					} // switch
				}
			} // for ti
		} // for side

		return false;
	}

	public Boolean containsCurve(double[] pt, double[] vel, double[] acc, double dt) {
		if (pt.length != 3 || vel.length != 3 || acc.length != 3)
			throw new IllegalArgumentException("Input pt needs to be size 3");
		if (dt < 0)
			throw new IllegalArgumentException("Input dt needs to be greater than 0");

		// Verify that the beginning and end are inside .. then verify that the
		// curve never intersects with the edge of the box.
		if (!contains(pt)) {
			return false;
		}

		// Last point
		double[] pt_last = propagatePosition(pt, vel, acc, dt);
		if (!contains(pt_last)) {
			return false;
		}

		// Now test the whole curve equation and verify that it doesn't intersect
		// through any of the 6 sides.
		if (curveIntersectsWalls(pt, vel, acc, dt)) {
			return false;
		}

		return true;
	}

	// This needs to be a separate function, because the answer is not as simple as taking the not of the above.
	public Boolean doesNotContainCurve(double[] pt, double[] vel, double[] acc, double dt) {
		if (pt.length != 3 || vel.length != 3 || acc.length != 3)
			throw new IllegalArgumentException("Input pt needs to be size 3");
		if (dt < 0)
			throw new IllegalArgumentException("Input dt needs to be greater than 0");

		// Verify that the ends points are not inside the box
		if (contains(pt))
			return false;

		// Last point
		double[] pt_last = propagatePosition(pt, vel, acc, dt);
		if (contains(pt_last))
			return false;

		// Verify that it doesn't intersect any of the walls
		if (curveIntersectsWalls(pt, vel, acc, dt))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "min = " + min[0] + ", " + min[1] + ", " + min[2]
				+ ", max = " + max[0] + ", " + max[1] + ", " + max[2];
	}

	public double distanceSquared(double[] p1, double[] p2) {
		if (p1.length != 3 || p2.length != 3 )
			throw new IllegalArgumentException("Input pt needs to be size 3");

		double ans = 0;
		for(int i=0; i<3; i++) {
			ans += (p1[i] - p2[i]) * (p1[i] - p2[i]);
		}

		return ans;
	}
}
