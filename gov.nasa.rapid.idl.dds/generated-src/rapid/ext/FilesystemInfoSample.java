

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

public class FilesystemInfoSample   implements Copyable, Serializable{

    public long used= 0;
    public long available= 0;

    public FilesystemInfoSample() {

    }
    public FilesystemInfoSample (FilesystemInfoSample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        FilesystemInfoSample self;
        self = new  FilesystemInfoSample();
        self.clear();
        return self;

    }

    public void clear() {

        used= 0;
        available= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        FilesystemInfoSample otherObj = (FilesystemInfoSample)o;

        if(used != otherObj.used) {
            return false;
        }
        if(available != otherObj.available) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += (int)used;
        __result += (int)available;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>FilesystemInfoSampleTypeSupport</code>
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

        FilesystemInfoSample typedSrc = (FilesystemInfoSample) src;
        FilesystemInfoSample typedDst = this;

        typedDst.used = typedSrc.used;
        typedDst.available = typedSrc.available;

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
        strBuffer.append("used: ").append(used).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("available: ").append(available).append("\n");  

        return strBuffer.toString();
    }

}
