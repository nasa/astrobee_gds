

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.astrobee;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

/**
* The state of the ekf.
*/

public class EkfState  extends rapid.Message implements Copyable, Serializable{

    /** robot body pose */
    public rapid.Transform3D pose = (rapid.Transform3D)rapid.Transform3D.create();
    /** the body velocity (m/s) */
    public rapid.Vec3d velocity = (rapid.Vec3d)rapid.Vec3d.create();
    /** body rotational velocity (rad/s) */
    public rapid.Vec3d omega = (rapid.Vec3d)rapid.Vec3d.create();
    /** estimated gyro bias (rad/s) */
    public rapid.Vec3d gyro_bias = (rapid.Vec3d)rapid.Vec3d.create();
    /** acceleration in body frame (m/s/s) */
    public rapid.Vec3d accel = (rapid.Vec3d)rapid.Vec3d.create();
    /** estimated accel bias (m/s/s) */
    public rapid.Vec3d accel_bias = (rapid.Vec3d)rapid.Vec3d.create();
    /** Filter Health - covariance diagonal.
    * 1-3 orientation, 4-6 gyro bias, 7-9 velocity,
    * 10-12 accel bias, 13-15 position
    */
    public rapid.ext.astrobee.FloatSequence15 cov_diag = (rapid.ext.astrobee.FloatSequence15)rapid.ext.astrobee.FloatSequence15.create();
    /** confidence in EKF, 0 is good, 1 is a bit
    * confused, 2 is lost
    */
    public short confidence= 0;
    /** Status byte sent by GNC */
    public short status= 0;
    /** optical flow features this frame
    * (0 if no update)
    */
    public short of_count= 0;
    /** ml features this frame (0 if no update) */
    public short ml_count= 0;
    /** Global handrail pose */
    public rapid.Transform3D hr_global_pose = (rapid.Transform3D)rapid.Transform3D.create();
    /** Mahalanobis distances for features */
    public rapid.ext.astrobee.FloatSequence50 ml_mahal_dists = (rapid.ext.astrobee.FloatSequence50)rapid.ext.astrobee.FloatSequence50.create();

    public EkfState() {

        super();

        /** robot body pose */
        /** the body velocity (m/s) */
        /** body rotational velocity (rad/s) */
        /** estimated gyro bias (rad/s) */
        /** acceleration in body frame (m/s/s) */
        /** estimated accel bias (m/s/s) */
        /** Filter Health - covariance diagonal.
        * 1-3 orientation, 4-6 gyro bias, 7-9 velocity,
        * 10-12 accel bias, 13-15 position
        */
        /** confidence in EKF, 0 is good, 1 is a bit
        * confused, 2 is lost
        */
        /** Status byte sent by GNC */
        /** optical flow features this frame
        * (0 if no update)
        */
        /** ml features this frame (0 if no update) */
        /** Global handrail pose */
        /** Mahalanobis distances for features */

    }
    public EkfState (EkfState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        EkfState self;
        self = new  EkfState();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** robot body pose */
        if (pose != null) {
            pose.clear();
        }
        /** the body velocity (m/s) */
        if (velocity != null) {
            velocity.clear();
        }
        /** body rotational velocity (rad/s) */
        if (omega != null) {
            omega.clear();
        }
        /** estimated gyro bias (rad/s) */
        if (gyro_bias != null) {
            gyro_bias.clear();
        }
        /** acceleration in body frame (m/s/s) */
        if (accel != null) {
            accel.clear();
        }
        /** estimated accel bias (m/s/s) */
        if (accel_bias != null) {
            accel_bias.clear();
        }
        /** Filter Health - covariance diagonal.
        * 1-3 orientation, 4-6 gyro bias, 7-9 velocity,
        * 10-12 accel bias, 13-15 position
        */
        if (cov_diag != null) {
            cov_diag.clear();
        }
        /** confidence in EKF, 0 is good, 1 is a bit
        * confused, 2 is lost
        */
        confidence= 0;
        /** Status byte sent by GNC */
        status= 0;
        /** optical flow features this frame
        * (0 if no update)
        */
        of_count= 0;
        /** ml features this frame (0 if no update) */
        ml_count= 0;
        /** Global handrail pose */
        if (hr_global_pose != null) {
            hr_global_pose.clear();
        }
        /** Mahalanobis distances for features */
        if (ml_mahal_dists != null) {
            ml_mahal_dists.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if (!super.equals(o)) {
            return false;
        }

        if(getClass() != o.getClass()) {
            return false;
        }

        EkfState otherObj = (EkfState)o;

        /** robot body pose */
        if(!pose.equals(otherObj.pose)) {
            return false;
        }
        /** the body velocity (m/s) */
        if(!velocity.equals(otherObj.velocity)) {
            return false;
        }
        /** body rotational velocity (rad/s) */
        if(!omega.equals(otherObj.omega)) {
            return false;
        }
        /** estimated gyro bias (rad/s) */
        if(!gyro_bias.equals(otherObj.gyro_bias)) {
            return false;
        }
        /** acceleration in body frame (m/s/s) */
        if(!accel.equals(otherObj.accel)) {
            return false;
        }
        /** estimated accel bias (m/s/s) */
        if(!accel_bias.equals(otherObj.accel_bias)) {
            return false;
        }
        /** Filter Health - covariance diagonal.
        * 1-3 orientation, 4-6 gyro bias, 7-9 velocity,
        * 10-12 accel bias, 13-15 position
        */
        if(!cov_diag.equals(otherObj.cov_diag)) {
            return false;
        }
        /** confidence in EKF, 0 is good, 1 is a bit
        * confused, 2 is lost
        */
        if(confidence != otherObj.confidence) {
            return false;
        }
        /** Status byte sent by GNC */
        if(status != otherObj.status) {
            return false;
        }
        /** optical flow features this frame
        * (0 if no update)
        */
        if(of_count != otherObj.of_count) {
            return false;
        }
        /** ml features this frame (0 if no update) */
        if(ml_count != otherObj.ml_count) {
            return false;
        }
        /** Global handrail pose */
        if(!hr_global_pose.equals(otherObj.hr_global_pose)) {
            return false;
        }
        /** Mahalanobis distances for features */
        if(!ml_mahal_dists.equals(otherObj.ml_mahal_dists)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** robot body pose */
        __result += pose.hashCode(); 
        /** the body velocity (m/s) */
        __result += velocity.hashCode(); 
        /** body rotational velocity (rad/s) */
        __result += omega.hashCode(); 
        /** estimated gyro bias (rad/s) */
        __result += gyro_bias.hashCode(); 
        /** acceleration in body frame (m/s/s) */
        __result += accel.hashCode(); 
        /** estimated accel bias (m/s/s) */
        __result += accel_bias.hashCode(); 
        /** Filter Health - covariance diagonal.
        * 1-3 orientation, 4-6 gyro bias, 7-9 velocity,
        * 10-12 accel bias, 13-15 position
        */
        __result += cov_diag.hashCode(); 
        /** confidence in EKF, 0 is good, 1 is a bit
        * confused, 2 is lost
        */
        __result += (int)confidence;
        /** Status byte sent by GNC */
        __result += (int)status;
        /** optical flow features this frame
        * (0 if no update)
        */
        __result += (int)of_count;
        /** ml features this frame (0 if no update) */
        __result += (int)ml_count;
        /** Global handrail pose */
        __result += hr_global_pose.hashCode(); 
        /** Mahalanobis distances for features */
        __result += ml_mahal_dists.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>EkfStateTypeSupport</code>
    * rather than here by using the <code>-noCopyable</code> option
    * to rtiddsgen.
    * 
    * @param src The Object which contains the data to be copied.
    * @return Returns <code>this</code>.
    * @exception NullPointerException If <code>src</code> is null.
    * @exception ClassCastException If <code>src</code> is not the 
    * same type as <code>this</code>.
    * @see com.rti.dds.infrastructure.Copyable#copy_from(java.lang.Object)
    */
    public Object copy_from(Object src) {

        EkfState typedSrc = (EkfState) src;
        EkfState typedDst = this;
        super.copy_from(typedSrc);
        /** robot body pose */
        typedDst.pose = (rapid.Transform3D) typedDst.pose.copy_from(typedSrc.pose);
        /** the body velocity (m/s) */
        typedDst.velocity = (rapid.Vec3d) typedDst.velocity.copy_from(typedSrc.velocity);
        /** body rotational velocity (rad/s) */
        typedDst.omega = (rapid.Vec3d) typedDst.omega.copy_from(typedSrc.omega);
        /** estimated gyro bias (rad/s) */
        typedDst.gyro_bias = (rapid.Vec3d) typedDst.gyro_bias.copy_from(typedSrc.gyro_bias);
        /** acceleration in body frame (m/s/s) */
        typedDst.accel = (rapid.Vec3d) typedDst.accel.copy_from(typedSrc.accel);
        /** estimated accel bias (m/s/s) */
        typedDst.accel_bias = (rapid.Vec3d) typedDst.accel_bias.copy_from(typedSrc.accel_bias);
        /** Filter Health - covariance diagonal.
        * 1-3 orientation, 4-6 gyro bias, 7-9 velocity,
        * 10-12 accel bias, 13-15 position
        */
        typedDst.cov_diag = (rapid.ext.astrobee.FloatSequence15) typedDst.cov_diag.copy_from(typedSrc.cov_diag);
        /** confidence in EKF, 0 is good, 1 is a bit
        * confused, 2 is lost
        */
        typedDst.confidence = typedSrc.confidence;
        /** Status byte sent by GNC */
        typedDst.status = typedSrc.status;
        /** optical flow features this frame
        * (0 if no update)
        */
        typedDst.of_count = typedSrc.of_count;
        /** ml features this frame (0 if no update) */
        typedDst.ml_count = typedSrc.ml_count;
        /** Global handrail pose */
        typedDst.hr_global_pose = (rapid.Transform3D) typedDst.hr_global_pose.copy_from(typedSrc.hr_global_pose);
        /** Mahalanobis distances for features */
        typedDst.ml_mahal_dists = (rapid.ext.astrobee.FloatSequence50) typedDst.ml_mahal_dists.copy_from(typedSrc.ml_mahal_dists);

        return this;
    }

    public String toString(){
        return toString("", 0);
    }

    public String toString(String desc, int indent) {
        StringBuffer strBuffer = new StringBuffer();        

        if (desc != null) {
            CdrHelper.printIndent(strBuffer, indent);
            strBuffer.append(desc).append(":\n");
        }

        strBuffer.append(super.toString("",indent));

        /** robot body pose */
        strBuffer.append(pose.toString("pose ", indent+1));
        /** the body velocity (m/s) */
        strBuffer.append(velocity.toString("velocity ", indent+1));
        /** body rotational velocity (rad/s) */
        strBuffer.append(omega.toString("omega ", indent+1));
        /** estimated gyro bias (rad/s) */
        strBuffer.append(gyro_bias.toString("gyro_bias ", indent+1));
        /** acceleration in body frame (m/s/s) */
        strBuffer.append(accel.toString("accel ", indent+1));
        /** estimated accel bias (m/s/s) */
        strBuffer.append(accel_bias.toString("accel_bias ", indent+1));
        /** Filter Health - covariance diagonal.
        * 1-3 orientation, 4-6 gyro bias, 7-9 velocity,
        * 10-12 accel bias, 13-15 position
        */
        strBuffer.append(cov_diag.toString("cov_diag ", indent+1));
        /** confidence in EKF, 0 is good, 1 is a bit
        * confused, 2 is lost
        */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("confidence: ").append(confidence).append("\n");  
        /** Status byte sent by GNC */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("status: ").append(status).append("\n");  
        /** optical flow features this frame
        * (0 if no update)
        */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("of_count: ").append(of_count).append("\n");  
        /** ml features this frame (0 if no update) */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("ml_count: ").append(ml_count).append("\n");  
        /** Global handrail pose */
        strBuffer.append(hr_global_pose.toString("hr_global_pose ", indent+1));
        /** Mahalanobis distances for features */
        strBuffer.append(ml_mahal_dists.toString("ml_mahal_dists ", indent+1));

        return strBuffer.toString();
    }

}
