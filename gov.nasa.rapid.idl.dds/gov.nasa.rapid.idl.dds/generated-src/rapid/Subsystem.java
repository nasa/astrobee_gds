

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

/** A command definition. Arguments will define name of argument as Key, type of argument as Type. */

public class Subsystem   implements Copyable, Serializable{

    /** Name of subsystem instance. */
    public String name=  "" ; /* maximum length = (32) */
    /** Has to match a subsystem type name. */
    public String subsystemTypeName=  "" ; /* maximum length = (32) */

    public Subsystem() {

        /** Name of subsystem instance. */
        /** Has to match a subsystem type name. */

    }
    public Subsystem (Subsystem other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        Subsystem self;
        self = new  Subsystem();
        self.clear();
        return self;

    }

    public void clear() {

        /** Name of subsystem instance. */
        name=  ""; 
        /** Has to match a subsystem type name. */
        subsystemTypeName=  ""; 
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        Subsystem otherObj = (Subsystem)o;

        /** Name of subsystem instance. */
        if(!name.equals(otherObj.name)) {
            return false;
        }
        /** Has to match a subsystem type name. */
        if(!subsystemTypeName.equals(otherObj.subsystemTypeName)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        /** Name of subsystem instance. */
        __result += name.hashCode(); 
        /** Has to match a subsystem type name. */
        __result += subsystemTypeName.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>SubsystemTypeSupport</code>
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

        Subsystem typedSrc = (Subsystem) src;
        Subsystem typedDst = this;

        /** Name of subsystem instance. */
        typedDst.name = typedSrc.name;
        /** Has to match a subsystem type name. */
        typedDst.subsystemTypeName = typedSrc.subsystemTypeName;

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

        /** Name of subsystem instance. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("name: ").append(name).append("\n");  
        /** Has to match a subsystem type name. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("subsystemTypeName: ").append(subsystemTypeName).append("\n");  

        return strBuffer.toString();
    }

}
