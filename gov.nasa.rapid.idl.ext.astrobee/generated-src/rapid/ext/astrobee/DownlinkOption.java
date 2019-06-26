

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
* DownlinkOption indicates if and when the data in this rostopic is downlinked
* <ul>
*   <li>NONE: topic is not saved to disk
*   <li>IMMEDIATE: topic saved to disk; upon docking it is downlinked
*   <li>DELAYED: topic saved to disk; upon docking it is transferred to ISS server for later downlink
* </ul>
*/

public class DownlinkOption  extends Enum {

    public static final DownlinkOption DATA_IMMEDIATE = new DownlinkOption("DATA_IMMEDIATE", 0);
    public static final int _DATA_IMMEDIATE = 0;
    public static final DownlinkOption DATA_DELAYED = new DownlinkOption("DATA_DELAYED", 1);
    public static final int _DATA_DELAYED = 1;
    public static DownlinkOption valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return DownlinkOption.DATA_IMMEDIATE;
            case 1: return DownlinkOption.DATA_DELAYED;

        }
        return null;
    }

    public static DownlinkOption from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[2];

        values[i] = DATA_IMMEDIATE.ordinal();
        i++;
        values[i] = DATA_DELAYED.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static DownlinkOption create() {

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

    private DownlinkOption(String name, int ordinal) {
        super(name, ordinal);
    }
}

