

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

/** Map layer of height fields (DEM). */
/**
* Map layer with traversability assessments. Goodness is derived value from roughness, slope &
* vehicle parameters.
*/
/** Roughness of terrain. */
/** Map layer with confidence values for the associated traversability asessment. */
/** Map layer with cell normal vectors. Two or three entries per pixel, packed layout. */
/** Telemetry data structure for transfering maps */

public class NavMapConfig  extends rapid.Message implements Copyable, Serializable{

    /** Reference frame. */
    public String referenceFrame=  "" ; /* maximum length = (128) */
    /** Offset from the center of rotation to the lower left corner of the map. */
    public float [] offset=  new float [2];
    /** Size of a cell (in the reference frame coordinate system). */
    public float [] cellSize=  new float [2];
    /** Number of cells in x & y. */
    public short [] numCells=  new short [2];
    public rapid.ext.ShortMapLayerNameSequence shortLayerNames = (rapid.ext.ShortMapLayerNameSequence)rapid.ext.ShortMapLayerNameSequence.create();
    public rapid.ext.OctetMapLayerNameSequence octetLayerNames = (rapid.ext.OctetMapLayerNameSequence)rapid.ext.OctetMapLayerNameSequence.create();

    public NavMapConfig() {

        super();

        /** Reference frame. */
        /** Offset from the center of rotation to the lower left corner of the map. */
        /** Size of a cell (in the reference frame coordinate system). */
        /** Number of cells in x & y. */

    }
    public NavMapConfig (NavMapConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        NavMapConfig self;
        self = new  NavMapConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** Reference frame. */
        referenceFrame=  ""; 
        /** Offset from the center of rotation to the lower left corner of the map. */
        for(int i1__ = 0; i1__< 2; ++i1__){

            offset[i1__] =  0;
        }

        /** Size of a cell (in the reference frame coordinate system). */
        for(int i1__ = 0; i1__< 2; ++i1__){

            cellSize[i1__] =  0;
        }

        /** Number of cells in x & y. */
        for(int i1__ = 0; i1__< 2; ++i1__){

            numCells[i1__] =  0;
        }

        if (shortLayerNames != null) {
            shortLayerNames.clear();
        }
        if (octetLayerNames != null) {
            octetLayerNames.clear();
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

        NavMapConfig otherObj = (NavMapConfig)o;

        /** Reference frame. */
        if(!referenceFrame.equals(otherObj.referenceFrame)) {
            return false;
        }
        /** Offset from the center of rotation to the lower left corner of the map. */
        for(int i1__ = 0; i1__< 2; ++i1__){

            if(offset[i1__] != otherObj.offset[i1__]) {
                return false;
            }
        }

        /** Size of a cell (in the reference frame coordinate system). */
        for(int i1__ = 0; i1__< 2; ++i1__){

            if(cellSize[i1__] != otherObj.cellSize[i1__]) {
                return false;
            }
        }

        /** Number of cells in x & y. */
        for(int i1__ = 0; i1__< 2; ++i1__){

            if(numCells[i1__] != otherObj.numCells[i1__]) {
                return false;
            }
        }

        if(!shortLayerNames.equals(otherObj.shortLayerNames)) {
            return false;
        }
        if(!octetLayerNames.equals(otherObj.octetLayerNames)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** Reference frame. */
        __result += referenceFrame.hashCode(); 
        /** Offset from the center of rotation to the lower left corner of the map. */
        for(int i1__ = 0; i1__< 2; ++i1__){

            __result += (int)offset[i1__];
        }

        /** Size of a cell (in the reference frame coordinate system). */
        for(int i1__ = 0; i1__< 2; ++i1__){

            __result += (int)cellSize[i1__];
        }

        /** Number of cells in x & y. */
        for(int i1__ = 0; i1__< 2; ++i1__){

            __result += (int)numCells[i1__];
        }

        __result += shortLayerNames.hashCode(); 
        __result += octetLayerNames.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>NavMapConfigTypeSupport</code>
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

        NavMapConfig typedSrc = (NavMapConfig) src;
        NavMapConfig typedDst = this;
        super.copy_from(typedSrc);
        /** Reference frame. */
        typedDst.referenceFrame = typedSrc.referenceFrame;
        /** Offset from the center of rotation to the lower left corner of the map. */
        System.arraycopy(typedSrc.offset,0,
        typedDst.offset,0,
        typedSrc.offset.length); 

        /** Size of a cell (in the reference frame coordinate system). */
        System.arraycopy(typedSrc.cellSize,0,
        typedDst.cellSize,0,
        typedSrc.cellSize.length); 

        /** Number of cells in x & y. */
        System.arraycopy(typedSrc.numCells,0,
        typedDst.numCells,0,
        typedSrc.numCells.length); 

        typedDst.shortLayerNames = (rapid.ext.ShortMapLayerNameSequence) typedDst.shortLayerNames.copy_from(typedSrc.shortLayerNames);
        typedDst.octetLayerNames = (rapid.ext.OctetMapLayerNameSequence) typedDst.octetLayerNames.copy_from(typedSrc.octetLayerNames);

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

        /** Reference frame. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("referenceFrame: ").append(referenceFrame).append("\n");  
        /** Offset from the center of rotation to the lower left corner of the map. */
        CdrHelper.printIndent(strBuffer, indent+1);
        strBuffer.append("offset: ");
        for(int i1__ = 0; i1__< 2; ++i1__){

            strBuffer.append(offset[i1__]).append(", ");
        }

        strBuffer.append("\n");
        /** Size of a cell (in the reference frame coordinate system). */
        CdrHelper.printIndent(strBuffer, indent+1);
        strBuffer.append("cellSize: ");
        for(int i1__ = 0; i1__< 2; ++i1__){

            strBuffer.append(cellSize[i1__]).append(", ");
        }

        strBuffer.append("\n");
        /** Number of cells in x & y. */
        CdrHelper.printIndent(strBuffer, indent+1);
        strBuffer.append("numCells: ");
        for(int i1__ = 0; i1__< 2; ++i1__){

            strBuffer.append(numCells[i1__]).append(", ");
        }

        strBuffer.append("\n");
        strBuffer.append(shortLayerNames.toString("shortLayerNames ", indent+1));
        strBuffer.append(octetLayerNames.toString("octetLayerNames ", indent+1));

        return strBuffer.toString();
    }

}
