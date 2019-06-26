

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

public class EphemerisSample  extends rapid.Message implements Copyable, Serializable{

    public int configIdx= 0;
    public long solutionTime= 0;
    public double lat= 0;
    public double lon= 0;
    public rapid.Vec3f vec = (rapid.Vec3f)rapid.Vec3f.create();

    public EphemerisSample() {

        super();

    }
    public EphemerisSample (EphemerisSample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        EphemerisSample self;
        self = new  EphemerisSample();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        configIdx= 0;
        solutionTime= 0;
        lat= 0;
        lon= 0;
        if (vec != null) {
            vec.clear();
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

        EphemerisSample otherObj = (EphemerisSample)o;

        if(configIdx != otherObj.configIdx) {
            return false;
        }
        if(solutionTime != otherObj.solutionTime) {
            return false;
        }
        if(lat != otherObj.lat) {
            return false;
        }
        if(lon != otherObj.lon) {
            return false;
        }
        if(!vec.equals(otherObj.vec)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += (int)configIdx;
        __result += (int)solutionTime;
        __result += (int)lat;
        __result += (int)lon;
        __result += vec.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>EphemerisSampleTypeSupport</code>
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

        EphemerisSample typedSrc = (EphemerisSample) src;
        EphemerisSample typedDst = this;
        super.copy_from(typedSrc);
        typedDst.configIdx = typedSrc.configIdx;
        typedDst.solutionTime = typedSrc.solutionTime;
        typedDst.lat = typedSrc.lat;
        typedDst.lon = typedSrc.lon;
        typedDst.vec = (rapid.Vec3f) typedDst.vec.copy_from(typedSrc.vec);

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
        strBuffer.append("configIdx: ").append(configIdx).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("solutionTime: ").append(solutionTime).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("lat: ").append(lat).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("lon: ").append(lon).append("\n");  
        strBuffer.append(vec.toString("vec ", indent+1));

        return strBuffer.toString();
    }

}
