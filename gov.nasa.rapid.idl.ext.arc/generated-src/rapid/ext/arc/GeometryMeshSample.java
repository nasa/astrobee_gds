

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

/**
* Telemetry data structure for transfering meshes.
*/

public class GeometryMeshSample  extends rapid.Message implements Copyable, Serializable{

    /** Mesh identifier that can be used to identify multiple mesh instances. */
    public int geometryId= 0;
    public rapid.ext.arc.GeometryIndexModeSequence32 indexModes = (rapid.ext.arc.GeometryIndexModeSequence32)rapid.ext.arc.GeometryIndexModeSequence32.create();
    public rapid.LongSequence32 indexLengths = (rapid.LongSequence32)rapid.LongSequence32.create();
    public rapid.ext.arc.IndexSequence256K indexData = (rapid.ext.arc.IndexSequence256K)rapid.ext.arc.IndexSequence256K.create();
    public float vertexScale= 0;
    public rapid.ShortSequence128K vertexData = (rapid.ShortSequence128K)rapid.ShortSequence128K.create();
    public float normalScale= 0;
    public rapid.OctetSequence128K normalData = (rapid.OctetSequence128K)rapid.OctetSequence128K.create();
    public float colorScale= 0;
    public rapid.ext.arc.OctetSequence170K colorData = (rapid.ext.arc.OctetSequence170K)rapid.ext.arc.OctetSequence170K.create();
    public float texCoord0Scale= 0;
    public rapid.ShortSequence96K texCoord0Data = (rapid.ShortSequence96K)rapid.ShortSequence96K.create();

    public GeometryMeshSample() {

        super();

        /** Mesh identifier that can be used to identify multiple mesh instances. */

    }
    public GeometryMeshSample (GeometryMeshSample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        GeometryMeshSample self;
        self = new  GeometryMeshSample();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** Mesh identifier that can be used to identify multiple mesh instances. */
        geometryId= 0;
        if (indexModes != null) {
            indexModes.clear();
        }
        if (indexLengths != null) {
            indexLengths.clear();
        }
        if (indexData != null) {
            indexData.clear();
        }
        vertexScale= 0;
        if (vertexData != null) {
            vertexData.clear();
        }
        normalScale= 0;
        if (normalData != null) {
            normalData.clear();
        }
        colorScale= 0;
        if (colorData != null) {
            colorData.clear();
        }
        texCoord0Scale= 0;
        if (texCoord0Data != null) {
            texCoord0Data.clear();
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

        GeometryMeshSample otherObj = (GeometryMeshSample)o;

        /** Mesh identifier that can be used to identify multiple mesh instances. */
        if(geometryId != otherObj.geometryId) {
            return false;
        }
        if(!indexModes.equals(otherObj.indexModes)) {
            return false;
        }
        if(!indexLengths.equals(otherObj.indexLengths)) {
            return false;
        }
        if(!indexData.equals(otherObj.indexData)) {
            return false;
        }
        if(vertexScale != otherObj.vertexScale) {
            return false;
        }
        if(!vertexData.equals(otherObj.vertexData)) {
            return false;
        }
        if(normalScale != otherObj.normalScale) {
            return false;
        }
        if(!normalData.equals(otherObj.normalData)) {
            return false;
        }
        if(colorScale != otherObj.colorScale) {
            return false;
        }
        if(!colorData.equals(otherObj.colorData)) {
            return false;
        }
        if(texCoord0Scale != otherObj.texCoord0Scale) {
            return false;
        }
        if(!texCoord0Data.equals(otherObj.texCoord0Data)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** Mesh identifier that can be used to identify multiple mesh instances. */
        __result += (int)geometryId;
        __result += indexModes.hashCode(); 
        __result += indexLengths.hashCode(); 
        __result += indexData.hashCode(); 
        __result += (int)vertexScale;
        __result += vertexData.hashCode(); 
        __result += (int)normalScale;
        __result += normalData.hashCode(); 
        __result += (int)colorScale;
        __result += colorData.hashCode(); 
        __result += (int)texCoord0Scale;
        __result += texCoord0Data.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>GeometryMeshSampleTypeSupport</code>
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

        GeometryMeshSample typedSrc = (GeometryMeshSample) src;
        GeometryMeshSample typedDst = this;
        super.copy_from(typedSrc);
        /** Mesh identifier that can be used to identify multiple mesh instances. */
        typedDst.geometryId = typedSrc.geometryId;
        typedDst.indexModes = (rapid.ext.arc.GeometryIndexModeSequence32) typedDst.indexModes.copy_from(typedSrc.indexModes);
        typedDst.indexLengths = (rapid.LongSequence32) typedDst.indexLengths.copy_from(typedSrc.indexLengths);
        typedDst.indexData = (rapid.ext.arc.IndexSequence256K) typedDst.indexData.copy_from(typedSrc.indexData);
        typedDst.vertexScale = typedSrc.vertexScale;
        typedDst.vertexData = (rapid.ShortSequence128K) typedDst.vertexData.copy_from(typedSrc.vertexData);
        typedDst.normalScale = typedSrc.normalScale;
        typedDst.normalData = (rapid.OctetSequence128K) typedDst.normalData.copy_from(typedSrc.normalData);
        typedDst.colorScale = typedSrc.colorScale;
        typedDst.colorData = (rapid.ext.arc.OctetSequence170K) typedDst.colorData.copy_from(typedSrc.colorData);
        typedDst.texCoord0Scale = typedSrc.texCoord0Scale;
        typedDst.texCoord0Data = (rapid.ShortSequence96K) typedDst.texCoord0Data.copy_from(typedSrc.texCoord0Data);

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

        /** Mesh identifier that can be used to identify multiple mesh instances. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("geometryId: ").append(geometryId).append("\n");  
        strBuffer.append(indexModes.toString("indexModes ", indent+1));
        strBuffer.append(indexLengths.toString("indexLengths ", indent+1));
        strBuffer.append(indexData.toString("indexData ", indent+1));
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("vertexScale: ").append(vertexScale).append("\n");  
        strBuffer.append(vertexData.toString("vertexData ", indent+1));
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("normalScale: ").append(normalScale).append("\n");  
        strBuffer.append(normalData.toString("normalData ", indent+1));
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("colorScale: ").append(colorScale).append("\n");  
        strBuffer.append(colorData.toString("colorData ", indent+1));
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("texCoord0Scale: ").append(texCoord0Scale).append("\n");  
        strBuffer.append(texCoord0Data.toString("texCoord0Data ", indent+1));

        return strBuffer.toString();
    }

}
