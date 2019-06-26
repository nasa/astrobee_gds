

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

/** Header of all messages. */

public class Header   implements Copyable, Serializable{

    /** Source name of the message. This is a key for filtering in DDS. */
    public String srcName=  "" ; /* maximum length = (32) */
    /**
    * Agent to which this message is being sent or from which this message was sent. This is also a key
    * for filtering in DDS.
    */
    public String assetName=  "" ; /* maximum length = (32) */
    /**
    * Timestamp is the number of microseconds elapsed since midnight proleptic
    * Coordinated Universal Time (UTC) of January 1, 1970, not counting leap seconds
    */
    public long timeStamp= 0;
    /**
    * If statusCode >= 0, then all is well. If statusCode < 0, there is some sort of error.
    * Each status shall have its own convention for error codes.
    */
    public int statusCode= 0;
    /**
    * Serial denotes a config/state pair (i.e. the code in a config header should match the code in the
    * corresponding state header).
    */
    public int serial= 0;

    public Header() {

        /** Source name of the message. This is a key for filtering in DDS. */
        /**
        * Agent to which this message is being sent or from which this message was sent. This is also a key
        * for filtering in DDS.
        */
        /**
        * Timestamp is the number of microseconds elapsed since midnight proleptic
        * Coordinated Universal Time (UTC) of January 1, 1970, not counting leap seconds
        */
        /**
        * If statusCode >= 0, then all is well. If statusCode < 0, there is some sort of error.
        * Each status shall have its own convention for error codes.
        */
        /**
        * Serial denotes a config/state pair (i.e. the code in a config header should match the code in the
        * corresponding state header).
        */

    }
    public Header (Header other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        Header self;
        self = new  Header();
        self.clear();
        return self;

    }

    public void clear() {

        /** Source name of the message. This is a key for filtering in DDS. */
        srcName=  ""; 
        /**
        * Agent to which this message is being sent or from which this message was sent. This is also a key
        * for filtering in DDS.
        */
        assetName=  ""; 
        /**
        * Timestamp is the number of microseconds elapsed since midnight proleptic
        * Coordinated Universal Time (UTC) of January 1, 1970, not counting leap seconds
        */
        timeStamp= 0;
        /**
        * If statusCode >= 0, then all is well. If statusCode < 0, there is some sort of error.
        * Each status shall have its own convention for error codes.
        */
        statusCode= 0;
        /**
        * Serial denotes a config/state pair (i.e. the code in a config header should match the code in the
        * corresponding state header).
        */
        serial= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        Header otherObj = (Header)o;

        /** Source name of the message. This is a key for filtering in DDS. */
        if(!srcName.equals(otherObj.srcName)) {
            return false;
        }
        /**
        * Agent to which this message is being sent or from which this message was sent. This is also a key
        * for filtering in DDS.
        */
        if(!assetName.equals(otherObj.assetName)) {
            return false;
        }
        /**
        * Timestamp is the number of microseconds elapsed since midnight proleptic
        * Coordinated Universal Time (UTC) of January 1, 1970, not counting leap seconds
        */
        if(timeStamp != otherObj.timeStamp) {
            return false;
        }
        /**
        * If statusCode >= 0, then all is well. If statusCode < 0, there is some sort of error.
        * Each status shall have its own convention for error codes.
        */
        if(statusCode != otherObj.statusCode) {
            return false;
        }
        /**
        * Serial denotes a config/state pair (i.e. the code in a config header should match the code in the
        * corresponding state header).
        */
        if(serial != otherObj.serial) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        /** Source name of the message. This is a key for filtering in DDS. */
        __result += srcName.hashCode(); 
        /**
        * Agent to which this message is being sent or from which this message was sent. This is also a key
        * for filtering in DDS.
        */
        __result += assetName.hashCode(); 
        /**
        * Timestamp is the number of microseconds elapsed since midnight proleptic
        * Coordinated Universal Time (UTC) of January 1, 1970, not counting leap seconds
        */
        __result += (int)timeStamp;
        /**
        * If statusCode >= 0, then all is well. If statusCode < 0, there is some sort of error.
        * Each status shall have its own convention for error codes.
        */
        __result += (int)statusCode;
        /**
        * Serial denotes a config/state pair (i.e. the code in a config header should match the code in the
        * corresponding state header).
        */
        __result += (int)serial;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>HeaderTypeSupport</code>
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

        Header typedSrc = (Header) src;
        Header typedDst = this;

        /** Source name of the message. This is a key for filtering in DDS. */
        typedDst.srcName = typedSrc.srcName;
        /**
        * Agent to which this message is being sent or from which this message was sent. This is also a key
        * for filtering in DDS.
        */
        typedDst.assetName = typedSrc.assetName;
        /**
        * Timestamp is the number of microseconds elapsed since midnight proleptic
        * Coordinated Universal Time (UTC) of January 1, 1970, not counting leap seconds
        */
        typedDst.timeStamp = typedSrc.timeStamp;
        /**
        * If statusCode >= 0, then all is well. If statusCode < 0, there is some sort of error.
        * Each status shall have its own convention for error codes.
        */
        typedDst.statusCode = typedSrc.statusCode;
        /**
        * Serial denotes a config/state pair (i.e. the code in a config header should match the code in the
        * corresponding state header).
        */
        typedDst.serial = typedSrc.serial;

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

        /** Source name of the message. This is a key for filtering in DDS. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("srcName: ").append(srcName).append("\n");  
        /**
        * Agent to which this message is being sent or from which this message was sent. This is also a key
        * for filtering in DDS.
        */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("assetName: ").append(assetName).append("\n");  
        /**
        * Timestamp is the number of microseconds elapsed since midnight proleptic
        * Coordinated Universal Time (UTC) of January 1, 1970, not counting leap seconds
        */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("timeStamp: ").append(timeStamp).append("\n");  
        /**
        * If statusCode >= 0, then all is well. If statusCode < 0, there is some sort of error.
        * Each status shall have its own convention for error codes.
        */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("statusCode: ").append(statusCode).append("\n");  
        /**
        * Serial denotes a config/state pair (i.e. the code in a config header should match the code in the
        * corresponding state header).
        */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("serial: ").append(serial).append("\n");  

        return strBuffer.toString();
    }

}
