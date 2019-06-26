

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

/** A fault within Astrobee.
* - timestamp: when this fault happened, in microseconds
* - code: an error code defined elsewhere
* - message: string containing why fault occurred
* - data: useful data that may accompany the fault
*/
/**
* The state of any faults in Astrobee
*/

public class FaultState  extends rapid.Message implements Copyable, Serializable{

    /** Up to 32 different faults within Astrobee */
    public rapid.ext.astrobee.FaultSequence32 faults = (rapid.ext.astrobee.FaultSequence32)rapid.ext.astrobee.FaultSequence32.create();

    public FaultState() {

        super();

        /** Up to 32 different faults within Astrobee */

    }
    public FaultState (FaultState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        FaultState self;
        self = new  FaultState();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** Up to 32 different faults within Astrobee */
        if (faults != null) {
            faults.clear();
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

        FaultState otherObj = (FaultState)o;

        /** Up to 32 different faults within Astrobee */
        if(!faults.equals(otherObj.faults)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** Up to 32 different faults within Astrobee */
        __result += faults.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>FaultStateTypeSupport</code>
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

        FaultState typedSrc = (FaultState) src;
        FaultState typedDst = this;
        super.copy_from(typedSrc);
        /** Up to 32 different faults within Astrobee */
        typedDst.faults = (rapid.ext.astrobee.FaultSequence32) typedDst.faults.copy_from(typedSrc.faults);

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

        /** Up to 32 different faults within Astrobee */
        strBuffer.append(faults.toString("faults ", indent+1));

        return strBuffer.toString();
    }

}
