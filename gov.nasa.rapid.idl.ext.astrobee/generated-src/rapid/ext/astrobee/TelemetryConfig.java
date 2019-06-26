

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
* Configuration message for a TelemetryState message
*/

public class TelemetryConfig  extends rapid.Message implements Copyable, Serializable{

    public rapid.ext.astrobee.CameraInfoConfigSequence8 cameras = (rapid.ext.astrobee.CameraInfoConfigSequence8)rapid.ext.astrobee.CameraInfoConfigSequence8.create();

    public TelemetryConfig() {

        super();

    }
    public TelemetryConfig (TelemetryConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        TelemetryConfig self;
        self = new  TelemetryConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        if (cameras != null) {
            cameras.clear();
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

        TelemetryConfig otherObj = (TelemetryConfig)o;

        if(!cameras.equals(otherObj.cameras)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += cameras.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>TelemetryConfigTypeSupport</code>
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

        TelemetryConfig typedSrc = (TelemetryConfig) src;
        TelemetryConfig typedDst = this;
        super.copy_from(typedSrc);
        typedDst.cameras = (rapid.ext.astrobee.CameraInfoConfigSequence8) typedDst.cameras.copy_from(typedSrc.cameras);

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

        strBuffer.append(cameras.toString("cameras ", indent+1));

        return strBuffer.toString();
    }

}
