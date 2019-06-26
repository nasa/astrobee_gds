

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

public class CameraInfoConfig   implements Copyable, Serializable{

    public String name=  "" ; /* maximum length = (32) */
    public rapid.ext.astrobee.CameraResolutionSequence8 availResolutions = (rapid.ext.astrobee.CameraResolutionSequence8)rapid.ext.astrobee.CameraResolutionSequence8.create();
    public rapid.ext.astrobee.CameraMode mode = (rapid.ext.astrobee.CameraMode)rapid.ext.astrobee.CameraMode.create();
    public float maxFrameRate= 0;

    public CameraInfoConfig() {

    }
    public CameraInfoConfig (CameraInfoConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        CameraInfoConfig self;
        self = new  CameraInfoConfig();
        self.clear();
        return self;

    }

    public void clear() {

        name=  ""; 
        if (availResolutions != null) {
            availResolutions.clear();
        }
        mode = rapid.ext.astrobee.CameraMode.create();
        maxFrameRate= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        CameraInfoConfig otherObj = (CameraInfoConfig)o;

        if(!name.equals(otherObj.name)) {
            return false;
        }
        if(!availResolutions.equals(otherObj.availResolutions)) {
            return false;
        }
        if(!mode.equals(otherObj.mode)) {
            return false;
        }
        if(maxFrameRate != otherObj.maxFrameRate) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += name.hashCode(); 
        __result += availResolutions.hashCode(); 
        __result += mode.hashCode(); 
        __result += (int)maxFrameRate;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>CameraInfoConfigTypeSupport</code>
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

        CameraInfoConfig typedSrc = (CameraInfoConfig) src;
        CameraInfoConfig typedDst = this;

        typedDst.name = typedSrc.name;
        typedDst.availResolutions = (rapid.ext.astrobee.CameraResolutionSequence8) typedDst.availResolutions.copy_from(typedSrc.availResolutions);
        typedDst.mode = (rapid.ext.astrobee.CameraMode) typedDst.mode.copy_from(typedSrc.mode);
        typedDst.maxFrameRate = typedSrc.maxFrameRate;

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
        strBuffer.append("name: ").append(name).append("\n");  
        strBuffer.append(availResolutions.toString("availResolutions ", indent+1));
        strBuffer.append(mode.toString("mode ", indent+1));
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("maxFrameRate: ").append(maxFrameRate).append("\n");  

        return strBuffer.toString();
    }

}
