

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

public class MemoryInfoConfig   implements Copyable, Serializable{

    public int total= 0;
    public int memoryLow= 0;
    public int memoryCritical= 0;

    public MemoryInfoConfig() {

    }
    public MemoryInfoConfig (MemoryInfoConfig other) {

        this();
        copy_from(other);
    }

    public static Object create() {

        MemoryInfoConfig self;
        self = new  MemoryInfoConfig();
        self.clear();
        return self;

    }

    public void clear() {

        total= 0;
        memoryLow= 0;
        memoryCritical= 0;
    }

    public boolean equals(Object o) {

        if (o == null) {
            return false;
        }        

        if(getClass() != o.getClass()) {
            return false;
        }

        MemoryInfoConfig otherObj = (MemoryInfoConfig)o;

        if(total != otherObj.total) {
            return false;
        }
        if(memoryLow != otherObj.memoryLow) {
            return false;
        }
        if(memoryCritical != otherObj.memoryCritical) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int __result = 0;
        __result += (int)total;
        __result += (int)memoryLow;
        __result += (int)memoryCritical;
        return __result;
    }

    /**
    * This is the implementation of the <code>Copyable</code> interface.
    * This method will perform a deep copy of <code>src</code>
    * This method could be placed into <code>MemoryInfoConfigTypeSupport</code>
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

        MemoryInfoConfig typedSrc = (MemoryInfoConfig) src;
        MemoryInfoConfig typedDst = this;

        typedDst.total = typedSrc.total;
        typedDst.memoryLow = typedSrc.memoryLow;
        typedDst.memoryCritical = typedSrc.memoryCritical;

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
        strBuffer.append("total: ").append(total).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("memoryLow: ").append(memoryLow).append("\n");  
        CdrHelper.printIndent(strBuffer, indent+1);        
        strBuffer.append("memoryCritical: ").append(memoryCritical).append("\n");  

        return strBuffer.toString();
    }

}
