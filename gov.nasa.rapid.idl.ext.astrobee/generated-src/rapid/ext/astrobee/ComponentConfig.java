

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
* A list of valid components to listen for states
*/

public class ComponentConfig  extends rapid.Message implements Copyable, Serializable{

    public rapid.ext.astrobee.ComponentInfoConfigSequence16 components = (rapid.ext.astrobee.ComponentInfoConfigSequence16)rapid.ext.astrobee.ComponentInfoConfigSequence16.create();

    public ComponentConfig() {

        super();

    }
    public ComponentConfig (ComponentConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        ComponentConfig self;
        self = new  ComponentConfig();
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

        ComponentConfig otherObj = (ComponentConfig)o;

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
    * This method could be placed into <code>ComponentConfigTypeSupport</code>
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

        ComponentConfig typedSrc = (ComponentConfig) src;
        ComponentConfig typedDst = this;
        super.copy_from(typedSrc);
        typedDst.components = (rapid.ext.astrobee.ComponentInfoConfigSequence16) typedDst.components.copy_from(typedSrc.components);

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
