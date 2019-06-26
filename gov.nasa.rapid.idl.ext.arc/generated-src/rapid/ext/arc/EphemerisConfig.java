

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

/** EphemerisConfig message sets up configuration for EphemerisSample messages. */

public class EphemerisConfig  extends rapid.Message implements Copyable, Serializable{

    public rapid.ext.arc.EphemerisConfigVector32 entries = (rapid.ext.arc.EphemerisConfigVector32)rapid.ext.arc.EphemerisConfigVector32.create();

    public EphemerisConfig() {

        super();

    }
    public EphemerisConfig (EphemerisConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        EphemerisConfig self;
        self = new  EphemerisConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        if (entries != null) {
            entries.clear();
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

        EphemerisConfig otherObj = (EphemerisConfig)o;

        if(!entries.equals(otherObj.entries)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += entries.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>EphemerisConfigTypeSupport</code>
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

        EphemerisConfig typedSrc = (EphemerisConfig) src;
        EphemerisConfig typedDst = this;
        super.copy_from(typedSrc);
        typedDst.entries = (rapid.ext.arc.EphemerisConfigVector32) typedDst.entries.copy_from(typedSrc.entries);

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

        strBuffer.append(entries.toString("entries ", indent+1));

        return strBuffer.toString();
    }

}
