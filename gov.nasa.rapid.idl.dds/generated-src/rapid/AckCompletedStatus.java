

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
/**
* This status is sent along when it has completed
* <ul>
*   <li>ACK_COMPLETED_NOT: Task has not completed yet.
*   <li>ACK_COMPLETED_OK: Successful completion.
*   <li>ACK_COMPLETED_BAD_SYNTAX: Command not recognized, bad parameters, etc.
*   <li>ACK_COMPLETED_EXEC_FAILED: Failed to complete.
*   <li>ACK_COMPLETED_CANCELED: Canceled from queue.
* </ul>
*/

public class AckCompletedStatus  extends Enum {

    public static final AckCompletedStatus ACK_COMPLETED_NOT = new AckCompletedStatus("ACK_COMPLETED_NOT", 0);
    public static final int _ACK_COMPLETED_NOT = 0;
    public static final AckCompletedStatus ACK_COMPLETED_OK = new AckCompletedStatus("ACK_COMPLETED_OK", 1);
    public static final int _ACK_COMPLETED_OK = 1;
    public static final AckCompletedStatus ACK_COMPLETED_BAD_SYNTAX = new AckCompletedStatus("ACK_COMPLETED_BAD_SYNTAX", 2);
    public static final int _ACK_COMPLETED_BAD_SYNTAX = 2;
    public static final AckCompletedStatus ACK_COMPLETED_EXEC_FAILED = new AckCompletedStatus("ACK_COMPLETED_EXEC_FAILED", 3);
    public static final int _ACK_COMPLETED_EXEC_FAILED = 3;
    public static final AckCompletedStatus ACK_COMPLETED_CANCELED = new AckCompletedStatus("ACK_COMPLETED_CANCELED", 4);
    public static final int _ACK_COMPLETED_CANCELED = 4;
    public static AckCompletedStatus valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return AckCompletedStatus.ACK_COMPLETED_NOT;
            case 1: return AckCompletedStatus.ACK_COMPLETED_OK;
            case 2: return AckCompletedStatus.ACK_COMPLETED_BAD_SYNTAX;
            case 3: return AckCompletedStatus.ACK_COMPLETED_EXEC_FAILED;
            case 4: return AckCompletedStatus.ACK_COMPLETED_CANCELED;

        }
        return null;
    }

    public static AckCompletedStatus from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[5];

        values[i] = ACK_COMPLETED_NOT.ordinal();
        i++;
        values[i] = ACK_COMPLETED_OK.ordinal();
        i++;
        values[i] = ACK_COMPLETED_BAD_SYNTAX.ordinal();
        i++;
        values[i] = ACK_COMPLETED_EXEC_FAILED.ordinal();
        i++;
        values[i] = ACK_COMPLETED_CANCELED.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static AckCompletedStatus create() {

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

    private AckCompletedStatus(String name, int ordinal) {
        super(name, ordinal);
    }
}

