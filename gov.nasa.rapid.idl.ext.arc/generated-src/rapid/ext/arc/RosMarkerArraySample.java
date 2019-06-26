

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.arc;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

/** Direct copy of ROS type */
/**
* MarkerArraySample
*/

public class RosMarkerArraySample  extends rapid.Message implements Copyable, Serializable{

    public rapid.ext.arc.MarkerArraySequence8 marker_array = (rapid.ext.arc.MarkerArraySequence8)rapid.ext.arc.MarkerArraySequence8.create();

    public RosMarkerArraySample() {

        super();

    }
    public RosMarkerArraySample (RosMarkerArraySample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        RosMarkerArraySample self;
        self = new  RosMarkerArraySample();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        if (marker_array != null) {
            marker_array.clear();
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

        RosMarkerArraySample otherObj = (RosMarkerArraySample)o;

        if(!marker_array.equals(otherObj.marker_array)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += marker_array.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>RosMarkerArraySampleTypeSupport</code>
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

        RosMarkerArraySample typedSrc = (RosMarkerArraySample) src;
        RosMarkerArraySample typedDst = this;
        super.copy_from(typedSrc);
        typedDst.marker_array = (rapid.ext.arc.MarkerArraySequence8) typedDst.marker_array.copy_from(typedSrc.marker_array);

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

        strBuffer.append(marker_array.toString("marker_array ", indent+1));

        return strBuffer.toString();
    }

}
