

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

/** A fault within Astrobee.
* - timestamp: when this fault happened, in microseconds
* - code: an error code defined elsewhere
* - message: string containing why fault occurred
* - data: useful data that may accompany the fault
*/

public class Fault   implements Copyable, Serializable{

    public long timestamp= 0;
    public int code= 0;
    public String message=  "" ; /* maximum length = (128) */
    public rapid.KeyTypeValueSequence8 data = (rapid.KeyTypeValueSequence8)rapid.KeyTypeValueSequence8.create();

    public Fault() {

    }
    public Fault (Fault other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        Fault self;
        self = new  Fault();
        self.clear();
        return self;

    }

    public void clear() {

        timestamp= 0;
        code= 0;
        message=  ""; 
        if (data != null) {
            data.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        Fault otherObj = (Fault)o;

        if(timestamp != otherObj.timestamp) {
            return false;
        }
        if(code != otherObj.code) {
            return false;
        }
        if(!message.equals(otherObj.message)) {
            return false;
        }
        if(!data.equals(otherObj.data)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += (int)timestamp;
        __result += (int)code;
        __result += message.hashCode(); 
        __result += data.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>FaultTypeSupport</code>
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

        Fault typedSrc = (Fault) src;
        Fault typedDst = this;

        typedDst.timestamp = typedSrc.timestamp;
        typedDst.code = typedSrc.code;
        typedDst.message = typedSrc.message;
        typedDst.data = (rapid.KeyTypeValueSequence8) typedDst.data.copy_from(typedSrc.data);

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
        strBuffer.append("timestamp: ").append(timestamp).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("code: ").append(code).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("message: ").append(message).append("\n");  
        strBuffer.append(data.toString("data ", indent+1));

        return strBuffer.toString();
    }

}
