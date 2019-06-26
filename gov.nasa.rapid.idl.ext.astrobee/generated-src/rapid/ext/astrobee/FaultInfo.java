

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

public class FaultInfo   implements Copyable, Serializable{

    public short subsystem= 0;
    public short node= 0;
    public int faultId= 0;
    public boolean warning= false;
    public String faultDescription=  "" ; /* maximum length = (64) */

    public FaultInfo() {

    }
    public FaultInfo (FaultInfo other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        FaultInfo self;
        self = new  FaultInfo();
        self.clear();
        return self;

    }

    public void clear() {

        subsystem= 0;
        node= 0;
        faultId= 0;
        warning= false;
        faultDescription=  ""; 
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        FaultInfo otherObj = (FaultInfo)o;

        if(subsystem != otherObj.subsystem) {
            return false;
        }
        if(node != otherObj.node) {
            return false;
        }
        if(faultId != otherObj.faultId) {
            return false;
        }
        if(warning != otherObj.warning) {
            return false;
        }
        if(!faultDescription.equals(otherObj.faultDescription)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += (int)subsystem;
        __result += (int)node;
        __result += (int)faultId;
        __result += (warning == true)?1:0;
        __result += faultDescription.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>FaultInfoTypeSupport</code>
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

        FaultInfo typedSrc = (FaultInfo) src;
        FaultInfo typedDst = this;

        typedDst.subsystem = typedSrc.subsystem;
        typedDst.node = typedSrc.node;
        typedDst.faultId = typedSrc.faultId;
        typedDst.warning = typedSrc.warning;
        typedDst.faultDescription = typedSrc.faultDescription;

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
        strBuffer.append("subsystem: ").append(subsystem).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("node: ").append(node).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("faultId: ").append(faultId).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("warning: ").append(warning).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("faultDescription: ").append(faultDescription).append("\n");  

        return strBuffer.toString();
    }

}
