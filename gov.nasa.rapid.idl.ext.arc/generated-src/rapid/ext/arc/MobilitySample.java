

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
* MobilitySample defines a snapshot of the Mobility subsystem's state.
*/

public class MobilitySample  extends rapid.Message implements Copyable, Serializable{

    public String goalName=  "" ; /* maximum length = (32) */
    public String commandFrame=  "" ; /* maximum length = (128) */
    public rapid.Vec3d xyt = (rapid.Vec3d)rapid.Vec3d.create();
    public rapid.Vec3d xytTolerance = (rapid.Vec3d)rapid.Vec3d.create();
    public float hintedSpeed= 0;
    public String navAlgo=  "" ; /* maximum length = (32) */
    /** extension values */
    public rapid.KeyTypeValueSequence8 keyedValues = (rapid.KeyTypeValueSequence8)rapid.KeyTypeValueSequence8.create();

    public MobilitySample() {

        super();

        /** extension values */

    }
    public MobilitySample (MobilitySample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        MobilitySample self;
        self = new  MobilitySample();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        goalName=  ""; 
        commandFrame=  ""; 
        if (xyt != null) {
            xyt.clear();
        }
        if (xytTolerance != null) {
            xytTolerance.clear();
        }
        hintedSpeed= 0;
        navAlgo=  ""; 
        /** extension values */
        if (keyedValues != null) {
            keyedValues.clear();
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

        MobilitySample otherObj = (MobilitySample)o;

        if(!goalName.equals(otherObj.goalName)) {
            return false;
        }
        if(!commandFrame.equals(otherObj.commandFrame)) {
            return false;
        }
        if(!xyt.equals(otherObj.xyt)) {
            return false;
        }
        if(!xytTolerance.equals(otherObj.xytTolerance)) {
            return false;
        }
        if(hintedSpeed != otherObj.hintedSpeed) {
            return false;
        }
        if(!navAlgo.equals(otherObj.navAlgo)) {
            return false;
        }
        /** extension values */
        if(!keyedValues.equals(otherObj.keyedValues)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += goalName.hashCode(); 
        __result += commandFrame.hashCode(); 
        __result += xyt.hashCode(); 
        __result += xytTolerance.hashCode(); 
        __result += (int)hintedSpeed;
        __result += navAlgo.hashCode(); 
        /** extension values */
        __result += keyedValues.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>MobilitySampleTypeSupport</code>
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

        MobilitySample typedSrc = (MobilitySample) src;
        MobilitySample typedDst = this;
        super.copy_from(typedSrc);
        typedDst.goalName = typedSrc.goalName;
        typedDst.commandFrame = typedSrc.commandFrame;
        typedDst.xyt = (rapid.Vec3d) typedDst.xyt.copy_from(typedSrc.xyt);
        typedDst.xytTolerance = (rapid.Vec3d) typedDst.xytTolerance.copy_from(typedSrc.xytTolerance);
        typedDst.hintedSpeed = typedSrc.hintedSpeed;
        typedDst.navAlgo = typedSrc.navAlgo;
        /** extension values */
        typedDst.keyedValues = (rapid.KeyTypeValueSequence8) typedDst.keyedValues.copy_from(typedSrc.keyedValues);

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
        strBuffer.append("goalName: ").append(goalName).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("commandFrame: ").append(commandFrame).append("\n");  
        strBuffer.append(xyt.toString("xyt ", indent+1));
        strBuffer.append(xytTolerance.toString("xytTolerance ", indent+1));
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("hintedSpeed: ").append(hintedSpeed).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("navAlgo: ").append(navAlgo).append("\n");  
        /** extension values */
        strBuffer.append(keyedValues.toString("keyedValues ", indent+1));

        return strBuffer.toString();
    }

}
