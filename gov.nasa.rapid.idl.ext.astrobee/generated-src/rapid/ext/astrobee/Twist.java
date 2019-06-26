

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

public class Twist   implements Copyable, Serializable{

    public rapid.Vec3d linear = (rapid.Vec3d)rapid.Vec3d.create();
    public rapid.Vec3d angular = (rapid.Vec3d)rapid.Vec3d.create();

    public Twist() {

    }
    public Twist (Twist other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        Twist self;
        self = new  Twist();
        self.clear();
        return self;

    }

    public void clear() {

        if (linear != null) {
            linear.clear();
        }
        if (angular != null) {
            angular.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        Twist otherObj = (Twist)o;

        if(!linear.equals(otherObj.linear)) {
            return false;
        }
        if(!angular.equals(otherObj.angular)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += linear.hashCode(); 
        __result += angular.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>TwistTypeSupport</code>
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

        Twist typedSrc = (Twist) src;
        Twist typedDst = this;

        typedDst.linear = (rapid.Vec3d) typedDst.linear.copy_from(typedSrc.linear);
        typedDst.angular = (rapid.Vec3d) typedDst.angular.copy_from(typedSrc.angular);

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

        strBuffer.append(linear.toString("linear ", indent+1));
        strBuffer.append(angular.toString("angular ", indent+1));

        return strBuffer.toString();
    }

}
