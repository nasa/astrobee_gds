

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
* Location of the battery
* <ul>
*   <li>SLOT_TOP_LEFT: Battery slot located at the top left side of the robot.
*   <li>SLOT_TOP_RIGHT: Battery slot located at the top right side of the robot.
*   <li>SLOT_BOTTOM_LEFT: Battery slot located at the bottom left side of the robot.
*   <li>SLOT_BOTTOM_RIGHT: Battery slot located at the bottom right side of the robot.
* </ul>
*/
/** The stats of a battery
* - slot: location of battery slot"
*/

public class BatteryInfoConfig   implements Copyable, Serializable{

    public rapid.ext.astrobee.BatterySlot slot = (rapid.ext.astrobee.BatterySlot)rapid.ext.astrobee.BatterySlot.create();
    public float designedCapacity= 0;
    public float currentMaxCapacity= 0;

    public BatteryInfoConfig() {

    }
    public BatteryInfoConfig (BatteryInfoConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        BatteryInfoConfig self;
        self = new  BatteryInfoConfig();
        self.clear();
        return self;

    }

    public void clear() {

        slot = rapid.ext.astrobee.BatterySlot.create();
        designedCapacity= 0;
        currentMaxCapacity= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        BatteryInfoConfig otherObj = (BatteryInfoConfig)o;

        if(!slot.equals(otherObj.slot)) {
            return false;
        }
        if(designedCapacity != otherObj.designedCapacity) {
            return false;
        }
        if(currentMaxCapacity != otherObj.currentMaxCapacity) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += slot.hashCode(); 
        __result += (int)designedCapacity;
        __result += (int)currentMaxCapacity;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>BatteryInfoConfigTypeSupport</code>
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

        BatteryInfoConfig typedSrc = (BatteryInfoConfig) src;
        BatteryInfoConfig typedDst = this;

        typedDst.slot = (rapid.ext.astrobee.BatterySlot) typedDst.slot.copy_from(typedSrc.slot);
        typedDst.designedCapacity = typedSrc.designedCapacity;
        typedDst.currentMaxCapacity = typedSrc.currentMaxCapacity;

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

        strBuffer.append(slot.toString("slot ", indent+1));
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("designedCapacity: ").append(designedCapacity).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("currentMaxCapacity: ").append(currentMaxCapacity).append("\n");  

        return strBuffer.toString();
    }

}
