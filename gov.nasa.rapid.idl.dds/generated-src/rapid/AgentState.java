

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
* AgentState message sends low-frequency updates of the state
* of a specific Agent.
*/

public class AgentState  extends rapid.Message implements Copyable, Serializable{

    /**
    * The AgentConfig message defines the names, data types
    * and order of the values contained in the sequence.
    */
    public rapid.ValueSequence64 values = (rapid.ValueSequence64)rapid.ValueSequence64.create();

    public AgentState() {

        super();

        /**
        * The AgentConfig message defines the names, data types
        * and order of the values contained in the sequence.
        */

    }
    public AgentState (AgentState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        AgentState self;
        self = new  AgentState();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /**
        * The AgentConfig message defines the names, data types
        * and order of the values contained in the sequence.
        */
        if (values != null) {
            values.clear();
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

        AgentState otherObj = (AgentState)o;

        /**
        * The AgentConfig message defines the names, data types
        * and order of the values contained in the sequence.
        */
        if(!values.equals(otherObj.values)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /**
        * The AgentConfig message defines the names, data types
        * and order of the values contained in the sequence.
        */
        __result += values.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>AgentStateTypeSupport</code>
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

        AgentState typedSrc = (AgentState) src;
        AgentState typedDst = this;
        super.copy_from(typedSrc);
        /**
        * The AgentConfig message defines the names, data types
        * and order of the values contained in the sequence.
        */
        typedDst.values = (rapid.ValueSequence64) typedDst.values.copy_from(typedSrc.values);

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

        /**
        * The AgentConfig message defines the names, data types
        * and order of the values contained in the sequence.
        */
        strBuffer.append(values.toString("values ", indent+1));

        return strBuffer.toString();
    }

}
