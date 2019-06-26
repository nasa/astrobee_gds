

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

public class ComponentInfo   implements Copyable, Serializable{

    public boolean present= false;
    public boolean powered= false;
    public float temperature= 0;
    public float current= 0;
    public rapid.ValueSequence16 metadata = (rapid.ValueSequence16)rapid.ValueSequence16.create();

    public ComponentInfo() {

    }
    public ComponentInfo (ComponentInfo other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        ComponentInfo self;
        self = new  ComponentInfo();
        self.clear();
        return self;

    }

    public void clear() {

        present= false;
        powered= false;
        temperature= 0;
        current= 0;
        if (metadata != null) {
            metadata.clear();
        }
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        ComponentInfo otherObj = (ComponentInfo)o;

        if(present != otherObj.present) {
            return false;
        }
        if(powered != otherObj.powered) {
            return false;
        }
        if(temperature != otherObj.temperature) {
            return false;
        }
        if(current != otherObj.current) {
            return false;
        }
        if(!metadata.equals(otherObj.metadata)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += (present == true)?1:0;
        __result += (powered == true)?1:0;
        __result += (int)temperature;
        __result += (int)current;
        __result += metadata.hashCode(); 
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>ComponentInfoTypeSupport</code>
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

        ComponentInfo typedSrc = (ComponentInfo) src;
        ComponentInfo typedDst = this;

        typedDst.present = typedSrc.present;
        typedDst.powered = typedSrc.powered;
        typedDst.temperature = typedSrc.temperature;
        typedDst.current = typedSrc.current;
        typedDst.metadata = (rapid.ValueSequence16) typedDst.metadata.copy_from(typedSrc.metadata);

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
        strBuffer.append("present: ").append(present).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("powered: ").append(powered).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("temperature: ").append(temperature).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("current: ").append(current).append("\n");  
        strBuffer.append(metadata.toString("metadata ", indent+1));

        return strBuffer.toString();
    }

}
