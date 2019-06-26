

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
/**
* Executing state of Astrobee
* <ul>
*   <li>EXECUTION_STATE_IDLE: Robot does not have a plan loaded.
*   <li>EXECUTION_STATE_EXECUTING: Robot is executing a plan
*   <li>EXECUTION_STATE_PAUSED: Robot is not executing a plan, but has a plan loaded and ready to resume
*   <li>EXECUTION_STATE_ERROR: Robot was unable to load the plan
* </ul>
*/

public class ExecutionState  extends Enum {

    public static final ExecutionState EXECUTION_STATE_IDLE = new ExecutionState("EXECUTION_STATE_IDLE", 0);
    public static final int _EXECUTION_STATE_IDLE = 0;
    public static final ExecutionState EXECUTION_STATE_EXECUTING = new ExecutionState("EXECUTION_STATE_EXECUTING", 1);
    public static final int _EXECUTION_STATE_EXECUTING = 1;
    public static final ExecutionState EXECUTION_STATE_PAUSED = new ExecutionState("EXECUTION_STATE_PAUSED", 2);
    public static final int _EXECUTION_STATE_PAUSED = 2;
    public static final ExecutionState EXECUTION_STATE_ERROR = new ExecutionState("EXECUTION_STATE_ERROR", 3);
    public static final int _EXECUTION_STATE_ERROR = 3;
    public static ExecutionState valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return ExecutionState.EXECUTION_STATE_IDLE;
            case 1: return ExecutionState.EXECUTION_STATE_EXECUTING;
            case 2: return ExecutionState.EXECUTION_STATE_PAUSED;
            case 3: return ExecutionState.EXECUTION_STATE_ERROR;

        }
        return null;
    }

    public static ExecutionState from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[4];

        values[i] = EXECUTION_STATE_IDLE.ordinal();
        i++;
        values[i] = EXECUTION_STATE_EXECUTING.ordinal();
        i++;
        values[i] = EXECUTION_STATE_PAUSED.ordinal();
        i++;
        values[i] = EXECUTION_STATE_ERROR.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static ExecutionState create() {

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

    private ExecutionState(String name, int ordinal) {
        super(name, ordinal);
    }
}

