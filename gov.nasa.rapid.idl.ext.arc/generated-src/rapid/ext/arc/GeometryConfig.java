

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

/** GeometryConfig  */

public class GeometryConfig  extends rapid.Message implements Copyable, Serializable{

    /** each distinct geometry must have a unique identifier */
    public int geometryId= 0;
    public String geometryName=  "" ; /* maximum length = (128) */
    /** reference frame for geometry - must be a valid FrameStore */
    public String refFrame=  "" ; /* maximum length = (32) */
    public rapid.KeyTypeValueSequence16 metaData = (rapid.KeyTypeValueSequence16)rapid.KeyTypeValueSequence16.create();

    public GeometryConfig() {

        super();

        /** each distinct geometry must have a unique identifier */
        /** reference frame for geometry - must be a valid FrameStore */

    }
    public GeometryConfig (GeometryConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        GeometryConfig self;
        self = new  GeometryConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** each distinct geometry must have a unique identifier */
        geometryId= 0;
        geometryName=  ""; 
        /** reference frame for geometry - must be a valid FrameStore */
        refFrame=  ""; 
        if (metaData != null) {
            metaData.clear();
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

        GeometryConfig otherObj = (GeometryConfig)o;

        /** each distinct geometry must have a unique identifier */
        if(geometryId != otherObj.geometryId) {
            return false;
        }
        if(!geometryName.equals(otherObj.geometryName)) {
            return false;
        }
        /** reference frame for geometry - must be a valid FrameStore */
        if(!refFrame.equals(otherObj.refFrame)) {
            return false;
        }
        if(!metaData.equals(otherObj.metaData)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** each distinct geometry must have a unique identifier */
        __result += (int)geometryId;
        __result += geometryName.hashCode(); 
        /** reference frame for geometry - must be a valid FrameStore */
        __result += refFrame.hashCode(); 
        __result += metaData.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>GeometryConfigTypeSupport</code>
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

        GeometryConfig typedSrc = (GeometryConfig) src;
        GeometryConfig typedDst = this;
        super.copy_from(typedSrc);
        /** each distinct geometry must have a unique identifier */
        typedDst.geometryId = typedSrc.geometryId;
        typedDst.geometryName = typedSrc.geometryName;
        /** reference frame for geometry - must be a valid FrameStore */
        typedDst.refFrame = typedSrc.refFrame;
        typedDst.metaData = (rapid.KeyTypeValueSequence16) typedDst.metaData.copy_from(typedSrc.metaData);

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

        /** each distinct geometry must have a unique identifier */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("geometryId: ").append(geometryId).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("geometryName: ").append(geometryName).append("\n");  
        /** reference frame for geometry - must be a valid FrameStore */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("refFrame: ").append(refFrame).append("\n");  
        strBuffer.append(metaData.toString("metaData ", indent+1));

        return strBuffer.toString();
    }

}
