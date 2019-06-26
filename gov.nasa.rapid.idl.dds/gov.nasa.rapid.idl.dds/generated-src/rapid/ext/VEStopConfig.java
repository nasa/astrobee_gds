

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

public class VEStopConfig  extends rapid.Message implements Copyable, Serializable{

    /** The evaluation interval for the trajectory sequences in microsec. Typically 0.2s. */
    public long evaluationInterval= 0;

    public VEStopConfig() {

        super();

        /** The evaluation interval for the trajectory sequences in microsec. Typically 0.2s. */

    }
    public VEStopConfig (VEStopConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        VEStopConfig self;
        self = new  VEStopConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** The evaluation interval for the trajectory sequences in microsec. Typically 0.2s. */
        evaluationInterval= 0;
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

        VEStopConfig otherObj = (VEStopConfig)o;

        /** The evaluation interval for the trajectory sequences in microsec. Typically 0.2s. */
        if(evaluationInterval != otherObj.evaluationInterval) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** The evaluation interval for the trajectory sequences in microsec. Typically 0.2s. */
        __result += (int)evaluationInterval;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>VEStopConfigTypeSupport</code>
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

        VEStopConfig typedSrc = (VEStopConfig) src;
        VEStopConfig typedDst = this;
        super.copy_from(typedSrc);
        /** The evaluation interval for the trajectory sequences in microsec. Typically 0.2s. */
        typedDst.evaluationInterval = typedSrc.evaluationInterval;

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

        /** The evaluation interval for the trajectory sequences in microsec. Typically 0.2s. */
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("evaluationInterval: ").append(evaluationInterval).append("\n");  

        return strBuffer.toString();
    }

}
