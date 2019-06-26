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
package gov.nasa.arc.verve.robot.rapid;

import gov.nasa.rapid.util.math.RapidMath;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.exception.NotSubscribedException;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;

import org.apache.log4j.Logger;

import rapid.PositionConfig;
import rapid.PositionSample;
import rapid.RotationEncoding;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyMatrix3;
import com.ardor3d.math.type.ReadOnlyQuaternion;

/**
 * 
 * @author mallan
 *
 */
public class PositionSampleSourceRot extends PositionSampleSource implements IRotSource {
    protected static final Logger logger = Logger.getLogger(PositionSampleSourceRot.class);

    final Matrix3    tmpa = new Matrix3();
    final Matrix3    tmpb = new Matrix3();
    final Matrix3    tmpc = new Matrix3();
    final Quaternion quat = new Quaternion();
    final Quaternion qa   = new Quaternion();
    final Quaternion qb   = new Quaternion();
    final Quaternion qc   = new Quaternion();

    protected int warnCount = 0;
    public static int warnFreq = 30;

    protected PositionSample lastCalcSample = null;

    /**
     * 
     * @param name
     * @param msgType
     */
    public PositionSampleSourceRot(MessageType msgType, Agent agent) {
        super(msgType, agent);
    }

    //    public Matrix3 getRot(Matrix3 rot) {
    //        //logger.debug("msgType = "+msgType);
    //        if(sample != null) {
    //            float[] r = sample.pose.rot.userData;
    //            if(config == null) {
    //                rot.setIdentity();
    //                warnNoConfig();
    //            }
    //            else {
    //                switch(config.poseEncoding.ordinal()) {
    //                case RotationEncoding._RAPID_ROT_NONE:
    //                    if(!isRotationMatrix(r)) {
    //                        rot.setIdentity();
    //                        break;
    //                    }
    //                    //$FALL-THROUGH$
    //                case RotationEncoding._RAPID_ROT_M33:
    //                    rot.set(r[0], r[1], r[2], r[3], r[4], r[5], r[6], r[7], r[8]);
    //                    break;
    //                case RotationEncoding._RAPID_ROT_QUAT:
    //                    quat.set(r[0], r[1], r[2], r[3]);
    //                    rot.set(quat);
    //                    break;
    //                case RotationEncoding._RAPID_ROT_XYZ: // TODO: more efficient calc of matrix
    //                    tmpa.fromAngleNormalAxis(r[0], Vector3.UNIT_X);
    //                    tmpb.fromAngleNormalAxis(r[1], Vector3.UNIT_Y);
    //                    tmpc.fromAngleNormalAxis(r[2], Vector3.UNIT_Z);
    //                    rot.set(tmpa).multiplyLocal(tmpb).multiplyLocal(tmpc);
    //                    break;
    //                case RotationEncoding._RAPID_ROT_ZYX: // TODO: more efficient calc of matrix
    //                    tmpa.fromAngleNormalAxis(r[0], Vector3.UNIT_Z);
    //                    tmpb.fromAngleNormalAxis(r[1], Vector3.UNIT_Y);
    //                    tmpc.fromAngleNormalAxis(r[2], Vector3.UNIT_X);
    //                    rot.set(tmpa).multiplyLocal(tmpb).multiplyLocal(tmpc);
    //                    break;
    //                case RotationEncoding._RAPID_ROT_ZYZ: // TODO: more efficient calc of matrix
    //                    tmpa.fromAngleNormalAxis(r[0], Vector3.UNIT_Z);
    //                    tmpb.fromAngleNormalAxis(r[1], Vector3.UNIT_Y);
    //                    tmpc.fromAngleNormalAxis(r[2], Vector3.UNIT_Z);
    //                    rot.set(tmpa).multiplyLocal(tmpb).multiplyLocal(tmpc);
    //                    break;
    //                default:
    //                    logger.warn("Unrecognized RotationEncoding enum: "+config.poseEncoding.ordinal());
    //                    break;
    //                }
    //            }
    //        }
    //        return rot;
    //    }

    public Matrix3 getRot(Matrix3 rot) {
        //logger.debug("msgType = "+msgType);
        if(sample != null) {
            if(config == null) {
                rot.setIdentity();
                warnNoConfig();
            }
            else {
                calcRotation();
                rot.set(quat);
            }
        }
        return rot;
    }

    public ReadOnlyQuaternion calcRotation() {
        if(sample == lastCalcSample) {
            return quat;
        }
        else {
            calculateRotation(sample, config, 
                              quat, 
                              qa, qb, qc,tmpa);
            lastCalcSample = sample;
        }
        return quat;
    }

    public static Quaternion calculateRotation(PositionSample sample, PositionConfig config, 
                                               Quaternion quat, 
                                               Quaternion qa, Quaternion qb, Quaternion qc,
                                               Matrix3 tmpa) {
        float[] r = sample.pose.rot.userData;

        switch(config.poseEncoding.ordinal()) {
        case RotationEncoding._RAPID_ROT_NONE:
            if(!isRotationMatrix(r)) {
                tmpa.setIdentity();
                break;
            }
            //$FALL-THROUGH$
        case RotationEncoding._RAPID_ROT_M33:
            tmpa.set(r[0], r[1], r[2], r[3], r[4], r[5], r[6], r[7], r[8]);
            quat.fromRotationMatrix(tmpa);
            break;
        case RotationEncoding._RAPID_ROT_QUAT:
            quat.set(r[0], r[1], r[2], r[3]);
            break;
        case RotationEncoding._RAPID_ROT_XYZ: 
            qa.fromAngleNormalAxis(r[0], Vector3.UNIT_X);
            qb.fromAngleNormalAxis(r[1], Vector3.UNIT_Y);
            qc.fromAngleNormalAxis(r[2], Vector3.UNIT_Z);
            quat.set(qa).multiplyLocal(qb).multiplyLocal(qc);
            break;
        case RotationEncoding._RAPID_ROT_ZYX: 
            qa.fromAngleNormalAxis(r[0], Vector3.UNIT_Z);
            qb.fromAngleNormalAxis(r[1], Vector3.UNIT_Y);
            qc.fromAngleNormalAxis(r[2], Vector3.UNIT_X);
            quat.set(qa).multiplyLocal(qb).multiplyLocal(qc);
            break;
        case RotationEncoding._RAPID_ROT_ZYZ: 
            qa.fromAngleNormalAxis(r[0], Vector3.UNIT_Z);
            qb.fromAngleNormalAxis(r[1], Vector3.UNIT_Y);
            qc.fromAngleNormalAxis(r[2], Vector3.UNIT_Z);
            quat.set(qa).multiplyLocal(qb).multiplyLocal(qc);
            break;
        default:
            logger.warn("Unrecognized RotationEncoding enum: "+config.poseEncoding.ordinal());
            break;
        }
        return quat;
    }

    protected void warnNoConfig() {
        if(++warnCount%warnFreq == 20) {
            MessageType cfgType = MessageType.valueOf(msgType.getConfigName());
            try {
                Object cfgObj = RapidMessageCollector.instance().getLastMessage(participantId, agent, cfgType);
                if(cfgObj != null) {
                    // this shouldn't happen
                    logger.warn(cfgType.name()+" lastMessage is NOT null! "+cfgObj.toString());
                }
                else {
                    //TODO Tamar for Spheres there is no config and this makes too many messages
                    logger.warn(cfgType.name()+" is null");
                }
            }
            catch(NotSubscribedException e) {
                logger.warn("not subscribed to "+cfgType+", "+e.getMessage());
            }
        }
    }

    public static boolean isRotationMatrix(float[] r) {
        ReadOnlyMatrix3 m = new Matrix3(r[0], r[1], r[2], r[3], r[4], r[5], r[6], r[7], r[8]);
        Vector3 sum = checkRotationMatrix(m);
        if( (sum.getX() > 0.999 && sum.getX() < 1.001) &&
                (sum.getY() > 0.999 && sum.getY() < 1.001) &&
                (sum.getZ() > 0.999 && sum.getZ() < 1.001) )
            return true;
        return false;
    }

    public static Vector3 checkRotationMatrix(ReadOnlyMatrix3 m) {
        double c0sum = m.getValue(0,0)*m.getValue(0,0) + m.getValue(1,0)*m.getValue(1,0) + m.getValue(2,0)*m.getValue(2,0);
        double c1sum = m.getValue(0,1)*m.getValue(0,1) + m.getValue(1,1)*m.getValue(1,1) + m.getValue(2,1)*m.getValue(2,1);
        double c2sum = m.getValue(0,2)*m.getValue(0,2) + m.getValue(1,2)*m.getValue(1,2) + m.getValue(2,2)*m.getValue(2,2);
        //System.err.println(Ardor3D.format(m));
        //System.err.println(String.format("col sum of squares = %.4f  %.4f  %.4f", c0sum, c1sum, c2sum));
        return new Vector3(c0sum, c1sum, c2sum);
    }

    protected void printRapidRotation() {
        final float R2D = (float)RapidMath.RAD2DEG;
        if(sample != null && config != null) {
            RotationEncoding rotEnc = config.poseEncoding;
            String msg;
            float[] r = sample.pose.rot.userData;
            switch(rotEnc.ordinal()) {
            case RotationEncoding._RAPID_ROT_NONE:
                msg = agent.name()+" "+rotEnc.toString();
                break;
            case RotationEncoding._RAPID_ROT_M33:
                msg = String.format(agent.name()+" M33 %.2f %.2f %.2f\n    %.2f %.2f %.2f\n    %.2f %.2f %.2f\n",
                                    r[0], r[1], r[2], r[3], r[4], r[5], r[6], r[7], r[8]);
                break;
            case RotationEncoding._RAPID_ROT_QUAT:
                msg = String.format(agent.name()+" Quat: %.2f %.2f %.2f %.2f", r[0], r[1], r[2], r[3]);
                break;
            case RotationEncoding._RAPID_ROT_XYZ:
            case RotationEncoding._RAPID_ROT_ZYX:
            case RotationEncoding._RAPID_ROT_ZYZ:
                msg = String.format(agent.name()+" "+rotEnc+" %.2f %.2f %.2f", R2D*r[0], R2D*r[1], R2D*r[2]);
                msg = String.format(agent.name()+" "+rotEnc+" %.2f %.2f %.2f degrees: %.2f %.2f %.2f", r[0], r[1], r[2], R2D*r[0], R2D*r[1], R2D*r[2]);
                break;
            default:
                msg = "oops";
                break;
            }
            logger.debug(msg);
        }
    }
}
