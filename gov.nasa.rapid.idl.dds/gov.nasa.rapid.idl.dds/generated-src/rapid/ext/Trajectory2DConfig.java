

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

/**
* Specifies how to interpret the sequence of transforms
* <ul>
*   <li>RTRANS2D_RELATIVE_TO_ORIGIN: transforms are all offsets from the origin
*   <li>RTRANS2D_RELATIVE_TO_PREVIOUS: transform is relative to the previous transform; the first transform is relative to the origin
* </ul>
*/

public class Trajectory2DConfig  extends rapid.Message implements Copyable, Serializable{

    /** Denotes the frame the pose is given in. This frame should exist in the frame store. */
    public String referenceFrame=  "" ; /* maximum length = (128) */
    /** Interpretation of trajectory transforms - either relative to origin, or relative to previous */
    public rapid.ext.RTrans2DInterpretation trajectoryInterp = (rapid.ext.RTrans2DInterpretation)rapid.ext.RTrans2DInterpretation.create();
    /**
    * The sampling interval for the trajectory sequence in microsec. Typically 0.1s.
    * If the trajectory is not time sampled, this field should be set to 0
    */
    public long samplingInterval= 0;
    /**
    * keys describing the fields in the trajectory RTransMetaSequence
    */
    public rapid.KeyTypeSequence4 trajectoryMetaKeys = (rapid.KeyTypeSequence4)rapid.KeyTypeSequence4.create();

    public Trajectory2DConfig() {

        super();

        /** Denotes the frame the pose is given in. This frame should exist in the frame store. */
        /** Interpretation of trajectory transforms - either relative to origin, or relative to previous */
        /**
        * The sampling interval for the trajectory sequence in microsec. Typically 0.1s.
        * If the trajectory is not time sampled, this field should be set to 0
        */
        /**
        * keys describing the fields in the trajectory RTransMetaSequence
        */

    }
    public Trajectory2DConfig (Trajectory2DConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        Trajectory2DConfig self;
        self = new  Trajectory2DConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** Denotes the frame the pose is given in. This frame should exist in the frame store. */
        referenceFrame=  ""; 
        /** Interpretation of trajectory transforms - either relative to origin, or relative to previous */
        trajectoryInterp = rapid.ext.RTrans2DInterpretation.create();
        /**
        * The sampling interval for the trajectory sequence in microsec. Typically 0.1s.
        * If the trajectory is not time sampled, this field should be set to 0
        */
        samplingInterval= 0;
        /**
        * keys describing the fields in the trajectory RTransMetaSequence
        */
        if (trajectoryMetaKeys != null) {
            trajectoryMetaKeys.clear();
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

        Trajectory2DConfig otherObj = (Trajectory2DConfig)o;

        /** Denotes the frame the pose is given in. This frame should exist in the frame store. */
        if(!referenceFrame.equals(otherObj.referenceFrame)) {
            return false;
        }
        /** Interpretation of trajectory transforms - either relative to origin, or relative to previous */
        if(!trajectoryInterp.equals(otherObj.trajectoryInterp)) {
            return false;
        }
        /**
        * The sampling interval for the trajectory sequence in microsec. Typically 0.1s.
        * If the trajectory is not time sampled, this field should be set to 0
        */
        if(samplingInterval != otherObj.samplingInterval) {
            return false;
        }
        /**
        * keys describing the fields in the trajectory RTransMetaSequence
        */
        if(!trajectoryMetaKeys.equals(otherObj.trajectoryMetaKeys)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** Denotes the frame the pose is given in. This frame should exist in the frame store. */
        __result += referenceFrame.hashCode(); 
        /** Interpretation of trajectory transforms - either relative to origin, or relative to previous */
        __result += trajectoryInterp.hashCode(); 
        /**
        * The sampling interval for the trajectory sequence in microsec. Typically 0.1s.
        * If the trajectory is not time sampled, this field should be set to 0
        */
        __result += (int)samplingInterval;
        /**
        * keys describing the fields in the trajectory RTransMetaSequence
        */
        __result += trajectoryMetaKeys.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>Trajectory2DConfigTypeSupport</code>
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

        Trajectory2DConfig typedSrc = (Trajectory2DConfig) src;
        Trajectory2DConfig typedDst = this;
        super.copy_from(typedSrc);
        /** Denotes the frame the pose is given in. This frame should exist in the frame store. */
        typedDst.referenceFrame = typedSrc.referenceFrame;
        /** Interpretation of trajectory transforms - either relative to origin, or relative to previous */
        typedDst.trajectoryInterp = (rapid.ext.RTrans2DInterpretation) typedDst.trajectoryInterp.copy_from(typedSrc.trajectoryInterp);
        /**
        * The sampling interval for the trajectory sequence in microsec. Typically 0.1s.
        * If the trajectory is not time sampled, this field should be set to 0
        */
        typedDst.samplingInterval = typedSrc.samplingInterval;
        /**
        * keys describing the fields in the trajectory RTransMetaSequence
        */
        typedDst.trajectoryMetaKeys = (rapid.KeyTypeSequence4) typedDst.trajectoryMetaKeys.copy_from(typedSrc.trajectoryMetaKeys);

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

        /** Denotes the frame the pose is given in. This frame should exist in the frame store. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("referenceFrame: ").append(referenceFrame).append("\n");  
        /** Interpretation of trajectory transforms - either relative to origin, or relative to previous */
        strBuffer.append(trajectoryInterp.toString("trajectoryInterp ", indent+1));
        /**
        * The sampling interval for the trajectory sequence in microsec. Typically 0.1s.
        * If the trajectory is not time sampled, this field should be set to 0
        */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("samplingInterval: ").append(samplingInterval).append("\n");  
        /**
        * keys describing the fields in the trajectory RTransMetaSequence
        */
        strBuffer.append(trajectoryMetaKeys.toString("trajectoryMetaKeys ", indent+1));

        return strBuffer.toString();
    }

}
