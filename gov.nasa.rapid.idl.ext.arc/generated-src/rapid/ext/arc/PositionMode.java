

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.arc;

import com.rti.dds.util.Enum;
import com.rti.dds.cdr.CdrHelper;
import java.util.Arrays;
import java.io.ObjectStreamException;

public class PositionMode  extends Enum {

    public static final PositionMode POS_ABS = new PositionMode("POS_ABS", 0);
    public static final int _POS_ABS = 0;
    public static final PositionMode POS_REL = new PositionMode("POS_REL", 1);
    public static final int _POS_REL = 1;
    public static final PositionMode POS_NA = new PositionMode("POS_NA", 2);
    public static final int _POS_NA = 2;
    public static PositionMode valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return PositionMode.POS_ABS;
            case 1: return PositionMode.POS_REL;
            case 2: return PositionMode.POS_NA;

        }
        return null;
    }

    public static PositionMode from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[3];

        values[i] = POS_ABS.ordinal();
        i++;
        values[i] = POS_REL.ordinal();
        i++;
        values[i] = POS_NA.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static PositionMode create() {

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

    private PositionMode(String name, int ordinal) {
        super(name, ordinal);
    }
}

