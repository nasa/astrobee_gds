

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

public class ChannelState   implements Copyable, Serializable{

    /** Status of file in queue. */
    public rapid.ChannelStatus status = (rapid.ChannelStatus)rapid.ChannelStatus.create();
    /** Size of queued data volume. */
    public long queuedDataVolume= 0;
    /** Number of queued files */
    public short queuedFiles= 0;

    public ChannelState() {

        /** Status of file in queue. */
        /** Size of queued data volume. */
        /** Number of queued files */

    }
    public ChannelState (ChannelState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        ChannelState self;
        self = new  ChannelState();
        self.clear();
        return self;

    }

    public void clear() {

        /** Status of file in queue. */
        status = rapid.ChannelStatus.create();
        /** Size of queued data volume. */
        queuedDataVolume= 0;
        /** Number of queued files */
        queuedFiles= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        ChannelState otherObj = (ChannelState)o;

        /** Status of file in queue. */
        if(!status.equals(otherObj.status)) {
            return false;
        }
        /** Size of queued data volume. */
        if(queuedDataVolume != otherObj.queuedDataVolume) {
            return false;
        }
        /** Number of queued files */
        if(queuedFiles != otherObj.queuedFiles) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        /** Status of file in queue. */
        __result += status.hashCode(); 
        /** Size of queued data volume. */
        __result += (int)queuedDataVolume;
        /** Number of queued files */
        __result += (int)queuedFiles;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>ChannelStateTypeSupport</code>
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

        ChannelState typedSrc = (ChannelState) src;
        ChannelState typedDst = this;

        /** Status of file in queue. */
        typedDst.status = (rapid.ChannelStatus) typedDst.status.copy_from(typedSrc.status);
        /** Size of queued data volume. */
        typedDst.queuedDataVolume = typedSrc.queuedDataVolume;
        /** Number of queued files */
        typedDst.queuedFiles = typedSrc.queuedFiles;

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

        /** Status of file in queue. */
        strBuffer.append(status.toString("status ", indent+1));
        /** Size of queued data volume. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("queuedDataVolume: ").append(queuedDataVolume).append("\n");  
        /** Number of queued files */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("queuedFiles: ").append(queuedFiles).append("\n");  

        return strBuffer.toString();
    }

}
