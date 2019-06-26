

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
* SegmentType describes the type of segment that is being represented.
*/

public class SegmentType  extends Enum {

    public static final SegmentType SEG_STRAIGHT = new SegmentType("SEG_STRAIGHT", 0);
    public static final int _SEG_STRAIGHT = 0;
    public static final SegmentType SEG_ARC = new SegmentType("SEG_ARC", 1);
    public static final int _SEG_ARC = 1;
    public static final SegmentType SEG_POINTTURN = new SegmentType("SEG_POINTTURN", 2);
    public static final int _SEG_POINTTURN = 2;
    public static final SegmentType SEG_CLOTHOID = new SegmentType("SEG_CLOTHOID", 3);
    public static final int _SEG_CLOTHOID = 3;
    public static SegmentType valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return SegmentType.SEG_STRAIGHT;
            case 1: return SegmentType.SEG_ARC;
            case 2: return SegmentType.SEG_POINTTURN;
            case 3: return SegmentType.SEG_CLOTHOID;

        }
        return null;
    }

    public static SegmentType from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[4];

        values[i] = SEG_STRAIGHT.ordinal();
        i++;
        values[i] = SEG_ARC.ordinal();
        i++;
        values[i] = SEG_POINTTURN.ordinal();
        i++;
        values[i] = SEG_CLOTHOID.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static SegmentType create() {

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

    private SegmentType(String name, int ordinal) {
        super(name, ordinal);
    }
}

