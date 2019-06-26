

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
* The state of the data partitions on Astrobee
*/

public class EpsState  extends rapid.Message implements Copyable, Serializable{

    public rapid.ext.astrobee.BatteryInfoSequence4 batteries = (rapid.ext.astrobee.BatteryInfoSequence4)rapid.ext.astrobee.BatteryInfoSequence4.create();
    /** The percentage remaining of each battery added
    * up. This value can be over 100.
    */
    public float batteryTotal= 0;
    /** The estimated time remaining in minutes. It is
    * calculated by adding up the current capacity
    * and current for all of the batteries and then
    * dividing the current capacity by the current.
    * It is then rounded to the nearest 10 minutes.
    */
    public int estimatedMinutesRemaining= 0;

    public EpsState() {

        super();

        /** The percentage remaining of each battery added
        * up. This value can be over 100.
        */
        /** The estimated time remaining in minutes. It is
        * calculated by adding up the current capacity
        * and current for all of the batteries and then
        * dividing the current capacity by the current.
        * It is then rounded to the nearest 10 minutes.
        */

    }
    public EpsState (EpsState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        EpsState self;
        self = new  EpsState();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        if (batteries != null) {
            batteries.clear();
        }
        /** The percentage remaining of each battery added
        * up. This value can be over 100.
        */
        batteryTotal= 0;
        /** The estimated time remaining in minutes. It is
        * calculated by adding up the current capacity
        * and current for all of the batteries and then
        * dividing the current capacity by the current.
        * It is then rounded to the nearest 10 minutes.
        */
        estimatedMinutesRemaining= 0;
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

        EpsState otherObj = (EpsState)o;

        if(!batteries.equals(otherObj.batteries)) {
            return false;
        }
        /** The percentage remaining of each battery added
        * up. This value can be over 100.
        */
        if(batteryTotal != otherObj.batteryTotal) {
            return false;
        }
        /** The estimated time remaining in minutes. It is
        * calculated by adding up the current capacity
        * and current for all of the batteries and then
        * dividing the current capacity by the current.
        * It is then rounded to the nearest 10 minutes.
        */
        if(estimatedMinutesRemaining != otherObj.estimatedMinutesRemaining) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += batteries.hashCode(); 
        /** The percentage remaining of each battery added
        * up. This value can be over 100.
        */
        __result += (int)batteryTotal;
        /** The estimated time remaining in minutes. It is
        * calculated by adding up the current capacity
        * and current for all of the batteries and then
        * dividing the current capacity by the current.
        * It is then rounded to the nearest 10 minutes.
        */
        __result += (int)estimatedMinutesRemaining;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>EpsStateTypeSupport</code>
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

        EpsState typedSrc = (EpsState) src;
        EpsState typedDst = this;
        super.copy_from(typedSrc);
        typedDst.batteries = (rapid.ext.astrobee.BatteryInfoSequence4) typedDst.batteries.copy_from(typedSrc.batteries);
        /** The percentage remaining of each battery added
        * up. This value can be over 100.
        */
        typedDst.batteryTotal = typedSrc.batteryTotal;
        /** The estimated time remaining in minutes. It is
        * calculated by adding up the current capacity
        * and current for all of the batteries and then
        * dividing the current capacity by the current.
        * It is then rounded to the nearest 10 minutes.
        */
        typedDst.estimatedMinutesRemaining = typedSrc.estimatedMinutesRemaining;

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
        /** The percentage remaining of each battery added
        * up. This value can be over 100.
        */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("batteryTotal: ").append(batteryTotal).append("\n");  
        /** The estimated time remaining in minutes. It is
        * calculated by adding up the current capacity
        * and current for all of the batteries and then
        * dividing the current capacity by the current.
        * It is then rounded to the nearest 10 minutes.
        */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("estimatedMinutesRemaining: ").append(estimatedMinutesRemaining).append("\n");  

        return strBuffer.toString();
    }

}
