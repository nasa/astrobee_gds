

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
* ChannelStatus is used to indicate whether a RAPID File Queue (part of the RAPID File Transfer Service)
* is currently capable of transferring files (Active) or is temporarily refraining from transferring files
* (Paused).
* <ul>
*   <li>FILE_QUEUE_CHANNEL_ACTIVE: Capable of transferring files.
*   <li>FILE_QUEUE_CHANNEL_PAUSED: Temporarily refraining from transferring files.
* </ul>
*/

public class ChannelStateSequence   implements Copyable, Serializable{

    public rapid.ChannelStateSeq userData =  new rapid.ChannelStateSeq(100);

    public ChannelStateSequence() {

    }
    public ChannelStateSequence (ChannelStateSequence other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        ChannelStateSequence self;
        self = new  ChannelStateSequence();
        self.clear();
        return self;

    }

    public void clear() {

        if (userData != null) {
            userData.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        ChannelStateSequence otherObj = (ChannelStateSequence)o;

        if(!userData.equals(otherObj.userData)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += userData.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>ChannelStateSequenceTypeSupport</code>
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

        ChannelStateSequence typedSrc = (ChannelStateSequence) src;
        ChannelStateSequence typedDst = this;

        typedDst.userData.copy_from(typedSrc.userData);

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
        strBuffer.append("userData:\n");
        for(int i__ = 0; i__ < userData.size(); ++i__) {
            strBuffer.append(((rapid.ChannelState)userData.get(i__)).toString(Integer.toString(i__),indent+2));
        }

        return strBuffer.toString();
    }

}
