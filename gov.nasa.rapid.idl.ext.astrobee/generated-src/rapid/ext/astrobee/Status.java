

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

public class Status   implements Copyable, Serializable{

    public int point= 0;
    public int command= 0;
    public int duration= 0;
    public rapid.AckCompletedStatus status = (rapid.AckCompletedStatus)rapid.AckCompletedStatus.create();

    public Status() {

    }
    public Status (Status other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        Status self;
        self = new  Status();
        self.clear();
        return self;

    }

    public void clear() {

        point= 0;
        command= 0;
        duration= 0;
        status = rapid.AckCompletedStatus.create();
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        Status otherObj = (Status)o;

        if(point != otherObj.point) {
            return false;
        }
        if(command != otherObj.command) {
            return false;
        }
        if(duration != otherObj.duration) {
            return false;
        }
        if(!status.equals(otherObj.status)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += (int)point;
        __result += (int)command;
        __result += (int)duration;
        __result += status.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>StatusTypeSupport</code>
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

        Status typedSrc = (Status) src;
        Status typedDst = this;

        typedDst.point = typedSrc.point;
        typedDst.command = typedSrc.command;
        typedDst.duration = typedSrc.duration;
        typedDst.status = (rapid.AckCompletedStatus) typedDst.status.copy_from(typedSrc.status);

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
        strBuffer.append("point: ").append(point).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("command: ").append(command).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("duration: ").append(duration).append("\n");  
        strBuffer.append(status.toString("status ", indent+1));

        return strBuffer.toString();
    }

}
