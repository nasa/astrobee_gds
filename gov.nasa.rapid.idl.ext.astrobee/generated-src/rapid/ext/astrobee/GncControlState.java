

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
* The control state of the GNC.
*/

public class GncControlState  extends rapid.Message implements Copyable, Serializable{

    public rapid.Transform3D pose = (rapid.Transform3D)rapid.Transform3D.create();
    public rapid.ext.astrobee.Twist twist = (rapid.ext.astrobee.Twist)rapid.ext.astrobee.Twist.create();
    public rapid.ext.astrobee.Twist accel = (rapid.ext.astrobee.Twist)rapid.ext.astrobee.Twist.create();

    public GncControlState() {

        super();

    }
    public GncControlState (GncControlState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        GncControlState self;
        self = new  GncControlState();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        if (pose != null) {
            pose.clear();
        }
        if (twist != null) {
            twist.clear();
        }
        if (accel != null) {
            accel.clear();
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

        GncControlState otherObj = (GncControlState)o;

        if(!pose.equals(otherObj.pose)) {
            return false;
        }
        if(!twist.equals(otherObj.twist)) {
            return false;
        }
        if(!accel.equals(otherObj.accel)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += pose.hashCode(); 
        __result += twist.hashCode(); 
        __result += accel.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>GncControlStateTypeSupport</code>
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

        GncControlState typedSrc = (GncControlState) src;
        GncControlState typedDst = this;
        super.copy_from(typedSrc);
        typedDst.pose = (rapid.Transform3D) typedDst.pose.copy_from(typedSrc.pose);
        typedDst.twist = (rapid.ext.astrobee.Twist) typedDst.twist.copy_from(typedSrc.twist);
        typedDst.accel = (rapid.ext.astrobee.Twist) typedDst.accel.copy_from(typedSrc.accel);

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

        strBuffer.append(pose.toString("pose ", indent+1));
        strBuffer.append(twist.toString("twist ", indent+1));
        strBuffer.append(accel.toString("accel ", indent+1));

        return strBuffer.toString();
    }

}
