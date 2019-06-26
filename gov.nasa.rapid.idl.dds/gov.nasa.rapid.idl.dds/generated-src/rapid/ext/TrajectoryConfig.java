

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
* TrajectoryConfig is a message that tells the receiver what frame the TrajectorySample messages will be in.
*/

public class TrajectoryConfig  extends rapid.Message implements Copyable, Serializable{

    /** Denotes the frame the pose is given in. This frame should exist in the frame store. */
    public String referenceFrame=  "" ; /* maximum length = (128) */

    public TrajectoryConfig() {

        super();

        /** Denotes the frame the pose is given in. This frame should exist in the frame store. */

    }
    public TrajectoryConfig (TrajectoryConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        TrajectoryConfig self;
        self = new  TrajectoryConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** Denotes the frame the pose is given in. This frame should exist in the frame store. */
        referenceFrame=  ""; 
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

        TrajectoryConfig otherObj = (TrajectoryConfig)o;

        /** Denotes the frame the pose is given in. This frame should exist in the frame store. */
        if(!referenceFrame.equals(otherObj.referenceFrame)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** Denotes the frame the pose is given in. This frame should exist in the frame store. */
        __result += referenceFrame.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>TrajectoryConfigTypeSupport</code>
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

        TrajectoryConfig typedSrc = (TrajectoryConfig) src;
        TrajectoryConfig typedDst = this;
        super.copy_from(typedSrc);
        /** Denotes the frame the pose is given in. This frame should exist in the frame store. */
        typedDst.referenceFrame = typedSrc.referenceFrame;

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

        return strBuffer.toString();
    }

}
