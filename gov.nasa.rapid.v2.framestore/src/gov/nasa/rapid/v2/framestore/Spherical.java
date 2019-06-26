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

import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Vector3;

/**
 * Spherical stores coordinates of a point expressed in azimuth, 
 * elevation and distance.
 * 
 * The convention for spherical is the following:
 * <ul>
 * <li>Both azimuth and elevation angles are expressed in radians.
 * <li>The azimuth angle is measured around the Z axis, from the X axis to the projection of the point on the X-Y plane</li>
 * <li>The elevation angle is measured from the X-Y plane to the point, positive on the side of positive Z values.
 * <li>Distance should always be positive. 
 * </ul>
 * <i>Note: the last rule is not enforced by the code, but creating Spherical with 
 * negative distance would lead to unexpected and likely wrong result!</i>
 * 
 * This class is a simple extension of the Vector3, with specific access
 * for safety.
 * 
 * @author Lorenzo Flueckiger
 *
 */
public class Spherical extends Vector3 implements ReadOnlySpherical {


    public Spherical() {
        super();
    }

    public Spherical(ReadOnlySpherical spherical) {
        super(spherical);
    }

    public Spherical(double azimuth, double elevation, double distance) {
        _x = azimuth;
        _y = elevation;
        _z = distance;
    }

    @Override
    public double getAzimuth() {
        return _x;
    }

    @Override
    public double getElevation() {
        return _y;
    }

    @Override
    public double getDistance() {
        // TODO Auto-generated method stub
        return _z;
    }

    public void setAzimuth(double azimuth) {
        _x = azimuth;
    }

    public void setElevation(double elevation) {
        _y = elevation;
    }

    public void setDistance(double distance) {
        _z = distance;
    }

    /**
     * Set the Spherical coordinates to the given parameters.
     * 
     * This method is not necessary (implemented exactly the same by the super class,
     *  but is repeated here to show meaningful arguments names
     */
    @Override
    public Spherical set(double azimuth, double elevation, double distance) {
        _x = azimuth;
        _y = elevation;
        _z = distance;
        return this;
    }

    @Override
    public String toString() {
        return "gov.nasa.rapid.v2.framestore.Spherical [az=" + getAzimuth() + ", el=" + getElevation() + ", d=" + getDistance() + "]";
    }

    @Override
    public boolean equals(final Object o) {
        if ( this == o ) {
            return true;
        }
        if ( !(o instanceof ReadOnlySpherical) ) {
            return false;
        }
        final ReadOnlySpherical that = (ReadOnlySpherical)o;
        if ( Math.abs(this.getDistance()-that.getDistance()) > MathUtils.ZERO_TOLERANCE ) {
            return false;
        }
        if ( !ConvertUtils.anglesEqual(this.getAzimuth(), that.getAzimuth()) ) {
            return false;
        }
        if ( !ConvertUtils.anglesEqual(this.getElevation(), that.getElevation()) ) {
            return false;
        }
        return true;
    }
}
