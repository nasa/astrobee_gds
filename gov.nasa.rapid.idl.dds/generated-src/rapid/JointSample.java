

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

/** Flag values for joint status sequence. */
/** Joint is enabled. */
/** Joint is disabled. */
/** Joint is unable to move. */
/** Joint has been e-stopped. */
/** Joint has had a failure. */
/** Joint is drawing too much current. */
/** Joint is exceeded position error limits. */
/**
* JointSample delivers joint data at a high frequency. Use JointConfig to deciper the configuration of
* joints.
*/

public class JointSample  extends rapid.Message implements Copyable, Serializable{

    /** Angle position (in radians) of the joint. */
    public rapid.FloatSequence64 anglePos = (rapid.FloatSequence64)rapid.FloatSequence64.create();
    /** Angle velocity (in radians/sec) of the joint. */
    public rapid.FloatSequence64 angleVel = (rapid.FloatSequence64)rapid.FloatSequence64.create();
    /** Angle acceleration (in radians/sec^2) of the joint. */
    public rapid.FloatSequence64 angleAcc = (rapid.FloatSequence64)rapid.FloatSequence64.create();
    /** Current draw of joint motor. */
    public rapid.FloatSequence64 current = (rapid.FloatSequence64)rapid.FloatSequence64.create();
    /** Torque sensed at the joint (in N-m). */
    public rapid.FloatSequence64 torque = (rapid.FloatSequence64)rapid.FloatSequence64.create();
    /** Temperature of the joint (in degrees Celsius). */
    public rapid.FloatSequence64 temperature = (rapid.FloatSequence64)rapid.FloatSequence64.create();
    /** Bit field representing the state of the joint. */
    public rapid.LongSequence64 status = (rapid.LongSequence64)rapid.LongSequence64.create();
    /** A list of sequences for any parameters not otherwise included. */
    public rapid.NFSeqSequence16 auxFloat = (rapid.NFSeqSequence16)rapid.NFSeqSequence16.create();

    public JointSample() {

        super();

        /** Angle position (in radians) of the joint. */
        /** Angle velocity (in radians/sec) of the joint. */
        /** Angle acceleration (in radians/sec^2) of the joint. */
        /** Current draw of joint motor. */
        /** Torque sensed at the joint (in N-m). */
        /** Temperature of the joint (in degrees Celsius). */
        /** Bit field representing the state of the joint. */
        /** A list of sequences for any parameters not otherwise included. */

    }
    public JointSample (JointSample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        JointSample self;
        self = new  JointSample();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** Angle position (in radians) of the joint. */
        if (anglePos != null) {
            anglePos.clear();
        }
        /** Angle velocity (in radians/sec) of the joint. */
        if (angleVel != null) {
            angleVel.clear();
        }
        /** Angle acceleration (in radians/sec^2) of the joint. */
        if (angleAcc != null) {
            angleAcc.clear();
        }
        /** Current draw of joint motor. */
        if (current != null) {
            current.clear();
        }
        /** Torque sensed at the joint (in N-m). */
        if (torque != null) {
            torque.clear();
        }
        /** Temperature of the joint (in degrees Celsius). */
        if (temperature != null) {
            temperature.clear();
        }
        /** Bit field representing the state of the joint. */
        if (status != null) {
            status.clear();
        }
        /** A list of sequences for any parameters not otherwise included. */
        if (auxFloat != null) {
            auxFloat.clear();
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

        JointSample otherObj = (JointSample)o;

        /** Angle position (in radians) of the joint. */
        if(!anglePos.equals(otherObj.anglePos)) {
            return false;
        }
        /** Angle velocity (in radians/sec) of the joint. */
        if(!angleVel.equals(otherObj.angleVel)) {
            return false;
        }
        /** Angle acceleration (in radians/sec^2) of the joint. */
        if(!angleAcc.equals(otherObj.angleAcc)) {
            return false;
        }
        /** Current draw of joint motor. */
        if(!current.equals(otherObj.current)) {
            return false;
        }
        /** Torque sensed at the joint (in N-m). */
        if(!torque.equals(otherObj.torque)) {
            return false;
        }
        /** Temperature of the joint (in degrees Celsius). */
        if(!temperature.equals(otherObj.temperature)) {
            return false;
        }
        /** Bit field representing the state of the joint. */
        if(!status.equals(otherObj.status)) {
            return false;
        }
        /** A list of sequences for any parameters not otherwise included. */
        if(!auxFloat.equals(otherObj.auxFloat)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** Angle position (in radians) of the joint. */
        __result += anglePos.hashCode(); 
        /** Angle velocity (in radians/sec) of the joint. */
        __result += angleVel.hashCode(); 
        /** Angle acceleration (in radians/sec^2) of the joint. */
        __result += angleAcc.hashCode(); 
        /** Current draw of joint motor. */
        __result += current.hashCode(); 
        /** Torque sensed at the joint (in N-m). */
        __result += torque.hashCode(); 
        /** Temperature of the joint (in degrees Celsius). */
        __result += temperature.hashCode(); 
        /** Bit field representing the state of the joint. */
        __result += status.hashCode(); 
        /** A list of sequences for any parameters not otherwise included. */
        __result += auxFloat.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>JointSampleTypeSupport</code>
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

        JointSample typedSrc = (JointSample) src;
        JointSample typedDst = this;
        super.copy_from(typedSrc);
        /** Angle position (in radians) of the joint. */
        typedDst.anglePos = (rapid.FloatSequence64) typedDst.anglePos.copy_from(typedSrc.anglePos);
        /** Angle velocity (in radians/sec) of the joint. */
        typedDst.angleVel = (rapid.FloatSequence64) typedDst.angleVel.copy_from(typedSrc.angleVel);
        /** Angle acceleration (in radians/sec^2) of the joint. */
        typedDst.angleAcc = (rapid.FloatSequence64) typedDst.angleAcc.copy_from(typedSrc.angleAcc);
        /** Current draw of joint motor. */
        typedDst.current = (rapid.FloatSequence64) typedDst.current.copy_from(typedSrc.current);
        /** Torque sensed at the joint (in N-m). */
        typedDst.torque = (rapid.FloatSequence64) typedDst.torque.copy_from(typedSrc.torque);
        /** Temperature of the joint (in degrees Celsius). */
        typedDst.temperature = (rapid.FloatSequence64) typedDst.temperature.copy_from(typedSrc.temperature);
        /** Bit field representing the state of the joint. */
        typedDst.status = (rapid.LongSequence64) typedDst.status.copy_from(typedSrc.status);
        /** A list of sequences for any parameters not otherwise included. */
        typedDst.auxFloat = (rapid.NFSeqSequence16) typedDst.auxFloat.copy_from(typedSrc.auxFloat);

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

        /** Angle position (in radians) of the joint. */
        strBuffer.append(anglePos.toString("anglePos ", indent+1));
        /** Angle velocity (in radians/sec) of the joint. */
        strBuffer.append(angleVel.toString("angleVel ", indent+1));
        /** Angle acceleration (in radians/sec^2) of the joint. */
        strBuffer.append(angleAcc.toString("angleAcc ", indent+1));
        /** Current draw of joint motor. */
        strBuffer.append(current.toString("current ", indent+1));
        /** Torque sensed at the joint (in N-m). */
        strBuffer.append(torque.toString("torque ", indent+1));
        /** Temperature of the joint (in degrees Celsius). */
        strBuffer.append(temperature.toString("temperature ", indent+1));
        /** Bit field representing the state of the joint. */
        strBuffer.append(status.toString("status ", indent+1));
        /** A list of sequences for any parameters not otherwise included. */
        strBuffer.append(auxFloat.toString("auxFloat ", indent+1));

        return strBuffer.toString();
    }

}
