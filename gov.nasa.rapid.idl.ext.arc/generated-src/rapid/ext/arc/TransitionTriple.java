

/*
WARNING: THIS FILE IS AUTO-GENERATED. DO NOT MODIFY.

This file was generated from .idl using "rtiddsgen".
The rtiddsgen tool is part of the RTI Connext distribution.
For more information, type 'rtiddsgen -help' at a command shell
or consult the RTI Connext manual.
*/

package rapid.ext.arc;

import com.rti.dds.infrastructure.*;
import com.rti.dds.infrastructure.Copyable;
import java.io.Serializable;
import com.rti.dds.cdr.CdrHelper;

public class TransitionTriple   implements Copyable, Serializable{

    public byte sourceState= 0;
    public byte event= 0;
    public byte targetState= 0;

    public TransitionTriple() {

    }
    public TransitionTriple (TransitionTriple other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        TransitionTriple self;
        self = new  TransitionTriple();
        self.clear();
        return self;

    }

    public void clear() {

        sourceState= 0;
        event= 0;
        targetState= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        TransitionTriple otherObj = (TransitionTriple)o;

        if(sourceState != otherObj.sourceState) {
            return false;
        }
        if(event != otherObj.event) {
            return false;
        }
        if(targetState != otherObj.targetState) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += (int)sourceState;
        __result += (int)event;
        __result += (int)targetState;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>TransitionTripleTypeSupport</code>
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

        TransitionTriple typedSrc = (TransitionTriple) src;
        TransitionTriple typedDst = this;

        typedDst.sourceState = typedSrc.sourceState;
        typedDst.event = typedSrc.event;
        typedDst.targetState = typedSrc.targetState;

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
        strBuffer.append("sourceState: ").append(sourceState).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("event: ").append(event).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("targetState: ").append(targetState).append("\n");  

        return strBuffer.toString();
    }

}
