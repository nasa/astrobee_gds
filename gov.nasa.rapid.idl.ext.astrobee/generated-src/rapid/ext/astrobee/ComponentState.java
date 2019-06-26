

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
* The state of a hardware component within Astrobee
* A state message is sent for each different component
*/

public class ComponentState  extends rapid.Message implements Copyable, Serializable{

    public rapid.ext.astrobee.ComponentInfoSequence16 components = (rapid.ext.astrobee.ComponentInfoSequence16)rapid.ext.astrobee.ComponentInfoSequence16.create();

    public ComponentState() {

        super();

    }
    public ComponentState (ComponentState other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        ComponentState self;
        self = new  ComponentState();
        self.clear();
        return self;

    }

    public void clear() {

        super.clear();
        if (components != null) {
            components.clear();
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

        ComponentState otherObj = (ComponentState)o;

        if(!components.equals(otherObj.components)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;

        __result = super.hashCode();
        __result += components.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>ComponentStateTypeSupport</code>
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

        ComponentState typedSrc = (ComponentState) src;
        ComponentState typedDst = this;
        super.copy_from(typedSrc);
        typedDst.components = (rapid.ext.astrobee.ComponentInfoSequence16) typedDst.components.copy_from(typedSrc.components);

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

        strBuffer.append(components.toString("components ", indent+1));

        return strBuffer.toString();
    }

}
