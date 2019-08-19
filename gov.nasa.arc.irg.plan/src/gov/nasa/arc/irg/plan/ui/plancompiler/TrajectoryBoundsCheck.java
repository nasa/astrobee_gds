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

import gov.nasa.arc.irg.plan.model.Position;
import gov.nasa.arc.irg.plan.ui.plancompiler.BoxMath.Contains;
import gov.nasa.rapid.v2.framestore.ConvertUtils;
import gov.nasa.rapid.v2.framestore.EulerAngles;
import gov.nasa.rapid.v2.framestore.ReadOnlyEulerAngles;

import java.util.List;
import java.util.Vector;

import org.eclipse.e4.core.contexts.IEclipseContext;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;

public class TrajectoryBoundsCheck {
	public java.util.List<BoxMath> ISS_bounding_boxes;
	public java.util.List<BoxMath> keepout_zones;
	public double beeRadius = 0.1524;
	private double margin = .05;
	private IEclipseContext context;
	private final double DEG2RAD = Math.PI / 180.0;

	public TrajectoryBoundsCheck(java.util.List<BoxMath> ISS_bounding_boxes,
			java.util.List<BoxMath> keepout_zones) {
		this.ISS_bounding_boxes = ISS_bounding_boxes;
		this.keepout_zones = keepout_zones;
	}

	public void setContext(IEclipseContext context) {
		this.context = context;
	}

	// returns true if segment does not intersect boxes except where boxes are touching
	public boolean isSegmentSafe(Position start, Position end) {
		double[] sCenter = convertFloatList(start.getCoordinates());
		double[] eCenter = convertFloatList(end.getCoordinates());
		if(!isSegmentSafe(sCenter, eCenter, true)) {
			return false;
		}

		double[][] sCorners = getCorners(start.getCoordinates(), start.getOrientation());
		double[][] eCorners = getCorners(end.getCoordinates(), end.getOrientation());

		for(int i = 0; i < sCorners.length; i++) {
			if(!isSegmentSafe(sCorners[i], eCorners[i])) {
				return false;
			}
		}

		return true;
	}

	private double[] convertFloatList(List<Float> floats) {
		return new double[] { floats.get(0), floats.get(1), floats.get(2) };
	}

	private double[][] getCorners(List<Float> t, List<Float> angles) {
		EulerAngles ea = EulerAngles.fromDegrees(ReadOnlyEulerAngles.Type.ZYXr, angles.get(2), angles.get(1), angles.get(0));
		Matrix3 m33 = ConvertUtils.toRotationMatrix(ea, null);
		Quaternion q = new Quaternion();
		q.fromRotationMatrix(m33);
		double r = beeRadius + margin;

		return getCorners(t.get(0), t.get(1), t.get(2), r, q);	
	}

	public boolean isSegmentSafe(double[] start, double[] end) {
		return isSegmentSafe(start, end, true);
	}

	public boolean isSegmentSafe(double[] start, double[] end, boolean showCollisions) {
		if (start.length != 3 || end.length != 3)
			throw new IllegalArgumentException("Input start and end need to be size 3");

		Vector<double[]> allIntersections = new Vector<double[]>();

		//If the segment intersects the box, store the point at which it exits.
		for (BoxMath keepin : ISS_bounding_boxes) {
			Vector<double[]> oneBoxWorth =  keepin.findSegmentKeepinWallIntersection(start, end);
			if(oneBoxWorth != null) {
				allIntersections.addAll(oneBoxWorth);
			}
		}
		allIntersections = removeDuplicates(allIntersections);
		allIntersections = removeInteriorPoints(allIntersections);

		// If it intersects keepout zone, fail
		for (BoxMath keepout : keepout_zones) {
			if (keepout.segmentIntersectsKeepoutZone(start, end)) {
				Vector<double[]> oneBoxWorth = keepout.findSegmentKeepinWallIntersection(start, end);
				if(oneBoxWorth != null) {
					allIntersections.addAll(oneBoxWorth);
				}
			} 
		}

		if(context != null && showCollisions) {
			context.set(Vector.class, allIntersections);
		}

		if(allIntersections.size() > 0) {
			return false;
		}

		boolean startContained = false;
		boolean endContained = false;
		for (BoxMath keepin : ISS_bounding_boxes) {
			Contains answer =  keepin.containsEnds(start, end);
			switch(answer) {
			case BOTH:
				return true;
			case START_ONLY:
				startContained = true;
				break;
			case END_ONLY:
				endContained = true;
				break;
			default:
				break;
			}
		}

		if(startContained && endContained) {
			return true;
		}

		return false;
	}

	public Vector<double[]> removeInteriorPoints(Vector<double[]> points) {
		Vector<double[]> noInteriorPoints = new Vector<double[]>();
		double interiorPointsMargin = 0.02;
		// see if the interesection point is well within another keepin
		for(double[] point : points) {
			boolean isInteriorPoint = false;
			for (BoxMath keepin : ISS_bounding_boxes) {
				if(keepin.containsWithMargin(point, interiorPointsMargin)) {
					isInteriorPoint = true;
					break;
				}
			}
			if(!isInteriorPoint) {
				noInteriorPoints.add(point);
			}
		}
		return noInteriorPoints;
	}


	private Vector<double[]> removeDuplicates(Vector<double[]> points) {
		Vector<double[]> noDuplicates = new Vector<double[]>();
		for(double[] point : points) {
			if(!hasPartnerInList(point, points)) {
				noDuplicates.add(point);
			}
		}
		return noDuplicates;
	}

	public boolean allPointsHavePartners(Vector<double[]> list) {
		for(double[] check : list) {
			if(!hasPartnerInList(check, list)) {
				return false;
			}
		}
		return true;
	}

	public boolean hasPartnerInList(double[] pt, Vector<double[]> list) {
		BoxMath box = new BoxMath(new double[]{0, 0, 0},
				new double[]{2, 2, 2});

		int count = 0;
		for(double[] check : list) {
			if(box.vectorsAreEqual(pt, check)) {
				count++;
			}
		}

		if(count > 1) {
			return true;
		}
		return false;
	}
	
	// Expects angles in radians
	public boolean isAstrobeeSafe(double x, double y, double z, double roll, double pitch, double yaw) {
		return isAstrobeeSafeRadians(x, y, z, roll, pitch, yaw);
	}
	
	public boolean isAstrobeeSafeDegrees(double x, double y, double z, double roll, double pitch, double yaw) {
		return isAstrobeeSafeRadians(x, y, z, roll*DEG2RAD, pitch*DEG2RAD, yaw*DEG2RAD);
	}

	public boolean isAstrobeeSafeRadians(double x, double y, double z, double roll, double pitch, double yaw) {
		EulerAngles ea = EulerAngles.fromDegrees(ReadOnlyEulerAngles.Type.ZYXr, yaw, pitch, roll);
		Matrix3 m33 = ConvertUtils.toRotationMatrix(ea, null);
		Quaternion q = new Quaternion();
		q.fromRotationMatrix(m33);
		double r = beeRadius + margin;

		return checkEdges(x, y, z, r, q);
	}

	public boolean isAstrobeeSafeQuaternion(double x, double y, double z, Quaternion q) {
		double r = beeRadius + margin;

		return checkEdges(x, y, z, r, q);
	}


	private boolean checkEdges(double x, double y, double z, double r, Quaternion q) {
		double[][] corners = getCorners(x, y, z, r, q);
		double[][][] edges = getEdges(corners);

		for(double[][] edge : edges) {
			if(!isSegmentSafe(edge[0], edge[1], false)) {
				return false;
			}
		}

		return true;
	}

	private double[][] getCorners(double x, double y, double z, double r, Quaternion q) {
		double[][] corners = new double[8][3];	

		corners[0] = q.apply(new Vector3(-r, -r, -r), null).add(new Vector3(x, y, z), null).toArray(null);
		corners[1] = q.apply(new Vector3(-r, -r, r), null).add(new Vector3(x, y, z), null).toArray(null);
		corners[2] = q.apply(new Vector3(-r, r, -r), null).add(new Vector3(x, y, z), null).toArray(null);
		corners[3] = q.apply(new Vector3(-r, r, r), null).add(new Vector3(x, y, z), null).toArray(null);
		corners[4] = q.apply(new Vector3(r, -r, -r), null).add(new Vector3(x, y, z), null).toArray(null);
		corners[5] = q.apply(new Vector3(r, -r, r), null).add(new Vector3(x, y, z), null).toArray(null);
		corners[6] = q.apply(new Vector3(r, r, -r), null).add(new Vector3(x, y, z), null).toArray(null);
		corners[7] = q.apply(new Vector3(r, r, r), null).add(new Vector3(x, y, z), null).toArray(null);

		return corners;
	}
	/*
	 * WARNING: Corners must be ordered as in getCorners method.
	 */
	private double[][][] getEdges(double[][] corners) {
		double[][][] edges = new double[12][2][3];

		edges[0] = new double[][] { corners[0], corners[1] };
		edges[1] = new double[][] { corners[0], corners[2] };
		edges[2] = new double[][] { corners[0], corners[4] };

		edges[3] = new double[][] { corners[3], corners[1] };
		edges[4] = new double[][] { corners[3], corners[2] };
		edges[5] = new double[][] { corners[3], corners[7] };

		edges[6] = new double[][] { corners[5], corners[1] };
		edges[7] = new double[][] { corners[5], corners[4] };
		edges[8] = new double[][] { corners[5], corners[7] };

		edges[9] = new double[][] { corners[6], corners[2] };
		edges[10] = new double[][] { corners[6], corners[4] };
		edges[11] = new double[][] { corners[6], corners[7] };

		return edges;
	}

	public boolean isPointSafe(double x, double y, double z) {

		double[] pt = {x, y, z};
		// See that we are inside of an ISS bound
		boolean inside = false;
		for (BoxMath positive : ISS_bounding_boxes) {
			if (positive.contains(pt)) {
				inside = true;
			}
		}
		if(!inside) {
			return false;
		}

		// See that we are outside all of keepout_zones
		for (BoxMath negative : keepout_zones) {
			if (negative.contains(pt)) {
				return false;
			}
		}
		return true;
	}

	public Boolean isTrajectorySafe(FullState[] trajectory) {

		// Check each segment of the trajectory
		for (int i = 0; i < trajectory.length; i++) {
			// calculate dt, the bounds we are doing to continuous checking
			double dt = 100;
			// default to large number in case the final position is non-zero
			// velocity. So I'm saying the trajectory is safe for at least a 100
			// seconds after the final position.
			if (i < trajectory.length - 1) {
				dt = trajectory[i+1].t - trajectory[i].t;
			}

			// See that we are inside of an ISS bound
			boolean inside = false;
			for (BoxMath positive : ISS_bounding_boxes) {
				if (positive.containsCurve(trajectory[i].pos,
						trajectory[i].vel,
						trajectory[i].accel, dt)) {
					inside = true;
				}
			}
			if(!inside) {
				return false;
			}

			// See that we are outside all of keepout_zones
			for (BoxMath negative : keepout_zones) {
				if (!negative.doesNotContainCurve(trajectory[i].pos,
						trajectory[i].vel,
						trajectory[i].accel, dt)) {
					return false;
				}
			}
		}

		return true;
	}

	public void setMargin(double margin) {
		this.margin = margin;
	}

	public double getMargin() {
		return margin;
	}

}
