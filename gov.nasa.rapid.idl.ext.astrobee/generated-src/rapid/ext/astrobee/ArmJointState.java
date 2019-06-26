

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

public class ArmJointState  extends Enum {

    public static final ArmJointState ARM_JOINT_STATE_UNKNOWN = new ArmJointState("ARM_JOINT_STATE_UNKNOWN", 0);
    public static final int _ARM_JOINT_STATE_UNKNOWN = 0;
    public static final ArmJointState ARM_JOINT_STATE_STOWED = new ArmJointState("ARM_JOINT_STATE_STOWED", 1);
    public static final int _ARM_JOINT_STATE_STOWED = 1;
    public static final ArmJointState ARM_JOINT_STATE_DEPLOYING = new ArmJointState("ARM_JOINT_STATE_DEPLOYING", 2);
    public static final int _ARM_JOINT_STATE_DEPLOYING = 2;
    public static final ArmJointState ARM_JOINT_STATE_STOPPED = new ArmJointState("ARM_JOINT_STATE_STOPPED", 3);
    public static final int _ARM_JOINT_STATE_STOPPED = 3;
    public static final ArmJointState ARM_JOINT_STATE_MOVING = new ArmJointState("ARM_JOINT_STATE_MOVING", 4);
    public static final int _ARM_JOINT_STATE_MOVING = 4;
    public static final ArmJointState ARM_JOINT_STATE_STOWING = new ArmJointState("ARM_JOINT_STATE_STOWING", 5);
    public static final int _ARM_JOINT_STATE_STOWING = 5;
    public static ArmJointState valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return ArmJointState.ARM_JOINT_STATE_UNKNOWN;
            case 1: return ArmJointState.ARM_JOINT_STATE_STOWED;
            case 2: return ArmJointState.ARM_JOINT_STATE_DEPLOYING;
            case 3: return ArmJointState.ARM_JOINT_STATE_STOPPED;
            case 4: return ArmJointState.ARM_JOINT_STATE_MOVING;
            case 5: return ArmJointState.ARM_JOINT_STATE_STOWING;

        }
        return null;
    }

    public static ArmJointState from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[6];

        values[i] = ARM_JOINT_STATE_UNKNOWN.ordinal();
        i++;
        values[i] = ARM_JOINT_STATE_STOWED.ordinal();
        i++;
        values[i] = ARM_JOINT_STATE_DEPLOYING.ordinal();
        i++;
        values[i] = ARM_JOINT_STATE_STOPPED.ordinal();
        i++;
        values[i] = ARM_JOINT_STATE_MOVING.ordinal();
        i++;
        values[i] = ARM_JOINT_STATE_STOWING.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static ArmJointState create() {

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

    private ArmJointState(String name, int ordinal) {
        super(name, ordinal);
    }
}

