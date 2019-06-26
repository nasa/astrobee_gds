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
/*
 * Copyright (c) 2009 United States Government as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package gov.nasa.rapid.util.math;

public class RapidMath {
    
    public static final double RAD2DEG = 180.0/Math.PI;
    public static final double DEG2RAD = Math.PI/180.0;
    
    public static final double PI      = Math.PI;
    public static final double TWO_PI  = Math.PI * 2.0;
    public static final double HALF_PI = Math.PI / 2.0;
    public static final double EPSILON = 0.000001;

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
     * ensure angls are in range of -pi to pi
     */
    public static double[] anglesNegPiToPi(double[] angles) {
        for(int i = 0; i < angles.length; i++) {
            angles[i] = angleNegPiToPi(angles[i]);
        }
        return angles;
    }

    /**
     * ensure angle is in range of 0 to pi*2
     */
    public static double angleZeroToTwoPi(double angle) {
        angle = angle % TWO_PI;
        if (angle < 0)
            angle += TWO_PI;
        return angle;
    }
    
    /**
     * ensure angles are in range of 0 to pi*2
     */
    public static double[] anglesZeroToTwoPi(double[] angles) {
        for(int i = 0; i < angles.length; i++) {
            angles[i] = angleZeroToTwoPi(angles[i]);
        }
        return angles;
    }
        
    /**
     * set float[9] to identity matrix
     */
    public static float[] identity3x3(float[] retVal) {
        retVal[0] = 1; retVal[1] = 0; retVal[2] = 0;
        retVal[3] = 0; retVal[4] = 1; retVal[5] = 0;
        retVal[6] = 0; retVal[7] = 0; retVal[8] = 1;
        return retVal;
    }

    
    public static double[] rotationMatrixToEulerXYZ(final float[] mat, double[] retVal) {
        if(retVal == null || retVal.length < 3) {
            retVal = new double[3];
        }
        retVal[1] = Math.atan2( mat[2], Math.sqrt(mat[0]*mat[0]+mat[1]*mat[1] )) ;
        if( Math.abs( retVal[1] - HALF_PI ) < EPSILON ){
            retVal[0] = Math.atan2( mat[3], mat[4] ) ;
            retVal[2] = 0.0 ;
        }
        else if( Math.abs( retVal[1] + HALF_PI ) < EPSILON ){
            retVal[0] = Math.atan2( -mat[3], mat[4] ) ;
            retVal[2] = 0.0 ;
        }
        else{
            retVal[0] = Math.atan2( -mat[5], mat[8] ) ;
            retVal[2] = Math.atan2( -mat[1], mat[0] ) ;
        }
        return anglesNegPiToPi(retVal);
    }
    
    public static double[] rotationMatrixToEulerXZY(final float[] mat, double[] retVal) {
        if(retVal == null || retVal.length < 3) {
            retVal = new double[3];
        }
        retVal[1] = Math.asin( -mat[1] ) ;
        if( Math.abs( retVal[1] - HALF_PI ) < EPSILON ){
            retVal[0] = Math.atan2( mat[6], mat[3] ) ;
            retVal[2] = 0.0 ;
        }
        else if( Math.abs(retVal[1] + HALF_PI ) < EPSILON ){
            retVal[0] = Math.atan2( -mat[6], -mat[3] ) ;
            retVal[2] = 0.0 ;
        }
        else{
            retVal[0] = Math.atan2( mat[7], mat[4] ) ;
            retVal[2] = Math.atan2( mat[2], mat[0] ) ;
        }
        return anglesNegPiToPi(retVal);
    }
    
    public static double[] rotationMatrixToEulerYXZ(final float[] mat, double[] retVal) {
        if(retVal == null || retVal.length < 3) {
            retVal = new double[3];
        }
        retVal[1] = Math.asin( -mat[5] ) ;
        if( Math.abs( retVal[1] - HALF_PI ) < EPSILON ){
            retVal[0] = Math.atan2( mat[1], mat[0] ) ;
            retVal[2] = 0.0 ;
        }
        else if( Math.abs( retVal[1] + HALF_PI ) < EPSILON ){
            retVal[0] = Math.atan2( -mat[1], mat[0] ) ;
            retVal[2] = 0.0 ;
        }
        else{
            retVal[0] = Math.atan2( mat[2], mat[8] ) ;
            retVal[2] = Math.atan2( mat[3], mat[4] ) ;
        }
        return anglesNegPiToPi(retVal);
    }
    
    public static double[] rotationMatrixToEulerYZX(final float[] mat, double[] retVal) {
        if(retVal == null || retVal.length < 3) {
            retVal = new double[3];
        }
        retVal[1] = Math.atan2( mat[3], Math.sqrt(mat[4]*mat[4]+mat[5]*mat[5]));
        if( Math.abs( retVal[1] - PI/2.0) < EPSILON ) { 
            retVal[0] = Math.atan2( mat[2], mat[8] ) ;
            retVal[2] = 0.0 ;
        }
        else if( Math.abs( retVal[1] + PI/2.0 ) < EPSILON ) {  
            retVal[0] = Math.atan2( mat[2], mat[8] ) ;
            retVal[2] = 0.0 ;
        }
        else{
             retVal[0] = Math.atan2( -mat[6], mat[0] ) ;
             retVal[2] = Math.atan2( -mat[5], mat[4] ) ;
        }
        return anglesNegPiToPi(retVal);
    }
    
    public static double[] rotationMatrixToEulerZXY(final float[] mat, double[] retVal) {
        if(retVal == null || retVal.length < 3) {
            retVal = new double[3];
        }
        retVal[1] = Math.asin( mat[7] ) ;
        if( Math.abs( retVal[1] - HALF_PI ) < EPSILON ){
            retVal[0] = Math.atan2( mat[3], mat[0] ) ;
            retVal[2] = 0.0 ;
        }
        else if( Math.abs( retVal[1] + HALF_PI ) < EPSILON ){
            retVal[0] = Math.atan2( mat[3], mat[0] ) ;
            retVal[2] = 0.0 ;
        }
        else{
            retVal[0] = Math.atan2( -mat[1], mat[4] ) ;
            retVal[2] = Math.atan2( -mat[2], mat[8] ) ;
        }
        return anglesNegPiToPi(retVal);
    }
    
    public static double[] rotationMatrixToEulerZYX(final float[] mat, double[] retVal) {
        if(retVal == null || retVal.length < 3) {
            retVal = new double[3];
        }
        retVal[1] = Math.asin( -mat[6] ) ;
        if( Math.abs( retVal[1] - HALF_PI ) < EPSILON ){
            retVal[0] = Math.atan2( mat[5], mat[2] ) ;
            retVal[2] = 0.0 ;
        }
        else if( Math.abs( retVal[1] + HALF_PI ) < EPSILON ){
            retVal[0] = Math.atan2( -mat[5], -mat[2] ) ;
            retVal[2] = 0.0 ;
        }
        else{
            retVal[0] = Math.atan2( mat[3], mat[0] ) ;
            retVal[2] = Math.atan2( mat[7], mat[8] ) ;
        }
        return anglesNegPiToPi(retVal);
    }
    
    public static float[] eulerToRotationMatrixXYZ(final double[] angles, float[] retVal) {
        if(retVal == null || retVal.length < 9) {
            retVal = new float[9];
        }
        double s1,c1;
        double s2,c2;
        double s3,c3;
        s1 = Math.sin( angles[0] ) ;
        c1 = Math.cos( angles[0] ) ;
        s2 = Math.sin( angles[1] ) ;
        c2 = Math.cos( angles[1] ) ;
        s3 = Math.sin( angles[2] ) ;
        c3 = Math.cos( angles[2] ) ;
        retVal[0] = (float)( c2*c3            );
        retVal[1] = (float)(-c2*s3            );
        retVal[2] = (float)( s2               ); 
        retVal[3] = (float)( s1*s2*c3 + c1*s3 );
        retVal[4] = (float)(-s1*s2*s3 + c1*c3 );
        retVal[5] = (float)(-s1*c2            );
        retVal[6] = (float)(-c1*s2*c3 + s1*s3 );
        retVal[7] = (float)( c1*s2*s3 + s1*c3 );
        retVal[8] = (float)( c1*c2            );
        return retVal;
    }
    
    public static float[] eulerToRotationMatrixXZY(final double[] angles, float[] retVal) {
        if(retVal == null || retVal.length < 9) {
            retVal = new float[9];
        }
        double s1,c1;
        double s2,c2;
        double s3,c3;
        s1 = Math.sin( angles[0] ) ;
        c1 = Math.cos( angles[0] ) ;
        s2 = Math.sin( angles[1] ) ;
        c2 = Math.cos( angles[1] ) ;
        s3 = Math.sin( angles[2] ) ;
        c3 = Math.cos( angles[2] ) ;
        retVal[0] = (float)( c2*c3            );
        retVal[1] = (float)(-s2               );
        retVal[2] = (float)( c2*s3            );
        retVal[3] = (float)( c1*s2*c3 + s1*s3 );
        retVal[4] = (float)( c1*c2            );
        retVal[5] = (float)( c1*s2*s3 - s1*c3 );
        retVal[6] = (float)( s1*s2*c3 - c1*s3 );
        retVal[7] = (float)( s1*c2            );
        retVal[8] = (float)( s1*s2*s3 + c1*c3 );
        return retVal;
    }
    
    public static float[] eulerToRotationMatrixYXZ(final double[] angles, float[] retVal) {
        if(retVal == null || retVal.length < 9) {
            retVal = new float[9];
        }
        double s1,c1;
        double s2,c2;
        double s3,c3;
        s1 = Math.sin( angles[0] ) ;
        c1 = Math.cos( angles[0] ) ;
        s2 = Math.sin( angles[1] ) ;
        c2 = Math.cos( angles[1] ) ;
        s3 = Math.sin( angles[2] ) ;
        c3 = Math.cos( angles[2] ) ;
        retVal[0] = (float)( c1*c3 + s1*s2*s3 );
        retVal[1] = (float)(-c1*s3 + s1*s2*c3 );
        retVal[2] = (float)( s1*c2            );
        retVal[3] = (float)( c2*s3            );
        retVal[4] = (float)( c2*c3            );
        retVal[5] = (float)(-s2               );
        retVal[6] = (float)(-s1*c3 + c1*s2*s3 );
        retVal[7] = (float)( s1*s3 + c1*s2*c3 );
        retVal[8] = (float)( c1*c2            );
        return retVal;
    }
    
    public static float[] eulerToRotationMatrixYZX(final double[] angles, float[] retVal) {
        if(retVal == null || retVal.length < 9) {
            retVal = new float[9];
        }
        double s1,c1;
        double s2,c2;
        double s3,c3;
        s1 = Math.sin( angles[0] ) ;
        c1 = Math.cos( angles[0] ) ;
        s2 = Math.sin( angles[1] ) ;
        c2 = Math.cos( angles[1] ) ;
        s3 = Math.sin( angles[2] ) ;
        c3 = Math.cos( angles[2] ) ;
        retVal[0] = (float)( c1*c2            );
        retVal[1] = (float)(-c1*s2*c3 + s1*s3 );
        retVal[2] = (float)( c1*s2*s3 + s1*c3 );
        retVal[3] = (float)( s2               );
        retVal[4] = (float)( c2*c3            );
        retVal[5] = (float)(-c2*s3            );
        retVal[6] = (float)(-s1*c2            );
        retVal[7] = (float)( s1*s2*c3 + c1*s3 );
        retVal[8] = (float)(-s1*s2*s3 + c1*c3 );
        return retVal;
    }
    
    public static float[] eulerToRotationMatrixZXY(final double[] angles, float[] retVal) {
        if(retVal == null || retVal.length < 9) {
            retVal = new float[9];
        }
        double s1,c1;
        double s2,c2;
        double s3,c3;
        s1 = Math.sin( angles[0] ) ;
        c1 = Math.cos( angles[0] ) ;
        s2 = Math.sin( angles[1] ) ;
        c2 = Math.cos( angles[1] ) ;
        s3 = Math.sin( angles[2] ) ;
        c3 = Math.cos( angles[2] ) ;
        retVal[0] = (float)( c1*c3 - s1*s2*s3 );
        retVal[1] = (float)(-s1*c2            );
        retVal[2] = (float)( c1*s3 + s1*s2*c3 );
        retVal[3] = (float)( s1*c3 + c1*s2*s3 );
        retVal[4] = (float)( c1*c2            );
        retVal[5] = (float)( s1*s3 - c1*s2*c3 );
        retVal[6] = (float)(-c2*s3            );
        retVal[7] = (float)( s2               );
        retVal[8] = (float)( c2*c3            );
        return retVal;
    }
    
    public static float[] eulerToRotationMatrixZYX(final double[] angles, float[] retVal) {
        if(retVal == null || retVal.length < 9) {
            retVal = new float[9];
        }
        double s1,c1;
        double s2,c2;
        double s3,c3;
        s1 = Math.sin( angles[0] ) ;
        c1 = Math.cos( angles[0] ) ;
        s2 = Math.sin( angles[1] ) ;
        c2 = Math.cos( angles[1] ) ;
        s3 = Math.sin( angles[2] ) ;
        c3 = Math.cos( angles[2] ) ;
        retVal[0] = (float)( c2*c1            );
        retVal[1] = (float)(-c3*s1 + s3*s2*c1 );
        retVal[2] = (float)( s3*s1 + c3*s2*c1 );
        retVal[3] = (float)( c2*s1            );
        retVal[4] = (float)( c3*c1 + s3*s2*s1 );
        retVal[5] = (float)(-s3*c1 + c3*s2*s1 );
        retVal[6] = (float)(-s2               ); 
        retVal[7] = (float)( s3*c2            );
        retVal[8] = (float)( c3*c2            );
        return retVal;
    }
    
}
