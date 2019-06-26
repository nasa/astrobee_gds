

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

public class CameraResolution  extends Enum {

    public static final CameraResolution RESOLUTION_320_240 = new CameraResolution("RESOLUTION_320_240", 0);
    public static final int _RESOLUTION_320_240 = 0;
    public static final CameraResolution RESOLUTION_640_480 = new CameraResolution("RESOLUTION_640_480", 1);
    public static final int _RESOLUTION_640_480 = 1;
    public static final CameraResolution RESOLUTION_1024_768 = new CameraResolution("RESOLUTION_1024_768", 2);
    public static final int _RESOLUTION_1024_768 = 2;
    public static final CameraResolution RESOLUTION_1280_960 = new CameraResolution("RESOLUTION_1280_960", 3);
    public static final int _RESOLUTION_1280_960 = 3;
    public static final CameraResolution RESOLUTION_480_270 = new CameraResolution("RESOLUTION_480_270", 4);
    public static final int _RESOLUTION_480_270 = 4;
    public static final CameraResolution RESOLUTION_960_540 = new CameraResolution("RESOLUTION_960_540", 5);
    public static final int _RESOLUTION_960_540 = 5;
    public static final CameraResolution RESOLUTION_1280_720 = new CameraResolution("RESOLUTION_1280_720", 6);
    public static final int _RESOLUTION_1280_720 = 6;
    public static final CameraResolution RESOLUTION_1920_1080 = new CameraResolution("RESOLUTION_1920_1080", 7);
    public static final int _RESOLUTION_1920_1080 = 7;
    public static final CameraResolution RESOLUTION_224_172 = new CameraResolution("RESOLUTION_224_172", 8);
    public static final int _RESOLUTION_224_172 = 8;
    public static final CameraResolution RESOLUTION_1_1 = new CameraResolution("RESOLUTION_1_1", 9);
    public static final int _RESOLUTION_1_1 = 9;
    public static CameraResolution valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return CameraResolution.RESOLUTION_320_240;
            case 1: return CameraResolution.RESOLUTION_640_480;
            case 2: return CameraResolution.RESOLUTION_1024_768;
            case 3: return CameraResolution.RESOLUTION_1280_960;
            case 4: return CameraResolution.RESOLUTION_480_270;
            case 5: return CameraResolution.RESOLUTION_960_540;
            case 6: return CameraResolution.RESOLUTION_1280_720;
            case 7: return CameraResolution.RESOLUTION_1920_1080;
            case 8: return CameraResolution.RESOLUTION_224_172;
            case 9: return CameraResolution.RESOLUTION_1_1;

        }
        return null;
    }

    public static CameraResolution from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[10];

        values[i] = RESOLUTION_320_240.ordinal();
        i++;
        values[i] = RESOLUTION_640_480.ordinal();
        i++;
        values[i] = RESOLUTION_1024_768.ordinal();
        i++;
        values[i] = RESOLUTION_1280_960.ordinal();
        i++;
        values[i] = RESOLUTION_480_270.ordinal();
        i++;
        values[i] = RESOLUTION_960_540.ordinal();
        i++;
        values[i] = RESOLUTION_1280_720.ordinal();
        i++;
        values[i] = RESOLUTION_1920_1080.ordinal();
        i++;
        values[i] = RESOLUTION_224_172.ordinal();
        i++;
        values[i] = RESOLUTION_1_1.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static CameraResolution create() {

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

    private CameraResolution(String name, int ordinal) {
        super(name, ordinal);
    }
}

