

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

public class GpsSample  extends rapid.Message implements Copyable, Serializable{

    /** The coordinates of the receiver in meters*/
    /** xyz is in NEU coordinates, z is ellipsoidal height*/
    public rapid.Vec3d xyz = (rapid.Vec3d)rapid.Vec3d.create();
    public rapid.Vec3d sigmaXyz = (rapid.Vec3d)rapid.Vec3d.create();
    public byte utmZone= 0;
    public char utmDesig= 0;
    public byte mode= 0;
    public byte numSats= 0;
    public long diffAge= 0;
    public long solAge= 0;
    /** mean sea level(orthometric height) = z(ellipsoidal height) - undulation (geoid height)*/
    public float undulation= 0;

    public GpsSample() {

        super();

        /** The coordinates of the receiver in meters*/
        /** xyz is in NEU coordinates, z is ellipsoidal height*/
        /** mean sea level(orthometric height) = z(ellipsoidal height) - undulation (geoid height)*/

    }
    public GpsSample (GpsSample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        GpsSample self;
        self = new  GpsSample();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** The coordinates of the receiver in meters*/
        /** xyz is in NEU coordinates, z is ellipsoidal height*/
        if (xyz != null) {
            xyz.clear();
        }
        if (sigmaXyz != null) {
            sigmaXyz.clear();
        }
        utmZone= 0;
        utmDesig= 0;
        mode= 0;
        numSats= 0;
        diffAge= 0;
        solAge= 0;
        /** mean sea level(orthometric height) = z(ellipsoidal height) - undulation (geoid height)*/
        undulation= 0;
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

        GpsSample otherObj = (GpsSample)o;

        /** The coordinates of the receiver in meters*/
        /** xyz is in NEU coordinates, z is ellipsoidal height*/
        if(!xyz.equals(otherObj.xyz)) {
            return false;
        }
        if(!sigmaXyz.equals(otherObj.sigmaXyz)) {
            return false;
        }
        if(utmZone != otherObj.utmZone) {
            return false;
        }
        if(utmDesig != otherObj.utmDesig) {
            return false;
        }
        if(mode != otherObj.mode) {
            return false;
        }
        if(numSats != otherObj.numSats) {
            return false;
        }
        if(diffAge != otherObj.diffAge) {
            return false;
        }
        if(solAge != otherObj.solAge) {
            return false;
        }
        /** mean sea level(orthometric height) = z(ellipsoidal height) - undulation (geoid height)*/
        if(undulation != otherObj.undulation) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** The coordinates of the receiver in meters*/
        /** xyz is in NEU coordinates, z is ellipsoidal height*/
        __result += xyz.hashCode(); 
        __result += sigmaXyz.hashCode(); 
        __result += (int)utmZone;
        __result += (int)utmDesig;
        __result += (int)mode;
        __result += (int)numSats;
        __result += (int)diffAge;
        __result += (int)solAge;
        /** mean sea level(orthometric height) = z(ellipsoidal height) - undulation (geoid height)*/
        __result += (int)undulation;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>GpsSampleTypeSupport</code>
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

        GpsSample typedSrc = (GpsSample) src;
        GpsSample typedDst = this;
        super.copy_from(typedSrc);
        /** The coordinates of the receiver in meters*/
        /** xyz is in NEU coordinates, z is ellipsoidal height*/
        typedDst.xyz = (rapid.Vec3d) typedDst.xyz.copy_from(typedSrc.xyz);
        typedDst.sigmaXyz = (rapid.Vec3d) typedDst.sigmaXyz.copy_from(typedSrc.sigmaXyz);
        typedDst.utmZone = typedSrc.utmZone;
        typedDst.utmDesig = typedSrc.utmDesig;
        typedDst.mode = typedSrc.mode;
        typedDst.numSats = typedSrc.numSats;
        typedDst.diffAge = typedSrc.diffAge;
        typedDst.solAge = typedSrc.solAge;
        /** mean sea level(orthometric height) = z(ellipsoidal height) - undulation (geoid height)*/
        typedDst.undulation = typedSrc.undulation;

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

        /** The coordinates of the receiver in meters*/
        /** xyz is in NEU coordinates, z is ellipsoidal height*/
        strBuffer.append(xyz.toString("xyz ", indent+1));
        strBuffer.append(sigmaXyz.toString("sigmaXyz ", indent+1));
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("utmZone: ").append(utmZone).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("utmDesig: ").append(utmDesig).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("mode: ").append(mode).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("numSats: ").append(numSats).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("diffAge: ").append(diffAge).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("solAge: ").append(solAge).append("\n");  
        /** mean sea level(orthometric height) = z(ellipsoidal height) - undulation (geoid height)*/
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("undulation: ").append(undulation).append("\n");  

        return strBuffer.toString();
    }

}
