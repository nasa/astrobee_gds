

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
/**
* Config for the CpuState message.
*/

public class CpuConfig  extends rapid.Message implements Copyable, Serializable{

    public rapid.ext.astrobee.CpuInfoConfigSequence8 machines = (rapid.ext.astrobee.CpuInfoConfigSequence8)rapid.ext.astrobee.CpuInfoConfigSequence8.create();

    public CpuConfig() {

        super();

    }
    public CpuConfig (CpuConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        CpuConfig self;
        self = new  CpuConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        if (machines != null) {
            machines.clear();
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

        CpuConfig otherObj = (CpuConfig)o;

        if(!machines.equals(otherObj.machines)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += machines.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>CpuConfigTypeSupport</code>
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

        CpuConfig typedSrc = (CpuConfig) src;
        CpuConfig typedDst = this;
        super.copy_from(typedSrc);
        typedDst.machines = (rapid.ext.astrobee.CpuInfoConfigSequence8) typedDst.machines.copy_from(typedSrc.machines);

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

        strBuffer.append(machines.toString("machines ", indent+1));

        return strBuffer.toString();
    }

}
