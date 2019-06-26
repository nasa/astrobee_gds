

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

public class CommandDef   implements Copyable, Serializable{

    /** The name of the command, to be used as cmdName in Command. */
    public String name=  "" ; /* maximum length = (64) */
    /** Describes whether or not this command can be aborted once begun. */
    public boolean abortable= false;
    /** Describes whether or not this command can be suspended once begun. */
    public boolean suspendable= false;
    /**
    * The arguments needed to send this command: key is the name of the argument, type is the expected
    * data type of the argument.
    */
    public rapid.KeyTypeSequence16 parameters = (rapid.KeyTypeSequence16)rapid.KeyTypeSequence16.create();

    public CommandDef() {

        /** The name of the command, to be used as cmdName in Command. */
        /** Describes whether or not this command can be aborted once begun. */
        /** Describes whether or not this command can be suspended once begun. */
        /**
        * The arguments needed to send this command: key is the name of the argument, type is the expected
        * data type of the argument.
        */

    }
    public CommandDef (CommandDef other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        CommandDef self;
        self = new  CommandDef();
        self.clear();
        return self;

    }

    public void clear() {

        /** The name of the command, to be used as cmdName in Command. */
        name=  ""; 
        /** Describes whether or not this command can be aborted once begun. */
        abortable= false;
        /** Describes whether or not this command can be suspended once begun. */
        suspendable= false;
        /**
        * The arguments needed to send this command: key is the name of the argument, type is the expected
        * data type of the argument.
        */
        if (parameters != null) {
            parameters.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        CommandDef otherObj = (CommandDef)o;

        /** The name of the command, to be used as cmdName in Command. */
        if(!name.equals(otherObj.name)) {
            return false;
        }
        /** Describes whether or not this command can be aborted once begun. */
        if(abortable != otherObj.abortable) {
            return false;
        }
        /** Describes whether or not this command can be suspended once begun. */
        if(suspendable != otherObj.suspendable) {
            return false;
        }
        /**
        * The arguments needed to send this command: key is the name of the argument, type is the expected
        * data type of the argument.
        */
        if(!parameters.equals(otherObj.parameters)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        /** The name of the command, to be used as cmdName in Command. */
        __result += name.hashCode(); 
        /** Describes whether or not this command can be aborted once begun. */
        __result += (abortable == true)?1:0;
        /** Describes whether or not this command can be suspended once begun. */
        __result += (suspendable == true)?1:0;
        /**
        * The arguments needed to send this command: key is the name of the argument, type is the expected
        * data type of the argument.
        */
        __result += parameters.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>CommandDefTypeSupport</code>
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

        CommandDef typedSrc = (CommandDef) src;
        CommandDef typedDst = this;

        /** The name of the command, to be used as cmdName in Command. */
        typedDst.name = typedSrc.name;
        /** Describes whether or not this command can be aborted once begun. */
        typedDst.abortable = typedSrc.abortable;
        /** Describes whether or not this command can be suspended once begun. */
        typedDst.suspendable = typedSrc.suspendable;
        /**
        * The arguments needed to send this command: key is the name of the argument, type is the expected
        * data type of the argument.
        */
        typedDst.parameters = (rapid.KeyTypeSequence16) typedDst.parameters.copy_from(typedSrc.parameters);

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

        /** The name of the command, to be used as cmdName in Command. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("name: ").append(name).append("\n");  
        /** Describes whether or not this command can be aborted once begun. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("abortable: ").append(abortable).append("\n");  
        /** Describes whether or not this command can be suspended once begun. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("suspendable: ").append(suspendable).append("\n");  
        /**
        * The arguments needed to send this command: key is the name of the argument, type is the expected
        * data type of the argument.
        */
        strBuffer.append(parameters.toString("parameters ", indent+1));

        return strBuffer.toString();
    }

}
