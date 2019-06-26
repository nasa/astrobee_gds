

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

public class Trajectory2DSample  extends rapid.Message implements Copyable, Serializable{

    /** origin of the trajectory */
    public rapid.Transform3D origin = (rapid.Transform3D)rapid.Transform3D.create();
    /** The trajectory of the robot relative to the provided pose, sampled at regular time steps, as specified by samplingInterval. */
    public rapid.ext.RTrans2DSequence trajectory = (rapid.ext.RTrans2DSequence)rapid.ext.RTrans2DSequence.create();

    public Trajectory2DSample() {

        super();

        /** origin of the trajectory */
        /** The trajectory of the robot relative to the provided pose, sampled at regular time steps, as specified by samplingInterval. */

    }
    public Trajectory2DSample (Trajectory2DSample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        Trajectory2DSample self;
        self = new  Trajectory2DSample();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** origin of the trajectory */
        if (origin != null) {
            origin.clear();
        }
        /** The trajectory of the robot relative to the provided pose, sampled at regular time steps, as specified by samplingInterval. */
        if (trajectory != null) {
            trajectory.clear();
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

        Trajectory2DSample otherObj = (Trajectory2DSample)o;

        /** origin of the trajectory */
        if(!origin.equals(otherObj.origin)) {
            return false;
        }
        /** The trajectory of the robot relative to the provided pose, sampled at regular time steps, as specified by samplingInterval. */
        if(!trajectory.equals(otherObj.trajectory)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** origin of the trajectory */
        __result += origin.hashCode(); 
        /** The trajectory of the robot relative to the provided pose, sampled at regular time steps, as specified by samplingInterval. */
        __result += trajectory.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>Trajectory2DSampleTypeSupport</code>
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

        Trajectory2DSample typedSrc = (Trajectory2DSample) src;
        Trajectory2DSample typedDst = this;
        super.copy_from(typedSrc);
        /** origin of the trajectory */
        typedDst.origin = (rapid.Transform3D) typedDst.origin.copy_from(typedSrc.origin);
        /** The trajectory of the robot relative to the provided pose, sampled at regular time steps, as specified by samplingInterval. */
        typedDst.trajectory = (rapid.ext.RTrans2DSequence) typedDst.trajectory.copy_from(typedSrc.trajectory);

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

        /** origin of the trajectory */
        strBuffer.append(origin.toString("origin ", indent+1));
        /** The trajectory of the robot relative to the provided pose, sampled at regular time steps, as specified by samplingInterval. */
        strBuffer.append(trajectory.toString("trajectory ", indent+1));

        return strBuffer.toString();
    }

}
