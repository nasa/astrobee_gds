

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext;

import com.rti.dds.util.Enum;
import com.rti.dds.cdr.CdrHelper;
import java.util.Arrays;
import java.io.ObjectStreamException;

public class ProcessStatus  extends Enum {

    public static final ProcessStatus PROCESS_STATE_UNAVAIL = new ProcessStatus("PROCESS_STATE_UNAVAIL", 0);
    public static final int _PROCESS_STATE_UNAVAIL = 0;
    public static final ProcessStatus PROCESS_STATE_STARTING = new ProcessStatus("PROCESS_STATE_STARTING", 1);
    public static final int _PROCESS_STATE_STARTING = 1;
    public static final ProcessStatus PROCESS_STATE_RESTARTING = new ProcessStatus("PROCESS_STATE_RESTARTING", 2);
    public static final int _PROCESS_STATE_RESTARTING = 2;
    public static final ProcessStatus PROCESS_STATE_RUNNING = new ProcessStatus("PROCESS_STATE_RUNNING", 3);
    public static final int _PROCESS_STATE_RUNNING = 3;
    public static final ProcessStatus PROCESS_STATE_STOPPING = new ProcessStatus("PROCESS_STATE_STOPPING", 4);
    public static final int _PROCESS_STATE_STOPPING = 4;
    public static final ProcessStatus PROCESS_STATE_STOPPED = new ProcessStatus("PROCESS_STATE_STOPPED", 5);
    public static final int _PROCESS_STATE_STOPPED = 5;
    public static final ProcessStatus PROCESS_STATE_STOPPED_UNEXPECTEDLY = new ProcessStatus("PROCESS_STATE_STOPPED_UNEXPECTEDLY", 6);
    public static final int _PROCESS_STATE_STOPPED_UNEXPECTEDLY = 6;
    public static final ProcessStatus PROCESS_STATE_COMPLETED = new ProcessStatus("PROCESS_STATE_COMPLETED", 7);
    public static final int _PROCESS_STATE_COMPLETED = 7;
    public static final ProcessStatus PROCESS_STATE_KILLED = new ProcessStatus("PROCESS_STATE_KILLED", 8);
    public static final int _PROCESS_STATE_KILLED = 8;
    public static ProcessStatus valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return ProcessStatus.PROCESS_STATE_UNAVAIL;
            case 1: return ProcessStatus.PROCESS_STATE_STARTING;
            case 2: return ProcessStatus.PROCESS_STATE_RESTARTING;
            case 3: return ProcessStatus.PROCESS_STATE_RUNNING;
            case 4: return ProcessStatus.PROCESS_STATE_STOPPING;
            case 5: return ProcessStatus.PROCESS_STATE_STOPPED;
            case 6: return ProcessStatus.PROCESS_STATE_STOPPED_UNEXPECTEDLY;
            case 7: return ProcessStatus.PROCESS_STATE_COMPLETED;
            case 8: return ProcessStatus.PROCESS_STATE_KILLED;

        }
        return null;
    }

    public static ProcessStatus from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[9];

        values[i] = PROCESS_STATE_UNAVAIL.ordinal();
        i++;
        values[i] = PROCESS_STATE_STARTING.ordinal();
        i++;
        values[i] = PROCESS_STATE_RESTARTING.ordinal();
        i++;
        values[i] = PROCESS_STATE_RUNNING.ordinal();
        i++;
        values[i] = PROCESS_STATE_STOPPING.ordinal();
        i++;
        values[i] = PROCESS_STATE_STOPPED.ordinal();
        i++;
        values[i] = PROCESS_STATE_STOPPED_UNEXPECTEDLY.ordinal();
        i++;
        values[i] = PROCESS_STATE_COMPLETED.ordinal();
        i++;
        values[i] = PROCESS_STATE_KILLED.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static ProcessStatus create() {

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

    private ProcessStatus(String name, int ordinal) {
        super(name, ordinal);
    }
}

