

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
/**
* Holds information about a file that could be transferred to the ground from the robot.
*/

public class FileQueueState  extends rapid.Message implements Copyable, Serializable{

    /**
    * Connection status of the file queue. The queue will not send data as long as there is no reader
    * connected.
    */
    public boolean connected= false;
    public int bandwidth= 0;
    public int prefetchBandwidth= 0;
    /** The state of the individual channels. */
    public rapid.ChannelStateSequence channels = (rapid.ChannelStateSequence)rapid.ChannelStateSequence.create();

    public FileQueueState() {

        super();

        /**
        * Connection status of the file queue. The queue will not send data as long as there is no reader
        * connected.
        */
        /** The state of the individual channels. */

    }
    public FileQueueState (FileQueueState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        FileQueueState self;
        self = new  FileQueueState();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /**
        * Connection status of the file queue. The queue will not send data as long as there is no reader
        * connected.
        */
        connected= false;
        bandwidth= 0;
        prefetchBandwidth= 0;
        /** The state of the individual channels. */
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

        FileQueueState otherObj = (FileQueueState)o;

        /**
        * Connection status of the file queue. The queue will not send data as long as there is no reader
        * connected.
        */
        if(connected != otherObj.connected) {
            return false;
        }
        if(bandwidth != otherObj.bandwidth) {
            return false;
        }
        if(prefetchBandwidth != otherObj.prefetchBandwidth) {
            return false;
        }
        /** The state of the individual channels. */
        if(!channels.equals(otherObj.channels)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /**
        * Connection status of the file queue. The queue will not send data as long as there is no reader
        * connected.
        */
        __result += (connected == true)?1:0;
        __result += (int)bandwidth;
        __result += (int)prefetchBandwidth;
        /** The state of the individual channels. */
        __result += channels.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>FileQueueStateTypeSupport</code>
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

        FileQueueState typedSrc = (FileQueueState) src;
        FileQueueState typedDst = this;
        super.copy_from(typedSrc);
        /**
        * Connection status of the file queue. The queue will not send data as long as there is no reader
        * connected.
        */
        typedDst.connected = typedSrc.connected;
        typedDst.bandwidth = typedSrc.bandwidth;
        typedDst.prefetchBandwidth = typedSrc.prefetchBandwidth;
        /** The state of the individual channels. */
        typedDst.channels = (rapid.ChannelStateSequence) typedDst.channels.copy_from(typedSrc.channels);

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

        /**
        * Connection status of the file queue. The queue will not send data as long as there is no reader
        * connected.
        */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("connected: ").append(connected).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("bandwidth: ").append(bandwidth).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("prefetchBandwidth: ").append(prefetchBandwidth).append("\n");  
        /** The state of the individual channels. */
        strBuffer.append(channels.toString("channels ", indent+1));

        return strBuffer.toString();
    }

}
