

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
* PointSampleXyzMode denotes whether the values represent:
* <ul>
*   <li>PS_XYZ: x, y, z
*   <li>PS_XYt: x, y, theta
*   <li>PD_Rae: range, azimuth, elevation
* </ul>
*/
/**
* PointSampleAttributeMode denotes the type of data contained in the attribute bytes.
* <ul>
*   <li>PS_UNUSED
*   <li>PS_INTENSITY: intensity of return. unsigned byte (0-255)
*   <li>PS_LAYER: for multi-layer scanners. unsigned byte (0-255)
*   <li>PS_ECHO: echo # for multiple returns. unsigned byte (0-255)
*   <li>PS_LAYER4_ECHO4: first 4 bits are layer (0-15), second 4 bits are echo (0-15)
*   <li>PS_PULSE: pulse index. unsigned byte (0-255)
*   <li>PS_RGB332: RGB packed into single byte
*   <li>PS_RGB565: Assumes both attribute bytes are used for 16 bit color.
*                  1st byte: upper 3 bits are first 3 bits of green, lower 5 bits are red
*                  2nd byte: upper 3 bits are second 3 bits of green, lower 5 bits are blue
* </ul>
*/
/** PointCloudConfig message sets up the configuration of a PointCloudSample message for a specific Agent. */

public class PointCloudConfig  extends rapid.Message implements Copyable, Serializable{

    /** Reference frame of the point cloud. Should exist in FrameStore. */
    public String referenceFrame=  "" ; /* maximum length = (128) */
    /** Interpretation of xyz array. */
    public rapid.PointSampleXyzMode xyzMode = (rapid.PointSampleXyzMode)rapid.PointSampleXyzMode.create();
    /** Interpretation of each attribute. */
    public rapid.PointSampleAttributeMode [] attributesMode=  new rapid.PointSampleAttributeMode [2];
    /** Additional attributes. */
    public rapid.KeyTypeValueSequence16 attributes = (rapid.KeyTypeValueSequence16)rapid.KeyTypeValueSequence16.create();

    public PointCloudConfig() {

        super();

        /** Reference frame of the point cloud. Should exist in FrameStore. */
        /** Interpretation of xyz array. */
        /** Interpretation of each attribute. */
        for(int i1__ = 0; i1__< 2; ++i1__){

            attributesMode[i1__]= (rapid.PointSampleAttributeMode) rapid.PointSampleAttributeMode.create();
        }

        /** Additional attributes. */

    }
    public PointCloudConfig (PointCloudConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        PointCloudConfig self;
        self = new  PointCloudConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** Reference frame of the point cloud. Should exist in FrameStore. */
        referenceFrame=  ""; 
        /** Interpretation of xyz array. */
        xyzMode = rapid.PointSampleXyzMode.create();
        /** Interpretation of each attribute. */
        for(int i1__ = 0; i1__< 2; ++i1__){

            attributesMode[i1__] = rapid.PointSampleAttributeMode.create();
        }

        /** Additional attributes. */
        if (attributes != null) {
            attributes.clear();
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

        PointCloudConfig otherObj = (PointCloudConfig)o;

        /** Reference frame of the point cloud. Should exist in FrameStore. */
        if(!referenceFrame.equals(otherObj.referenceFrame)) {
            return false;
        }
        /** Interpretation of xyz array. */
        if(!xyzMode.equals(otherObj.xyzMode)) {
            return false;
        }
        /** Interpretation of each attribute. */
        for(int i1__ = 0; i1__< 2; ++i1__){

            if(!attributesMode[i1__].equals(otherObj.attributesMode[i1__])) {
                return false;
            }
        }

        /** Additional attributes. */
        if(!attributes.equals(otherObj.attributes)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** Reference frame of the point cloud. Should exist in FrameStore. */
        __result += referenceFrame.hashCode(); 
        /** Interpretation of xyz array. */
        __result += xyzMode.hashCode(); 
        /** Interpretation of each attribute. */
        for(int i1__ = 0; i1__< 2; ++i1__){

            __result += attributesMode[i1__].hashCode(); 
        }

        /** Additional attributes. */
        __result += attributes.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>PointCloudConfigTypeSupport</code>
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

        PointCloudConfig typedSrc = (PointCloudConfig) src;
        PointCloudConfig typedDst = this;
        super.copy_from(typedSrc);
        /** Reference frame of the point cloud. Should exist in FrameStore. */
        typedDst.referenceFrame = typedSrc.referenceFrame;
        /** Interpretation of xyz array. */
        typedDst.xyzMode = (rapid.PointSampleXyzMode) typedDst.xyzMode.copy_from(typedSrc.xyzMode);
        /** Interpretation of each attribute. */
        for(int i1__ = 0; i1__< 2; ++i1__){

            typedDst.attributesMode[i1__] = (rapid.PointSampleAttributeMode) typedDst.attributesMode[i1__].copy_from(typedSrc.attributesMode[i1__]);
        }

        /** Additional attributes. */
        typedDst.attributes = (rapid.KeyTypeValueSequence16) typedDst.attributes.copy_from(typedSrc.attributes);

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

        /** Reference frame of the point cloud. Should exist in FrameStore. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("referenceFrame: ").append(referenceFrame).append("\n");  
        /** Interpretation of xyz array. */
        strBuffer.append(xyzMode.toString("xyzMode ", indent+1));
        /** Interpretation of each attribute. */
        CdrHelper.printIndent(strBuffer, indent+1);
        strBuffer.append("attributesMode:\n");
        for(int i1__ = 0; i1__< 2; ++i1__){

            strBuffer.append(attributesMode[i1__].toString(
                "["+Integer.toString(i1__)+"]",indent+2));
        }

        /** Additional attributes. */
        strBuffer.append(attributes.toString("attributes ", indent+1));

        return strBuffer.toString();
    }

}
