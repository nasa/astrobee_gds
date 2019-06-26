

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

public class HydraConfig  extends rapid.Message implements Copyable, Serializable{

    /** Specifies name of instrument */
    public String name=  "" ; /* maximum length = (32) */
    /** Specifies any additional metaData */
    public rapid.KeyTypeValueSequence32 valueKeys = (rapid.KeyTypeValueSequence32)rapid.KeyTypeValueSequence32.create();

    public HydraConfig() {

        super();

        /** Specifies name of instrument */
        /** Specifies any additional metaData */

    }
    public HydraConfig (HydraConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        HydraConfig self;
        self = new  HydraConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** Specifies name of instrument */
        name=  ""; 
        /** Specifies any additional metaData */
        if (valueKeys != null) {
            valueKeys.clear();
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

        HydraConfig otherObj = (HydraConfig)o;

        /** Specifies name of instrument */
        if(!name.equals(otherObj.name)) {
            return false;
        }
        /** Specifies any additional metaData */
        if(!valueKeys.equals(otherObj.valueKeys)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** Specifies name of instrument */
        __result += name.hashCode(); 
        /** Specifies any additional metaData */
        __result += valueKeys.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>HydraConfigTypeSupport</code>
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

        HydraConfig typedSrc = (HydraConfig) src;
        HydraConfig typedDst = this;
        super.copy_from(typedSrc);
        /** Specifies name of instrument */
        typedDst.name = typedSrc.name;
        /** Specifies any additional metaData */
        typedDst.valueKeys = (rapid.KeyTypeValueSequence32) typedDst.valueKeys.copy_from(typedSrc.valueKeys);

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

        /** Specifies name of instrument */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("name: ").append(name).append("\n");  
        /** Specifies any additional metaData */
        strBuffer.append(valueKeys.toString("valueKeys ", indent+1));

        return strBuffer.toString();
    }

}
