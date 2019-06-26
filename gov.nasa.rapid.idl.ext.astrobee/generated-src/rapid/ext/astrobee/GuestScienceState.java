

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.astrobee;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

/**
* Guest science state, list of whether apks are
* running or not
*/

public class GuestScienceState  extends rapid.Message implements Copyable, Serializable{

    /** A list of whether an apk is running or not */
    public rapid.ext.astrobee.BooleanSequence32 runningApks = (rapid.ext.astrobee.BooleanSequence32)rapid.ext.astrobee.BooleanSequence32.create();

    public GuestScienceState() {

        super();

        /** A list of whether an apk is running or not */

    }
    public GuestScienceState (GuestScienceState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        GuestScienceState self;
        self = new  GuestScienceState();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** A list of whether an apk is running or not */
        if (runningApks != null) {
            runningApks.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if (!super.equals(o)) {
            return false;
        }

        if(getClass() != o.getClass()) {
            return false;
        }

        GuestScienceState otherObj = (GuestScienceState)o;

        /** A list of whether an apk is running or not */
        if(!runningApks.equals(otherObj.runningApks)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** A list of whether an apk is running or not */
        __result += runningApks.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>GuestScienceStateTypeSupport</code>
    * rather than here by using the <code>-noCopyable</code> option
    * to rtiddsgen.
    * 
    * @param src The Object which contains the data to be copied.
    * @return Returns <code>this</code>.
    * @exception NullPointerException If <code>src</code> is null.
    * @exception ClassCastException If <code>src</code> is not the 
    * same type as <code>this</code>.
    * @see com.rti.dds.infrastructure.Copyable#copy_from(java.lang.Object)
    */
    public Object copy_from(Object src) {

        GuestScienceState typedSrc = (GuestScienceState) src;
        GuestScienceState typedDst = this;
        super.copy_from(typedSrc);
        /** A list of whether an apk is running or not */
        typedDst.runningApks = (rapid.ext.astrobee.BooleanSequence32) typedDst.runningApks.copy_from(typedSrc.runningApks);

        return this;
    }

    public String toString(){
        return toString("", 0);
    }

    public String toString(String desc, int indent) {
        StringBuffer strBuffer = new StringBuffer();        

        if (desc != null) {
            CdrHelper.printIndent(strBuffer, indent);
            strBuffer.append(desc).append(":\n");
        }

        strBuffer.append(super.toString("",indent));

        /** A list of whether an apk is running or not */
        strBuffer.append(runningApks.toString("runningApks ", indent+1));

        return strBuffer.toString();
    }

}
