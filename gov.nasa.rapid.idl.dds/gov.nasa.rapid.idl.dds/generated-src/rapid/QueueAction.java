

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
* QueueAction
* <ul>
*   <li> BYPASS: the cmd is acted upon immediately.
*   <li> APPEND: the cmd is appended to the tail of the Pending queue. This is the normal operation for commanding.
*   <li> DELETE: the cmd with the ID specified in targetCmdID is removed from the queue.
*   <li> INSERT: the cmd is inserted immediately following the cmd with the ID specified in targetCmdID.
*		      If targetCmdID is "head", then the cmd is inserted at the head of the queue.
*   <li> REPLACE: cmd replaces the ID specified in targetCmdID. This cmd will have a new ID
* <ul>
*/

public class QueueAction  extends Enum {

    public static final QueueAction QUEUE_BYPASS = new QueueAction("QUEUE_BYPASS", 0);
    public static final int _QUEUE_BYPASS = 0;
    public static final QueueAction QUEUE_APPEND = new QueueAction("QUEUE_APPEND", 1);
    public static final int _QUEUE_APPEND = 1;
    public static final QueueAction QUEUE_DELETE = new QueueAction("QUEUE_DELETE", 2);
    public static final int _QUEUE_DELETE = 2;
    public static final QueueAction QUEUE_INSERT = new QueueAction("QUEUE_INSERT", 3);
    public static final int _QUEUE_INSERT = 3;
    public static final QueueAction QUEUE_REPLACE = new QueueAction("QUEUE_REPLACE", 4);
    public static final int _QUEUE_REPLACE = 4;
    public static QueueAction valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return QueueAction.QUEUE_BYPASS;
            case 1: return QueueAction.QUEUE_APPEND;
            case 2: return QueueAction.QUEUE_DELETE;
            case 3: return QueueAction.QUEUE_INSERT;
            case 4: return QueueAction.QUEUE_REPLACE;

        }
        return null;
    }

    public static QueueAction from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[5];

        values[i] = QUEUE_BYPASS.ordinal();
        i++;
        values[i] = QUEUE_APPEND.ordinal();
        i++;
        values[i] = QUEUE_DELETE.ordinal();
        i++;
        values[i] = QUEUE_INSERT.ordinal();
        i++;
        values[i] = QUEUE_REPLACE.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static QueueAction create() {

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

    private QueueAction(String name, int ordinal) {
        super(name, ordinal);
    }
}

