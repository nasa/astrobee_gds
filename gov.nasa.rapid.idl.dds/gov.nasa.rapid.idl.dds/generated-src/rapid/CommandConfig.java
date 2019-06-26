

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
/**
* Message that sets up the possible commands an Agent can send and any arguments needed to perform said
* command.
*/

public class CommandConfig  extends rapid.Message implements Copyable, Serializable{

    /** Lists CommandDefs of all commands that can be performed by said Agent. */
    public rapid.SubsystemTypeSequence availableSubsystemTypes = (rapid.SubsystemTypeSequence)rapid.SubsystemTypeSequence.create();
    public rapid.SubsystemSequence availableSubsystems = (rapid.SubsystemSequence)rapid.SubsystemSequence.create();

    public CommandConfig() {

        super();

        /** Lists CommandDefs of all commands that can be performed by said Agent. */

    }
    public CommandConfig (CommandConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        CommandConfig self;
        self = new  CommandConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** Lists CommandDefs of all commands that can be performed by said Agent. */
        if (availableSubsystemTypes != null) {
            availableSubsystemTypes.clear();
        }
        if (availableSubsystems != null) {
            availableSubsystems.clear();
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

        CommandConfig otherObj = (CommandConfig)o;

        /** Lists CommandDefs of all commands that can be performed by said Agent. */
        if(!availableSubsystemTypes.equals(otherObj.availableSubsystemTypes)) {
            return false;
        }
        if(!availableSubsystems.equals(otherObj.availableSubsystems)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** Lists CommandDefs of all commands that can be performed by said Agent. */
        __result += availableSubsystemTypes.hashCode(); 
        __result += availableSubsystems.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>CommandConfigTypeSupport</code>
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

        CommandConfig typedSrc = (CommandConfig) src;
        CommandConfig typedDst = this;
        super.copy_from(typedSrc);
        /** Lists CommandDefs of all commands that can be performed by said Agent. */
        typedDst.availableSubsystemTypes = (rapid.SubsystemTypeSequence) typedDst.availableSubsystemTypes.copy_from(typedSrc.availableSubsystemTypes);
        typedDst.availableSubsystems = (rapid.SubsystemSequence) typedDst.availableSubsystems.copy_from(typedSrc.availableSubsystems);

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

        /** Lists CommandDefs of all commands that can be performed by said Agent. */
        strBuffer.append(availableSubsystemTypes.toString("availableSubsystemTypes ", indent+1));
        strBuffer.append(availableSubsystems.toString("availableSubsystems ", indent+1));

        return strBuffer.toString();
    }

}
