

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
* Holds status information for an image sensor - typically a camera, but could be a laser scanner, gpr, or
* any sensor that can pack a data sample into a 2D array.
*/

public class ImageMetadata   implements Copyable, Serializable{

    /** Unique identifier for sensor. */
    public String sensorName=  "" ; /* maximum length = (32) */
    /** Sensor type, e.g. Camera, LaserScanner. */
    public String sensorType=  "" ; /* maximum length = (32) */
    /**
    * 3D transform which defines the zero position of the sensor with respect to the Agent's origin.
    * May be deprecated in future because the framestore service should handle this.
    */
    public rapid.Transform3D offset = (rapid.Transform3D)rapid.Transform3D.create();
    /** Native width (columns) of data sample. */
    public int width= 0;
    /** Native height (rows) of data sample. */
    public int height= 0;
    public rapid.NamedFloatRangeValueSequence16 rangeSettings = (rapid.NamedFloatRangeValueSequence16)rapid.NamedFloatRangeValueSequence16.create();
    /** One of the values here is the MIME type if available. */
    public rapid.NamedOptionSetValueSequence16 optionSettings = (rapid.NamedOptionSetValueSequence16)rapid.NamedOptionSetValueSequence16.create();
    /** Extra sequence for native or Agent-specific information. */
    public rapid.KeyTypeValueSequence16 extras = (rapid.KeyTypeValueSequence16)rapid.KeyTypeValueSequence16.create();

    public ImageMetadata() {

        /** Unique identifier for sensor. */
        /** Sensor type, e.g. Camera, LaserScanner. */
        /**
        * 3D transform which defines the zero position of the sensor with respect to the Agent's origin.
        * May be deprecated in future because the framestore service should handle this.
        */
        /** Native width (columns) of data sample. */
        /** Native height (rows) of data sample. */
        /** One of the values here is the MIME type if available. */
        /** Extra sequence for native or Agent-specific information. */

    }
    public ImageMetadata (ImageMetadata other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        ImageMetadata self;
        self = new  ImageMetadata();
        self.clear();
        return self;

    }

    public void clear() {

        /** Unique identifier for sensor. */
        sensorName=  ""; 
        /** Sensor type, e.g. Camera, LaserScanner. */
        sensorType=  ""; 
        /**
        * 3D transform which defines the zero position of the sensor with respect to the Agent's origin.
        * May be deprecated in future because the framestore service should handle this.
        */
        if (offset != null) {
            offset.clear();
        }
        /** Native width (columns) of data sample. */
        width= 0;
        /** Native height (rows) of data sample. */
        height= 0;
        if (rangeSettings != null) {
            rangeSettings.clear();
        }
        /** One of the values here is the MIME type if available. */
        if (optionSettings != null) {
            optionSettings.clear();
        }
        /** Extra sequence for native or Agent-specific information. */
        if (extras != null) {
            extras.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        ImageMetadata otherObj = (ImageMetadata)o;

        /** Unique identifier for sensor. */
        if(!sensorName.equals(otherObj.sensorName)) {
            return false;
        }
        /** Sensor type, e.g. Camera, LaserScanner. */
        if(!sensorType.equals(otherObj.sensorType)) {
            return false;
        }
        /**
        * 3D transform which defines the zero position of the sensor with respect to the Agent's origin.
        * May be deprecated in future because the framestore service should handle this.
        */
        if(!offset.equals(otherObj.offset)) {
            return false;
        }
        /** Native width (columns) of data sample. */
        if(width != otherObj.width) {
            return false;
        }
        /** Native height (rows) of data sample. */
        if(height != otherObj.height) {
            return false;
        }
        if(!rangeSettings.equals(otherObj.rangeSettings)) {
            return false;
        }
        /** One of the values here is the MIME type if available. */
        if(!optionSettings.equals(otherObj.optionSettings)) {
            return false;
        }
        /** Extra sequence for native or Agent-specific information. */
        if(!extras.equals(otherObj.extras)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        /** Unique identifier for sensor. */
        __result += sensorName.hashCode(); 
        /** Sensor type, e.g. Camera, LaserScanner. */
        __result += sensorType.hashCode(); 
        /**
        * 3D transform which defines the zero position of the sensor with respect to the Agent's origin.
        * May be deprecated in future because the framestore service should handle this.
        */
        __result += offset.hashCode(); 
        /** Native width (columns) of data sample. */
        __result += (int)width;
        /** Native height (rows) of data sample. */
        __result += (int)height;
        __result += rangeSettings.hashCode(); 
        /** One of the values here is the MIME type if available. */
        __result += optionSettings.hashCode(); 
        /** Extra sequence for native or Agent-specific information. */
        __result += extras.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>ImageMetadataTypeSupport</code>
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

        ImageMetadata typedSrc = (ImageMetadata) src;
        ImageMetadata typedDst = this;

        /** Unique identifier for sensor. */
        typedDst.sensorName = typedSrc.sensorName;
        /** Sensor type, e.g. Camera, LaserScanner. */
        typedDst.sensorType = typedSrc.sensorType;
        /**
        * 3D transform which defines the zero position of the sensor with respect to the Agent's origin.
        * May be deprecated in future because the framestore service should handle this.
        */
        typedDst.offset = (rapid.Transform3D) typedDst.offset.copy_from(typedSrc.offset);
        /** Native width (columns) of data sample. */
        typedDst.width = typedSrc.width;
        /** Native height (rows) of data sample. */
        typedDst.height = typedSrc.height;
        typedDst.rangeSettings = (rapid.NamedFloatRangeValueSequence16) typedDst.rangeSettings.copy_from(typedSrc.rangeSettings);
        /** One of the values here is the MIME type if available. */
        typedDst.optionSettings = (rapid.NamedOptionSetValueSequence16) typedDst.optionSettings.copy_from(typedSrc.optionSettings);
        /** Extra sequence for native or Agent-specific information. */
        typedDst.extras = (rapid.KeyTypeValueSequence16) typedDst.extras.copy_from(typedSrc.extras);

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

        /** Unique identifier for sensor. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("sensorName: ").append(sensorName).append("\n");  
        /** Sensor type, e.g. Camera, LaserScanner. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("sensorType: ").append(sensorType).append("\n");  
        /**
        * 3D transform which defines the zero position of the sensor with respect to the Agent's origin.
        * May be deprecated in future because the framestore service should handle this.
        */
        strBuffer.append(offset.toString("offset ", indent+1));
        /** Native width (columns) of data sample. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("width: ").append(width).append("\n");  
        /** Native height (rows) of data sample. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("height: ").append(height).append("\n");  
        strBuffer.append(rangeSettings.toString("rangeSettings ", indent+1));
        /** One of the values here is the MIME type if available. */
        strBuffer.append(optionSettings.toString("optionSettings ", indent+1));
        /** Extra sequence for native or Agent-specific information. */
        strBuffer.append(extras.toString("extras ", indent+1));

        return strBuffer.toString();
    }

}
