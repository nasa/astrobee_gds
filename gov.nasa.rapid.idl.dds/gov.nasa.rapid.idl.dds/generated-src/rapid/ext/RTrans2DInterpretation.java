

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

/**
* Specifies how to interpret the sequence of transforms
* <ul>
*   <li>RTRANS2D_RELATIVE_TO_ORIGIN: transforms are all offsets from the origin
*   <li>RTRANS2D_RELATIVE_TO_PREVIOUS: transform is relative to the previous transform; the first transform is relative to the origin
* </ul>
*/

public class RTrans2DInterpretation  extends Enum {

    public static final RTrans2DInterpretation RTRANS2D_RELATIVE_TO_ORIGIN = new RTrans2DInterpretation("RTRANS2D_RELATIVE_TO_ORIGIN", 0);
    public static final int _RTRANS2D_RELATIVE_TO_ORIGIN = 0;
    public static final RTrans2DInterpretation RTRANS2D_RELATIVE_TO_PREVIOUS = new RTrans2DInterpretation("RTRANS2D_RELATIVE_TO_PREVIOUS", 1);
    public static final int _RTRANS2D_RELATIVE_TO_PREVIOUS = 1;
    public static RTrans2DInterpretation valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return RTrans2DInterpretation.RTRANS2D_RELATIVE_TO_ORIGIN;
            case 1: return RTrans2DInterpretation.RTRANS2D_RELATIVE_TO_PREVIOUS;

        }
        return null;
    }

    public static RTrans2DInterpretation from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[2];

        values[i] = RTRANS2D_RELATIVE_TO_ORIGIN.ordinal();
        i++;
        values[i] = RTRANS2D_RELATIVE_TO_PREVIOUS.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static RTrans2DInterpretation create() {

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

    private RTrans2DInterpretation(String name, int ordinal) {
        super(name, ordinal);
    }
}

