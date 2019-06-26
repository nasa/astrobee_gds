

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
* <ul>
*   <li>Q_STATUS_INIT:
*   <li>Q_STATUS_OUTBOUND: SENT
*   <li>Q_STATUS_LOST: SENT
*   <li>Q_STATUS_INLINE: PENDING
*   <li>Q_STATUS_PREEMPTED: PENDING
*   <li>Q_STATUS_INPROCESS: ACTIVE
*   <li>Q_STATUS_PAUSED: ACTIVE
*   <li>Q_STATUS_CANCELED: COMPLETED
*   <li>Q_STATUS_SUCCEEDED: COMPLETED
*   <li>Q_STATUS_FAILED: COMPLETED
*   <li>Q_STATUS_ALIEN_PENDING:
*   <li>Q_STATUS_ALIEN_ACTIVE:
*   <li>Q_STATUS_ALIEN_COMPLETED:
*   <li>Q_STATUS_FORGOTTEN_BY_ROBOT:
* </ul>
*/
/**
* ResultType describes the result of a command on the completed queue.
* <ul>
*   <li>Q_RESULT_INIT:
*   <li>Q_RESULT_SUCCEEDED:
*   <li>Q_RESULT_FAILED:
* </ul>
*/
/** CommandRecord holds a command, its status and its result. */
/** SingleQueue holds a sequence of CommandRecords. Maximum length for a SingleQueue is 64. */
/**
* QueueState is a message that holds single queues of pending, active, completed and sent commands. This
* is primarily used with a Sequencer that handles the queueing of commands.
*/

public class QueueState  extends rapid.Message implements Copyable, Serializable{

    public rapid.SingleQueue pending = (rapid.SingleQueue)rapid.SingleQueue.create();
    public rapid.SingleQueue active = (rapid.SingleQueue)rapid.SingleQueue.create();
    public rapid.SingleQueue completed = (rapid.SingleQueue)rapid.SingleQueue.create();
    public rapid.SingleQueue sent = (rapid.SingleQueue)rapid.SingleQueue.create();

    public QueueState() {

        super();

    }
    public QueueState (QueueState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        QueueState self;
        self = new  QueueState();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        if (pending != null) {
            pending.clear();
        }
        if (active != null) {
            active.clear();
        }
        if (completed != null) {
            completed.clear();
        }
        if (sent != null) {
            sent.clear();
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

        QueueState otherObj = (QueueState)o;

        if(!pending.equals(otherObj.pending)) {
            return false;
        }
        if(!active.equals(otherObj.active)) {
            return false;
        }
        if(!completed.equals(otherObj.completed)) {
            return false;
        }
        if(!sent.equals(otherObj.sent)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += pending.hashCode(); 
        __result += active.hashCode(); 
        __result += completed.hashCode(); 
        __result += sent.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>QueueStateTypeSupport</code>
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

        QueueState typedSrc = (QueueState) src;
        QueueState typedDst = this;
        super.copy_from(typedSrc);
        typedDst.pending = (rapid.SingleQueue) typedDst.pending.copy_from(typedSrc.pending);
        typedDst.active = (rapid.SingleQueue) typedDst.active.copy_from(typedSrc.active);
        typedDst.completed = (rapid.SingleQueue) typedDst.completed.copy_from(typedSrc.completed);
        typedDst.sent = (rapid.SingleQueue) typedDst.sent.copy_from(typedSrc.sent);

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

        strBuffer.append(pending.toString("pending ", indent+1));
        strBuffer.append(active.toString("active ", indent+1));
        strBuffer.append(completed.toString("completed ", indent+1));
        strBuffer.append(sent.toString("sent ", indent+1));

        return strBuffer.toString();
    }

}
