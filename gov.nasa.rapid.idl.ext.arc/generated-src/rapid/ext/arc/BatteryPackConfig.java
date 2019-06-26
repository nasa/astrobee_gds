

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

public class BatteryPackConfig  extends rapid.Message implements Copyable, Serializable{

    public rapid.String32Sequence16 controllerNames = (rapid.String32Sequence16)rapid.String32Sequence16.create();

    public BatteryPackConfig() {

        super();

    }
    public BatteryPackConfig (BatteryPackConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        BatteryPackConfig self;
        self = new  BatteryPackConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        if (controllerNames != null) {
            controllerNames.clear();
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

        BatteryPackConfig otherObj = (BatteryPackConfig)o;

        if(!controllerNames.equals(otherObj.controllerNames)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += controllerNames.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>BatteryPackConfigTypeSupport</code>
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

        BatteryPackConfig typedSrc = (BatteryPackConfig) src;
        BatteryPackConfig typedDst = this;
        super.copy_from(typedSrc);
        typedDst.controllerNames = (rapid.String32Sequence16) typedDst.controllerNames.copy_from(typedSrc.controllerNames);

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

        strBuffer.append(controllerNames.toString("controllerNames ", indent+1));

        return strBuffer.toString();
    }

}
