

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

/**
* JointDef defines the frame and DOF for a specific Agent's joints.
*/

public class JointDef   implements Copyable, Serializable{

    /**
    * Name of frame that joint reports its position in - should match a frame in FrameStore.
    */
    public String frameName=  "" ; /* maximum length = (128) */
    /**
    * Defines the axis of rotation or motion. Note: This will change in the near future.
    */
    public String dof=  "" ; /* maximum length = (64) */

    public JointDef() {

        /**
        * Name of frame that joint reports its position in - should match a frame in FrameStore.
        */
        /**
        * Defines the axis of rotation or motion. Note: This will change in the near future.
        */

    }
    public JointDef (JointDef other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        JointDef self;
        self = new  JointDef();
        self.clear();
        return self;

    }

    public void clear() {

        /**
        * Name of frame that joint reports its position in - should match a frame in FrameStore.
        */
        frameName=  ""; 
        /**
        * Defines the axis of rotation or motion. Note: This will change in the near future.
        */
        dof=  ""; 
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        JointDef otherObj = (JointDef)o;

        /**
        * Name of frame that joint reports its position in - should match a frame in FrameStore.
        */
        if(!frameName.equals(otherObj.frameName)) {
            return false;
        }
        /**
        * Defines the axis of rotation or motion. Note: This will change in the near future.
        */
        if(!dof.equals(otherObj.dof)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        /**
        * Name of frame that joint reports its position in - should match a frame in FrameStore.
        */
        __result += frameName.hashCode(); 
        /**
        * Defines the axis of rotation or motion. Note: This will change in the near future.
        */
        __result += dof.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>JointDefTypeSupport</code>
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

        JointDef typedSrc = (JointDef) src;
        JointDef typedDst = this;

        /**
        * Name of frame that joint reports its position in - should match a frame in FrameStore.
        */
        typedDst.frameName = typedSrc.frameName;
        /**
        * Defines the axis of rotation or motion. Note: This will change in the near future.
        */
        typedDst.dof = typedSrc.dof;

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

        /**
        * Name of frame that joint reports its position in - should match a frame in FrameStore.
        */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("frameName: ").append(frameName).append("\n");  
        /**
        * Defines the axis of rotation or motion. Note: This will change in the near future.
        */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("dof: ").append(dof).append("\n");  

        return strBuffer.toString();
    }

}
