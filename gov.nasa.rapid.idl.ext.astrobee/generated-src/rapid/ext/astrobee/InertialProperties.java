

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.astrobee;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

/**
* Network state of Astrobee
*/

public class InertialProperties  extends rapid.Message implements Copyable, Serializable{

    /** Name of this set of mass properties */
    public String name=  "" ; /* maximum length = (32) */
    /** Mass of the Astrobee in kg, used for control calculations */
    public float mass= 0;
    /** Center of mass of thhe Astrobee, used for control calculations */
    public rapid.Vec3d centerOfMass = (rapid.Vec3d)rapid.Vec3d.create();
    /** Inertia matrix of Astrobee, used for control calculations */
    public rapid.Mat33f matrix = (rapid.Mat33f)rapid.Mat33f.create();

    public InertialProperties() {

        super();

        /** Name of this set of mass properties */
        /** Mass of the Astrobee in kg, used for control calculations */
        /** Center of mass of thhe Astrobee, used for control calculations */
        /** Inertia matrix of Astrobee, used for control calculations */

    }
    public InertialProperties (InertialProperties other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        InertialProperties self;
        self = new  InertialProperties();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** Name of this set of mass properties */
        name=  ""; 
        /** Mass of the Astrobee in kg, used for control calculations */
        mass= 0;
        /** Center of mass of thhe Astrobee, used for control calculations */
        if (centerOfMass != null) {
            centerOfMass.clear();
        }
        /** Inertia matrix of Astrobee, used for control calculations */
        if (matrix != null) {
            matrix.clear();
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

        InertialProperties otherObj = (InertialProperties)o;

        /** Name of this set of mass properties */
        if(!name.equals(otherObj.name)) {
            return false;
        }
        /** Mass of the Astrobee in kg, used for control calculations */
        if(mass != otherObj.mass) {
            return false;
        }
        /** Center of mass of thhe Astrobee, used for control calculations */
        if(!centerOfMass.equals(otherObj.centerOfMass)) {
            return false;
        }
        /** Inertia matrix of Astrobee, used for control calculations */
        if(!matrix.equals(otherObj.matrix)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** Name of this set of mass properties */
        __result += name.hashCode(); 
        /** Mass of the Astrobee in kg, used for control calculations */
        __result += (int)mass;
        /** Center of mass of thhe Astrobee, used for control calculations */
        __result += centerOfMass.hashCode(); 
        /** Inertia matrix of Astrobee, used for control calculations */
        __result += matrix.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>InertialPropertiesTypeSupport</code>
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

        InertialProperties typedSrc = (InertialProperties) src;
        InertialProperties typedDst = this;
        super.copy_from(typedSrc);
        /** Name of this set of mass properties */
        typedDst.name = typedSrc.name;
        /** Mass of the Astrobee in kg, used for control calculations */
        typedDst.mass = typedSrc.mass;
        /** Center of mass of thhe Astrobee, used for control calculations */
        typedDst.centerOfMass = (rapid.Vec3d) typedDst.centerOfMass.copy_from(typedSrc.centerOfMass);
        /** Inertia matrix of Astrobee, used for control calculations */
        typedDst.matrix = (rapid.Mat33f) typedDst.matrix.copy_from(typedSrc.matrix);

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

        /** Name of this set of mass properties */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("name: ").append(name).append("\n");  
        /** Mass of the Astrobee in kg, used for control calculations */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("mass: ").append(mass).append("\n");  
        /** Center of mass of thhe Astrobee, used for control calculations */
        strBuffer.append(centerOfMass.toString("centerOfMass ", indent+1));
        /** Inertia matrix of Astrobee, used for control calculations */
        strBuffer.append(matrix.toString("matrix ", indent+1));

        return strBuffer.toString();
    }

}
