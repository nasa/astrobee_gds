

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
/**
* PointSampleAttributeMode denotes the type of data contained in the attribute bytes.
* <ul>
*   <li>PS_UNUSED
*   <li>PS_INTENSITY: intensity of return. unsigned byte (0-255)
*   <li>PS_LAYER: for multi-layer scanners. unsigned byte (0-255)
*   <li>PS_ECHO: echo # for multiple returns. unsigned byte (0-255)
*   <li>PS_LAYER4_ECHO4: first 4 bits are layer (0-15), second 4 bits are echo (0-15)
*   <li>PS_PULSE: pulse index. unsigned byte (0-255)
*   <li>PS_RGB332: RGB packed into single byte
*   <li>PS_RGB565: Assumes both attribute bytes are used for 16 bit color.
*                  1st byte: upper 3 bits are first 3 bits of green, lower 5 bits are red
*                  2nd byte: upper 3 bits are second 3 bits of green, lower 5 bits are blue
* </ul>
*/

public class PointSampleAttributeMode  extends Enum {

    public static final PointSampleAttributeMode PS_UNUSED = new PointSampleAttributeMode("PS_UNUSED", 0);
    public static final int _PS_UNUSED = 0;
    public static final PointSampleAttributeMode PS_INTENSITY = new PointSampleAttributeMode("PS_INTENSITY", 1);
    public static final int _PS_INTENSITY = 1;
    public static final PointSampleAttributeMode PS_LAYER = new PointSampleAttributeMode("PS_LAYER", 2);
    public static final int _PS_LAYER = 2;
    public static final PointSampleAttributeMode PS_ECHO = new PointSampleAttributeMode("PS_ECHO", 3);
    public static final int _PS_ECHO = 3;
    public static final PointSampleAttributeMode PS_LAYER4_ECHO4 = new PointSampleAttributeMode("PS_LAYER4_ECHO4", 4);
    public static final int _PS_LAYER4_ECHO4 = 4;
    public static final PointSampleAttributeMode PS_PULSE = new PointSampleAttributeMode("PS_PULSE", 5);
    public static final int _PS_PULSE = 5;
    public static final PointSampleAttributeMode PS_RGB332 = new PointSampleAttributeMode("PS_RGB332", 6);
    public static final int _PS_RGB332 = 6;
    public static final PointSampleAttributeMode PS_RGB565 = new PointSampleAttributeMode("PS_RGB565", 7);
    public static final int _PS_RGB565 = 7;
    public static PointSampleAttributeMode valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return PointSampleAttributeMode.PS_UNUSED;
            case 1: return PointSampleAttributeMode.PS_INTENSITY;
            case 2: return PointSampleAttributeMode.PS_LAYER;
            case 3: return PointSampleAttributeMode.PS_ECHO;
            case 4: return PointSampleAttributeMode.PS_LAYER4_ECHO4;
            case 5: return PointSampleAttributeMode.PS_PULSE;
            case 6: return PointSampleAttributeMode.PS_RGB332;
            case 7: return PointSampleAttributeMode.PS_RGB565;

        }
        return null;
    }

    public static PointSampleAttributeMode from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[8];

        values[i] = PS_UNUSED.ordinal();
        i++;
        values[i] = PS_INTENSITY.ordinal();
        i++;
        values[i] = PS_LAYER.ordinal();
        i++;
        values[i] = PS_ECHO.ordinal();
        i++;
        values[i] = PS_LAYER4_ECHO4.ordinal();
        i++;
        values[i] = PS_PULSE.ordinal();
        i++;
        values[i] = PS_RGB332.ordinal();
        i++;
        values[i] = PS_RGB565.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static PointSampleAttributeMode create() {

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

    private PointSampleAttributeMode(String name, int ordinal) {
        super(name, ordinal);
    }
}

