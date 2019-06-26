

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.astrobee;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

/**
* Compression format used for file
* <ul>
*   <li>COMPRESSION_TYPE_NONE: no compression
*   <li>COMPRESSION_TYPE_DEFLATE: zlib deflate compression
*   <li>COMPRESSION_TYPE_BZ2: bzip2
*   <li>COMPRESSION_TYPE_GZ:  GNU Zip
*   <li>COMPRESSION_TYPE_ZIP: ZIP
* </ul>
*/
/**
* CompressedFile message delivers a compressed file, with the compression format used
*/

public class CompressedFile  extends rapid.Message implements Copyable, Serializable{

    /** ID of this compressed file. See CompressedFileAck.idl */
    public int id= 0;
    /** Store binary data of compressed file */
    public rapid.OctetSequence128K compressedFile = (rapid.OctetSequence128K)rapid.OctetSequence128K.create();
    /** Compression format used on file, to allow proper decompression on receiving end */
    public rapid.ext.astrobee.FileCompressionType compressionType = (rapid.ext.astrobee.FileCompressionType)rapid.ext.astrobee.FileCompressionType.create();

    public CompressedFile() {

        super();

        /** ID of this compressed file. See CompressedFileAck.idl */
        /** Store binary data of compressed file */
        /** Compression format used on file, to allow proper decompression on receiving end */

    }
    public CompressedFile (CompressedFile other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        CompressedFile self;
        self = new  CompressedFile();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** ID of this compressed file. See CompressedFileAck.idl */
        id= 0;
        /** Store binary data of compressed file */
        if (compressedFile != null) {
            compressedFile.clear();
        }
        /** Compression format used on file, to allow proper decompression on receiving end */
        compressionType = rapid.ext.astrobee.FileCompressionType.create();
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

        CompressedFile otherObj = (CompressedFile)o;

        /** ID of this compressed file. See CompressedFileAck.idl */
        if(id != otherObj.id) {
            return false;
        }
        /** Store binary data of compressed file */
        if(!compressedFile.equals(otherObj.compressedFile)) {
            return false;
        }
        /** Compression format used on file, to allow proper decompression on receiving end */
        if(!compressionType.equals(otherObj.compressionType)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** ID of this compressed file. See CompressedFileAck.idl */
        __result += (int)id;
        /** Store binary data of compressed file */
        __result += compressedFile.hashCode(); 
        /** Compression format used on file, to allow proper decompression on receiving end */
        __result += compressionType.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>CompressedFileTypeSupport</code>
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

        CompressedFile typedSrc = (CompressedFile) src;
        CompressedFile typedDst = this;
        super.copy_from(typedSrc);
        /** ID of this compressed file. See CompressedFileAck.idl */
        typedDst.id = typedSrc.id;
        /** Store binary data of compressed file */
        typedDst.compressedFile = (rapid.OctetSequence128K) typedDst.compressedFile.copy_from(typedSrc.compressedFile);
        /** Compression format used on file, to allow proper decompression on receiving end */
        typedDst.compressionType = (rapid.ext.astrobee.FileCompressionType) typedDst.compressionType.copy_from(typedSrc.compressionType);

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

        /** ID of this compressed file. See CompressedFileAck.idl */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("id: ").append(id).append("\n");  
        /** Store binary data of compressed file */
        strBuffer.append(compressedFile.toString("compressedFile ", indent+1));
        /** Compression format used on file, to allow proper decompression on receiving end */
        strBuffer.append(compressionType.toString("compressionType ", indent+1));

        return strBuffer.toString();
    }

}
