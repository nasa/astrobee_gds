

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
* Holds information about a file that could be transferred to the ground from the robot.
*/

public class FileQueueReceiverSample  extends rapid.Message implements Copyable, Serializable{

    /**
    * Connection status to the file queue.
    */
    public boolean connected= false;
    /**
    * Number of bufferd but unprocessed chunks.
    */
    public int bufferedChunks= 0;
    /**
    * Number of processed chunks.
    */
    public int processedChunks= 0;
    /**
    * Number of completed files.
    */
    public int completedFiles= 0;

    public FileQueueReceiverSample() {

        super();

        /**
        * Connection status to the file queue.
        */
        /**
        * Number of bufferd but unprocessed chunks.
        */
        /**
        * Number of processed chunks.
        */
        /**
        * Number of completed files.
        */

    }
    public FileQueueReceiverSample (FileQueueReceiverSample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        FileQueueReceiverSample self;
        self = new  FileQueueReceiverSample();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /**
        * Connection status to the file queue.
        */
        connected= false;
        /**
        * Number of bufferd but unprocessed chunks.
        */
        bufferedChunks= 0;
        /**
        * Number of processed chunks.
        */
        processedChunks= 0;
        /**
        * Number of completed files.
        */
        completedFiles= 0;
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

        FileQueueReceiverSample otherObj = (FileQueueReceiverSample)o;

        /**
        * Connection status to the file queue.
        */
        if(connected != otherObj.connected) {
            return false;
        }
        /**
        * Number of bufferd but unprocessed chunks.
        */
        if(bufferedChunks != otherObj.bufferedChunks) {
            return false;
        }
        /**
        * Number of processed chunks.
        */
        if(processedChunks != otherObj.processedChunks) {
            return false;
        }
        /**
        * Number of completed files.
        */
        if(completedFiles != otherObj.completedFiles) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /**
        * Connection status to the file queue.
        */
        __result += (connected == true)?1:0;
        /**
        * Number of bufferd but unprocessed chunks.
        */
        __result += (int)bufferedChunks;
        /**
        * Number of processed chunks.
        */
        __result += (int)processedChunks;
        /**
        * Number of completed files.
        */
        __result += (int)completedFiles;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>FileQueueReceiverSampleTypeSupport</code>
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

        FileQueueReceiverSample typedSrc = (FileQueueReceiverSample) src;
        FileQueueReceiverSample typedDst = this;
        super.copy_from(typedSrc);
        /**
        * Connection status to the file queue.
        */
        typedDst.connected = typedSrc.connected;
        /**
        * Number of bufferd but unprocessed chunks.
        */
        typedDst.bufferedChunks = typedSrc.bufferedChunks;
        /**
        * Number of processed chunks.
        */
        typedDst.processedChunks = typedSrc.processedChunks;
        /**
        * Number of completed files.
        */
        typedDst.completedFiles = typedSrc.completedFiles;

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
        * Connection status to the file queue.
        */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("connected: ").append(connected).append("\n");  
        /**
        * Number of bufferd but unprocessed chunks.
        */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("bufferedChunks: ").append(bufferedChunks).append("\n");  
        /**
        * Number of processed chunks.
        */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("processedChunks: ").append(processedChunks).append("\n");  
        /**
        * Number of completed files.
        */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("completedFiles: ").append(completedFiles).append("\n");  

        return strBuffer.toString();
    }

}
