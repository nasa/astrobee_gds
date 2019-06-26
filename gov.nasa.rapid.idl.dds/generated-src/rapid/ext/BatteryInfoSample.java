

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

public class BatteryInfoSample   implements Copyable, Serializable{

    public int acStatus= 0;
    public int batteryStatus= 0;
    public float percentage= 0;

    public BatteryInfoSample() {

    }
    public BatteryInfoSample (BatteryInfoSample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        BatteryInfoSample self;
        self = new  BatteryInfoSample();
        self.clear();
        return self;

    }

    public void clear() {

        acStatus= 0;
        batteryStatus= 0;
        percentage= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        BatteryInfoSample otherObj = (BatteryInfoSample)o;

        if(acStatus != otherObj.acStatus) {
            return false;
        }
        if(batteryStatus != otherObj.batteryStatus) {
            return false;
        }
        if(percentage != otherObj.percentage) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += (int)acStatus;
        __result += (int)batteryStatus;
        __result += (int)percentage;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>BatteryInfoSampleTypeSupport</code>
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

        BatteryInfoSample typedSrc = (BatteryInfoSample) src;
        BatteryInfoSample typedDst = this;

        typedDst.acStatus = typedSrc.acStatus;
        typedDst.batteryStatus = typedSrc.batteryStatus;
        typedDst.percentage = typedSrc.percentage;

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
        strBuffer.append("acStatus: ").append(acStatus).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("batteryStatus: ").append(batteryStatus).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("percentage: ").append(percentage).append("\n");  

        return strBuffer.toString();
    }

}
