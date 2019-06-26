

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

public class Action  extends Enum {

    public static final Action ADD = new Action("ADD", 0);
    public static final int _ADD = 0;
    public static final Action DELETE = new Action("DELETE", 2);
    public static final int _DELETE = 2;
    public static final Action DELETEALL = new Action("DELETEALL", 3);
    public static final int _DELETEALL = 3;
    public static Action valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return Action.ADD;
            case 2: return Action.DELETE;
            case 3: return Action.DELETEALL;

        }
        return null;
    }

    public static Action from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[3];

        values[i] = ADD.ordinal();
        i++;
        values[i] = DELETE.ordinal();
        i++;
        values[i] = DELETEALL.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static Action create() {

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

    private Action(String name, int ordinal) {
        super(name, ordinal);
    }
}

