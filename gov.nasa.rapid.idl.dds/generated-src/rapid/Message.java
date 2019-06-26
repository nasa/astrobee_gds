

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

/** Definition of message, which is the foundation of all other message types. */

public class Message   implements Copyable, Serializable{

    /** The header in a message is the key for filtering in DDS. */
    public rapid.Header hdr = (rapid.Header)rapid.Header.create();

    public Message() {

        /** The header in a message is the key for filtering in DDS. */

    }
    public Message (Message other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        Message self;
        self = new  Message();
        self.clear();
        return self;

    }

    public void clear() {

        /** The header in a message is the key for filtering in DDS. */
        if (hdr != null) {
            hdr.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        Message otherObj = (Message)o;

        /** The header in a message is the key for filtering in DDS. */
        if(!hdr.equals(otherObj.hdr)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        /** The header in a message is the key for filtering in DDS. */
        __result += hdr.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>MessageTypeSupport</code>
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

        Message typedSrc = (Message) src;
        Message typedDst = this;

        /** The header in a message is the key for filtering in DDS. */
        typedDst.hdr = (rapid.Header) typedDst.hdr.copy_from(typedSrc.hdr);

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

        /** The header in a message is the key for filtering in DDS. */
        strBuffer.append(hdr.toString("hdr ", indent+1));

        return strBuffer.toString();
    }

}
