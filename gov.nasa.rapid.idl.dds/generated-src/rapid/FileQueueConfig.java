

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
* The file queue channel configuration. The file queue can have multiple channels. Bandwidth is split up
* between the channels through a token system.
*/
/** Sequence of channel specifications. */
/** Holds information about a file that could be transferred to the ground from the robot. */

public class FileQueueConfig  extends rapid.Message implements Copyable, Serializable{

    public rapid.ChannelConfigSequence channels = (rapid.ChannelConfigSequence)rapid.ChannelConfigSequence.create();

    public FileQueueConfig() {

        super();

    }
    public FileQueueConfig (FileQueueConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        FileQueueConfig self;
        self = new  FileQueueConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        if (channels != null) {
            channels.clear();
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

        FileQueueConfig otherObj = (FileQueueConfig)o;

        if(!channels.equals(otherObj.channels)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += channels.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>FileQueueConfigTypeSupport</code>
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

        FileQueueConfig typedSrc = (FileQueueConfig) src;
        FileQueueConfig typedDst = this;
        super.copy_from(typedSrc);
        typedDst.channels = (rapid.ChannelConfigSequence) typedDst.channels.copy_from(typedSrc.channels);

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

        strBuffer.append(channels.toString("channels ", indent+1));

        return strBuffer.toString();
    }

}
