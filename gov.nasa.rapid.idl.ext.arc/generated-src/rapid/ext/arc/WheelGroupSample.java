

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

public class WheelGroupSample  extends rapid.Message implements Copyable, Serializable{

    public long targetTime= 0;
    public float curvature= 0;
    public float curvatureRate= 0;
    public float speed= 0;
    public float crabAngle= 0;
    public float crabRate= 0;
    public float targetCurvature= 0;
    public float targetCurvatureRate= 0;
    public float targetCrabRate= 0;
    public float targetSpeed= 0;
    public float targetCrabAngle= 0;
    public rapid.ext.arc.MotorStateSequence32 motors = (rapid.ext.arc.MotorStateSequence32)rapid.ext.arc.MotorStateSequence32.create();
    public rapid.LongSequence32 motorStatus = (rapid.LongSequence32)rapid.LongSequence32.create();
    public rapid.FloatSequence32 currents = (rapid.FloatSequence32)rapid.FloatSequence32.create();
    public rapid.FloatSequence32 temperatures = (rapid.FloatSequence32)rapid.FloatSequence32.create();

    public WheelGroupSample() {

        super();

    }
    public WheelGroupSample (WheelGroupSample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        WheelGroupSample self;
        self = new  WheelGroupSample();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        targetTime= 0;
        curvature= 0;
        curvatureRate= 0;
        speed= 0;
        crabAngle= 0;
        crabRate= 0;
        targetCurvature= 0;
        targetCurvatureRate= 0;
        targetCrabRate= 0;
        targetSpeed= 0;
        targetCrabAngle= 0;
        if (motors != null) {
            motors.clear();
        }
        if (motorStatus != null) {
            motorStatus.clear();
        }
        if (currents != null) {
            currents.clear();
        }
        if (temperatures != null) {
            temperatures.clear();
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

        WheelGroupSample otherObj = (WheelGroupSample)o;

        if(targetTime != otherObj.targetTime) {
            return false;
        }
        if(curvature != otherObj.curvature) {
            return false;
        }
        if(curvatureRate != otherObj.curvatureRate) {
            return false;
        }
        if(speed != otherObj.speed) {
            return false;
        }
        if(crabAngle != otherObj.crabAngle) {
            return false;
        }
        if(crabRate != otherObj.crabRate) {
            return false;
        }
        if(targetCurvature != otherObj.targetCurvature) {
            return false;
        }
        if(targetCurvatureRate != otherObj.targetCurvatureRate) {
            return false;
        }
        if(targetCrabRate != otherObj.targetCrabRate) {
            return false;
        }
        if(targetSpeed != otherObj.targetSpeed) {
            return false;
        }
        if(targetCrabAngle != otherObj.targetCrabAngle) {
            return false;
        }
        if(!motors.equals(otherObj.motors)) {
            return false;
        }
        if(!motorStatus.equals(otherObj.motorStatus)) {
            return false;
        }
        if(!currents.equals(otherObj.currents)) {
            return false;
        }
        if(!temperatures.equals(otherObj.temperatures)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += (int)targetTime;
        __result += (int)curvature;
        __result += (int)curvatureRate;
        __result += (int)speed;
        __result += (int)crabAngle;
        __result += (int)crabRate;
        __result += (int)targetCurvature;
        __result += (int)targetCurvatureRate;
        __result += (int)targetCrabRate;
        __result += (int)targetSpeed;
        __result += (int)targetCrabAngle;
        __result += motors.hashCode(); 
        __result += motorStatus.hashCode(); 
        __result += currents.hashCode(); 
        __result += temperatures.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>WheelGroupSampleTypeSupport</code>
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

        WheelGroupSample typedSrc = (WheelGroupSample) src;
        WheelGroupSample typedDst = this;
        super.copy_from(typedSrc);
        typedDst.targetTime = typedSrc.targetTime;
        typedDst.curvature = typedSrc.curvature;
        typedDst.curvatureRate = typedSrc.curvatureRate;
        typedDst.speed = typedSrc.speed;
        typedDst.crabAngle = typedSrc.crabAngle;
        typedDst.crabRate = typedSrc.crabRate;
        typedDst.targetCurvature = typedSrc.targetCurvature;
        typedDst.targetCurvatureRate = typedSrc.targetCurvatureRate;
        typedDst.targetCrabRate = typedSrc.targetCrabRate;
        typedDst.targetSpeed = typedSrc.targetSpeed;
        typedDst.targetCrabAngle = typedSrc.targetCrabAngle;
        typedDst.motors = (rapid.ext.arc.MotorStateSequence32) typedDst.motors.copy_from(typedSrc.motors);
        typedDst.motorStatus = (rapid.LongSequence32) typedDst.motorStatus.copy_from(typedSrc.motorStatus);
        typedDst.currents = (rapid.FloatSequence32) typedDst.currents.copy_from(typedSrc.currents);
        typedDst.temperatures = (rapid.FloatSequence32) typedDst.temperatures.copy_from(typedSrc.temperatures);

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
        strBuffer.append("targetTime: ").append(targetTime).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("curvature: ").append(curvature).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("curvatureRate: ").append(curvatureRate).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("speed: ").append(speed).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("crabAngle: ").append(crabAngle).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("crabRate: ").append(crabRate).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("targetCurvature: ").append(targetCurvature).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("targetCurvatureRate: ").append(targetCurvatureRate).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("targetCrabRate: ").append(targetCrabRate).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("targetSpeed: ").append(targetSpeed).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("targetCrabAngle: ").append(targetCrabAngle).append("\n");  
        strBuffer.append(motors.toString("motors ", indent+1));
        strBuffer.append(motorStatus.toString("motorStatus ", indent+1));
        strBuffer.append(currents.toString("currents ", indent+1));
        strBuffer.append(temperatures.toString("temperatures ", indent+1));

        return strBuffer.toString();
    }

}
