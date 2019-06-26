/*******************************************************************************
 * Copyright (c) 2013 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package gov.nasa.rapid.v2.framestore;

import gov.nasa.rapid.v2.framestore.ReadOnlyEulerAngles.Type;

import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyMatrix3;
import com.ardor3d.math.type.ReadOnlyVector3;

/**
 * Utility class to handle common transforms.
 * 
 * Unless specified, all angles are expressed in radians.
 * 
 * @author Lorenzo Flueckiger
 */
public class ConvertUtils {
	
	/**
     * Compute a set of Euler angles (XYZr) from a rotation matrix.
     * This method uses the Euler angles convention XYZr, meaning a transform equivalent
     * to successive rotations around X, then Y and finally Z of the body (rotating) frame.
     * Note that this set of Euler angles is equivalent to ZYXs: rotations around the fixed
     * (static) axes starting with Z, then Y and finally X.
     * @param rot       input rotation matrix
     * @param result    output result, if null, a new vector is created
     * @return          the Euler angles solution
     */
	static public EulerAngles toEulerAnglesXYZr(ReadOnlyMatrix3 rot, EulerAngles result) {
		 if ( result == null ) {
	            result = new EulerAngles(Type.XYZr);
	        }
	        if ( result.getType() != Type.XYZr ) {
	            throw new IllegalArgumentException("toEulerAnglesXYZr cannot use a store which is not XYZr");
	        }

	        double r11 = rot.getValue(0, 0);
	        double r12 = rot.getValue(0, 1);
	        double r13 = rot.getValue(0, 2);
	        //double r21 = rot.getValue(1, 0);
	        double r22 = rot.getValue(1, 1);
	        double r23 = rot.getValue(1, 2);
	        //double r31 = rot.getValue(2, 0);
	        double r32 = rot.getValue(2, 1);
	        double r33 = rot.getValue(2, 2);

	        // Solution Craig's "Introduction to Robotics"
	        // a = alpha, B = beta, g = gamma
	        // We compute a single solution with -90 < Deg(B) < 90
	        double cB = Math.sqrt(r11*r11+r12*r12);
	        double a, B, g;
	        if ( cB < 1E-6 ) {
	            a = 0.0;
	            if ( r13 > 0 ) {
	                B = Math.PI/2.0;
	                g = Math.atan2(r32, r22);
	            }
	            else {
	                B = -Math.PI/2.0;
	                g = -Math.atan2(r32, r22);
	            }
	        }
	        else {
	            B = Math.atan2(r13, cB);
	            a = Math.atan2(-r23/cB, r33/cB);
	            g = Math.atan2(-r12/cB, r11/cB);
	        }

	        result.set(a, B, g);
	        return result;
	}
	

    /**
     * Compute a set of Euler angles (ZYXr) from a rotation matrix.
     * This method uses the Euler angles convention ZYXr, meaning a transform equivalent
     * to successive rotations around Z, then Y and finally X of the body (rotating) frame.
     * Note that this set of Euler angles is equivalent to XYZs: rotations around the fixed
     * (static) axes starting with X, then Y and finally Z.
     * @param rot       input rotation matrix
     * @param result    output result, if null, a new vector is created
     * @return          the Euler angles solution
     */
    static public EulerAngles toEulerAngles(ReadOnlyMatrix3 rot, EulerAngles result) {
        if ( result == null ) {
            result = new EulerAngles(Type.ZYXr);
        }
        if ( result.getType() != Type.ZYXr ) {
            throw new IllegalArgumentException("toEulerAngles cannot use a store which is not ZYXr");
        }

        double r11 = rot.getValue(0, 0);
        double r22 = rot.getValue(1, 1);
        double r33 = rot.getValue(2, 2);
        double r12 = rot.getValue(0, 1);
        double r21 = rot.getValue(1, 0);
        double r32 = rot.getValue(2, 1);
        double r31 = rot.getValue(2, 0);

        // Solution Craig's "Introduction to Robotics"
        // We compute a single solution with -90 < Deg(th2) < 90
        double cy = Math.sqrt(r11*r11+r21*r21);
        double th1, th2, th3;
        if ( cy < 1E-6 ) {
            th1 = 0.0;
            if ( r31 < 0 ) {
                th2 = Math.PI/2.0;
                th3 = Math.atan2(r12, r22);
            }
            else {
                th2 = -Math.PI/2.0;
                th3 = -Math.atan2(r12, r22);
            }
        }
        else {
            th2 = Math.atan2(-r31, cy);
            th1 = Math.atan2(r21/cy, r11/cy);
            th3 = Math.atan2(r32/cy, r33/cy);
        }

        result.set(th1, th2, th3);
        return result;
    }

    /**
     * Compute a rotation matrix from a set of Euler angles (ZYXr).
     * This method uses the Euler angles convention ZYXr, meaning a transform equivalent
     * to successive rotations around Z, then Y and finally X of the body (rotating) frame.
     * Note that this set of Euler angles is equivalent to XYZs: rotations around the fixed
     * (static) axes starting with X, then Y and finally Z.
     * @param angles    input set of Euler angles 
     * @param result    output result, if null, a new matrix is created
     * @return          the rotation matrix derived from the Euler angles
     */
    static public Matrix3 toRotationMatrix(ReadOnlyEulerAngles angles, Matrix3 result) {
        if ( angles.getType() == Type.ZYXr ) {
            return toRotationMatrixZYXr(angles, result);
        }
        if ( angles.getType() == Type.XYZr ) {
        	return toRotationMatrixXYZr(angles, result);
        }
        throw new IllegalArgumentException("toRotationMatrix cannot use a angles which are not XYZr or ZYXr");
    }
    
    static public Matrix3 toRotationMatrixZYXr(ReadOnlyEulerAngles angles, Matrix3 result) {
    	if ( angles.getType() != Type.ZYXr ) {
            throw new IllegalArgumentException("toRotationMatrix cannot use angles which are not ZYXr");
        }
        if ( result == null ) {
            result = new Matrix3();
        }

        double gamma = angles.getAngle3();
        double beta = angles.getAngle2();
        double alpha = angles.getAngle1();
        double ca = Math.cos(alpha);
        double sa = Math.sin(alpha);
        double cb = Math.cos(beta);
        double sb = Math.sin(beta);
        double cg = Math.cos(gamma);
        double sg = Math.sin(gamma);

        result.setValue(0, 0, ca*cb);
        result.setValue(0, 1, ca*sb*sg-sa*cg);
        result.setValue(0, 2, ca*sb*cg+sa*sg);

        result.setValue(1, 0, sa*cb);
        result.setValue(1, 1, sa*sb*sg+ca*cg);
        result.setValue(1, 2, sa*sb*cg-ca*sg);

        result.setValue(2, 0, -sb);
        result.setValue(2, 1, cb*sg);
        result.setValue(2, 2, cb*cg);

        return result;
    }
    
    static public Matrix3 toRotationMatrixXYZr(ReadOnlyEulerAngles angles, Matrix3 result) {
    	if ( angles.getType() != Type.XYZr ) {
            throw new IllegalArgumentException("toRotationMatrix cannot use angles which are not XYZr");
        }
        if ( result == null ) {
            result = new Matrix3();
        }

        double gamma = angles.getAngle3();
        double beta = angles.getAngle2();
        double alpha = angles.getAngle1();
        double ca = Math.cos(alpha);
        double sa = Math.sin(alpha);
        double cb = Math.cos(beta);
        double sb = Math.sin(beta);
        double cg = Math.cos(gamma);
        double sg = Math.sin(gamma);

        result.setValue(0, 0,  cb*cg);
        result.setValue(0, 1, -cb*sg);
        result.setValue(0, 2,  sb);

        result.setValue(1, 0, sa*sb*cg+ca*sg);
        result.setValue(1, 1,-sa*sb*sg+ca*cg);
        result.setValue(1, 2,-sa*cb);

        result.setValue(2, 0, -ca*sb*cg+sa*sg);
        result.setValue(2, 1, ca*sb*sg+sa*cg);
        result.setValue(2, 2, ca*cb);

        return result;
    }
    
    

    /**
     * Convert a 3D point in Cartesian coordinates to its equivalent spherical coordinates
     * @param point     Coordinates of the point to convert (Cartesian)
     * @param result    Optional storage for the result (if null, then a new Spherical object will be created)
     * @return          Spherical coordinates of the point
     */
    static public Spherical toSpherical(ReadOnlyVector3 point, Spherical result) {
        if ( result == null ) {
            result = new Spherical();
        }
        double xyd = point.getX()*point.getX()+point.getY()*point.getY();
        result.setDistance( Math.sqrt(xyd+point.getZ()*point.getZ()) );
        if ( result.getDistance() < MathUtils.ZERO_TOLERANCE ) {
            result.setAzimuth( 0 );
            result.setElevation( 0 );
            result.setDistance( Double.NaN );
        }
        else {
            result.setAzimuth( Math.atan2(point.getY(), point.getX()) );
            result.setElevation(  Math.atan2(point.getZ(), Math.sqrt(xyd)) );
            if ( Math.abs(result.getElevation()-Math.PI/2)<MathUtils.ZERO_TOLERANCE/100  ||
                    Math.abs(result.getElevation()+Math.PI/2)<MathUtils.ZERO_TOLERANCE/100 ) {
                // Normalize the azimuth for elevation close to the poles
                result.setAzimuth(0);
            }
        }
        return result;
    }

    /**
     * Convert a 3D point from Spherical coordinates to its equivalent in Cartesian coordinates
     * @param spherical Coordinates of the point to convert (Spherical)
     * @param result    Optional storage for the result (if null, then a new Vector3 object will be created) 
     * @return          Cartesian coordinates of the point
     */
    static public Vector3 toPoint(ReadOnlySpherical spherical, Vector3 result) {
        if ( result == null ) {
            result = new Vector3();
        } 
        if ( spherical.getDistance() < MathUtils.ZERO_TOLERANCE || Double.isNaN(spherical.getDistance()) ) {
            result.zero();
        }
        else {
            double xyd = spherical.getDistance()*Math.cos(spherical.getElevation());
            result.setX( xyd*Math.cos(spherical.getAzimuth()) );
            result.setY( xyd*Math.sin(spherical.getAzimuth()) );
            result.setZ( spherical.getDistance()*Math.sin(spherical.getElevation()) );
        }
        return result;
    }

    /** Compares angles without being affected by a different number of turns (equal modulo 2*Pi)
     * @param a     Input angle 1
     * @param b     Input angle 2
     * @return      True if the angles are similar regarding the ZERO_TOLERANCE
     */
    static public boolean anglesEqual(double a, double b) {
        double turnsA = Math.floor(Math.abs(a/(2*Math.PI)));
        double turnsB = Math.floor(Math.abs(b/(2*Math.PI)));
        double normA = a-Math.signum(a)*turnsA*2*Math.PI;
        double normB = b-Math.signum(b)*turnsB*2*Math.PI;
        if ( normA < -MathUtils.ZERO_TOLERANCE/2 ) { normA += 2*Math.PI; }
        if ( normB < -MathUtils.ZERO_TOLERANCE/2 ) { normB += 2*Math.PI; }
        if ( Math.abs(normA-normB) < MathUtils.ZERO_TOLERANCE ) {
            return true;
        }
        return false;
    }

}
