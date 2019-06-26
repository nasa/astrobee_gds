

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

public class SingleQueue   implements Copyable, Serializable{

    public rapid.CommandRecordSeq queue =  new rapid.CommandRecordSeq(64);

    public SingleQueue() {

    }
    public SingleQueue (SingleQueue other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        SingleQueue self;
        self = new  SingleQueue();
        self.clear();
        return self;

    }

    public void clear() {

        if (queue != null) {
            queue.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        SingleQueue otherObj = (SingleQueue)o;

        if(!queue.equals(otherObj.queue)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += queue.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>SingleQueueTypeSupport</code>
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

        SingleQueue typedSrc = (SingleQueue) src;
        SingleQueue typedDst = this;

        typedDst.queue.copy_from(typedSrc.queue);

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

        CdrHelper.printIndent(strBuffer, indent+1);
        strBuffer.append("queue:\n");
        for(int i__ = 0; i__ < queue.size(); ++i__) {
            strBuffer.append(((rapid.CommandRecord)queue.get(i__)).toString(Integer.toString(i__),indent+2));
        }

        return strBuffer.toString();
    }

}
