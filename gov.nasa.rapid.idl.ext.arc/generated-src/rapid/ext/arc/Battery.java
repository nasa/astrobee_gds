

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.arc;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

public class Battery   implements Copyable, Serializable{

    public int serialNumber= 0;
    public float voltage= 0;
    public float averageCurrent= 0;
    public float temperature= 0;
    public float relativeState= 0;
    public float absoluteState= 0;
    public float remainingCapacity= 0;
    public float fullChargeCapacity= 0;
    public long remainingTime= 0;
    public long averageRemainingTime= 0;
    public long averageRemainingChargeTime= 0;
    public int numberOfChargeCycles= 0;

    public Battery() {

    }
    public Battery (Battery other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        Battery self;
        self = new  Battery();
        self.clear();
        return self;

    }

    public void clear() {

        serialNumber= 0;
        voltage= 0;
        averageCurrent= 0;
        temperature= 0;
        relativeState= 0;
        absoluteState= 0;
        remainingCapacity= 0;
        fullChargeCapacity= 0;
        remainingTime= 0;
        averageRemainingTime= 0;
        averageRemainingChargeTime= 0;
        numberOfChargeCycles= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        Battery otherObj = (Battery)o;

        if(serialNumber != otherObj.serialNumber) {
            return false;
        }
        if(voltage != otherObj.voltage) {
            return false;
        }
        if(averageCurrent != otherObj.averageCurrent) {
            return false;
        }
        if(temperature != otherObj.temperature) {
            return false;
        }
        if(relativeState != otherObj.relativeState) {
            return false;
        }
        if(absoluteState != otherObj.absoluteState) {
            return false;
        }
        if(remainingCapacity != otherObj.remainingCapacity) {
            return false;
        }
        if(fullChargeCapacity != otherObj.fullChargeCapacity) {
            return false;
        }
        if(remainingTime != otherObj.remainingTime) {
            return false;
        }
        if(averageRemainingTime != otherObj.averageRemainingTime) {
            return false;
        }
        if(averageRemainingChargeTime != otherObj.averageRemainingChargeTime) {
            return false;
        }
        if(numberOfChargeCycles != otherObj.numberOfChargeCycles) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += (int)serialNumber;
        __result += (int)voltage;
        __result += (int)averageCurrent;
        __result += (int)temperature;
        __result += (int)relativeState;
        __result += (int)absoluteState;
        __result += (int)remainingCapacity;
        __result += (int)fullChargeCapacity;
        __result += (int)remainingTime;
        __result += (int)averageRemainingTime;
        __result += (int)averageRemainingChargeTime;
        __result += (int)numberOfChargeCycles;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>BatteryTypeSupport</code>
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

        Battery typedSrc = (Battery) src;
        Battery typedDst = this;

        typedDst.serialNumber = typedSrc.serialNumber;
        typedDst.voltage = typedSrc.voltage;
        typedDst.averageCurrent = typedSrc.averageCurrent;
        typedDst.temperature = typedSrc.temperature;
        typedDst.relativeState = typedSrc.relativeState;
        typedDst.absoluteState = typedSrc.absoluteState;
        typedDst.remainingCapacity = typedSrc.remainingCapacity;
        typedDst.fullChargeCapacity = typedSrc.fullChargeCapacity;
        typedDst.remainingTime = typedSrc.remainingTime;
        typedDst.averageRemainingTime = typedSrc.averageRemainingTime;
        typedDst.averageRemainingChargeTime = typedSrc.averageRemainingChargeTime;
        typedDst.numberOfChargeCycles = typedSrc.numberOfChargeCycles;

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
        strBuffer.append("serialNumber: ").append(serialNumber).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("voltage: ").append(voltage).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("averageCurrent: ").append(averageCurrent).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("temperature: ").append(temperature).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("relativeState: ").append(relativeState).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("absoluteState: ").append(absoluteState).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("remainingCapacity: ").append(remainingCapacity).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("fullChargeCapacity: ").append(fullChargeCapacity).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("remainingTime: ").append(remainingTime).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("averageRemainingTime: ").append(averageRemainingTime).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("averageRemainingChargeTime: ").append(averageRemainingChargeTime).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("numberOfChargeCycles: ").append(numberOfChargeCycles).append("\n");  

        return strBuffer.toString();
    }

}
