

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
/**
* Mobility state of Astrobee
* <ul>
*   <li>MOBILITY_STATE_DRIFTING: Robot is floating around in spaaaaace
*   <li>MOBILITY_STATE_STOPPING: Robot is either stopping or stopped
*   <li>MOBILITY_STATE_FLYING: Robot is flying around in spaaaaace
*   <li>MOBILITY_STATE_DOCKING: Robot is either docking, docked, or undocking.
*   <li>MOBILITY_STATE_PERCHING: Robot is either perching, perched, or unperching via the arm.
* </ul>
*/

public class MobilityState  extends Enum {

    public static final MobilityState MOBILITY_STATE_DRIFTING = new MobilityState("MOBILITY_STATE_DRIFTING", 0);
    public static final int _MOBILITY_STATE_DRIFTING = 0;
    public static final MobilityState MOBILITY_STATE_STOPPING = new MobilityState("MOBILITY_STATE_STOPPING", 1);
    public static final int _MOBILITY_STATE_STOPPING = 1;
    public static final MobilityState MOBILITY_STATE_FLYING = new MobilityState("MOBILITY_STATE_FLYING", 2);
    public static final int _MOBILITY_STATE_FLYING = 2;
    public static final MobilityState MOBILITY_STATE_DOCKING = new MobilityState("MOBILITY_STATE_DOCKING", 3);
    public static final int _MOBILITY_STATE_DOCKING = 3;
    public static final MobilityState MOBILITY_STATE_PERCHING = new MobilityState("MOBILITY_STATE_PERCHING", 4);
    public static final int _MOBILITY_STATE_PERCHING = 4;
    public static MobilityState valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return MobilityState.MOBILITY_STATE_DRIFTING;
            case 1: return MobilityState.MOBILITY_STATE_STOPPING;
            case 2: return MobilityState.MOBILITY_STATE_FLYING;
            case 3: return MobilityState.MOBILITY_STATE_DOCKING;
            case 4: return MobilityState.MOBILITY_STATE_PERCHING;

        }
        return null;
    }

    public static MobilityState from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[5];

        values[i] = MOBILITY_STATE_DRIFTING.ordinal();
        i++;
        values[i] = MOBILITY_STATE_STOPPING.ordinal();
        i++;
        values[i] = MOBILITY_STATE_FLYING.ordinal();
        i++;
        values[i] = MOBILITY_STATE_DOCKING.ordinal();
        i++;
        values[i] = MOBILITY_STATE_PERCHING.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static MobilityState create() {

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

    private MobilityState(String name, int ordinal) {
        super(name, ordinal);
    }
}

