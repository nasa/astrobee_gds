

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.arc;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

/** Float32Config message sets up configuration for Float32Sample messages. */

public class Float32Config  extends rapid.Message implements Copyable, Serializable{

    public String category=  "" ; /* maximum length = (32) */
    public rapid.KeyTypeValueSequence16 metaData = (rapid.KeyTypeValueSequence16)rapid.KeyTypeValueSequence16.create();
    public rapid.String32Sequence32 dataKeys = (rapid.String32Sequence32)rapid.String32Sequence32.create();

    public Float32Config() {

        super();

    }
    public Float32Config (Float32Config other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        Float32Config self;
        self = new  Float32Config();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        category=  ""; 
        if (metaData != null) {
            metaData.clear();
        }
        if (dataKeys != null) {
            dataKeys.clear();
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

        Float32Config otherObj = (Float32Config)o;

        if(!category.equals(otherObj.category)) {
            return false;
        }
        if(!metaData.equals(otherObj.metaData)) {
            return false;
        }
        if(!dataKeys.equals(otherObj.dataKeys)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += category.hashCode(); 
        __result += metaData.hashCode(); 
        __result += dataKeys.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>Float32ConfigTypeSupport</code>
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

        Float32Config typedSrc = (Float32Config) src;
        Float32Config typedDst = this;
        super.copy_from(typedSrc);
        typedDst.category = typedSrc.category;
        typedDst.metaData = (rapid.KeyTypeValueSequence16) typedDst.metaData.copy_from(typedSrc.metaData);
        typedDst.dataKeys = (rapid.String32Sequence32) typedDst.dataKeys.copy_from(typedSrc.dataKeys);

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

        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("category: ").append(category).append("\n");  
        strBuffer.append(metaData.toString("metaData ", indent+1));
        strBuffer.append(dataKeys.toString("dataKeys ", indent+1));

        return strBuffer.toString();
    }

}
