

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

public class DlpChannelConfig   implements Copyable, Serializable{

    public String name=  "" ; /* maximum length = (32) */
    public rapid.ext.arc.DlpWarnCriticalRange voltageRange = (rapid.ext.arc.DlpWarnCriticalRange)rapid.ext.arc.DlpWarnCriticalRange.create();
    public rapid.ext.arc.DlpWarnCriticalRange currentRange = (rapid.ext.arc.DlpWarnCriticalRange)rapid.ext.arc.DlpWarnCriticalRange.create();

    public DlpChannelConfig() {

    }
    public DlpChannelConfig (DlpChannelConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        DlpChannelConfig self;
        self = new  DlpChannelConfig();
        self.clear();
        return self;

    }

    public void clear() {

        name=  ""; 
        if (voltageRange != null) {
            voltageRange.clear();
        }
        if (currentRange != null) {
            currentRange.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        DlpChannelConfig otherObj = (DlpChannelConfig)o;

        if(!name.equals(otherObj.name)) {
            return false;
        }
        if(!voltageRange.equals(otherObj.voltageRange)) {
            return false;
        }
        if(!currentRange.equals(otherObj.currentRange)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += name.hashCode(); 
        __result += voltageRange.hashCode(); 
        __result += currentRange.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>DlpChannelConfigTypeSupport</code>
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

        DlpChannelConfig typedSrc = (DlpChannelConfig) src;
        DlpChannelConfig typedDst = this;

        typedDst.name = typedSrc.name;
        typedDst.voltageRange = (rapid.ext.arc.DlpWarnCriticalRange) typedDst.voltageRange.copy_from(typedSrc.voltageRange);
        typedDst.currentRange = (rapid.ext.arc.DlpWarnCriticalRange) typedDst.currentRange.copy_from(typedSrc.currentRange);

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
        strBuffer.append(voltageRange.toString("voltageRange ", indent+1));
        strBuffer.append(currentRange.toString("currentRange ", indent+1));

        return strBuffer.toString();
    }

}
