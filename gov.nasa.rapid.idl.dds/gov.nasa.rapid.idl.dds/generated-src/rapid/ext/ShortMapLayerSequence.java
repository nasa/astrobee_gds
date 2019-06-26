

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
/**
* Map layer data for data samples reduced to 16 bit values. Data structure holding a vector of data
* points. To reduce footprint, the data is sent as 16 bit values (short). The data-structure holds an
* offset and a scaling factor, to restore the original data samples. e.g.
* <code>
* double value = data * scale + offset
* </code>
* The density parameter allows a layer to be of lower resolution than the map itself.
*/

public class ShortMapLayerSequence   implements Copyable, Serializable{

    public rapid.ext.ShortMapLayerSeq userData =  new rapid.ext.ShortMapLayerSeq((rapid.ext.NAVMAP_NUM_SHORT_LAYERS.VALUE));

    public ShortMapLayerSequence() {

    }
    public ShortMapLayerSequence (ShortMapLayerSequence other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        ShortMapLayerSequence self;
        self = new  ShortMapLayerSequence();
        self.clear();
        return self;

    }

    public void clear() {

        if (userData != null) {
            userData.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        ShortMapLayerSequence otherObj = (ShortMapLayerSequence)o;

        if(!userData.equals(otherObj.userData)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += userData.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>ShortMapLayerSequenceTypeSupport</code>
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

        ShortMapLayerSequence typedSrc = (ShortMapLayerSequence) src;
        ShortMapLayerSequence typedDst = this;

        typedDst.userData.copy_from(typedSrc.userData);

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

        CdrHelper.printIndent(strBuffer, indent+1);
        strBuffer.append("userData:\n");
        for(int i__ = 0; i__ < userData.size(); ++i__) {
            strBuffer.append(((rapid.ext.ShortMapLayer)userData.get(i__)).toString(Integer.toString(i__),indent+2));
        }

        return strBuffer.toString();
    }

}
