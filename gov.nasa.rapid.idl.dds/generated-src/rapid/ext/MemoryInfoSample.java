

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

public class MemoryInfoSample   implements Copyable, Serializable{

    public int buffers= 0;
    public int cached= 0;
    public int free= 0;
    public int swap= 0;

    public MemoryInfoSample() {

    }
    public MemoryInfoSample (MemoryInfoSample other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        MemoryInfoSample self;
        self = new  MemoryInfoSample();
        self.clear();
        return self;

    }

    public void clear() {

        buffers= 0;
        cached= 0;
        free= 0;
        swap= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        MemoryInfoSample otherObj = (MemoryInfoSample)o;

        if(buffers != otherObj.buffers) {
            return false;
        }
        if(cached != otherObj.cached) {
            return false;
        }
        if(free != otherObj.free) {
            return false;
        }
        if(swap != otherObj.swap) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += (int)buffers;
        __result += (int)cached;
        __result += (int)free;
        __result += (int)swap;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>MemoryInfoSampleTypeSupport</code>
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

        MemoryInfoSample typedSrc = (MemoryInfoSample) src;
        MemoryInfoSample typedDst = this;

        typedDst.buffers = typedSrc.buffers;
        typedDst.cached = typedSrc.cached;
        typedDst.free = typedSrc.free;
        typedDst.swap = typedSrc.swap;

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
        strBuffer.append("buffers: ").append(buffers).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("cached: ").append(cached).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("free: ").append(free).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("swap: ").append(swap).append("\n");  

        return strBuffer.toString();
    }

}
