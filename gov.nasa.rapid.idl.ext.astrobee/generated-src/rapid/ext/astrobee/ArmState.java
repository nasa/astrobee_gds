

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
* The joint state of Astrobee's arm
* <ul>
*   <li>ARM_JOINT_STATE_UNKNOWN: The arm doesn't know what state it is in
*   <li>ARM_JOINT_STATE_STOWED: The arm is not deployed (thus not moving)
*   <li>ARM_JOINT_STATE_DEPLOYING: The arm is deploying itself
*   <li>ARM_JOINT_STATE_STOPPED: The arm is deployed, but not moving
*   <li>ARM_JOINT_STATE_MOVING: The arm is deployed and moving
*   <li>ARM_JOINT_STATE_STOWING: The arm is stowing itself
* </ul>
*/
/**
* The state of Astrobee's arm's gripper
* <ul>
*   <li>ARM_GRIPPER_STATE_UNKNOWN: Astrobee's gripper doesn't know what state it is in
*   <li>ARM_GRIPPER_STATE_UNCALIBRATED: Astrobee's gripper isn't calibrated and thus doesn't know what state it is in
*   <li>ARM_GRIPPER_STATE_CALIBRATING: Astrobee's gripper is going through a calibration sequence.
*   <li>ARM_GRIPPER_STATE_CLOSED: Astrobee's lil' fingers are clenched tight
*   <li>ARM_GRIPPER_STATE_OPEN: Astrobee's lil' fingers are wide open
* </ul>
*/
/**
* The state of Astrobee's arm
*/

public class ArmState  extends rapid.Message implements Copyable, Serializable{

    /** @see ArmJointState */
    public rapid.ext.astrobee.ArmJointState jointState = (rapid.ext.astrobee.ArmJointState)rapid.ext.astrobee.ArmJointState.create();
    /** @see ArmGripperState */
    public rapid.ext.astrobee.ArmGripperState gripperState = (rapid.ext.astrobee.ArmGripperState)rapid.ext.astrobee.ArmGripperState.create();

    public ArmState() {

        super();

        /** @see ArmJointState */
        /** @see ArmGripperState */

    }
    public ArmState (ArmState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        ArmState self;
        self = new  ArmState();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** @see ArmJointState */
        jointState = rapid.ext.astrobee.ArmJointState.create();
        /** @see ArmGripperState */
        gripperState = rapid.ext.astrobee.ArmGripperState.create();
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

        ArmState otherObj = (ArmState)o;

        /** @see ArmJointState */
        if(!jointState.equals(otherObj.jointState)) {
            return false;
        }
        /** @see ArmGripperState */
        if(!gripperState.equals(otherObj.gripperState)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** @see ArmJointState */
        __result += jointState.hashCode(); 
        /** @see ArmGripperState */
        __result += gripperState.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>ArmStateTypeSupport</code>
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

        ArmState typedSrc = (ArmState) src;
        ArmState typedDst = this;
        super.copy_from(typedSrc);
        /** @see ArmJointState */
        typedDst.jointState = (rapid.ext.astrobee.ArmJointState) typedDst.jointState.copy_from(typedSrc.jointState);
        /** @see ArmGripperState */
        typedDst.gripperState = (rapid.ext.astrobee.ArmGripperState) typedDst.gripperState.copy_from(typedSrc.gripperState);

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

        /** @see ArmJointState */
        strBuffer.append(jointState.toString("jointState ", indent+1));
        /** @see ArmGripperState */
        strBuffer.append(gripperState.toString("gripperState ", indent+1));

        return strBuffer.toString();
    }

}
