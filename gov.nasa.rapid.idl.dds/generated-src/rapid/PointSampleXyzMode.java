

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
* PointSampleXyzMode denotes whether the values represent:
* <ul>
*   <li>PS_XYZ: x, y, z
*   <li>PS_XYt: x, y, theta
*   <li>PD_Rae: range, azimuth, elevation
* </ul>
*/

public class PointSampleXyzMode  extends Enum {

    public static final PointSampleXyzMode PS_XYZ = new PointSampleXyzMode("PS_XYZ", 0);
    public static final int _PS_XYZ = 0;
    public static final PointSampleXyzMode PS_XYt = new PointSampleXyzMode("PS_XYt", 1);
    public static final int _PS_XYt = 1;
    public static final PointSampleXyzMode PS_Rae = new PointSampleXyzMode("PS_Rae", 2);
    public static final int _PS_Rae = 2;
    public static PointSampleXyzMode valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return PointSampleXyzMode.PS_XYZ;
            case 1: return PointSampleXyzMode.PS_XYt;
            case 2: return PointSampleXyzMode.PS_Rae;

        }
        return null;
    }

    public static PointSampleXyzMode from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[3];

        values[i] = PS_XYZ.ordinal();
        i++;
        values[i] = PS_XYt.ordinal();
        i++;
        values[i] = PS_Rae.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static PointSampleXyzMode create() {

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

    private PointSampleXyzMode(String name, int ordinal) {
        super(name, ordinal);
    }
}

