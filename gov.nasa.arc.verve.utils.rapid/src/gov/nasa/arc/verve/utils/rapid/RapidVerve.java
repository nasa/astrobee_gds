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
package gov.nasa.arc.verve.utils.rapid;

import org.apache.log4j.Logger;

import rapid.RotationEncoding;
import rapid.Transform3D;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Transform;
import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Spatial;

public class RapidVerve {
    private static final Logger logger = Logger.getLogger(RapidVerve.class);

    //    public static Matrix4 toArdor(Matrix4d in, Matrix4 retVal) {
    //        retVal.set( 
    //                   in.m00, in.m01, in.m02, in.m03,
    //                   in.m10, in.m11, in.m12, in.m13,
    //                   in.m20, in.m21, in.m22, in.m23,
    //                   in.m30, in.m31, in.m32, in.m33 );
    //        return retVal;
    //    }
    //
    //    public static Matrix3 toArdorRotation(Matrix4d in, Matrix3 retVal) {
    //        retVal.set( 
    //                   in.m00, in.m01, in.m02,
    //                   in.m10, in.m11, in.m12,
    //                   in.m20, in.m21, in.m22);
    //        return retVal;
    //    }
    //
    //    public static Vector3 toArdorTranslation(Matrix4d in, Vector3 retVal) {
    //        retVal.set(in.m03, in.m13, in.m23);
    //        return retVal;
    //    }
    //
    //    public static Transform toArdorTransform(Matrix4d in, Transform retVal) {
    //        retVal.setIdentity();
    //        retVal.setRotation(toArdorRotation(in, (Matrix3)retVal.getMatrix()));
    //        retVal.setTranslation(toArdorTranslation(in, (Vector3)retVal.getTranslation()));
    //        return retVal;
    //    }

    /**
     * set a Matrix3 from a float array rotation matrix
     * @param r 3x3 rotation matrix from RAPID
     * @param rotEncoding rotation encoding
     * @param retVal
     * @return
     */
    public static Matrix3 toArdor(float[] r, RotationEncoding rotEncoding, Matrix3 retVal) {
        Matrix3 tmpa = new Matrix3();
        Matrix3 tmpb = new Matrix3();
        Matrix3 tmpc = new Matrix3();
        if(retVal == null) 
            retVal = new Matrix3();
        switch(rotEncoding.ordinal()) {
        case RotationEncoding._RAPID_ROT_NONE:
            //$FALL-THROUGH$
        case RotationEncoding._RAPID_ROT_M33:
            retVal.set(r[0], r[1], r[2], r[3], r[4], r[5], r[6], r[7], r[8]);
            break;
        case RotationEncoding._RAPID_ROT_QUAT:
            Quaternion quat = new Quaternion(r[0], r[1], r[2], r[3]);
            retVal.set(quat);
            break;
        case RotationEncoding._RAPID_ROT_XYZ: // TODO: more efficient calc of matrix
            tmpa.fromAngleNormalAxis(r[0], Vector3.UNIT_X);
            tmpb.fromAngleNormalAxis(r[1], Vector3.UNIT_Y);
            tmpc.fromAngleNormalAxis(r[2], Vector3.UNIT_Z);
            retVal.set(tmpa).multiplyLocal(tmpb).multiplyLocal(tmpc);
            break;
        case RotationEncoding._RAPID_ROT_ZYX: // TODO: more efficient calc of matrix
            tmpa.fromAngleNormalAxis(r[0], Vector3.UNIT_Z);
            tmpb.fromAngleNormalAxis(r[1], Vector3.UNIT_Y);
            tmpc.fromAngleNormalAxis(r[2], Vector3.UNIT_X);
            retVal.set(tmpa).multiplyLocal(tmpb).multiplyLocal(tmpc);
            break;
        case RotationEncoding._RAPID_ROT_ZYZ: // TODO: more efficient calc of matrix
            tmpa.fromAngleNormalAxis(r[0], Vector3.UNIT_Z);
            tmpb.fromAngleNormalAxis(r[1], Vector3.UNIT_Y);
            tmpc.fromAngleNormalAxis(r[2], Vector3.UNIT_Z);
            retVal.set(tmpa).multiplyLocal(tmpb).multiplyLocal(tmpc);
            break;
        default:
            logger.warn("Unrecognized RotationEncoding enum: "+rotEncoding);
            break;
        }
        return retVal;
    }


    /**
     * set a float[9] from a Matrix3 rotation matrix
     * @param rot
     * @param retVal
     * @return
     */
    public static float[] fromArdor(Matrix3 rot, float[] retVal) {
        if(retVal == null) {
            retVal = new float[9];
        }
        retVal[0] = (float)rot.getM00();
        retVal[1] = (float)rot.getM01();
        retVal[2] = (float)rot.getM02();
        retVal[3] = (float)rot.getM10();
        retVal[4] = (float)rot.getM11();
        retVal[5] = (float)rot.getM12();
        retVal[6] = (float)rot.getM20();
        retVal[7] = (float)rot.getM21();
        retVal[8] = (float)rot.getM22();
        return retVal;
    }

    /** XXX FIXME XXX this needs to accept rotation interpretation. 
     * currently only works for Poses w/ rotation matrixes
     */
    public static Transform toArdor(Transform3D rxfm, RotationEncoding rotEncoding, Transform retVal) {
        if(retVal == null) 
            retVal = new Transform();
        Matrix3 rot = toArdor(rxfm.rot.userData, rotEncoding, null);
        retVal.setRotation(rot);
        retVal.setTranslation(rxfm.xyz.userData[0], rxfm.xyz.userData[1], rxfm.xyz.userData[2]);
        return retVal;
    }



    public static Vector3 toArdor(double[] xyz, Vector3 retVal) {
        if(retVal == null) 
            retVal = new Vector3();
        retVal.set(xyz[0], xyz[1], xyz[2]);
        return retVal;
    }

    public static double[] fromArdor(Vector3 xyz, double[] retVal) {
        if(retVal == null) 
            retVal = new double[3];
        retVal[0] = xyz.getX();
        retVal[1] = xyz.getY();
        retVal[2] = xyz.getZ();
        return retVal;
    }

    public static Spatial setTransform(Spatial spatial, Transform3D rapidXfm, RotationEncoding rotEncoding) {
        Matrix3 rot = toArdor(rapidXfm.rot.userData, rotEncoding, null);
        spatial.setRotation(rot);
        final double[] xyz = rapidXfm.xyz.userData;
        spatial.setTranslation(xyz[0], xyz[1], xyz[2]);
        return spatial;
    }


}
