

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
* PointSample describes the position and attributes of a point in the cloud. Note: Attributes are included
* in this struct because we get them for free on the wire due to word alignment.
*/

public class PointSample   implements Copyable, Serializable{

    /**
    * Position relative to the cloud origin, in arbitrary point units. Reference PointSampleConfig
    * to determine the meaning of the elements in this vector. These are signed short values so
    * in order to exploit the full 16 bits of resolution, data should be scaled to
    * a range of -32768 to 32767. If 15 bits of resolution is adequate, data can be scaled to a
    * range of 0 to 32767 for computational efficiency on the sending side.
    * @see PointSampleXyzMode
    */
    public short [] xyz=  new short [3];
    /**
    * Particle attributes (e.g., intensity).
    * @see PointSampleAttributeMode
    */
    public byte [] attributes=  new byte [2];

    public PointSample() {

        /**
        * Position relative to the cloud origin, in arbitrary point units. Reference PointSampleConfig
        * to determine the meaning of the elements in this vector. These are signed short values so
        * in order to exploit the full 16 bits of resolution, data should be scaled to
        * a range of -32768 to 32767. If 15 bits of resolution is adequate, data can be scaled to a
        * range of 0 to 32767 for computational efficiency on the sending side.
        * @see PointSampleXyzMode
        */
        /**
        * Particle attributes (e.g., intensity).
        * @see PointSampleAttributeMode
        */

    }
    public PointSample (PointSample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        PointSample self;
        self = new  PointSample();
        self.clear();
        return self;

    }

    public void clear() {

        /**
        * Position relative to the cloud origin, in arbitrary point units. Reference PointSampleConfig
        * to determine the meaning of the elements in this vector. These are signed short values so
        * in order to exploit the full 16 bits of resolution, data should be scaled to
        * a range of -32768 to 32767. If 15 bits of resolution is adequate, data can be scaled to a
        * range of 0 to 32767 for computational efficiency on the sending side.
        * @see PointSampleXyzMode
        */
        for(int i1__ = 0; i1__< 3; ++i1__){

            xyz[i1__] =  0;
        }

        /**
        * Particle attributes (e.g., intensity).
        * @see PointSampleAttributeMode
        */
        for(int i1__ = 0; i1__< 2; ++i1__){

            attributes[i1__] =  0;
        }

    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        PointSample otherObj = (PointSample)o;

        /**
        * Position relative to the cloud origin, in arbitrary point units. Reference PointSampleConfig
        * to determine the meaning of the elements in this vector. These are signed short values so
        * in order to exploit the full 16 bits of resolution, data should be scaled to
        * a range of -32768 to 32767. If 15 bits of resolution is adequate, data can be scaled to a
        * range of 0 to 32767 for computational efficiency on the sending side.
        * @see PointSampleXyzMode
        */
        for(int i1__ = 0; i1__< 3; ++i1__){

            if(xyz[i1__] != otherObj.xyz[i1__]) {
                return false;
            }
        }

        /**
        * Particle attributes (e.g., intensity).
        * @see PointSampleAttributeMode
        */
        for(int i1__ = 0; i1__< 2; ++i1__){

            if(attributes[i1__] != otherObj.attributes[i1__]) {
                return false;
            }
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        /**
        * Position relative to the cloud origin, in arbitrary point units. Reference PointSampleConfig
        * to determine the meaning of the elements in this vector. These are signed short values so
        * in order to exploit the full 16 bits of resolution, data should be scaled to
        * a range of -32768 to 32767. If 15 bits of resolution is adequate, data can be scaled to a
        * range of 0 to 32767 for computational efficiency on the sending side.
        * @see PointSampleXyzMode
        */
        for(int i1__ = 0; i1__< 3; ++i1__){

            __result += (int)xyz[i1__];
        }

        /**
        * Particle attributes (e.g., intensity).
        * @see PointSampleAttributeMode
        */
        for(int i1__ = 0; i1__< 2; ++i1__){

            __result += (int)attributes[i1__];
        }

        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>PointSampleTypeSupport</code>
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

        PointSample typedSrc = (PointSample) src;
        PointSample typedDst = this;

        /**
        * Position relative to the cloud origin, in arbitrary point units. Reference PointSampleConfig
        * to determine the meaning of the elements in this vector. These are signed short values so
        * in order to exploit the full 16 bits of resolution, data should be scaled to
        * a range of -32768 to 32767. If 15 bits of resolution is adequate, data can be scaled to a
        * range of 0 to 32767 for computational efficiency on the sending side.
        * @see PointSampleXyzMode
        */
        System.arraycopy(typedSrc.xyz,0,
        typedDst.xyz,0,
        typedSrc.xyz.length); 

        /**
        * Particle attributes (e.g., intensity).
        * @see PointSampleAttributeMode
        */
        System.arraycopy(typedSrc.attributes,0,
        typedDst.attributes,0,
        typedSrc.attributes.length); 

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

        /**
        * Position relative to the cloud origin, in arbitrary point units. Reference PointSampleConfig
        * to determine the meaning of the elements in this vector. These are signed short values so
        * in order to exploit the full 16 bits of resolution, data should be scaled to
        * a range of -32768 to 32767. If 15 bits of resolution is adequate, data can be scaled to a
        * range of 0 to 32767 for computational efficiency on the sending side.
        * @see PointSampleXyzMode
        */
        CdrHelper.printIndent(strBuffer, indent+1);
        strBuffer.append("xyz: ");
        for(int i1__ = 0; i1__< 3; ++i1__){

            strBuffer.append(xyz[i1__]).append(", ");
        }

        strBuffer.append("\n");
        /**
        * Particle attributes (e.g., intensity).
        * @see PointSampleAttributeMode
        */
        CdrHelper.printIndent(strBuffer, indent+1);
        strBuffer.append("attributes: ");
        for(int i1__ = 0; i1__< 2; ++i1__){

            strBuffer.append(attributes[i1__]).append(", ");
        }

        strBuffer.append("\n");

        return strBuffer.toString();
    }

}
