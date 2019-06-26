

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.arc;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

/** StateMachineConfig message sets up configuration for StateMachineSample messages. */

public class StateMachineConfig  extends rapid.Message implements Copyable, Serializable{

    public String subsystem=  "" ; /* maximum length = (32) */
    public String stateMachine=  "" ; /* maximum length = (32) */
    public rapid.String32Sequence128 states = (rapid.String32Sequence128)rapid.String32Sequence128.create();
    public rapid.String32Sequence128 events = (rapid.String32Sequence128)rapid.String32Sequence128.create();
    public rapid.ext.arc.TransitionSequence16K transitions = (rapid.ext.arc.TransitionSequence16K)rapid.ext.arc.TransitionSequence16K.create();

    public StateMachineConfig() {

        super();

    }
    public StateMachineConfig (StateMachineConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        StateMachineConfig self;
        self = new  StateMachineConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        subsystem=  ""; 
        stateMachine=  ""; 
        if (states != null) {
            states.clear();
        }
        if (events != null) {
            events.clear();
        }
        if (transitions != null) {
            transitions.clear();
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

        StateMachineConfig otherObj = (StateMachineConfig)o;

        if(!subsystem.equals(otherObj.subsystem)) {
            return false;
        }
        if(!stateMachine.equals(otherObj.stateMachine)) {
            return false;
        }
        if(!states.equals(otherObj.states)) {
            return false;
        }
        if(!events.equals(otherObj.events)) {
            return false;
        }
        if(!transitions.equals(otherObj.transitions)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += subsystem.hashCode(); 
        __result += stateMachine.hashCode(); 
        __result += states.hashCode(); 
        __result += events.hashCode(); 
        __result += transitions.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>StateMachineConfigTypeSupport</code>
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

        StateMachineConfig typedSrc = (StateMachineConfig) src;
        StateMachineConfig typedDst = this;
        super.copy_from(typedSrc);
        typedDst.subsystem = typedSrc.subsystem;
        typedDst.stateMachine = typedSrc.stateMachine;
        typedDst.states = (rapid.String32Sequence128) typedDst.states.copy_from(typedSrc.states);
        typedDst.events = (rapid.String32Sequence128) typedDst.events.copy_from(typedSrc.events);
        typedDst.transitions = (rapid.ext.arc.TransitionSequence16K) typedDst.transitions.copy_from(typedSrc.transitions);

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

        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("subsystem: ").append(subsystem).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("stateMachine: ").append(stateMachine).append("\n");  
        strBuffer.append(states.toString("states ", indent+1));
        strBuffer.append(events.toString("events ", indent+1));
        strBuffer.append(transitions.toString("transitions ", indent+1));

        return strBuffer.toString();
    }

}
