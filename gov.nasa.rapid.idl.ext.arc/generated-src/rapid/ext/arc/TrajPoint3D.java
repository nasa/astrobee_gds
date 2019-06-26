

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

/**
* pose rotation is assumed to be quaternion
*/

public class TrajPoint3D   implements Copyable, Serializable{

    public rapid.Transform3D pose = (rapid.Transform3D)rapid.Transform3D.create();

    public TrajPoint3D() {

    }
    public TrajPoint3D (TrajPoint3D other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        TrajPoint3D self;
        self = new  TrajPoint3D();
        self.clear();
        return self;

    }

    public void clear() {

        if (pose != null) {
            pose.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        TrajPoint3D otherObj = (TrajPoint3D)o;

        if(!pose.equals(otherObj.pose)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += pose.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>TrajPoint3DTypeSupport</code>
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

        TrajPoint3D typedSrc = (TrajPoint3D) src;
        TrajPoint3D typedDst = this;

        typedDst.pose = (rapid.Transform3D) typedDst.pose.copy_from(typedSrc.pose);

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

        strBuffer.append(pose.toString("pose ", indent+1));

        return strBuffer.toString();
    }

}
