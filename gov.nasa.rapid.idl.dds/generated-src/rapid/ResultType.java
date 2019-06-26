

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
/**
* ResultType describes the result of a command on the completed queue.
* <ul>
*   <li>Q_RESULT_INIT:
*   <li>Q_RESULT_SUCCEEDED:
*   <li>Q_RESULT_FAILED:
* </ul>
*/

public class ResultType  extends Enum {

    public static final ResultType Q_RESULT_INIT = new ResultType("Q_RESULT_INIT", 0);
    public static final int _Q_RESULT_INIT = 0;
    public static final ResultType Q_RESULT_SUCCEEDED = new ResultType("Q_RESULT_SUCCEEDED", 1);
    public static final int _Q_RESULT_SUCCEEDED = 1;
    public static final ResultType Q_RESULT_FAILED = new ResultType("Q_RESULT_FAILED", 2);
    public static final int _Q_RESULT_FAILED = 2;
    public static ResultType valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return ResultType.Q_RESULT_INIT;
            case 1: return ResultType.Q_RESULT_SUCCEEDED;
            case 2: return ResultType.Q_RESULT_FAILED;

        }
        return null;
    }

    public static ResultType from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[3];

        values[i] = Q_RESULT_INIT.ordinal();
        i++;
        values[i] = Q_RESULT_SUCCEEDED.ordinal();
        i++;
        values[i] = Q_RESULT_FAILED.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static ResultType create() {

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

    private ResultType(String name, int ordinal) {
        super(name, ordinal);
    }
}

