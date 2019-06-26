

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid;

import com.rti.dds.util.Enum;
import com.rti.dds.cdr.CdrHelper;
import java.util.Arrays;
import java.io.ObjectStreamException;

/**
* Status of the command paired with this Ack.
* <ul>
*   <li>ACK_QUEUED: Sent by Sequencer when queueing Tasks.
*   <li>ACK_EXECUTING: Command has been started by the Bridge.
*   <li>ACK_REQUEUED: Only sent when a command in the ActiveQueue and a PAUSE is sent;
*                     command is REQUEUED in the PendingQueue preempting the next command
*                     in the PendingQueue
*   <li>ACK_COMPLETED: Sent when command is done.
* </ul>
*/

public class AckStatus  extends Enum {

    public static final AckStatus ACK_QUEUED = new AckStatus("ACK_QUEUED", 0);
    public static final int _ACK_QUEUED = 0;
    public static final AckStatus ACK_EXECUTING = new AckStatus("ACK_EXECUTING", 1);
    public static final int _ACK_EXECUTING = 1;
    public static final AckStatus ACK_REQUEUED = new AckStatus("ACK_REQUEUED", 2);
    public static final int _ACK_REQUEUED = 2;
    public static final AckStatus ACK_COMPLETED = new AckStatus("ACK_COMPLETED", 3);
    public static final int _ACK_COMPLETED = 3;
    public static AckStatus valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return AckStatus.ACK_QUEUED;
            case 1: return AckStatus.ACK_EXECUTING;
            case 2: return AckStatus.ACK_REQUEUED;
            case 3: return AckStatus.ACK_COMPLETED;

        }
        return null;
    }

    public static AckStatus from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[4];

        values[i] = ACK_QUEUED.ordinal();
        i++;
        values[i] = ACK_EXECUTING.ordinal();
        i++;
        values[i] = ACK_REQUEUED.ordinal();
        i++;
        values[i] = ACK_COMPLETED.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static AckStatus create() {

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

    private AckStatus(String name, int ordinal) {
        super(name, ordinal);
    }
}

