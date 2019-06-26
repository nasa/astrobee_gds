

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

/**
* ProcessConfig
*/
/**
* ProcessManagerConfig
*/

public class ProcessManagerConfig  extends rapid.Message implements Copyable, Serializable{

    public String configName=  "" ; /* maximum length = (64) */
    public rapid.ext.ProcessConfigSequence processes = (rapid.ext.ProcessConfigSequence)rapid.ext.ProcessConfigSequence.create();

    public ProcessManagerConfig() {

        super();

    }
    public ProcessManagerConfig (ProcessManagerConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        ProcessManagerConfig self;
        self = new  ProcessManagerConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        configName=  ""; 
        if (processes != null) {
            processes.clear();
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

        ProcessManagerConfig otherObj = (ProcessManagerConfig)o;

        if(!configName.equals(otherObj.configName)) {
            return false;
        }
        if(!processes.equals(otherObj.processes)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += configName.hashCode(); 
        __result += processes.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>ProcessManagerConfigTypeSupport</code>
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

        ProcessManagerConfig typedSrc = (ProcessManagerConfig) src;
        ProcessManagerConfig typedDst = this;
        super.copy_from(typedSrc);
        typedDst.configName = typedSrc.configName;
        typedDst.processes = (rapid.ext.ProcessConfigSequence) typedDst.processes.copy_from(typedSrc.processes);

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
        strBuffer.append("configName: ").append(configName).append("\n");  
        strBuffer.append(processes.toString("processes ", indent+1));

        return strBuffer.toString();
    }

}
