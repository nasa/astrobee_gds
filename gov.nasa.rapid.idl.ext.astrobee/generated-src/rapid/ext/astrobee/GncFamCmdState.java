

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
* Represents force in free space.
*/
/**
* Command sent from control to FAM.
*/

public class GncFamCmdState  extends rapid.Message implements Copyable, Serializable{

    /** Force and torque */
    public rapid.ext.astrobee.Wrench wrench = (rapid.ext.astrobee.Wrench)rapid.ext.astrobee.Wrench.create();
    /** Linear acceleration (wrench without estimated
    * mass)
    */
    public rapid.Vec3d accel = (rapid.Vec3d)rapid.Vec3d.create();
    /** Angular acceleration (wrench without estimated
    * mass)
    */
    public rapid.Vec3d alpha = (rapid.Vec3d)rapid.Vec3d.create();
    /** Status byte from GNC ICD */
    public short status= 0;
    /** Position error */
    public rapid.Vec3d position_error = (rapid.Vec3d)rapid.Vec3d.create();
    /** Integrated position error */
    public rapid.Vec3d position_error_integrated = (rapid.Vec3d)rapid.Vec3d.create();
    /** Attitude error */
    public rapid.Vec3d attitude_error = (rapid.Vec3d)rapid.Vec3d.create();
    /** Integrated attitude error */
    public rapid.Vec3d attitude_error_integrated = (rapid.Vec3d)rapid.Vec3d.create();
    /** Magnitude of attitude error */
    public float attitude_error_mag= 0;
    /** Control mode from GNC ICD */
    public short control_mode= 0;

    public GncFamCmdState() {

        super();

        /** Force and torque */
        /** Linear acceleration (wrench without estimated
        * mass)
        */
        /** Angular acceleration (wrench without estimated
        * mass)
        */
        /** Status byte from GNC ICD */
        /** Position error */
        /** Integrated position error */
        /** Attitude error */
        /** Integrated attitude error */
        /** Magnitude of attitude error */
        /** Control mode from GNC ICD */

    }
    public GncFamCmdState (GncFamCmdState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        GncFamCmdState self;
        self = new  GncFamCmdState();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** Force and torque */
        if (wrench != null) {
            wrench.clear();
        }
        /** Linear acceleration (wrench without estimated
        * mass)
        */
        if (accel != null) {
            accel.clear();
        }
        /** Angular acceleration (wrench without estimated
        * mass)
        */
        if (alpha != null) {
            alpha.clear();
        }
        /** Status byte from GNC ICD */
        status= 0;
        /** Position error */
        if (position_error != null) {
            position_error.clear();
        }
        /** Integrated position error */
        if (position_error_integrated != null) {
            position_error_integrated.clear();
        }
        /** Attitude error */
        if (attitude_error != null) {
            attitude_error.clear();
        }
        /** Integrated attitude error */
        if (attitude_error_integrated != null) {
            attitude_error_integrated.clear();
        }
        /** Magnitude of attitude error */
        attitude_error_mag= 0;
        /** Control mode from GNC ICD */
        control_mode= 0;
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

        GncFamCmdState otherObj = (GncFamCmdState)o;

        /** Force and torque */
        if(!wrench.equals(otherObj.wrench)) {
            return false;
        }
        /** Linear acceleration (wrench without estimated
        * mass)
        */
        if(!accel.equals(otherObj.accel)) {
            return false;
        }
        /** Angular acceleration (wrench without estimated
        * mass)
        */
        if(!alpha.equals(otherObj.alpha)) {
            return false;
        }
        /** Status byte from GNC ICD */
        if(status != otherObj.status) {
            return false;
        }
        /** Position error */
        if(!position_error.equals(otherObj.position_error)) {
            return false;
        }
        /** Integrated position error */
        if(!position_error_integrated.equals(otherObj.position_error_integrated)) {
            return false;
        }
        /** Attitude error */
        if(!attitude_error.equals(otherObj.attitude_error)) {
            return false;
        }
        /** Integrated attitude error */
        if(!attitude_error_integrated.equals(otherObj.attitude_error_integrated)) {
            return false;
        }
        /** Magnitude of attitude error */
        if(attitude_error_mag != otherObj.attitude_error_mag) {
            return false;
        }
        /** Control mode from GNC ICD */
        if(control_mode != otherObj.control_mode) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** Force and torque */
        __result += wrench.hashCode(); 
        /** Linear acceleration (wrench without estimated
        * mass)
        */
        __result += accel.hashCode(); 
        /** Angular acceleration (wrench without estimated
        * mass)
        */
        __result += alpha.hashCode(); 
        /** Status byte from GNC ICD */
        __result += (int)status;
        /** Position error */
        __result += position_error.hashCode(); 
        /** Integrated position error */
        __result += position_error_integrated.hashCode(); 
        /** Attitude error */
        __result += attitude_error.hashCode(); 
        /** Integrated attitude error */
        __result += attitude_error_integrated.hashCode(); 
        /** Magnitude of attitude error */
        __result += (int)attitude_error_mag;
        /** Control mode from GNC ICD */
        __result += (int)control_mode;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>GncFamCmdStateTypeSupport</code>
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

        GncFamCmdState typedSrc = (GncFamCmdState) src;
        GncFamCmdState typedDst = this;
        super.copy_from(typedSrc);
        /** Force and torque */
        typedDst.wrench = (rapid.ext.astrobee.Wrench) typedDst.wrench.copy_from(typedSrc.wrench);
        /** Linear acceleration (wrench without estimated
        * mass)
        */
        typedDst.accel = (rapid.Vec3d) typedDst.accel.copy_from(typedSrc.accel);
        /** Angular acceleration (wrench without estimated
        * mass)
        */
        typedDst.alpha = (rapid.Vec3d) typedDst.alpha.copy_from(typedSrc.alpha);
        /** Status byte from GNC ICD */
        typedDst.status = typedSrc.status;
        /** Position error */
        typedDst.position_error = (rapid.Vec3d) typedDst.position_error.copy_from(typedSrc.position_error);
        /** Integrated position error */
        typedDst.position_error_integrated = (rapid.Vec3d) typedDst.position_error_integrated.copy_from(typedSrc.position_error_integrated);
        /** Attitude error */
        typedDst.attitude_error = (rapid.Vec3d) typedDst.attitude_error.copy_from(typedSrc.attitude_error);
        /** Integrated attitude error */
        typedDst.attitude_error_integrated = (rapid.Vec3d) typedDst.attitude_error_integrated.copy_from(typedSrc.attitude_error_integrated);
        /** Magnitude of attitude error */
        typedDst.attitude_error_mag = typedSrc.attitude_error_mag;
        /** Control mode from GNC ICD */
        typedDst.control_mode = typedSrc.control_mode;

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

        /** Force and torque */
        strBuffer.append(wrench.toString("wrench ", indent+1));
        /** Linear acceleration (wrench without estimated
        * mass)
        */
        strBuffer.append(accel.toString("accel ", indent+1));
        /** Angular acceleration (wrench without estimated
        * mass)
        */
        strBuffer.append(alpha.toString("alpha ", indent+1));
        /** Status byte from GNC ICD */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("status: ").append(status).append("\n");  
        /** Position error */
        strBuffer.append(position_error.toString("position_error ", indent+1));
        /** Integrated position error */
        strBuffer.append(position_error_integrated.toString("position_error_integrated ", indent+1));
        /** Attitude error */
        strBuffer.append(attitude_error.toString("attitude_error ", indent+1));
        /** Integrated attitude error */
        strBuffer.append(attitude_error_integrated.toString("attitude_error_integrated ", indent+1));
        /** Magnitude of attitude error */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("attitude_error_mag: ").append(attitude_error_mag).append("\n");  
        /** Control mode from GNC ICD */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("control_mode: ").append(control_mode).append("\n");  

        return strBuffer.toString();
    }

}
