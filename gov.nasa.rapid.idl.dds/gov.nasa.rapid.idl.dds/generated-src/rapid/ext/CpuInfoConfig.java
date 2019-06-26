

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

public class CpuInfoConfig   implements Copyable, Serializable{

    public int numCpus= 0;
    public float busyHigh= 0;
    public float busyCritical= 0;

    public CpuInfoConfig() {

    }
    public CpuInfoConfig (CpuInfoConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        CpuInfoConfig self;
        self = new  CpuInfoConfig();
        self.clear();
        return self;

    }

    public void clear() {

        numCpus= 0;
        busyHigh= 0;
        busyCritical= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        CpuInfoConfig otherObj = (CpuInfoConfig)o;

        if(numCpus != otherObj.numCpus) {
            return false;
        }
        if(busyHigh != otherObj.busyHigh) {
            return false;
        }
        if(busyCritical != otherObj.busyCritical) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += (int)numCpus;
        __result += (int)busyHigh;
        __result += (int)busyCritical;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>CpuInfoConfigTypeSupport</code>
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

        CpuInfoConfig typedSrc = (CpuInfoConfig) src;
        CpuInfoConfig typedDst = this;

        typedDst.numCpus = typedSrc.numCpus;
        typedDst.busyHigh = typedSrc.busyHigh;
        typedDst.busyCritical = typedSrc.busyCritical;

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
        strBuffer.append("numCpus: ").append(numCpus).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("busyHigh: ").append(busyHigh).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("busyCritical: ").append(busyCritical).append("\n");  

        return strBuffer.toString();
    }

}
