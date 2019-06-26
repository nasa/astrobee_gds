

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

/** Information specific to the fault.
* - subsystem: index into the subsystems sequence
*              in the fault config message
* - node: index into the nodes sequence in the
*         fault config message
* - faultId: id of the fault
* - warning: Whether the fault is a warning or not
* - faultDescription: short description of the fault
*/

public class FaultInfoSequence256   implements Copyable, Serializable{

    public rapid.ext.astrobee.FaultInfoSeq userData =  new rapid.ext.astrobee.FaultInfoSeq(256);

    public FaultInfoSequence256() {

    }
    public FaultInfoSequence256 (FaultInfoSequence256 other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        FaultInfoSequence256 self;
        self = new  FaultInfoSequence256();
        self.clear();
        return self;

    }

    public void clear() {

        if (userData != null) {
            userData.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        FaultInfoSequence256 otherObj = (FaultInfoSequence256)o;

        if(!userData.equals(otherObj.userData)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += userData.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>FaultInfoSequence256TypeSupport</code>
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

        FaultInfoSequence256 typedSrc = (FaultInfoSequence256) src;
        FaultInfoSequence256 typedDst = this;

        typedDst.userData.copy_from(typedSrc.userData);

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

        CdrHelper.printIndent(strBuffer, indent+1);
        strBuffer.append("userData:\n");
        for(int i__ = 0; i__ < userData.size(); ++i__) {
            strBuffer.append(((rapid.ext.astrobee.FaultInfo)userData.get(i__)).toString(Integer.toString(i__),indent+2));
        }

        return strBuffer.toString();
    }

}
