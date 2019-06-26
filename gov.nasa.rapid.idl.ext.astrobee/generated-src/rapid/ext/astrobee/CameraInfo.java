

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

public class CameraInfo   implements Copyable, Serializable{

    public boolean streaming= false;
    public boolean recording= false;
    public rapid.ext.astrobee.CameraResolution resolution = (rapid.ext.astrobee.CameraResolution)rapid.ext.astrobee.CameraResolution.create();
    public float frameRate= 0;
    public float bandwidth= 0;

    public CameraInfo() {

    }
    public CameraInfo (CameraInfo other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        CameraInfo self;
        self = new  CameraInfo();
        self.clear();
        return self;

    }

    public void clear() {

        streaming= false;
        recording= false;
        resolution = rapid.ext.astrobee.CameraResolution.create();
        frameRate= 0;
        bandwidth= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        CameraInfo otherObj = (CameraInfo)o;

        if(streaming != otherObj.streaming) {
            return false;
        }
        if(recording != otherObj.recording) {
            return false;
        }
        if(!resolution.equals(otherObj.resolution)) {
            return false;
        }
        if(frameRate != otherObj.frameRate) {
            return false;
        }
        if(bandwidth != otherObj.bandwidth) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += (streaming == true)?1:0;
        __result += (recording == true)?1:0;
        __result += resolution.hashCode(); 
        __result += (int)frameRate;
        __result += (int)bandwidth;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>CameraInfoTypeSupport</code>
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

        CameraInfo typedSrc = (CameraInfo) src;
        CameraInfo typedDst = this;

        typedDst.streaming = typedSrc.streaming;
        typedDst.recording = typedSrc.recording;
        typedDst.resolution = (rapid.ext.astrobee.CameraResolution) typedDst.resolution.copy_from(typedSrc.resolution);
        typedDst.frameRate = typedSrc.frameRate;
        typedDst.bandwidth = typedSrc.bandwidth;

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
        strBuffer.append("streaming: ").append(streaming).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("recording: ").append(recording).append("\n");  
        strBuffer.append(resolution.toString("resolution ", indent+1));
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("frameRate: ").append(frameRate).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("bandwidth: ").append(bandwidth).append("\n");  

        return strBuffer.toString();
    }

}
