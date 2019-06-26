

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
* Importance level of the message.
* <ul>
*   <li>MSG_DEBUG:
*   <li>MSG_INFO:
*   <li>MSG_ATTENTION: Important, but not in a bad way.
*   <li>MSG_WARN: Something potentially bad happened.
*   <li>MSG_ERROR: Something bad definitely happened.
*   <li>MSG_CRITICAL: Something really bad happened.
* </ul>
*/

public class MessageLevel  extends Enum {

    public static final MessageLevel MSG_DEBUG = new MessageLevel("MSG_DEBUG", 0);
    public static final int _MSG_DEBUG = 0;
    public static final MessageLevel MSG_INFO = new MessageLevel("MSG_INFO", 1);
    public static final int _MSG_INFO = 1;
    public static final MessageLevel MSG_ATTENTION = new MessageLevel("MSG_ATTENTION", 2);
    public static final int _MSG_ATTENTION = 2;
    public static final MessageLevel MSG_WARN = new MessageLevel("MSG_WARN", 3);
    public static final int _MSG_WARN = 3;
    public static final MessageLevel MSG_ERROR = new MessageLevel("MSG_ERROR", 4);
    public static final int _MSG_ERROR = 4;
    public static final MessageLevel MSG_CRITICAL = new MessageLevel("MSG_CRITICAL", 5);
    public static final int _MSG_CRITICAL = 5;
    public static MessageLevel valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return MessageLevel.MSG_DEBUG;
            case 1: return MessageLevel.MSG_INFO;
            case 2: return MessageLevel.MSG_ATTENTION;
            case 3: return MessageLevel.MSG_WARN;
            case 4: return MessageLevel.MSG_ERROR;
            case 5: return MessageLevel.MSG_CRITICAL;

        }
        return null;
    }

    public static MessageLevel from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[6];

        values[i] = MSG_DEBUG.ordinal();
        i++;
        values[i] = MSG_INFO.ordinal();
        i++;
        values[i] = MSG_ATTENTION.ordinal();
        i++;
        values[i] = MSG_WARN.ordinal();
        i++;
        values[i] = MSG_ERROR.ordinal();
        i++;
        values[i] = MSG_CRITICAL.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static MessageLevel create() {

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

    private MessageLevel(String name, int ordinal) {
        super(name, ordinal);
    }
}

