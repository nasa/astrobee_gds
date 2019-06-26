

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

public class MotorState   implements Copyable, Serializable{

    public rapid.ext.arc.MotorProfile cmd = (rapid.ext.arc.MotorProfile)rapid.ext.arc.MotorProfile.create();
    public double position= 0;
    public float speed= 0;

    public MotorState() {

    }
    public MotorState (MotorState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        MotorState self;
        self = new  MotorState();
        self.clear();
        return self;

    }

    public void clear() {

        if (cmd != null) {
            cmd.clear();
        }
        position= 0;
        speed= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        MotorState otherObj = (MotorState)o;

        if(!cmd.equals(otherObj.cmd)) {
            return false;
        }
        if(position != otherObj.position) {
            return false;
        }
        if(speed != otherObj.speed) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += cmd.hashCode(); 
        __result += (int)position;
        __result += (int)speed;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>MotorStateTypeSupport</code>
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

        MotorState typedSrc = (MotorState) src;
        MotorState typedDst = this;

        typedDst.cmd = (rapid.ext.arc.MotorProfile) typedDst.cmd.copy_from(typedSrc.cmd);
        typedDst.position = typedSrc.position;
        typedDst.speed = typedSrc.speed;

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

        strBuffer.append(cmd.toString("cmd ", indent+1));
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("position: ").append(position).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("speed: ").append(speed).append("\n");  

        return strBuffer.toString();
    }

}
