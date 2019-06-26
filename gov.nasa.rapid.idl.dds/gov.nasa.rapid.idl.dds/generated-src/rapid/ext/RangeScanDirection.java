

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

public class RangeScanDirection  extends Enum {

    public static final RangeScanDirection SCAN_HORIZONTAL = new RangeScanDirection("SCAN_HORIZONTAL", 0);
    public static final int _SCAN_HORIZONTAL = 0;
    public static final RangeScanDirection SCAN_VERTICAL = new RangeScanDirection("SCAN_VERTICAL", 1);
    public static final int _SCAN_VERTICAL = 1;
    public static RangeScanDirection valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return RangeScanDirection.SCAN_HORIZONTAL;
            case 1: return RangeScanDirection.SCAN_VERTICAL;

        }
        return null;
    }

    public static RangeScanDirection from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[2];

        values[i] = SCAN_HORIZONTAL.ordinal();
        i++;
        values[i] = SCAN_VERTICAL.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static RangeScanDirection create() {

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

    private RangeScanDirection(String name, int ordinal) {
        super(name, ordinal);
    }
}

