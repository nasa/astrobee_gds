

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

/** PositionConfig message sets up configuration for PositionSample messages. */

public class PositionConfig  extends rapid.Message implements Copyable, Serializable{

    /** Denotes the frame that position is given in. This frame should exist in the frame store. */
    /** Denotes the frame of the position. This frame should exist in the frame store. */
    public String frameName=  "" ; /* maximum length = (128) */
    /**
    * Specifies the interpretation of the rotation field in PositionSample.pose
    * @see RotationEncoding
    */
    public rapid.RotationEncoding poseEncoding = (rapid.RotationEncoding)rapid.RotationEncoding.create();
    /**
    * Specifies the interpretation of the rotation field in PositionSample.velocity
    * @see RotationEncoding
    */
    public rapid.RotationEncoding velocityEncoding = (rapid.RotationEncoding)rapid.RotationEncoding.create();
    /** Holds extra Agent-specific information about position samples. */
    public rapid.KeyTypeValueSequence64 valueKeys = (rapid.KeyTypeValueSequence64)rapid.KeyTypeValueSequence64.create();

    public PositionConfig() {

        super();

        /** Denotes the frame that position is given in. This frame should exist in the frame store. */
        /** Denotes the frame of the position. This frame should exist in the frame store. */
        /**
        * Specifies the interpretation of the rotation field in PositionSample.pose
        * @see RotationEncoding
        */
        /**
        * Specifies the interpretation of the rotation field in PositionSample.velocity
        * @see RotationEncoding
        */
        /** Holds extra Agent-specific information about position samples. */

    }
    public PositionConfig (PositionConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        PositionConfig self;
        self = new  PositionConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** Denotes the frame that position is given in. This frame should exist in the frame store. */
        /** Denotes the frame of the position. This frame should exist in the frame store. */
        frameName=  ""; 
        /**
        * Specifies the interpretation of the rotation field in PositionSample.pose
        * @see RotationEncoding
        */
        poseEncoding = rapid.RotationEncoding.create();
        /**
        * Specifies the interpretation of the rotation field in PositionSample.velocity
        * @see RotationEncoding
        */
        velocityEncoding = rapid.RotationEncoding.create();
        /** Holds extra Agent-specific information about position samples. */
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

        PositionConfig otherObj = (PositionConfig)o;

        /** Denotes the frame that position is given in. This frame should exist in the frame store. */
        /** Denotes the frame of the position. This frame should exist in the frame store. */
        if(!frameName.equals(otherObj.frameName)) {
            return false;
        }
        /**
        * Specifies the interpretation of the rotation field in PositionSample.pose
        * @see RotationEncoding
        */
        if(!poseEncoding.equals(otherObj.poseEncoding)) {
            return false;
        }
        /**
        * Specifies the interpretation of the rotation field in PositionSample.velocity
        * @see RotationEncoding
        */
        if(!velocityEncoding.equals(otherObj.velocityEncoding)) {
            return false;
        }
        /** Holds extra Agent-specific information about position samples. */
        if(!valueKeys.equals(otherObj.valueKeys)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** Denotes the frame that position is given in. This frame should exist in the frame store. */
        /** Denotes the frame of the position. This frame should exist in the frame store. */
        __result += frameName.hashCode(); 
        /**
        * Specifies the interpretation of the rotation field in PositionSample.pose
        * @see RotationEncoding
        */
        __result += poseEncoding.hashCode(); 
        /**
        * Specifies the interpretation of the rotation field in PositionSample.velocity
        * @see RotationEncoding
        */
        __result += velocityEncoding.hashCode(); 
        /** Holds extra Agent-specific information about position samples. */
        __result += valueKeys.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>PositionConfigTypeSupport</code>
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

        PositionConfig typedSrc = (PositionConfig) src;
        PositionConfig typedDst = this;
        super.copy_from(typedSrc);
        /** Denotes the frame that position is given in. This frame should exist in the frame store. */
        /** Denotes the frame of the position. This frame should exist in the frame store. */
        typedDst.frameName = typedSrc.frameName;
        /**
        * Specifies the interpretation of the rotation field in PositionSample.pose
        * @see RotationEncoding
        */
        typedDst.poseEncoding = (rapid.RotationEncoding) typedDst.poseEncoding.copy_from(typedSrc.poseEncoding);
        /**
        * Specifies the interpretation of the rotation field in PositionSample.velocity
        * @see RotationEncoding
        */
        typedDst.velocityEncoding = (rapid.RotationEncoding) typedDst.velocityEncoding.copy_from(typedSrc.velocityEncoding);
        /** Holds extra Agent-specific information about position samples. */
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

        /** Denotes the frame that position is given in. This frame should exist in the frame store. */
        /** Denotes the frame of the position. This frame should exist in the frame store. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("frameName: ").append(frameName).append("\n");  
        /**
        * Specifies the interpretation of the rotation field in PositionSample.pose
        * @see RotationEncoding
        */
        strBuffer.append(poseEncoding.toString("poseEncoding ", indent+1));
        /**
        * Specifies the interpretation of the rotation field in PositionSample.velocity
        * @see RotationEncoding
        */
        strBuffer.append(velocityEncoding.toString("velocityEncoding ", indent+1));
        /** Holds extra Agent-specific information about position samples. */
        strBuffer.append(valueKeys.toString("valueKeys ", indent+1));

        return strBuffer.toString();
    }

}
