

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
* ChannelStatus is used to indicate whether a RAPID File Queue (part of the RAPID File Transfer Service)
* is currently capable of transferring files (Active) or is temporarily refraining from transferring files
* (Paused).
* <ul>
*   <li>FILE_QUEUE_CHANNEL_ACTIVE: Capable of transferring files.
*   <li>FILE_QUEUE_CHANNEL_PAUSED: Temporarily refraining from transferring files.
* </ul>
*/

public class ChannelStatus  extends Enum {

    public static final ChannelStatus FILE_QUEUE_CHANNEL_ACTIVE = new ChannelStatus("FILE_QUEUE_CHANNEL_ACTIVE", 0);
    public static final int _FILE_QUEUE_CHANNEL_ACTIVE = 0;
    public static final ChannelStatus FILE_QUEUE_CHANNEL_PAUSED = new ChannelStatus("FILE_QUEUE_CHANNEL_PAUSED", 1);
    public static final int _FILE_QUEUE_CHANNEL_PAUSED = 1;
    public static ChannelStatus valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return ChannelStatus.FILE_QUEUE_CHANNEL_ACTIVE;
            case 1: return ChannelStatus.FILE_QUEUE_CHANNEL_PAUSED;

        }
        return null;
    }

    public static ChannelStatus from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[2];

        values[i] = FILE_QUEUE_CHANNEL_ACTIVE.ordinal();
        i++;
        values[i] = FILE_QUEUE_CHANNEL_PAUSED.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static ChannelStatus create() {

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

    private ChannelStatus(String name, int ordinal) {
        super(name, ordinal);
    }
}

