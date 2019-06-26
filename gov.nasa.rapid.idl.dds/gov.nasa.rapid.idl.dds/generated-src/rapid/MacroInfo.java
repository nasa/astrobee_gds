

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

/**
* Meta-information for an associated MacroConfig as seen by the MacroManager.
*/

public class MacroInfo   implements Copyable, Serializable{

    /** Name of the macro. */
    public String name=  "" ; /* maximum length = (64) */
    /** Version number of the macro. As provided in by hdr.serial of the latest corresponding MacroConfig instance. */
    public int serial= 0;
    /** True if the macro passed basic checking by the MacroManager. */
    public boolean accepted= false;

    public MacroInfo() {

        /** Name of the macro. */
        /** Version number of the macro. As provided in by hdr.serial of the latest corresponding MacroConfig instance. */
        /** True if the macro passed basic checking by the MacroManager. */

    }
    public MacroInfo (MacroInfo other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        MacroInfo self;
        self = new  MacroInfo();
        self.clear();
        return self;

    }

    public void clear() {

        /** Name of the macro. */
        name=  ""; 
        /** Version number of the macro. As provided in by hdr.serial of the latest corresponding MacroConfig instance. */
        serial= 0;
        /** True if the macro passed basic checking by the MacroManager. */
        accepted= false;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        MacroInfo otherObj = (MacroInfo)o;

        /** Name of the macro. */
        if(!name.equals(otherObj.name)) {
            return false;
        }
        /** Version number of the macro. As provided in by hdr.serial of the latest corresponding MacroConfig instance. */
        if(serial != otherObj.serial) {
            return false;
        }
        /** True if the macro passed basic checking by the MacroManager. */
        if(accepted != otherObj.accepted) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        /** Name of the macro. */
        __result += name.hashCode(); 
        /** Version number of the macro. As provided in by hdr.serial of the latest corresponding MacroConfig instance. */
        __result += (int)serial;
        /** True if the macro passed basic checking by the MacroManager. */
        __result += (accepted == true)?1:0;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>MacroInfoTypeSupport</code>
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

        MacroInfo typedSrc = (MacroInfo) src;
        MacroInfo typedDst = this;

        /** Name of the macro. */
        typedDst.name = typedSrc.name;
        /** Version number of the macro. As provided in by hdr.serial of the latest corresponding MacroConfig instance. */
        typedDst.serial = typedSrc.serial;
        /** True if the macro passed basic checking by the MacroManager. */
        typedDst.accepted = typedSrc.accepted;

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

        /** Name of the macro. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("name: ").append(name).append("\n");  
        /** Version number of the macro. As provided in by hdr.serial of the latest corresponding MacroConfig instance. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("serial: ").append(serial).append("\n");  
        /** True if the macro passed basic checking by the MacroManager. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("accepted: ").append(accepted).append("\n");  

        return strBuffer.toString();
    }

}
