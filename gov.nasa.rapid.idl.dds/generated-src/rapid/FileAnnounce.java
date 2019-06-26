

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

public class FileAnnounce  extends rapid.Message implements Copyable, Serializable{

    /** The unique identifier for this file. */
    public String fileUuid=  "" ; /* maximum length = (64) */
    /** URI of where the file is located. */
    public String fileLocator=  "" ; /* maximum length = (256) */
    /** Total size of the file in bytes. */
    public long fileSize= 0;
    /** Additional key/value pairs of metadata such as group-id or mime-type. */
    public rapid.KeyTypeValueSequence16 metaData = (rapid.KeyTypeValueSequence16)rapid.KeyTypeValueSequence16.create();

    public FileAnnounce() {

        super();

        /** The unique identifier for this file. */
        /** URI of where the file is located. */
        /** Total size of the file in bytes. */
        /** Additional key/value pairs of metadata such as group-id or mime-type. */

    }
    public FileAnnounce (FileAnnounce other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        FileAnnounce self;
        self = new  FileAnnounce();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** The unique identifier for this file. */
        fileUuid=  ""; 
        /** URI of where the file is located. */
        fileLocator=  ""; 
        /** Total size of the file in bytes. */
        fileSize= 0;
        /** Additional key/value pairs of metadata such as group-id or mime-type. */
        if (metaData != null) {
            metaData.clear();
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

        FileAnnounce otherObj = (FileAnnounce)o;

        /** The unique identifier for this file. */
        if(!fileUuid.equals(otherObj.fileUuid)) {
            return false;
        }
        /** URI of where the file is located. */
        if(!fileLocator.equals(otherObj.fileLocator)) {
            return false;
        }
        /** Total size of the file in bytes. */
        if(fileSize != otherObj.fileSize) {
            return false;
        }
        /** Additional key/value pairs of metadata such as group-id or mime-type. */
        if(!metaData.equals(otherObj.metaData)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** The unique identifier for this file. */
        __result += fileUuid.hashCode(); 
        /** URI of where the file is located. */
        __result += fileLocator.hashCode(); 
        /** Total size of the file in bytes. */
        __result += (int)fileSize;
        /** Additional key/value pairs of metadata such as group-id or mime-type. */
        __result += metaData.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>FileAnnounceTypeSupport</code>
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

        FileAnnounce typedSrc = (FileAnnounce) src;
        FileAnnounce typedDst = this;
        super.copy_from(typedSrc);
        /** The unique identifier for this file. */
        typedDst.fileUuid = typedSrc.fileUuid;
        /** URI of where the file is located. */
        typedDst.fileLocator = typedSrc.fileLocator;
        /** Total size of the file in bytes. */
        typedDst.fileSize = typedSrc.fileSize;
        /** Additional key/value pairs of metadata such as group-id or mime-type. */
        typedDst.metaData = (rapid.KeyTypeValueSequence16) typedDst.metaData.copy_from(typedSrc.metaData);

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
        /** URI of where the file is located. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("fileLocator: ").append(fileLocator).append("\n");  
        /** Total size of the file in bytes. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("fileSize: ").append(fileSize).append("\n");  
        /** Additional key/value pairs of metadata such as group-id or mime-type. */
        strBuffer.append(metaData.toString("metaData ", indent+1));

        return strBuffer.toString();
    }

}
