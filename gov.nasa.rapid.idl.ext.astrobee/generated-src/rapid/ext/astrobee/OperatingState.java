

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
* Operating state of Astrobee
* <ul>
*   <li>OPERATING_STATE_READY: Robot is ready to take commands.
*   <li>OPERATING_STATE_FAULT: Robot is executing a fault response
*   <li>OPERATING_STATE_PLAN_EXECUTION: Robot is executing a loaded plan
*   <li>OPERATING_STATE_TELEOPERATION: Robot is executing a teleop command
*   <li>OPERATING_STATE_AUTO_RETURN: Robot returns to dock
*   <li>OPERATING_STATE_GUEST_SCIENCE: Guest science has control.
* </ul>
*/

public class OperatingState  extends Enum {

    public static final OperatingState OPERATING_STATE_READY = new OperatingState("OPERATING_STATE_READY", 0);
    public static final int _OPERATING_STATE_READY = 0;
    public static final OperatingState OPERATING_STATE_PLAN_EXECUTION = new OperatingState("OPERATING_STATE_PLAN_EXECUTION", 1);
    public static final int _OPERATING_STATE_PLAN_EXECUTION = 1;
    public static final OperatingState OPERATING_STATE_TELEOPERATION = new OperatingState("OPERATING_STATE_TELEOPERATION", 2);
    public static final int _OPERATING_STATE_TELEOPERATION = 2;
    public static final OperatingState OPERATING_STATE_AUTO_RETURN = new OperatingState("OPERATING_STATE_AUTO_RETURN", 3);
    public static final int _OPERATING_STATE_AUTO_RETURN = 3;
    public static final OperatingState OPERATING_STATE_FAULT = new OperatingState("OPERATING_STATE_FAULT", 4);
    public static final int _OPERATING_STATE_FAULT = 4;
    public static OperatingState valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return OperatingState.OPERATING_STATE_READY;
            case 1: return OperatingState.OPERATING_STATE_PLAN_EXECUTION;
            case 2: return OperatingState.OPERATING_STATE_TELEOPERATION;
            case 3: return OperatingState.OPERATING_STATE_AUTO_RETURN;
            case 4: return OperatingState.OPERATING_STATE_FAULT;

        }
        return null;
    }

    public static OperatingState from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[5];

        values[i] = OPERATING_STATE_READY.ordinal();
        i++;
        values[i] = OPERATING_STATE_PLAN_EXECUTION.ordinal();
        i++;
        values[i] = OPERATING_STATE_TELEOPERATION.ordinal();
        i++;
        values[i] = OPERATING_STATE_AUTO_RETURN.ordinal();
        i++;
        values[i] = OPERATING_STATE_FAULT.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static OperatingState create() {

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

    private OperatingState(String name, int ordinal) {
        super(name, ordinal);
    }
}

