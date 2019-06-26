

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
* Represents force in free space.
*/

public class Wrench   implements Copyable, Serializable{

    public rapid.Vec3d force = (rapid.Vec3d)rapid.Vec3d.create();
    public rapid.Vec3d torque = (rapid.Vec3d)rapid.Vec3d.create();

    public Wrench() {

    }
    public Wrench (Wrench other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        Wrench self;
        self = new  Wrench();
        self.clear();
        return self;

    }

    public void clear() {

        if (force != null) {
            force.clear();
        }
        if (torque != null) {
            torque.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        Wrench otherObj = (Wrench)o;

        if(!force.equals(otherObj.force)) {
            return false;
        }
        if(!torque.equals(otherObj.torque)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += force.hashCode(); 
        __result += torque.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>WrenchTypeSupport</code>
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

        Wrench typedSrc = (Wrench) src;
        Wrench typedDst = this;

        typedDst.force = (rapid.Vec3d) typedDst.force.copy_from(typedSrc.force);
        typedDst.torque = (rapid.Vec3d) typedDst.torque.copy_from(typedSrc.torque);

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

        strBuffer.append(force.toString("force ", indent+1));
        strBuffer.append(torque.toString("torque ", indent+1));

        return strBuffer.toString();
    }

}
