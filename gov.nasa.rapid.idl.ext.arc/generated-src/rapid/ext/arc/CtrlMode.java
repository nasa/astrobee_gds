

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

public class CtrlMode  extends Enum {

    public static final CtrlMode CTRL_POSITION = new CtrlMode("CTRL_POSITION", 0);
    public static final int _CTRL_POSITION = 0;
    public static final CtrlMode CTRL_TRAPECOID = new CtrlMode("CTRL_TRAPECOID", 1);
    public static final int _CTRL_TRAPECOID = 1;
    public static final CtrlMode CTRL_TRAJECTORY = new CtrlMode("CTRL_TRAJECTORY", 2);
    public static final int _CTRL_TRAJECTORY = 2;
    public static final CtrlMode CTRL_HOLD = new CtrlMode("CTRL_HOLD", 3);
    public static final int _CTRL_HOLD = 3;
    public static final CtrlMode CTRL_OFF = new CtrlMode("CTRL_OFF", 4);
    public static final int _CTRL_OFF = 4;
    public static CtrlMode valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return CtrlMode.CTRL_POSITION;
            case 1: return CtrlMode.CTRL_TRAPECOID;
            case 2: return CtrlMode.CTRL_TRAJECTORY;
            case 3: return CtrlMode.CTRL_HOLD;
            case 4: return CtrlMode.CTRL_OFF;

        }
        return null;
    }

    public static CtrlMode from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[5];

        values[i] = CTRL_POSITION.ordinal();
        i++;
        values[i] = CTRL_TRAPECOID.ordinal();
        i++;
        values[i] = CTRL_TRAJECTORY.ordinal();
        i++;
        values[i] = CTRL_HOLD.ordinal();
        i++;
        values[i] = CTRL_OFF.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static CtrlMode create() {

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

    private CtrlMode(String name, int ordinal) {
        super(name, ordinal);
    }
}

