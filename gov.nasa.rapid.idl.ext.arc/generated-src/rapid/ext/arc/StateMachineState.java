

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

/**
* StateMachineState message delivers the position of the Agent. Corresponding Float32Config sets up the
* coordinate frame and specifies how the Transform3D.rot field is to be interpreted for pose and velocity.
*/

public class StateMachineState  extends rapid.Message implements Copyable, Serializable{

    public String subsystem=  "" ; /* maximum length = (32) */
    public String stateMachine=  "" ; /* maximum length = (32) */
    public byte currentState= 0;
    public byte previousState= 0;
    public byte lastEvent= 0;
    public String message=  "" ; /* maximum length = (128) */

    public StateMachineState() {

        super();

    }
    public StateMachineState (StateMachineState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        StateMachineState self;
        self = new  StateMachineState();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        subsystem=  ""; 
        stateMachine=  ""; 
        currentState= 0;
        previousState= 0;
        lastEvent= 0;
        message=  ""; 
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

        StateMachineState otherObj = (StateMachineState)o;

        if(!subsystem.equals(otherObj.subsystem)) {
            return false;
        }
        if(!stateMachine.equals(otherObj.stateMachine)) {
            return false;
        }
        if(currentState != otherObj.currentState) {
            return false;
        }
        if(previousState != otherObj.previousState) {
            return false;
        }
        if(lastEvent != otherObj.lastEvent) {
            return false;
        }
        if(!message.equals(otherObj.message)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += subsystem.hashCode(); 
        __result += stateMachine.hashCode(); 
        __result += (int)currentState;
        __result += (int)previousState;
        __result += (int)lastEvent;
        __result += message.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>StateMachineStateTypeSupport</code>
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

        StateMachineState typedSrc = (StateMachineState) src;
        StateMachineState typedDst = this;
        super.copy_from(typedSrc);
        typedDst.subsystem = typedSrc.subsystem;
        typedDst.stateMachine = typedSrc.stateMachine;
        typedDst.currentState = typedSrc.currentState;
        typedDst.previousState = typedSrc.previousState;
        typedDst.lastEvent = typedSrc.lastEvent;
        typedDst.message = typedSrc.message;

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
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("currentState: ").append(currentState).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("previousState: ").append(previousState).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("lastEvent: ").append(lastEvent).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("message: ").append(message).append("\n");  

        return strBuffer.toString();
    }

}
