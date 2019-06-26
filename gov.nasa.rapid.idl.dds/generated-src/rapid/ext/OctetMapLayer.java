

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
* Map layer data for data samples reduced to 8 bit values. Data structure holding a vector of data
* points. To reduce footprint, the data is sent as 8 bit values (octet). The data-structure holds an
* offset and a scaling factor, to restore the original data samples. e.g.
* <code>
* double value = data * scale + offset
* </code>
* The density parameter allows a layer to be of lower resolution than the map itself.
*/

public class OctetMapLayer   implements Copyable, Serializable{

    /** Data value offset. */
    public double offset= 0;
    /** Data value scale. */
    public float scale= 0;
    /**
    * Sampling density. A full resolution layer has a density of 1, a half resolution layer has a
    * density of 2, etc. <i>Sampling density must be a power of 2</i>
    */
    public short density= 0;
    /** Vector of 8 bit data samples. */
    public rapid.OctetSequence128K data = (rapid.OctetSequence128K)rapid.OctetSequence128K.create();

    public OctetMapLayer() {

        /** Data value offset. */
        /** Data value scale. */
        /**
        * Sampling density. A full resolution layer has a density of 1, a half resolution layer has a
        * density of 2, etc. <i>Sampling density must be a power of 2</i>
        */
        /** Vector of 8 bit data samples. */

    }
    public OctetMapLayer (OctetMapLayer other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        OctetMapLayer self;
        self = new  OctetMapLayer();
        self.clear();
        return self;

    }

    public void clear() {

        /** Data value offset. */
        offset= 0;
        /** Data value scale. */
        scale= 0;
        /**
        * Sampling density. A full resolution layer has a density of 1, a half resolution layer has a
        * density of 2, etc. <i>Sampling density must be a power of 2</i>
        */
        density= 0;
        /** Vector of 8 bit data samples. */
        if (data != null) {
            data.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        OctetMapLayer otherObj = (OctetMapLayer)o;

        /** Data value offset. */
        if(offset != otherObj.offset) {
            return false;
        }
        /** Data value scale. */
        if(scale != otherObj.scale) {
            return false;
        }
        /**
        * Sampling density. A full resolution layer has a density of 1, a half resolution layer has a
        * density of 2, etc. <i>Sampling density must be a power of 2</i>
        */
        if(density != otherObj.density) {
            return false;
        }
        /** Vector of 8 bit data samples. */
        if(!data.equals(otherObj.data)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        /** Data value offset. */
        __result += (int)offset;
        /** Data value scale. */
        __result += (int)scale;
        /**
        * Sampling density. A full resolution layer has a density of 1, a half resolution layer has a
        * density of 2, etc. <i>Sampling density must be a power of 2</i>
        */
        __result += (int)density;
        /** Vector of 8 bit data samples. */
        __result += data.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>OctetMapLayerTypeSupport</code>
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

        OctetMapLayer typedSrc = (OctetMapLayer) src;
        OctetMapLayer typedDst = this;

        /** Data value offset. */
        typedDst.offset = typedSrc.offset;
        /** Data value scale. */
        typedDst.scale = typedSrc.scale;
        /**
        * Sampling density. A full resolution layer has a density of 1, a half resolution layer has a
        * density of 2, etc. <i>Sampling density must be a power of 2</i>
        */
        typedDst.density = typedSrc.density;
        /** Vector of 8 bit data samples. */
        typedDst.data = (rapid.OctetSequence128K) typedDst.data.copy_from(typedSrc.data);

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

        /** Data value offset. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("offset: ").append(offset).append("\n");  
        /** Data value scale. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("scale: ").append(scale).append("\n");  
        /**
        * Sampling density. A full resolution layer has a density of 1, a half resolution layer has a
        * density of 2, etc. <i>Sampling density must be a power of 2</i>
        */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("density: ").append(density).append("\n");  
        /** Vector of 8 bit data samples. */
        strBuffer.append(data.toString("data ", indent+1));

        return strBuffer.toString();
    }

}
