

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
* AccessControlState is a recurring message type that provides information
* about who has control of a specified Agent and who has requested control
* of that Agent.
*/

public class AccessControlState  extends rapid.Message implements Copyable, Serializable{

    /** Specifies agentId that currently controls the Agent. */
    public String controller=  "" ; /* maximum length = (32) */
    /** Specifies agentIds that have requested control of the Agent. */
    public rapid.String32Sequence16 requestors = (rapid.String32Sequence16)rapid.String32Sequence16.create();

    public AccessControlState() {

        super();

        /** Specifies agentId that currently controls the Agent. */
        /** Specifies agentIds that have requested control of the Agent. */

    }
    public AccessControlState (AccessControlState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        AccessControlState self;
        self = new  AccessControlState();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** Specifies agentId that currently controls the Agent. */
        controller=  ""; 
        /** Specifies agentIds that have requested control of the Agent. */
        if (requestors != null) {
            requestors.clear();
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

        AccessControlState otherObj = (AccessControlState)o;

        /** Specifies agentId that currently controls the Agent. */
        if(!controller.equals(otherObj.controller)) {
            return false;
        }
        /** Specifies agentIds that have requested control of the Agent. */
        if(!requestors.equals(otherObj.requestors)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** Specifies agentId that currently controls the Agent. */
        __result += controller.hashCode(); 
        /** Specifies agentIds that have requested control of the Agent. */
        __result += requestors.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>AccessControlStateTypeSupport</code>
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

        AccessControlState typedSrc = (AccessControlState) src;
        AccessControlState typedDst = this;
        super.copy_from(typedSrc);
        /** Specifies agentId that currently controls the Agent. */
        typedDst.controller = typedSrc.controller;
        /** Specifies agentIds that have requested control of the Agent. */
        typedDst.requestors = (rapid.String32Sequence16) typedDst.requestors.copy_from(typedSrc.requestors);

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

        /** Specifies agentId that currently controls the Agent. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("controller: ").append(controller).append("\n");  
        /** Specifies agentIds that have requested control of the Agent. */
        strBuffer.append(requestors.toString("requestors ", indent+1));

        return strBuffer.toString();
    }

}
