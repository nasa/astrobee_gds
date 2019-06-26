

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
* CompressedFileAck message delivers a compressed file, with the compression format used
*/

public class CompressedFileAck  extends rapid.Message implements Copyable, Serializable{

    /** ID of the sent CompressedFile */
    public int id= 0;

    public CompressedFileAck() {

        super();

        /** ID of the sent CompressedFile */

    }
    public CompressedFileAck (CompressedFileAck other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        CompressedFileAck self;
        self = new  CompressedFileAck();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** ID of the sent CompressedFile */
        id= 0;
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

        CompressedFileAck otherObj = (CompressedFileAck)o;

        /** ID of the sent CompressedFile */
        if(id != otherObj.id) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** ID of the sent CompressedFile */
        __result += (int)id;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>CompressedFileAckTypeSupport</code>
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

        CompressedFileAck typedSrc = (CompressedFileAck) src;
        CompressedFileAck typedDst = this;
        super.copy_from(typedSrc);
        /** ID of the sent CompressedFile */
        typedDst.id = typedSrc.id;

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

        /** ID of the sent CompressedFile */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("id: ").append(id).append("\n");  

        return strBuffer.toString();
    }

}
