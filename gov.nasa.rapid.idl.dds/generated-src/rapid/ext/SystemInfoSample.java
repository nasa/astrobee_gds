

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

public class SystemInfoSample  extends rapid.Message implements Copyable, Serializable{

    public rapid.ext.BatteryInfoSampleSequence batteries = (rapid.ext.BatteryInfoSampleSequence)rapid.ext.BatteryInfoSampleSequence.create();
    public rapid.ext.MemoryInfoSample memory = (rapid.ext.MemoryInfoSample)rapid.ext.MemoryInfoSample.create();
    public rapid.ext.CpuInfoSampleSequence cpus = (rapid.ext.CpuInfoSampleSequence)rapid.ext.CpuInfoSampleSequence.create();
    public rapid.ext.FilesystemInfoSampleSequence filesystems = (rapid.ext.FilesystemInfoSampleSequence)rapid.ext.FilesystemInfoSampleSequence.create();
    public rapid.ext.ThermalInfoSampleSequence temperatures = (rapid.ext.ThermalInfoSampleSequence)rapid.ext.ThermalInfoSampleSequence.create();
    public rapid.ext.NetTrafficInfoSampleSequence netInterfaces = (rapid.ext.NetTrafficInfoSampleSequence)rapid.ext.NetTrafficInfoSampleSequence.create();
    public rapid.ext.WifiInfoSampleSequence wifiInterfaces = (rapid.ext.WifiInfoSampleSequence)rapid.ext.WifiInfoSampleSequence.create();

    public SystemInfoSample() {

        super();

    }
    public SystemInfoSample (SystemInfoSample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        SystemInfoSample self;
        self = new  SystemInfoSample();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        if (batteries != null) {
            batteries.clear();
        }
        if (memory != null) {
            memory.clear();
        }
        if (cpus != null) {
            cpus.clear();
        }
        if (filesystems != null) {
            filesystems.clear();
        }
        if (temperatures != null) {
            temperatures.clear();
        }
        if (netInterfaces != null) {
            netInterfaces.clear();
        }
        if (wifiInterfaces != null) {
            wifiInterfaces.clear();
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

        SystemInfoSample otherObj = (SystemInfoSample)o;

        if(!batteries.equals(otherObj.batteries)) {
            return false;
        }
        if(!memory.equals(otherObj.memory)) {
            return false;
        }
        if(!cpus.equals(otherObj.cpus)) {
            return false;
        }
        if(!filesystems.equals(otherObj.filesystems)) {
            return false;
        }
        if(!temperatures.equals(otherObj.temperatures)) {
            return false;
        }
        if(!netInterfaces.equals(otherObj.netInterfaces)) {
            return false;
        }
        if(!wifiInterfaces.equals(otherObj.wifiInterfaces)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += batteries.hashCode(); 
        __result += memory.hashCode(); 
        __result += cpus.hashCode(); 
        __result += filesystems.hashCode(); 
        __result += temperatures.hashCode(); 
        __result += netInterfaces.hashCode(); 
        __result += wifiInterfaces.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>SystemInfoSampleTypeSupport</code>
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

        SystemInfoSample typedSrc = (SystemInfoSample) src;
        SystemInfoSample typedDst = this;
        super.copy_from(typedSrc);
        typedDst.batteries = (rapid.ext.BatteryInfoSampleSequence) typedDst.batteries.copy_from(typedSrc.batteries);
        typedDst.memory = (rapid.ext.MemoryInfoSample) typedDst.memory.copy_from(typedSrc.memory);
        typedDst.cpus = (rapid.ext.CpuInfoSampleSequence) typedDst.cpus.copy_from(typedSrc.cpus);
        typedDst.filesystems = (rapid.ext.FilesystemInfoSampleSequence) typedDst.filesystems.copy_from(typedSrc.filesystems);
        typedDst.temperatures = (rapid.ext.ThermalInfoSampleSequence) typedDst.temperatures.copy_from(typedSrc.temperatures);
        typedDst.netInterfaces = (rapid.ext.NetTrafficInfoSampleSequence) typedDst.netInterfaces.copy_from(typedSrc.netInterfaces);
        typedDst.wifiInterfaces = (rapid.ext.WifiInfoSampleSequence) typedDst.wifiInterfaces.copy_from(typedSrc.wifiInterfaces);

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

        strBuffer.append(batteries.toString("batteries ", indent+1));
        strBuffer.append(memory.toString("memory ", indent+1));
        strBuffer.append(cpus.toString("cpus ", indent+1));
        strBuffer.append(filesystems.toString("filesystems ", indent+1));
        strBuffer.append(temperatures.toString("temperatures ", indent+1));
        strBuffer.append(netInterfaces.toString("netInterfaces ", indent+1));
        strBuffer.append(wifiInterfaces.toString("wifiInterfaces ", indent+1));

        return strBuffer.toString();
    }

}
