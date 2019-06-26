

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
* pose rotation is assumed to be quaternion
*/

public class Trajectory3D   implements Copyable, Serializable{

    public float weight= 0;
    public String name=  "" ; /* maximum length = (32) */
    public long t0= 0;
    public long tsep= 0;
    public rapid.ext.arc.TrajPoint3DSequence points = (rapid.ext.arc.TrajPoint3DSequence)rapid.ext.arc.TrajPoint3DSequence.create();

    public Trajectory3D() {

    }
    public Trajectory3D (Trajectory3D other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        Trajectory3D self;
        self = new  Trajectory3D();
        self.clear();
        return self;

    }

    public void clear() {

        weight= 0;
        name=  ""; 
        t0= 0;
        tsep= 0;
        if (points != null) {
            points.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        Trajectory3D otherObj = (Trajectory3D)o;

        if(weight != otherObj.weight) {
            return false;
        }
        if(!name.equals(otherObj.name)) {
            return false;
        }
        if(t0 != otherObj.t0) {
            return false;
        }
        if(tsep != otherObj.tsep) {
            return false;
        }
        if(!points.equals(otherObj.points)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += (int)weight;
        __result += name.hashCode(); 
        __result += (int)t0;
        __result += (int)tsep;
        __result += points.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>Trajectory3DTypeSupport</code>
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

        Trajectory3D typedSrc = (Trajectory3D) src;
        Trajectory3D typedDst = this;

        typedDst.weight = typedSrc.weight;
        typedDst.name = typedSrc.name;
        typedDst.t0 = typedSrc.t0;
        typedDst.tsep = typedSrc.tsep;
        typedDst.points = (rapid.ext.arc.TrajPoint3DSequence) typedDst.points.copy_from(typedSrc.points);

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
        strBuffer.append("weight: ").append(weight).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("name: ").append(name).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("t0: ").append(t0).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("tsep: ").append(tsep).append("\n");  
        strBuffer.append(points.toString("points ", indent+1));

        return strBuffer.toString();
    }

}
