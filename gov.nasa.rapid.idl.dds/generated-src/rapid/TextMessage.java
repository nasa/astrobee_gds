

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
* Importance level of the message.
* <ul>
*   <li>MSG_DEBUG:
*   <li>MSG_INFO:
*   <li>MSG_ATTENTION: Important, but not in a bad way.
*   <li>MSG_WARN: Something potentially bad happened.
*   <li>MSG_ERROR: Something bad definitely happened.
*   <li>MSG_CRITICAL: Something really bad happened.
* </ul>
*/
/**
* TextMessage is a message that allows RAPID application users to chat with each other or for logging-style
* activities.
*/

public class TextMessage  extends rapid.Message implements Copyable, Serializable{

    /** Importance level of message. */
    public rapid.MessageLevel level = (rapid.MessageLevel)rapid.MessageLevel.create();
    /** What message is pertaining to. */
    public String category=  "" ; /* maximum length = (64) */
    /** Actual text message. */
    public String message=  "" ; /* maximum length = (2048) */

    public TextMessage() {

        super();

        /** Importance level of message. */
        /** What message is pertaining to. */
        /** Actual text message. */

    }
    public TextMessage (TextMessage other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        TextMessage self;
        self = new  TextMessage();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** Importance level of message. */
        level = rapid.MessageLevel.create();
        /** What message is pertaining to. */
        category=  ""; 
        /** Actual text message. */
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

        TextMessage otherObj = (TextMessage)o;

        /** Importance level of message. */
        if(!level.equals(otherObj.level)) {
            return false;
        }
        /** What message is pertaining to. */
        if(!category.equals(otherObj.category)) {
            return false;
        }
        /** Actual text message. */
        if(!message.equals(otherObj.message)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** Importance level of message. */
        __result += level.hashCode(); 
        /** What message is pertaining to. */
        __result += category.hashCode(); 
        /** Actual text message. */
        __result += message.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>TextMessageTypeSupport</code>
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

        TextMessage typedSrc = (TextMessage) src;
        TextMessage typedDst = this;
        super.copy_from(typedSrc);
        /** Importance level of message. */
        typedDst.level = (rapid.MessageLevel) typedDst.level.copy_from(typedSrc.level);
        /** What message is pertaining to. */
        typedDst.category = typedSrc.category;
        /** Actual text message. */
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

        /** Importance level of message. */
        strBuffer.append(level.toString("level ", indent+1));
        /** What message is pertaining to. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("category: ").append(category).append("\n");  
        /** Actual text message. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("message: ").append(message).append("\n");  

        return strBuffer.toString();
    }

}
