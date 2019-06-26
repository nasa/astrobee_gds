

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

public class ProcessManagerState  extends rapid.Message implements Copyable, Serializable{

    /** status sequence corresponds with ProcessManagerConfig.processes sequence */
    public rapid.ext.ProcessStatusSequence processStatus = (rapid.ext.ProcessStatusSequence)rapid.ext.ProcessStatusSequence.create();

    public ProcessManagerState() {

        super();

        /** status sequence corresponds with ProcessManagerConfig.processes sequence */

    }
    public ProcessManagerState (ProcessManagerState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        ProcessManagerState self;
        self = new  ProcessManagerState();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** status sequence corresponds with ProcessManagerConfig.processes sequence */
        if (processStatus != null) {
            processStatus.clear();
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

        ProcessManagerState otherObj = (ProcessManagerState)o;

        /** status sequence corresponds with ProcessManagerConfig.processes sequence */
        if(!processStatus.equals(otherObj.processStatus)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** status sequence corresponds with ProcessManagerConfig.processes sequence */
        __result += processStatus.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>ProcessManagerStateTypeSupport</code>
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

        ProcessManagerState typedSrc = (ProcessManagerState) src;
        ProcessManagerState typedDst = this;
        super.copy_from(typedSrc);
        /** status sequence corresponds with ProcessManagerConfig.processes sequence */
        typedDst.processStatus = (rapid.ext.ProcessStatusSequence) typedDst.processStatus.copy_from(typedSrc.processStatus);

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

        /** status sequence corresponds with ProcessManagerConfig.processes sequence */
        strBuffer.append(processStatus.toString("processStatus ", indent+1));

        return strBuffer.toString();
    }

}
