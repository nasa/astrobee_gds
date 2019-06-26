

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

public class MacroCommand   implements Copyable, Serializable{

    /** Name of command being sent. Should get this from list of possible commands in CommandConfig. */
    public String cmdName=  "" ; /* maximum length = (64) */
    /** Suffix to be appended to the cmdId of the loadMacro Command.
    *  Each cmdIdSuffix within a macro MUST be unique. When the macro is loaded
    *  into the Queue, the cmdId of each command in the Macro will be
    *  <cmdId>-<cmdIdSuffix> where cmdId is the command id of the loadMacro command
    *  that loaded the Macro.
    */
    public String cmdIdSuffix=  "" ; /* maximum length = (64) */
    /** Name of subsystem to which cmdName belongs. */
    public String subsysName=  "" ; /* maximum length = (32) */
    /** Data type and value of the arguments for this particular command. */
    public rapid.ParameterSequence16 arguments = (rapid.ParameterSequence16)rapid.ParameterSequence16.create();
    /** Data type and value for any metaData fields contained in the MacroConfig. */
    public rapid.ParameterSequence16 metaData = (rapid.ParameterSequence16)rapid.ParameterSequence16.create();

    public MacroCommand() {

        /** Name of command being sent. Should get this from list of possible commands in CommandConfig. */
        /** Suffix to be appended to the cmdId of the loadMacro Command.
        *  Each cmdIdSuffix within a macro MUST be unique. When the macro is loaded
        *  into the Queue, the cmdId of each command in the Macro will be
        *  <cmdId>-<cmdIdSuffix> where cmdId is the command id of the loadMacro command
        *  that loaded the Macro.
        */
        /** Name of subsystem to which cmdName belongs. */
        /** Data type and value of the arguments for this particular command. */
        /** Data type and value for any metaData fields contained in the MacroConfig. */

    }
    public MacroCommand (MacroCommand other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        MacroCommand self;
        self = new  MacroCommand();
        self.clear();
        return self;

    }

    public void clear() {

        /** Name of command being sent. Should get this from list of possible commands in CommandConfig. */
        cmdName=  ""; 
        /** Suffix to be appended to the cmdId of the loadMacro Command.
        *  Each cmdIdSuffix within a macro MUST be unique. When the macro is loaded
        *  into the Queue, the cmdId of each command in the Macro will be
        *  <cmdId>-<cmdIdSuffix> where cmdId is the command id of the loadMacro command
        *  that loaded the Macro.
        */
        cmdIdSuffix=  ""; 
        /** Name of subsystem to which cmdName belongs. */
        subsysName=  ""; 
        /** Data type and value of the arguments for this particular command. */
        if (arguments != null) {
            arguments.clear();
        }
        /** Data type and value for any metaData fields contained in the MacroConfig. */
        if (metaData != null) {
            metaData.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        MacroCommand otherObj = (MacroCommand)o;

        /** Name of command being sent. Should get this from list of possible commands in CommandConfig. */
        if(!cmdName.equals(otherObj.cmdName)) {
            return false;
        }
        /** Suffix to be appended to the cmdId of the loadMacro Command.
        *  Each cmdIdSuffix within a macro MUST be unique. When the macro is loaded
        *  into the Queue, the cmdId of each command in the Macro will be
        *  <cmdId>-<cmdIdSuffix> where cmdId is the command id of the loadMacro command
        *  that loaded the Macro.
        */
        if(!cmdIdSuffix.equals(otherObj.cmdIdSuffix)) {
            return false;
        }
        /** Name of subsystem to which cmdName belongs. */
        if(!subsysName.equals(otherObj.subsysName)) {
            return false;
        }
        /** Data type and value of the arguments for this particular command. */
        if(!arguments.equals(otherObj.arguments)) {
            return false;
        }
        /** Data type and value for any metaData fields contained in the MacroConfig. */
        if(!metaData.equals(otherObj.metaData)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        /** Name of command being sent. Should get this from list of possible commands in CommandConfig. */
        __result += cmdName.hashCode(); 
        /** Suffix to be appended to the cmdId of the loadMacro Command.
        *  Each cmdIdSuffix within a macro MUST be unique. When the macro is loaded
        *  into the Queue, the cmdId of each command in the Macro will be
        *  <cmdId>-<cmdIdSuffix> where cmdId is the command id of the loadMacro command
        *  that loaded the Macro.
        */
        __result += cmdIdSuffix.hashCode(); 
        /** Name of subsystem to which cmdName belongs. */
        __result += subsysName.hashCode(); 
        /** Data type and value of the arguments for this particular command. */
        __result += arguments.hashCode(); 
        /** Data type and value for any metaData fields contained in the MacroConfig. */
        __result += metaData.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>MacroCommandTypeSupport</code>
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

        MacroCommand typedSrc = (MacroCommand) src;
        MacroCommand typedDst = this;

        /** Name of command being sent. Should get this from list of possible commands in CommandConfig. */
        typedDst.cmdName = typedSrc.cmdName;
        /** Suffix to be appended to the cmdId of the loadMacro Command.
        *  Each cmdIdSuffix within a macro MUST be unique. When the macro is loaded
        *  into the Queue, the cmdId of each command in the Macro will be
        *  <cmdId>-<cmdIdSuffix> where cmdId is the command id of the loadMacro command
        *  that loaded the Macro.
        */
        typedDst.cmdIdSuffix = typedSrc.cmdIdSuffix;
        /** Name of subsystem to which cmdName belongs. */
        typedDst.subsysName = typedSrc.subsysName;
        /** Data type and value of the arguments for this particular command. */
        typedDst.arguments = (rapid.ParameterSequence16) typedDst.arguments.copy_from(typedSrc.arguments);
        /** Data type and value for any metaData fields contained in the MacroConfig. */
        typedDst.metaData = (rapid.ParameterSequence16) typedDst.metaData.copy_from(typedSrc.metaData);

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

        /** Name of command being sent. Should get this from list of possible commands in CommandConfig. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("cmdName: ").append(cmdName).append("\n");  
        /** Suffix to be appended to the cmdId of the loadMacro Command.
        *  Each cmdIdSuffix within a macro MUST be unique. When the macro is loaded
        *  into the Queue, the cmdId of each command in the Macro will be
        *  <cmdId>-<cmdIdSuffix> where cmdId is the command id of the loadMacro command
        *  that loaded the Macro.
        */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("cmdIdSuffix: ").append(cmdIdSuffix).append("\n");  
        /** Name of subsystem to which cmdName belongs. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("subsysName: ").append(subsysName).append("\n");  
        /** Data type and value of the arguments for this particular command. */
        strBuffer.append(arguments.toString("arguments ", indent+1));
        /** Data type and value for any metaData fields contained in the MacroConfig. */
        strBuffer.append(metaData.toString("metaData ", indent+1));

        return strBuffer.toString();
    }

}
