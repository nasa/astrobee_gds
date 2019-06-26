

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.astrobee;

import com.rti.dds.util.Enum;
import com.rti.dds.cdr.CdrHelper;
import java.util.Arrays;
import java.io.ObjectStreamException;

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

public class ArmGripperState  extends Enum {

    public static final ArmGripperState ARM_GRIPPER_STATE_UNKNOWN = new ArmGripperState("ARM_GRIPPER_STATE_UNKNOWN", 0);
    public static final int _ARM_GRIPPER_STATE_UNKNOWN = 0;
    public static final ArmGripperState ARM_GRIPPER_STATE_UNCALIBRATED = new ArmGripperState("ARM_GRIPPER_STATE_UNCALIBRATED", 1);
    public static final int _ARM_GRIPPER_STATE_UNCALIBRATED = 1;
    public static final ArmGripperState ARM_GRIPPER_STATE_CALIBRATING = new ArmGripperState("ARM_GRIPPER_STATE_CALIBRATING", 2);
    public static final int _ARM_GRIPPER_STATE_CALIBRATING = 2;
    public static final ArmGripperState ARM_GRIPPER_STATE_CLOSED = new ArmGripperState("ARM_GRIPPER_STATE_CLOSED", 3);
    public static final int _ARM_GRIPPER_STATE_CLOSED = 3;
    public static final ArmGripperState ARM_GRIPPER_STATE_OPEN = new ArmGripperState("ARM_GRIPPER_STATE_OPEN", 4);
    public static final int _ARM_GRIPPER_STATE_OPEN = 4;
    public static ArmGripperState valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return ArmGripperState.ARM_GRIPPER_STATE_UNKNOWN;
            case 1: return ArmGripperState.ARM_GRIPPER_STATE_UNCALIBRATED;
            case 2: return ArmGripperState.ARM_GRIPPER_STATE_CALIBRATING;
            case 3: return ArmGripperState.ARM_GRIPPER_STATE_CLOSED;
            case 4: return ArmGripperState.ARM_GRIPPER_STATE_OPEN;

        }
        return null;
    }

    public static ArmGripperState from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[5];

        values[i] = ARM_GRIPPER_STATE_UNKNOWN.ordinal();
        i++;
        values[i] = ARM_GRIPPER_STATE_UNCALIBRATED.ordinal();
        i++;
        values[i] = ARM_GRIPPER_STATE_CALIBRATING.ordinal();
        i++;
        values[i] = ARM_GRIPPER_STATE_CLOSED.ordinal();
        i++;
        values[i] = ARM_GRIPPER_STATE_OPEN.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static ArmGripperState create() {

        return valueOf(0);
    }

    /**
    * Print Method
    */     
    public String toString(String desc, int indent) {
        StringBuffer strBuffer = new StringBuffer();

        CdrHelper.printIndent(strBuffer, indent);

        if (desc != null) {
            strBuffer.append(desc).append(": ");
        }

        strBuffer.append(this);
        strBuffer.append("\n");              
        return strBuffer.toString();
    }

    private Object readResolve() throws ObjectStreamException {
        return valueOf(ordinal());
    }

    private ArmGripperState(String name, int ordinal) {
        super(name, ordinal);
    }
}

