

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
* Float32Sample message delivers the position of the Agent. Corresponding Float32Config sets up the
* coordinate frame and specifies how the Transform3D.rot field is to be interpreted for pose and velocity.
*/

public class Float32Sample  extends rapid.Message implements Copyable, Serializable{

    public rapid.FloatSequence32 data = (rapid.FloatSequence32)rapid.FloatSequence32.create();

    public Float32Sample() {

        super();

    }
    public Float32Sample (Float32Sample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        Float32Sample self;
        self = new  Float32Sample();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        if (data != null) {
            data.clear();
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

        Float32Sample otherObj = (Float32Sample)o;

        if(!data.equals(otherObj.data)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += data.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>Float32SampleTypeSupport</code>
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

        Float32Sample typedSrc = (Float32Sample) src;
        Float32Sample typedDst = this;
        super.copy_from(typedSrc);
        typedDst.data = (rapid.FloatSequence32) typedDst.data.copy_from(typedSrc.data);

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

        strBuffer.append(data.toString("data ", indent+1));

        return strBuffer.toString();
    }

}
