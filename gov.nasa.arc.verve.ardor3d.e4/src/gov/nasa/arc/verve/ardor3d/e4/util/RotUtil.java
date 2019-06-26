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
package gov.nasa.arc.verve.ardor3d.e4.util;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyMatrix3;
import com.ardor3d.math.type.ReadOnlyVector3;

public class RotUtil {
    public static final double PI      = Math.PI;
    public static final double TWO_PI  = Math.PI * 2.0;
    public static final double HALF_PI = Math.PI / 2.0;
    public static final double EPSILON = 0.000001;


    public static double clamp(double val, double min, double max) {
        if(val > max)
            val = max;
        else if(val < min)
            val = min;
        return val;
    }

    /**
     * ensure angle is in range of -pi to pi
     */
    public static double angleNegPiToPi(double angle) {
        angle = angle % TWO_PI;
        if (angle >= Math.PI)
            angle -= TWO_PI;
        else if (angle < -Math.PI)
            angle += TWO_PI;
        return angle;
    }

    /**
     * ensure angles are in range of -pi to pi
     */
    public static Vector3 anglesNegPiToPi(Vector3 angles) {
        angles.setX(angleNegPiToPi(angles.getX()));
        angles.setY(angleNegPiToPi(angles.getY()));
        angles.setZ(angleNegPiToPi(angles.getZ()));
        return angles;
    }


    public static Vector3 toEulerXYZ(ReadOnlyMatrix3 rot, Vector3 rpyStore) {
        rpyStore.setY(Math.atan2( rot.getValue(0,2), Math.sqrt(rot.getValue(0,0)*rot.getValue(0,0)+rot.getValue(0,1)*rot.getValue(0,1) ))) ;
        if( Math.abs( rpyStore.getY() - HALF_PI ) < EPSILON ){
            rpyStore.setX(Math.atan2( rot.getValue(1,0), rot.getValue(1,1) ));
            rpyStore.setZ(0.0);
        }
        else if( Math.abs( rpyStore.getY() + HALF_PI ) < EPSILON ){
            rpyStore.setX(Math.atan2( -rot.getValue(1,0), rot.getValue(1,1) ));
            rpyStore.setZ(0.0);
        }
        else{
            rpyStore.setX(Math.atan2( -rot.getValue(1,2), rot.getValue(2,2) ));
            rpyStore.setZ(Math.atan2( -rot.getValue(0,1), rot.getValue(0,0) ));
        }
        return anglesNegPiToPi(rpyStore);
    }

    public static Vector3 toEulerXZY(ReadOnlyMatrix3 rot, Vector3 rpyStore) {
        rpyStore.setY(Math.asin( -rot.getValue(0,1) ));
        if( Math.abs( rpyStore.getY() - HALF_PI ) < EPSILON ){
            rpyStore.setX(Math.atan2( rot.getValue(2,0), rot.getValue(1,0) ));
            rpyStore.setZ(0.0);
        }
        else if( Math.abs(rpyStore.getY() + HALF_PI ) < EPSILON ){
            rpyStore.setX(Math.atan2( -rot.getValue(2,0), -rot.getValue(1,0) ));
            rpyStore.setZ(0.0);
        }
        else{
            rpyStore.setX(Math.atan2( rot.getValue(2,1), rot.getValue(1,1) ));
            rpyStore.setZ(Math.atan2( rot.getValue(0,2), rot.getValue(0,0) ));
        }
        return anglesNegPiToPi(rpyStore);
    }

    public static Vector3 toEulerYXZ(ReadOnlyMatrix3 rot, Vector3 rpyStore) {
        rpyStore.setY(Math.asin( -rot.getValue(1,2) ));
        if( Math.abs( rpyStore.getY() - HALF_PI ) < EPSILON ){
            rpyStore.setX(Math.atan2( rot.getValue(0,1), rot.getValue(0,0) ));
            rpyStore.setZ(0.0);
        }
        else if( Math.abs( rpyStore.getY() + HALF_PI ) < EPSILON ){
            rpyStore.setX(Math.atan2( -rot.getValue(0,1), rot.getValue(0,0) ));
            rpyStore.setZ(0.0);
        }
        else{
            rpyStore.setX(Math.atan2( rot.getValue(0,2), rot.getValue(2,2) ));
            rpyStore.setZ(Math.atan2( rot.getValue(1,0), rot.getValue(1,1) ));
        }
        return anglesNegPiToPi(rpyStore);
    }

    public static Vector3 toEulerYZX(ReadOnlyMatrix3 rot, Vector3 rpyStore) {
        rpyStore.setY(Math.atan2( rot.getValue(1,0), Math.sqrt(rot.getValue(1,1)*rot.getValue(1,1)+rot.getValue(1,2)*rot.getValue(1,2))));
        if( Math.abs( rpyStore.getY() - HALF_PI) < EPSILON ) {  
            rpyStore.setX(Math.atan2( rot.getValue(0,2), rot.getValue(2,2) ));
            rpyStore.setZ(0.0);
        }
        else if( Math.abs( rpyStore.getY() + HALF_PI) < EPSILON ) {  
            rpyStore.setX(Math.atan2( rot.getValue(0,2), rot.getValue(2,2) ));
            rpyStore.setZ(0.0);
        }
        else{
            rpyStore.setX(Math.atan2( -rot.getValue(2,0), rot.getValue(0,0) ));
            rpyStore.setZ(Math.atan2( -rot.getValue(1,2), rot.getValue(1,1) ));
        }
        return anglesNegPiToPi(rpyStore);
    }

    public static Vector3 toEulerZXY(ReadOnlyMatrix3 rot, Vector3 rpyStore) {
        rpyStore.setY(Math.asin( rot.getValue(2,1) ));
        if( Math.abs( rpyStore.getY() - HALF_PI ) < EPSILON ){
            rpyStore.setX(Math.atan2( rot.getValue(1,0), rot.getValue(0,0) ));
            rpyStore.setZ(0.0);
        }
        else if( Math.abs( rpyStore.getY() + HALF_PI ) < EPSILON ){
            rpyStore.setX(Math.atan2( rot.getValue(1,0), rot.getValue(0,0) ));
            rpyStore.setZ(0.0);
        }
        else{
            rpyStore.setX(Math.atan2( -rot.getValue(0,1), rot.getValue(1,1) ));
            rpyStore.setZ(Math.atan2( -rot.getValue(0,2), rot.getValue(2,2) ));
        }
        return anglesNegPiToPi(rpyStore);
    }

    public static Vector3 toEulerZYX(ReadOnlyMatrix3 rot, Vector3 rpyStore) {
        rpyStore.setY(Math.asin( -rot.getValue(2,0) )) ;
        if( Math.abs( rpyStore.getY() - HALF_PI ) < EPSILON ){
            rpyStore.setX(Math.atan2( rot.getValue(1,2), rot.getValue(0,2) ));
            rpyStore.setZ(0.0);
        }
        else if( Math.abs( rpyStore.getY() + HALF_PI ) < EPSILON ){
            rpyStore.setX(Math.atan2( -rot.getValue(1,2), -rot.getValue(0,2) ));
            rpyStore.setZ(0.0);
        }
        else{
            rpyStore.setX(Math.atan2( rot.getValue(1,0), rot.getValue(0,0))) ;
            rpyStore.setZ(Math.atan2( rot.getValue(2,1), rot.getValue(2,2))) ;
        }
        return anglesNegPiToPi(rpyStore);
    }
    
    //----------------------------------------------------------------------------
    
    public static Matrix3 toMatrixXYZ(ReadOnlyVector3 angles, Matrix3 retVal) {
        if(retVal == null) {
            retVal = new Matrix3();
        }
        double s1,c1;
        double s2,c2;
        double s3,c3;
        s1 = Math.sin( angles.getX() ) ;
        c1 = Math.cos( angles.getX() ) ;
        s2 = Math.sin( angles.getY() ) ;
        c2 = Math.cos( angles.getY() ) ;
        s3 = Math.sin( angles.getZ() ) ;
        c3 = Math.cos( angles.getZ() ) ;
        retVal.setM00((float)( c2*c3            ));
        retVal.setM01((float)(-c2*s3            ));
        retVal.setM02((float)( s2               )); 
        retVal.setM10((float)( s1*s2*c3 + c1*s3 ));
        retVal.setM11((float)(-s1*s2*s3 + c1*c3 ));
        retVal.setM12((float)(-s1*c2            ));
        retVal.setM20((float)(-c1*s2*c3 + s1*s3 ));
        retVal.setM21((float)( c1*s2*s3 + s1*c3 ));
        retVal.setM22((float)( c1*c2            ));
        return retVal;
    }
    
    public static Matrix3 toMatrixXZY(ReadOnlyVector3 angles, Matrix3 retVal) {
        if(retVal == null) {
            retVal = new Matrix3();
        }
        double s1,c1;
        double s2,c2;
        double s3,c3;
        s1 = Math.sin( angles.getX() ) ;
        c1 = Math.cos( angles.getX() ) ;
        s2 = Math.sin( angles.getY() ) ;
        c2 = Math.cos( angles.getY() ) ;
        s3 = Math.sin( angles.getZ() ) ;
        c3 = Math.cos( angles.getZ() ) ;
        retVal.setM00((float)( c2*c3            ));
        retVal.setM01((float)(-s2               ));
        retVal.setM02((float)( c2*s3            ));
        retVal.setM10((float)( c1*s2*c3 + s1*s3 ));
        retVal.setM11((float)( c1*c2            ));
        retVal.setM12((float)( c1*s2*s3 - s1*c3 ));
        retVal.setM20((float)( s1*s2*c3 - c1*s3 ));
        retVal.setM21((float)( s1*c2            ));
        retVal.setM22((float)( s1*s2*s3 + c1*c3 ));
        return retVal;
    }
    
    public static Matrix3 toMatrixYXZ(ReadOnlyVector3 angles, Matrix3 retVal) {
        if(retVal == null) {
            retVal = new Matrix3();
        }
        double s1,c1;
        double s2,c2;
        double s3,c3;
        s1 = Math.sin( angles.getX() ) ;
        c1 = Math.cos( angles.getX() ) ;
        s2 = Math.sin( angles.getY() ) ;
        c2 = Math.cos( angles.getY() ) ;
        s3 = Math.sin( angles.getZ() ) ;
        c3 = Math.cos( angles.getZ() ) ;
        retVal.setM00((float)( c1*c3 + s1*s2*s3 ));
        retVal.setM01((float)(-c1*s3 + s1*s2*c3 ));
        retVal.setM02((float)( s1*c2            ));
        retVal.setM10((float)( c2*s3            ));
        retVal.setM11((float)( c2*c3            ));
        retVal.setM12((float)(-s2               ));
        retVal.setM20((float)(-s1*c3 + c1*s2*s3 ));
        retVal.setM21((float)( s1*s3 + c1*s2*c3 ));
        retVal.setM22((float)( c1*c2            ));
        return retVal;
    }
    
    public static Matrix3 toMatrixYZX(ReadOnlyVector3 angles, Matrix3 retVal) {
        if(retVal == null) {
            retVal = new Matrix3();
        }
        double s1,c1;
        double s2,c2;
        double s3,c3;
        s1 = Math.sin( angles.getX() ) ;
        c1 = Math.cos( angles.getX() ) ;
        s2 = Math.sin( angles.getY() ) ;
        c2 = Math.cos( angles.getY() ) ;
        s3 = Math.sin( angles.getZ() ) ;
        c3 = Math.cos( angles.getZ() ) ;
        retVal.setM00((float)( c1*c2            ));
        retVal.setM01((float)(-c1*s2*c3 + s1*s3 ));
        retVal.setM02((float)( c1*s2*s3 + s1*c3 ));
        retVal.setM10((float)( s2               ));
        retVal.setM11((float)( c2*c3            ));
        retVal.setM12((float)(-c2*s3            ));
        retVal.setM20((float)(-s1*c2            ));
        retVal.setM21((float)( s1*s2*c3 + c1*s3 ));
        retVal.setM22((float)(-s1*s2*s3 + c1*c3 ));
        return retVal;
    }
    
    public static Matrix3 toMatrix3ZXY(ReadOnlyVector3 angles, Matrix3 retVal) {
        if(retVal == null) {
            retVal = new Matrix3();
        }
        double s1,c1;
        double s2,c2;
        double s3,c3;
        s1 = Math.sin( angles.getX() ) ;
        c1 = Math.cos( angles.getX() ) ;
        s2 = Math.sin( angles.getY() ) ;
        c2 = Math.cos( angles.getY() ) ;
        s3 = Math.sin( angles.getZ() ) ;
        c3 = Math.cos( angles.getZ() ) ;
        retVal.setM00((float)( c1*c3 - s1*s2*s3 ));
        retVal.setM01((float)(-s1*c2            ));
        retVal.setM02((float)( c1*s3 + s1*s2*c3 ));
        retVal.setM10((float)( s1*c3 + c1*s2*s3 ));
        retVal.setM11((float)( c1*c2            ));
        retVal.setM12((float)( s1*s3 - c1*s2*c3 ));
        retVal.setM20((float)(-c2*s3            ));
        retVal.setM21((float)( s2               ));
        retVal.setM22((float)( c2*c3            ));
        return retVal;
    }
    
    public static Matrix3 toMatrixZYX(ReadOnlyVector3 angles, Matrix3 retVal) {
        if(retVal == null) {
            retVal = new Matrix3();
        }
        double s1,c1;
        double s2,c2;
        double s3,c3;
        s1 = Math.sin( angles.getX() ) ;
        c1 = Math.cos( angles.getX() ) ;
        s2 = Math.sin( angles.getY() ) ;
        c2 = Math.cos( angles.getY() ) ;
        s3 = Math.sin( angles.getZ() ) ;
        c3 = Math.cos( angles.getZ() ) ;
        retVal.setM00((float)( c2*c1            ));
        retVal.setM01((float)(-c3*s1 + s3*s2*c1 ));
        retVal.setM02((float)( s3*s1 + c3*s2*c1 ));
        retVal.setM10((float)( c2*s1            ));
        retVal.setM11((float)( c3*c1 + s3*s2*s1 ));
        retVal.setM12((float)(-s3*c1 + c3*s2*s1 ));
        retVal.setM20((float)(-s2               )); 
        retVal.setM21((float)( s3*c2            ));
        retVal.setM22((float)( c3*c2            ));
        return retVal;
    }


}
