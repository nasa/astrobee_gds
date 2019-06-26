

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

public class SubsystemType   implements Copyable, Serializable{

    /** Name of subsystem type. */
    public String name=  "" ; /* maximum length = (32) */
    public rapid.CommandDefSequence commands = (rapid.CommandDefSequence)rapid.CommandDefSequence.create();

    public SubsystemType() {

        /** Name of subsystem type. */

    }
    public SubsystemType (SubsystemType other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        SubsystemType self;
        self = new  SubsystemType();
        self.clear();
        return self;

    }

    public void clear() {

        /** Name of subsystem type. */
        name=  ""; 
        if (commands != null) {
            commands.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        SubsystemType otherObj = (SubsystemType)o;

        /** Name of subsystem type. */
        if(!name.equals(otherObj.name)) {
            return false;
        }
        if(!commands.equals(otherObj.commands)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        /** Name of subsystem type. */
        __result += name.hashCode(); 
        __result += commands.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>SubsystemTypeTypeSupport</code>
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

        SubsystemType typedSrc = (SubsystemType) src;
        SubsystemType typedDst = this;

        /** Name of subsystem type. */
        typedDst.name = typedSrc.name;
        typedDst.commands = (rapid.CommandDefSequence) typedDst.commands.copy_from(typedSrc.commands);

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

        /** Name of subsystem type. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("name: ").append(name).append("\n");  
        strBuffer.append(commands.toString("commands ", indent+1));

        return strBuffer.toString();
    }

}
