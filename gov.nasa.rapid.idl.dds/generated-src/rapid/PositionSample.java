

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
* PositionSample message delivers the position of the Agent. Corresponding PositionConfig sets up the
* coordinate frame and specifies how the Transform3D.rot field is to be interpreted for pose and velocity.
*/

public class PositionSample  extends rapid.Message implements Copyable, Serializable{

    /** 3D pose of the agent. PositionConfig.poseEncoding specifies how to interpret the rotation. */
    public rapid.Transform3D pose = (rapid.Transform3D)rapid.Transform3D.create();
    /** 3D velocity of the agent. PositionConfig.velocityEncoding specifies how to interpret the rotation. */
    public rapid.Transform3D velocity = (rapid.Transform3D)rapid.Transform3D.create();
    /** Any Agent-specific information needed. */
    public rapid.ValueSequence64 values = (rapid.ValueSequence64)rapid.ValueSequence64.create();

    public PositionSample() {

        super();

        /** 3D pose of the agent. PositionConfig.poseEncoding specifies how to interpret the rotation. */
        /** 3D velocity of the agent. PositionConfig.velocityEncoding specifies how to interpret the rotation. */
        /** Any Agent-specific information needed. */

    }
    public PositionSample (PositionSample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        PositionSample self;
        self = new  PositionSample();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** 3D pose of the agent. PositionConfig.poseEncoding specifies how to interpret the rotation. */
        if (pose != null) {
            pose.clear();
        }
        /** 3D velocity of the agent. PositionConfig.velocityEncoding specifies how to interpret the rotation. */
        if (velocity != null) {
            velocity.clear();
        }
        /** Any Agent-specific information needed. */
        if (values != null) {
            values.clear();
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

        PositionSample otherObj = (PositionSample)o;

        /** 3D pose of the agent. PositionConfig.poseEncoding specifies how to interpret the rotation. */
        if(!pose.equals(otherObj.pose)) {
            return false;
        }
        /** 3D velocity of the agent. PositionConfig.velocityEncoding specifies how to interpret the rotation. */
        if(!velocity.equals(otherObj.velocity)) {
            return false;
        }
        /** Any Agent-specific information needed. */
        if(!values.equals(otherObj.values)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** 3D pose of the agent. PositionConfig.poseEncoding specifies how to interpret the rotation. */
        __result += pose.hashCode(); 
        /** 3D velocity of the agent. PositionConfig.velocityEncoding specifies how to interpret the rotation. */
        __result += velocity.hashCode(); 
        /** Any Agent-specific information needed. */
        __result += values.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>PositionSampleTypeSupport</code>
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

        PositionSample typedSrc = (PositionSample) src;
        PositionSample typedDst = this;
        super.copy_from(typedSrc);
        /** 3D pose of the agent. PositionConfig.poseEncoding specifies how to interpret the rotation. */
        typedDst.pose = (rapid.Transform3D) typedDst.pose.copy_from(typedSrc.pose);
        /** 3D velocity of the agent. PositionConfig.velocityEncoding specifies how to interpret the rotation. */
        typedDst.velocity = (rapid.Transform3D) typedDst.velocity.copy_from(typedSrc.velocity);
        /** Any Agent-specific information needed. */
        typedDst.values = (rapid.ValueSequence64) typedDst.values.copy_from(typedSrc.values);

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

        /** 3D pose of the agent. PositionConfig.poseEncoding specifies how to interpret the rotation. */
        strBuffer.append(pose.toString("pose ", indent+1));
        /** 3D velocity of the agent. PositionConfig.velocityEncoding specifies how to interpret the rotation. */
        strBuffer.append(velocity.toString("velocity ", indent+1));
        /** Any Agent-specific information needed. */
        strBuffer.append(values.toString("values ", indent+1));

        return strBuffer.toString();
    }

}
