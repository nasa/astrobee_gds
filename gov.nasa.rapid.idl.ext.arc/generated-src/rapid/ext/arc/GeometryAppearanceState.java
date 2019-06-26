

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

/** TODO: GeometryAppearanceState  */

public class GeometryAppearanceState  extends rapid.Message implements Copyable, Serializable{

    public int geometryId= 0;
    public boolean hasMaterial= false;
    public rapid.Color4f diffuse = (rapid.Color4f)rapid.Color4f.create();
    public rapid.Color4f ambient = (rapid.Color4f)rapid.Color4f.create();
    public rapid.Color4f specular = (rapid.Color4f)rapid.Color4f.create();
    public rapid.Color4f emissive = (rapid.Color4f)rapid.Color4f.create();
    public float shininess= 0;
    public boolean hasTexture= false;
    public String texImage0Url=  "" ; /* maximum length = (128) */

    public GeometryAppearanceState() {

        super();

    }
    public GeometryAppearanceState (GeometryAppearanceState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        GeometryAppearanceState self;
        self = new  GeometryAppearanceState();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        geometryId= 0;
        hasMaterial= false;
        if (diffuse != null) {
            diffuse.clear();
        }
        if (ambient != null) {
            ambient.clear();
        }
        if (specular != null) {
            specular.clear();
        }
        if (emissive != null) {
            emissive.clear();
        }
        shininess= 0;
        hasTexture= false;
        texImage0Url=  ""; 
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

        GeometryAppearanceState otherObj = (GeometryAppearanceState)o;

        if(geometryId != otherObj.geometryId) {
            return false;
        }
        if(hasMaterial != otherObj.hasMaterial) {
            return false;
        }
        if(!diffuse.equals(otherObj.diffuse)) {
            return false;
        }
        if(!ambient.equals(otherObj.ambient)) {
            return false;
        }
        if(!specular.equals(otherObj.specular)) {
            return false;
        }
        if(!emissive.equals(otherObj.emissive)) {
            return false;
        }
        if(shininess != otherObj.shininess) {
            return false;
        }
        if(hasTexture != otherObj.hasTexture) {
            return false;
        }
        if(!texImage0Url.equals(otherObj.texImage0Url)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += (int)geometryId;
        __result += (hasMaterial == true)?1:0;
        __result += diffuse.hashCode(); 
        __result += ambient.hashCode(); 
        __result += specular.hashCode(); 
        __result += emissive.hashCode(); 
        __result += (int)shininess;
        __result += (hasTexture == true)?1:0;
        __result += texImage0Url.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>GeometryAppearanceStateTypeSupport</code>
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

        GeometryAppearanceState typedSrc = (GeometryAppearanceState) src;
        GeometryAppearanceState typedDst = this;
        super.copy_from(typedSrc);
        typedDst.geometryId = typedSrc.geometryId;
        typedDst.hasMaterial = typedSrc.hasMaterial;
        typedDst.diffuse = (rapid.Color4f) typedDst.diffuse.copy_from(typedSrc.diffuse);
        typedDst.ambient = (rapid.Color4f) typedDst.ambient.copy_from(typedSrc.ambient);
        typedDst.specular = (rapid.Color4f) typedDst.specular.copy_from(typedSrc.specular);
        typedDst.emissive = (rapid.Color4f) typedDst.emissive.copy_from(typedSrc.emissive);
        typedDst.shininess = typedSrc.shininess;
        typedDst.hasTexture = typedSrc.hasTexture;
        typedDst.texImage0Url = typedSrc.texImage0Url;

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

        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("geometryId: ").append(geometryId).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("hasMaterial: ").append(hasMaterial).append("\n");  
        strBuffer.append(diffuse.toString("diffuse ", indent+1));
        strBuffer.append(ambient.toString("ambient ", indent+1));
        strBuffer.append(specular.toString("specular ", indent+1));
        strBuffer.append(emissive.toString("emissive ", indent+1));
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("shininess: ").append(shininess).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("hasTexture: ").append(hasTexture).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("texImage0Url: ").append(texImage0Url).append("\n");  

        return strBuffer.toString();
    }

}
