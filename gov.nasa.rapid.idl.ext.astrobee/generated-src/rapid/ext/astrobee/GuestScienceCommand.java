

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

public class GuestScienceCommand   implements Copyable, Serializable{

    /** Name of command */
    public String name=  "" ; /* maximum length = (32) */
    /** Actual syntax of the command */
    public String command=  "" ; /* maximum length = (128) */

    public GuestScienceCommand() {

        /** Name of command */
        /** Actual syntax of the command */

    }
    public GuestScienceCommand (GuestScienceCommand other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        GuestScienceCommand self;
        self = new  GuestScienceCommand();
        self.clear();
        return self;

    }

    public void clear() {

        /** Name of command */
        name=  ""; 
        /** Actual syntax of the command */
        command=  ""; 
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        GuestScienceCommand otherObj = (GuestScienceCommand)o;

        /** Name of command */
        if(!name.equals(otherObj.name)) {
            return false;
        }
        /** Actual syntax of the command */
        if(!command.equals(otherObj.command)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        /** Name of command */
        __result += name.hashCode(); 
        /** Actual syntax of the command */
        __result += command.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>GuestScienceCommandTypeSupport</code>
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

        GuestScienceCommand typedSrc = (GuestScienceCommand) src;
        GuestScienceCommand typedDst = this;

        /** Name of command */
        typedDst.name = typedSrc.name;
        /** Actual syntax of the command */
        typedDst.command = typedSrc.command;

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

        /** Name of command */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("name: ").append(name).append("\n");  
        /** Actual syntax of the command */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("command: ").append(command).append("\n");  

        return strBuffer.toString();
    }

}
