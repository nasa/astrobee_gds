

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

public class GeometryIndexMode  extends Enum {

    public static final GeometryIndexMode GIM_TRIANGLES = new GeometryIndexMode("GIM_TRIANGLES", 0);
    public static final int _GIM_TRIANGLES = 0;
    public static final GeometryIndexMode GIM_TRISTRIP = new GeometryIndexMode("GIM_TRISTRIP", 1);
    public static final int _GIM_TRISTRIP = 1;
    public static final GeometryIndexMode GIM_TRIFAN = new GeometryIndexMode("GIM_TRIFAN", 2);
    public static final int _GIM_TRIFAN = 2;
    public static final GeometryIndexMode GIM_QUADS = new GeometryIndexMode("GIM_QUADS", 3);
    public static final int _GIM_QUADS = 3;
    public static final GeometryIndexMode GIM_LINES = new GeometryIndexMode("GIM_LINES", 4);
    public static final int _GIM_LINES = 4;
    public static final GeometryIndexMode GIM_LINE_STRIP = new GeometryIndexMode("GIM_LINE_STRIP", 5);
    public static final int _GIM_LINE_STRIP = 5;
    public static final GeometryIndexMode GIM_LINE_LOOP = new GeometryIndexMode("GIM_LINE_LOOP", 6);
    public static final int _GIM_LINE_LOOP = 6;
    public static final GeometryIndexMode GIM_POINTS = new GeometryIndexMode("GIM_POINTS", 7);
    public static final int _GIM_POINTS = 7;
    public static GeometryIndexMode valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return GeometryIndexMode.GIM_TRIANGLES;
            case 1: return GeometryIndexMode.GIM_TRISTRIP;
            case 2: return GeometryIndexMode.GIM_TRIFAN;
            case 3: return GeometryIndexMode.GIM_QUADS;
            case 4: return GeometryIndexMode.GIM_LINES;
            case 5: return GeometryIndexMode.GIM_LINE_STRIP;
            case 6: return GeometryIndexMode.GIM_LINE_LOOP;
            case 7: return GeometryIndexMode.GIM_POINTS;

        }
        return null;
    }

    public static GeometryIndexMode from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[8];

        values[i] = GIM_TRIANGLES.ordinal();
        i++;
        values[i] = GIM_TRISTRIP.ordinal();
        i++;
        values[i] = GIM_TRIFAN.ordinal();
        i++;
        values[i] = GIM_QUADS.ordinal();
        i++;
        values[i] = GIM_LINES.ordinal();
        i++;
        values[i] = GIM_LINE_STRIP.ordinal();
        i++;
        values[i] = GIM_LINE_LOOP.ordinal();
        i++;
        values[i] = GIM_POINTS.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static GeometryIndexMode create() {

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

    private GeometryIndexMode(String name, int ordinal) {
        super(name, ordinal);
    }
}

