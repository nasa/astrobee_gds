

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

public class GenericConfig  extends rapid.Message implements Copyable, Serializable{

    public String category=  "" ; /* maximum length = (32) */
    public rapid.KeyTypeValueSequence32 metaData = (rapid.KeyTypeValueSequence32)rapid.KeyTypeValueSequence32.create();

    public GenericConfig() {

        super();

    }
    public GenericConfig (GenericConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        GenericConfig self;
        self = new  GenericConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        category=  ""; 
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

        GenericConfig otherObj = (GenericConfig)o;

        if(!category.equals(otherObj.category)) {
            return false;
        }
        if(!metaData.equals(otherObj.metaData)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += category.hashCode(); 
        __result += metaData.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>GenericConfigTypeSupport</code>
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

        GenericConfig typedSrc = (GenericConfig) src;
        GenericConfig typedDst = this;
        super.copy_from(typedSrc);
        typedDst.category = typedSrc.category;
        typedDst.metaData = (rapid.KeyTypeValueSequence32) typedDst.metaData.copy_from(typedSrc.metaData);

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

        return strBuffer.toString();
    }

}
