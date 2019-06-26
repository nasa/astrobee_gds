

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
/**
* The state of the data partitions on Astrobee
*/

public class EpsConfig  extends rapid.Message implements Copyable, Serializable{

    public rapid.ext.astrobee.BatteryInfoConfigSequence4 batteries = (rapid.ext.astrobee.BatteryInfoConfigSequence4)rapid.ext.astrobee.BatteryInfoConfigSequence4.create();

    public EpsConfig() {

        super();

    }
    public EpsConfig (EpsConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        EpsConfig self;
        self = new  EpsConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        if (batteries != null) {
            batteries.clear();
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

        EpsConfig otherObj = (EpsConfig)o;

        if(!batteries.equals(otherObj.batteries)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += batteries.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>EpsConfigTypeSupport</code>
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

        EpsConfig typedSrc = (EpsConfig) src;
        EpsConfig typedDst = this;
        super.copy_from(typedSrc);
        typedDst.batteries = (rapid.ext.astrobee.BatteryInfoConfigSequence4) typedDst.batteries.copy_from(typedSrc.batteries);

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

        return strBuffer.toString();
    }

}
