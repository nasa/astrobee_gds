

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
* The stats on one of the cpus
* - total_load: total load percentage for the cpu
* - frequency: current operating frequency in Hz
*/

public class CpuInfo   implements Copyable, Serializable{

    public float total_load= 0;
    public int frequency= 0;

    public CpuInfo() {

    }
    public CpuInfo (CpuInfo other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        CpuInfo self;
        self = new  CpuInfo();
        self.clear();
        return self;

    }

    public void clear() {

        total_load= 0;
        frequency= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        CpuInfo otherObj = (CpuInfo)o;

        if(total_load != otherObj.total_load) {
            return false;
        }
        if(frequency != otherObj.frequency) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += (int)total_load;
        __result += (int)frequency;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>CpuInfoTypeSupport</code>
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

        CpuInfo typedSrc = (CpuInfo) src;
        CpuInfo typedDst = this;

        typedDst.total_load = typedSrc.total_load;
        typedDst.frequency = typedSrc.frequency;

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
        strBuffer.append("total_load: ").append(total_load).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("frequency: ").append(frequency).append("\n");  

        return strBuffer.toString();
    }

}
