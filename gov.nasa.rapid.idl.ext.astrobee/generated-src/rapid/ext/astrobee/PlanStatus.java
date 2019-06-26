

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.astrobee;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

/** The completed status of a thing in the past.
* Specifies the thing and the Completed status
* Also specifies the duration that the thing took
* @see AckCompletedStatus
*/
/** A secquence of status events */
/**
* The status of the current plan that may or may not be running.
*/

public class PlanStatus  extends rapid.Message implements Copyable, Serializable{

    /**  the name of the currently executing/paused plan */
    public String planName=  "" ; /* maximum length = (32) */
    /**  The current station or segment */
    public int currentPoint= 0;
    /**  The current command within the station/segment. -1 means no command */
    public int currentCommand= 0;
    /**  Status of the currently executing 'thing' @see AckStatus */
    public rapid.AckStatus currentStatus = (rapid.AckStatus)rapid.AckStatus.create();
    /**  Status of the last N things (N=64) @see AckCompletedStatus */
    public rapid.ext.astrobee.StatusSequence statusHistory = (rapid.ext.astrobee.StatusSequence)rapid.ext.astrobee.StatusSequence.create();

    public PlanStatus() {

        super();

        /**  the name of the currently executing/paused plan */
        /**  The current station or segment */
        /**  The current command within the station/segment. -1 means no command */
        /**  Status of the currently executing 'thing' @see AckStatus */
        /**  Status of the last N things (N=64) @see AckCompletedStatus */

    }
    public PlanStatus (PlanStatus other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        PlanStatus self;
        self = new  PlanStatus();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /**  the name of the currently executing/paused plan */
        planName=  ""; 
        /**  The current station or segment */
        currentPoint= 0;
        /**  The current command within the station/segment. -1 means no command */
        currentCommand= 0;
        /**  Status of the currently executing 'thing' @see AckStatus */
        currentStatus = rapid.AckStatus.create();
        /**  Status of the last N things (N=64) @see AckCompletedStatus */
        if (statusHistory != null) {
            statusHistory.clear();
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

        PlanStatus otherObj = (PlanStatus)o;

        /**  the name of the currently executing/paused plan */
        if(!planName.equals(otherObj.planName)) {
            return false;
        }
        /**  The current station or segment */
        if(currentPoint != otherObj.currentPoint) {
            return false;
        }
        /**  The current command within the station/segment. -1 means no command */
        if(currentCommand != otherObj.currentCommand) {
            return false;
        }
        /**  Status of the currently executing 'thing' @see AckStatus */
        if(!currentStatus.equals(otherObj.currentStatus)) {
            return false;
        }
        /**  Status of the last N things (N=64) @see AckCompletedStatus */
        if(!statusHistory.equals(otherObj.statusHistory)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /**  the name of the currently executing/paused plan */
        __result += planName.hashCode(); 
        /**  The current station or segment */
        __result += (int)currentPoint;
        /**  The current command within the station/segment. -1 means no command */
        __result += (int)currentCommand;
        /**  Status of the currently executing 'thing' @see AckStatus */
        __result += currentStatus.hashCode(); 
        /**  Status of the last N things (N=64) @see AckCompletedStatus */
        __result += statusHistory.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>PlanStatusTypeSupport</code>
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

        PlanStatus typedSrc = (PlanStatus) src;
        PlanStatus typedDst = this;
        super.copy_from(typedSrc);
        /**  the name of the currently executing/paused plan */
        typedDst.planName = typedSrc.planName;
        /**  The current station or segment */
        typedDst.currentPoint = typedSrc.currentPoint;
        /**  The current command within the station/segment. -1 means no command */
        typedDst.currentCommand = typedSrc.currentCommand;
        /**  Status of the currently executing 'thing' @see AckStatus */
        typedDst.currentStatus = (rapid.AckStatus) typedDst.currentStatus.copy_from(typedSrc.currentStatus);
        /**  Status of the last N things (N=64) @see AckCompletedStatus */
        typedDst.statusHistory = (rapid.ext.astrobee.StatusSequence) typedDst.statusHistory.copy_from(typedSrc.statusHistory);

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

        /**  the name of the currently executing/paused plan */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("planName: ").append(planName).append("\n");  
        /**  The current station or segment */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("currentPoint: ").append(currentPoint).append("\n");  
        /**  The current command within the station/segment. -1 means no command */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("currentCommand: ").append(currentCommand).append("\n");  
        /**  Status of the currently executing 'thing' @see AckStatus */
        strBuffer.append(currentStatus.toString("currentStatus ", indent+1));
        /**  Status of the last N things (N=64) @see AckCompletedStatus */
        strBuffer.append(statusHistory.toString("statusHistory ", indent+1));

        return strBuffer.toString();
    }

}
