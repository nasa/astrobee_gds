

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
* SegmentType describes the type of segment that is being represented.
*/
/**
* SingleSegment holds the info for an "atomic" segment.
*
*/
/**
* TrajectorySample is a message that holds the current sequence of geometric trajectory segments that the robot plans to follow.
* The receiver will need to convert this to whatever form they need to identify potential collisions or to render it in a GUI
*/

public class TrajectorySample  extends rapid.Message implements Copyable, Serializable{

    public rapid.ext.SegmentSequence geometricSegments = (rapid.ext.SegmentSequence)rapid.ext.SegmentSequence.create();

    public TrajectorySample() {

        super();

    }
    public TrajectorySample (TrajectorySample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        TrajectorySample self;
        self = new  TrajectorySample();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        if (geometricSegments != null) {
            geometricSegments.clear();
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

        TrajectorySample otherObj = (TrajectorySample)o;

        if(!geometricSegments.equals(otherObj.geometricSegments)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += geometricSegments.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>TrajectorySampleTypeSupport</code>
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

        TrajectorySample typedSrc = (TrajectorySample) src;
        TrajectorySample typedDst = this;
        super.copy_from(typedSrc);
        typedDst.geometricSegments = (rapid.ext.SegmentSequence) typedDst.geometricSegments.copy_from(typedSrc.geometricSegments);

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

        strBuffer.append(geometricSegments.toString("geometricSegments ", indent+1));

        return strBuffer.toString();
    }

}
