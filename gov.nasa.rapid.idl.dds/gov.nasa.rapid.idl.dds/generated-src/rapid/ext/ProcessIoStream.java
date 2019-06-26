

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext;

import com.rti.dds.util.Enum;
import com.rti.dds.cdr.CdrHelper;
import java.util.Arrays;
import java.io.ObjectStreamException;

public class ProcessIoStream  extends Enum {

    public static final ProcessIoStream PROCESS_STDIN = new ProcessIoStream("PROCESS_STDIN", 0);
    public static final int _PROCESS_STDIN = 0;
    public static final ProcessIoStream PROCESS_STDOUT = new ProcessIoStream("PROCESS_STDOUT", 1);
    public static final int _PROCESS_STDOUT = 1;
    public static final ProcessIoStream PROCESS_STDERR = new ProcessIoStream("PROCESS_STDERR", 2);
    public static final int _PROCESS_STDERR = 2;
    public static ProcessIoStream valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return ProcessIoStream.PROCESS_STDIN;
            case 1: return ProcessIoStream.PROCESS_STDOUT;
            case 2: return ProcessIoStream.PROCESS_STDERR;

        }
        return null;
    }

    public static ProcessIoStream from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[3];

        values[i] = PROCESS_STDIN.ordinal();
        i++;
        values[i] = PROCESS_STDOUT.ordinal();
        i++;
        values[i] = PROCESS_STDERR.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static ProcessIoStream create() {

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

    private ProcessIoStream(String name, int ordinal) {
        super(name, ordinal);
    }
}

