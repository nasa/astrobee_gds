

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

public class CameraMode  extends Enum {

    public static final CameraMode MODE_VIDEO = new CameraMode("MODE_VIDEO", 0);
    public static final int _MODE_VIDEO = 0;
    public static final CameraMode MODE_FRAMES = new CameraMode("MODE_FRAMES", 1);
    public static final int _MODE_FRAMES = 1;
    public static CameraMode valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return CameraMode.MODE_VIDEO;
            case 1: return CameraMode.MODE_FRAMES;

        }
        return null;
    }

    public static CameraMode from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[2];

        values[i] = MODE_VIDEO.ordinal();
        i++;
        values[i] = MODE_FRAMES.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static CameraMode create() {

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

    private CameraMode(String name, int ordinal) {
        super(name, ordinal);
    }
}

