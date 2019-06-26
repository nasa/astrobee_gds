

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
* Command is a message used to send directives to an Agent.
*/

public class MacroConfig  extends rapid.Message implements Copyable, Serializable{

    /** Name of this macro. */
    public String name=  "" ; /* maximum length = (64) */
    /** The commands that are put onto the Queue when this macro is loaded. */
    public rapid.MacroCommandSequence128 commands = (rapid.MacroCommandSequence128)rapid.MacroCommandSequence128.create();
    /** Metadata for the entire plan */
    public rapid.KeyTypeValueSequence16 metaData = (rapid.KeyTypeValueSequence16)rapid.KeyTypeValueSequence16.create();
    /** Metadata fields found in each MacroCommnad */
    public rapid.KeyTypeSequence16 commandMetaDataSpec = (rapid.KeyTypeSequence16)rapid.KeyTypeSequence16.create();

    public MacroConfig() {

        super();

        /** Name of this macro. */
        /** The commands that are put onto the Queue when this macro is loaded. */
        /** Metadata for the entire plan */
        /** Metadata fields found in each MacroCommnad */

    }
    public MacroConfig (MacroConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        MacroConfig self;
        self = new  MacroConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** Name of this macro. */
        name=  ""; 
        /** The commands that are put onto the Queue when this macro is loaded. */
        if (commands != null) {
            commands.clear();
        }
        /** Metadata for the entire plan */
        if (metaData != null) {
            metaData.clear();
        }
        /** Metadata fields found in each MacroCommnad */
        if (commandMetaDataSpec != null) {
            commandMetaDataSpec.clear();
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

        MacroConfig otherObj = (MacroConfig)o;

        /** Name of this macro. */
        if(!name.equals(otherObj.name)) {
            return false;
        }
        /** The commands that are put onto the Queue when this macro is loaded. */
        if(!commands.equals(otherObj.commands)) {
            return false;
        }
        /** Metadata for the entire plan */
        if(!metaData.equals(otherObj.metaData)) {
            return false;
        }
        /** Metadata fields found in each MacroCommnad */
        if(!commandMetaDataSpec.equals(otherObj.commandMetaDataSpec)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** Name of this macro. */
        __result += name.hashCode(); 
        /** The commands that are put onto the Queue when this macro is loaded. */
        __result += commands.hashCode(); 
        /** Metadata for the entire plan */
        __result += metaData.hashCode(); 
        /** Metadata fields found in each MacroCommnad */
        __result += commandMetaDataSpec.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>MacroConfigTypeSupport</code>
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

        MacroConfig typedSrc = (MacroConfig) src;
        MacroConfig typedDst = this;
        super.copy_from(typedSrc);
        /** Name of this macro. */
        typedDst.name = typedSrc.name;
        /** The commands that are put onto the Queue when this macro is loaded. */
        typedDst.commands = (rapid.MacroCommandSequence128) typedDst.commands.copy_from(typedSrc.commands);
        /** Metadata for the entire plan */
        typedDst.metaData = (rapid.KeyTypeValueSequence16) typedDst.metaData.copy_from(typedSrc.metaData);
        /** Metadata fields found in each MacroCommnad */
        typedDst.commandMetaDataSpec = (rapid.KeyTypeSequence16) typedDst.commandMetaDataSpec.copy_from(typedSrc.commandMetaDataSpec);

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

        /** Name of this macro. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("name: ").append(name).append("\n");  
        /** The commands that are put onto the Queue when this macro is loaded. */
        strBuffer.append(commands.toString("commands ", indent+1));
        /** Metadata for the entire plan */
        strBuffer.append(metaData.toString("metaData ", indent+1));
        /** Metadata fields found in each MacroCommnad */
        strBuffer.append(commandMetaDataSpec.toString("commandMetaDataSpec ", indent+1));

        return strBuffer.toString();
    }

}
