

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

/** Information specific to the fault.
* - subsystem: index into the subsystems sequence
*              in the fault config message
* - node: index into the nodes sequence in the
*         fault config message
* - faultId: id of the fault
* - warning: Whether the fault is a warning or not
* - faultDescription: short description of the fault
*/
/**
* The Fault config for Astrobee
*/

public class FaultConfig  extends rapid.Message implements Copyable, Serializable{

    /** Names of the subsystems in Astrobee */
    public rapid.String32Sequence16 subsystems = (rapid.String32Sequence16)rapid.String32Sequence16.create();
    /** Names of the nodes in Astrobee */
    public rapid.String32Sequence128 nodes = (rapid.String32Sequence128)rapid.String32Sequence128.create();
    /** A list of faults */
    public rapid.ext.astrobee.FaultInfoSequence256 faults = (rapid.ext.astrobee.FaultInfoSequence256)rapid.ext.astrobee.FaultInfoSequence256.create();

    public FaultConfig() {

        super();

        /** Names of the subsystems in Astrobee */
        /** Names of the nodes in Astrobee */
        /** A list of faults */

    }
    public FaultConfig (FaultConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        FaultConfig self;
        self = new  FaultConfig();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        /** Names of the subsystems in Astrobee */
        if (subsystems != null) {
            subsystems.clear();
        }
        /** Names of the nodes in Astrobee */
        if (nodes != null) {
            nodes.clear();
        }
        /** A list of faults */
        if (faults != null) {
            faults.clear();
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

        FaultConfig otherObj = (FaultConfig)o;

        /** Names of the subsystems in Astrobee */
        if(!subsystems.equals(otherObj.subsystems)) {
            return false;
        }
        /** Names of the nodes in Astrobee */
        if(!nodes.equals(otherObj.nodes)) {
            return false;
        }
        /** A list of faults */
        if(!faults.equals(otherObj.faults)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        /** Names of the subsystems in Astrobee */
        __result += subsystems.hashCode(); 
        /** Names of the nodes in Astrobee */
        __result += nodes.hashCode(); 
        /** A list of faults */
        __result += faults.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>FaultConfigTypeSupport</code>
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

        FaultConfig typedSrc = (FaultConfig) src;
        FaultConfig typedDst = this;
        super.copy_from(typedSrc);
        /** Names of the subsystems in Astrobee */
        typedDst.subsystems = (rapid.String32Sequence16) typedDst.subsystems.copy_from(typedSrc.subsystems);
        /** Names of the nodes in Astrobee */
        typedDst.nodes = (rapid.String32Sequence128) typedDst.nodes.copy_from(typedSrc.nodes);
        /** A list of faults */
        typedDst.faults = (rapid.ext.astrobee.FaultInfoSequence256) typedDst.faults.copy_from(typedSrc.faults);

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

        /** Names of the subsystems in Astrobee */
        strBuffer.append(subsystems.toString("subsystems ", indent+1));
        /** Names of the nodes in Astrobee */
        strBuffer.append(nodes.toString("nodes ", indent+1));
        /** A list of faults */
        strBuffer.append(faults.toString("faults ", indent+1));

        return strBuffer.toString();
    }

}
