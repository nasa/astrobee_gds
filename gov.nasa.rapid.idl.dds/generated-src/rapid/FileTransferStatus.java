

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
* <ul>
*   <li>RAPID_FILE_PREFETCH_PENDING:
*   <li>RAPID_FILE_PREFETCHING:
*   <li>RAPID_FILE_PENDING:
*   <li>RAPID_FILE_ACTIVE:
*   <li>RAPID_FILE_PAUSED:
*   <li>RAPID_FILE_DONE:
*   <li>RAPID_FILE_CANCELED:
*   <li>RAPID_FILE_ERROR:
* </ul>
*/

public class FileTransferStatus  extends Enum {

    public static final FileTransferStatus RAPID_FILE_PREFETCH_PENDING = new FileTransferStatus("RAPID_FILE_PREFETCH_PENDING", 0);
    public static final int _RAPID_FILE_PREFETCH_PENDING = 0;
    public static final FileTransferStatus RAPID_FILE_PREFETCHING = new FileTransferStatus("RAPID_FILE_PREFETCHING", 1);
    public static final int _RAPID_FILE_PREFETCHING = 1;
    public static final FileTransferStatus RAPID_FILE_PENDING = new FileTransferStatus("RAPID_FILE_PENDING", 2);
    public static final int _RAPID_FILE_PENDING = 2;
    public static final FileTransferStatus RAPID_FILE_ACTIVE = new FileTransferStatus("RAPID_FILE_ACTIVE", 3);
    public static final int _RAPID_FILE_ACTIVE = 3;
    public static final FileTransferStatus RAPID_FILE_PAUSED = new FileTransferStatus("RAPID_FILE_PAUSED", 4);
    public static final int _RAPID_FILE_PAUSED = 4;
    public static final FileTransferStatus RAPID_FILE_DONE = new FileTransferStatus("RAPID_FILE_DONE", 5);
    public static final int _RAPID_FILE_DONE = 5;
    public static final FileTransferStatus RAPID_FILE_CANCELED = new FileTransferStatus("RAPID_FILE_CANCELED", 6);
    public static final int _RAPID_FILE_CANCELED = 6;
    public static final FileTransferStatus RAPID_FILE_ERROR = new FileTransferStatus("RAPID_FILE_ERROR", 7);
    public static final int _RAPID_FILE_ERROR = 7;
    public static FileTransferStatus valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return FileTransferStatus.RAPID_FILE_PREFETCH_PENDING;
            case 1: return FileTransferStatus.RAPID_FILE_PREFETCHING;
            case 2: return FileTransferStatus.RAPID_FILE_PENDING;
            case 3: return FileTransferStatus.RAPID_FILE_ACTIVE;
            case 4: return FileTransferStatus.RAPID_FILE_PAUSED;
            case 5: return FileTransferStatus.RAPID_FILE_DONE;
            case 6: return FileTransferStatus.RAPID_FILE_CANCELED;
            case 7: return FileTransferStatus.RAPID_FILE_ERROR;

        }
        return null;
    }

    public static FileTransferStatus from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[8];

        values[i] = RAPID_FILE_PREFETCH_PENDING.ordinal();
        i++;
        values[i] = RAPID_FILE_PREFETCHING.ordinal();
        i++;
        values[i] = RAPID_FILE_PENDING.ordinal();
        i++;
        values[i] = RAPID_FILE_ACTIVE.ordinal();
        i++;
        values[i] = RAPID_FILE_PAUSED.ordinal();
        i++;
        values[i] = RAPID_FILE_DONE.ordinal();
        i++;
        values[i] = RAPID_FILE_CANCELED.ordinal();
        i++;
        values[i] = RAPID_FILE_ERROR.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static FileTransferStatus create() {

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

    private FileTransferStatus(String name, int ordinal) {
        super(name, ordinal);
    }
}

