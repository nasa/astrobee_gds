

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
/** Sequence of MacroInfo
*
* An asset can store at max 128 macros.
*/

public class MacroState  extends rapid.Message implements Copyable, Serializable{

    /** All of the MacroConfigs that this agent knows about. */
    public rapid.MacroInfoSequence macros = (rapid.MacroInfoSequence)rapid.MacroInfoSequence.create();

    public MacroState() {

        super();

        /** All of the MacroConfigs that this agent knows about. */

    }
    public MacroState (MacroState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        MacroState self;
        self = new  MacroState();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** All of the MacroConfigs that this agent knows about. */
        if (macros != null) {
            macros.clear();
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

        MacroState otherObj = (MacroState)o;

        /** All of the MacroConfigs that this agent knows about. */
        if(!macros.equals(otherObj.macros)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** All of the MacroConfigs that this agent knows about. */
        __result += macros.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>MacroStateTypeSupport</code>
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

        MacroState typedSrc = (MacroState) src;
        MacroState typedDst = this;
        super.copy_from(typedSrc);
        /** All of the MacroConfigs that this agent knows about. */
        typedDst.macros = (rapid.MacroInfoSequence) typedDst.macros.copy_from(typedSrc.macros);

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

        /** All of the MacroConfigs that this agent knows about. */
        strBuffer.append(macros.toString("macros ", indent+1));

        return strBuffer.toString();
    }

}
