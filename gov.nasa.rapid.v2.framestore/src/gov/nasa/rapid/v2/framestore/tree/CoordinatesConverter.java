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
package gov.nasa.rapid.v2.framestore.tree;

import gov.nasa.rapid.v2.framestore.ConvertUtils;
import gov.nasa.rapid.v2.framestore.EulerAngles;
import gov.nasa.rapid.v2.framestore.ReadOnlyEulerAngles;
import gov.nasa.rapid.v2.framestore.Spherical;
import gov.nasa.rapid.v2.framestore.ReadOnlySpherical;

import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.type.ReadOnlyMatrix3;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.math.type.ReadOnlyVector3;

/**
 * CoordinatesConverter ease the transform between reference frames of common 3D
 * concepts: point, vector, Euler angles and spherical coordinates.
 * 
 * @author Lorenzo Flueckiger
 */
public class CoordinatesConverter {

    /**
     * Transform a 3d point from one frame into another frame.
     * 
     * The 3d point is stored in a "Vector3", but is really considered as 
     * a point in space: it is subject to the full transform (translation+rotation)
     * between the two considered frames.
     * 
     * @param point
     *            Point to transform
     * @param refFrame
     *            Reference frame in which the point is currently expressed
     * @param wrtFrame
     *            Target frame in which the point should be expressed
     * @param store
     *            Store for the transformed coordinates of the point.
     *            If null, then a new point (Vector3) is created.
     * @return Transformed point expressed in wrtFrame (same point, new coordinate system) 
     */
    public static Vector3 transformPoint(ReadOnlyVector3 point,
            FrameTreeNode refFrame, FrameTreeNode wrtFrame, Vector3 store) {
        if (store == null) {
            store = new Vector3();
        }
        ReadOnlyTransform xfm = FrameTree.getTransform(wrtFrame, refFrame);
        xfm.applyForward(point, store);
        return store;
    }

    /**
     * Transform a 3d vector from one frame into another frame.
     * 
     * By "vector" we mean a direction in 3d space expressed by its 3
     * coordinates. Basically, the vector is subject to the relative rotation of
     * the two frames, but not their relative translations.
     * 
     * @param vector
     *            Vector to transform
     * @param refFrame
     *            Reference frame in which the vector is currently expressed
     * @param wrtFrame
     *            Target frame in which the vector should be expressed
     * @param store
     *            Store for the transformed coordinates of the vector.
     *            If null, then a new vector is created.
     * @return Transformed vector expressed in wrtFrame (same vector, new coordinate system)
     */
    public static Vector3 transformVector(ReadOnlyVector3 vector,
            FrameTreeNode refFrame, FrameTreeNode wrtFrame, Vector3 store) {
        if (store == null) {
            store = new Vector3();
        }
        ReadOnlyMatrix3 rot = FrameTree.getTransform(wrtFrame, refFrame).getMatrix();
        rot.applyPost(vector, store);
        return store;
    }

    /**
     * Transform a set of Euler angles from one frame into another frame.
     * 
     * @param angles    Source Euler angles to transform
     * @param refFrame  Reference frame in which the angles are currently expressed
     * @param wrtFrame  Target frame in which the Euler angles should be expressed
     * @param store     Store for the transformed Euler angles. If null, a new
     *                  Euler angles set is created.
     * @return          Transformed Euler angles set expressed in wrtFrame
     */
    public static EulerAngles transformAngles(ReadOnlyEulerAngles angles,
            FrameTreeNode refFrame, FrameTreeNode wrtFrame, EulerAngles store) {
        if ( store == null ) {
            store = new EulerAngles(angles.getType());
        }
        if ( store.getType() != angles.getType() ) {
            System.err.println("CoordinatesConverter.transformAngles Error: input angles and store have different types!");
            // TODO Maybe should throw an exception instead...
            return null;
        }
        Matrix3 orientation = ConvertUtils.toRotationMatrix(angles, null);
        ReadOnlyMatrix3 rotation = FrameTree.getTransform(wrtFrame, refFrame).getMatrix();
        Matrix3 result = rotation.multiply(orientation, null);
        ConvertUtils.toEulerAngles(result, store);
        return store;
    }
    
    /**
     * Transform a point expressed in spherical coordinates from one frame into another frame.
     * 
     * This methods as in fact two distinct usages:
     * <ol>
     * <li>If a fully defined spherical coordinate is given (distance > 0), then spherical
     * coordinates expressed in the target frame for the input point are returned. This 
     * transformation of spherical coordinates are using a <b>3d point</b>, and thus will also
     * take into account the relative translation between the source and target frame.</li>
     * <li>If only azimuth and elevation are given (distance set to zero [0] or nan [Double.NaN]),
     * then a new azimuth and elevation expressed in the target frame for a <b>vector</b> pointing
     * in the same direction than the input is returned. In this scenarion, the relative
     * translation between the source and target frame is ignored, and only their relative
     * orientation is taken into account.</li>
     * </ol>
     * This method is typically used to convert Azimuth/Elevation angles between reference frames, but
     * can also be used to convert 3d points expressed in spherical coordinates.
     * For the spherical convention used, see the @see Spherical class.
     * <br>
     * 
     * @param spherical Source Spherical Azimuth/Elevation/Distance
     *                  The spherical can describe the 3d location of a point if distance is strictly
     *                  greater than zero. Or the spherical will define a direction (vector) is
     *                  the distance is zero (0) or NaN (Double.NaN).
     * @param refFrame  Reference frame in which the Spherical is currently expressed
     * @param wrtFrame  Target frame in which the Spherical should be expressed
     * @param store     Store for the transformed coordinates of the Spherical.
     *                  If null, a new Spherical is created and returned.
     * @return          Transformed spherical coordinates expressed in wrtFrame
     *                  Note: the type of transform really depends of the spherical distance paramter. 
     */
    public static Spherical transformSpherical(ReadOnlySpherical spherical,
            FrameTreeNode refFrame, FrameTreeNode wrtFrame, Spherical store) {
        if ( store == null ) {
            store = new Spherical();
        }
        boolean degenerated = false;
        Spherical rectified = new Spherical(spherical);
        Vector3 transformed = new Vector3();
        // Handle the case where we only want to transform azimuth and elevation and
        // do not care about the distance
        if ( ( Math.abs(spherical.getDistance()) < MathUtils.ZERO_TOLERANCE ) || Double.isNaN(spherical.getDistance()) ) {
            rectified.setDistance(1.0);
            degenerated = true;
            Vector3 vector = ConvertUtils.toPoint(rectified, null);
            transformVector(vector, refFrame, wrtFrame, transformed);
        }
        else {
            Vector3 point = ConvertUtils.toPoint(rectified, null);
            transformPoint(point, refFrame, wrtFrame, transformed);
        }
        ConvertUtils.toSpherical(transformed, store);
        if ( degenerated ) {
            store.setDistance(spherical.getDistance());
        }
        return store;
    }
}
