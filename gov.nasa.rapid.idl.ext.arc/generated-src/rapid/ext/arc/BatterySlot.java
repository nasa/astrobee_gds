

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

public class BatterySlot   implements Copyable, Serializable{

    public boolean present= false;
    public boolean charging= false;
    public boolean supplyingPower= false;
    public boolean chargePowerPresent= false;
    public boolean powerNoGood= false;
    public boolean chargeInhibited= false;

    public BatterySlot() {

    }
    public BatterySlot (BatterySlot other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        BatterySlot self;
        self = new  BatterySlot();
        self.clear();
        return self;

    }

    public void clear() {

        present= false;
        charging= false;
        supplyingPower= false;
        chargePowerPresent= false;
        powerNoGood= false;
        chargeInhibited= false;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        BatterySlot otherObj = (BatterySlot)o;

        if(present != otherObj.present) {
            return false;
        }
        if(charging != otherObj.charging) {
            return false;
        }
        if(supplyingPower != otherObj.supplyingPower) {
            return false;
        }
        if(chargePowerPresent != otherObj.chargePowerPresent) {
            return false;
        }
        if(powerNoGood != otherObj.powerNoGood) {
            return false;
        }
        if(chargeInhibited != otherObj.chargeInhibited) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += (present == true)?1:0;
        __result += (charging == true)?1:0;
        __result += (supplyingPower == true)?1:0;
        __result += (chargePowerPresent == true)?1:0;
        __result += (powerNoGood == true)?1:0;
        __result += (chargeInhibited == true)?1:0;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>BatterySlotTypeSupport</code>
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

        BatterySlot typedSrc = (BatterySlot) src;
        BatterySlot typedDst = this;

        typedDst.present = typedSrc.present;
        typedDst.charging = typedSrc.charging;
        typedDst.supplyingPower = typedSrc.supplyingPower;
        typedDst.chargePowerPresent = typedSrc.chargePowerPresent;
        typedDst.powerNoGood = typedSrc.powerNoGood;
        typedDst.chargeInhibited = typedSrc.chargeInhibited;

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
        strBuffer.append("present: ").append(present).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("charging: ").append(charging).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("supplyingPower: ").append(supplyingPower).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("chargePowerPresent: ").append(chargePowerPresent).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("powerNoGood: ").append(powerNoGood).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("chargeInhibited: ").append(chargeInhibited).append("\n");  

        return strBuffer.toString();
    }

}
