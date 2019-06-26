

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

public class BatteryController   implements Copyable, Serializable{

    public rapid.ext.arc.BatterySlot [] batterySlots=  new rapid.ext.arc.BatterySlot [8];
    public rapid.ext.arc.Battery [] batteries=  new rapid.ext.arc.Battery [8];

    public BatteryController() {

        for(int i1__ = 0; i1__< 8; ++i1__){

            batterySlots[i1__]= (rapid.ext.arc.BatterySlot) rapid.ext.arc.BatterySlot.create();
        }

        for(int i1__ = 0; i1__< 8; ++i1__){

            batteries[i1__]= (rapid.ext.arc.Battery) rapid.ext.arc.Battery.create();
        }

    }
    public BatteryController (BatteryController other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        BatteryController self;
        self = new  BatteryController();
        self.clear();
        return self;

    }

    public void clear() {

        for(int i1__ = 0; i1__< 8; ++i1__){

            if ( batterySlots[i1__] != null) {
                batterySlots[i1__].clear();
            }
        }

        for(int i1__ = 0; i1__< 8; ++i1__){

            if ( batteries[i1__] != null) {
                batteries[i1__].clear();
            }
        }

    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        BatteryController otherObj = (BatteryController)o;

        for(int i1__ = 0; i1__< 8; ++i1__){

            if(!batterySlots[i1__].equals(otherObj.batterySlots[i1__])) {
                return false;
            }
        }

        for(int i1__ = 0; i1__< 8; ++i1__){

            if(!batteries[i1__].equals(otherObj.batteries[i1__])) {
                return false;
            }
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        for(int i1__ = 0; i1__< 8; ++i1__){

            __result += batterySlots[i1__].hashCode(); 
        }

        for(int i1__ = 0; i1__< 8; ++i1__){

            __result += batteries[i1__].hashCode(); 
        }

        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>BatteryControllerTypeSupport</code>
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

        BatteryController typedSrc = (BatteryController) src;
        BatteryController typedDst = this;

        for(int i1__ = 0; i1__< 8; ++i1__){

            typedDst.batterySlots[i1__] = (rapid.ext.arc.BatterySlot) typedDst.batterySlots[i1__].copy_from(typedSrc.batterySlots[i1__]);
        }

        for(int i1__ = 0; i1__< 8; ++i1__){

            typedDst.batteries[i1__] = (rapid.ext.arc.Battery) typedDst.batteries[i1__].copy_from(typedSrc.batteries[i1__]);
        }

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
        strBuffer.append("batterySlots:\n");
        for(int i1__ = 0; i1__< 8; ++i1__){

            strBuffer.append(batterySlots[i1__].toString(
                "["+Integer.toString(i1__)+"]",indent+2));
        }

        CdrHelper.printIndent(strBuffer, indent+1);
        strBuffer.append("batteries:\n");
        for(int i1__ = 0; i1__< 8; ++i1__){

            strBuffer.append(batteries[i1__].toString(
                "["+Integer.toString(i1__)+"]",indent+2));
        }

        return strBuffer.toString();
    }

}
