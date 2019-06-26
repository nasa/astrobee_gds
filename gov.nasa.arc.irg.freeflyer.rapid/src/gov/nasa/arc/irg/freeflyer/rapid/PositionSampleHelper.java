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
package gov.nasa.arc.irg.freeflyer.rapid;

import gov.nasa.rapid.util.math.RapidMath;
import gov.nasa.rapid.v2.e4.message.MessageType;

import org.apache.log4j.Logger;

import rapid.PositionConfig;
import rapid.PositionSample;
import rapid.RotationEncoding;


/**
 * This class does not depend on Ardor3D but is otherwise similar to PositionSampleSource
 * @author mallan, tecohen
 *
 */
public class PositionSampleHelper {
    private static final Logger logger = Logger.getLogger(PositionSampleHelper.class);
    
    public static final double[] UNIT_X = new double[] {1, 0, 0};
    public static final double[] UNIT_Y = new double[] {0, 1, 0};
    public static final double[] UNIT_Z = new double[] {0, 0, 1};
    public static final double[][] IDENTITY = new double[][] {UNIT_X, UNIT_Y, UNIT_Z};
    
    protected final MessageType m_msgType;
    protected PositionConfig    m_config = null;
    protected PositionSample    m_sample = null;

    
    /**
     * 
     * @param name
     * @param msgType
     */
    public PositionSampleHelper( MessageType msgType) {
        this.m_msgType = msgType;
    }

    public MessageType msgType() { return m_msgType; }

    public void setData(Object sampleObj, Object configObj) {
        this.m_sample = (PositionSample)sampleObj;
        this.m_config = (PositionConfig)configObj;
//        if(false) {
//            printRapidRotation();
//        }
    }

    public double[] getXyz() {
        if(m_sample != null) {
            return m_sample.pose.xyz.userData;
        }
        return new double[]{};
    }
    
    public double[] getRpy() {
    	double[][] rot = getRot();
    	double omega = Math.asin(-rot[2][0]);
        double kappa = Math.atan2(rot[1][0], rot[0][0]);
        double phi = Math.atan2(rot[2][1], rot[2][2]);
        return new double[]{phi, omega, kappa};
    }

    public double[] getEuler() {
    	double[][] rot = getRot();
    	float[] inputs = new float[9];
    	inputs[0] = (float)rot[0][0];
    	inputs[1] = (float)rot[0][1];
    	inputs[2] = (float)rot[0][2];
    	inputs[3] = (float)rot[1][0];
    	inputs[4] = (float)rot[1][1];
    	inputs[5] = (float)rot[1][2];
    	inputs[6] = (float)rot[2][0];
    	inputs[7] = (float)rot[2][1];
    	inputs[8] = (float)rot[2][2];
    	double[] result =  RapidMath.rotationMatrixToEulerXYZ(inputs, null);
    	//swap shit around
    	double[] fixedResult = new double[3];
    	// roll
    	fixedResult[0] = -result[0];
    	fixedResult[1] = result[1];
    	fixedResult[2] = result[2];
    	return fixedResult;
    }
    
    public double[][] getRot() {
    	double[][] rot = new double[3][3];
        if(m_sample != null) {
            float[] r = m_sample.pose.rot.userData;
            if(m_config == null) {
                rot =  IDENTITY;
            }
            else {
                switch(m_config.poseEncoding.ordinal()) {
                case RotationEncoding._RAPID_ROT_NONE:
                    if(!isRotationMatrix(r)) {
                        rot =  IDENTITY;
                        break;
                    }
                    //$FALL-THROUGH$
                case RotationEncoding._RAPID_ROT_M33:
                	rot[0][0] = r[0];
                	rot[0][1] = r[1];
                	rot[0][2] = r[2];
                	rot[1][0] = r[3];
                	rot[1][1] = r[4];
                	rot[1][2] = r[5];
                	rot[2][0] = r[6];
                	rot[2][1] = r[7];
                	rot[2][2] = r[8];
                    break;
                case RotationEncoding._RAPID_ROT_QUAT:
                	rot = quaternionToRotationMatrix(r);
                    break;
                case RotationEncoding._RAPID_ROT_XYZ: // TODO: more efficient calc of matrix
                	double[][]  tmpa;
                    double[][]  tmpb;
                    double[][]  tmpc;

                    tmpa = fromAngleNormalAxis(r[0], UNIT_X);
                    tmpb = fromAngleNormalAxis(r[1], UNIT_Y);
                    tmpc = fromAngleNormalAxis(r[2], UNIT_Z);
                    rot = multiply(multiply(tmpa, tmpb), tmpc);
                    break;
                case RotationEncoding._RAPID_ROT_ZYX: // TODO: more efficient calc of matrix
                    tmpa = fromAngleNormalAxis(r[0], UNIT_Z);
                    tmpb = fromAngleNormalAxis(r[1], UNIT_Y);
                    tmpc = fromAngleNormalAxis(r[2], UNIT_X);
                    rot = multiply(multiply(tmpa, tmpb), tmpc);
                    break;
                case RotationEncoding._RAPID_ROT_ZYZ: // TODO: more efficient calc of matrix
                    tmpa = fromAngleNormalAxis(r[0], UNIT_Z);
                    tmpb = fromAngleNormalAxis(r[1], UNIT_Y);
                    tmpc = fromAngleNormalAxis(r[2], UNIT_Z);
                    rot = multiply(multiply(tmpa, tmpb), tmpc);
                    break;
                default:
                    logger.warn("Unrecognized RotationEncoding enum: "+m_config.poseEncoding.ordinal());
                    break;
                }
            }
        }
        return rot;
    }

    protected boolean isRotationMatrix(float[] r) {
        double[] sum = checkRotationMatrix(r);
        if( (sum[0] > 0.999 && sum[0] < 1.001) &&
                (sum[1] > 0.999 && sum[1] < 1.001) &&
                (sum[2] > 0.999 && sum[2] < 1.001) )
            return true;
        return false;
    }

    protected double[] checkRotationMatrix(float[] m) {
    	double[] result = new double[3];
        result[0] = m[0]*m[0] + m[1]*m[1] + m[2]*m[2];
        result[1] = m[3]*m[3] + m[4]*m[4] + m[5]*m[5];
        result[2] = m[6]*m[6] + m[7]*m[7] + m[8]*m[8];
       
        return result;
    }

    public String debugFileHeader() {
        return "#x,y,z,poseEncoding,rot[9]";
    }

    public String debugFileRow() {
        StringBuilder sb = new StringBuilder();
        if(m_sample != null) {
            sb.append(m_sample.pose.xyz.userData[0]).append(",");
            sb.append(m_sample.pose.xyz.userData[1]).append(",");
            sb.append(m_sample.pose.xyz.userData[2]).append(",");
            if(m_config == null) {
                sb.append("NULL").append(",");
            }
            else {
                sb.append(m_config.poseEncoding.ordinal()).append(",");
            }
            for(int i = 0; i < 9; i++) {
                if(i > 0)
                    sb.append(",");
                sb.append(m_sample.pose.rot.userData[i]);
            }
        }
        return sb.toString();
    }
    
//    protected void printRapidRotation() {
//        final float R2D = (float)RapidMath.RAD2DEG;
//        if(sample != null && config != null) {
//            RotationEncoding rotEnc = config.poseEncoding;
//            String msg;
//            float[] r = sample.pose.rot.userData;
//            switch(rotEnc.ordinal()) {
//            case RotationEncoding._RAPID_ROT_NONE:
//                msg = agent.name()+" "+rotEnc.toString();
//                break;
//            case RotationEncoding._RAPID_ROT_M33:
//                msg = String.format(agent.name()+" M33 %.2f %.2f %.2f\n    %.2f %.2f %.2f\n    %.2f %.2f %.2f\n",
//                                    r[0], r[1], r[2], r[3], r[4], r[5], r[6], r[7], r[8]);
//                break;
//            case RotationEncoding._RAPID_ROT_QUAT:
//                msg = String.format(agent.name()+" Quat: %.2f %.2f %.2f %.2f", r[0], r[1], r[2], r[3]);
//                break;
//            case RotationEncoding._RAPID_ROT_XYZ:
//            case RotationEncoding._RAPID_ROT_ZYX:
//            case RotationEncoding._RAPID_ROT_ZYZ:
//                msg = String.format(agent.name()+" "+rotEnc+" %.2f %.2f %.2f", R2D*r[0], R2D*r[1], R2D*r[2]);
//                msg = String.format(agent.name()+" "+rotEnc+" %.2f %.2f %.2f degrees: %.2f %.2f %.2f", r[0], r[1], r[2], R2D*r[0], R2D*r[1], R2D*r[2]);
//                break;
//            default:
//                msg = "oops";
//                break;
//            }
//            logger.debug(msg);
//        }
//    }

    /**
     * Sets this matrix to the rotation indicated by the given angle and a unit-length axis of rotation.
     * 
     * @param angle
     *            the angle to rotate (in radians).
     * @param axis
     *            the axis of rotation (already normalized).
     * @return this matrix for chaining
     * @throws NullPointerException
     *             if axis is null.
     */
    public double[][] fromAngleNormalAxis(final double angle, final double[] axis) {
    	
    	double[][] result = new double[3][3];
    	
        final double fCos = Math.cos(angle);
        final double fSin = Math.sin(angle);
        final double fOneMinusCos = (1.0) - fCos;
        final double fX2 = axis[0] * axis[0];
        final double fY2 = axis[1] * axis[1];
        final double fZ2 = axis[2] * axis[2];
        final double fXYM = axis[0] * axis[1] * fOneMinusCos;
        final double fXZM = axis[0] * axis[2] * fOneMinusCos;
        final double fYZM = axis[1] * axis[2] * fOneMinusCos;
        final double fXSin = axis[0] * fSin;
        final double fYSin = axis[1] * fSin;
        final double fZSin = axis[2] * fSin;

        result[0][0] = fX2 * fOneMinusCos + fCos;
        result[0][1] = fXYM - fZSin;
        result[0][2] = fXZM + fYSin;
        result[1][0] = fXYM + fZSin;
        result[1][1] = fY2 * fOneMinusCos + fCos;
        result[1][2] = fYZM - fXSin;
        result[2][0] = fXZM - fYSin;
        result[2][1] = fYZM + fXSin;
        result[2][2] = fZ2 * fOneMinusCos + fCos;

        return result;
    }
    
    /**
     * @param store
     *            the matrix to store our result in. If null, a new matrix is created.
     * @return the rotation matrix representation of this quaternion (normalized)
     * 
     *         if store is not null and is read only.
     */
    public double[][] quaternionToRotationMatrix(float[] quat) {
        double[][] result = new double[3][3];

        final double norm = quat[3] * quat[3] + quat[0] * quat[0] + quat[1] * quat[1] + quat[2] * quat[2];
        final double s = (norm > 0.0 ? 2.0 / norm : 0.0);

        // compute xs/ys/zs first to save 6 multiplications, since xs/ys/zs
        // will be used 2-4 times each.
        final double xs = quat[0] * s;
        final double ys = quat[1] * s;
        final double zs = quat[2] * s;
        final double xx = quat[0] * xs;
        final double xy = quat[0] * ys;
        final double xz = quat[0] * zs;
        final double xw = quat[3] * xs;
        final double yy = quat[1] * ys;
        final double yz = quat[1] * zs;
        final double yw = quat[3] * ys;
        final double zz = quat[2] * zs;
        final double zw = quat[3] * zs;

        // using s=2/norm (instead of 1/norm) saves 9 multiplications by 2 here
        result[0][0] = 1.0 - (yy + zz);
        result[0][1] = xy - zw;
        result[0][2] = xz + yw;
        result[1][0] = xy + zw;
        result[1][1] = 1.0 - (xx + zz);
        result[1][2] = yz - xw;
        result[2][0] = xz - yw;
        result[2][1] = yz + xw;
        result[2][2] = 1.0 - (xx + yy);

        return result;
    }
    
    /**
     * @param matrix
     * @param store
     *            a matrix to store the result in. if null, a new matrix is created. It is safe for the given matrix and
     *            this parameter to be the same object.
     * @return this matrix multiplied by the given matrix.
     * @throws NullPointerException
     *             if matrix is null.
     */
    public double[][] multiply(double[][] _data, double[][] matrix) {
        double[][] result = new double[3][3];
        result[0][0] = _data[0][0] * matrix[0][0] + _data[0][1] * matrix[1][0] + _data[0][2]
                * matrix[2][0];
        result[0][1] = _data[0][0] * matrix[0][1] + _data[0][1] * matrix[1][1] + _data[0][2]
                * matrix[2][1];
        result[0][2] = _data[0][0] * matrix[0][2] + _data[0][1] * matrix[1][2] + _data[0][2]
                * matrix[2][2];
        result[1][0] = _data[1][0] * matrix[0][0] + _data[1][1] * matrix[1][0] + _data[1][2]
                * matrix[2][0];
        result[1][1] = _data[1][0] * matrix[0][1] + _data[1][1] * matrix[1][1] + _data[1][2]
                * matrix[2][1];
        result[1][2] = _data[1][0] * matrix[0][2] + _data[1][1] * matrix[1][2] + _data[1][2]
                * matrix[2][2];
        result[2][0] = _data[2][0] * matrix[0][0] + _data[2][1] * matrix[1][0] + _data[2][2]
                * matrix[2][0];
        result[2][1] = _data[2][0] * matrix[0][1] + _data[2][1] * matrix[1][1] + _data[2][2]
                * matrix[2][1];
        result[2][2] = _data[2][0] * matrix[0][2] + _data[2][1] * matrix[1][2] + _data[2][2]
                * matrix[2][2];

        return result;
    }

}
