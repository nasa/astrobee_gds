

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

public class MotorProfile   implements Copyable, Serializable{

    public rapid.ext.arc.CtrlMode ctrlMode = (rapid.ext.arc.CtrlMode)rapid.ext.arc.CtrlMode.create();
    public rapid.ext.arc.PositionMode posMode = (rapid.ext.arc.PositionMode)rapid.ext.arc.PositionMode.create();
    public double position= 0;
    public float speed= 0;
    public float acc= 0;

    public MotorProfile() {

    }
    public MotorProfile (MotorProfile other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        MotorProfile self;
        self = new  MotorProfile();
        self.clear();
        return self;

    }

    public void clear() {

        ctrlMode = rapid.ext.arc.CtrlMode.create();
        posMode = rapid.ext.arc.PositionMode.create();
        position= 0;
        speed= 0;
        acc= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        MotorProfile otherObj = (MotorProfile)o;

        if(!ctrlMode.equals(otherObj.ctrlMode)) {
            return false;
        }
        if(!posMode.equals(otherObj.posMode)) {
            return false;
        }
        if(position != otherObj.position) {
            return false;
        }
        if(speed != otherObj.speed) {
            return false;
        }
        if(acc != otherObj.acc) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += ctrlMode.hashCode(); 
        __result += posMode.hashCode(); 
        __result += (int)position;
        __result += (int)speed;
        __result += (int)acc;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>MotorProfileTypeSupport</code>
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

        MotorProfile typedSrc = (MotorProfile) src;
        MotorProfile typedDst = this;

        typedDst.ctrlMode = (rapid.ext.arc.CtrlMode) typedDst.ctrlMode.copy_from(typedSrc.ctrlMode);
        typedDst.posMode = (rapid.ext.arc.PositionMode) typedDst.posMode.copy_from(typedSrc.posMode);
        typedDst.position = typedSrc.position;
        typedDst.speed = typedSrc.speed;
        typedDst.acc = typedSrc.acc;

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

        strBuffer.append(ctrlMode.toString("ctrlMode ", indent+1));
        strBuffer.append(posMode.toString("posMode ", indent+1));
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("position: ").append(position).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("speed: ").append(speed).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("acc: ").append(acc).append("\n");  

        return strBuffer.toString();
    }

}
