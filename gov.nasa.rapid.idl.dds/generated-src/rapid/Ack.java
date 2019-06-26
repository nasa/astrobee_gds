

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
* Status of the command paired with this Ack.
* <ul>
*   <li>ACK_QUEUED: Sent by Sequencer when queueing Tasks.
*   <li>ACK_EXECUTING: Command has been started by the Bridge.
*   <li>ACK_REQUEUED: Only sent when a command in the ActiveQueue and a PAUSE is sent;
*                     command is REQUEUED in the PendingQueue preempting the next command
*                     in the PendingQueue
*   <li>ACK_COMPLETED: Sent when command is done.
* </ul>
*/
/**
* This status is sent along when it has completed
* <ul>
*   <li>ACK_COMPLETED_NOT: Task has not completed yet.
*   <li>ACK_COMPLETED_OK: Successful completion.
*   <li>ACK_COMPLETED_BAD_SYNTAX: Command not recognized, bad parameters, etc.
*   <li>ACK_COMPLETED_EXEC_FAILED: Failed to complete.
*   <li>ACK_COMPLETED_CANCELED: Canceled from queue.
* </ul>
*/
/**
* Ack is a Message that sends an acknowledgement of commands received.
*/

public class Ack  extends rapid.Message implements Copyable, Serializable{

    /** Command identifier of command being acknowledged. */
    public String cmdId=  "" ; /* maximum length = (64) */
    /** @see AckStatus. */
    public rapid.AckStatus status = (rapid.AckStatus)rapid.AckStatus.create();
    /** Details how the task completed. */
    public rapid.AckCompletedStatus completedStatus = (rapid.AckCompletedStatus)rapid.AckCompletedStatus.create();
    /** Message details any exceptions made during Ack transition. Analogous to exception message string. */
    public String message=  "" ; /* maximum length = (128) */

    public Ack() {

        super();

        /** Command identifier of command being acknowledged. */
        /** @see AckStatus. */
        /** Details how the task completed. */
        /** Message details any exceptions made during Ack transition. Analogous to exception message string. */

    }
    public Ack (Ack other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        Ack self;
        self = new  Ack();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** Command identifier of command being acknowledged. */
        cmdId=  ""; 
        /** @see AckStatus. */
        status = rapid.AckStatus.create();
        /** Details how the task completed. */
        completedStatus = rapid.AckCompletedStatus.create();
        /** Message details any exceptions made during Ack transition. Analogous to exception message string. */
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

        Ack otherObj = (Ack)o;

        /** Command identifier of command being acknowledged. */
        if(!cmdId.equals(otherObj.cmdId)) {
            return false;
        }
        /** @see AckStatus. */
        if(!status.equals(otherObj.status)) {
            return false;
        }
        /** Details how the task completed. */
        if(!completedStatus.equals(otherObj.completedStatus)) {
            return false;
        }
        /** Message details any exceptions made during Ack transition. Analogous to exception message string. */
        if(!message.equals(otherObj.message)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** Command identifier of command being acknowledged. */
        __result += cmdId.hashCode(); 
        /** @see AckStatus. */
        __result += status.hashCode(); 
        /** Details how the task completed. */
        __result += completedStatus.hashCode(); 
        /** Message details any exceptions made during Ack transition. Analogous to exception message string. */
        __result += message.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>AckTypeSupport</code>
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

        Ack typedSrc = (Ack) src;
        Ack typedDst = this;
        super.copy_from(typedSrc);
        /** Command identifier of command being acknowledged. */
        typedDst.cmdId = typedSrc.cmdId;
        /** @see AckStatus. */
        typedDst.status = (rapid.AckStatus) typedDst.status.copy_from(typedSrc.status);
        /** Details how the task completed. */
        typedDst.completedStatus = (rapid.AckCompletedStatus) typedDst.completedStatus.copy_from(typedSrc.completedStatus);
        /** Message details any exceptions made during Ack transition. Analogous to exception message string. */
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

        /** Command identifier of command being acknowledged. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("cmdId: ").append(cmdId).append("\n");  
        /** @see AckStatus. */
        strBuffer.append(status.toString("status ", indent+1));
        /** Details how the task completed. */
        strBuffer.append(completedStatus.toString("completedStatus ", indent+1));
        /** Message details any exceptions made during Ack transition. Analogous to exception message string. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("message: ").append(message).append("\n");  

        return strBuffer.toString();
    }

}
