

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
/**
* Joint configuration of a kinematic chain. Gives configuration of joint data for a specific Agent. Joint
* data is delivered using JointSample.
*/

public class JointConfig  extends rapid.Message implements Copyable, Serializable{

    /**
    * Name of the joint group.
    */
    public String jointGroupName=  "" ; /* maximum length = (32) */
    /**
    * JointDefs for each joint.
    */
    public rapid.JointDefSequence jointDefinitions = (rapid.JointDefSequence)rapid.JointDefSequence.create();

    public JointConfig() {

        super();

        /**
        * Name of the joint group.
        */
        /**
        * JointDefs for each joint.
        */

    }
    public JointConfig (JointConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        JointConfig self;
        self = new  JointConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /**
        * Name of the joint group.
        */
        jointGroupName=  ""; 
        /**
        * JointDefs for each joint.
        */
        if (jointDefinitions != null) {
            jointDefinitions.clear();
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

        JointConfig otherObj = (JointConfig)o;

        /**
        * Name of the joint group.
        */
        if(!jointGroupName.equals(otherObj.jointGroupName)) {
            return false;
        }
        /**
        * JointDefs for each joint.
        */
        if(!jointDefinitions.equals(otherObj.jointDefinitions)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /**
        * Name of the joint group.
        */
        __result += jointGroupName.hashCode(); 
        /**
        * JointDefs for each joint.
        */
        __result += jointDefinitions.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>JointConfigTypeSupport</code>
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

        JointConfig typedSrc = (JointConfig) src;
        JointConfig typedDst = this;
        super.copy_from(typedSrc);
        /**
        * Name of the joint group.
        */
        typedDst.jointGroupName = typedSrc.jointGroupName;
        /**
        * JointDefs for each joint.
        */
        typedDst.jointDefinitions = (rapid.JointDefSequence) typedDst.jointDefinitions.copy_from(typedSrc.jointDefinitions);

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

        /**
        * Name of the joint group.
        */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("jointGroupName: ").append(jointGroupName).append("\n");  
        /**
        * JointDefs for each joint.
        */
        strBuffer.append(jointDefinitions.toString("jointDefinitions ", indent+1));

        return strBuffer.toString();
    }

}
