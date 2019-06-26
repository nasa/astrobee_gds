

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
/**
* The stats of one of the machines
* - name: machine name
* - avg_total_load: average total load percentage
* - temperature: average of all thermal zones
* - cpus: array with info for each cpu
*/

public class MachineInfo   implements Copyable, Serializable{

    public float avg_total_load= 0;
    public float temperature= 0;
    public rapid.ext.astrobee.CpuInfoSequence8 cpus = (rapid.ext.astrobee.CpuInfoSequence8)rapid.ext.astrobee.CpuInfoSequence8.create();

    public MachineInfo() {

    }
    public MachineInfo (MachineInfo other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        MachineInfo self;
        self = new  MachineInfo();
        self.clear();
        return self;

    }

    public void clear() {

        avg_total_load= 0;
        temperature= 0;
        if (cpus != null) {
            cpus.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        MachineInfo otherObj = (MachineInfo)o;

        if(avg_total_load != otherObj.avg_total_load) {
            return false;
        }
        if(temperature != otherObj.temperature) {
            return false;
        }
        if(!cpus.equals(otherObj.cpus)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += (int)avg_total_load;
        __result += (int)temperature;
        __result += cpus.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>MachineInfoTypeSupport</code>
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

        MachineInfo typedSrc = (MachineInfo) src;
        MachineInfo typedDst = this;

        typedDst.avg_total_load = typedSrc.avg_total_load;
        typedDst.temperature = typedSrc.temperature;
        typedDst.cpus = (rapid.ext.astrobee.CpuInfoSequence8) typedDst.cpus.copy_from(typedSrc.cpus);

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
        strBuffer.append("avg_total_load: ").append(avg_total_load).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("temperature: ").append(temperature).append("\n");  
        strBuffer.append(cpus.toString("cpus ", indent+1));

        return strBuffer.toString();
    }

}
