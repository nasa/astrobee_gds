

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

/** Images of size up to 1MB are supported. */
/** Image sensor information, plus payload. */

public class ImageSensorSample  extends rapid.Message implements Copyable, Serializable{

    /** Metadata for this specific sensor. */
    public rapid.ImageMetadata meta = (rapid.ImageMetadata)rapid.ImageMetadata.create();
    /** MIME type, if available. */
    public String mimeType=  "" ; /* maximum length = (32) */
    /** Image data */
    public rapid.ImageData data = (rapid.ImageData)rapid.ImageData.create();

    public ImageSensorSample() {

        super();

        /** Metadata for this specific sensor. */
        /** MIME type, if available. */
        /** Image data */

    }
    public ImageSensorSample (ImageSensorSample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        ImageSensorSample self;
        self = new  ImageSensorSample();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** Metadata for this specific sensor. */
        if (meta != null) {
            meta.clear();
        }
        /** MIME type, if available. */
        mimeType=  ""; 
        /** Image data */
        if (data != null) {
            data.clear();
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

        ImageSensorSample otherObj = (ImageSensorSample)o;

        /** Metadata for this specific sensor. */
        if(!meta.equals(otherObj.meta)) {
            return false;
        }
        /** MIME type, if available. */
        if(!mimeType.equals(otherObj.mimeType)) {
            return false;
        }
        /** Image data */
        if(!data.equals(otherObj.data)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** Metadata for this specific sensor. */
        __result += meta.hashCode(); 
        /** MIME type, if available. */
        __result += mimeType.hashCode(); 
        /** Image data */
        __result += data.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>ImageSensorSampleTypeSupport</code>
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

        ImageSensorSample typedSrc = (ImageSensorSample) src;
        ImageSensorSample typedDst = this;
        super.copy_from(typedSrc);
        /** Metadata for this specific sensor. */
        typedDst.meta = (rapid.ImageMetadata) typedDst.meta.copy_from(typedSrc.meta);
        /** MIME type, if available. */
        typedDst.mimeType = typedSrc.mimeType;
        /** Image data */
        typedDst.data = (rapid.ImageData) typedDst.data.copy_from(typedSrc.data);

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

        /** Metadata for this specific sensor. */
        strBuffer.append(meta.toString("meta ", indent+1));
        /** MIME type, if available. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("mimeType: ").append(mimeType).append("\n");  
        /** Image data */
        strBuffer.append(data.toString("data ", indent+1));

        return strBuffer.toString();
    }

}
