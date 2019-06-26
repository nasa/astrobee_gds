

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

public class CommandRecord   implements Copyable, Serializable{

    public rapid.Command cmd = (rapid.Command)rapid.Command.create();
    public rapid.ResultType trResult = (rapid.ResultType)rapid.ResultType.create();
    public rapid.StatusType trStatus = (rapid.StatusType)rapid.StatusType.create();

    public CommandRecord() {

    }
    public CommandRecord (CommandRecord other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        CommandRecord self;
        self = new  CommandRecord();
        self.clear();
        return self;

    }

    public void clear() {

        if (cmd != null) {
            cmd.clear();
        }
        trResult = rapid.ResultType.create();
        trStatus = rapid.StatusType.create();
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        CommandRecord otherObj = (CommandRecord)o;

        if(!cmd.equals(otherObj.cmd)) {
            return false;
        }
        if(!trResult.equals(otherObj.trResult)) {
            return false;
        }
        if(!trStatus.equals(otherObj.trStatus)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += cmd.hashCode(); 
        __result += trResult.hashCode(); 
        __result += trStatus.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>CommandRecordTypeSupport</code>
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

        CommandRecord typedSrc = (CommandRecord) src;
        CommandRecord typedDst = this;

        typedDst.cmd = (rapid.Command) typedDst.cmd.copy_from(typedSrc.cmd);
        typedDst.trResult = (rapid.ResultType) typedDst.trResult.copy_from(typedSrc.trResult);
        typedDst.trStatus = (rapid.StatusType) typedDst.trStatus.copy_from(typedSrc.trStatus);

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

        strBuffer.append(cmd.toString("cmd ", indent+1));
        strBuffer.append(trResult.toString("trResult ", indent+1));
        strBuffer.append(trStatus.toString("trStatus ", indent+1));

        return strBuffer.toString();
    }

}
