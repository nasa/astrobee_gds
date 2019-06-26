

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

public class SingleSegment   implements Copyable, Serializable{

    public rapid.ext.SegmentType type = (rapid.ext.SegmentType)rapid.ext.SegmentType.create();
    public float distance= 0;
    public float length= 0;
    public float angleOffset= 0;
    public rapid.Transform3D start = (rapid.Transform3D)rapid.Transform3D.create();
    public rapid.Transform3D end = (rapid.Transform3D)rapid.Transform3D.create();
    public float velocity= 0;

    public SingleSegment() {

    }
    public SingleSegment (SingleSegment other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        SingleSegment self;
        self = new  SingleSegment();
        self.clear();
        return self;

    }

    public void clear() {

        type = rapid.ext.SegmentType.create();
        distance= 0;
        length= 0;
        angleOffset= 0;
        if (start != null) {
            start.clear();
        }
        if (end != null) {
            end.clear();
        }
        velocity= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        SingleSegment otherObj = (SingleSegment)o;

        if(!type.equals(otherObj.type)) {
            return false;
        }
        if(distance != otherObj.distance) {
            return false;
        }
        if(length != otherObj.length) {
            return false;
        }
        if(angleOffset != otherObj.angleOffset) {
            return false;
        }
        if(!start.equals(otherObj.start)) {
            return false;
        }
        if(!end.equals(otherObj.end)) {
            return false;
        }
        if(velocity != otherObj.velocity) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += type.hashCode(); 
        __result += (int)distance;
        __result += (int)length;
        __result += (int)angleOffset;
        __result += start.hashCode(); 
        __result += end.hashCode(); 
        __result += (int)velocity;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>SingleSegmentTypeSupport</code>
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

        SingleSegment typedSrc = (SingleSegment) src;
        SingleSegment typedDst = this;

        typedDst.type = (rapid.ext.SegmentType) typedDst.type.copy_from(typedSrc.type);
        typedDst.distance = typedSrc.distance;
        typedDst.length = typedSrc.length;
        typedDst.angleOffset = typedSrc.angleOffset;
        typedDst.start = (rapid.Transform3D) typedDst.start.copy_from(typedSrc.start);
        typedDst.end = (rapid.Transform3D) typedDst.end.copy_from(typedSrc.end);
        typedDst.velocity = typedSrc.velocity;

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

        strBuffer.append(type.toString("type ", indent+1));
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("distance: ").append(distance).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("length: ").append(length).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("angleOffset: ").append(angleOffset).append("\n");  
        strBuffer.append(start.toString("start ", indent+1));
        strBuffer.append(end.toString("end ", indent+1));
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("velocity: ").append(velocity).append("\n");  

        return strBuffer.toString();
    }

}
