

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

public class RTrans2DMeta   implements Copyable, Serializable{

    public float x= 0;
    public float y= 0;
    public float theta= 0;
    public rapid.ext.RTransMetaSequence meta = (rapid.ext.RTransMetaSequence)rapid.ext.RTransMetaSequence.create();

    public RTrans2DMeta() {

    }
    public RTrans2DMeta (RTrans2DMeta other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        RTrans2DMeta self;
        self = new  RTrans2DMeta();
        self.clear();
        return self;

    }

    public void clear() {

        x= 0;
        y= 0;
        theta= 0;
        if (meta != null) {
            meta.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        RTrans2DMeta otherObj = (RTrans2DMeta)o;

        if(x != otherObj.x) {
            return false;
        }
        if(y != otherObj.y) {
            return false;
        }
        if(theta != otherObj.theta) {
            return false;
        }
        if(!meta.equals(otherObj.meta)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += (int)x;
        __result += (int)y;
        __result += (int)theta;
        __result += meta.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>RTrans2DMetaTypeSupport</code>
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

        RTrans2DMeta typedSrc = (RTrans2DMeta) src;
        RTrans2DMeta typedDst = this;

        typedDst.x = typedSrc.x;
        typedDst.y = typedSrc.y;
        typedDst.theta = typedSrc.theta;
        typedDst.meta = (rapid.ext.RTransMetaSequence) typedDst.meta.copy_from(typedSrc.meta);

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
        strBuffer.append("x: ").append(x).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("y: ").append(y).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("theta: ").append(theta).append("\n");  
        strBuffer.append(meta.toString("meta ", indent+1));

        return strBuffer.toString();
    }

}
