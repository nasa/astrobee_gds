

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

/**
* Location of the battery
* <ul>
*   <li>SLOT_TOP_LEFT: Battery slot located at the top left side of the robot.
*   <li>SLOT_TOP_RIGHT: Battery slot located at the top right side of the robot.
*   <li>SLOT_BOTTOM_LEFT: Battery slot located at the bottom left side of the robot.
*   <li>SLOT_BOTTOM_RIGHT: Battery slot located at the bottom right side of the robot.
* </ul>
*/

public class BatterySlot  extends Enum {

    public static final BatterySlot SLOT_TOP_LEFT = new BatterySlot("SLOT_TOP_LEFT", 0);
    public static final int _SLOT_TOP_LEFT = 0;
    public static final BatterySlot SLOT_TOP_RIGHT = new BatterySlot("SLOT_TOP_RIGHT", 1);
    public static final int _SLOT_TOP_RIGHT = 1;
    public static final BatterySlot SLOT_BOTTOM_LEFT = new BatterySlot("SLOT_BOTTOM_LEFT", 2);
    public static final int _SLOT_BOTTOM_LEFT = 2;
    public static final BatterySlot SLOT_BOTTOM_RIGHT = new BatterySlot("SLOT_BOTTOM_RIGHT", 3);
    public static final int _SLOT_BOTTOM_RIGHT = 3;
    public static final BatterySlot SLOT_UNKNOWN = new BatterySlot("SLOT_UNKNOWN", 4);
    public static final int _SLOT_UNKNOWN = 4;
    public static BatterySlot valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return BatterySlot.SLOT_TOP_LEFT;
            case 1: return BatterySlot.SLOT_TOP_RIGHT;
            case 2: return BatterySlot.SLOT_BOTTOM_LEFT;
            case 3: return BatterySlot.SLOT_BOTTOM_RIGHT;
            case 4: return BatterySlot.SLOT_UNKNOWN;

        }
        return null;
    }

    public static BatterySlot from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[5];

        values[i] = SLOT_TOP_LEFT.ordinal();
        i++;
        values[i] = SLOT_TOP_RIGHT.ordinal();
        i++;
        values[i] = SLOT_BOTTOM_LEFT.ordinal();
        i++;
        values[i] = SLOT_BOTTOM_RIGHT.ordinal();
        i++;
        values[i] = SLOT_UNKNOWN.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static BatterySlot create() {

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

    private BatterySlot(String name, int ordinal) {
        super(name, ordinal);
    }
}

