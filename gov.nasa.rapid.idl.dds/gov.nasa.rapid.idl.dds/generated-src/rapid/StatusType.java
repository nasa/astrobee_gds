

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
* <ul>
*   <li>Q_STATUS_INIT:
*   <li>Q_STATUS_OUTBOUND: SENT
*   <li>Q_STATUS_LOST: SENT
*   <li>Q_STATUS_INLINE: PENDING
*   <li>Q_STATUS_PREEMPTED: PENDING
*   <li>Q_STATUS_INPROCESS: ACTIVE
*   <li>Q_STATUS_PAUSED: ACTIVE
*   <li>Q_STATUS_CANCELED: COMPLETED
*   <li>Q_STATUS_SUCCEEDED: COMPLETED
*   <li>Q_STATUS_FAILED: COMPLETED
*   <li>Q_STATUS_ALIEN_PENDING:
*   <li>Q_STATUS_ALIEN_ACTIVE:
*   <li>Q_STATUS_ALIEN_COMPLETED:
*   <li>Q_STATUS_FORGOTTEN_BY_ROBOT:
* </ul>
*/

public class StatusType  extends Enum {

    public static final StatusType Q_STATUS_INIT = new StatusType("Q_STATUS_INIT", 0);
    public static final int _Q_STATUS_INIT = 0;
    public static final StatusType Q_STATUS_OUTBOUND = new StatusType("Q_STATUS_OUTBOUND", 1);
    public static final int _Q_STATUS_OUTBOUND = 1;
    public static final StatusType Q_STATUS_LOST = new StatusType("Q_STATUS_LOST", 2);
    public static final int _Q_STATUS_LOST = 2;
    public static final StatusType Q_STATUS_INLINE = new StatusType("Q_STATUS_INLINE", 3);
    public static final int _Q_STATUS_INLINE = 3;
    public static final StatusType Q_STATUS_PREEMPTED = new StatusType("Q_STATUS_PREEMPTED", 4);
    public static final int _Q_STATUS_PREEMPTED = 4;
    public static final StatusType Q_STATUS_INPROCESS = new StatusType("Q_STATUS_INPROCESS", 5);
    public static final int _Q_STATUS_INPROCESS = 5;
    public static final StatusType Q_STATUS_PAUSED = new StatusType("Q_STATUS_PAUSED", 6);
    public static final int _Q_STATUS_PAUSED = 6;
    public static final StatusType Q_STATUS_CANCELED = new StatusType("Q_STATUS_CANCELED", 7);
    public static final int _Q_STATUS_CANCELED = 7;
    public static final StatusType Q_STATUS_SUCCEEDED = new StatusType("Q_STATUS_SUCCEEDED", 8);
    public static final int _Q_STATUS_SUCCEEDED = 8;
    public static final StatusType Q_STATUS_FAILED = new StatusType("Q_STATUS_FAILED", 9);
    public static final int _Q_STATUS_FAILED = 9;
    public static final StatusType Q_STATUS_ALIEN_PENDING = new StatusType("Q_STATUS_ALIEN_PENDING", 10);
    public static final int _Q_STATUS_ALIEN_PENDING = 10;
    public static final StatusType Q_STATUS_ALIEN_ACTIVE = new StatusType("Q_STATUS_ALIEN_ACTIVE", 11);
    public static final int _Q_STATUS_ALIEN_ACTIVE = 11;
    public static final StatusType Q_STATUS_ALIEN_COMPLETED = new StatusType("Q_STATUS_ALIEN_COMPLETED", 12);
    public static final int _Q_STATUS_ALIEN_COMPLETED = 12;
    public static final StatusType Q_STATUS_FORGOTTEN_BY_ROBOT = new StatusType("Q_STATUS_FORGOTTEN_BY_ROBOT", 13);
    public static final int _Q_STATUS_FORGOTTEN_BY_ROBOT = 13;
    public static StatusType valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return StatusType.Q_STATUS_INIT;
            case 1: return StatusType.Q_STATUS_OUTBOUND;
            case 2: return StatusType.Q_STATUS_LOST;
            case 3: return StatusType.Q_STATUS_INLINE;
            case 4: return StatusType.Q_STATUS_PREEMPTED;
            case 5: return StatusType.Q_STATUS_INPROCESS;
            case 6: return StatusType.Q_STATUS_PAUSED;
            case 7: return StatusType.Q_STATUS_CANCELED;
            case 8: return StatusType.Q_STATUS_SUCCEEDED;
            case 9: return StatusType.Q_STATUS_FAILED;
            case 10: return StatusType.Q_STATUS_ALIEN_PENDING;
            case 11: return StatusType.Q_STATUS_ALIEN_ACTIVE;
            case 12: return StatusType.Q_STATUS_ALIEN_COMPLETED;
            case 13: return StatusType.Q_STATUS_FORGOTTEN_BY_ROBOT;

        }
        return null;
    }

    public static StatusType from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[14];

        values[i] = Q_STATUS_INIT.ordinal();
        i++;
        values[i] = Q_STATUS_OUTBOUND.ordinal();
        i++;
        values[i] = Q_STATUS_LOST.ordinal();
        i++;
        values[i] = Q_STATUS_INLINE.ordinal();
        i++;
        values[i] = Q_STATUS_PREEMPTED.ordinal();
        i++;
        values[i] = Q_STATUS_INPROCESS.ordinal();
        i++;
        values[i] = Q_STATUS_PAUSED.ordinal();
        i++;
        values[i] = Q_STATUS_CANCELED.ordinal();
        i++;
        values[i] = Q_STATUS_SUCCEEDED.ordinal();
        i++;
        values[i] = Q_STATUS_FAILED.ordinal();
        i++;
        values[i] = Q_STATUS_ALIEN_PENDING.ordinal();
        i++;
        values[i] = Q_STATUS_ALIEN_ACTIVE.ordinal();
        i++;
        values[i] = Q_STATUS_ALIEN_COMPLETED.ordinal();
        i++;
        values[i] = Q_STATUS_FORGOTTEN_BY_ROBOT.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static StatusType create() {

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

    private StatusType(String name, int ordinal) {
        super(name, ordinal);
    }
}

