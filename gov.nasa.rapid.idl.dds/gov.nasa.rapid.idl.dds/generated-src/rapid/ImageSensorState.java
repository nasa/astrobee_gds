

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
* Holds status information for an image sensor - typically a camera, but could be a laser scanner, gpr, or any
* sensor that can pack a data sample into a 2D array
*/

public class ImageSensorState  extends rapid.Message implements Copyable, Serializable{

    public rapid.ImageMetadata meta = (rapid.ImageMetadata)rapid.ImageMetadata.create();

    public ImageSensorState() {

        super();

    }
    public ImageSensorState (ImageSensorState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        ImageSensorState self;
        self = new  ImageSensorState();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        if (meta != null) {
            meta.clear();
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

        ImageSensorState otherObj = (ImageSensorState)o;

        if(!meta.equals(otherObj.meta)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += meta.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>ImageSensorStateTypeSupport</code>
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

        ImageSensorState typedSrc = (ImageSensorState) src;
        ImageSensorState typedDst = this;
        super.copy_from(typedSrc);
        typedDst.meta = (rapid.ImageMetadata) typedDst.meta.copy_from(typedSrc.meta);

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

        strBuffer.append(meta.toString("meta ", indent+1));

        return strBuffer.toString();
    }

}
