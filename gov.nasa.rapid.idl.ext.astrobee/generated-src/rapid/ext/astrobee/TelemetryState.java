

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
* The state of telemetry flowing to GDS
*/

public class TelemetryState  extends rapid.Message implements Copyable, Serializable{

    public float commStatusRate= 0;
    public float cpuStateRate= 0;
    public float diskStateRate= 0;
    public float ekfStateRate= 0;
    public float gncStateRate= 0;
    public float pmcCmdStateRate= 0;
    public float positionRate= 0;
    public rapid.ext.astrobee.CameraInfoSequence8 cameras = (rapid.ext.astrobee.CameraInfoSequence8)rapid.ext.astrobee.CameraInfoSequence8.create();

    public TelemetryState() {

        super();

    }
    public TelemetryState (TelemetryState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        TelemetryState self;
        self = new  TelemetryState();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        commStatusRate= 0;
        cpuStateRate= 0;
        diskStateRate= 0;
        ekfStateRate= 0;
        gncStateRate= 0;
        pmcCmdStateRate= 0;
        positionRate= 0;
        if (cameras != null) {
            cameras.clear();
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

        TelemetryState otherObj = (TelemetryState)o;

        if(commStatusRate != otherObj.commStatusRate) {
            return false;
        }
        if(cpuStateRate != otherObj.cpuStateRate) {
            return false;
        }
        if(diskStateRate != otherObj.diskStateRate) {
            return false;
        }
        if(ekfStateRate != otherObj.ekfStateRate) {
            return false;
        }
        if(gncStateRate != otherObj.gncStateRate) {
            return false;
        }
        if(pmcCmdStateRate != otherObj.pmcCmdStateRate) {
            return false;
        }
        if(positionRate != otherObj.positionRate) {
            return false;
        }
        if(!cameras.equals(otherObj.cameras)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += (int)commStatusRate;
        __result += (int)cpuStateRate;
        __result += (int)diskStateRate;
        __result += (int)ekfStateRate;
        __result += (int)gncStateRate;
        __result += (int)pmcCmdStateRate;
        __result += (int)positionRate;
        __result += cameras.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>TelemetryStateTypeSupport</code>
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

        TelemetryState typedSrc = (TelemetryState) src;
        TelemetryState typedDst = this;
        super.copy_from(typedSrc);
        typedDst.commStatusRate = typedSrc.commStatusRate;
        typedDst.cpuStateRate = typedSrc.cpuStateRate;
        typedDst.diskStateRate = typedSrc.diskStateRate;
        typedDst.ekfStateRate = typedSrc.ekfStateRate;
        typedDst.gncStateRate = typedSrc.gncStateRate;
        typedDst.pmcCmdStateRate = typedSrc.pmcCmdStateRate;
        typedDst.positionRate = typedSrc.positionRate;
        typedDst.cameras = (rapid.ext.astrobee.CameraInfoSequence8) typedDst.cameras.copy_from(typedSrc.cameras);

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

        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("commStatusRate: ").append(commStatusRate).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("cpuStateRate: ").append(cpuStateRate).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("diskStateRate: ").append(diskStateRate).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("ekfStateRate: ").append(ekfStateRate).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("gncStateRate: ").append(gncStateRate).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("pmcCmdStateRate: ").append(pmcCmdStateRate).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("positionRate: ").append(positionRate).append("\n");  
        strBuffer.append(cameras.toString("cameras ", indent+1));

        return strBuffer.toString();
    }

}
