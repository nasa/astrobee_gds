

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.astrobee;

import com.rti.dds.util.Enum;
import com.rti.dds.cdr.CdrHelper;
import java.util.Arrays;
import java.io.ObjectStreamException;

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

public class FileCompressionType  extends Enum {

    public static final FileCompressionType COMPRESSION_TYPE_NONE = new FileCompressionType("COMPRESSION_TYPE_NONE", 0);
    public static final int _COMPRESSION_TYPE_NONE = 0;
    public static final FileCompressionType COMPRESSION_TYPE_DEFLATE = new FileCompressionType("COMPRESSION_TYPE_DEFLATE", 1);
    public static final int _COMPRESSION_TYPE_DEFLATE = 1;
    public static final FileCompressionType COMPRESSION_TYPE_BZ2 = new FileCompressionType("COMPRESSION_TYPE_BZ2", 2);
    public static final int _COMPRESSION_TYPE_BZ2 = 2;
    public static final FileCompressionType COMPRESSION_TYPE_GZ = new FileCompressionType("COMPRESSION_TYPE_GZ", 3);
    public static final int _COMPRESSION_TYPE_GZ = 3;
    public static final FileCompressionType COMPRESSION_TYPE_ZIP = new FileCompressionType("COMPRESSION_TYPE_ZIP", 4);
    public static final int _COMPRESSION_TYPE_ZIP = 4;
    public static FileCompressionType valueOf(int ordinal) {
        switch(ordinal) {

            case 0: return FileCompressionType.COMPRESSION_TYPE_NONE;
            case 1: return FileCompressionType.COMPRESSION_TYPE_DEFLATE;
            case 2: return FileCompressionType.COMPRESSION_TYPE_BZ2;
            case 3: return FileCompressionType.COMPRESSION_TYPE_GZ;
            case 4: return FileCompressionType.COMPRESSION_TYPE_ZIP;

        }
        return null;
    }

    public static FileCompressionType from_int(int __value) {
        return valueOf(__value);
    }

    public static int[] getOrdinals() {
        int i = 0;
        int[] values = new int[5];

        values[i] = COMPRESSION_TYPE_NONE.ordinal();
        i++;
        values[i] = COMPRESSION_TYPE_DEFLATE.ordinal();
        i++;
        values[i] = COMPRESSION_TYPE_BZ2.ordinal();
        i++;
        values[i] = COMPRESSION_TYPE_GZ.ordinal();
        i++;
        values[i] = COMPRESSION_TYPE_ZIP.ordinal();
        i++;

        return values;
    }

    public int value() {
        return super.ordinal();
    }

    /**
    * Create a default instance
    */  
    public static FileCompressionType create() {

        return valueOf(0);
    }

    /**
    * Print Method
    */     
    public String toString(String desc, int indent) {
        StringBuffer strBuffer = new StringBuffer();

        CdrHelper.printIndent(strBuffer, indent);

        if (desc != null) {
            strBuffer.append(desc).append(": ");
        }

        strBuffer.append(this);
        strBuffer.append("\n");              
        return strBuffer.toString();
    }

    private Object readResolve() throws ObjectStreamException {
        return valueOf(ordinal());
    }

    private FileCompressionType(String name, int ordinal) {
        super(name, ordinal);
    }
}

