

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
/**
* Telemetry data structure for transfering maps.
*/

public class NavMapSample  extends rapid.Message implements Copyable, Serializable{

    /** The x,y integer tile idenitifier. */
    public int [] tileId=  new int [2];
    /**
    * Location of the map in the reference frame. This is a 6 dof pose, so far all our mapping
    * algorithms only deal with rotations around the z-axis.
    */
    public rapid.Transform3D location = (rapid.Transform3D)rapid.Transform3D.create();
    public rapid.ext.ShortMapLayerSequence shortLayers = (rapid.ext.ShortMapLayerSequence)rapid.ext.ShortMapLayerSequence.create();
    public rapid.ext.OctetMapLayerSequence octetLayers = (rapid.ext.OctetMapLayerSequence)rapid.ext.OctetMapLayerSequence.create();

    public NavMapSample() {

        super();

        /** The x,y integer tile idenitifier. */
        /**
        * Location of the map in the reference frame. This is a 6 dof pose, so far all our mapping
        * algorithms only deal with rotations around the z-axis.
        */

    }
    public NavMapSample (NavMapSample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        NavMapSample self;
        self = new  NavMapSample();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** The x,y integer tile idenitifier. */
        for(int i1__ = 0; i1__< 2; ++i1__){

            tileId[i1__] =  0;
        }

        /**
        * Location of the map in the reference frame. This is a 6 dof pose, so far all our mapping
        * algorithms only deal with rotations around the z-axis.
        */
        if (location != null) {
            location.clear();
        }
        if (shortLayers != null) {
            shortLayers.clear();
        }
        if (octetLayers != null) {
            octetLayers.clear();
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

        NavMapSample otherObj = (NavMapSample)o;

        /** The x,y integer tile idenitifier. */
        for(int i1__ = 0; i1__< 2; ++i1__){

            if(tileId[i1__] != otherObj.tileId[i1__]) {
                return false;
            }
        }

        /**
        * Location of the map in the reference frame. This is a 6 dof pose, so far all our mapping
        * algorithms only deal with rotations around the z-axis.
        */
        if(!location.equals(otherObj.location)) {
            return false;
        }
        if(!shortLayers.equals(otherObj.shortLayers)) {
            return false;
        }
        if(!octetLayers.equals(otherObj.octetLayers)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** The x,y integer tile idenitifier. */
        for(int i1__ = 0; i1__< 2; ++i1__){

            __result += (int)tileId[i1__];
        }

        /**
        * Location of the map in the reference frame. This is a 6 dof pose, so far all our mapping
        * algorithms only deal with rotations around the z-axis.
        */
        __result += location.hashCode(); 
        __result += shortLayers.hashCode(); 
        __result += octetLayers.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>NavMapSampleTypeSupport</code>
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

        NavMapSample typedSrc = (NavMapSample) src;
        NavMapSample typedDst = this;
        super.copy_from(typedSrc);
        /** The x,y integer tile idenitifier. */
        System.arraycopy(typedSrc.tileId,0,
        typedDst.tileId,0,
        typedSrc.tileId.length); 

        /**
        * Location of the map in the reference frame. This is a 6 dof pose, so far all our mapping
        * algorithms only deal with rotations around the z-axis.
        */
        typedDst.location = (rapid.Transform3D) typedDst.location.copy_from(typedSrc.location);
        typedDst.shortLayers = (rapid.ext.ShortMapLayerSequence) typedDst.shortLayers.copy_from(typedSrc.shortLayers);
        typedDst.octetLayers = (rapid.ext.OctetMapLayerSequence) typedDst.octetLayers.copy_from(typedSrc.octetLayers);

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

        /** The x,y integer tile idenitifier. */
        CdrHelper.printIndent(strBuffer, indent+1);
        strBuffer.append("tileId: ");
        for(int i1__ = 0; i1__< 2; ++i1__){

            strBuffer.append(tileId[i1__]).append(", ");
        }

        strBuffer.append("\n");
        /**
        * Location of the map in the reference frame. This is a 6 dof pose, so far all our mapping
        * algorithms only deal with rotations around the z-axis.
        */
        strBuffer.append(location.toString("location ", indent+1));
        strBuffer.append(shortLayers.toString("shortLayers ", indent+1));
        strBuffer.append(octetLayers.toString("octetLayers ", indent+1));

        return strBuffer.toString();
    }

}
