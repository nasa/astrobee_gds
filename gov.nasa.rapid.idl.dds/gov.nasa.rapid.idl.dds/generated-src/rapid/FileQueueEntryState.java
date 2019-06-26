

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
* <ul>
*   <li>RAPID_FILE_PREFETCH_PENDING:
*   <li>RAPID_FILE_PREFETCHING:
*   <li>RAPID_FILE_PENDING:
*   <li>RAPID_FILE_ACTIVE:
*   <li>RAPID_FILE_PAUSED:
*   <li>RAPID_FILE_DONE:
*   <li>RAPID_FILE_CANCELED:
*   <li>RAPID_FILE_ERROR:
* </ul>
*/
/** Holds information about a file that could be transferred to the ground from the robot. */

public class FileQueueEntryState  extends rapid.Message implements Copyable, Serializable{

    /** The unique identifier for this file. */
    public String fileUuid=  "" ; /* maximum length = (64) */
    /** Status of file in queue. */
    public rapid.FileTransferStatus status = (rapid.FileTransferStatus)rapid.FileTransferStatus.create();
    /** Number of chunks already sent. */
    public int chunksSent= 0;
    /** Total number of chunks to transfer. */
    public int numChunks= 0;
    public long submissionTime= 0;
    public float priority= 0;
    public short channelId= 0;

    public FileQueueEntryState() {

        super();

        /** The unique identifier for this file. */
        /** Status of file in queue. */
        /** Number of chunks already sent. */
        /** Total number of chunks to transfer. */

    }
    public FileQueueEntryState (FileQueueEntryState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        FileQueueEntryState self;
        self = new  FileQueueEntryState();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** The unique identifier for this file. */
        fileUuid=  ""; 
        /** Status of file in queue. */
        status = rapid.FileTransferStatus.create();
        /** Number of chunks already sent. */
        chunksSent= 0;
        /** Total number of chunks to transfer. */
        numChunks= 0;
        submissionTime= 0;
        priority= 0;
        channelId= 0;
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

        FileQueueEntryState otherObj = (FileQueueEntryState)o;

        /** The unique identifier for this file. */
        if(!fileUuid.equals(otherObj.fileUuid)) {
            return false;
        }
        /** Status of file in queue. */
        if(!status.equals(otherObj.status)) {
            return false;
        }
        /** Number of chunks already sent. */
        if(chunksSent != otherObj.chunksSent) {
            return false;
        }
        /** Total number of chunks to transfer. */
        if(numChunks != otherObj.numChunks) {
            return false;
        }
        if(submissionTime != otherObj.submissionTime) {
            return false;
        }
        if(priority != otherObj.priority) {
            return false;
        }
        if(channelId != otherObj.channelId) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** The unique identifier for this file. */
        __result += fileUuid.hashCode(); 
        /** Status of file in queue. */
        __result += status.hashCode(); 
        /** Number of chunks already sent. */
        __result += (int)chunksSent;
        /** Total number of chunks to transfer. */
        __result += (int)numChunks;
        __result += (int)submissionTime;
        __result += (int)priority;
        __result += (int)channelId;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>FileQueueEntryStateTypeSupport</code>
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

        FileQueueEntryState typedSrc = (FileQueueEntryState) src;
        FileQueueEntryState typedDst = this;
        super.copy_from(typedSrc);
        /** The unique identifier for this file. */
        typedDst.fileUuid = typedSrc.fileUuid;
        /** Status of file in queue. */
        typedDst.status = (rapid.FileTransferStatus) typedDst.status.copy_from(typedSrc.status);
        /** Number of chunks already sent. */
        typedDst.chunksSent = typedSrc.chunksSent;
        /** Total number of chunks to transfer. */
        typedDst.numChunks = typedSrc.numChunks;
        typedDst.submissionTime = typedSrc.submissionTime;
        typedDst.priority = typedSrc.priority;
        typedDst.channelId = typedSrc.channelId;

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

        /** The unique identifier for this file. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("fileUuid: ").append(fileUuid).append("\n");  
        /** Status of file in queue. */
        strBuffer.append(status.toString("status ", indent+1));
        /** Number of chunks already sent. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("chunksSent: ").append(chunksSent).append("\n");  
        /** Total number of chunks to transfer. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("numChunks: ").append(numChunks).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("submissionTime: ").append(submissionTime).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("priority: ").append(priority).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("channelId: ").append(channelId).append("\n");  

        return strBuffer.toString();
    }

}
