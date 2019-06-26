

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
* Guest Science Data Type - used to express the type
* of data stored in the data octet
*/

public class GuestScienceDataType  extends Enum {

    public static final GuestScienceDataType GUEST_SCIENCE_STRING = new GuestScienceDataType("GUEST_SCIENCE_STRING", 0);
    public static final int _GUEST_SCIENCE_STRING = 0;
    public static final GuestScienceDataType GUEST_SCIENCE_JSON = new GuestScienceDataType("GUEST_SCIENCE_JSON", 1);
    public static final int _GUEST_SCIENCE_JSON = 1;
    public static final GuestScienceDataType GUEST_SCIENCE_BINARY = new GuestScienceDataType("GUEST_SCIENCE_BINARY", 2);
    public static final int _GUEST_SCIENCE_BINARY = 2;
    public static GuestScienceDataType valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return GuestScienceDataType.GUEST_SCIENCE_STRING;
            case 1: return GuestScienceDataType.GUEST_SCIENCE_JSON;
            case 2: return GuestScienceDataType.GUEST_SCIENCE_BINARY;

        }
        return null;
    }

    public static GuestScienceDataType from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[3];

        values[i] = GUEST_SCIENCE_STRING.ordinal();
        i++;
        values[i] = GUEST_SCIENCE_JSON.ordinal();
        i++;
        values[i] = GUEST_SCIENCE_BINARY.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static GuestScienceDataType create() {

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

    private GuestScienceDataType(String name, int ordinal) {
        super(name, ordinal);
    }
}

