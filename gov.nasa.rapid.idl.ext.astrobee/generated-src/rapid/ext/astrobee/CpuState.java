

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
/**
* The state of the cpus on Astrobee
*/

public class CpuState  extends rapid.Message implements Copyable, Serializable{

    /** Array with info for each machine */
    public rapid.ext.astrobee.MachineInfoSequence8 machines = (rapid.ext.astrobee.MachineInfoSequence8)rapid.ext.astrobee.MachineInfoSequence8.create();

    public CpuState() {

        super();

        /** Array with info for each machine */

    }
    public CpuState (CpuState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        CpuState self;
        self = new  CpuState();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** Array with info for each machine */
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

        CpuState otherObj = (CpuState)o;

        /** Array with info for each machine */
        if(!machines.equals(otherObj.machines)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** Array with info for each machine */
        __result += machines.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>CpuStateTypeSupport</code>
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

        CpuState typedSrc = (CpuState) src;
        CpuState typedDst = this;
        super.copy_from(typedSrc);
        /** Array with info for each machine */
        typedDst.machines = (rapid.ext.astrobee.MachineInfoSequence8) typedDst.machines.copy_from(typedSrc.machines);

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

        /** Array with info for each machine */
        strBuffer.append(machines.toString("machines ", indent+1));

        return strBuffer.toString();
    }

}
