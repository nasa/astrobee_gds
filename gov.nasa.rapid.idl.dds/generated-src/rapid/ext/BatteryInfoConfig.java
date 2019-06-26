

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

public class BatteryInfoConfig   implements Copyable, Serializable{

    public String name=  "" ; /* maximum length = (32) */
    public byte percentageLow= 0;
    public byte percentageCritical= 0;

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

        name=  ""; 
        percentageLow= 0;
        percentageCritical= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        BatteryInfoConfig otherObj = (BatteryInfoConfig)o;

        if(!name.equals(otherObj.name)) {
            return false;
        }
        if(percentageLow != otherObj.percentageLow) {
            return false;
        }
        if(percentageCritical != otherObj.percentageCritical) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += name.hashCode(); 
        __result += (int)percentageLow;
        __result += (int)percentageCritical;
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

        typedDst.name = typedSrc.name;
        typedDst.percentageLow = typedSrc.percentageLow;
        typedDst.percentageCritical = typedSrc.percentageCritical;

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
        strBuffer.append("name: ").append(name).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("percentageLow: ").append(percentageLow).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("percentageCritical: ").append(percentageCritical).append("\n");  

        return strBuffer.toString();
    }

}
