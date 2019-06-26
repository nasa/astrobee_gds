

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

public class SpectrumConfig  extends rapid.Message implements Copyable, Serializable{

    /** name of instrument */
    public String name=  "" ; /* maximum length = (32) */
    /** Specifies index range information about spectrum samples */
    public rapid.ext.arc.NamedIndexRangeSequence16 indexRanges = (rapid.ext.arc.NamedIndexRangeSequence16)rapid.ext.arc.NamedIndexRangeSequence16.create();
    /** Specifies any spectrum instrument specific information */
    public rapid.KeyTypeValueSequence64 valueKeys = (rapid.KeyTypeValueSequence64)rapid.KeyTypeValueSequence64.create();

    public SpectrumConfig() {

        super();

        /** name of instrument */
        /** Specifies index range information about spectrum samples */
        /** Specifies any spectrum instrument specific information */

    }
    public SpectrumConfig (SpectrumConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        SpectrumConfig self;
        self = new  SpectrumConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** name of instrument */
        name=  ""; 
        /** Specifies index range information about spectrum samples */
        if (indexRanges != null) {
            indexRanges.clear();
        }
        /** Specifies any spectrum instrument specific information */
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

        SpectrumConfig otherObj = (SpectrumConfig)o;

        /** name of instrument */
        if(!name.equals(otherObj.name)) {
            return false;
        }
        /** Specifies index range information about spectrum samples */
        if(!indexRanges.equals(otherObj.indexRanges)) {
            return false;
        }
        /** Specifies any spectrum instrument specific information */
        if(!valueKeys.equals(otherObj.valueKeys)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** name of instrument */
        __result += name.hashCode(); 
        /** Specifies index range information about spectrum samples */
        __result += indexRanges.hashCode(); 
        /** Specifies any spectrum instrument specific information */
        __result += valueKeys.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>SpectrumConfigTypeSupport</code>
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

        SpectrumConfig typedSrc = (SpectrumConfig) src;
        SpectrumConfig typedDst = this;
        super.copy_from(typedSrc);
        /** name of instrument */
        typedDst.name = typedSrc.name;
        /** Specifies index range information about spectrum samples */
        typedDst.indexRanges = (rapid.ext.arc.NamedIndexRangeSequence16) typedDst.indexRanges.copy_from(typedSrc.indexRanges);
        /** Specifies any spectrum instrument specific information */
        typedDst.valueKeys = (rapid.KeyTypeValueSequence64) typedDst.valueKeys.copy_from(typedSrc.valueKeys);

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

        /** name of instrument */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("name: ").append(name).append("\n");  
        /** Specifies index range information about spectrum samples */
        strBuffer.append(indexRanges.toString("indexRanges ", indent+1));
        /** Specifies any spectrum instrument specific information */
        strBuffer.append(valueKeys.toString("valueKeys ", indent+1));

        return strBuffer.toString();
    }

}
