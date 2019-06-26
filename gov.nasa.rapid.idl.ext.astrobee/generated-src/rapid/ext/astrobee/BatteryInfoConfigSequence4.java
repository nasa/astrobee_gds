

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

public class BatteryInfoConfigSequence4   implements Copyable, Serializable{

    public rapid.ext.astrobee.BatteryInfoConfigSeq userData =  new rapid.ext.astrobee.BatteryInfoConfigSeq(4);

    public BatteryInfoConfigSequence4() {

    }
    public BatteryInfoConfigSequence4 (BatteryInfoConfigSequence4 other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        BatteryInfoConfigSequence4 self;
        self = new  BatteryInfoConfigSequence4();
        self.clear();
        return self;

    }

    public void clear() {

        if (userData != null) {
            userData.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        BatteryInfoConfigSequence4 otherObj = (BatteryInfoConfigSequence4)o;

        if(!userData.equals(otherObj.userData)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += userData.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>BatteryInfoConfigSequence4TypeSupport</code>
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

        BatteryInfoConfigSequence4 typedSrc = (BatteryInfoConfigSequence4) src;
        BatteryInfoConfigSequence4 typedDst = this;

        typedDst.userData.copy_from(typedSrc.userData);

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
        strBuffer.append("userData:\n");
        for(int i__ = 0; i__ < userData.size(); ++i__) {
            strBuffer.append(((rapid.ext.astrobee.BatteryInfoConfig)userData.get(i__)).toString(Integer.toString(i__),indent+2));
        }

        return strBuffer.toString();
    }

}
