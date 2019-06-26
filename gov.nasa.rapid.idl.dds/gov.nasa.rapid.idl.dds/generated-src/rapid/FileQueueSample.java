

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

public class FileQueueSample  extends rapid.Message implements Copyable, Serializable{

    /** The unique identifier for this file. */
    public String fileUuid=  "" ; /* maximum length = (64) */
    /** Bucket number of sliced file. */
    public int chunkId= 0;
    /** Total number of chunks to transfer. */
    public int numChunks= 0;
    /**
    * Bit bucket with the binary data chunk. Max size of the bucket is 128KByte. For samples with
    * chunkId < numChunks - 1, chunkData.length() == 131072.
    */
    public rapid.OctetSequence1K chunkData = (rapid.OctetSequence1K)rapid.OctetSequence1K.create();

    public FileQueueSample() {

        super();

        /** The unique identifier for this file. */
        /** Bucket number of sliced file. */
        /** Total number of chunks to transfer. */
        /**
        * Bit bucket with the binary data chunk. Max size of the bucket is 128KByte. For samples with
        * chunkId < numChunks - 1, chunkData.length() == 131072.
        */

    }
    public FileQueueSample (FileQueueSample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        FileQueueSample self;
        self = new  FileQueueSample();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** The unique identifier for this file. */
        fileUuid=  ""; 
        /** Bucket number of sliced file. */
        chunkId= 0;
        /** Total number of chunks to transfer. */
        numChunks= 0;
        /**
        * Bit bucket with the binary data chunk. Max size of the bucket is 128KByte. For samples with
        * chunkId < numChunks - 1, chunkData.length() == 131072.
        */
        if (chunkData != null) {
            chunkData.clear();
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

        FileQueueSample otherObj = (FileQueueSample)o;

        /** The unique identifier for this file. */
        if(!fileUuid.equals(otherObj.fileUuid)) {
            return false;
        }
        /** Bucket number of sliced file. */
        if(chunkId != otherObj.chunkId) {
            return false;
        }
        /** Total number of chunks to transfer. */
        if(numChunks != otherObj.numChunks) {
            return false;
        }
        /**
        * Bit bucket with the binary data chunk. Max size of the bucket is 128KByte. For samples with
        * chunkId < numChunks - 1, chunkData.length() == 131072.
        */
        if(!chunkData.equals(otherObj.chunkData)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** The unique identifier for this file. */
        __result += fileUuid.hashCode(); 
        /** Bucket number of sliced file. */
        __result += (int)chunkId;
        /** Total number of chunks to transfer. */
        __result += (int)numChunks;
        /**
        * Bit bucket with the binary data chunk. Max size of the bucket is 128KByte. For samples with
        * chunkId < numChunks - 1, chunkData.length() == 131072.
        */
        __result += chunkData.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>FileQueueSampleTypeSupport</code>
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

        FileQueueSample typedSrc = (FileQueueSample) src;
        FileQueueSample typedDst = this;
        super.copy_from(typedSrc);
        /** The unique identifier for this file. */
        typedDst.fileUuid = typedSrc.fileUuid;
        /** Bucket number of sliced file. */
        typedDst.chunkId = typedSrc.chunkId;
        /** Total number of chunks to transfer. */
        typedDst.numChunks = typedSrc.numChunks;
        /**
        * Bit bucket with the binary data chunk. Max size of the bucket is 128KByte. For samples with
        * chunkId < numChunks - 1, chunkData.length() == 131072.
        */
        typedDst.chunkData = (rapid.OctetSequence1K) typedDst.chunkData.copy_from(typedSrc.chunkData);

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
        /** Bucket number of sliced file. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("chunkId: ").append(chunkId).append("\n");  
        /** Total number of chunks to transfer. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("numChunks: ").append(numChunks).append("\n");  
        /**
        * Bit bucket with the binary data chunk. Max size of the bucket is 128KByte. For samples with
        * chunkId < numChunks - 1, chunkData.length() == 131072.
        */
        strBuffer.append(chunkData.toString("chunkData ", indent+1));

        return strBuffer.toString();
    }

}
