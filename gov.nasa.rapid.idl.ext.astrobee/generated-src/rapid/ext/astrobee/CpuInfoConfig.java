

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
* The configuration of a cpu
* - name: machine name
* - num_cpus: number of cpus
* - max_frequencies: max frequency for each cpu
*/

public class CpuInfoConfig   implements Copyable, Serializable{

    public String name=  "" ; /* maximum length = (16) */
    public short num_cpus= 0;
    public rapid.ext.astrobee.UnsignedLongSequence8 max_frequencies = (rapid.ext.astrobee.UnsignedLongSequence8)rapid.ext.astrobee.UnsignedLongSequence8.create();

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

        name=  ""; 
        num_cpus= 0;
        if (max_frequencies != null) {
            max_frequencies.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        CpuInfoConfig otherObj = (CpuInfoConfig)o;

        if(!name.equals(otherObj.name)) {
            return false;
        }
        if(num_cpus != otherObj.num_cpus) {
            return false;
        }
        if(!max_frequencies.equals(otherObj.max_frequencies)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += name.hashCode(); 
        __result += (int)num_cpus;
        __result += max_frequencies.hashCode(); 
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

        typedDst.name = typedSrc.name;
        typedDst.num_cpus = typedSrc.num_cpus;
        typedDst.max_frequencies = (rapid.ext.astrobee.UnsignedLongSequence8) typedDst.max_frequencies.copy_from(typedSrc.max_frequencies);

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
        strBuffer.append("name: ").append(name).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("num_cpus: ").append(num_cpus).append("\n");  
        strBuffer.append(max_frequencies.toString("max_frequencies ", indent+1));

        return strBuffer.toString();
    }

}
